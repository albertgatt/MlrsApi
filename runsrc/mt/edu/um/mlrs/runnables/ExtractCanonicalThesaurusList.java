package mt.edu.um.mlrs.runnables;

import java.io.BufferedWriter;
import java.util.List;

import mt.edu.um.util.io.FileUtils;

public class ExtractCanonicalThesaurusList {

	public static String FILE  = "A:\\mlrs\\lexicon\\sketchengine-collocations\\thes_mt02.txt";
	public static String OUT = "A:\\mlrs\\lexicon\\sketchengine-collocations\\thes_mt02.list-format.txt";
	
	public static void main(String[] args) throws Exception {
		BufferedWriter writer = FileUtils.getBufferedWriter(OUT, "UTF-8");
		List<String> lines = FileUtils.readLinesFromFile(FILE, "UTF-8");
		
		for(String line: lines) {
			String[] elements = line.split("\\s+");
			int id = Integer.parseInt(elements[0].trim());
			String headword = elements[1].trim();
			
			for(int i = 2; i < elements.length; i++) {
				String next = elements[i];
				String split[] = next.split("_");
				String colloc = split[0].trim();
				double salience = Double.parseDouble(split[1].trim());
				writer.write(id + "," + headword + "," + colloc + "," + salience + "\n");
			}
		
		}
		
		writer.close();
	}
	
}
