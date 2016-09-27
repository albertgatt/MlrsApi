package mt.edu.um.util.datastructures.trees;

import java.util.ArrayList;
import java.util.List;

public abstract class LabelledTree<O, T extends LabelledTree<O, T>> extends
		Tree<T> {

	protected O _label;

	public LabelledTree() {
		super();
	}
	
	public boolean addChild(O label, T child) {
		child._label = label;
		return super.addChild(child);
	}
	
	public O getLabel() {
		return this._label;
	}
	
	public void setLabel(O label) {
		this._label = label;
	}

	public List<T> getChildren(O label, boolean transitive) {
		List<T> children = new ArrayList<T>();

		for (T child : this.children) {
			if (child._label != null && child._label.equals(label)) {
				children.add(child);
			}

			if (transitive) {
				children.addAll(child.getChildren(label, transitive));
			}
		}

		return children;
	}

	public boolean removeChildren(O label) {
		List<T> kids = getChildren(label, false);
		boolean success = !kids.isEmpty();

		for (T child : kids) {
			super.removeChild(child);
		}

		return success;
	}

	public boolean hasChild(O label) {
		return !getChildren(label, false).isEmpty();
	}

	public List<O> getChildRelations() {
		List<O> labels = new ArrayList<O>();

		for (T child : this.children) {
			labels.add(child._label);
		}

		return labels;
	}

}
