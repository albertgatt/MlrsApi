package mt.edu.um.mlrs.classify.tag;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import mt.edu.um.mlrs.exception.TaggerException;
import mt.edu.um.mlrs.split.Tokeniser;
import mt.edu.um.mlrs.text.TextNode;
import mt.edu.um.rules.Rule;
import mt.edu.um.rules.provide.Provider;
import mt.edu.um.util.io.FileFinder;

import com.aliasi.hmm.HiddenMarkovModel;
import com.aliasi.hmm.HmmCharLmEstimator;
import com.aliasi.hmm.HmmDecoder;
import com.aliasi.tag.Tagging;
import com.aliasi.util.Compilable;


public class HmmTagger extends Tagger {
	public static int N_GRAM = 12;
	public static int NUM_CHARS = 300;
	public static double LAMBDA_FACTOR = 8.0;
	public static String DEFAULT_SEPARATOR = "_";

	private HiddenMarkovModel _hmm;
	private HmmDecoder _decoder;
	private String _model;	
	private Tokeniser _tokeniser;
	private List<Rule<TextNode, TextNode>> _rules;
	private boolean _retrainOnRule;
	private int _ngram, _numchars;
	private double _lambda;
	private String _separator;

	public static HmmTagger trainTagger(String inputDirectory) {
		HmmTagger tagger = new HmmTagger();
		tagger.trainFromText(new MLRSParser(), inputDirectory);
		return tagger;
	}

	public static void trainTagger(String inputDirectory, String outputFile) {
		HmmTagger tagger = HmmTagger.trainTagger(inputDirectory);
		tagger.writeModel(outputFile);
	}

	HmmTagger() {
		super();
		this._rules = new ArrayList<Rule<TextNode, TextNode>>();		
		setTargetType("SENTENCE");
		setDestinationType("TOKEN");		
		setNgram(HmmTagger.N_GRAM);
		setNumChars(HmmTagger.NUM_CHARS);
		setLambda(HmmTagger.LAMBDA_FACTOR);
		setSeparator(HmmTagger.DEFAULT_SEPARATOR);
	}

	public HmmTagger(String modelFile, Tokeniser tokeniser) {
		this();
		this._model = modelFile;
		initDecoder();
		this._tokeniser = tokeniser;
	}

	public HmmTagger(String modelFile, Tokeniser tokeniser, TagDictionary dictionary) {
		this(modelFile, tokeniser);
		setDictionary(dictionary);
	}

	public int getNgram() {
		return _ngram;
	}

	public void setNgram(int _ngram) {
		this._ngram = _ngram;
	}

	public int getNumChars() {
		return _numchars;
	}

	public void setNumChars(int _numchars) {
		this._numchars = _numchars;
	}

	public double getLambda() {
		return _lambda;
	}

	public void setLambda(double _lambda) {
		this._lambda = _lambda;
	}

	public String getSeparator() {
		return _separator;
	}

	public void setSeparator(String _separator) {
		this._separator = _separator;
	}

	public void trainFromText(MLRSParser parser, String inputDirectory) {
		HmmCharLmEstimator estimator = new HmmCharLmEstimator(this._ngram, this._numchars,
				this._lambda);
		parser.setHandler(estimator);

		// read in files
		List<File> files = FileFinder
				.findFiles(new File(inputDirectory), "txt");

		// train it
		for (File f : files) {
			try {
				parser.parse(f);
			} catch (Exception e) {
				throw new TaggerException("Error while training on file "
						+ f.getName() + ": " + e.getMessage(), e);
			}
		}
		
		this._hmm = estimator;
		setDictionary(parser.getDictionary());
	}			
		

	public HiddenMarkovModel getHmm() {
		return this._decoder.getHmm();
	}

	@Override
	public void accept(Provider<Rule<TextNode, TextNode>> provider) {
		provider.populate(this);
	}

	public void writeModel(String outputFile) {
		try {
			FileOutputStream fileOut = new FileOutputStream(
					new File(outputFile));
			ObjectOutputStream objOut = new ObjectOutputStream(fileOut);				
			
			if(this._hmm instanceof Compilable) {
				((Compilable) this._hmm).compileTo(objOut);
			} else {
				throw new TaggerException("Cannot write model: hmm does not instantiate Compilable interface");
			}
			
			objOut.close();
		} catch (FileNotFoundException fnfe) {
			throw new TaggerException("No such file: " + outputFile, fnfe);
		} catch (IOException ioe) {
			throw new TaggerException("Error compiling model to file.", ioe);
		}
	}

	@Override
	public void add(Rule<TextNode, TextNode> rule) {
		this._rules.add(rule);
		Collections.sort(this._rules);
	}

	
//	public List<String> processToStrings(String text) throws TaggerException {
//		List<String> tokens = this._tokeniser.split(text);
//		Tagging<String> tags = this._decoder.tag(tokens);
//		return tags.tags();
//	}
//
//	@Override
//	public List<TextNode> process(String text) throws TaggerException {
//		List<TextNode> results = new ArrayList<TextNode>();
//		List<String> tokens = this._tokeniser.split(text);
//		Tagging<String> tags = this._decoder.tag(tokens);
//
//		for (int i = 0; i < tags.size(); i++) {
//			TextNode word = new TextNode(this._destinationType, tags.token(i));
//			word.setValue(this._attName, tags.tag(i));
//			word = runRules(word);
//			results.add(word);
//		}
//
//		return results;
//	}
	
	public String classify(String text) {
		List<String> tokens = this._tokeniser.split(text);
		List<String> tags = this._decoder.tag(tokens).tags();
		StringBuffer result = new StringBuffer();
		
		for(int i = 0; i < tokens.size(); i++) {
			result.append(tokens.get(i)).append(this._separator).append(tags.get(i)).append(" ");
		}
		
		return result.toString();		
	}

	public void setModelFile(String modelFile) {
		this._model = modelFile;
	}

	public String getModelFile() {
		return this._model;
	}
	
	public void trainState(String tag, String token) {
		HiddenMarkovModel model = getHmm();
		
		if(model instanceof HmmCharLmEstimator) {
			((HmmCharLmEstimator) model).trainEmit(tag,token);
		}
	}

	private void initDecoder() {
		try {
			FileInputStream fileIn = new FileInputStream(this._model);
			ObjectInputStream objIn = new ObjectInputStream(fileIn);			
			this._decoder = new HmmDecoder((HiddenMarkovModel) objIn.readObject());
			objIn.close();
		} catch (Exception e) {
			throw new TaggerException(
					"Could not build decoder from supplied model file.", e);
		}
	}

	private TextNode runRules(TextNode token) {
		boolean done = false;
		Iterator<Rule<TextNode, TextNode>> iter = this._rules.iterator();

		while (iter.hasNext() && !done) {
			Rule<TextNode, TextNode> nextRule = iter.next();

			if (nextRule.appliesTo(token)) {
				token = nextRule.apply(token);

				if (this._retrainOnRule) {
					trainState(token.getValue(this._attName),
							token.getContent());
				}

				done = true;
			}
		}

		return token;
	}

}
