package mt.edu.um.util.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public abstract class FileUtils {

	public static String getFileNameWithoutExt(File f) {
		String name = null;
		
		if(f != null) {
			String fname = f.getName();
			
			if(fname.matches("\\.\\w+$")) {
				name = fname.replace("\\.\\w+$", ""); 
			} else {
				name =fname;
			}			
		}
		
		return name;
	}

	public static List<File> getAllFiles(File root) throws IOException {
		List<File> files = new ArrayList<File>();

		if (root.isDirectory()) {

			for (File f : root.listFiles()) {

				if (f.isDirectory()) {
					files.addAll(FileUtils.getAllFiles(f));

				} else {
					files.add(f);
				}
			}
		}

		return files;
	}

	public static List<File> getAllFiles(File root, FileFilter filter) {
		List<File> files = new ArrayList<File>();

		if (root.isDirectory()) {

			for (File f : root.listFiles()) {

				if (f.isDirectory()) {
					files.addAll(FileUtils.getAllFiles(f, filter));

				} else if (filter.accept(f)) {
					files.add(f);
				}
			}
		}

		return files;

	}

	public static void writeStringToFile(String filename, String output,
			String charset) throws IOException {
		FileUtils.writeStringToFile(new File(filename), output, charset);
	}

	public static void writeStringToFile(File file, String output,
			String charset) throws IOException {
		OutputStreamWriter writer = FileUtils.getWriter(file, charset);
		writer.write(output);
		writer.close();
	}

	public static void writeLinesToFile(String filename,
			Collection<String> output, String charset, String newline)
			throws IOException {
		OutputStreamWriter writer = FileUtils.getWriter(filename, charset);

		for (String s : output) {
			writer.write(s + newline);
		}

		writer.close();
	}

	public static void writeLinesToFile(String filename,
			Collection<String> output, String charset) throws IOException {
		FileUtils.writeLinesToFile(filename, output, charset, "\n");
	}

	public static void writeLinesToFile(File file, Collection<String> output,
			String charset, String newline) throws IOException {
		OutputStreamWriter writer = FileUtils.getWriter(file, charset);

		for (String s : output) {
			writer.write(s + newline);
		}

		writer.close();
	}

	public static void writeLinesToFile(File file, Collection<String> output,
			String charset) throws IOException {
		FileUtils.writeLinesToFile(file, output, charset, "\n");
	}

	public static void writeFieldsToFile(Map<String, String> fields, File file,
			String charset, String separator) throws IOException {
		OutputStreamWriter writer = FileUtils.getWriter(file, charset);

		for (String s : fields.keySet()) {
			writer.write(s + separator + fields.get(s) + "\n");
		}

		writer.close();
	}

	public static void writeFieldsToFile(Map<String, String> fields,
			String filepath, String charset, String separator)
			throws IOException {
		FileUtils.writeFieldsToFile(fields, new File(filepath), charset,
				separator);
	}

	public static Map<String, String> readFieldsFromFile(File file,
			String charset, String separator) throws IOException {
		Map<String, String> map = new HashMap<String, String>();
		List<String> lines = FileUtils.readLinesFromFile(file, charset);

		for (String line : lines) {
			line = line.trim();
			String[] split = line.split(separator);
			
			if (split.length >= 2) {
				String field = split[0];
				map.put(field, line.replaceFirst(field + separator, ""));
			}
		}

		return map;
	}

	public static Map<String, String> readFieldsFromFile(String filename,
			String charset, String separator) throws IOException {
		return readFieldsFromFile(new File(filename), charset, separator);
	}

	public static List<String> readLinesFromFile(File file, String charset)
			throws IOException {
		List<String> list = new ArrayList<String>();
		InputStreamReader input = FileUtils.getReader(file, charset);
		BufferedReader reader = new BufferedReader(input);

		while (reader.ready()) {
			String line = reader.readLine();
			line = StringUtils.cleanWithin(line);
			
			if (line != null && line.length() > 0) {
				list.add(line);
			}
		}

		reader.close();
		return list;
	}

	public static List<String> readLinesFromFile(String filename, String charset)
			throws IOException {
		return FileUtils.readLinesFromFile(new File(filename), charset);
	}

	public static List<String> readLinesFromFile(File file, String charset,
			String upTo) throws IOException {
		List<String> list = new ArrayList<String>();
		InputStreamReader input = FileUtils.getReader(file, charset);
		BufferedReader reader = new BufferedReader(input);

		while (reader.ready()) {
			String line = reader.readLine();

			if (line != null) {
				if (line.matches(upTo)) {
					break;
				}

				list.add(line);
			}
		}

		reader.close();
		return list;
	}

	public static List<String> readLinesFromFile(String filename,
			String charset, String upTo) throws IOException {
		return readLinesFromFile(new File(filename), charset, upTo);
	}

	public static String readStringFromFile(String filename, String charset)
			throws IOException {
		return FileUtils.readStringFromFile(new File(filename), charset);
	}

	public static String readStringFromFile(File file, String charset)
			throws IOException {
		InputStreamReader reader = FileUtils.getReader(file, charset);
		CharArrayWriter charWriter = new CharArrayWriter();
		char[] buffer = new char[1024 * 4];
		int numChars;

		while ((numChars = reader.read(buffer)) > 0) {
			charWriter.write(buffer, 0, numChars);
		}

		reader.close();
		return charWriter.toString();
	}

	public static InputStreamReader getReader(String filename, String charset)
			throws IOException {
		FileInputStream in = new FileInputStream(new File(filename));
		InputStreamReader input = new InputStreamReader(in, charset);
		return input;
	}

	public static InputStreamReader getReader(File file, String charset)
			throws IOException {
		FileInputStream in = new FileInputStream(file);
		InputStreamReader input = new InputStreamReader(in, charset);
		return input;
	}

	public static BufferedReader getBufferedReader(File file, String charset)
			throws IOException {
		return new BufferedReader(getReader(file, charset));
	}

	public static BufferedReader getBufferedReader(String filepath,
			String charset) throws IOException {
		return new BufferedReader(getReader(filepath, charset));
	}

	public static OutputStreamWriter getWriter(String filename, String charset)
			throws IOException {
		return FileUtils.getWriter(new File(filename), charset);
	}

	public static OutputStreamWriter getWriter(File file, String charset)
			throws IOException {
		FileOutputStream out = new FileOutputStream(file);
		OutputStreamWriter writer = new OutputStreamWriter(out, charset);
		return writer;
	}

	public static BufferedWriter getBufferedWriter(File file, String charset)
			throws IOException {
		return new BufferedWriter(getWriter(file, charset));
	}

	public static BufferedWriter getBufferedWriter(String filepath,
			String charset) throws IOException {
		return new BufferedWriter(getWriter(filepath, charset));
	}

	// public static TextNode readFile(String filename, String charset,
	// String rootNode) throws IOException {
	// String string = FileUtils.readStringFromFile(filename, charset);
	// return new TextNode(rootNode, string);
	// }

}
