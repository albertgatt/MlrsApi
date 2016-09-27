package mt.edu.um.rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mt.edu.um.rules.exception.RuleEvaluationException;

public abstract class Rule<I,O> implements Comparable<Rule<I,O>> {

	protected String _id;
	protected List<Argument<I>> _args;
	protected double _salience;
	protected List<Operation<I,O>> _operations;

	public Rule() {
		this._args = new ArrayList<Argument<I>>();
		this._operations = new ArrayList<Operation<I,O>>();
		this._salience = 100;
	}

	public Rule(String id) {
		this();
		setID(id);
	}

	public Rule(String id, double salience) {
		this(id);
		setSalience(salience);
	}

	public int numArgs() {
		return this._args.size();
	}

	public void setSalience(Number number) {
		this._salience = number.doubleValue();
	}

	public double getSalience() {
		return this._salience;
	}

	public void setID(String id) {
		this._id = id;
	}

	public String getID() {
		return this._id;
	}

	public void addArguments(Argument<I>... args) {
		for (Argument<I> arg : args) {
			this._args.add(arg);
		}
	}

	public void addArguments(List<Argument<I>> args) {
		this._args.addAll(args);
	}

	public List<Argument<I>> getArguments() {
		return this._args;
	}

	public void addOperations(Operation<I,O>... operations) {
		addOperations(Arrays.asList(operations));
	}

	public void addOperations(List<Operation<I,O>> operations) {
		this._operations.addAll(operations);
	}

	public List<Operation<I,O>> getOperations() {
		return this._operations;
	}

	public boolean appliesTo(I... objects) throws RuleEvaluationException {
		boolean success = objects.length == this._args.size();

		for (int i = 0; i < objects.length && success; i++) {
			success = this._args.get(i).evaluate(objects[i]);
		}

		return success;
	}

	@Override
	public int compareTo(Rule<I,O> r) {
		int comp = 0;
		Double mySalience = this.getSalience();
		Double yourSalience = r.getSalience();

		if (mySalience == yourSalience) {
			Integer myArgs = this.numArgs();
			Integer yourArgs = r.numArgs();
			comp = myArgs.compareTo(yourArgs);
		} else {
			comp = mySalience.compareTo(yourSalience);
		}

		return -comp;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("RULE[").append(this._id)
				.append(" (").append(this.numArgs()).append(")]");
		return builder.toString();
	}

	public abstract O apply(I... objects) throws RuleEvaluationException;

}
