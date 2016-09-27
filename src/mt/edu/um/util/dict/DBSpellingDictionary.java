package mt.edu.um.util.dict;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import mt.edu.um.mlrs.exception.MLRSException;
import mt.edu.um.mlrs.spell.EditDistance;

public class DBSpellingDictionary extends DBBackedDictionary {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6463225156219405739L;
	protected String _getWordCorr = "SELECT correction FROM corrections2 WHERE word = ?";
	protected String _getAllCorr = "SELECT word, correction FROM corrections2 WHERE 1";
	protected PreparedStatement getCorrectionsByToken, getCorrections;
	protected EditDistance _edit;
	protected Map<String, List<DictEntry>> _cache;

	public DBSpellingDictionary(String driver, String url, String user,
			String pass) {
		super(driver, url, user, pass);
		this._edit = new EditDistance();
		this._cache = new TreeMap<String, List<DictEntry>>();

	}

	public void init() throws MLRSException {
		super.init();

		try {
			this.getCorrectionsByToken = this._conn
					.prepareStatement(this._getWordCorr);
			this.getCorrections = this._conn.prepareStatement(this._getAllCorr);
		} catch (Exception e) {
			throw new MLRSException("Could not prepare entries statement", e);
		}
	}

	public Map<String, List<DictEntry>> getCorrections() {
		Map<String, List<DictEntry>> corrections = new TreeMap<String, List<DictEntry>>();

		try {
			ResultSet result = this.getCorrections.executeQuery();
			
			while(result.next()) {
				String token = result.getString(1);	
				DictEntry entry = this.getEntry(token); // entry with frequency
				List<DictEntry> alternatives = (corrections.containsKey(token) ? corrections.get(token) : new ArrayList<DictEntry>());				
				String alt = result.getString(2);
				entry._label = alt;
				entry._weight = this._edit.distance(token, alt, false);
				alternatives.add(entry);				
 			}
			
			result.close();
			return corrections;
			
		} catch (SQLException e) {
			throw new MLRSException("Error iterating through ResultSet", e);
		}

	}

	public List<DictEntry> getCorrections(String token) {
		List<DictEntry> entries = null;

		if (this._cache.containsKey(token)) {
			entries = this._cache.get(token);

		} else {
			DictEntry mainEntry = this.getEntry(token); // entry with frequency
			entries = new ArrayList<DictEntry>();

			try {
				this.getCorrectionsByToken.setString(1, token);
				ResultSet result = this.getCorrectionsByToken.executeQuery();
				String corr;

				while (result.next()) {
					corr = result.getString(1);

					if (!corr.equalsIgnoreCase(token)) {
						DictEntry entry = new DictEntry(token, corr, this._edit
								.distance(token, corr, false));

						if (mainEntry != null) {
							entry.incrementFrequency(mainEntry.getFrequency());
							entries.add(entry);
						}
					}
				}

				result.close();
			} catch (Exception e) {
				throw new MLRSException("Could not retrieve alternatives", e);
			}

			Collections.sort(entries, DictEntry.LOWEST_WEIGHT_COMPARATOR);
			this._cache.put(token, entries);
		}

		return entries;
	}

}
