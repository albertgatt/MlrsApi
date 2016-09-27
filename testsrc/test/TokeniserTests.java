package test;

import java.util.List;

import junit.framework.Assert;
import mt.edu.um.mlrs.split.MTRegex;
import mt.edu.um.mlrs.split.RegexTokeniser;
import mt.edu.um.mlrs.split.Tokeniser;
import mt.edu.um.mlrs.text.TextNode;

import org.junit.Test;

public class TokeniserTests extends MLRSTests {
	Tokeniser tokeniser;

	public TokeniserTests(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		this.tokeniser = new RegexTokeniser(MTRegex.TOKEN);
	}

	/**
	 * Test various regexes used to split text
	 */
	@Test
	public void testRegex() {
		List<String> tokenised = this.tokeniser.split(this.textWithURL);	
		Assert.assertEquals(32, tokenised.size());
	}
	
	@Test
	public void testTokenize1() {
		List<TextNode> node = this.tokeniser.process(this.text1);
		Assert.assertEquals(35, node.size());
	}

	@Test
	public void testTokenize2() {
		List<TextNode> node = this.tokeniser.process(this.text2);
		Assert.assertEquals(15, node.size());
	}

	@Test
	public void testTokenize3() {
		List<TextNode> node = this.tokeniser.process(this.text3);
		Assert.assertEquals(6, node.size());
	}

	public void testTokenize4() {
		List<TextNode> node = this.tokeniser.process(this.text4);
		Assert.assertEquals(79, node.size());
	}

}
