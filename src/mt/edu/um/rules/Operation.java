package mt.edu.um.rules;

public abstract class Operation<I,O> {

	public abstract O apply(I... args) throws Exception;

}
