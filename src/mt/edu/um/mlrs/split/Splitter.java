package mt.edu.um.mlrs.split;

import java.util.ArrayList;
import java.util.List;

import mt.edu.um.mlrs.classify.Classifier;
import mt.edu.um.mlrs.exception.TextProcessorException;
import mt.edu.um.mlrs.text.AddChildProcess;
import mt.edu.um.mlrs.text.TextNode;

import org.apache.commons.lang.StringUtils;

public abstract class Splitter extends AddChildProcess {
	
	protected List<Classifier> _classifiers;
	
	public Splitter() {
		super();
		this._classifiers = new ArrayList<Classifier>();
	}
	
	public void addClassifier(Classifier cls) {
		this._classifiers.add(cls);
	}
	
	public List<Classifier> getClassifiers() {
		return this._classifiers;		
	}
	
	protected TextNode runClassifiers(TextNode node) {
		for(Classifier c: this._classifiers) {
			node = c.process(node);
		}
		
		return node;
	}
	
	public abstract List<String> split(String string);
	
	public TextNode process(TextNode text) throws TextProcessorException {
		for (TextNode child : text.getChildren(true, this._targetTypes
				.toArray(new String[this._targetTypes.size()]))) {			
			String content = StringUtils.defaultString(StringUtils.strip(child.getContent()));
			
			if (content != null && content.length() > 0) {				
				child.deleteContent();

				for (TextNode newNode : process(content)) {	
					newNode = runClassifiers(newNode);
					child.addChild(newNode);
				}
			}
		}

		return text;
	}
	
	@Override
	public List<TextNode> process(String text) throws TextProcessorException {
		List<TextNode> results = new ArrayList<TextNode>();

		for (String sentence : split(text)) {
			TextNode node = new TextNode(this._destinationType, sentence);
			node = runClassifiers(node);
			results.add(node);
		}

		return results;
	}
}
