package mt.edu.um.mlrs.split;

import java.util.ArrayList;
import java.util.List;

import mt.edu.um.mlrs.spell.SpellChecker;

public abstract class Tokeniser extends Splitter {
	/**
	 * List of tokens and whitespace tokens identified at last call
	 */
	protected List<String> _tokens, _spaces;
	protected SpellChecker _speller;
	protected boolean _keepEmptyTokens;
	
	public Tokeniser() {
		this._tokens = new ArrayList<String>();
		this._spaces = new ArrayList<String>();
		addTargetType("SENTENCE");
		addTargetType("HEADING");
		addTargetType("LIST-ITEM");
		setDestinationType("TOKEN");
	}
	
	public void setSpellChecker(SpellChecker checker) {
		this._speller = checker;
	}
	
	public void setKeepEmptyTokens(boolean keep) {
		this._keepEmptyTokens =keep;
	}
	
	public boolean keepEmptyTokens() {
		return this._keepEmptyTokens;
	}
	
	public SpellChecker getSpellChecker() {
		return this._speller;
	}
	
	public List<String> getWhitespaceTokens() {
		return this._spaces;
	}
	
}
