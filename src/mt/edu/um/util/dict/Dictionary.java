package mt.edu.um.util.dict;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;

import mt.edu.um.mlrs.spell.EditDistance;

public abstract class Dictionary implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4870263473161004676L;
	protected char[] _chars;
	protected int _minLength, _maxLength;

	public Dictionary() {
		this._chars = new char[0];
		this._minLength = 0;
		this._maxLength = 0;
	}

	public int numChars() {
		return this._chars.length;
	}

	public char[] getChars() {
		return this._chars;
	}
	
	protected void updateChars(CharSequence token) {
		// check characters
		//char[] chars = token.toCharArray();

		for (int i = 0; i < token.length(); i++) {
			char c = token.charAt(i);
			
			if (Arrays.binarySearch(this._chars, c) < 0) {
				int prevLength = this._chars.length;
				this._chars = Arrays.copyOf(this._chars, prevLength + 1);
				this._chars[prevLength] = c;
			}
		}
		
		int length = token.length();
		
		if(length < this._minLength) {
			this._minLength = length;
		}
		
		if(length > this._maxLength) {
			this._maxLength = length;
		}
	}
	
	public int maxLength() {
		return this._maxLength;
	}
	
	public int minLength() {
		return this._minLength;
	}

	public abstract void insert(CharSequence string);

	public abstract boolean contains(CharSequence string);

	public abstract Collection<DictEntry> entries();

	public abstract Collection<CharSequence> tokens();

	public abstract Iterator<DictEntry> iterator();

	public abstract void clear();

	public abstract int numEntries();

	public abstract Queue<DictEntry> nBestMatches(String token, EditDistance edit,
			double max, int n);
}
