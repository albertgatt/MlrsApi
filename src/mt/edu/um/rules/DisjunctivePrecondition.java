package mt.edu.um.rules;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class DisjunctivePrecondition<I> extends Precondition<I> {

	private Set<Precondition<I>> _disjuncts;

	public DisjunctivePrecondition() {
		this._disjuncts = new HashSet<Precondition<I>>();
	}

	public void addDisjunct(Precondition<I> precondition) {
		this._disjuncts.add(precondition);
	}

	public void addDisjuncts(Collection<Precondition<I>> preconditions) {
		this._disjuncts.addAll(preconditions);
	}

	public Set<Precondition<I>> getDisjuncts() {
		return this._disjuncts;
	}

	public int numDisjuncts() {
		return this._disjuncts.size();
	}

	@Override
	public boolean evaluate(I input) throws Exception {
		boolean satisfied = false;
		Iterator<Precondition<I>> iterator = this._disjuncts.iterator();

		while (iterator.hasNext() && !satisfied) {
			satisfied = iterator.next().evaluate(input);
		}

		return satisfied;
	}

	@Override
	public boolean equals(Object o) {
		boolean eq = false;

		if (o instanceof DisjunctivePrecondition<?>) {
			eq = ((DisjunctivePrecondition<?>) o)._disjuncts
					.equals(this._disjuncts);
		}

		return eq;
	}

}
