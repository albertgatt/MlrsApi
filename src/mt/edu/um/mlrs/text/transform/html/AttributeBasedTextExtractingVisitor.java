package mt.edu.um.mlrs.text.transform.html;

import java.util.HashMap;
import java.util.Map;

import mt.edu.um.mlrs.exception.TransformerException;

import org.apache.commons.lang.StringUtils;
import org.htmlparser.Node;
import org.htmlparser.Tag;
import org.htmlparser.Text;
import org.htmlparser.nodes.RemarkNode;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.util.Translate;
import org.htmlparser.visitors.TextExtractingVisitor;

public class AttributeBasedTextExtractingVisitor extends TextExtractingVisitor {

	private Map<String, Map<String, String>> _allowedAttributes;

	private boolean _extractAll;

	public AttributeBasedTextExtractingVisitor() {
		super();
		this._allowedAttributes = new HashMap<String, Map<String, String>>();
		this._extractAll = true;
	}

	public void visitTag(Tag tag) {
		super.visitTag(tag);
		
		if (isParagraphTag(tag)) {
			this.textAccumulator.append("\n\n");
		} else if(isSpaceTag(tag) || isLineBreakTag(tag)) {
			this.textAccumulator.append(" ");
		}
	}
	
	public void visitEndTag(Tag tag) {
		super.visitEndTag(tag);
		
		if(isSpaceTag(tag) || isLineBreakTag(tag)) {
			this.textAccumulator.append(" ");
		}		
	}

	private boolean isLineBreakTag(Tag tag) {
		return tag.getTagName().equalsIgnoreCase("br");
	}
	
	private boolean isSpaceTag(Tag tag) {
		String name = tag.getTagName();
		return name.equalsIgnoreCase("span") || name.equalsIgnoreCase("a");
	}

	private boolean isParagraphTag(Tag tag) {
		String name = tag.getTagName();
		return (name.equalsIgnoreCase("p") || name.equalsIgnoreCase("div")
				|| name.equalsIgnoreCase("li") || name.equalsIgnoreCase("td"));
	}

	public void reset() {
		this.textAccumulator = new StringBuffer();
	}

	public void extractAllText(boolean all) {
		this._extractAll = all;
	}

	public void extractTextFrom(String tagName) {
		this._extractAll = false;
		Map<String, String> attributes = new HashMap<String, String>();
		this._allowedAttributes.put(tagName, attributes);
	}

	public void extractTextFrom(String tagName, String attribute, String value) {
		this._extractAll = false;

		if (this._allowedAttributes.containsKey(tagName)) {
			Map<String, String> attributes = this._allowedAttributes
					.get(tagName.toLowerCase());
			attributes.put(attribute, value);
		} else {
			Map<String, String> attributes = new HashMap<String, String>();
			attributes.put(attribute, value);
			this._allowedAttributes.put(tagName, attributes);
		}
	}

	@Override
	public void visitStringNode(Text stringNode) {
		try {

			Node parent = stringNode.getParent();

			if (!(parent instanceof RemarkNode)) {

				if (parent instanceof TagNode) {

					if (tagAllowed((TagNode) parent) && !preTagBeingProcessed) {
						String text = stringNode.getText();
						text = Translate.decode(text);
						text = replaceNonBreakingSpaceWithOrdinarySpace(text);
						textAccumulator.append(StringUtils.cleanWithin(text));
					}
				}
			}
		} catch (Exception e) {
			throw new TransformerException(e);
		}
	}

	private boolean tagAllowed(TagNode node) {
		boolean allow = true;
		String name = node.getTagName().toLowerCase();

		if (name.equalsIgnoreCase("script")) {
			allow = false;

		} else if (!this._extractAll) {

			if (this._allowedAttributes.containsKey(name)) {
				Map<String, String> atts = this._allowedAttributes.get(name);

				if (!atts.isEmpty()) {

					for (String att : atts.keySet()) {
						String val = atts.get(att);
						String nodeVal = node.getAttribute(val);

						if (nodeVal == null || !nodeVal.equalsIgnoreCase(val)) {
							allow = false;
							break;
						}
					}
				}

			} else {
				Node parent = node.getParent();

				if (parent != null && parent instanceof TagNode) {
					allow = tagAllowed((TagNode) parent);
				} else {
					allow = false;
				}
			}
		}

		return allow;
	}
}
