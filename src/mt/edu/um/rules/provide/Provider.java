package mt.edu.um.rules.provide;

import mt.edu.um.rules.exception.ProviderException;

public interface Provider<E> {

	public void initialise() throws ProviderException;

	public void populate(Container<E> container) throws ProviderException;

}
