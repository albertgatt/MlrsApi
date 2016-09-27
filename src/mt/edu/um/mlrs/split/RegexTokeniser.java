package mt.edu.um.mlrs.split;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Pattern;

import mt.edu.um.mlrs.text.TextNode;

import com.aliasi.tokenizer.RegExTokenizerFactory;
import com.aliasi.tokenizer.Tokenizer;

public class RegexTokeniser extends Tokeniser {
	/**
	 * Lingpipe tokenizerfactory
	 */
	private RegExTokenizerFactory _factory;

	public RegexTokeniser(String regex) {
		super();
		this._factory = new RegExTokenizerFactory(regex,
				Pattern.CASE_INSENSITIVE);
		this._tokens = new ArrayList<String>();
		this._spaces = new ArrayList<String>();
	}

	@Override
	public List<String> split(String text) {
		this._tokens.clear();
		this._spaces.clear();
		Tokenizer tok = this._factory.tokenizer(text.toCharArray(), 0, text
				.length());
		tok.tokenize(this._tokens, this._spaces);

		if (this._speller != null) {
			this._tokens = this._speller.getBest(this._tokens);
		}
		
		if(!this._keepEmptyTokens) {
			ListIterator<String> iter = this._tokens.listIterator();
			String next;
			
			while(iter.hasNext()) {
				next = iter.next();
				
				if(next.length() == 0) {
					iter.remove();
				}
			}
		}

		return this._tokens;
	}

	@Override
	public List<TextNode> process(String text) {
		List<TextNode> results = new ArrayList<TextNode>();

		for (String token : split(text)) {
			results.add(new TextNode(this._destinationType, token));			
		}

		return results;
	}
}
