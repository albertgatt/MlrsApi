package mt.edu.um.mlrs.text.transform;

public abstract class TransformationRules<I,O> {

	public abstract O apply(I input);
	
	public abstract String apply(String text);
	
}
