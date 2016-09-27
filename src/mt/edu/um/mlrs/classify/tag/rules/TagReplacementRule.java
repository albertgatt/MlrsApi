package mt.edu.um.mlrs.classify.tag.rules;

import mt.edu.um.mlrs.text.TextNode;
import mt.edu.um.rules.Operation;
import mt.edu.um.rules.Rule;
import mt.edu.um.rules.exception.RuleEvaluationException;

public class TagReplacementRule extends Rule<TextNode, TextNode> {

	public TagReplacementRule() {
		super();
	}

	@Override
	public TextNode apply(TextNode... objects) throws RuleEvaluationException {
		TextNode result = objects[0];

		for (Operation<TextNode, TextNode> op : this._operations) {
			try {
				result = op.apply(result);

			} catch (Exception e) {
				throw new RuleEvaluationException(
						"Exception applying operation " + op.getClass(), e);
			}
		}

		// TODO Auto-generated method stub
		return result;
	}

}
