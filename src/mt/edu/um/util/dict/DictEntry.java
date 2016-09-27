package mt.edu.um.util.dict;

import java.util.Comparator;

import com.aliasi.util.BoundedPriorityQueue;

public class DictEntry implements Comparable<DictEntry> {
	public static final Comparator<DictEntry> LOWEST_WEIGHT_COMPARATOR = new Comparator<DictEntry>() {
		public int compare(DictEntry t1, DictEntry t2) {
			return -t1._weight.compareTo(t2._weight);
		}
	};
	
	public static final Comparator<DictEntry> HIGHEST_WEIGHT_COMPARATOR = new Comparator<DictEntry>() {
		public int compare(DictEntry t1, DictEntry t2) {
			return t1._weight.compareTo(t2._weight);
		}
	};
	
	protected CharSequence _label;
	protected CharSequence _token;
	protected Double _weight;
	protected int _frequency;

	public DictEntry(CharSequence token) {
		this(token, token, 0.0D);
	}
	

	public DictEntry(CharSequence token, CharSequence category) {
		this(token, category, 0.0D);
		this._label = category;
	}

	public DictEntry(CharSequence token, CharSequence category, double weight) {
		this._token = token;
		this._label = category;
		this._weight = weight;
		this._frequency = 0;
	}
	
	public DictEntry(CharSequence token, CharSequence category, double weight, int frequency) {
		this._token = token;
		this._label = category;
		this._weight = weight;
		this._frequency = frequency;
	}

	public int compareTo(DictEntry sr) {
		return this._weight.compareTo(sr._weight);
	}

	public CharSequence getToken() {
		return this._token;
	}

	public CharSequence getLabel() {
		return this._label;
	}

	public double getWeight() {
		return this._weight;
	}

	public void incrementWeight(double incr) {
		this._weight += incr;
	}

	public void incrementFrequency(int freq) {
		this._frequency += freq;
	}

	public int getFrequency() {
		return this._frequency;
	}

	public boolean equals(Object o) {
		boolean tok = false;
		boolean cat = false;
		boolean wt = false;

		if (o instanceof DictEntry) {
			DictEntry alt = (DictEntry) o;
			cat = this._label == alt._label
					|| this._label.equals(this._label);
			tok = this._token == alt._token || this._token.equals(this._token);
			wt = this._weight == alt._weight
					&& this._frequency == alt._frequency;
		}

		return tok && cat && wt;
	}

	public String toString() {
		return new StringBuffer(this._token).append("/").append(this._label)
				.append("/").append(this._weight).toString();
	}
}
