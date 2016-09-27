package mt.edu.um.mlrs.runnables;

import java.io.BufferedWriter;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mt.edu.um.mlrs.split.MTRegex;
import mt.edu.um.mlrs.split.RegexTokeniser;
import mt.edu.um.mlrs.split.Tokeniser;
import mt.edu.um.util.dict.DBBackedDictionary;
import mt.edu.um.util.dict.DictEntry;
import mt.edu.um.util.io.FileUtils;

public class ExtractPatterns {

	public static Map<String, DictEntry> matches = new HashMap<String, DictEntry>();

	public static Tokeniser TOKENISER = new RegexTokeniser(MTRegex.TOKEN);

	public static Matcher HEAD_MATCHER = Pattern.compile("\\p{Lu}{5,}")
			.matcher("");
	
	public static void main(String[] args) throws Exception {
		String inFile = args[0];
		String outDir = args[1];
		DBBackedDictionary dict = new DBBackedDictionary(
				"com.mysql.jdbc.Driver", "jdbc:mysql://localhost/mlrsspell",
				"root", "root");
		dict.init();
		
		List<String> search = FileUtils.readLinesFromFile(inFile, "UTF-8");
		BufferedWriter writer;
		String pattern;
		
		for(String head: search) {
			pattern = ".*" + head.replaceAll("\\.", ".*") + ".*";
			System.err.println(head + ": " + pattern);
			writer = new BufferedWriter(FileUtils.getWriter(new File(outDir, head + ".txt"), "UTF-8"));
			
			for(DictEntry entry: dict.matchEntries(pattern)) {
				writer.write(entry.getToken() + "\t" + entry.getFrequency() + "\n");
			}
			
			writer.close();
		}
		
	}
	

//	public static void main(String[] args) throws Exception {		
//		XmlToTextNode transformer = new XmlToTextNode();
//		List<File> files = FileFinder.findFiles(new File(args[0]), ".xml",
//				"web");
//		List<String> patterns = FileUtils.readLinesFromFile(args[1], "UTF-8");
//		System.err.println(files.size() + " files found");
//		int sentenceCount = 0;
//		int tokenCount = 0;
//
//		for (File f : files) {
//			System.err.println("Processing: " + f.getAbsolutePath());
//			TextNode node = transformer.transform(DomUtils.loadDocument(f));
//
//			for (TextNode n : node.getChildren(true, "TOKEN")) {
//				String lc = StringUtils.cleanWithin(n.getContent())
//						.toLowerCase();
//
//				if (!StringUtils.isEmpty(lc) && !StringUtils.isWhitespace(lc)) {
//
//					for (String pattern : patterns) {
//
//						if (lc.matches(pattern)) {
//
//							if (ExtractPatterns.matches.containsKey(lc)) {
//								ExtractPatterns.matches.get(lc).incrementFrequency(1);
//
//							} else {
//								DictEntry entry = new DictEntry(lc, pattern);
//								ExtractPatterns.matches.put(lc, entry);
//							}
//
//							break;
//						}
//					}
//				}
//			}
//		}
//
//		File out = new File(args[2]);
//		OutputStreamWriter writer = FileUtils.getWriter(out, "UTF-8");
//
//		for (DictEntry match : ExtractPatterns.matches.values()) {
//			writer.write(match.getLabel() + "\t" + match.getToken() + "\t"
//					+ match.getFrequency() + "\n");
//		}
//
//		writer.close();
//		System.err.println(sentenceCount + " sentences; " + tokenCount
//				+ " tokens.");
//	}
}
