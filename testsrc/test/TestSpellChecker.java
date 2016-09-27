package test;

import java.io.File;
import java.util.Set;

import junit.framework.Assert;
import mt.edu.um.mlrs.classify.lang.CharLanguageModel;
import mt.edu.um.mlrs.spell.CharacterMapping;
import mt.edu.um.mlrs.spell.CharacterSubstitutionChecker;
import mt.edu.um.mlrs.spell.DictionaryBasedSpellChecker;
import mt.edu.um.mlrs.spell.NGramSpellChecker;
import mt.edu.um.mlrs.split.MTRegex;
import mt.edu.um.mlrs.split.Tokeniser;
import mt.edu.um.util.dict.ListDictionary;

import org.junit.Test;

import com.aliasi.lm.CompiledNGramProcessLM;
import com.aliasi.spell.FixedWeightEditDistance;
import com.aliasi.spell.WeightedEditDistance;
import com.aliasi.tokenizer.RegExTokenizerFactory;
import com.aliasi.tokenizer.TokenizerFactory;
import com.ibm.icu.text.Normalizer;

public class TestSpellChecker extends MLRSTests {

	private NGramSpellChecker ngramSC;
	private DictionaryBasedSpellChecker dictSC;
	private CharacterSubstitutionChecker charSC;
	private CharLanguageModel model = new CharLanguageModel(
			"res/lm/charlm.5gram.model");
	private Tokeniser tokeniser;
	private CharacterMapping mapping;
	private ListDictionary dict;

	public TestSpellChecker(String name) throws Exception {
		super(name);
		// this.tokeniser = new MTRegexTokeniser();
//		dict = Dictionary.fromFile(new File("res/spell/words.utf8.txt"),
//				"UTF-8", "\t");
		// mapping = CharacterMapping.fromFile("res/spell/charmap2.mt.txt",
		// "UTF-8", "\t");
		

		// this.ngramSC = new NGramSpellChecker(
		// "res/spell/speller.5gram.1.2m.notok.model");
		// FixedWeightEditDistance distance = new FixedWeightEditDistance(0.0,
		// -1.0, -1.0, -1.0, -1.0);
		// this.ngramSC.setEditDistance(distance);
		// this.ngramSC.setTokenSet(dict.tokens());
		// this.ngramSC.setTokenizerFactory(new RegExTokenizerFactory(
		// MTRegex.TOKEN));
		// this.ngramSC.setNBest(10000);
		
		//CompiledNGramProcessLM lm = this.ngramSC.languageModel();
		// this.ngramSC.doNotEdit(dict.tokens());
		// this.ngramSC.setKnownTokenEditCost(-3.0);
		// this.ngramSC.setMinTokenLengthToCorrect(3);
		// this.ngramSC.setAllowMatch(false);
		// this.ngramSC.setNBest(100);
		// this.charSC = new CharacterSubstitutionChecker(tokeniser, dict,
		// mapping);
		// this.dictSC = new DictionaryBasedSpellChecker(tokeniser, dict);
	}

	//
	// @Test
	// public void testCharReplace1() {
	// // Assert.assertEquals(Normalizer.normalize(this.spellSolution2,
	// // Normalizer.NFC), ngramSC.getBest(Normalizer.normalize(
	// // this.spellCase2, Normalizer.NFC)));
	// System.out.println(ngramSC.getNBest("hafna"));
	// Assert.assertEquals(Normalizer.normalize("ħafna", Normalizer.NFC),
	// ngramSC.getBest(Normalizer.normalize(
	// "shafna", Normalizer.NFC)));
	// }

	// @Test
	// public void testProbabilities() {
	// Double p1 = model.probability(Normalizer.normalize(
	// this.spellSolution2, Normalizer.NFC));
	// Double p2 = model.probability(Normalizer.normalize(
	// this.spellCase2, Normalizer.NFC));
	// Assert.assertTrue( p1 > p2);
	// }

	// @Test
	// public void testProbabilities2() {
	// Double p1 = model.probability(Normalizer.normalize(this.spellSolution3,
	// Normalizer.NFC));
	// Double p2 = model.probability(Normalizer.normalize(this.spellCase3,
	// Normalizer.NFC));
	// System.out.println(p1 + " " + p2);
	// Assert.assertTrue(p1 > p2);
	//	
	// }
	//	
	@Test
	public void testProbabilities3() {
		Double p1 = model.probability(Normalizer.normalize(this.spellSolution4,
				Normalizer.NFC));
		Double p2 = model.probability(Normalizer.normalize(this.spellCase4,
				Normalizer.NFC));
		System.out.println(p1 + " " + p2);
		Assert.assertTrue(p1 > p2);

	}

}
