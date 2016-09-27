package mt.edu.um.mlrs.spell;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mt.edu.um.mlrs.split.MTRegex;
import mt.edu.um.mlrs.split.Tokeniser;
import mt.edu.um.util.dict.DictEntry;
import mt.edu.um.util.dict.Dictionary;
import mt.edu.um.util.io.FileUtils;

public class DictionaryBasedSpellChecker implements SpellChecker {

	protected Tokeniser _tokeniser;
	protected double _maxEdit;
	protected EditDistance _edit;
	protected boolean _caseSensitive;
	protected Map<String, DictEntry> _knownSolutions; // word --> solution
	protected DictEntry _lastSolution;
	protected boolean _acceptAllSolutions;
	protected Dictionary _dictionary;
	protected Pattern _wordPattern;
	protected Matcher _wordMatcher;
	protected int _nbest;

	public DictionaryBasedSpellChecker(Tokeniser tok, Dictionary dict) {
		this._edit = new EditDistance();
		this._tokeniser = tok;
		this.setCaseSensitive(true);
		this.setTokeniser(tok);
		this._knownSolutions = new TreeMap<String, DictEntry>();
		this._acceptAllSolutions = false;
		this._dictionary = dict;
		this._nbest = 10;
	}
	
	public void setNBest(int n) {
		this._nbest = n;		
	}
	
	public int getNBest() {
		return this._nbest;
	}

	public void acceptAllSolutions(boolean accept) {
		this._acceptAllSolutions = accept;
	}

	public boolean acceptsAllSolutions() {
		return this._acceptAllSolutions;
	}

	public void setCaseSensitive(boolean cs) {
		this._caseSensitive = cs;
	}
	
	public boolean isCaseSensitive() {
		return this._caseSensitive;
	}

	public void setTokeniser(Tokeniser tok) {
		this._tokeniser = tok;
	}

	public void setMaxEditDistance(double max) {
		this._maxEdit = max;
	}

	public void setMatchWeight(double match) {
		this._edit.setMatchWeight(match);
	}

	public void setDeleteWeight(double delete) {
		this._edit.setDeleteWeight(delete);
	}

	public void setInsertWeight(double insert) {
		this._edit.setInsertWeight(insert);
	}

	public void setSubstituteWeight(double substitute) {
		this._edit.setSubstituteWeight(substitute);
	}

	public void setTransposeWeight(double transpose) {
		this._edit.setTransposeWeight(transpose);
	}
	
	public void readKnownSolutionsFromFile(File file, String charset,
			String separator) throws IOException {
		List<String> lines = FileUtils.readLinesFromFile(file, charset);

		for (String line : lines) {
			String[] split = line.split(separator);
			
			if(split.length < 2) {
				throw new IllegalArgumentException("Solutions file must have at least two fields per line");
			}
			
			addSolution(split[0], split[1]);
		}
	}

	public void addSolution(String word, String solution) {
		this._knownSolutions.put(this._caseSensitive ? word : word
				.toLowerCase(), new DictEntry(word, solution, this._edit
				.distance(word, solution, false)));
	}

	public void writeKnownSolutionsToFile(File file, String charset,
			String separator) throws IOException {
		BufferedWriter out = new BufferedWriter(FileUtils.getWriter(file,
				charset));
		List<DictEntry> entries = new ArrayList<DictEntry>(this._knownSolutions.values());
		Collections.sort(entries);
		
		for (DictEntry entry : entries) {
			out.write(entry.toString());
			out.write("\n");
		}

		out.close();
	}

	public Collection<DictEntry> getKnownSolutions() {
		return this._knownSolutions.values();
	}

	public String getBestPhrase(String phrase) {
		StringBuffer buffer = new StringBuffer();
		List<String> tokens = this._tokeniser.split(phrase);
		String token;
		// String previous;
		String next;

		for (int i = 0; i < tokens.size(); i++) {
			token = tokens.get(i);
			// previous = i == 0 ? null : tokens.get(i - 1);
			next = i == tokens.size() - 1 ? null : tokens.get(i + 1);
			buffer.append(getBest(token));

			if (MTRegex.requiresFollowingSpace(token, next)) {
				buffer.append(" ");
			}
		}

		return buffer.toString();
	}

	public String[] getBest(String[] tokens) {
		String[] results = new String[tokens.length];

		for (int i = 0; i < tokens.length; i++) {
			results[i] = getBest(tokens[i]);
		}

		return results;
	}
	
	public List<String> getBest(Collection<String> tokens) {
		List<String> results = new ArrayList<String>();
		
		for(String s: tokens) {
			results.add(getBest(s));
		}
		
		return results;
		
	}
	
	public String getBest(String w) {
		this._lastSolution = null;
		String word = this._caseSensitive ? w : w.toLowerCase();

		if (!this._dictionary.contains(word)) {

			if (this._knownSolutions.containsKey(word)) {
				this._lastSolution = this._knownSolutions.get(word);

			} else {				
				Queue<DictEntry> replacements = getReplacements(w);
				
				if (!replacements.isEmpty()) {
					this._lastSolution = replacements.poll();
				
				} else {
					this._lastSolution = new DictEntry(w, w, 0.0D);
				}
			}
		} else {
			this._lastSolution = new DictEntry(w, w, 0.0D);
		}

		if(this._acceptAllSolutions) {
			acceptLastSolution();
		}
		
		return this._lastSolution.getLabel().toString();
	}
	
	protected Queue<DictEntry> getReplacements(String w) {				
		return this._dictionary.nBestMatches(w, this._edit, this._maxEdit, this._nbest);
	}
	
	
	//TODO: generalise to n-best
	public List<String> getNBest(String word) {
		List<String> nbest = new ArrayList<String>();
		
		for(DictEntry e: getReplacements(word)) {
			nbest.add(e.getLabel().toString() + e.getWeight());
		}
		
		return nbest;
	}

	public void acceptLastSolution() {
		this._knownSolutions.put(this._lastSolution.getToken().toString(),
				this._lastSolution);
	}
	
	protected char renderCase(char oldChar, char newChar) {
		return Character.isUpperCase(oldChar) && Character.isLowerCase(newChar) ? Character.toUpperCase(newChar) : newChar;
	}
}
