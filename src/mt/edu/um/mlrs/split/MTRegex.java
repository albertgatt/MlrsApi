package mt.edu.um.mlrs.split;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MTRegex {

	/**
	 * Definite article + prepositions with cliticised article
	 */
	public static String DEF_ARTICLE = "((((da|di|b[ħh]a|g[hħ]a|b[ħh]al|g[ħh]al|li(l?)|sa|ta|ma|f(i?)|mi(l?)|bi(l?)|[ġg]o|i|sa|b)?[dtlrnsxzcżċ])|eks)-)";

	public static String DEF_NUMERAL = "(-i[dtlrnsxzcżċ])";

	public static String L_APOST = "([’'](i?)l-?)";

	/**
	 * Apostrophised prepositions
	 */
	public static String APOST = "((\\p{L}+a|[mtxbfs])['’])";

	public static String NUMBER = "(\\d+([\\.,]\\d+)*)";

	/**
	 * All other tokens: string of alphanumeric chars, numbers or a single
	 * non-alphanumeric char. (Accent allowed at end of string of alpha chars).
	 */
	public static String WORD = "((\\p{L}+`?)|\\.{3,}|\\S)";

	public static String FUNNY_WORD = "(([\\p{L}\\[\\]/\\?;:{}]+`?)|\\.{3,}|\\S)";
	
	public static String ALPHA_WORD = "\\p{L}+";
	
	public static String ALL_WORDS = DEF_ARTICLE + "|" + DEF_NUMERAL + "|" + L_APOST + "|" + APOST + "|" + WORD + "|" + ALPHA_WORD;

	public static String END_PUNCTUATION = "(\\?|\\.|,|\\!|;|:|…|\"|'|\\.\\.\\.)";

	public static String PROCLITIC_PREP = "(^\\p{L}['’]$)";

	public static String ABBREV_PREFIX = "(sant['’]|(onor|sra|nru|dott|kap|mons|dr|prof)\\.)";

	public static String NUMERIC_DATE = "(\\d{1,2}[-/]\\d{1,2}[-/]\\d{2,4})|(\\d{2,4}[-/]\\d{1,2}[-/]\\d{1,2})";

	public static String URL = "(((http|ftp|gopher|javascript|telnet|file)://(www\\.)?)|mailto:|www\\.).+\\s$";
	public static String URL2 = "(((http|ftp|https|gopher|javascript|telnet|file)://)|(www\\.)|(mailto:))[\\w\\-_]+(\\.[\\w\\-_]+)?([\\w\\-\\.,@?^=%&amp;:/~\\+#]*[\\w\\-\\@?^=%&amp;/~\\+#])?";
	
	// It-Tlieta, 25 ta' Frar, 2003
	public static String FULL_DATE = "(^I[ltnsdxr]-(Tnejn|Tlieta|Erbgħa|Ħamis|Ġimgħa|Sibt|Ħadd), \\d{1,2} ta' "
			+ "(Jannar|Frar|Marzu|April|Mejju|Ġunju|Lulju|Awwissu|Settembru|Ottubru|Novembru|Diċembru), \\d{4,}$)";

	
	//Hyphen-separated prefixes which shouldn't be separated from following words
	public static String PREFIX = "(sotto|inter|intra|mini|ex|eks|pre|post|sub|neo|soċjo)-";
	
	/**
	 * All tokens: definite article or token
	 */
	public static String TOKEN = MTRegex.URL2 + "|" + MTRegex.DEF_ARTICLE + "|" + DEF_NUMERAL + "|"
			+ MTRegex.APOST + "|" + MTRegex.L_APOST + "|"
			+ MTRegex.ABBREV_PREFIX + "|" + MTRegex.NUMERIC_DATE + "|"
			+ MTRegex.NUMBER + "|" + MTRegex.WORD;

	public static String ALPHA_TOKEN = MTRegex.DEF_ARTICLE + "|" + DEF_NUMERAL
			+ "|" + MTRegex.APOST + "|" + MTRegex.L_APOST + "|"
			+ MTRegex.ABBREV_PREFIX + "|" + MTRegex.ALPHA_WORD;

	public static String ANY_TOKEN = MTRegex.DEF_ARTICLE + "|" + DEF_NUMERAL
			+ "|" + MTRegex.APOST + "|" + MTRegex.L_APOST + "|"
			+ MTRegex.ABBREV_PREFIX + "|" + MTRegex.FUNNY_WORD;
	
	public static boolean requiresFollowingSpace(String word, String next) {
		return next == null ? false : (!isEndPunctuation(next)
				&& !isSuffix(next) && !isPrefix(word));
	}

	private static boolean isEndPunctuation(String string) {
		Matcher matcher = Pattern.compile(MTRegex.END_PUNCTUATION).matcher(
				string);
		return matcher.matches();
	}

	private static boolean isPrefix(String string) {
		Matcher matcher = Pattern.compile(
				MTRegex.DEF_ARTICLE + "|" + MTRegex.PROCLITIC_PREP + "|"
						+ MTRegex.ABBREV_PREFIX, Pattern.CASE_INSENSITIVE)
				.matcher(string);
		return matcher.matches();
	}

	private static boolean isSuffix(String string) {
		Matcher matcher = Pattern.compile(MTRegex.DEF_NUMERAL,
				Pattern.CASE_INSENSITIVE).matcher(string);
		return matcher.matches();

	}

}
