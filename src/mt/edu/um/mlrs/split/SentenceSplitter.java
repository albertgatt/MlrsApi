package mt.edu.um.mlrs.split;

import java.util.ArrayList;
import java.util.List;

import mt.edu.um.mlrs.exception.TextProcessorException;

import org.apache.commons.lang.StringUtils;

import com.aliasi.sentences.HeuristicSentenceModel;

public class SentenceSplitter extends Splitter {
	
	private HeuristicSentenceModel _sentenceModel;
	private Tokeniser _tokeniser;

	public SentenceSplitter(Tokeniser tokeniser) {
		super();
		this._sentenceModel = new MTSentenceModel();						
		this._tokeniser = tokeniser;
		setTargetType("PARAGRAPH");
		setDestinationType("SENTENCE");
	}	

//	public SentenceSplitter(Tokeniser tokeniser,
//			Classifier classifier) {
//		this(tokeniser);		
//	}

	@Override
	public List<String> split(String str)
			throws TextProcessorException {
		String text = StringUtils.cleanWithin(str);
		List<String> results = new ArrayList<String>();
		List<String> tokens = this._tokeniser.split(text);
		List<String> spaces = this._tokeniser.getWhitespaceTokens();
		String[] tokArray = new String[tokens.size()];
		String[] whiteArray = new String[spaces.size()];
		tokens.toArray(tokArray);
		spaces.toArray(whiteArray);
		int[] boundaries = this._sentenceModel.boundaryIndices(tokArray,
				whiteArray);
		int sentStartTok = 0;
		int sentEndTok = 0;

		for (int i = 0; i < boundaries.length; ++i) {
			sentEndTok = boundaries[i];
			StringBuffer buffer = new StringBuffer();

			for (int j = sentStartTok; j <= sentEndTok; j++) {
				buffer.append(tokArray[j]);
				buffer.append(whiteArray[j + 1]);
			}

			results.add(buffer.toString());
			sentStartTok = sentEndTok + 1;
		}
		
		//Some sentences can consist of no end-sentence markers and this
		//model won't recongise them.
		if(results.size() == 0) {
			results.add(text);
		}

		return results;
	}

	

}
