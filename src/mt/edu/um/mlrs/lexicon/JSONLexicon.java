package mt.edu.um.mlrs.lexicon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import mt.edu.um.mlrs.exception.TextProcessorException;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONLexicon extends Lexicon {

	public static enum Mode {
		JSON, XML
	};

	static String DEFAULT_WORD_PATTERN = "[\\wġħċżĦĠĊŻ-]{2,}";
	static String LEMMA_FEATURE = "lemma";
	static String RADICAL_FEATURE = "radicals";
	static String WF_FEATURE = "Wordform";
	static String SF_FEATURE = "surface_form";
	static String GLOSS_FEATURE = "gloss";

	private String _lexUrl, _radUrl;
	private Map<String, String> _lemmas;
	private Map<String, String> _radicals;
	private Map<String, Map<Integer, List<String>>> _mirrorRadicals;
	private Mode _mode = Mode.XML;
	private String _wPattern;

	public JSONLexicon(String urlString) {
		this._lexUrl = urlString;
		this._lemmas = new TreeMap<String, String>();
		this._radicals = new TreeMap<String, String>();
		this._mirrorRadicals = new TreeMap<String, Map<Integer, List<String>>>();
		setWordPattern(DEFAULT_WORD_PATTERN);
	}

	public JSONLexicon(String lexicalURL, String radicalURL) {
		this(lexicalURL);
		this._radUrl = radicalURL;
	}

	public void setWordPattern(String pattern) {
		this._wPattern = pattern;
	}

	public void setMode(Mode mode) {
		this._mode = mode;
	}

	public Mode getMode() {
		return this._mode;
	}

	public String get(String word, String feature) {
		String result = null;
		word = StringUtils.trimToEmpty(word);

		if (StringUtils.isEmpty(word)) {
			return null;

		} else {
			word = word.toLowerCase();

			if (RADICAL_FEATURE.equals(feature)) {
				result = getRadicals(word);

			} else if (LEMMA_FEATURE.equals(feature)) {
				result = getLemma(word);
			
			} 
		}

		return result;
	}

	public String getLemma(String word) {

		if (!this._lemmas.containsKey(word)) {
			this.retrieveWordData(word);
		}

		return this._lemmas.get(word);
	}

	public String getRadicals(String word) {

		if (!this._radicals.containsKey(word)) {
			this.retrieveWordData(word);
		}

		return this._radicals.get(word);
	}

	public Map<Integer, List<String>> getRootWords(String root, String pos, int nRadicals) {
		if (!this._mirrorRadicals.containsKey(root)) {
			this.retrieveRootData(root, pos, nRadicals);
		}

		return this._mirrorRadicals.get(root);
	}

	/*
	 * Check if a String is JSON-compatible, i.e. begins and ends with []; if
	 * not, enclose it.
	 */
	private String checkJSONCompatibleString(String s) {
		if (!(s.startsWith("[") && s.endsWith("]"))) {
			s = "[" + s + "]";
		}

		return s;
	}

	protected void retrieveRootData(String root, String pos, int nRadicals) {
		try {
			StringBuilder builder = new StringBuilder();
			URL url = new URL(this._radUrl + root);
			URLConnection urlc = url.openConnection();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					urlc.getInputStream()));
			String line;

			while ((line = reader.readLine()) != null) {
				String js = checkJSONCompatibleString(line);
				builder.append(js);
			}

			final JSONArray arr = new JSONArray(builder.toString());

			if (arr.length() > 0) {
				JSONObject obj = arr.getJSONObject(0);

				if (obj.has("roots")) {
					JSONArray wf = obj.getJSONArray("roots");

					for (int i = 0; i < wf.length(); i++) {
						JSONObject rootObject = wf.getJSONObject(i);

						if (rootObject != null && rootObject.has(pos)
								&& rootObject.has("Root")) {

							JSONObject actualRoot = rootObject
									.getJSONObject("Root");
							int radicalCount = actualRoot
									.getInt("radical_count");
							String radicals = actualRoot.getString("radicals");							
							
							//Only accept with this radical
							if (radicalCount == nRadicals
									&& radicals.equalsIgnoreCase(root)) {								
								
								// lemmas for this root
								List<String> words = new ArrayList<String>();

								// get the part of the root entry corresponding
								// to
								// desired POS
								JSONObject posObject = rootObject
										.getJSONObject(pos);

								// retrieve the names of the fields
								// NB: they are numeric, and oddly numberede
								for (String field : JSONObject
										.getNames(posObject)) {
									if (field.length() > 0) {
										JSONObject nextPOS = posObject
												.getJSONArray(field)
												.getJSONObject(0);

										if (nextPOS.has(LEMMA_FEATURE)) {
											String lemmaSF = nextPOS
													.getString(LEMMA_FEATURE);
											words.add(lemmaSF);
										}
									}
								}

								if (this._mirrorRadicals.containsKey(root)) {
									this._mirrorRadicals.get(root)
											.put(i, words);

								} else {
									Map<Integer, List<String>> map = new HashMap<Integer, List<String>>();
									map.put(i, words);
									this._mirrorRadicals.put(root, map);
								}
							}
						}
					}
				}
			}

		} catch (IOException ioe) {
			throw new TextProcessorException(
					"Error opening URL connection while processing word "
							+ root, ioe);
		} catch (JSONException jse) {
			throw new TextProcessorException("JSON web service exception", jse);

		}
	}

	protected void retrieveWordData(String word) {

		if (word.matches("\\p{Punct}") || !word.matches(this._wPattern)) {
			this._lemmas.put(word, null);
			this._radicals.put(word, null);

		} else {

			try {
				StringBuilder builder = new StringBuilder();
				URL url = new URL(this._lexUrl + word);
				URLConnection urlc = url.openConnection();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(urlc.getInputStream()));
				String line;
				String lemma = null;
				String radical = null;

				while ((line = reader.readLine()) != null) {
					String js = checkJSONCompatibleString(line);
					builder.append(js);
				}

				final JSONArray arr = new JSONArray(builder.toString());

				if (arr.length() > 0) {
					JSONObject obj = arr.getJSONObject(0);

					if (obj.has("wordforms")) {
						JSONArray wf = obj.getJSONArray("wordforms");

						for (int i = 0; i < wf.length(); i++) {
							JSONObject wordFormObject = wf.getJSONObject(i);
							String wordForm = wordFormObject.getJSONObject(
									"Wordform").getString(SF_FEATURE);

							if (wordForm != null
									&& word.equalsIgnoreCase(wordForm)
									&& wordFormObject.has("Lexeme")) {
								JSONObject lexeme = wordFormObject
										.getJSONObject("Lexeme");
								lemma = lexeme.getString(LEMMA_FEATURE);
								radical = lexeme.has("root") ? lexeme
										.getJSONObject("root").getString(
												RADICAL_FEATURE) : null;
								break;
							}
						}
					}
				}

				this._lemmas.put(word, lemma);
				this._radicals.put(word, radical);

				// if (this._mirrorRadicals.containsKey(radical)) {
				// this._mirrorRadicals.get(radical).add(word);
				//
				// } else {
				// List<String> list = new ArrayList<String>();
				// list.add(word);
				// this._mirrorRadicals.put(radical, list);
				// }

			} catch (IOException ioe) {
				throw new TextProcessorException(
						"Error opening URL connection while processing word "
								+ word, ioe);
			} catch (JSONException jse) {
				throw new TextProcessorException("JSON web service exception",
						jse);

			}
		}
	}

}
