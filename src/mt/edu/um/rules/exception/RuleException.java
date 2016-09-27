package mt.edu.um.rules.exception;

/**
 * A <code>RuntimeException</code> thrown by any module within the
 * DiscoursePlanner. Represented in a separate class to permit applications to
 * specifically handle Microplanning exceptions.
 * 
 * @author agatt
 * 
 */

public class RuleException extends RuntimeException {

	static final long serialVersionUID = 1; // needed because this is

	// serialisable

	/**
	 * The constructor simply calls the superclass and prints an error message
	 * to the default <code>System.err</code>.
	 */
	public RuleException(String arg0) {
		super(arg0);
	}

	public RuleException(String arg, Exception e) {
		super(arg, e);
	}

	/**
	 * Constructor for nested exceptions.
	 * 
	 * @param e
	 *            The nested Exception
	 */
	public RuleException(Exception e) {
		super(e);
	}

}
