package mt.edu.um.mlrs.runnables;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.List;

import mt.edu.um.mlrs.split.MTRegex;
import mt.edu.um.mlrs.text.TextNode;
import mt.edu.um.mlrs.text.transform.XmlToTextNode;
import mt.edu.um.util.dict.DictEntry;
import mt.edu.um.util.dict.Dictionary;
import mt.edu.um.util.dict.ListDictionary;
import mt.edu.um.util.io.FileFinder;
import mt.edu.um.util.io.FileUtils;
import mt.edu.um.util.xml.DomUtils;

import org.apache.commons.lang.StringUtils;

public class FrequencyCounter {
	private XmlToTextNode transformer;
	private Dictionary dict;

	public static void main(String[] args) throws Exception {
		String in = args[0];
		String out = args[1];
		String mode = args[2];
		FrequencyCounter counter = new FrequencyCounter();

		System.err.println("Starting count...");

		if (mode.equals("-xml")) {
			counter.processXML(in);
		} else if (mode.equals("-cwb")) {
			counter.processCWB(in);
		}

		System.err.println("Printing...");
		counter.print(out);
		System.err.println("Done!");
	}

	public FrequencyCounter() {
		this.transformer = new XmlToTextNode();
		this.dict = new ListDictionary();
	}

	public void processCWB(String inputDir) throws Exception {		
		List<File> files = FileFinder.findFiles(inputDir, "nd");
		BufferedReader reader;

		for (File f : files) {
			System.err.println("Processing: " + f.getAbsolutePath());
			reader = FileUtils.getBufferedReader(f, "UTF-8");

			while (reader.ready()) {
				String line = reader.readLine().toLowerCase().trim();
			
				if (line.matches(MTRegex.ALPHA_TOKEN)) {
					this.dict.insert(line);
				}
			}
			
			reader.close();			
		}
	}

	public void processXML(String inputDir) throws Exception {
		List<File> files = FileFinder.findFiles(inputDir, "xml");
		TextNode node;
		Iterator<TextNode> iter;
		TextNode child;
		String tokenLC;

		for (File f : files) {
			System.err.println("Processing: " + f.getAbsolutePath());
			node = this.transformer.transform(DomUtils.loadDocument(f));
			iter = node.preorder();

			while (iter.hasNext()) {
				child = iter.next();
				if (child.getType().equals("TOKEN") && child.hasContent()) {
					tokenLC = StringUtils.cleanWithin(child.getContent()
							.toLowerCase());

					if (accept(tokenLC)) {
						this.dict.insert(tokenLC);
					}
				}
			}
		}
	}

	private boolean accept(String tokenLC) {
		if (!StringUtils.isWhitespace(tokenLC) && !StringUtils.isEmpty(tokenLC)
				&& StringUtils.isAlpha(tokenLC)) {
			return true;
		}

		return false;
	}

	public void print(String outfile) throws Exception {
		OutputStreamWriter writer = FileUtils.getWriter(outfile, "UTF-8");
		BufferedWriter bf = new BufferedWriter(writer);

		for (DictEntry entry : this.dict.entries()) {
			bf.write(entry.getToken() + "\t" + entry.getFrequency() + "\n");
		}

		bf.close();

	}

}
