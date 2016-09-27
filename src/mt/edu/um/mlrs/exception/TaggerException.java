package mt.edu.um.mlrs.exception;

public class TaggerException extends TextProcessorException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TaggerException(String message) {
		super(message);
	}

	public TaggerException(String message, Exception nested) {
		super(message, nested);
	}

}
