package mt.edu.um.mlrs.spell;

import java.util.Comparator;
import java.util.Queue;

import mt.edu.um.mlrs.split.Tokeniser;
import mt.edu.um.util.dict.DictEntry;
import mt.edu.um.util.dict.TrieDictionary;

import com.aliasi.util.BoundedPriorityQueue;

public class CharacterSubstitutionChecker extends DictionaryBasedSpellChecker {

	public static enum Strategy {
		LOWEST, HIGHEST
	};

	private CharacterMapping mapping;
	private boolean overrideDict;
	private Strategy strategy;
	private double overrideDictCost;

	public CharacterSubstitutionChecker(Tokeniser tok, TrieDictionary dict,
			CharacterMapping mapping) {
		super(tok, dict);
		this.mapping = mapping;
		this.overrideDict = false;
		this.overrideDictCost = Double.POSITIVE_INFINITY;
		
	}

	public void setStrategy(Strategy s) {
		this.strategy = s;
	}

	public void overrideDictionary(boolean override, double overrideCost) {
		this.overrideDict = override;
		this.overrideDictCost = overrideCost;
	}

	protected Queue<DictEntry> getReplacements(String word) {
		Comparator<DictEntry> comp = this.strategy == Strategy.HIGHEST ? DictEntry.HIGHEST_WEIGHT_COMPARATOR
				: DictEntry.LOWEST_WEIGHT_COMPARATOR;
		BoundedPriorityQueue<DictEntry> replacements = new BoundedPriorityQueue<DictEntry>(
				comp, this._nbest);
		recursiveGetReplacements(0, word, word, replacements);

		return replacements;
	}

	private void recursiveGetReplacements(int i, String word,
			String lastReplacement, Queue<DictEntry> replacements) {

		if (word.length() > this._dictionary.maxLength()) {
			replacements.offer(new DictEntry(word, word, 0.0D));

		} else {
			StringBuffer buffer;
			String newWord;
			char c;
			char toReplace;
			char repl;

			for (int j = i; j < lastReplacement.length(); j++) {
				c = lastReplacement.charAt(j); // char to replace
				toReplace = this._caseSensitive ? c : Character.toLowerCase(c);

				if (mapping.hasReplacement(toReplace)) {
					buffer = new StringBuffer(lastReplacement);
					String r = mapping.getReplacement(toReplace);
					repl = r.toCharArray()[0];
					buffer.setCharAt(j, this.renderCase(c, repl));
					newWord = buffer.toString();
					boolean wordKnown = dictContains(newWord);
					double dist;

					if (wordKnown) {
						dist = this._edit.distance(word, newWord,
								false);
						replacements.offer(new DictEntry(word, newWord, dist));
						// System.err.println(word + " " + newWord + " " +
						// dist);
					} else if (this.overrideDict) {
						dist = this._edit.distance(word, newWord,
								false) + this.overrideDictCost;
						replacements.offer(new DictEntry(word, newWord, dist));
					}

					recursiveGetReplacements(j, word, newWord, replacements);
				}
			}
		}
	}

	private boolean dictContains(String word) {
		boolean inDict = this._dictionary.contains(word);

		if (!inDict && !this._caseSensitive) {
			inDict = this._dictionary.contains(word.toLowerCase());
		}

		return inDict;
	}
}
