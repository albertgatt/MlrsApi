package mt.edu.um.mlrs.spell;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.regex.Pattern;

import mt.edu.um.mlrs.split.MTRegex;
import mt.edu.um.util.io.FileUtils;

import com.aliasi.lm.NGramProcessLM;
import com.aliasi.spell.FixedWeightEditDistance;
import com.aliasi.spell.TrainSpellChecker;
import com.aliasi.tokenizer.RegExTokenizerFactory;
import com.aliasi.tokenizer.TokenizerFactory;
import com.ibm.icu.text.Normalizer;
import com.ibm.icu.text.Normalizer2;

public class SpellModelBuilder {
	public static final double MATCH = -0.0D;
	public static final double INSERT = -3.0D;
	public static final double DELETE = -2.0D;
	public static final double SUBSTITUTE = -1.0D;
	public static final double TRANSPOSE = -1.0D;
	public static final int NGRAM = 5;

	private int _ngram;
	private double _match, _insert, _delete, _substitute, _transpose;
	private TrainSpellChecker _trainSpellChecker;

	public static void main(String[] args) throws Exception {
		String input = args[0];
		String dict = args[1];
		String charset = args[2];
		String outFile = args[3];
		int pruneFactor = Integer.parseInt(args[4]);
		SpellModelBuilder builder;

		if (args.length > 5) {
			builder = new SpellModelBuilder(Double.parseDouble(args[4]), Double
					.parseDouble(args[5]), Double.parseDouble(args[6]), Double
					.parseDouble(args[7]), Double.parseDouble(args[8]));
		} else {
			builder = new SpellModelBuilder();
		}

		 InputStreamReader reader = FileUtils.getReader(input, charset);
		 BufferedReader breader = new BufferedReader(reader);
		int counter = 0;

		while (breader.ready()) {
			counter++;
			String line = breader.readLine();
			String normalised = Normalizer.normalize(line, Normalizer.NFC);
			builder.train(normalised);
		}

		breader.close();
		System.err.println(counter + " sentences");
		counter = 0;

		List<String> words = FileUtils.readLinesFromFile(dict, "UTF-8");

		for (String word : words) {
			counter++;
			String norm = Normalizer.normalize(word, Normalizer.NFC);
			builder.train(norm);
		}

		System.err.println(counter + " words");
		builder.prune(pruneFactor);
		builder.outputModel(outFile);
	}

	public SpellModelBuilder() {
		this._match = MATCH;
		this._insert = INSERT;
		this._transpose = TRANSPOSE;
		this._substitute = SUBSTITUTE;
		this._delete = DELETE;
		init();
	}

	public SpellModelBuilder(double match, double insert, double delete,
			double transpose, double substitute) {
		this._match = match;
		this._insert = insert;
		this._transpose = transpose;
		this._substitute = substitute;
		this._delete = delete;
		init();
	}

	public void prune(int minFreq) {
		this._trainSpellChecker.pruneLM(minFreq);
	}

	public void train(String text) {
		this._trainSpellChecker.handle(text);
	}

	public void train(String text, int count) {
		this._trainSpellChecker.train(text, count);
	}

	public void outputModel(String outputFileName) throws IOException {
		FileOutputStream fileOut = new FileOutputStream(outputFileName);
		BufferedOutputStream bufOut = new BufferedOutputStream(fileOut);
		ObjectOutputStream objOut = new ObjectOutputStream(bufOut);

		// write the spell checker to the file
		this._trainSpellChecker.compileTo(objOut);
		objOut.close();
		bufOut.close();
		fileOut.close();
	}

	public void init() {
		FixedWeightEditDistance fixedEdit = new FixedWeightEditDistance(
				this._match, this._delete, this._insert, this._substitute,
				this._transpose);

		NGramProcessLM lm = new NGramProcessLM(this._ngram);
//		TokenizerFactory tokenizerFactory = new RegExTokenizerFactory(
//				MTRegex.TOKEN, Pattern.CASE_INSENSITIVE);
		this._trainSpellChecker = new TrainSpellChecker(lm, fixedEdit);
				//tokenizerFactory);
	}
}
