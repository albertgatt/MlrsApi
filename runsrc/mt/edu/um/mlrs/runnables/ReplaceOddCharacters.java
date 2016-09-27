package mt.edu.um.mlrs.runnables;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.util.List;

import mt.edu.um.util.io.FileFinder;
import mt.edu.um.util.io.FileUtils;

public class ReplaceOddCharacters {

	static String IN_DIR = "A:\\mlrs\\corpus-local\\mlrs.v2\\level2.cwb.nd.spell\\old";
	static String OUT_DIR = "A:\\mlrs\\corpus-local\\mlrs.v2\\level2.cwb.nd.spell";
	static String XML = "^<.+>$";

	public static void main(String[] args) throws Exception {
		List<File> in = FileFinder.findFiles(IN_DIR);
		BufferedReader reader;
		BufferedWriter writer;

		for (File f : in) {
			String filename = f.getName();
			System.out.println("File: " + filename);
			reader = FileUtils.getBufferedReader(f, "UTF-8");
			writer = FileUtils.getBufferedWriter(new File(OUT_DIR, filename),
					"UTF-8");

			while (reader.ready()) {
				String currLine = reader.readLine();

				if (currLine != null) {
					currLine = currLine.trim();

					if (!currLine.matches(XML)) {
						currLine = standardise(currLine);
					}

					if (currLine.length() > 0) {
						writer.write(currLine + "\n");
					}

				}
			}

			reader.close();
			writer.close();
		}
	}

	private static String standardise(String currLine) {
		currLine = currLine.replaceAll("\'\'", "'");
		currLine = currLine.replaceAll("ˮ", "");
		currLine = currLine.replaceAll("\"", "'");
		currLine = currLine.replaceAll("`", "'");
		currLine = currLine.replaceAll("’", "'");
		currLine = currLine.replaceAll("", "'");
		currLine = currLine.replaceAll("", "'");
		currLine = currLine.replaceAll("", "'");
		currLine = currLine.replaceAll("“", "'");
		currLine = currLine.replaceAll("”", "'");
		currLine = currLine.replaceAll("", "-");
		currLine = currLine.replaceAll("–", "-");
		currLine = currLine.replaceAll("", "'");
		currLine = currLine.replaceAll("‘", "'");
		currLine = currLine.replaceAll("\\.\\.\\.+", "...");
		currLine = currLine.replaceAll("摹", "ġ");
		currLine = currLine.replaceAll("ћ", "ħ");
		currLine = currLine.replaceAll("偶", "ż");
		currLine = currLine.replaceAll("魔", "ħ");
		currLine = currLine.replaceAll("ð", "ċ");
		currLine = currLine.replaceAll("ω", "ż");
		currLine = currLine.replaceAll("ÿ", "ż");
		currLine = currLine.replaceAll("ú", "ż");
		currLine = currLine.replaceAll("\u0093", "\"");
		currLine = currLine.replaceAll("\u0094", "\"");
		currLine = currLine.replaceAll("\u0095", "");
		currLine = currLine.replaceAll("\\p{Zs}", "");

		return currLine;
	}

}
