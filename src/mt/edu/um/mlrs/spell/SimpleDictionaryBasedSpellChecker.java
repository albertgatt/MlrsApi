package mt.edu.um.mlrs.spell;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import mt.edu.um.util.dict.DBSpellingDictionary;
import mt.edu.um.util.dict.DictEntry;

public class SimpleDictionaryBasedSpellChecker implements SpellChecker {

	protected DBSpellingDictionary _dictionary;
	
	public SimpleDictionaryBasedSpellChecker(DBSpellingDictionary dict) {
		super();
		this._dictionary = dict;
	}			

	@Override
	public List<String> getBest(Collection<String> tokens) {
		return null;
	}

	@Override
	public String getBest(String text) {
		List<DictEntry> entries = this._dictionary.getCorrections(text);
		String solution;
		
		if(entries.size() > 0) {
			solution = entries.get(0).getLabel().toString();
		} else {
			solution = null;
		}
		
		return solution;
	}

	@Override
	public String[] getBest(String[] tokens) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNBest() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<String> getNBest(String text) {
		List<DictEntry> entries = this._dictionary.getCorrections(text);
		List<String> solutions = new ArrayList<String>();
		
		for(DictEntry de: entries) {
			solutions.add(de.getLabel().toString());
		}
		
		return solutions;
	}

	@Override
	public boolean isCaseSensitive() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setCaseSensitive(boolean cs) {
		
	}

	@Override
	public void setNBest(int n) {
		// TODO Auto-generated method stub
		
	}

	
	
}
