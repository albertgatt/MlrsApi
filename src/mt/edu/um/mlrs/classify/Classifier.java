package mt.edu.um.mlrs.classify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mt.edu.um.mlrs.exception.TextProcessorException;
import mt.edu.um.mlrs.text.AddChildProcess;
import mt.edu.um.mlrs.text.TextNode;

import org.apache.commons.lang.StringUtils;

public abstract class Classifier extends AddChildProcess {

	protected Map<String, String> _defaults;
	protected String _attName;

	public Classifier() {
		this._defaults = new HashMap<String, String>();
	}

	public void setAttributeName(String name) {
		this._attName = name;
	}

	public String getAttributeName() {
		return this._attName;
	}

	public void addDefault(String pattern, String category) {
		this._defaults.put(pattern, category);
	}

	public String getDefault(String string) {
		String result = null;

		for (String pattern : this._defaults.keySet()) {
			if (string.matches(pattern)) {
				result = this._defaults.get(pattern);
			}
		}

		return result;
	}

	public abstract String classify(String seq);

	@Override
	public List<TextNode> process(String text) throws TextProcessorException {
		TextNode node = new TextNode(this._destinationType);
		String cls = classify(text);

		if (cls != null) {
			node.setValue(this._attName, cls);
		}

		List<TextNode> list = new ArrayList<TextNode>();
		list.add(node);
		return list;
	}

	public TextNode process(TextNode node) throws TextProcessorException {
		for (TextNode child : node.getChildren(true, this._targetTypes
				.toArray(new String[this._targetTypes.size()]))) {
			String content = StringUtils.defaultString(StringUtils.strip(child
					.getContent()));
			String cls = classify(content);

			if (cls != null) {
				child.setValue(this._attName, cls);
			}
		}

		return node;
	}

}
