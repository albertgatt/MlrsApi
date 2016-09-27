package mt.edu.um.util.io.convert;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.Document;
import javax.swing.text.rtf.RTFEditorKit;

import mt.edu.um.util.io.FileFinder;


public class RTFConverter {
	List<File> files;
	RTFEditorKit rtf;

	public RTFConverter(String directory) {
		this.files = new ArrayList<File>();
		this.files = FileFinder.findFiles(new File(directory));
		this.rtf = new RTFEditorKit();
	}

	public void parseAll(String outputDirectory) {
		for (File f : this.files) {
			try {
				FileInputStream stream = new FileInputStream(f);
				InputStreamReader reader = new InputStreamReader(stream, "UTF8");
				Document doc = this.rtf.createDefaultDocument();
				this.rtf.read(reader, doc, 0);
				String plainText = doc.getText(0, doc.getLength());
				byte[] utf8 = plainText.getBytes("UTF8");
				// String utf8String = new String(utf8, "UTF8");
				FileOutputStream output = new FileOutputStream(new File(
						outputDirectory + "/" + f.getName() + ".txt"));

				output.write(utf8);
				output.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		RTFConverter conv = new RTFConverter(args[0]);
		conv.parseAll(args[1]);
	}

}
