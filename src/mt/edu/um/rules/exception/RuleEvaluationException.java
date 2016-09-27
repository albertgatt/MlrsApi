package mt.edu.um.rules.exception;

public class RuleEvaluationException extends RuleException {
	static final long serialVersionUID = 1; // needed because this is

	// serialisable

	/**
	 * The constructor simply calls the superclass and prints an error message
	 * to the default <code>System.err</code>.
	 */
	public RuleEvaluationException(String arg0) {
		super(arg0);
	}

	public RuleEvaluationException(String arg, Exception e) {
		super(arg, e);
	}

	/**
	 * Constructor for nested exceptions.
	 * 
	 * @param e
	 *            The nested Exception
	 */
	public RuleEvaluationException(Exception e) {
		super(e);
	}
}
