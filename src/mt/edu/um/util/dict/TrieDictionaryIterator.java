/**
 * 
 */
package mt.edu.um.util.dict;

import java.util.Iterator;
import java.util.LinkedList;

class TrieDictionaryIterator implements Iterator<DictEntry> {
	private TrieNode _root;
	private StringBuffer _nextEntry;
	private LinkedList<TrieNode> _nodeQueue;

	TrieDictionaryIterator(TrieNode root) {
		this._root = root;
		this._nodeQueue = new LinkedList<TrieNode>();
		unpackToEndToken(this._root);
		buffer();
	}

	@Override
	public boolean hasNext() {
		return this._nextEntry != null;
	}

	@Override
	public DictEntry next() {
		DictEntry ret = this._nextEntry == null ? null : new DictEntry(this._nextEntry.toString());
		this._nextEntry = null;		
		buffer();
		return ret;
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub

	}
	
	private void buffer() {
		if (!this._nodeQueue.isEmpty()) {
			this._nextEntry = new StringBuffer();
			bufferString(this._nodeQueue.poll());
		}
	}
	
	private void unpackToEndToken(TrieNode node) {

		for (TrieNode child : node.children()) {
			if (child != null) {

				if (child.isEnd()) {
					this._nodeQueue.offer(child);
				}

				unpackToEndToken(child);
			}
		}
	}

	private void bufferString(TrieNode next) {
		char c;
		c = next.getChar();					
		this._nextEntry.insert(0, c);
		next = next.getParent();

		if (next != this._root) {
			bufferString(next);
		}
	}

}