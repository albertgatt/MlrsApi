package mt.edu.um.rules.exception;

public class ProviderException extends RuleException {

	/**
	 * Serial version ID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The constructor simply calls the superclass and prints an error message
	 * to the default <code>System.err</code>.
	 */
	public ProviderException(String arg0) {
		super(arg0);
	}

	public ProviderException(String arg, Exception e) {
		super(arg, e);
	}

	/**
	 * Constructor for nested exceptions.
	 * 
	 * @param e
	 *            The nested Exception
	 */
	public ProviderException(Exception e) {
		super(e);
	}

}
