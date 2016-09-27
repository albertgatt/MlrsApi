package mt.edu.um.util.dict;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;

import mt.edu.um.mlrs.spell.EditDistance;
import mt.edu.um.util.io.FileUtils;

import org.apache.commons.lang.StringUtils;

import com.aliasi.util.BoundedPriorityQueue;

public class ListDictionary extends Dictionary {
	private static final long serialVersionUID = 1L;
	private Map<CharSequence, DictEntry> _entries;
	private double _total;

	public static ListDictionary fromFile(String wordFile, String charset,
			String separator) {
		ListDictionary dict = null;

		try {
			List<String> lines = FileUtils.readLinesFromFile(wordFile, charset);
			dict = new ListDictionary();
			DictEntry entry = null;

			for (int i = 0; i < lines.size(); i++) {
				String s = lines.get(i);

				if (s != null) {
					String[] fields = lines.get(i).split(separator);

					if (fields.length == 1) {
						entry = new DictEntry(fields[0]);
						dict.insert(entry);

					} else if (fields.length == 2) {

						if (StringUtils.isNumeric(fields[1])) {
							entry = new DictEntry(fields[0], fields[0], 0.0D,
									Integer.parseInt(fields[1]));
						
						} else {
							entry = new DictEntry(fields[0], fields[1]);
						}

						dict.insert(entry);

					} else if (fields.length == 3) {
						entry = new DictEntry(fields[0], fields[1], Double
								.parseDouble(fields[2]));
						dict.insert(entry);

					} else {
						throw new IllegalArgumentException(
								"One, two or three fields per line required at line "
										+ i);
					}										
				}
			}
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}

		return dict;
	}

	public ListDictionary() {
		super();
		this._entries = new TreeMap<CharSequence, DictEntry>();
		this._maxLength = 0;
		this._minLength = Integer.MAX_VALUE;
		this._total = 0;
	}

	public int numEntries() {
		return this._entries.size();
	}

	public Queue<DictEntry> nBestMatches(String token, EditDistance edit,
			double max, int n) {
		Queue<DictEntry> nbest = new BoundedPriorityQueue<DictEntry>(
				DictEntry.LOWEST_WEIGHT_COMPARATOR, n);

		// initialise a worst score
		double worstScore = Double.POSITIVE_INFINITY;

		for (CharSequence tok : this.tokens()) {
			double score = edit.distance(token, tok, false);

			if (score < worstScore && score <= max) {
				nbest.offer(new DictEntry(token, tok, score));
				worstScore = score;
			}
		}

		return nbest;
	}

	public void clear() {
		this._entries.clear();
		this._maxLength = 0;
		this._minLength = Integer.MAX_VALUE;
		this._total = 0;
	}

	public Iterator<DictEntry> iterator() {
		return this._entries.values().iterator();
	}

	public void insert(CharSequence string) {
		if(this.contains(string)) {
			this._entries.get(string).incrementFrequency(1);
		} else {
			DictEntry entry = new DictEntry(string,string);
			entry.incrementFrequency(1);
			insert(entry);
		}
	
	}

	public void insert(CharSequence word, double weight) {
		insert(word, word, weight);
	}

	public void insert(CharSequence word, CharSequence cat, double weight) {
		insert(new DictEntry(word, cat, weight));
	}

	public void insert(CharSequence word, CharSequence cat) {
		insert(new DictEntry(word, cat));
	}

	public void insert(DictEntry entry) {
		CharSequence token = entry.getToken();
		CharSequence category = entry.getLabel();		
		this._total += entry.getFrequency();

		if (this.containsToken(token, category)) {
			this.getEntry(token).incrementFrequency(entry.getFrequency());

		} else {
			this._entries.put(token, entry);
			updateChars(token);
		}
	}

	public int numTokensWithWeight(double wt) {
		List<DictEntry> entries = topN(size());
		int num = 0;
		double weight;

		for (DictEntry entry : entries) {
			weight = entry.getWeight();

			if (weight > wt) {
				break;
			} else if (weight == wt) {
				num++;
			}
		}

		return num;
	}

	public double total() {
		return this._total;
	}

	public int compare(String token1, String token2) {
		return ((Double) getWeight(token1)).compareTo(getWeight(token2));
	}

	public double getWeight(String token) {
		return this.contains(token) ? this._entries.get(token).getWeight()
				: Double.NaN;
	}

	public DictEntry getEntry(CharSequence token) {
		return this.contains(token) ? this._entries.get(token) : null;
	}

	public CharSequence getCategory(CharSequence token) {
		return this.contains(token) ? this._entries.get(token).getLabel()
				: null;
	}

	public boolean contains(CharSequence token) {
		return this._entries.containsKey(token);
	}

	public boolean containsToken(CharSequence token, CharSequence category) {
		return contains(token)
				&& this._entries.get(token).getLabel().equals(category);
	}

	public Set<CharSequence> tokens() {
		return this._entries.keySet();
	}

	public Collection<DictEntry> entries() {
		return this._entries.values();
	}

	public Collection<DictEntry> matchEntries(String regex) {
		List<DictEntry> matches = new ArrayList<DictEntry>();

		for (CharSequence s : this._entries.keySet()) {
			if (Pattern.matches(regex, s)) {
				matches.add(this._entries.get(s));
			}
		}

		return matches;
	}

	public int size() {
		return this._entries.size();
	}

	public List<DictEntry> topN(int n) {
		List<DictEntry> topN;
		List<DictEntry> sortedEntries = new ArrayList<DictEntry>(this._entries
				.values());
		Collections.sort(sortedEntries);

		if (n <= this._entries.size()) {
			topN = sortedEntries.subList(0, n);

		} else {
			topN = sortedEntries;
		}

		return topN;
	}

	public void writeToFile(File f, String charset) throws IOException {
		BufferedWriter writer = new BufferedWriter(FileUtils.getWriter(f,
				charset));
		List<DictEntry> sorted = topN(size());
		StringBuffer outString;

		for (DictEntry entry : sorted) {
			outString = new StringBuffer(entry.getToken());
			outString.append("\t").append(entry.getLabel()).append("\t")
					.append(entry.getWeight()).append("\t").append(
							entry.getFrequency()).append("\n");
			writer.write(outString.toString());
		}

		writer.close();
	}

}
