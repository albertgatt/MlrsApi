package mt.edu.um.mlrs.runnables;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;

import mt.edu.um.util.dict.DBBackedDictionary;
import mt.edu.um.util.dict.DictEntry;
import mt.edu.um.util.io.FileUtils;

public class MatchWordsInList {

	
	
	public static void main(String[] args) throws Exception {
		DBBackedDictionary dict = new DBBackedDictionary("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/mlrs", "root", "root");
//		String freq = args[0];
		List<String> regexes = FileUtils.readLinesFromFile(args[0], "UTF-8");
		String out = args[1];
//		ListDictionary dict = ListDictionary.fromFile(freq, "UTF-8", "\t");
//		System.err.println("Dictionary loaded");
		
		List<DictEntry> matches = new ArrayList<DictEntry>();
		
		for(String pattern: regexes) {
			System.err.println("Searching: " + pattern);
			matches.addAll(dict.matchEntries(pattern));
		}
		
		System.err.println("Writing results..");
		BufferedWriter writer = new BufferedWriter(FileUtils.getWriter(out, "UTF-8"));
		
		for(DictEntry e: matches) {
			writer.write(e.getToken() + "\t" + e.getWeight() + "\n");
		}
		
		writer.close();
		
	}
	
}
