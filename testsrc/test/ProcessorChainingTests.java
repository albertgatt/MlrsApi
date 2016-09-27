package test;

import mt.edu.um.mlrs.doc.PDFReader;
import mt.edu.um.mlrs.doc.XmlDocumentWriter;
import mt.edu.um.mlrs.text.TextNode;

import org.junit.Test;

public class ProcessorChainingTests extends MLRSTests {

	public ProcessorChainingTests(String name) {
		super(name);
	}

	/*
	 * @Test public void testWordConversion() { MSWordReader converter = new
	 * MSWordReader(); TextNode node =
	 * converter.readDocument("res/testDocs/ovella.doc"); TextProcessor
	 * tokeniser = new Tokeniser(); TextProcessor sentence = new
	 * SentenceSplitter(); TextProcessor parag= new ParagraphSplitter();
	 * 
	 * //node = parag.process(node); //node.printTree();
	 * //Assert.assertEquals(5, node.numChildren());
	 * 
	 * node = sentence.process(node); node.printTree();
	 * //Assert.assertEquals(11, node.numChildren());
	 * 
	 * node = tokeniser.process(node); node.printTree();
	 * //Assert.assertEquals(11, node.numChildren());
	 * 
	 * XmlDocumentWriter writer = new XmlDocumentWriter();
	 * writer.writeDocument(node, "res/testDocs/ovella.xml"); }
	 */

	@Test
	public void testPDFConversion() {

		PDFReader converter = new PDFReader();
		TextNode node = converter.readDocument("res/testDocs/ovella.pdf");
		// TextProcessor tokeniser = new Tokeniser();
		// TextProcessor sentence = new SentenceSplitter();
		// TextProcessor parag= new ParagraphSplitter();

		// node = parag.process(node);
		// Assert.assertEquals(27, node.numChildren());

		/*
		 * node = sentence.process(node); Assert.assertEquals(11,
		 * node.numChildren());
		 * 
		 * node = tokeniser.process(node); node.printTree();
		 * //Assert.assertEquals(11, node.numChildren());
		 */
		XmlDocumentWriter writer = new XmlDocumentWriter();
		writer.writeDocument(node, "res/testDocs/ovella.xml");
	}
}
