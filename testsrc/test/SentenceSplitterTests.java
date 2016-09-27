package test;

import java.util.List;

import junit.framework.Assert;
import mt.edu.um.mlrs.split.MTRegex;
import mt.edu.um.mlrs.split.RegexTokeniser;
import mt.edu.um.mlrs.split.SentenceSplitter;
import mt.edu.um.mlrs.text.TextNode;

import org.junit.Before;
import org.junit.Test;

public class SentenceSplitterTests extends MLRSTests {

	SentenceSplitter splitter;

	public SentenceSplitterTests(String name) {
		super(name);
	}

	@Override
	@Before
	public void setUp() {
		this.splitter = new SentenceSplitter(new RegexTokeniser(MTRegex.TOKEN));
	}

	@Test
	public void testSplit1() {
		List<TextNode> result = this.splitter.process(this.text4);
		Assert.assertEquals(2, result.size());

		for (TextNode n : result) {
			System.out.println(n);
		}
	}

}
