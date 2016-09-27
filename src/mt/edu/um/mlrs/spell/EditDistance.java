package mt.edu.um.mlrs.spell;

public class EditDistance {
	private double _match, _delete, _insert, _substitute, _transpose;

	public EditDistance() {
		this(0.0, 1.0, 1.0, 2.0, 2.0);
	}
	
	public EditDistance(double match, double delete, double insert, double substitute, double transpose) {
		setMatchWeight(match);
		setDeleteWeight(delete);		
		setInsertWeight(insert);
		setSubstituteWeight(substitute);
		setTransposeWeight(transpose);
	}

	public void setInsertWeight(double insert) {
		this._insert = insert;
	}
	
	public void setMatchWeight(double match) {
		this._match = match;
	}

	public void setDeleteWeight(double delete) {
		this._delete = delete;
	}

	public void setSubstituteWeight(double substitute) {
		this._substitute = substitute;
	}

	public void setTransposeWeight(double transpose) {
		this._transpose = transpose;
	}
	
	public double getMatchWeight() {
		return _match;
	}

	public double getDeleteWeight() {
		return _delete;
	}

	public double getInsertWeight() {
		return _insert;
	}

	public double getSubstituteWeight() {
		return _substitute;
	}

	public double getTransposeWeight() {
		return _transpose;
	}

	public double distance(CharSequence csIn, CharSequence csOut,
			boolean similarity) {

		if (csOut.length() == 0) { // all deletes
			double sum = 0.0;

			for (int i = 0; i < csIn.length(); ++i) {
				sum += this._delete;
			}

			return sum;
		}

		if (csIn.length() == 0) { // all inserts
			double sum = 0.0;
			for (int j = 0; j < csOut.length(); ++j)
				sum += this._insert;
			return sum;
		}

		int xsLength = csIn.length() + 1; // >= 2
		int ysLength = csOut.length() + 1; // >= 2

		// x=0: first slice, all inserts
		double lastSlice[] = new double[ysLength];
		lastSlice[0] = 0.0; // upper left corner of lattice
		for (int y = 1; y < ysLength; ++y)
			lastSlice[y] = lastSlice[y - 1] + this._insert;

		// x=1: second slice, no transpose
		double[] currentSlice = new double[ysLength];
		currentSlice[0] = this._insert;
		char cX = csIn.charAt(0);

		for (int y = 1; y < ysLength; ++y) {
			int yMinus1 = y - 1;
			char cY = csOut.charAt(yMinus1);
			double matchSubstWeight = lastSlice[yMinus1]
					+ ((cX == cY) ? this._match : this._substitute);
			double deleteWeight = lastSlice[y] + this._delete;
			double insertWeight = currentSlice[yMinus1] + this._insert;

			currentSlice[y] = bestScore(similarity, matchSubstWeight,
					deleteWeight, insertWeight);
		}

		// avoid third array allocation if possible
		if (xsLength == 2)
			return currentSlice[currentSlice.length - 1];

		char cYZero = csOut.charAt(0);
		double[] twoLastSlice = new double[ysLength];

		// x>1:transpose after first element
		for (int x = 2; x < xsLength; ++x) {
			char cXMinus1 = cX;
			cX = csIn.charAt(x - 1);

			// rotate slices
			double[] tmpSlice = twoLastSlice;
			twoLastSlice = lastSlice;
			lastSlice = currentSlice;
			currentSlice = tmpSlice;

			currentSlice[0] = lastSlice[0] + this._delete;

			// y=1: no transpose here
			currentSlice[1] = bestScore(similarity,
					(cX == cYZero) ? (lastSlice[0] + this._match)
							: (lastSlice[0] + this._substitute), lastSlice[1]
							+ this._delete, currentSlice[0] + this._insert);

			// y > 1: transpose
			char cY = cYZero;

			for (int y = 2; y < ysLength; ++y) {
				int yMinus1 = y - 1;
				char cYMinus1 = cY;
				cY = csOut.charAt(yMinus1);
				currentSlice[y] = bestScore(similarity,
						(cX == cY) ? (lastSlice[yMinus1] + this._match)
								: (lastSlice[yMinus1] + this._substitute),
						lastSlice[y] + this._delete, currentSlice[yMinus1]
								+ this._insert);

				if (cX == cYMinus1 && cY == cXMinus1) {
					currentSlice[y] = bestScore(similarity, currentSlice[y],
							twoLastSlice[y - 2] + this._transpose);
				}
			}
		}
		
		return currentSlice[currentSlice.length - 1];
	}

	private double bestScore(boolean similarity, double x, double y) {
		return (similarity ? Math.max(x, y) : Math.min(x, y));
	}

	private double bestScore(boolean isSimilarity, double x, double y, double z) {
		return bestScore(isSimilarity, x, bestScore(isSimilarity, y, z));
	}

}
