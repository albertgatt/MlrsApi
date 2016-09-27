package mt.edu.um.mlrs.text;

import java.util.List;

import mt.edu.um.mlrs.exception.TextProcessorException;


public abstract class AddChildProcess extends TextNodeProcessor {
	protected String _destinationType;

	public AddChildProcess() {
		super();
	}
	
	public void setDestinationType(String type) {
		this._destinationType = type;
	}

	public String getDestinationType() {
		return this._destinationType;
	}
	
	public abstract List<TextNode> process(String text)
			throws TextProcessorException;	

}
