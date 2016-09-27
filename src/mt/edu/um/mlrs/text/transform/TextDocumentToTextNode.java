package mt.edu.um.mlrs.text.transform;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.lang.StringUtils;

import mt.edu.um.mlrs.exception.TransformerException;
import mt.edu.um.mlrs.text.TextNode;
import mt.edu.um.util.io.FileUtils;

public class TextDocumentToTextNode extends TextTransformer<File, TextNode> {

	static String HEADER_SEP = "====";
	static String HEADER_SUBSEP = "|";
	static String HEADER_REGEX = "^\\s*[\\w\\s]+:.+\\s*$";

	public TextDocumentToTextNode() {
		super();
	}

	@Override
	public TextNode transform(File file) throws TransformerException {
		try {
			List<String> lines = FileUtils.readLinesFromFile(file, "UTF-8");
			return transform(lines);

		} catch (Exception e) {
			throw new TransformerException(e);
		}
	}

	public TextNode transform(List<String> lines) throws TransformerException {
		TextNode node = new TextNode("DOCUMENT");
		TextNode header = new TextNode("HEADER");
		TextNode body = new TextNode("TEXT");
		ListIterator<String> iter = lines.listIterator();
		boolean headerDone = false;
		
		if (hasDefaultHeader()) {
			node.addChild(getDefaultHeader());
			headerDone = true;
		}

		while (iter.hasNext()) {
			String line = iter.next();
			line = StringUtils.cleanWithin(line);

			if (line == null) {
				continue;
			}

			line = StringUtils.trimToEmpty(line);

			if (StringUtils.isEmpty(line)) {
				continue;
			}

			if (!headerDone && line.matches(HEADER_REGEX)) {

				String[] fields = line.split(":");
				String type = null;
				String val = null;

				if (fields.length == 2) {
					type = StringUtils.trimToEmpty(fields[0]);
					val = StringUtils.trimToEmpty(fields[1]);
				
				} else if (fields.length > 2) {
					type = StringUtils.trimToEmpty(fields[0]);
					val = StringUtils.trimToEmpty(String.join(":",
							Arrays.copyOfRange(fields, 1, fields.length - 1)));
				
				} else {
					throw new TransformerException(
							"Header entry had unexpected number of fields: "
									+ line);
				}

				if (type.contains(HEADER_SUBSEP)) {
					String[] subs = line.split(HEADER_SUBSEP);

					if (subs.length == 2) {
						TextNode child = new TextNode(subs[0], val);
						child.setValue("TYPE", subs[1]);
						header.addChild(child);

					} else {
						throw new TransformerException("Cannot have more than 2 subfields in headerline");
					}

				} else {
					TextNode child = new TextNode(type, val);
					header.addChild(child);
				}
				
			} else if (line.matches(HEADER_SEP) && !headerDone) {
				headerDone = true;
			
			} else if(headerDone) {
				TextNode parag = runProcessors(new TextNode("PARAGRAPH", line));
				body.addChild(parag);
			}
		}
		
		node.addChild(header);
		node.addChild(body);
		return node;
	}

}
