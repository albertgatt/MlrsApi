package mt.edu.um.rules;

public interface Operator {

	public boolean appliesTo(Object value);
	
	public boolean evaluate(Object value1, Object value2);
	
}
