package test;

import junit.framework.Assert;
import mt.edu.um.mlrs.classify.lang.CharLanguageModel;
import mt.edu.um.mlrs.classify.lang.LanguageClassifier;

import org.junit.Test;

public class TestLanguageModel extends MLRSTests {

	CharLanguageModel model;
	LanguageClassifier classifier;

	public TestLanguageModel(String name) {
		super(name);
		this.model = new CharLanguageModel("res/lm/charlm_8gram.model");
		this.classifier = new LanguageClassifier("res/lm/lang-5gram.classifier");
	}

//	@Test
//	public void testByMinLength() {
//		double mp1 = model.maxProbability(this.text1,8);
//		double mp2 = model.maxProbability(this.text5, 8);
//		double mp3 = model.maxProbability(this.text4,8);
//		double ep1 = model.maxProbability(this.chinese1,8);
//		double ep2 = model.maxProbability(this.chinese2, 8);
//		double ep3 = model.maxProbability(this.english1, 8);
//		double ep4 = model.maxProbability(this.english2, 8);
//		System.out.println("Actual 1: " + mp1);
//		System.out.println("Actual 2: " + mp2);
//		System.out.println("Actual 3: " + mp3);
//		System.out.println("Fake 1: " + ep1);
//		System.out.println("Fake 2: " + ep2);
//		System.out.println("English1: " + ep3);
//		System.out.println("English2: " + ep4);
//		System.out.println("************");
//		Assert.assertTrue(mp2 > ep1);
//		Assert.assertTrue(mp2 > ep2);
//		Assert.assertTrue(mp2 > ep3);
//		Assert.assertTrue(mp1 > ep1);
//		Assert.assertTrue(mp1 > ep2);
//		Assert.assertTrue(mp1 > ep3);
//		Assert.assertTrue(mp3 > ep1);
//		Assert.assertTrue(mp3 > ep2);
//		Assert.assertTrue(mp3 > ep3);
//		Assert.assertTrue(mp1 > ep4);
//		Assert.assertTrue(mp2 > ep4);
//		Assert.assertTrue(mp3 > ep4);
//	}
	
	@Test
	public void testLanguageIdentification() {
		Assert.assertEquals(this.classifier.classify(this.text1), "mt");
		Assert.assertEquals(this.classifier.classify(this.text5), "mt");
		Assert.assertEquals(this.classifier.classify(this.text4), "mt");
		Assert.assertEquals(this.classifier.classify(this.fakeMaltese1), "mt");
		Assert.assertEquals(this.classifier.classify(this.english1), "en");
		Assert.assertEquals(this.classifier.classify(this.english2), "en");
		Assert.assertEquals(this.classifier.classify(this.chinese1), "jp");
	}
	
}
