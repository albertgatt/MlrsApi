package mt.edu.um.util.datastructures.trees;

public class ScoredNode<E> extends Node<E> implements Comparable<ScoredNode<E>> {

	protected Double _score;

	public ScoredNode(E data) {
		this(data, 0.0D);
	}

	public ScoredNode(E data, double score) {
		super(data);
	}

	public double score() {
		return this._score;
	}

	public void incrementScore(double incr) {
		this._score += incr;
	}

	public int compareTo(ScoredNode<E> node) {
		return this._score.compareTo(node._score);
	}
}
