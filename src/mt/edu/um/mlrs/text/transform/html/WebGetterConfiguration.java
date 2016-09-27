package mt.edu.um.mlrs.text.transform.html;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import mt.edu.um.util.xml.DomUtils;
import mt.edu.um.util.xml.XPathUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class WebGetterConfiguration {

	public static WebGetterConfiguration fromFile(String file) throws Exception {
		WebGetterConfiguration config = new WebGetterConfiguration();
		Document doc = DomUtils.loadDocument(file);

		String directory = XPathUtils.getChildNode(doc, "config/directory")
				.getTextContent().trim();
		config.setDirectory(directory);

		String filename = XPathUtils.getChildNode(doc, "config/filename")
				.getTextContent().trim();
		config.setFilenamePrefix(filename);

		String minlength = XPathUtils.getChildNode(doc, "config/mintextlength")
				.getTextContent().trim();
		config.setMinLength(Integer.parseInt(minlength));

		// String minsentencelength = XPathUtils.getChildNode(doc,
		// "config/minsentlength").getTextContent().trim();
		// config.setMinSentenceLength(Integer.parseInt(minsentencelength));

		Node encoding = XPathUtils.getChildNode(doc, "config/encoding");

		if (encoding != null) {
			String encString = encoding.getTextContent();
			config.setEncoding(encString);

		}

		NodeList seeds = XPathUtils.getChildNodes(doc, "config/seed");
		for (int i = 0; i < seeds.getLength(); i++) {
			config.addSeed(seeds.item(i).getTextContent().trim());
		}

		NodeList searches = XPathUtils.getChildNodes(doc, "config/search");
		for (int i = 0; i < searches.getLength(); i++) {
			config.addSearchPattern(searches.item(i).getTextContent().trim());
		}

		NodeList print = XPathUtils.getChildNodes(doc, "config/print");
		for (int i = 0; i < print.getLength(); i++) {
			config.addPrintPattern(print.item(i).getTextContent().trim());
		}

		NodeList header = XPathUtils.getChildNodes(doc, "config/header/param");
		for (int i = 0; i < header.getLength(); i++) {
			Node param = header.item(i);
			String name = XPathUtils.getAttributeValue(param, "name").trim();
			String value = param.getTextContent().trim();
			config.addHeaderNode(name, value);
		}

		Node classifierNode = XPathUtils.getChildNode(doc, "config/classifier");
		if (classifierNode != null) {
			String path = classifierNode.getTextContent().trim();
			config.setClassifierPath(path);
		}

		Node counterNode = XPathUtils.getChildNode(doc, "config/filecounter");
		if (counterNode != null) {
			config.setCount(Integer.parseInt(counterNode.getTextContent()
					.trim()));
		} else {
			config.setCount(1);
		}

		Node extractNode = XPathUtils.getChildNode(doc, "config/extractlinks");
		if (extractNode != null) {
			config.setExtractLinks(Boolean.parseBoolean(extractNode
					.getTextContent().trim()));
		}

		NodeList textnodes = XPathUtils.getChildNodes(doc, "config/textnode");
		if (textnodes != null) {
			for (int i = 0; i < textnodes.getLength(); i++) {
				Node tnode = textnodes.item(i);
				config.addTextNode(tnode.getTextContent().trim());
			}
		}

		return config;
	}

	private String directory, filenamePrefix;
	private Set<String> seeds;
	private Set<String> printPatterns;
	private Set<String> searchPatterns;
	private Map<String, String> header;
	private int minLength, minSentenceLength;
	private String classifierPath;
	private int count;
	private boolean extractLinks;
	private String encoding;
	private Set<String> textNodes;

	public WebGetterConfiguration() {
		this.seeds = new HashSet<String>();
		this.printPatterns = new HashSet<String>();
		this.searchPatterns = new HashSet<String>();
		this.textNodes = new HashSet<String>();
		this.header = new HashMap<String, String>();
		this.extractLinks = true;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public Set<String> getTextNodes() {
		return textNodes;
	}

	public void setTextNodes(Set<String> textNodes) {
		this.textNodes = textNodes;
	}

	public void addTextNode(String node) {
		this.textNodes.add(node);
	}

	public boolean extractLinks() {
		return extractLinks;
	}

	public void setExtractLinks(boolean extractLinks) {
		this.extractLinks = extractLinks;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getClassifierPath() {
		return classifierPath;
	}

	public void setClassifierPath(String classifierPath) {
		this.classifierPath = classifierPath;
	}

	public int getMinLength() {
		return minLength;
	}

	public void setMinLength(int minLength) {
		this.minLength = minLength;
	}

	public int getMinSentenceLength() {
		return minSentenceLength;
	}

	public void setMinSentenceLength(int minSentenceLength) {
		this.minSentenceLength = minSentenceLength;
	}

	public String getFilenamePrefix() {
		return this.filenamePrefix;
	}

	public void setFilenamePrefix(String pref) {
		this.filenamePrefix = pref;
	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public Set<String> getSeeds() {
		return seeds;
	}

	public void addSeed(String seed) {
		this.seeds.add(seed);
	}

	public void addSeeds(Collection<String> seeds) {
		this.seeds.addAll(seeds);
	}

	public Set<String> getPrintPatterns() {
		return printPatterns;
	}

	public void addPrintPattern(String pattern) {
		this.printPatterns.add(pattern);
	}

	public void addPrintPatterns(Collection<String> printPatterns) {
		this.printPatterns.addAll(printPatterns);
	}

	public Set<String> getSearchPatterns() {
		return searchPatterns;
	}

	public void addSearchPatterns(Collection<String> searchPatterns) {
		this.searchPatterns.addAll(searchPatterns);
	}

	public void addSearchPattern(String pattern) {
		this.searchPatterns.add(pattern);
	}

	public Map<String, String> getHeader() {
		return header;
	}

	public void setHeader(Map<String, String> header) {
		this.header = header;
	}

	public void addHeaderNode(String name, String value) {
		this.header.put(name, value);
	}

}
