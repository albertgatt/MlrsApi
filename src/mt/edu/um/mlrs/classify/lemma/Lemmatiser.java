package mt.edu.um.mlrs.classify.lemma;

import mt.edu.um.mlrs.exception.TextProcessorException;
import mt.edu.um.mlrs.lexicon.Lexicon;
import mt.edu.um.mlrs.text.AddAttributeProcess;
import mt.edu.um.mlrs.text.TextNode;

import org.apache.commons.lang.StringUtils;

public class Lemmatiser extends AddAttributeProcess {

	private Lexicon _lexicon;

	public Lemmatiser(Lexicon lexicon) {
		super();
		this._lexicon = lexicon;
		this.setTargetType("TOKEN");
		this.setDestinationAttributes("lemma", "root");
	}

	public Lexicon getLexicon() {
		return this._lexicon;
	}

	@Override
	public TextNode process(String text) throws TextProcessorException {
		TextNode node = new TextNode("TOKEN", text);
		setAttributes(node);
		return node;
	}

	@Override
	public TextNode process(TextNode node) {
		for (TextNode child : node.getChildren(true, this._targetTypes)) {
			setAttributes(child);
		}

		return node;
	}

	private void setAttributes(TextNode node) {
		String text = StringUtils.defaultString(StringUtils.strip(node
				.getContent()));

		for (String s : this._destinationAttributes) {

			if ("lemma".equals(s)) {
				String lemma = this._lexicon.get(text, "lemma");
				node.setValue(s, lemma == null ? text : lemma);

			} else if ("root".equals(s)) {
				String root = this._lexicon.get(text, "root");
				node.setValue(s, root == null ? "NONE" : root);
			}

		}
	}

}
