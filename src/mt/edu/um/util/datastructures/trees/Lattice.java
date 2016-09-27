package mt.edu.um.util.datastructures.trees;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Lattice<E> extends ScoredNode<E> {

	private Map<E, Lattice<E>> _children;
	private Map<E, Lattice<E>> _parents;

	public Lattice(E data) {
		this(data, 0.0D);
	}

	public Lattice(E data, double score) {
		super(data, score);
		this._children = new TreeMap<E, Lattice<E>>();
		this._parents = new TreeMap<E, Lattice<E>>();
	}

	public void addChild(Lattice<E> child) {
		this._children.put(child._data, child);
		child._children.put(this._data, this);
	}

	public Collection<Lattice<E>> getChildren() {
		return this._children.values();
	}

	public Collection<Lattice<E>> getParents() {
		return this._parents.values();
	}
	
	public Lattice<E> getChild(E data) {
		return this._children.get(data);
	}
	
	public Lattice<E> getParent(E data) {
		return this._parents.get(data);
	}
	
	public double cumulativeScore() {
		double cScore = this._score;
		
		for(Lattice<E> parent: this._parents.values()) {
			cScore += parent.cumulativeScore();
		}
		
		return cScore;
	}
	

	@SuppressWarnings("unused")
	private void addToMap(Map<E, List<Lattice<E>>> map, Lattice<E> lattice) {
		if (map.containsKey(lattice._data)) {
			map.get(lattice._data).add(lattice);
		} else {
			List<Lattice<E>> list = new ArrayList<Lattice<E>>();
			list.add(lattice);
			map.put(lattice._data, list);
		}
	}

}
