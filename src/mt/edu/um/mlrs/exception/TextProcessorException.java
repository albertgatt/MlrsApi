package mt.edu.um.mlrs.exception;

public class TextProcessorException extends MLRSException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TextProcessorException(String message) {
		super(message);
	}

	public TextProcessorException(String message, Exception nested) {
		super(message, nested);
	}

	public TextProcessorException(Exception nested) {
		super(nested);
	}

}
