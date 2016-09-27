package mt.edu.um.mlrs.exception;

public class SpellerException extends MLRSException {
	
	public SpellerException() {
		super();
	}

	public SpellerException(String message) {
		super(message);
	}
	
	public SpellerException(Exception nested) {
		super(nested);
	}

	public SpellerException(String message, Exception nested) {
		super(message, nested);
	}

}
