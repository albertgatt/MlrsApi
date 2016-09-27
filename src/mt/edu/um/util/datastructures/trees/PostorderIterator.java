package mt.edu.um.util.datastructures.trees;

public class PostorderIterator<T extends Tree<T>> extends PreorderIterator<T> {

	public PostorderIterator(T root) {
		super(root);
	}

	@Override
	public T next() {
		this.next = this.stack.pop();
		return this.next;
	}

	@Override
	protected void unpack(T root) {
		this.stack.push(root);

		for (T child : root.getChildren()) {
			unpack(child);
		}
	}

}
