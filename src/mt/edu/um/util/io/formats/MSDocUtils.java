package mt.edu.um.util.io.formats;

import java.io.File;
import java.io.FileInputStream;

import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.hwpf.HWPFDocument;

public class MSDocUtils {

	public static String readTextFrom(File file) throws Exception {
		HWPFDocument doc = new HWPFDocument(new FileInputStream(file));
		WordExtractor extractor = new WordExtractor(doc);
		return extractor.getText();
	}
	
	public static String[] getParagraphs(File file) throws Exception {
		HWPFDocument doc = new HWPFDocument(new FileInputStream(file));
		WordExtractor extractor = new WordExtractor(doc);
		return extractor.getParagraphText();
	}

}
