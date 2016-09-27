package mt.edu.um.rules.provide;

import mt.edu.um.rules.exception.ProviderException;
import uk.ac.abdn.ban.DatabaseException;
import uk.ac.abdn.ban.DatabaseHandler;

public abstract class DBProvider<E> implements Provider<E> {

	protected DatabaseHandler _db;

	public DBProvider(DatabaseHandler handler) {
		this._db = handler;
	}

	public DatabaseHandler getDBHandler() {
		return this._db;
	}

	public void reset() {
		this._db.removeAllListeners();
		this._db.closeDatabase();
	}

	public void initialise() throws ProviderException {
		try {
			this._db.openDatabase();
		} catch (DatabaseException de) {
			throw new ProviderException("Connection failed.", de);
		}
	}

	public void setDBParameters(String driver, String url, String user,
			String pass) {
		this._db.configureDatabase(driver, url, user, pass);
	}

	public abstract void populate(Container<E> container);

}
