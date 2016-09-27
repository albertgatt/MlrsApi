package mt.edu.um.mlrs.text.transform.cwb;

import java.io.BufferedWriter;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import mt.edu.um.mlrs.text.TextNode;
import mt.edu.um.mlrs.text.transform.XmlToTextNode;
import mt.edu.um.util.io.FileFinder;
import mt.edu.um.util.io.FileUtils;
import mt.edu.um.util.xml.DomUtils;

import org.w3c.dom.Document;

public class CWBEncoder {

	public static void main(String[] args) throws Exception {
		String in = args[0];
		String out = args[1];
		List<String> unwanted = args.length > 2 ? FileUtils.readLinesFromFile(
				args[2], "UTF-8") : new ArrayList<String>();

		XmlToTextNode xmlToNode = new XmlToTextNode();
		Document doc;
		BufferedWriter err = new BufferedWriter(FileUtils.getWriter(new File(
				out, "err.txt"), "UTF-8"));

		for (File f : FileFinder.findFiles(in, ".xml")) {

			if (unwanted.contains(f.getName())) {
				System.err.println("Skipping: " + f.getName());
				continue;
			}

			try {
				doc = DomUtils.loadDocument(f);
			} catch (Exception e) {
				err.write(f.getAbsolutePath() + "\n");
				continue;
			}

			String name = f.getName().replace(".xml", "");
			BufferedWriter writer = new BufferedWriter(FileUtils.getWriter(
					new File(out, name + ".txt"), "UTF-8"));
			writer.write("<text id=\"" + name.replaceAll("-", "") + "\">\n");
			// writer.write("<text>\n");
			TextNode node = xmlToNode.transform(doc);
			List<TextNode> pars = node.getChildren(true, "PARAGRAPH");
			Iterator<TextNode> iter = pars.iterator();

			while (iter.hasNext()) {
				TextNode next = iter.next();
				writer.write("<p>\n");

				for (TextNode s : next.getChildren(false, "SENTENCE")) {
					writer.write("<s>\n");

					for (TextNode t : s.getChildren(false, "TOKEN")) {
						writer.write(t.getContent() + "\n");
					}

					writer.write("</s>\n");
				}

				writer.write("</p>\n");
			}

			writer.write("</text>");
			writer.close();
		}

		err.close();
	}

}
