package mt.edu.um.mlrs.text.transform;

import mt.edu.um.mlrs.exception.TransformerException;
import mt.edu.um.mlrs.text.TextNode;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;

public class MSWordToTextNode extends TextTransformer<HWPFDocument, TextNode> {

	public MSWordToTextNode() {
		super();
	}

	@Override
	public TextNode transform(HWPFDocument document)
			throws TransformerException {

		WordExtractor extractor = new WordExtractor(document);
		String[] parags = extractor.getParagraphText();
		TextNode doc = new TextNode("DOCUMENT");

		if (hasDefaultHeader()) {
			doc.addChild(getDefaultHeader());
		}

		for (String parag : parags) {
			parag = WordExtractor.stripFields(parag);
			parag = StringUtils.cleanWithin(StringUtils.trim(parag));

			if (!parag.isEmpty()) {
				TextNode paragraphNode = runProcessors(new TextNode(
						"PARAGRAPH", parag));
				doc.addChild(paragraphNode);
			}
		}

		return doc;
	}

}
