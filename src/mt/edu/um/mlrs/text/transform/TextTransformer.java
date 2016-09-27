package mt.edu.um.mlrs.text.transform;

import java.util.ArrayList;
import java.util.List;

import mt.edu.um.mlrs.exception.TransformerException;
import mt.edu.um.mlrs.text.TextNode;
import mt.edu.um.mlrs.text.TextNodeProcessor;

public abstract class TextTransformer<I,O> {

	//protected TransformationRules<I,O> rules;
	protected TextNode header;
	protected List<TextNodeProcessor> processors;
	
	public TextTransformer() {
		this.processors = new ArrayList<TextNodeProcessor>();
	}
	

	// public TextTransformer(TransformationRules<I,O> rules) {
	// this();
	// this.rules = rules;
	// }
 
	public void setDefaultHeader(TextNode header) {
		this.header = header;
	}
	
	public TextNode getDefaultHeader() {
		return this.header;
	}
	
	public boolean hasDefaultHeader() {
		return this.header != null;
	}
	
	public void addProcessor(TextNodeProcessor proc) {
		this.processors.add(proc);
	}
	
	public List<TextNodeProcessor> getProcessors() {
		return this.processors;
	}
	
	protected TextNode runProcessors(TextNode text) {		
		
		for(TextNodeProcessor proc: this.processors) {
			text = proc.process(text);
		}
		
		return text;
	}
	
	public abstract O transform(I textnode) throws TransformerException;	

}
