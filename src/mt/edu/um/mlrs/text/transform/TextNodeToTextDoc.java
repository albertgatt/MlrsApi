package mt.edu.um.mlrs.text.transform;

import java.io.BufferedWriter;
import java.io.File;
import java.util.Iterator;

import mt.edu.um.mlrs.exception.TransformerException;
import mt.edu.um.mlrs.text.TextNode;
import mt.edu.um.util.io.FileFinder;
import mt.edu.um.util.io.FileUtils;
import mt.edu.um.util.xml.DomUtils;

public class TextNodeToTextDoc extends TextTransformer<TextNode, String> {

	public static String DEFAULT_TOK_SEP = " ";
	public static String DEFAULT_SENT_SEP = "\n";
	public static String DEFAULT_PARA_SEP = "\n\n";
	private String _tokSeparator, _sentSeparator, _paraSeparator;

	public TextNodeToTextDoc(String tokenSeparator, String sentenceSeparator,
			String paraSep) {
		super();
		this.setTokenSeparator(tokenSeparator);
		this.setSentenceSeparator(sentenceSeparator);
		this.setParagraphSeparator(paraSep);
	}

	public TextNodeToTextDoc() {
		this(DEFAULT_TOK_SEP, DEFAULT_SENT_SEP, DEFAULT_PARA_SEP);
	}

	public void setTokenSeparator(String separator) {
		this._tokSeparator = separator;
	}

	public void setSentenceSeparator(String sep) {
		this._sentSeparator = sep;
	}

	public void setParagraphSeparator(String para) {
		this._paraSeparator = para;
	}

	@Override
	public String transform(TextNode node) {
		StringBuilder builder = new StringBuilder();
		Iterator<TextNode> iter = node.preorder();
		TextNode next;
		String type;

		while (iter.hasNext()) {
			next = iter.next();
			type = next.getType();

			if (type != null) {

				if (type.equals("PARAGRAPH")) {
					builder.append(this._paraSeparator);
				} else if (type.equals("TOKEN")) {
					if (next.hasContent()) {
						String cont = next.getContent().trim();

						if (cont.length() > 0) {
							builder.append(next.getContent().trim());
							builder.append(this._tokSeparator);
						}
					}

				} else if (type.equals("SENTENCE")) {
					builder.append(this._sentSeparator);
				}
			}
		}

		return builder.toString();
	}

	public void transformToFile(TextNode node, File outputFile, String charset)
			throws TransformerException {
		String result = transform(node);

		try {
			FileUtils.writeStringToFile(outputFile, result, charset);

		} catch (Exception e) {
			throw new TransformerException("Exception writing to file: "
					+ e.getMessage(), e);
		}
	}

	public static void main(String[] args) throws Exception {
		String in = args[0];
		String out = args[1];
		String err = args[2];
		boolean overwrite = Boolean.parseBoolean(args[3]);
		BufferedWriter errWriter = new BufferedWriter(FileUtils.getWriter(err, "UTF-8"));
		XmlToTextNode xt = new XmlToTextNode();
		TextNodeToTextDoc tt = new TextNodeToTextDoc();

		for (File f : FileFinder.findFiles(in, ".xml")) {
			File fo = new File(out, f.getName().replace("xml", "txt"));

			if (fo.exists() && !overwrite) {
				System.err.println("File exists: " + f.getAbsolutePath());
				
			} else {
				try {
					TextNode t = xt.transform(DomUtils.loadDocument(f));
					tt.transformToFile(t, fo, "UTF-8");

				} catch (Exception e) {
					errWriter.write(f.getAbsolutePath() + "\n");
				}
			}
		}
		
		errWriter.close();
	}

}
