package mt.edu.um.mlrs.classify.lang;

import java.io.File;
import java.util.List;

import com.ibm.icu.text.Normalizer;

import mt.edu.um.mlrs.text.TextNode;
import mt.edu.um.mlrs.text.transform.XmlToTextNode;
import mt.edu.um.util.io.FileFinder;
import mt.edu.um.util.xml.DomUtils;

public class BuildLanguageModel {

	public static int MIN_SENT_LENGTH = 8;

	public static void main(String[] args) throws Exception {
		CharLanguageModel model = new CharLanguageModel(5);
		XmlToTextNode xml2text = new XmlToTextNode();
		TextNode node;
		TextNode sentence;

		for (String s : args) {
			List<File> files = FileFinder.findFiles(new File(s), ".xml", "web.general");

			for (File f : files) {
				System.err.append("Processing: " + f.getAbsolutePath() + "...");
				node = xml2text.transform(DomUtils.loadDocument(f));
				List<TextNode> children = node.getChildren(true, "SENTENCE", "HEADING");

				for (int i = 0; i < children.size(); i++) {
					sentence = children.get(i);

					if (sentence.hasContent()
							&& sentence.getContent().length() >= MIN_SENT_LENGTH) {

						try {
							model.train(Normalizer.normalize(sentence.getContent(), Normalizer.NFC));
						} catch (OutOfMemoryError oom) {
							oom.printStackTrace();
						}
					}
				}

				System.err.append("done!\n");
			}
			
			System.gc();			
		}

		model.prune(5);
		model.writeModel("res/lm/charlm.5gram.model");

	}

}
