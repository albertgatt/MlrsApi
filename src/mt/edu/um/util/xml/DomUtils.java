package mt.edu.um.util.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class DomUtils {

	private static class CachedDTD implements EntityResolver {

		public static final String XHTML_DTD = "xhtml1-strict.dtd";
		public static final String XHTML_LAT1 = "xhtml-lat1.ent";
		public static final String XHTML_SYMBOL = "xhtml-symbol.ent";
		public static final String XHTML_SPECIAL = "xhtml-special.ent";

		public InputSource resolveEntity(String arg0, String arg1)
				throws SAXException, IOException {
			String resource = arg1.substring(arg1.lastIndexOf("/") + 1);
			InputStream uri = null;
			if (resource.equals(XHTML_DTD)) {
				uri = this.getClass().getResourceAsStream("/" + XHTML_DTD);
			} else if (resource.equals(XHTML_LAT1)) {
				uri = this.getClass().getResourceAsStream("/" + XHTML_LAT1);
			} else if (resource.equals(XHTML_SYMBOL)) {
				uri = this.getClass().getResourceAsStream("/" + XHTML_SYMBOL);
			} else if (resource.equals(XHTML_SPECIAL)) {
				uri = this.getClass().getResourceAsStream("/" + XHTML_SPECIAL);
			} else {
				return null;
			}
			return new InputSource(uri);

		}

	}

	public static final Document parseXML(String xml) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		StringReader reader = new StringReader(xml);
		InputSource source = new InputSource(reader);
		return builder.parse(source);
	}
	
	public static final Document loadDocument(String path) throws Exception {
		return loadDocument(new File(path));
	}
	
	

	public static final Document loadDocument(File file) throws Exception {
		InputStream is = new FileInputStream(file);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		builder.setEntityResolver(new CachedDTD());
		return builder.parse(is);
	}

	public static final void writeDocument(Document doc, String path)
			throws Exception {
		writeDocument(doc, path, "UTF-8");
	}

	public static final void writeDocument(Document doc, File file)
			throws Exception {
		writeDocument(doc, file, "UTF-8");
	}

	public static final void writeDocument(Document doc, String path,
			String charset) throws Exception {
		writeDocument(doc, new File(path), charset);
	}

	public static final void writeDocument(Document doc, File file,
			String charset) throws Exception {
		// output objects
		FileOutputStream stream = new FileOutputStream(file);
		OutputStreamWriter writer = new OutputStreamWriter(stream, charset);
		StreamResult result = new StreamResult(writer);

		// transform doc
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		transformer.transform(source, result);
		stream.close();
	}

}
