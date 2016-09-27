package mt.edu.um.mlrs.runnables;

import java.io.BufferedWriter;
import java.util.List;

import mt.edu.um.mlrs.lexicon.JSONLexicon;
import mt.edu.um.util.io.FileUtils;

public class WebServiceLemmatiser {

	static String url = "http://mlrs.research.um.edu.mt/resources/gabra/api/lemmatise.json?surface_form=";
	static String[] testWords = new String[] { "dan", "ktibnielhom",
			"gdimnihomlhom", "tajna", "sparajna", "raġel", "ttewweb", "mexa",
			"mascara", "ġrejt", "splodejna", "Ħrajtu", "rġiel" };
	static String wordlistFile = "A:\\mlrs\\lexicon\\gabra\\mlrs_wlist_cleaned.txt";
	static String outputFile = "A:\\mlrs\\lexicon\\gabra\\mlrs_wlist_lemmatised.txt";

	public static void main(String[] args) throws Exception {
		JSONLexicon lex = new JSONLexicon(url);
		List<String> words = FileUtils.readLinesFromFile(wordlistFile, "UTF-8");
		BufferedWriter writer = FileUtils
				.getBufferedWriter(outputFile, "UTF-8");

		for (String testWord : words) {
			String lemma = null;
			String root = null;

			try {
				lemma = lex.get(testWord, "lemma");
				root = lex.get(testWord, "radicals");				
				
			} catch (Exception e) {
				System.err.println(testWord + " " + e.getMessage());
			}

			writer.write(testWord + "|" + lemma + "|" + root + "\n");
			System.err.println(testWord + "\t" + lemma);
		}

		writer.close();
	}
}
