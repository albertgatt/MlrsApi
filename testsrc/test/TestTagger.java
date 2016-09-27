package test;

import mt.edu.um.mlrs.classify.tag.HmmTagger;
import mt.edu.um.mlrs.split.MTRegex;
import mt.edu.um.mlrs.split.RegexTokeniser;

import org.junit.Before;
import org.junit.Test;

public class TestTagger extends MLRSTests {
	HmmTagger tagger;

	public TestTagger(String name) {
		super(name);

	}

	@Override
	@Before
	public void setUp() throws Exception {
		this.tagger = new HmmTagger("res/tag/tagger.level1.12gram.hmm", new RegexTokeniser(MTRegex.TOKEN));
		// XMLMorphologicalRuleProvider provider = new
		// XMLMorphologicalRuleProvider(
		// IOConfig.LEVEL1_POSTPROC);
		// provider.initialise();
		// this.tagger.accept(provider);
	}

	@Test
	public void testTagLongText() {
		System.out.println(this.tagger.classify(this.phrase3));

	}

	/*
	 * @Test public void test1() { TextNode node1 = tagger.process(phrase1);
	 * node1.printTree(); assertEquals(13, node1.numChildren(false)); }
	 * 
	 * @Test public void test2() { TextNode p2 = tagger.process(phrase2);
	 * p2.printTree(); assertEquals(16, p2.numChildren(false)); }
	 * 
	 * @Test public void test3() { TextNode p3 = tagger.process(phrase3);
	 * p3.printTree(); assertEquals(12, p3.numChildren(false)); }
	 * 
	 * @Test public void test4() { TextNode p4 = tagger.process(phrase4);
	 * p4.printTree(); assertEquals(15, p4.numChildren(false)); }
	 * 
	 * @Test public void test5() { TextNode p5 = tagger.process(phrase5);
	 * p5.printTree(); assertEquals(16, p5.numChildren(false)); }
	 * 
	 * @Test public void test6() { TextNode p6 = tagger.process(phrase6);
	 * p6.printTree(); assertEquals(8, p6.numChildren(false)); }
	 * 
	 * @Test public void test7() { TextNode p7 = tagger.process(phrase7);
	 * p7.printTree(); assertEquals(9, p7.numChildren(false)); }
	 * 
	 * @Test public void test8() { TextNode p8 = tagger.process(phrase8);
	 * p8.printTree(); assertEquals(10, p8.numChildren(false)); }
	 */
}
