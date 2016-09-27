package mt.edu.um.mlrs.runnables;

import java.util.List;
import java.util.Map;

import mt.edu.um.mlrs.split.MTRegex;
import mt.edu.um.mlrs.split.RegexTokeniser;
import mt.edu.um.mlrs.split.Tokeniser;
import mt.edu.um.util.CollectArgs;
import mt.edu.um.util.io.FileUtils;

public class TokeniseCorpus {
	static Tokeniser tok = new RegexTokeniser(MTRegex.TOKEN);

	private static void printHow() {
		System.err
				.println("To run: \n java -jar mlrs.jar -i <input text file> -o <output text file>");
	}

	public static void main(String[] args) throws Exception {
		Map<String, String> a = CollectArgs.getArgs(args);

		if (a.containsKey("-h")) {
			printHow();
			System.exit(1);
		} else if (a.size() != 2 || !a.containsKey("-i")
				|| !a.containsKey("-o")) {
			System.err.println("Unexpected number of arguments");
			printHow();
			System.exit(1);
		}

		String input = a.get("-i");
		String output = a.get("-o");
		List<String> tokens = null;

		try {
			tokens = tok.split(FileUtils.readStringFromFile(input, "UTF-8"));
		} catch (Exception e) {
			System.err
					.println("Error reading input file. Please check that the input file exists and is in utf-8 text format.");
			System.exit(1);
		}

		try {
			FileUtils.writeLinesToFile(output, tokens, "UTF-8");
		} catch (Exception e) {
			System.err
					.println("Error writing to output file. Please check that the destination directory exists.");
			System.exit(1);
		}
	}
}
