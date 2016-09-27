package mt.edu.um.util.dict;

import java.io.Serializable;
import java.util.Arrays;

public class TrieNode implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5654923622060595257L;
	private int _endToken; // flag whether this is end of token
	private int _startToken; // flag whether this is a token start
	private int _numOccurrences;
	private TrieNode[] _children;
	private char[] _childChars;
	private TrieNode _parent;
	private char _char;
	private int _numInsertions;

	TrieNode() {
		this._children = new TrieNode[0];
		this._childChars = new char[0];
		this._endToken = 0;
		this._startToken = 0;
		this._numOccurrences = 1;
		this._numInsertions = 0;
	}
	
	public TrieNode(char c) {
		this();
		this._char = c;
	}

	public int tokenFrequency(String string) {
		int tokenFreq = 0;
		
		if (string.length() > 0) {
			char val = string.charAt(0);
			TrieNode node = getChild(val);

			if (node != null) {
				if (string.length() > 1) {
					tokenFreq += node.tokenFrequency(string.substring(1));
				
				} else {
					tokenFreq = node._endToken;
				}
			}
		}

		return tokenFreq;
	}
	
	public int prefixFrequency(String string) {
		int tokenFreq = 0;
		
		if (string.length() > 0) {
			char val = string.charAt(0);
			TrieNode node = getChild(val);

			if (node != null) {
				if (string.length() > 1) {
					tokenFreq += node.prefixFrequency(string.substring(1));
				
				} else {
					tokenFreq += node._numOccurrences;
				}
			}
		}

		return tokenFreq;
	}

	public TrieNode getChild(char c) {
		int index = Arrays.binarySearch(this._childChars, c);
		return index < 0 ? null : this._children[index];

	}

	public int numOccurrences() {
		return this._numOccurrences;
	}

	public boolean isStart() {
		return this._startToken > 0;
	}

	public int starts() {
		return this._startToken;
	}

	public boolean isEnd() {
		return this._endToken > 0;
	}

	public TrieNode[] children() {
		return this._children;
	}

	public char[] followingChars() {
		return this._childChars;
	}

	// int indexOf(TrieNode child) {
	// int ret = -1;
	//
	// for (int i = 0; i < this._children.length; i++) {
	// if (this._children[i] == child) {
	// ret = i;
	// break;
	// }
	// }
	//
	// return ret;
	// }

	public int numInsertions() {
		return this._numInsertions;
	}

	public void insert(CharSequence string) {
		this._numInsertions++;
		insert(string, true, 1);
	}
	
	public void insert(CharSequence string, int freq) {
		insert(string, true, freq);
	}

	private TrieNode getOrInsert(char c, int f) {
		TrieNode child = getChild(c);

		if (child != null) {
			child._numOccurrences+=f;
			return child;
		}

		TrieNode result = new TrieNode(c);
		char[] oldChars = this._childChars;
		TrieNode[] oldChildren = this._children;
		this._childChars = new char[oldChars.length + 1];
		this._children = new TrieNode[oldChildren.length + 1];
		int i = 0;

		for (; i < oldChars.length; ++i) {
			if (oldChars[i] > c)
				break;
			this._childChars[i] = oldChars[i];
			this._children[i] = oldChildren[i];
		}

		this._childChars[i] = c;
		this._children[i] = result;

		for (; i < oldChars.length; ++i) {
			this._childChars[i + 1] = oldChars[i];
			this._children[i + 1] = oldChildren[i];
		}

		result._numOccurrences = f;
		result._parent = this;
		return result;

	}

	private void insert(CharSequence string, boolean start, int freq) {
		char c = string.charAt(0);
		TrieNode n = getOrInsert(c, freq);

		if (start) {
			n._startToken+=freq;
		}

		// if word length > 1, then word is not finished being added.
		// otherwise, set the flag to true so we know a word ends there.
		if (string.length() > 1) {			
			n.insert(string.subSequence(1, string.length()), false, freq);
			
		} else {
			n._endToken+=freq;
		}
	}

	public boolean contains(CharSequence string) {
		boolean contains = false;

		if (string.length() > 0) {
			char val = string.charAt(0);
			TrieNode node = getChild(val);

			if (node != null) {
				if (string.length() > 1) {
					contains = node.contains(string.subSequence(1, string.length()));
				} else {
					contains = true;
				}
			}
		}

		return contains;
	}

	public boolean isToken(CharSequence string) {
		boolean contains = false;

		if (string.length() > 0) {
			char val = string.charAt(0);
			TrieNode node = getChild(val);

			if (node != null) {
				if (string.length() > 1) {
					contains = node.isToken(string.subSequence(1, string.length()));
				} else {
					contains = node._endToken > 0;
				}
			}
		}

		return contains;
	}

	public char getChar() {
		return this._char;
	}

	public boolean hasParent() {
		return this._parent != null;
	}

	public TrieNode getParent() {
		return this._parent;
	}

	// public void removeChild(TrieNode child) {
	// int index = this.indexOf(child);
	//
	// if (index >= 0 && index < this._children.length) {
	// this._children[index] = null;
	// }
	// }

	public String toString() {
		return new StringBuffer().append(getChar()).toString();
	}

	public String prettyString() {
		return printTree(this, 0, new StringBuffer());
	}

	private String printTree(TrieNode trie, int indent, StringBuffer sb) {

		for (int i = 0; i < indent; ++i) {
			sb.append("+");
		}

		sb.append(trie.getChar()).append("\n");

		for (TrieNode child : trie._children) {
			if (child != null) {
				printTree(child, indent + 1, sb);
			}
		}

		return sb.toString();
	}

}
