package mt.edu.um.mlrs.spell;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;

import mt.edu.um.mlrs.split.Tokeniser;
import mt.edu.um.util.dict.TrieDictionary;
import mt.edu.um.util.dict.TrieNode;

import org.apache.commons.lang.StringUtils;

import com.aliasi.lm.NGramProcessLM;

public class NGramSpellChecker2 implements SpellChecker {

	private NGramProcessLM _lm;
	private int _nbest;
	private EditDistance _distance;
	private int _max;
	private boolean _caseSensitive;
	private TrieDictionary _dict;
	private Tokeniser _tokeniser;

	public NGramSpellChecker2(NGramProcessLM lm, TrieDictionary dict,
			Tokeniser tok) {
		this(lm, dict, tok, new EditDistance());
	}

	public NGramSpellChecker2(NGramProcessLM lm, TrieDictionary dict,
			Tokeniser tok, EditDistance distance) {
		this._lm = lm;
		this._distance = distance;
		this._dict = dict;
		this._tokeniser = tok;
	}

	public char[] getObservedCharacters() {
		return this._lm.observedCharacters();
	}

	public void setCaseSensitive(boolean cs) {
		this._caseSensitive = cs;
	}

	public boolean isCaseSensitive() {
		return this._caseSensitive;
	}

	public double getDeleteWeight() {
		return _distance.getDeleteWeight();
	}

	public double getInsertWeight() {
		return _distance.getInsertWeight();
	}

	public double getMatchWeight() {
		return _distance.getMatchWeight();
	}

	public double getSubstituteWeight() {
		return _distance.getSubstituteWeight();
	}

	public double getTransposeWeight() {
		return _distance.getTransposeWeight();
	}

	public void setDeleteWeight(double delete) {
		_distance.setDeleteWeight(delete);
	}

	public void setInsertWeight(double insert) {
		_distance.setInsertWeight(insert);
	}

	public void setMatchWeight(double match) {
		_distance.setMatchWeight(match);
	}

	public void setSubstituteWeight(double substitute) {
		_distance.setSubstituteWeight(substitute);
	}

	public void setTransposeWeight(double transpose) {
		_distance.setTransposeWeight(transpose);
	}

	public NGramProcessLM languageModel() {
		return this._lm;
	}

	@Override
	public String getBest(String text) {
		String msg = StringUtils.cleanWithin(text);

		if (msg.length() == 0) {
			return msg;
		}

		// create a new state tree
		List<String> tokens = this._tokeniser.split(msg);
		StringBuffer buffer = new StringBuffer();
		buffer.append(' ');

		for (String token : tokens) {
			buffer.append(token);
		}		

		return null;
	}

	private void insertions(Queue<SpellState> queue, SpellState state) {
		char[] nextChars = state._node.followingChars();
		
		for(int i = 0; i < nextChars.length; i++) {
			//SpellState newState = new SpellState(newState._node, false, );
		}
	}
	
	
	@Override
	public String[] getBest(String[] tokens) {
		return null;
	}
	
	public List<String> getBest(Collection<String> tokens) {
		List<String> results = new ArrayList<String>();
		
		for(String s: tokens) {
			results.add(getBest(s));
		}
		
		return results;
	}


	@Override
	public List<String> getNBest(String text) {
		return null;
	}

	@Override
	public void setNBest(int n) {
		this._nbest = n;
	}

	public int getNBest() {
		return this._nbest;
	}

	
	final class SpellState {
		TrieNode _node;
		Double _score;
		boolean _endToken;
		boolean _edited;
		char _ch;
		
		SpellState(TrieNode node, Double score, boolean end, boolean edit, char c) {
			this._node = node;
			this._score = score;
			this._endToken = end;
			this._edited = edit;
			this._ch = c;
		}
	}
	
}
