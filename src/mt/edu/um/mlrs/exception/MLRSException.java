package mt.edu.um.mlrs.exception;

public class MLRSException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MLRSException() {
		super();
	}

	public MLRSException(String message) {
		super(message);
	}

	public MLRSException(String message, Exception nested) {
		super(message, nested);
	}

	public MLRSException(Exception nested) {
		super(nested);
	}

}
