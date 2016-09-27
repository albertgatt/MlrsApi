package mt.edu.um.mlrs.classify.tag.rules;

import mt.edu.um.mlrs.text.TextNode;
import mt.edu.um.rules.exception.RuleException;

public class SetTag extends TagReplacementOperation {

	private String _tag;

	public SetTag(String tag) {
		super();
		this._tag = tag;
	}

	@Override
	public TextNode apply(TextNode... args) throws RuleException {

		if ((args.length == 0) || (args.length > 1)) {
			throw new RuleException(
					"ReTagOperation can only apply to one argument");
		}

		TextNode oldNode = args[0];
		TextNode newNode = new TextNode(oldNode.getType());
		newNode.setContent(oldNode.getContent());

		for (String attribute : oldNode.getAttributes()) {
			newNode.setValue(attribute, oldNode.getValue(attribute));
		}

		newNode.setValue("CAT", this._tag);
		return newNode;
	}

}
