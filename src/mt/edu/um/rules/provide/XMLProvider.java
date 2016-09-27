package mt.edu.um.rules.provide;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import mt.edu.um.rules.exception.ProviderException;

import org.w3c.dom.Document;

public abstract class XMLProvider<E> implements Provider<E> {
	protected String file;
	protected Document document;
	protected DocumentBuilder builder;

	public XMLProvider(String configFile) {
		this.file = configFile;
	}

	public String getFile() {
		return this.file;
	}

	public void initialise() throws ProviderException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			this.builder = factory.newDocumentBuilder();
			this.document = this.builder.parse(new File(this.file));
		} catch (Exception e) {
			throw new ProviderException("Initialisng document builder failed.",
					e);
		}
	}

	public void reset() {
		this.builder = null;
		this.document = null;
	}
}
