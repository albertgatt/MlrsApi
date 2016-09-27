package mt.edu.um.util.dict;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import mt.edu.um.mlrs.spell.EditDistance;
import mt.edu.um.util.io.FileUtils;

import com.aliasi.util.BoundedPriorityQueue;

/**
 * A TrieDictionary represents sequences (tokens) using a Trie structure
 * (essentially, a character tree, where common prefixes are stored only once).
 * See {@link http://en.wikipedia.org/wiki/Trie} for more information on tries.
 * The present implementation also permits the computation of frequencies of
 * sequences.
 * 
 * <P>
 * <strong>Implementation note:</strong> The dictionary stores sequences as
 * children of a single root {@link TrieNode}. Several methods are delegated to
 * the root node.
 * 
 * @author Albert Gatt
 * 
 */
public class TrieDictionary extends Dictionary {

	private static final long serialVersionUID = 6208010986535718539L;

	public static TrieDictionary fromFile(String file, String charset,
			boolean lc) throws IOException {
		List<String> lines = FileUtils.readLinesFromFile(file, charset);
		TrieDictionary dict = new TrieDictionary();

		for (String token : lines) {
			
			if (token.length() > 0 ) {
				token = token.toLowerCase().trim();

				if (token.matches("[\\p{L}-']+") && !dict.contains(token)) {
					dict.insert(lc ? token.toLowerCase().trim() : token);
				}
			}
		}

		return dict;
	}

	/*
	 * The root node of the Trie
	 */
	private TrieNode _root;

	/**
	 * Construct a new instance of a TrieDictionary
	 */
	public TrieDictionary() {
		super();
		this._root = new TrieNode();
	}

	/**
	 * Get the frequency of a particular sequence.
	 * 
	 * @param token
	 *            the sequence
	 * @return the frequency
	 */
	public int tokenFrequency(String token) {
		return this._root.tokenFrequency(token);
	}

	/**
	 * Get the root node of this Trie.
	 * 
	 * @return the root
	 */
	public TrieNode root() {
		return this._root;
	}

	/**
	 * REturns the number of entries that the TrieDictionary contains, that is
	 * the number of sequences inserted -- this is essentially the number of
	 * times {@link #insert(CharSequence)} has been called.
	 * 
	 * @return the number of entries
	 */
	public int numEntries() {
		return this._root.numInsertions();
	}

	/**
	 * Return a string representing the trie in a format that permits easy
	 * viewing
	 * 
	 * @return a string representation
	 */
	public String printTrie() {
		return this._root.prettyString();
	}

	/**
	 * Empties the dictionary, initialising a new root node with no children.
	 */
	public void clear() {
		this._root = new TrieNode();
	}

	/**
	 * Check whether the given sequence is contained in this dictionary
	 * 
	 * @return <code>true</code> if the dictionary contains the sequence
	 */
	public boolean contains(CharSequence string) {
		return _root.contains(string);
	}

	/**
	 * Check whether a given sequence is a token. A sequence c1,...,cn is a
	 * token iff (a) c1 is an immediate child of the root node; (b) cn is an end
	 * node. Effectively, this returns true if {@link #insert(CharSequence)} was
	 * previously called with this sequence.
	 * 
	 * @param string
	 *            the sequence
	 * @return <code>true</code> if the sequence is an entire token.
	 */
	public boolean isToken(CharSequence string) {
		return this._root.isToken(string);
	}

	public void insert(CharSequence string) {
		_root.insert(string);
		updateChars(string);
	}

	/**
	 * Get the set of entries in this dictionary. These are returned as
	 * instances of {@link DictEntry}.
	 * 
	 * @return THe set of entries
	 */
	public Set<DictEntry> entries() {
		TrieDictionaryIterator iterator = (TrieDictionaryIterator) iterator();
		Set<DictEntry> entries = new HashSet<DictEntry>();

		while (iterator.hasNext()) {
			DictEntry next = iterator.next();
			entries.add(next);
		}

		return entries;
	}

	/**
	 * Get the set of tokens in this dictionary -- this is the set of strings
	 * which were inserted.
	 * 
	 * @return the set of tokens
	 */
	public Set<CharSequence> tokens() {
		Iterator<DictEntry> iter = iterator();
		Set<CharSequence> tokens = new HashSet<CharSequence>();

		while (iter.hasNext()) {
			tokens.add(iter.next().getToken());
		}

		return tokens;

	}

	/**
	 * Get an iterator over the entries in this dictionary.
	 * 
	 * @return an iterator
	 */
	public Iterator<DictEntry> iterator() {
		return new TrieDictionaryIterator(this._root);
	}

	/**
	 * 
	 * @param token
	 * @param edit
	 * @param max
	 * @param n
	 * @return
	 */
	public Queue<DictEntry> nBestMatches(String token, EditDistance edit,
			double max, int n) {
		Queue<DictEntry> nbest = new BoundedPriorityQueue<DictEntry>(
				DictEntry.LOWEST_WEIGHT_COMPARATOR, n);
		findBest(nbest, token, "", edit, max, new TrieEntry(this._root, token,
				"", edit.distance(token, "", false)));
		return nbest;
	}

	public DictEntry bestMatch(String token, EditDistance edit, double max) {
		Queue<DictEntry> nbest = new PriorityQueue<DictEntry>();
		findBest(nbest, token, "", edit, max, new TrieEntry(this._root, token,
				"", edit.distance(token, "", false)));
		DictEntry result = nbest.isEmpty() ? new DictEntry(token, token, 0.0)
				: nbest.poll();
		return result;
	}

	public int neighbourhodDensity(String token, EditDistance edit, double max,
			int queuesize) {
		Queue<DictEntry> nbest = nBestMatches(token, edit, max, queuesize);

		return nbest.size();

	}

	private void findBest(Queue<DictEntry> nbest, String token, String start,
			EditDistance edit, double max, TrieEntry best) {

		String candidate;
		TrieNode currentNode = best._node;
		char currChar;

		for (TrieNode child : currentNode.children()) {

			if (child != null) {
				currChar = child.getChar();

				if (currChar != 0) {
					candidate = new StringBuffer().append(best.getLabel())
							.append(currChar).toString();
					double dist = edit.distance(candidate, token, false);
					TrieEntry entry = new TrieEntry(child, token,
							candidate.toString(), dist);

					if (dist <= max && isToken(candidate)) {
						nbest.offer(entry);
					}

					findBest(nbest, token, candidate, edit, max, entry);
				}
			}
		}

	}

	// private void findBest(Queue<DictEntry> nbest, String token, String start,
	// EditDistance edit, double max) {
	// TrieNode currentNode;
	// TrieEntry best = new TrieEntry(this._root, token, start, edit.distance(
	// token, start, false));
	// // nbest.offer(best);
	// char currChar;
	//
	// while (true) {
	// currentNode = best._node;
	// String candidate;
	//
	// for (TrieNode child : currentNode.children()) {
	//
	// if (child != null) {
	// currChar = child.getChar();
	//
	// if (currChar != 0) {
	// candidate = new StringBuffer().append(best.getLabel())
	// .append(currChar).toString();
	// double dist = edit.distance(candidate, token, false);
	//
	// if (dist <= max) {
	//
	// if (contains(candidate)) {
	// nbest.offer(new TrieEntry(child, token,
	// candidate.toString(), dist));
	// } else {
	// findBest(nbest, token, candidate, edit, max);
	// }
	// }
	// }
	// }
	// }
	//
	// TrieEntry next = (TrieEntry) nbest.peek();
	//
	// if (next == null || next == best) {
	// break;
	// } else {
	// best = next;
	// }
	// }
	// }

	final class TrieEntry extends DictEntry {
		TrieNode _node;

		TrieEntry(TrieNode node, String token, String replacement, double weight) {
			super(token, replacement, weight);
			this._node = node;
		}

		public String toString() {
			return new StringBuffer().append(this.getToken()).append("/")
					.append(this.getLabel()).append("/")
					.append(this.getWeight()).toString();
		}

		public boolean equals(Object o) {
			boolean tok = false;
			boolean cat = false;
			boolean wt = false;
			boolean n = false;

			if (o instanceof TrieEntry) {
				TrieEntry alt = (TrieEntry) o;
				cat = this._label == alt._label
						|| this._label.equals(this._label);
				tok = this._token == alt._token
						|| this._token.equals(this._token);
				wt = this._weight == alt._weight
						&& this._frequency == alt._frequency;
				n = this._node == alt._node;
			}

			return tok && cat && wt && n;
		}
	}

}
