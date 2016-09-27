package mt.edu.um.mlrs.exception;

public class TransformerException extends MLRSException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TransformerException() {
		super();
	}

	public TransformerException(String message) {
		super(message);
	}

	public TransformerException(String message, Exception nested) {
		super(message, nested);
	}

	public TransformerException(Exception nested) {
		super(nested);
	}
}
