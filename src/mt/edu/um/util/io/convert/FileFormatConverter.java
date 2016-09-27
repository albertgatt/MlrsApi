package mt.edu.um.util.io.convert;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;

import mt.edu.um.util.io.FileFinder;


public class FileFormatConverter {

	private String _in;
	private String _out;

	public FileFormatConverter(String inputFormat, String outputFormat) {
		this._in = inputFormat;
		this._out = outputFormat;
	}

	public void convert(String inputDir, String outputDir) {
		List<File> inFiles = FileFinder.findFiles(new File(inputDir));

		for (File f : inFiles) {
			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(new FileInputStream(f), this._in));
				FileOutputStream out = new FileOutputStream(new File(outputDir
						+ "/" + f.getName()));
				BufferedWriter writer = new BufferedWriter(
						new OutputStreamWriter(out, this._out));
				// List<String> lines = new ArrayList<String>();

				while (reader.ready()) {
					String line = reader.readLine();
					// String newLine = line.replaceAll("\\t", "_");
					// newLine = newLine.replaceAll("\\s", "");
					// lines.add(newLine);
					writer.write(line);
					writer.write("\n");
				}

				// for(String s: lines) {
				// writer.write(s.trim());
				// writer.write(" ");
				// }

				writer.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		String in = args[0];
		String out = args[1];
		String indir = args[2];
		String outdir = args[3];
		FileFormatConverter conv = new FileFormatConverter(in, out);
		conv.convert(indir, outdir);
	}

}
