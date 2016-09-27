package mt.edu.um.mlrs.text.transform;

import mt.edu.um.mlrs.exception.TransformerException;
import mt.edu.um.mlrs.text.TextNode;
import mt.edu.um.util.io.FileUtils;

public class TextNodeToTaggedTextDoc extends TextTransformer<TextNode,String> {

	private String _separator;
	private boolean _oneTokenPerLine;

	public TextNodeToTaggedTextDoc() {
		super();
		init();
	}
	

	// public TextNodeToCanonicalTagForm(TransformationRules<TextNode,String>
	// rules) {
	// super(rules);
	// init();
	// }

	private void init() {
		setTokenTagSeparator("_");
		setOneTokenPerLine(true);	
	}
	
	public void setOneTokenPerLine(boolean tok) {
		this._oneTokenPerLine = tok;
	}

	public boolean isOneSentencePerLine() {
		return this._oneTokenPerLine;
	}

	public void setTokenTagSeparator(String separator) {
		this._separator = separator;
	}

	public String getTokenTagSeparator() {
		return this._separator;
	}

	@Override
	public String transform(TextNode node) {
		StringBuilder builder = new StringBuilder();
		String type = node.getType();
		String content = node.getContent();

		if ((type != null) && type.equals("TOKEN")) {
			String tag = node.getValue("CAT");

			builder.append(content);
			builder.append(this._separator);
			builder.append(tag);

			if (this._oneTokenPerLine) {
				builder.append("\n");
			} else {
				builder.append(" ");
			}
		} else {
			for (TextNode child : node.getChildren()) {
				builder.append(transform(child));
			}
		}

		return builder.toString();
	}

	public void transformToFile(TextNode node, String outputFile, String charset)
			throws TransformerException {
		String result = transform(node);

		try {
			FileUtils.writeStringToFile(outputFile, result, charset);

		} catch (Exception e) {
			throw new TransformerException("Exception writing to file: "
					+ e.getMessage(), e);
		}
	}

}
