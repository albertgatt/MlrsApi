package test;

import java.util.List;

import junit.framework.Assert;
import mt.edu.um.mlrs.split.ParagraphSplitter;
import mt.edu.um.mlrs.text.TextNode;

import org.junit.Before;
import org.junit.Test;

public class ParagraphSplitterTests extends MLRSTests {

	ParagraphSplitter parag;

	public ParagraphSplitterTests(String name) {
		super(name);
	}

	@Override
	@Before
	public void setUp() {
		this.parag = new ParagraphSplitter();
	}

	@Test
	public void testSplit1() {
		List<TextNode> nodes = this.parag.process(this.text5);
		Assert.assertEquals(2, nodes.size());

		for (TextNode node : nodes) {
			System.out.println(node);
		}
	}

}
