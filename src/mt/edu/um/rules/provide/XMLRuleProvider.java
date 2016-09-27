package mt.edu.um.rules.provide;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import mt.edu.um.rules.Argument;
import mt.edu.um.rules.BasicOperator;
import mt.edu.um.rules.DisjunctivePrecondition;
import mt.edu.um.rules.Operation;
import mt.edu.um.rules.Precondition;
import mt.edu.um.rules.Rule;
import mt.edu.um.rules.exception.ProviderException;
import mt.edu.um.util.xml.XPathUtils;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class XMLRuleProvider<I, O> extends XMLProvider<Rule<I, O>> {

	public XMLRuleProvider(String file) {
		super(file);
	}

	public void populate(Container<Rule<I, O>> container) {
		try {
			NodeList ruleNodes = XPathUtils.getChildNodes(this.document,
					"/rules/rule");

			for (int i = 0; i < ruleNodes.getLength(); i++) {
				Rule<I, O> rule = parseRule(ruleNodes.item(i));
				container.add(rule);
			}

			reset();

		} catch (Exception e) {
			throw new ProviderException("Error while parsing XML file.", e);
		}
	}

	protected abstract Rule<I, O> parseRule(Node node) throws Exception;

	protected abstract List<Operation<I, O>> parseRuleRHS(Node rhs)
			throws Exception;

	protected List<Argument<I>> parseRuleLHS(Node lhs) throws Exception {
		List<Argument<I>> args = new ArrayList<Argument<I>>();

		NodeList argNodes = XPathUtils.getChildNodes(lhs, "arg");

		for (int i = 0; i < argNodes.getLength(); i++) {
			Node argNode = argNodes.item(i);
			Argument<I> arg = new Argument<I>();

			for (Precondition<I> p : getPreconditions(argNode)) {
				arg.addPrecondition(p);
			}

			args.add(arg);
		}

		return args;
	}

	protected Collection<Precondition<I>> getPreconditions(Node node)
			throws Exception {
		List<Precondition<I>> result = new ArrayList<Precondition<I>>();
		NodeList preconditions = XPathUtils.getChildNodes(node,
				"or|isa|eq|defined|stringeq|neq|prefix|suffix|regex");

		for (int j = 0; j < preconditions.getLength(); j++) {
			Node preconditionNode = preconditions.item(j);
			String name = preconditionNode.getNodeName();

			if (name.equals("or")) {
				DisjunctivePrecondition<I> disj = new DisjunctivePrecondition<I>();

				for (Precondition<I> p : getPreconditions(preconditionNode)) {
					disj.addDisjunct(p);
				}

				result.add(disj);

			} else {
				name = name.toUpperCase();
				Precondition<I> precon = new Precondition<I>();
				BasicOperator op = BasicOperator.valueOf(name);
				String method = XPathUtils.getAttributeValue(preconditionNode, "valueof");
				String methodArgString = XPathUtils.getAttributeValue(
						preconditionNode, "args");
				String targetMethod = XPathUtils.getAttributeValue(
						preconditionNode, "target");
				String valueEnumName = XPathUtils.getAttributeValue(
						preconditionNode, "from");
				String argsEnumName = XPathUtils.getAttributeValue(
						preconditionNode, "args_from");

				Object expectedValue = XPathUtils.getAttributeValue(
						preconditionNode, "value");

				if (valueEnumName != null && expectedValue != null) {
					expectedValue = getEnumValue(valueEnumName,
							(String) expectedValue);
				}

				precon.setMethodToCall(method);
				precon.setExpectedValue(expectedValue);
				precon.setOperator(op);

				if (targetMethod != null) {
					precon.setMethodInvocationTarget(targetMethod);
				}

				if (methodArgString != null) {
					if (argsEnumName != null) {
						List<Object> args = new ArrayList<Object>();

						for (String s : methodArgString.split(",")) {
							args.add(getEnumValue(argsEnumName, s));
						}

						precon.setMethodArgs(args.toArray());

					} else {
						precon.setMethodArgs((Object[]) methodArgString
								.split(","));
					}
				}

				result.add(precon);
			}
		}

		return result;
	}

	protected Object getEnumValue(String enumName, String value)
			throws Exception {
		Object enumValue = null;
		Class<?> enumClass = Class.forName(enumName);

		for (Object o : enumClass.getEnumConstants()) {
			if (o.toString().equals(value)) {
				enumValue = o;
				break;
			}
		}

		return enumValue;
	}
}
