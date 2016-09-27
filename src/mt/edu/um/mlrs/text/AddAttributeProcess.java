package mt.edu.um.mlrs.text;

import java.util.ArrayList;
import java.util.List;

public abstract class AddAttributeProcess extends TextNodeProcessor {
	protected List<String> _destinationAttributes;

	public AddAttributeProcess() {
		super();
		this._destinationAttributes = new ArrayList<String>();
	}

	public void setDestinationAttributes(List<String> attributes) {
		this._destinationAttributes.addAll(attributes);
	}

	public void setDestinationAttributes(String... attributes) {
		for (String s : attributes) {
			this._destinationAttributes.add(s);
		}
	}

	public List<String> getDestinationType() {
		return this._destinationAttributes;
	}	
	
	public abstract TextNode process(String text);

}
