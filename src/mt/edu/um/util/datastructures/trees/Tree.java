package mt.edu.um.util.datastructures.trees;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class Tree<T extends Tree<T>> {

	protected List<T> children;
	protected T parent;
	protected String _id;

	public Tree() {
		this.children = new ArrayList<T>();
	}

	public abstract Iterator<T> preorder();

	public abstract Iterator<T> postorder();

	public List<T> preorderList() {
		Iterator<T> preorder = preorder();
		List<T> list = new ArrayList<T>();

		while (preorder.hasNext()) {
			list.add(preorder.next());
		}

		return list;
	}

	public int childIndex(T child) {
		return this.children.indexOf(child);
	}

	public List<T> postorderList() {
		Iterator<T> postorder = postorder();
		List<T> list = new ArrayList<T>();

		while (postorder.hasNext()) {
			list.add(postorder.next());
		}

		return list;
	}

	public void setID(String id) {
		this._id = id;
	}

	public boolean hasID() {
		return this._id != null;
	}

	public String getID() {
		return this._id;
	}

	public List<T> getChildren() {
		return this.children;
	}

	public boolean hasChildren() {
		return !this.children.isEmpty();
	}

	public int numChildren(boolean transitive) {
		int num = 0;

		if (transitive) {
			for (Tree<T> child : this.children) {
				num += child.numChildren(transitive);
			}
		} else {
			num = this.children.size();
		}

		return num;
	}

	public T getChild(String id, boolean transitive) {
		T child = null;
		Iterator<T> iter = transitive ? preorder() : this.children.iterator();

		while (iter.hasNext() && child == null) {
			T next = iter.next();

			if (next.hasID() && next.getID().equals(id)) {
				child = next;
			}
		}

		return child;
	}

	@SuppressWarnings("unchecked")
	public boolean addChild(T child) {
		child.parent = (T) this;
		return this.children.add(child);
	}

	public T leastCommonParent(Tree<T> tree) {
		T lcp = null;

		if (this.isSibling(tree) || this.isAncestor(tree)) {
			lcp = this.parent;
		} else if (tree.isAncestor(this)) {
			lcp = tree.parent;
		} else {
			lcp = this.parent.leastCommonParent(tree);

			if (lcp == null) {
				lcp = tree.parent.leastCommonParent(this);
			}
		}

		return lcp;
	}

	@SuppressWarnings("unchecked")
	public boolean addChild(int index, T child) {
		boolean success = false;

		if (index > -1 && (index < this.children.size() || index == 0)) {
			child.parent = (T) this;
			this.children.add(index, child);
			success = true;
		} else if (index == this.children.size()) {
			this.children.add(child);
			success = true;
		}

		return success;
	}

	public void clearChildren() {
		for (T c : this.children) {
			c.parent = null;
			c.clearChildren();
		}

		this.children.clear();
	}

	public boolean isSibling(Tree<T> t) {
		return t.parent == this.parent;
	}

	public boolean isAncestor(Tree<T> t) {
		boolean ancestor = false;
		Tree<T> currTree = this;

		while (!ancestor && currTree != null) {
			ancestor = (t == currTree.getParent());
		}

		return ancestor;
	}

	public boolean isParent(T t) {
		return t == this.parent;
	}

	public List<T> getSiblings() {
		List<T> siblings = new ArrayList<T>();

		if (this.hasParent()) {
			siblings.addAll(this.parent.getChildren());
			siblings.remove(this);
		}

		return siblings;
	}

	public List<T> getLeftSiblings() {
		List<T> siblings = new ArrayList<T>();

		if (hasParent()) {
			int index = this.parent.children.indexOf(this);
			siblings.addAll(this.parent.children.subList(0, index));
		}

		return siblings;
	}

	public T getLeftSibling() {
		T sibling = null;

		if (hasParent()) {
			int index = this.parent.children.indexOf(this);

			if (index > 0) {
				sibling = this.parent.children.get(index - 1);
			}

		}

		return sibling;
	}

	public T getRightSibling() {
		T sibling = null;

		if (hasParent()) {
			int index = this.parent.children.indexOf(this);

			if (index < this.parent.children.size() - 2) {
				sibling = this.parent.children.get(index + 1);
			}

		}

		return sibling;
	}

	public List<T> getSiblings(int offset) {
		List<T> siblings = new ArrayList<T>();
		int index = this.parent.children.indexOf(this);

		if (hasParent()) {
			int start = -1;
			int end = -1;

			if (offset < 0) {
				start = index - Math.abs(offset);
				end = index;
			} else {
				start = index + 1;
				end = (this.parent.children.size() - index) + offset;
			}

			if (start >= 0 && end <= this.parent.children.size()) {
				siblings.addAll(this.parent.children.subList(start, end));
			}
		}

		return siblings;
	}

	public List<T> getRightSiblings() {
		List<T> siblings = new ArrayList<T>();

		if (hasParent()) {
			int index = this.parent.children.indexOf(this);
			siblings.addAll(this.parent.children.subList(index + 1,
					this.parent.children.size()));
		}

		return siblings;
	}

	public boolean hasParent() {
		return this.parent != null;
	}

	public T getParent() {
		return this.parent;
	}

	public List<T> getAncestors() {
		List<T> ancestors = new ArrayList<T>();

		if (hasParent()) {
			ancestors.add(parent);
			ancestors.addAll(0, parent.getAncestors());
		}

		return ancestors;
	}

	public boolean removeChild(T child) {
		child.parent = null;
		return this.children.remove(child);
	}

	@SuppressWarnings("unchecked")
	public boolean replaceChild(T oldChild, T newChild) {
		int index = this.children.indexOf(oldChild);
		boolean success = false;

		if (index > -1) {
			newChild.parent = (T) this;
			this.children.remove(index);
			this.children.add(index, newChild);
			success = true;
		}

		return success;
	}

	public boolean hasChild(T child) {
		return this.children.contains(child);
	}

	public void printTree() {
		recursiveToString("+");
	}

	protected void recursiveToString(String sep) {
		System.out.println(sep + toString());

		for (T child : getChildren()) {
			child.recursiveToString(sep + "+");
		}
	}

}
