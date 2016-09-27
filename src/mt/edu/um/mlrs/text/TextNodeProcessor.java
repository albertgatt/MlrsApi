package mt.edu.um.mlrs.text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mt.edu.um.mlrs.exception.TextProcessorException;

public abstract class TextNodeProcessor {

	protected String _inputEncoding, _outputEncoding;
	protected Set<String> ignoreNodes;	
	protected List<String> _targetTypes;

	public TextNodeProcessor() {
		setInputEncoding("UTF-8");
		setOutputEncoding("UTF-8");
		this.ignoreNodes = new HashSet<String>();
		this._targetTypes = new ArrayList<String>();
	}

	public void ignoreNode(String tag) {
		this.ignoreNodes.add(tag);
	}

	public boolean ignoresNode(String nodeName) {
		return this.ignoreNodes.contains(nodeName);
	}
	
	public void addTargetType(String type) {
		this._targetTypes.add(type);
	}
	
	public void setTargetType(String type) {
		this._targetTypes.clear();
		this._targetTypes.add(type);
	}

	public void setTargetTypes(Collection<String> types) {
		this._targetTypes.clear();
		this._targetTypes.addAll(types);
	}

	public List<String> getTargetTypes() {
		return this._targetTypes;
	}

	public void setOutputEncoding(String encoding) {
		this._outputEncoding = encoding;
	}

	public String getOutputEncoding() {
		return this._outputEncoding;
	}

	public void setInputEncoding(String encoding) {
		this._inputEncoding = encoding;
	}

	public String getInputEncoding() {
		return this._inputEncoding;
	}	

	public abstract TextNode process(TextNode node) throws TextProcessorException;

}
