/**
 * 
 */
package mt.edu.um.util.datastructures.trees;

import java.util.Iterator;
import java.util.List;
import java.util.Stack;

public class PreorderIterator<T extends Tree<T>> implements Iterator<T> {
	protected T root;
	protected T next;
	protected Stack<T> stack;

	protected PreorderIterator() {
		this.stack = new Stack<T>();
	}

	public PreorderIterator(T tree) {
		this();
		this.root = tree;
		unpack(this.root);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#next()
	 */
	@Override
	public T next() {
		this.next = this.stack.pop();
		List<T> children = this.next.getChildren();

		for (int i = children.size() - 1; i >= 0; i--) {
			this.stack.push(children.get(i));
		}

		return this.next;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#remove()
	 */
	public void remove() {
		if (this.next.hasParent()) {
			this.next.getParent().removeChild(this.next);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext() {
		return !this.stack.isEmpty();
	}

	protected void unpack(T root) {
		this.stack.push(root);
	}

}