package mt.edu.um.util.xml;

import java.util.HashMap;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XPathUtils {

	public static XPath xpath;

	private static void checkXPath() {
		if (xpath == null) {
			xpath = XPathFactory.newInstance().newXPath();
		}
	}

	public static NodeList getNodeList(Node node, String path)
			throws XPathException {
		checkXPath();
		NodeList list = null;
		list = (NodeList) xpath.evaluate(path, node, XPathConstants.NODESET);
		return list;
	}

	public static Node getNode(Node node, String path) throws XPathException {
		checkXPath();
		Node result = null;
		result = (Node) xpath.evaluate(path, node, XPathConstants.NODE);
		return result;
	}

	public static NodeList getChildNodes(Node parent, String path)
			throws XPathException {
		checkXPath();
		NodeList list = null;
		list = (NodeList) xpath.evaluate(path, parent, XPathConstants.NODESET);
		return list;
	}

	public static Map<String, String> getAttributes(Node node)
			throws XPathException {
		Map<String, String> attributes = new HashMap<String, String>();

		if (node.hasAttributes()) {
			NamedNodeMap attmap = node.getAttributes();

			for (int i = 0; i < attmap.getLength(); i++) {
				Node attNode = attmap.item(i);
				attributes.put(attNode.getNodeName(), attNode.getNodeValue());
			}
		}

		return attributes;
	}

	public static Node getChildNode(Node parent, String path)
			throws XPathException {
		checkXPath();
		Node node = null;
		node = (Node) xpath.evaluate(path, parent, XPathConstants.NODE);
		return node;
	}

	public static String getAttributeValue(Node parent, String att) {
		checkXPath();
		String value = null;
		NamedNodeMap map = parent.getAttributes();

		if (map != null) {
			Node attribute = map.getNamedItem(att);

			if (attribute != null) {
				value = attribute.getNodeValue();
			}
		}
		return value;
	}

}
