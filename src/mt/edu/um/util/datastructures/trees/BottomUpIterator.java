package mt.edu.um.util.datastructures.trees;

import java.util.LinkedList;
import java.util.Queue;

public class BottomUpIterator<T extends Tree<T>> extends PreorderIterator<T> {
	Queue<T> q1, q2;

	public BottomUpIterator(T root) {
		super();
		this.root = root;
		this.q1 = new LinkedList<T>();
		this.q2 = new LinkedList<T>();
		unpack(root);

		for (T t : this.q1) {
			this.stack.push(t);
		}

		for (T t : this.q2) {
			this.stack.push(t);
		}
	}

	@Override
	public T next() {
		this.next = this.stack.pop();
		return this.next;
	}

	@Override
	protected void unpack(T root) {

		if (root.hasChildren()) {
			this.q1.offer(root);

			for (T child : root.getChildren()) {
				unpack(child);
			}
		} else {
			this.q2.offer(root);
		}
	}
}
