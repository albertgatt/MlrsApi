package mt.edu.um.mlrs.runnables;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mt.edu.um.mlrs.spell.CharacterMapping;
import mt.edu.um.mlrs.spell.CharacterSubstitutionChecker;
import mt.edu.um.mlrs.spell.SpellChecker;
import mt.edu.um.mlrs.split.MTRegex;
import mt.edu.um.mlrs.split.RegexTokeniser;
import mt.edu.um.mlrs.split.Tokeniser;
import mt.edu.um.util.CollectArgs;
import mt.edu.um.util.dict.DBSpellingDictionary;
import mt.edu.um.util.dict.DictEntry;
import mt.edu.um.util.dict.ListDictionary;
import mt.edu.um.util.dict.TrieDictionary;
import mt.edu.um.util.io.FileFinder;
import mt.edu.um.util.io.FileUtils;

import org.apache.commons.lang.StringUtils;

public class CorpusSpellCheck {

	public static String MAP_FILE = "res/spell/charmap2.mt.txt";
	public static String WORD_FILE = "A:\\mlrs\\speller\\rcasha-speller\\words.utf8.txt";
	public static String CORRECTION_FILE = "/media/work/mlrs/speller/spellingDB/corrections.agreed.txt";
	// "A:\\mlrs\\speller\\spellingDB\\corrections.agreed.txt";
	public static int MIN_FREQ = 1;
	public static double MIN_EDIT = 1.0D;

	public static void main(String[] args) throws Exception {
		Map<String, String> arg = CollectArgs.getArgs(args);
		String mode = arg.get("-mode");

		if (mode.equals("c")) {
			correctMTChars(arg.get("-in"));

		} else if (mode.equals("r")) {
			replaceFromDB(arg.get("-in"), arg.get("-out"), arg.get("-log"));

		} else if (mode.equals("rf")) {
			replaceFromFile(arg.get("-in"), arg.get("-out"), arg.get("-log"));

		} else if (mode.equals("over")) {
			findOverlaps(arg.get("-in"), arg.get("-log"));
		}

	}

	private static void dumpTable() throws Exception {
		BufferedWriter writer = new BufferedWriter(FileUtils.getWriter(
				"A:\\mlrs\\speller\\corrections.per.token_15-03-2012.txt",
				"UTF-8"));
		DBSpellingDictionary dict = new DBSpellingDictionary(
				"com.mysql.jdbc.Driver", "jdbc:mysql://localhost/mlrsspell",
				"root", "root");
		dict.init();
		System.err.append("Retrieving corrections...");
		Map<String, List<DictEntry>> entries = dict.getCorrections();
		System.err.append("done\n");

		System.err.append("Dumping list to file...");
		for (String s : entries.keySet()) {
			writer.write(s);

			for (DictEntry e : entries.get(s)) {
				writer.write(": " + e.getLabel() + ": " + e.getWeight());
			}

			writer.write("\n");
		}
		writer.close();
		System.err.append("done\n.");
	}

	private static void replaceFromFile(String in, String out, String log)
			throws Exception {
		System.err.append("Loading file-based dictionary...");
		ListDictionary dict = ListDictionary.fromFile(CORRECTION_FILE, "UTF-8",
				"\t");
		System.err.append("found " + dict.numEntries() + " entries \n");

		Tokeniser t = new RegexTokeniser(MTRegex.TOKEN);
		long t1, t2, duration;
		BufferedReader reader;
		BufferedWriter writer, logWriter;
		int num, line;
		String token, correction;

		for (File f : FileFinder.findFiles(in)) {
			t1 = System.currentTimeMillis();
			System.err.append(f.getAbsolutePath() + "...");
			reader = FileUtils.getBufferedReader(f, "UTF-8");
			writer = FileUtils.getBufferedWriter(new File(out, f.getName()),
					"UTF-8");
			logWriter = FileUtils.getBufferedWriter(new File(log, f.getName()
					+ ".log.txt"), "UTF-8");
			num = 0;
			line = 0;

			while (reader.ready()) {
				line++;
				token = reader.readLine().trim();

				if (StringUtils.isAlpha(token) && dict.contains(token)) {
					correction = dict.getCategory(token).toString();
					correction = renderCase(token, correction);
					List<String> tokenised = t.split(correction);
					logWriter.write(f.getName() + ": " + line + ": " + token
							+ ": " + correction + "\n");
					correction = reconstituteString(correction, tokenised);
					num++;
				} else {
					correction = token;
				}

				writer.write(correction + "\n");
			}

			// System.err.append(num + "\n");
			reader.close();
			writer.close();
			logWriter.close();
			t2 = System.currentTimeMillis();
			duration = t2 - t1;
			System.err.append(num + " corrections in " + duration + "ms for "
					+ line + " lines\n");
		}
	}

	private static void findOverlaps(String in, String log) throws Exception {
		BufferedWriter logWriter;		
		String token, previous;
		BufferedReader reader;

		for (File f : FileFinder.findFiles(in, ".nd")) {
			int i = -1;
			System.err.append(f.getAbsolutePath() + "...");
			reader = FileUtils.getBufferedReader(in, "UTF-8");
			logWriter = FileUtils.getBufferedWriter(new File(log, f.getName()
					+ ".overlaps.txt"), "UTF-8");
			previous = null;			

			while (reader.ready()) {
				i++;
				token = reader.readLine().toLowerCase().trim();

				if (previous != null && token.startsWith(previous)) {
					logWriter.write(f.getAbsolutePath() + "\t" + i + "\t"
							+ "PREVIOUS" + "\t" + previous + "\t" + token
							+ "\n");
				}

//				if (next != null && token.endsWith(next)) {
//					logWriter.write(f.getAbsolutePath() + "\t" + i + "\t"
//							+ "NEXT" + "\t" + next + "\t" + token + "\n");
//				}
//				
				previous = token;				
			}
			
			System.err.append("done\n");
		}

	}

	private static void replaceFromDB(String in, String out, String log)
			throws Exception {
		DBSpellingDictionary dict = new DBSpellingDictionary(
				"com.mysql.jdbc.Driver", "jdbc:mysql://localhost/mlrsspell",
				"root", "root");
		dict.init();
		Tokeniser t = new RegexTokeniser(MTRegex.TOKEN);
		Pattern pattern = Pattern.compile("^" + MTRegex.ALPHA_TOKEN + "$",
				Pattern.CASE_INSENSITIVE);
		Matcher matcher;
		long t1, t2;

		for (File f : FileFinder.findFiles(in)) {
			t1 = System.currentTimeMillis();
			System.err.append(f.getAbsolutePath() + "...");
			BufferedReader reader = new BufferedReader(FileUtils.getReader(f,
					"UTF-8"));
			BufferedWriter writer = new BufferedWriter(FileUtils.getWriter(
					new File(out, f.getName()), "UTF-8"));
			BufferedWriter logWriter = new BufferedWriter(FileUtils.getWriter(
					new File(log, f.getName() + ".log.txt"), "UTF-8"));
			int num = 0;
			int line = 0;
			String previous = null;
			String token = null;
			String correction = null;

			while (reader.ready()) {
				line++;
				token = reader.readLine();
				correction = token;
				int wordFreq = 0;
				matcher = pattern.matcher(token);

				if (matcher.matches()) {
					token = token.trim();
					List<DictEntry> corrections = dict.getCorrections(token);

					if (!corrections.isEmpty()) {
						DictEntry entry = corrections.get(0);
						wordFreq = entry.getFrequency(); // the word's frequency

						if (entry.getLabel() != null && wordFreq <= MIN_FREQ
								&& entry.getWeight() >= MIN_EDIT) {
							// only correct words which are very low freq and
							// are actually different from the correction
							correction = renderCase(token, entry.getLabel()
									.toString());

							List<String> tokenised = t.split(correction);

							if (tokenised.size() > 1) {
								// case where correction is a string that was
								// run together
								logWriter.write(f.getName() + ": " + line
										+ ": " + token + ": " + correction
										+ "\n");
								correction = reconstituteString(correction,
										tokenised);
								num++;
							}

							// else if (previous != null
							// && correction.startsWith(previous)) {
							// //overlap
							// logWriter.write(f.getName() + ": " + line
							// + ": OVERLAP\n");
							// } else {
							// logWriter.write("\n");
							// }
						}
					}
					// } else {
					// logWriter.write(f.getName() + ": " + line + ": "
					// + token + ": NONE\n");
					// }
				}

				previous = token;
				writer.write(correction + "\n");
			}

			// System.err.append(num + "\n");
			reader.close();
			writer.close();
			logWriter.close();
			t2 = System.currentTimeMillis();
			long duration = t2 - t1;
			System.err.append(num + " corrections in " + duration + "ms for "
					+ line + " lines\n");
		}
	}

	private static String reconstituteString(String tok, List<String> tokens) {
		String result = tok;

		if (tokens.size() > 1) {
			StringBuffer buffer = new StringBuffer();

			for (int i = 0; i < tokens.size() - 1; i++) {
				buffer.append(tokens.get(i).trim()).append("\n");
			}

			buffer.append(tokens.get(tokens.size() - 1));
			result = buffer.toString();
		}

		return result;
	}

	private static String renderCase(String original, String repl) {
		if (StringUtils.isAllUpperCase(original)) {
			repl = StringUtils.upperCase(repl);

		} else if (Character.isUpperCase(original.charAt(0))) {
			repl = StringUtils.capitalize(repl);
		}

		return repl;

	}

	private static void correctMTChars(String indir) throws Exception {
		CharacterMapping mapping = CharacterMapping.fromFile(
				"res/spell/charmap2.mt.txt", "UTF-8", "\t");
		Tokeniser tok = new RegexTokeniser(MTRegex.TOKEN);
		TrieDictionary dict = TrieDictionary.fromFile(WORD_FILE, "UTF-8", true);
		SpellChecker speller = new CharacterSubstitutionChecker(tok, dict,
				mapping);

		for (File infile : FileFinder.findFiles(indir, ".txt")) {
			List<String> lines = FileUtils.readLinesFromFile(infile, "UTF-8");

		}
	}
}
