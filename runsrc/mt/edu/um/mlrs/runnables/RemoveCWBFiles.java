package mt.edu.um.mlrs.runnables;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mt.edu.um.util.io.FileFinder;
import mt.edu.um.util.io.FileUtils;

public class RemoveCWBFiles {

	public static String TO_REMOVE = "/media/work/mlrs/corpus-local/mlrs.v2/removed.txt";
	public static String INPUTDIR = "/media/work/mlrs/corpus-local/mlrs.v2/level2.cwb.nd1";
	public static String OUTPUTDIR = "/media/work/mlrs/corpus-local/mlrs.v2/level2.cwb.nd2";
	public static String PREFIX = "press";
	public static String STARTTEXT = "<text id=\"(.+)\">";
	public static String ENDTEXT = "</text>";

	public static void main(String[] args) throws Exception {
		List<File> files = FileFinder.findFiles(INPUTDIR);
		List<String> toremove = FileUtils.readLinesFromFile(TO_REMOVE, "UTF-8");
		Pattern startText = Pattern.compile(STARTTEXT);

		for (File f : files) {
			if (f.getName().startsWith(PREFIX)) {
				System.err.println("Processing: " + f.getName());
				BufferedReader reader = FileUtils.getBufferedReader(f, "UTF-8");
				BufferedWriter writer = FileUtils.getBufferedWriter(new File(
						OUTPUTDIR, f.getName()), "UTF-8");
				String currFile;
				boolean skipline = false;

				while (reader.ready()) {
					String line = reader.readLine().trim();
					Matcher m = startText.matcher(line);
					
					if (m.matches()) {						
						currFile = m.group(1);						

						if (currFile != null && toremove.contains(currFile)) {
							System.err.println("Skipping: " + currFile);
							skipline = true;

						} else {
							writer.write(line + "\n");
						}

					} else if (line.matches(ENDTEXT)) {
						if (!skipline) {
							writer.write(line + "\n");
						}

						skipline = false;
						
					} else if (!skipline) {
						writer.write(line + "\n");

					}
				}
				
				reader.close();
				writer.close();

			}
		}

	}

}
