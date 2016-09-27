package mt.edu.um.mlrs.classify.lang;

import java.io.File;

import mt.edu.um.mlrs.classify.Classifier;
import mt.edu.um.mlrs.exception.ClassifierException;

import com.aliasi.classify.BaseClassifier;
import com.aliasi.classify.Classification;
import com.aliasi.util.AbstractExternalizable;

public class LanguageClassifier extends Classifier {

	private BaseClassifier<CharSequence> _classifier;
	private String _modelFile;

	public LanguageClassifier(String modelFile) {
		this._modelFile = modelFile;
		initModel();
		this.setTargetType("SENTENCE");
		this.setDestinationType("SENTENCE");
		this.setAttributeName("LANG");
	}

	public String getModelFile() {
		return this._modelFile;
	}

	public String classify(String seq) {
		Classification cl = this._classifier.classify(seq);
		return cl.bestCategory();
	}
	
	@SuppressWarnings("unchecked")
	private void initModel() {
		try {
			this._classifier = (BaseClassifier<CharSequence>) AbstractExternalizable
					.readObject(new File(this._modelFile));

		} catch (Exception e) {
			throw new ClassifierException("Exception loading model: ", e);
		}
	}
	
	
}
