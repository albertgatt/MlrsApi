package mt.edu.um.mlrs.classify.tag;

import mt.edu.um.mlrs.classify.Classifier;
import mt.edu.um.mlrs.text.TextNode;
import mt.edu.um.rules.Rule;
import mt.edu.um.rules.provide.Container;

public abstract class Tagger extends Classifier implements Container<Rule<TextNode, TextNode>> {

	protected TagDictionary dictionary;

	public Tagger() {
		super();
		this.setAttributeName("POS");
	}

	public Tagger(TagDictionary dict) {
		setDictionary(dict);
	}	

	public TagDictionary getDictionary() {
		return dictionary;
	}

	public void setDictionary(TagDictionary dictionary) {
		this.dictionary = dictionary;
	}
	
	public void addToDictionary(String token, String tag) {
		this.dictionary.addEntry(token, tag);
	}
}
