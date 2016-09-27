package mt.edu.um.mlrs.spell;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import mt.edu.um.mlrs.exception.SpellerException;

import com.aliasi.lm.CompiledNGramProcessLM;
import com.aliasi.spell.WeightedEditDistance;
import com.aliasi.tokenizer.TokenizerFactory;
import com.aliasi.util.ScoredObject;
import com.aliasi.util.Streams;

public class NGramSpellChecker implements SpellChecker {
	private CompiledSpellChecker _spellChecker;
	private String _modelFile;

	
	
	public NGramSpellChecker(String modelFile) {
		this._modelFile = modelFile;
		initModel();
	}

	public final void setTokenSet(Set<String> tokenSet) {
		_spellChecker.setTokenSet(tokenSet);
	}

	public Set<String> tokenSet() {
		return _spellChecker.tokenSet();
	}

	public void setTokenizerFactory(TokenizerFactory factory) {
		_spellChecker.setTokenizerFactory(factory);
	}

	public TokenizerFactory tokenizerFactory() {
		return _spellChecker.tokenizerFactory();
	}

	public boolean isCaseSensitive() {
		return false;
	}

	public void setCaseSensitive(boolean cs) {
		;// do nothing -- needed for override purposes
	}

	public CompiledNGramProcessLM languageModel() {
		return this._spellChecker.languageModel();
	}

	public void setLanguageModel(CompiledNGramProcessLM lm) {
		_spellChecker.setLanguageModel(lm);
	}

	public void setEditDistance(WeightedEditDistance distance) {
		this._spellChecker.setEditDistance(distance);
	}

	public void setAllowMatch(boolean allow) {
		this._spellChecker.setAllowMatch(allow);
	}

	public void setAllowDelete(boolean allow) {
		this._spellChecker.setAllowDelete(allow);
	}

	public void setAllowInsert(boolean allow) {
		this._spellChecker.setAllowInsert(allow);
	}

	public void setAllowSubstitute(boolean allow) {
		this._spellChecker.setAllowSubstitute(allow);
	}

	public void setAllowTranspose(boolean allow) {
		this._spellChecker.setAllowTranspose(allow);
	}

	public CompiledNGramProcessLM getModel() {
		return this._spellChecker.languageModel();
	}

	private void initModel() throws SpellerException {
		// create object input stream from file
		FileInputStream fileIn;
		BufferedInputStream bufIn;
		ObjectInputStream objIn;

		try {
			fileIn = new FileInputStream(new File(this._modelFile));
			bufIn = new BufferedInputStream(fileIn);
			objIn = new ObjectInputStream(bufIn);
			this._spellChecker = (CompiledSpellChecker) objIn
					.readObject();

		} catch (Exception e) {
			throw new SpellerException(
					"Error instantiating CompiledSpellChecker object", e);
		}

		// close the resources and return result
		Streams.closeInputStream(objIn);
		Streams.closeInputStream(bufIn);
		Streams.closeInputStream(fileIn);
	}

	public void setMinTokenLengthToCorrect(int length) {
		this._spellChecker.setMinimumTokenLengthToCorrect(length);
	}

	public void doNotEdit(Set<String> tokens) {
		this._spellChecker.setDoNotEditTokens(tokens);
	}

	public void setNBest(int n) {
		this._spellChecker.setNBest(n);
	}

	public int getNBest() {
		return this._spellChecker.nBestSize();
	}

	public void setFirstCharEditCost(double cost) {
		this._spellChecker.setFirstCharEditCost(cost);
	}

	public void setSecondCharEditCost(double cost) {
		this._spellChecker.setSecondCharEditCost(cost);
	}

	public void setKnownTokenEditCost(double cost) {
		this._spellChecker.setKnownTokenEditCost(cost);
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

	public List<String> getNBest(String text) {
		Iterator<ScoredObject<String>> iter = this._spellChecker
				.didYouMeanNBest(text);
		List<String> nbest = new ArrayList<String>();

		while (iter.hasNext()) {
			nbest.add(iter.next().getObject());
		}

		return nbest;
	}

	public double probability(String text) {
		return this._spellChecker.languageModel().prob(text);
	}

	public String getBest(String text) {
		String bestAlternative = this._spellChecker.didYouMean(text);
		return bestAlternative == null ? text : bestAlternative;
	}

}
