package mt.edu.um.mlrs.classify.tag;

import java.util.ArrayList;
import java.util.List;

import mt.edu.um.mlrs.exception.TaggerException;

import com.aliasi.corpus.ObjectHandler;
import com.aliasi.corpus.StringParser;
import com.aliasi.tag.Tagging;
import com.aliasi.util.Strings;

/**
 * Parses text consisting of a single
 * 
 * @author agatt
 * 
 */
public class MLRSParser extends
		StringParser<ObjectHandler<Tagging<String>>> {

	private String tokenTagSeparator;
	private TagDictionary dictionary;

	public MLRSParser() {
		super();
		this.dictionary = new TagDictionary();
		setTokenTagSeparator("_");
	}

	public MLRSParser(String separator) {
		this();
		setTokenTagSeparator(separator);
	}

	public MLRSParser(ObjectHandler<Tagging<String>> handler) {
		super(handler);
	}

	public TagDictionary getDictionary() {
		return dictionary;
	}

	public void setDictionary(TagDictionary dictionary) {
		this.dictionary = dictionary;
	}

	public void setTokenTagSeparator(String separator) {
		this.tokenTagSeparator = separator;
	}

	@Override
	public void parseString(char[] cs, int start, int end) {
		String in = new String(cs, start, end - start);
		String[] sentences = in.split("\n");

		for (int i = 0; i < sentences.length; ++i) {
			if (Strings.allWhitespace(sentences[i])) {
				continue;
			}

			processSentence(sentences[i]);
		}
	}

	private void processSentence(String sentence) {
		String[] tagTokenPairs = sentence.split("\\s+");
		List<String> tokens = new ArrayList<String>();
		List<String> tags = new ArrayList<String>();

		for (int i = 0; i < tagTokenPairs.length; ++i) {
			String pair = tagTokenPairs[i];

			try {
				int j = pair.lastIndexOf(this.tokenTagSeparator);

				if (j > -1) {
					String token = pair.substring(0, j).trim();
					String tag = pair.substring(j + 1).trim();
					this.dictionary.addEntry(token, tag);

					if (token == null) {
						throw new TaggerException("Null token at index " + j);
					} else if (tag == null) {
						throw new TaggerException("Null tag at index " + j);
					}

					tokens.add(token);
					tags.add(tag);
				}
			} catch (StringIndexOutOfBoundsException e) {
				throw new TaggerException("Error splitting token-tag pair in: "
						+ pair, e);
			}
		}

		getHandler().handle(new Tagging<String>(tokens, tags));
	}
}
