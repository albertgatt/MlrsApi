package mt.edu.um.mlrs.exception;

public class ClassifierException extends MLRSException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ClassifierException() {
		super();
	}

	public ClassifierException(String message) {
		super(message);
	}

	public ClassifierException(String message, Exception nested) {
		super(message, nested);
	}

	public ClassifierException(Exception nested) {
		super(nested);
	}
}
