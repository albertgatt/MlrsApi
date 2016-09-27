package mt.edu.um.mlrs.text.transform;

import java.util.Map;

import javax.xml.xpath.XPathException;

import mt.edu.um.mlrs.exception.TransformerException;
import mt.edu.um.mlrs.text.TextNode;
import mt.edu.um.util.xml.XPathUtils;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class XmlToTextNode extends TextTransformer<Node, TextNode> {

	public XmlToTextNode() {
		super();
	}
	

	// public XmlToTextNode(TransformationRules<Node,TextNode> rules) {
	// super(rules);
	// }
	
	@Override
	public TextNode transform(Node xml) throws TransformerException {
		TextNode node = null;

		if (xml instanceof Document) {
			node = transform(xml.getFirstChild());
			
		} else {
			node = new TextNode(xml.getNodeName());

			try {
				Map<String, String> attributes = XPathUtils.getAttributes(xml);

				for (String a : attributes.keySet()) {
					node.setValue(a, attributes.get(a));
				}

				NodeList children = xml.getChildNodes();

				for (int i = 0; i < children.getLength(); i++) {
					Node child = children.item(i);

					if (child instanceof CDATASection || child instanceof Text) {
						node.setContent(child.getNodeValue());

					} else {
						TextNode childTransform = transform(child);
						node.addChild(childTransform);
					}
				}

			} catch (XPathException xpe) {
				throw new TransformerException("Exception processing XML.", xpe);
			}
		}

		return node;
	}

}
