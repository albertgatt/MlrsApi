/**
 * 
 */
package mt.edu.um.mlrs.spell;

import java.util.Iterator;

import mt.edu.um.util.datastructures.trees.PostorderIterator;
import mt.edu.um.util.datastructures.trees.PreorderIterator;
import mt.edu.um.util.datastructures.trees.Tree;

class SpellState extends Tree<SpellState> implements
		Comparable<SpellState> {

	private Double _score;
	private char _c;
	private char _r;

	SpellState() {
		this._score = 0.0D;
	}

	SpellState(char c, char r) {
		this();
		this._c = c;
		this._r = r;
	}

	SpellState(char c) {
		this(c, c);
	}

	void setReplacement(char repl) {
		this._r = repl;
	}

	Double score() {
		return this._score;
	}

	void setScore(double score) {
		this._score = score;
	}

	void incrementScore(double inc) {
		this._score += inc;
	}

	char original() {
		return this._c;
	}

	char replacement() {
		return this._r;
	}		
	
	boolean insertBefore(SpellState s) {
		SpellState oldParent = this.parent;
		return this.parent.removeChild(this) && oldParent.addChild(s) && s.addChild(this); 
	}				

	public int compareTo(SpellState s) {
		return this.score().compareTo(s.score());
	}

	@Override
	public Iterator<SpellState> postorder() {
		return new PostorderIterator<SpellState>(this);
	}

	@Override
	public Iterator<SpellState> preorder() {
		return new PreorderIterator<SpellState>(this);
	}

}