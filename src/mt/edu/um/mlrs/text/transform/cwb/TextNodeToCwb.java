package mt.edu.um.mlrs.text.transform.cwb;

import java.util.Iterator;
import java.util.List;

import mt.edu.um.mlrs.exception.TransformerException;
import mt.edu.um.mlrs.text.TextNode;
import mt.edu.um.mlrs.text.transform.TextTransformer;

import org.apache.commons.lang.StringUtils;

public class TextNodeToCwb extends TextTransformer<TextNode, String> {

	private String id;
	private int totalS;
	private int totalP;

	public TextNodeToCwb() {
		totalS = 0;
		totalP = 0;
	}

	public void setCurrentID(String id) {
		this.id = id;
	}

	public int getTotalSentences() {
		return this.totalS;
	}

	public int getTotalPars() {
		return this.totalP;
	}

	@Override
	public String transform(TextNode textnode) throws TransformerException {
		StringBuffer buffer = new StringBuffer();

		if (this.id != null) {
			buffer.append("<text id=\"").append(id).append("\">\n");
		}

		List<TextNode> pars = textnode.getChildren(true, "PARAGRAPH");
		Iterator<TextNode> iter = pars.iterator();
		int scount = 0;
		int pcount = 0;

		while (iter.hasNext()) {
			TextNode next = iter.next();
			List<TextNode> sentences = next.getChildren(false, "SENTENCE");

			if (sentences.size() == 0) {
				continue;
			}

			buffer.append("<p id=\"" + pcount + "\">\n");
			for (TextNode s : next.getChildren(false, "SENTENCE")) {
				String lang = s.getValue("LANG");

				if (lang == null || lang.equalsIgnoreCase("mt")) {
					List<TextNode> tokens = s.getChildren(false,"TOKEN");
					
					if(tokens.isEmpty()) {
						continue;
					}
					
					buffer.append("<s id=\"" + scount + "\">\n");
	
					for (TextNode t : s.getChildren(false, "TOKEN")) {
						String content = StringUtils
								.cleanWithin(t.getContent());

						if (content != null && content.length() > 0) {
							buffer.append(content);
							
							if(t.hasAttributes()) {
								
								for(String a: t.getAttributes()) {
									buffer.append("\t").append(t.getValue(a));
								}
							}
							
							buffer.append("\n");
						} 
					}

					buffer.append("</s>\n");
					scount++;
					this.totalS++;
				}
			}

			buffer.append("</p>\n");
			pcount++;
			this.totalP++;
		}

		buffer.append("</text>\n");

		if (pcount == 0) {
			return null;
		} else {
			return buffer.toString();
		}
	}
}
