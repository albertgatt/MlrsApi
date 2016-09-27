package mt.edu.um.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Loggable {

	protected Logger _logger = Logger.getAnonymousLogger();
	protected String _loggingClass;
	
	public Loggable() {
		this._loggingClass = this.getClass().getName();
	}

	public void setLoggingClass(String cls) {
		this._loggingClass = cls;
	}
	
	public void setLogger(Logger logger) {
		this._logger = logger;
	}

	public Logger getLogger() {
		return this._logger;
	}

	protected void logMessage(String message) {
		logMessage(Level.INFO, message);
	}

	protected void logMessage(Exception e) {
		logMessage(Level.WARNING, e);
	}

	protected void logMessage(Level loggingLevel, Exception e) {		
		e.printStackTrace();
		if (this._logger != null) {
			this._logger.logp(loggingLevel, this._loggingClass, null,
					"Exception thrown: " + e.getClass() + " " + e.getMessage());
		
		} else {
			throw new RuntimeException(e);
		}
	}

	protected void logMessage(Level loggingLevel, String message) {

		if (this._logger != null) {
			this._logger.logp(loggingLevel, this._loggingClass, null, message);		
		} 

	}
}
