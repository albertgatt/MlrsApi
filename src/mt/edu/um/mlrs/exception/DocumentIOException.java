package mt.edu.um.mlrs.exception;

public class DocumentIOException extends MLRSException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DocumentIOException() {
		super();
	}

	public DocumentIOException(String message) {
		super(message);
	}

	public DocumentIOException(String message, Exception nested) {
		super(message, nested);
	}

}
