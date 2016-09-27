package mt.edu.um.mlrs.exception;

public class LexiconException extends MLRSException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LexiconException() {
		super();
	}

	public LexiconException(String message) {
		super(message);
	}

	public LexiconException(String message, Exception nested) {
		super(message, nested);
	}

	public LexiconException(Exception nested) {
		super(nested);
	}
}
