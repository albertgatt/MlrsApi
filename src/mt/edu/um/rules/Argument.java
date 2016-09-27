package mt.edu.um.rules;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import mt.edu.um.rules.exception.RuleEvaluationException;

public class Argument<I> {

	private List<Precondition<I>> _preconditions;

	public Argument() {
		this._preconditions = new ArrayList<Precondition<I>>();
	}

	public void addPrecondition(Precondition<I> precondition) {
		this._preconditions.add(precondition);
	}

	public boolean evaluate(I object) throws RuleEvaluationException {
		boolean success = true;
		Iterator<Precondition<I>> iter = this._preconditions.iterator();

		while (iter.hasNext() && success) {
			try {
				success = iter.next().evaluate(object);
			} catch (Exception e) {				
				throw new RuleEvaluationException(
						"Evaluation exception on object " + object + ": "
								+ e.getClass() + " " + e.getMessage(), e);
			}
		}

		return success;
	}
}
