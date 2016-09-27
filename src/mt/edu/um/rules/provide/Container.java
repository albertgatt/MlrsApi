package mt.edu.um.rules.provide;

public interface Container<E> {

	public void accept(Provider<E> provider);

	public void add(E item);

}
