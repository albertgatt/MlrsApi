package mt.edu.um.mlrs.runnables;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import mt.edu.um.mlrs.lexicon.JSONLexicon;
import mt.edu.um.util.io.FileUtils;

public class GetGabraGlosses {

	static String url = "http://mlrs.research.um.edu.mt/resources/gabra/api/search.json?s=";
	static String wordlistFile = "res/wordlists/gabralexemes.txt";
	static String outputFile = "res/wordlists/gabralexemes.glosses.txt";

	private static String checkJSONCompatibleString(String s) {
		if (!(s.startsWith("[") && s.endsWith("]"))) {
			s = "[" + s + "]";
		}

		return s;
	}

	public static void main(String[] args) throws Exception {
		List<String> words = FileUtils.readLinesFromFile(wordlistFile, "UTF-8");
		BufferedWriter writer = FileUtils
				.getBufferedWriter(outputFile, "UTF-8");

		for (String word : words) {
			StringBuffer glossString = new StringBuffer();
			glossString.append(word);

			try {
				StringBuilder builder = new StringBuilder();
				URL url = new URL(GetGabraGlosses.url + word);
				URLConnection urlc = url.openConnection();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(urlc.getInputStream()));
				String line;

				while ((line = reader.readLine()) != null) {
					String js = checkJSONCompatibleString(line);
					builder.append(js);
				}

				final JSONArray arr = new JSONArray(builder.toString());

				if (arr.length() > 0) {
					JSONObject obj = arr.getJSONObject(0);

					if (obj.has("lexemes")) {
						JSONArray lexemes = obj.getJSONArray("lexemes");

						for (int i = 0; i < lexemes.length(); i++) {
							JSONObject lexemeObject = lexemes.getJSONObject(i).getJSONObject("Lexeme");

							if (lexemeObject.has("gloss")) {
								String gloss = lexemeObject.getString("gloss");
								gloss = gloss.replaceAll("[\\r\\n]", "|");
								glossString.append("|").append(gloss.trim());
							}
						}
					}
				}
				
				System.out.println(glossString.toString());
				writer.write(glossString.toString() + "\n");
				
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println(word);
			}
		}
	
		writer.close();
	}
	
}
