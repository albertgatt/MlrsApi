package mt.edu.um.mlrs.runnables;

import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mt.edu.um.util.io.FileFinder;
import mt.edu.um.util.io.FileUtils;

/**
 * Extract the list of file ids from a CWB-format corpus. Compare the list to
 * the metadata file, to see if any files have missing entries in the metadata
 * table.
 * 
 * @author Albert Gatt
 * 
 */
public class ExtractFileList {
	public static String CORPUS_DIR = "A:\\mlrs\\corpus-local\\mlrs.v2\\level2.cwb.nd.spell.tag";
	public static String CORPUS_FILE_LIST = "A:\\mlrs\\corpus-local\\mlrs.v2\\filelist.txt";
	public static String METADATA_FILE = "A:\\mlrs\\corpus-local\\mlrs.v2\\level2.cwb\\metadata.txt";
	public static String METADATA_NEW = "A:\\mlrs\\corpus-local\\mlrs.v2\\level2.cwb.nd.spell.tag\\metadata.txt";
	public static String IN_CORPUS_ONLY = "A:\\mlrs\\corpus-local\\mlrs.v2\\level2.cwb.nd.spell.tag\\missing-metadata.txt";
	public static String IN_METADATA_ONLY = "A:\\mlrs\\corpus-local\\mlrs.v2\\level2.cwb.nd.spell.tag\\removed-from-corpus.txt";
	public static String FILE_EXT = ".nd";
	public static String TEXT_START = "<text id=\"(.+)\">";
	private static Pattern TEXT_PATTERN = Pattern.compile(TEXT_START);

	public static void main(String[] args) throws Exception {
		Map<String,String> filesInMetadata = readMetadataFile();
		Collection<String> filesInCorpus = getCorpusFileList();
		
		//write corpus list to file
		FileUtils.writeLinesToFile(CORPUS_FILE_LIST, filesInCorpus, "UTF-8");

		// files in metadata which are not in the corpus				
		Set<String> filesNotInCorpus = new HashSet<String>(filesInMetadata.keySet());
		filesNotInCorpus.removeAll(filesInCorpus);
		FileUtils.writeLinesToFile(IN_METADATA_ONLY, filesNotInCorpus, "UTF-8");
		
		//update the metadata list: remove spurious files
		for(String s: filesNotInCorpus) {
			filesInMetadata.remove(s);
		}
				
		
		// files in corpus which are not in the metadata
		Set<String> filesNotInMetadata = new HashSet<String>(filesInCorpus);
		filesNotInMetadata.removeAll(filesInMetadata.keySet());
		
		//update metadata list: add new files
		//currently only for press files
		for(String s: filesNotInMetadata ) {
			if(s.startsWith("press")) {
				filesInMetadata.put(s, "press");
			} else if(s.startsWith("religion")) {
				filesInMetadata.put(s, "religion");
			}  else {
				System.err.println(s);
			}
		}
		
		//and re-write metadata
		FileUtils.writeFieldsToFile(filesInMetadata, METADATA_NEW, "UTF-8", "\t");
		
		
	}

	private static Collection<String> getCorpusFileList() throws Exception {
		List<File> files = FileFinder.findFiles(CORPUS_DIR, FILE_EXT);
		List<String> filenames = new ArrayList<String>();
		BufferedReader reader;

		for (File f : files) {
			reader = FileUtils.getBufferedReader(f, "UTF-8");

			while (reader.ready()) {
				String line = reader.readLine();
				Matcher matcher = TEXT_PATTERN.matcher(line);
				
				if (matcher.matches()) {
					String textid = matcher.group(1);

					if (textid != null) {
						filenames.add(textid.trim());
					}
				}

			}

			reader.close();
		}

		return filenames;
	}

	private static Map<String,String> readMetadataFile() throws Exception {
		Map<String, String> lines = FileUtils.readFieldsFromFile(METADATA_FILE,
				"UTF-8", "\t");
		return lines;
	}
}