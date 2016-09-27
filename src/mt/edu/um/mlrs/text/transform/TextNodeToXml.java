package mt.edu.um.mlrs.text.transform;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import mt.edu.um.mlrs.exception.TransformerException;
import mt.edu.um.mlrs.text.TextNode;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


public class TextNodeToXml extends TextTransformer<TextNode,Document> {

	private DocumentBuilderFactory _docBuilderFactory;
	private DocumentBuilder _builder;

	public TextNodeToXml() {
		super();
		init();
	}
	

	// public TextNodeToXml(TransformationRules<TextNode,Document> rules) {
	// super(rules);
	// init();
	// }
	
	private void init() {
		this._docBuilderFactory = DocumentBuilderFactory.newInstance();
	}

	@Override
	public Document transform(TextNode node) {
		Document document = null;

		try {

			if (this._builder == null) {
				this._builder = this._docBuilderFactory.newDocumentBuilder();
				
			}

			document = this._builder.newDocument();
			document.setStrictErrorChecking(false);
			document.appendChild(doTransform(node, document));

		} catch (Exception e) {
			throw new TransformerException(e);
		}

		return document;
	}

	public Node doTransform(TextNode node, Document document) {
		Element element = document.createElement(node.getType());

		if (node.hasContent()) {
			element.setTextContent(node.getContent());
		}

		for (String attribute : node.getAttributes()) {
			element.setAttribute(attribute, node.getValue(attribute));
		}

		for (TextNode child : node.getChildren()) {
			element.appendChild(doTransform(child, document));
		}

		return element;
	}
}
