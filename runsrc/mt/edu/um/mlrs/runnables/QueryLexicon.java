package mt.edu.um.mlrs.runnables;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mt.edu.um.mlrs.exception.TextProcessorException;
import mt.edu.um.mlrs.lexicon.JSONLexicon;
import mt.edu.um.util.io.FileUtils;

public class QueryLexicon {

	static String DIR = "A:\\morphology\\rsvp\\rootdata\\maltese.tri.csv";
	static String ERR = "A:\\morphology\\rsvp\\rootdata\\maltese.error.csv";
	static String LIST = "A:\\morphology\\rsvp\\rsvp_2014\\roots.txt";
	static String NOUNS = "A:\\morphology\\rsvp\\rsvp_2014\\nouns.txt";
	static String url = "http://mlrs.research.um.edu.mt/resources/gabra/ws/lemmatise.json?surface_form=";
	static String radUrl = "http://mlrs.research.um.edu.mt/resources/gabra/ws/root.json?radicals=";
	static String[] consonants = new String[] { "b", "ċ", "d", "f", "g", "ġ",
			"ħ", "h", "j", "k", "l", "m", "n", "p", "q", "r", "s", "t", "v",
			"w", "x", "z", "ż", "għ", "'" };
	static String[] consonants2 = new String[] { "k", "l", "m", "n", "p", "q",
			"r", "s", "t", "v", "w", "x", "z", "ż", "għ", "'" };
	private JSONLexicon lexicon;

	public QueryLexicon() {
		lexicon = new JSONLexicon(url, radUrl);
	}

	public Map<Integer, List<String>> getWordsByRoot(String root, String pos,
			int nRadicals) {
		return lexicon.getRootWords(root, pos, nRadicals);
	}

	public static void main(String[] args) throws Exception {
		// countFormsForRSVP();
		getNounsForRSVP();
	}

	public static void getNounsForRSVP() throws Exception {
		List<String> roots = FileUtils.readLinesFromFile(LIST, "utf-8");
		BufferedWriter writer = FileUtils.getBufferedWriter(NOUNS, "utf-8");
		QueryLexicon lex = new QueryLexicon();

		for (String r : roots) {
			System.err.println("Querying: " + r);
			Map<Integer, List<String>> map = lex.getWordsByRoot(r, "", 3);

			if (map != null) {

				for (int i : map.keySet()) {
					writer.write(r + "\t" + i + "\t" + map.get(i) + "\n");
				}
			}

		}

		writer.close();
	}

	public static void countFormsForRSVP() throws Exception {
		QueryLexicon lex = new QueryLexicon();
		List<String> errors = new ArrayList<String>();
		BufferedWriter writer = FileUtils.getBufferedWriter(DIR, "UTF-8");
		BufferedWriter error = FileUtils.getBufferedWriter(ERR, "UTF-8");
		int count = 0;

		for (String c1 : consonants) {
			for (String c2 : consonants) {
				for (String c3 : consonants) {
					String root = c1 + "-" + c2 + "-" + c3;
					System.err.println("Querying: " + root);

					try {
						Map<Integer, List<String>> words = lex.getWordsByRoot(
								root, "Verb", 3);
						count++;

						if (words != null) {

							for (Integer i : words.keySet()) {
								writer.write(root + "\t" + i);
								List<String> forms = words.get(i);
								writer.write("\t" + forms.size());

								for (String w : words.get(i)) {
									writer.write("\t" + w);
								}

								writer.write("\n");
							}
						}
					} catch (TextProcessorException e) {
						errors.add(root);
						System.err.println("\t Error: sleeping");
						Thread.sleep(3000);
					}
				}
			}
		}

		writer.close();

		for (String r : errors) {
			error.write(r + "\n");
		}

		error.close();

	}

}
