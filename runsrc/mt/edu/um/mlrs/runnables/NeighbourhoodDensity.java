package mt.edu.um.mlrs.runnables;

import java.io.BufferedWriter;
import java.util.List;

import mt.edu.um.mlrs.spell.EditDistance;
import mt.edu.um.util.dict.TrieDictionary;
import mt.edu.um.util.io.FileUtils;

public class NeighbourhoodDensity {
	public static String SERI_WORD = "res/spell/dict_18-11-2011.dict";
	public static String WORD_FILE = "res/wordlists/types_malti02+03+aquilina.txt";
	public static String TARGETS = "res/wordlists/rsvp.verbs.txt";
	public static String OUTFILE = "res/wordlists/rsvp.nd.txt";

	public static void main(String[] args) throws Exception {
		List<String> words = FileUtils.readLinesFromFile(TARGETS, "UTF-8");		
		long t1 = System.currentTimeMillis();
		TrieDictionary dict = TrieDictionary.fromFile(WORD_FILE, "UTF-8", true);
		//TrieDictionary dict = SerialiseFiles.deserialize(SERI_WORD, TrieDictionary.class);//TrieDictionary.fromFile(WORD_FILE, "UTF-8");		
		long t2 = System.currentTimeMillis();
		long diff = t2-t1;
		System.out.println("Loaded dictionary in " + diff + "ms");
		
		// //SerialiseFiles.deserialize(
		// // "res/spell/charmapping.mt.dict", TrieDictionary.class);
		// System.out.println("Dict loaded");
		// NeighbourhoodDensity density = new NeighbourhoodDensity(dict);
		// System.out.println(dict.nBestMatches("tar", density.distance, 1.0D,
		// 5000));
		// System.out.println(density.getNeighbours("tar"));
		EditDistance dist = new EditDistance(0.0D, 1.0D, 1.0D, 1.0D, Double.POSITIVE_INFINITY);
		
		BufferedWriter writer = FileUtils.getBufferedWriter(OUTFILE, "UTF-8");
		
		for(String w: words) {
			System.out.println(w);
			writer.write(w + ": " + dict.neighbourhodDensity(w, dist, 1.0D, 100000) + "\n");
		}
		
		System.out.println("Done!");
		writer.close();
	
	}

}
