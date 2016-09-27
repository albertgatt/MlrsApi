package test;

import java.util.Collection;

import junit.framework.Assert;
import mt.edu.um.mlrs.spell.EditDistance;
import mt.edu.um.util.dict.DictEntry;
import mt.edu.um.util.dict.TrieDictionary;
import mt.edu.um.util.io.SerialiseFiles;

import org.junit.Before;
import org.junit.Test;

public class DictionaryTests extends MLRSTests {

	TrieDictionary dict;
	EditDistance dist;

	public DictionaryTests(String name) {
		super(name);
	}

	@Before
	public void setUp() {
		dict = new TrieDictionary();
		dist = new EditDistance();
	}

	@Test
	public void testSerialiseTrie() throws Exception {
		this.dict.insert("ħafna");
		this.dict.insert("ħafen");
		this.dict.insert("ħafnet");
		SerialiseFiles.serialize(this.dict, "res/output/testdict.ser");
		TrieDictionary newDict = SerialiseFiles.deserialize(
				"res/output/testdict.ser", TrieDictionary.class);
		Assert.assertEquals(3, newDict.numEntries());
	}

	@Test
	public void testTrieDict1() {
		this.dict.insert("ħafna");
		this.dict.insert("ħafen");
		this.dict.insert("ħafnet");
		Assert.assertTrue(dict.contains("ħafen"));
		Assert.assertTrue(dict.contains("ħafna"));
		Assert.assertTrue(dict.contains("ħafnet"));
		Assert.assertTrue(dict.contains("ħaf"));
	}

	@Test
	public void testTrieDict2() {
		this.dict.insert("ħafna");
		this.dict.insert("ħafen");
		this.dict.insert("ħafnet");
		Assert.assertTrue(dict.isToken("ħafen"));
		Assert.assertTrue(dict.isToken("ħafna"));
		Assert.assertTrue(dict.isToken("ħafnet"));
		Assert.assertFalse(dict.isToken("ħaf"));
	}

	@Test
	public void testBestMatch() {
		this.dict.insert("ħafna");
		this.dict.insert("ħafen");
		this.dict.insert("ħafnet");
		DictEntry best = dict.bestMatch("ħafnaa", dist,
				Double.POSITIVE_INFINITY);
		Assert.assertEquals("ħafna", best.getLabel());

		// should fail -- no matches at zero dist
		DictEntry best2 = dict.bestMatch("ħafnaa", dist, 0);
		Assert.assertEquals("ħafnaa", best2.getLabel());
	}

	public void testNBestMatch() {
		this.dict.insert("ħafna");
		this.dict.insert("ħafen");
		this.dict.insert("ħafnet");
		Collection<DictEntry> best = dict.nBestMatches("ħafnaa", dist,
				Double.POSITIVE_INFINITY, 2);
		System.out.println(best);
		Assert.assertEquals(2, best.size());
	}

	public void testEntrySet() {
		this.dict.insert("ħafna");
		this.dict.insert("ħafen");
		this.dict.insert("ħafnet");
		Assert.assertEquals(3, dict.entries().size());
	}

	public void testNeighbourhoodDensity() {
		this.dist.setDeleteWeight(Double.POSITIVE_INFINITY);
		this.dist.setInsertWeight(Double.POSITIVE_INFINITY);
		this.dist.setSubstituteWeight(1.0D);
		this.dist.setTransposeWeight(Double.POSITIVE_INFINITY);
		this.dict.insert("jinstalla");
		this.dict.insert("ninstalla");
		this.dict.insert("kinstalla");
		this.dict.insert("ra");
		this.dict.insert("ma");
		this.dict.insert("ħa");
		Assert.assertEquals(3, this.dict.neighbourhodDensity("minstalla",
				this.dist, 1.0D, 50));
		Assert.assertEquals(0, this.dict.neighbourhodDensity("ħabb", this.dist,
				1.0D, 50));
		Assert.assertEquals(3, this.dict.neighbourhodDensity("sa", this.dist,
				1.0D, 50));
	}

	public void testBigDict() throws Exception {
		this.dict = SerialiseFiles.deserialize("res/spell/charmapping.mt.dict",
				TrieDictionary.class);
		this.dist.setDeleteWeight(Double.POSITIVE_INFINITY);
		this.dist.setInsertWeight(Double.POSITIVE_INFINITY);
		this.dist.setSubstituteWeight(1.0D);
		this.dist.setTransposeWeight(Double.POSITIVE_INFINITY);

		Assert.assertEquals(837721, this.dict.entries().size());

		Assert.assertEquals(22, this.dict.neighbourhodDensity("ħara",
				this.dist, 1.0D, 200));
		long t1 = System.currentTimeMillis();

		System.out
				.println(this.dict.nBestMatches("ħara", this.dist, 1.0D, 200));
		long t2 = System.currentTimeMillis();
		System.err.println(t2 - t1);
	}

	public void testFrequency() {
		this.dict.insert("ħafna");
		this.dict.insert("Christina");
		this.dict.insert("ħafen");
		this.dict.insert("ħafnet");
		this.dict.insert("ħafna");
		this.dict.insert("ħafna");
		Assert.assertEquals(1, this.dict.tokenFrequency("ħafen"));
		Assert.assertEquals(3, this.dict.tokenFrequency("ħafna"));
	}

}
