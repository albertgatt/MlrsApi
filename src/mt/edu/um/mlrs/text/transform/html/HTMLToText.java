package mt.edu.um.mlrs.text.transform.html;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mt.edu.um.mlrs.classify.Classifier;
import mt.edu.um.mlrs.exception.TransformerException;
import mt.edu.um.mlrs.split.SentenceSplitter;
import mt.edu.um.mlrs.text.transform.TextTransformer;

import org.apache.commons.lang.StringUtils;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

public class HTMLToText extends TextTransformer<URLConnection, CharSequence> {
	public static final int MIN_TEXT_LENGTH = 3000;

	public static final int MIN_SENT_LENGTH = 30;

	public static final String DISALLOW = "Disallow:";

	private NodeClassFilter _linkTagFilter;

	private SentenceSplitter _sentenceSplitter;

	private int _minTextLength, _minSentLength;

	private Parser _parser;

	private AttributeBasedTextExtractingVisitor _extractor;

	private Map<String, String> _header;

	private Classifier _classifier;

	private String _defaultClass;

	private String _encoding;

	public HTMLToText(SentenceSplitter splitter) {
		super();
		this._linkTagFilter = new NodeClassFilter(LinkTag.class);
		this._sentenceSplitter = splitter;
		this._extractor = new AttributeBasedTextExtractingVisitor();
		setMinTextLength(HTMLToText.MIN_TEXT_LENGTH);
		setMinSentenceLength(HTMLToText.MIN_SENT_LENGTH);
	}

	public void setEncoding(String encoding) {
		this._encoding = encoding;
	}

	public String getEncoding() {
		return this._encoding;
	}

	public void setDefaultClass(String cl) {
		this._defaultClass = cl;
	}

	public String getDefaultClass() {
		return this._defaultClass;
	}

	public void extractTextFrom(String node, String attribute, String value) {
		this._extractor.extractTextFrom(node, attribute, value);
	}

	public void extractTextFrom(String node) {
		this._extractor.extractTextFrom(node);
	}

	public void setClassifier(Classifier classifier) {
		this._classifier = classifier;
	}

	public Classifier getClassifier() {
		return this._classifier;
	}

	public void setHeaderInfo(Map<String, String> header) {
		this._header = header;
	}

	public Map<String, String> getHeaderInfo() {
		return this._header;
	}

	public void setMinTextLength(int minchar) {
		this._minTextLength = minchar;
	}

	public int getMinTextLength() {
		return this._minTextLength;
	}

	public void setMinSentenceLength(int minchar) {
		this._minSentLength = minchar;
	}

	public int getMinSentenceLength() {
		return this._minSentLength;
	}

	public CharSequence transform(String url) throws TransformerException {
		try {
			return transform(new URL(url).openConnection());
		} catch (IOException ioe) {
			throw new TransformerException("Could not open connection", ioe);
		}
	}

	private void resetParser(URLConnection url) throws ParserException {
		this._parser = new Parser(url);

		if (this._encoding != null) {
			this._parser.setEncoding(this._encoding);
		}

		this._extractor.reset();
	}

	@Override
	public CharSequence transform(URLConnection url) throws TransformerException {
		StringBuffer buffer = null;

		try {
			resetParser(url);
			buffer = new StringBuffer();			
			this._parser.visitAllNodesWith(this._extractor);
			String textContent = removeComments(this._extractor
					.getExtractedText());

			for (String s : this._sentenceSplitter
					.split(textContent)) {
				buffer.append(s).append("\n");
			}
			
			return buffer.toString();

		} catch (ParserException pe) {
			throw new TransformerException(
					"Error while downloading and parsing url.", pe);
		}

		
	}

	public Parser getParser() {
		return this._parser;
	}

	public List<String> getURLs(String url) throws TransformerException {
		try {
			return getURLs(new URL(url).openConnection());
		} catch (Exception e) {
			throw new TransformerException(e);
		}
	}

	public List<String> getURLs(URLConnection conn) throws TransformerException {
		try {
			this._parser = new Parser(conn);
			NodeList linktags = this._parser.parse(this._linkTagFilter);
			List<String> hrefs = new ArrayList<String>();

			for (int i = 0; i < linktags.size(); i++) {
				LinkTag nextLink = (LinkTag) linktags.elementAt(i);
				String href = nextLink.extractLink();
				hrefs.add(href);
			}

			return hrefs;
		} catch (Exception e) {
			throw new TransformerException(e);
		}
	}

	@SuppressWarnings("unused")
	private String cleanUpString(String string) {
		String result = StringUtils.defaultString(string);

		if (!StringUtils.isWhitespace(result)) {
			// result = string.replaceAll("\n", " ");
			result = string.replaceAll("\\s+", " ");
		}

		return result;
	}

	private String removeComments(String s) {
		return s.replaceAll("(&lt;|<)!--[\\s\\S]*?--(&gt;|>)", " ");
	}
}
