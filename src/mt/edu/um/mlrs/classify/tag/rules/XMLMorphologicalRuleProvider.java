package mt.edu.um.mlrs.classify.tag.rules;

import java.util.ArrayList;
import java.util.List;

import mt.edu.um.mlrs.text.TextNode;
import mt.edu.um.rules.Operation;
import mt.edu.um.rules.Rule;
import mt.edu.um.rules.provide.XMLRuleProvider;
import mt.edu.um.util.xml.XPathUtils;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class XMLMorphologicalRuleProvider extends
		XMLRuleProvider<TextNode, TextNode> {

	public XMLMorphologicalRuleProvider(String ruleFile) {
		super(ruleFile);
	}

	@Override
	protected Rule<TextNode, TextNode> parseRule(Node node) throws Exception {
		Rule<TextNode, TextNode> rule = new TagReplacementRule();
		String id = XPathUtils.getAttributeValue(node, "id");
		String sal = XPathUtils.getAttributeValue(node, "salience");

		if (id != null) {
			rule.setID(id);
		}

		if (sal != null) {
			rule.setSalience(Double.parseDouble(sal));
		}

		Node lhs = XPathUtils.getChildNode(node, "lhs");
		Node rhs = XPathUtils.getChildNode(node, "rhs");
		rule.addArguments(parseRuleLHS(lhs));
		rule.addOperations(parseRuleRHS(rhs));
		return rule;
	}

	@Override
	protected List<Operation<TextNode, TextNode>> parseRuleRHS(Node rhs)
			throws Exception {
		NodeList children = XPathUtils.getChildNodes(rhs, "settag");
		List<Operation<TextNode, TextNode>> operations = new ArrayList<Operation<TextNode, TextNode>>();

		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			String name = child.getNodeName();

			if (name.equals("settag")) {
				String value = XPathUtils.getAttributeValue(child, "value");
				Operation<TextNode, TextNode> op = new SetTag(value);
				operations.add(op);
			}
		}

		return operations;
	}

}
