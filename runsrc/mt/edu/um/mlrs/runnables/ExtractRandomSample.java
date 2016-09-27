package mt.edu.um.mlrs.runnables;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import mt.edu.um.util.io.FileFinder;
import mt.edu.um.util.io.FileUtils;

/**
 * Extract a random sample of sentences from files in a corpus directory. Files
 * are assumed to be in CWB format.
 * 
 * @author Albert Gatt
 * 
 */
public class ExtractRandomSample {
	public static String CORPUS_DIR = "A:\\mlrs\\corpus-local\\mlrs.v2\\level2.cwb.nd.spell.tag";
	public static String OUTPUT_FILE = "A:\\mlrs\\annotation\\corpus-sample.txt";
	public static String CORPUS_FILE_LIST = "A:\\mlrs\\corpus-local\\mlrs.v2\\filelist.txt";
	public static String FILE_EXT = ".nd";
	public static String TEXT_START = "<text id=\"(.+)\">";
	public static String SENT_START = "^<s.+>$";
	public static String SENT_END = "^</s>$";
	public static int SAMPLE = 1000;
	
	public static void main(String[] args) throws Exception {
		List<File> files = FileFinder.findFiles(CORPUS_DIR, FILE_EXT);
		BufferedWriter writer = FileUtils.getBufferedWriter(new File(
				OUTPUT_FILE), "UTF-8");
		BufferedReader reader;
		int count = 0;
		boolean start = false;
		List<String[]> sentences = new ArrayList<String[]>();

		for (File f : files) {
			reader = FileUtils.getBufferedReader(f, "UTF-8");
			String s;
			List<String> currSentence = new ArrayList<String>();

			while (reader.ready()) {
				s = reader.readLine();

				if (start) {
					currSentence.add(s);
				}

				if (s.matches(SENT_START)) {
					start = true;
					currSentence.clear();

				} else if (s.matches(SENT_END)) {
					sentences.add(currSentence.toArray(new String[currSentence
							.size()]));
					count++;
					start = false;
				}
			}
			
			reader.close();
		}

		
		List<String[]> sample= pickSample(sentences, SAMPLE, new Random());
		
		for(String[] s: sample) {
			for(String t: s) {
				writer.write(t);
			}
		}
		
		writer.close();
		System.out.println("Sentences: " + count);
	}

	public static <T> List<T> pickSample(List<T> population, int nSamplesNeeded,
			Random r) {
		List<T> ret = new ArrayList<T>();
		
		int i = 0, nLeft = population.size();

		while (nSamplesNeeded > 0) {
			int rand = r.nextInt(nLeft);
			
			if (rand < nSamplesNeeded) {
				ret.add(population.get(i));
				nSamplesNeeded--;
			}

			nLeft--;
			i++;
		}

		return ret;
	}

}
