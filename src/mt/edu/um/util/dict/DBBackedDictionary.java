package mt.edu.um.util.dict;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import mt.edu.um.mlrs.exception.MLRSException;

import java.sql.ResultSet;

public class DBBackedDictionary extends ListDictionary {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2268581914927070774L;
	protected Connection _conn;
	protected String _driver, _url, _user, _pass;
	protected String _selectAll = "SELECT word, frequency from words";
	protected String _selectFreq = "SELECT frequency FROM words WHERE word=?";
	protected String _selectByFreq = "SELECT word FROM words WHERE frequency >= ? AND frequency <= ?";
	protected String selectByRegex = "SELECT word, frequency FROM words WHERE word REGEXP ?";
	protected String _insertWord = "INSERT INTO words VALUES(?,?)";
	protected String _count = "SELECT count(*) FROM words";
	protected PreparedStatement _words, _freq, _wordsByFreq, _insert,
			_getCount, wordsByRegex;

	public DBBackedDictionary(String driver, String url, String user,
			String pass) {
		super();
		this._driver = driver;
		this._url = url;
		this._user = user;
		this._pass = pass;
	}

	public void init() throws MLRSException {
		// try to open DB and set up lexicon
		try {
			Class.forName(_driver);
			this._conn = DriverManager.getConnection(_url, _user, _pass);
			this._words = this._conn.prepareStatement(this._selectAll);
			this._freq = this._conn.prepareStatement(this._selectFreq);
			this._wordsByFreq = this._conn.prepareStatement(this._selectByFreq);
			this._insert = this._conn.prepareStatement(this._insertWord);
			this._getCount = this._conn.prepareStatement(this._count);
			this.wordsByRegex = this._conn.prepareStatement(this.selectByRegex);
		} catch (Exception ex) {
			throw new MLRSException("Connection could not be established", ex);
		}
	}

	@Override
	public void insert(CharSequence string) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean contains(CharSequence string) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<DictEntry> entries() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<CharSequence> tokens() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<DictEntry> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	public double getFrequency(String word) {
		DictEntry entry = getEntry(word);
		int freq = 0;
		
		if(entry != null) {
			freq = entry.getFrequency();
		}
		
		return freq;
	}

	public DictEntry getEntry(CharSequence seq) {
		DictEntry entry = null;
		String word = seq.toString();
		
		try {
			this._freq.setString(1, word);
			this._freq.execute();
			ResultSet result = this._freq.getResultSet();			
			
			if(result != null && result.next()) {
				int freq = result.getInt(1);			
				entry = new DictEntry(word, word, 0.0D);
				entry.incrementFrequency(freq);				
			}			
			
		} catch (Exception e) {
			throw new MLRSException("Error retrieving word data from table", e);
		}
		
		return entry;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
	}

	@Override
	public int numEntries() {
		int size = -1;

		try {
			this._getCount.execute();
			ResultSet result = this._getCount.getResultSet();

			if (result.next()) {
				size = result.getInt(1);
			}

			result.close();
			
		} catch (Exception e) {
			throw new MLRSException("Error retrieving word count", e);
		}

		return size;
	}

	public Collection<DictEntry> matchEntries(String regex) {
		List<DictEntry> matches = new ArrayList<DictEntry>();

		try {
			this.wordsByRegex.setString(1, regex);
			ResultSet result = this.wordsByRegex.executeQuery();
			DictEntry entry;
			
			while (result.next()) {
				String word = result.getString(1);
				int freq = result.getInt(2);
				entry = new DictEntry(word, word, 0);
				entry.incrementFrequency(freq);
				matches.add(entry);
			}			
			
			result.close();			
		} catch (Exception e) {
			throw new MLRSException("Error retrieving matching words", e);
		}

		return matches;
	}

}
