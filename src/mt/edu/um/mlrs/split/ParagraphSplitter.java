package mt.edu.um.mlrs.split;

import java.util.ArrayList;
import java.util.List;

import mt.edu.um.mlrs.exception.TextProcessorException;
import mt.edu.um.mlrs.text.TextNode;

import org.apache.commons.lang.StringUtils;

public class ParagraphSplitter extends Splitter {

	private String _paragraphRegex = "\\n{1,}";

	public ParagraphSplitter() {
		setTargetType("TEXT");
		setDestinationType("PARAGRAPH");
	}

	public void setParagraphRegex(String regex) {
		this._paragraphRegex = regex;
	}

	@Override
	public List<String> split(String text) {
		List<String> paragraphs = new ArrayList<String>();
		String[] parags = text.split(this._paragraphRegex);
		
		for (String nextPar : parags) {
			String clean = StringUtils.defaultString(StringUtils.trimToEmpty(nextPar));

			if (!StringUtils.isWhitespace(clean) && !StringUtils.isEmpty(clean)) {
				paragraphs.add(clean);
			}
		}

		return paragraphs;
	}

	@Override
	public List<TextNode> process(String text) throws TextProcessorException {
		List<TextNode> results = new ArrayList<TextNode>();

		for (String nextPar : split(text)) {
			results.add(new TextNode(this._destinationType, nextPar));
		}

		return results;
	}

}
