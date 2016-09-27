package mt.edu.um.mlrs.runnables;

import java.io.File;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mt.edu.um.mlrs.classify.lang.LanguageClassifier;
import mt.edu.um.mlrs.spell.CharacterMapping;
import mt.edu.um.mlrs.spell.CharacterSubstitutionChecker;
import mt.edu.um.mlrs.spell.SpellChecker;
import mt.edu.um.mlrs.split.MTRegex;
import mt.edu.um.mlrs.split.RegexTokeniser;
import mt.edu.um.mlrs.split.SentenceSplitter;
import mt.edu.um.mlrs.split.Tokeniser;
import mt.edu.um.mlrs.text.TextNode;
import mt.edu.um.mlrs.text.TextNodeProcessor;
import mt.edu.um.mlrs.text.transform.TextDocumentToTextNode;
import mt.edu.um.mlrs.text.transform.TextNodeToXml;
import mt.edu.um.util.dict.TrieDictionary;
import mt.edu.um.util.io.FileFinder;
import mt.edu.um.util.io.FileUtils;
import mt.edu.um.util.io.SerialiseFiles;
import mt.edu.um.util.xml.DomUtils;

import org.w3c.dom.Document;

public class XmlCorpusFileEncoder {
	public static int MIN_SENTENCES = 5;
	public static String MAP_FILE = "res/spell/charmap2.mt.txt";
	public static String WORD_FILE = "/media/work/mlrs/speller/rcasha-speller/words.utf8.txt";
	// "A:\\mlrs\\speller\\rcasha-speller\\words.utf8.txt";

	private static final Pattern DATE = Pattern
			.compile(".*(\\d{4})\\.(\\d{1,2})\\.(\\d{1,2}).*");
	private TextDocumentToTextNode _docToNode;
	private TextNodeToXml _nodeToXml;
	private int _tokens;
	private int _skipped;
	private TextNode _header;

	public XmlCorpusFileEncoder() throws Exception {
		this._docToNode = new TextDocumentToTextNode();
		this._nodeToXml = new TextNodeToXml();
		SentenceSplitter ssplitter = new SentenceSplitter(new RegexTokeniser(
				MTRegex.TOKEN));
		LanguageClassifier classifier = new LanguageClassifier(
				"res/lm/lang-5gram.classifier");
		ssplitter.addClassifier(classifier);
		this._docToNode.addProcessor(ssplitter);
		Tokeniser tok = new RegexTokeniser(MTRegex.TOKEN);
		this._docToNode.addProcessor(tok);
		this._tokens = 0;
		this._skipped = 0;
	}

	public void addProcessor(TextNodeProcessor proc) {
		this._docToNode.addProcessor(proc);
	}

	public void addCharacterSubstitutionProcessor(String charFile)
			throws Exception {
		CharacterMapping mapping = CharacterMapping.fromFile(MAP_FILE, "UTF-8",
				"\t");
		Tokeniser tok = new RegexTokeniser(MTRegex.TOKEN);
		System.out.append("Loading dictionary...");
		long ms1 = System.currentTimeMillis();
		TrieDictionary dict = SerialiseFiles.deserialize(
				"res/spell/charmapping.mt.dict", TrieDictionary.class);
		long ms2 = System.currentTimeMillis();
		System.out.append(" done in " + (ms2 - ms1) + "ms\n");

		SpellChecker speller = new CharacterSubstitutionChecker(tok, dict,
				mapping);
		speller.setCaseSensitive(false);
		tok.setSpellChecker(speller);
		tok.setKeepEmptyTokens(false);
		this._docToNode.addProcessor(tok);
	}

	public boolean encodeWithHeader(File in, File out, String sep)
			throws Exception {
		List<String> allStrings = FileUtils.readLinesFromFile(in, "UTF-8");
		TextNode header = new TextNode("HEADER");
		ListIterator<String> iter = allStrings.listIterator();
		int i = 0;
		
		while (iter.hasNext()) {
			String s = iter.next();
			i++;
			if (s != null) {
				if (s.matches(sep)) {				
					break;
				}
				
				String[] fields =s.split(":");
				String type = fields[0];
				String val = fields[1];
				
				if (type.contains("-")) {
					String[] subs = s.split("-");

					if (subs.length == 2) {
						TextNode child = new TextNode(subs[0], val);
						child.setValue("TYPE", subs[1]);
						header.addChild(child);
					} else {
						throw new RuntimeException(
								"Cannot have more than 2 subfields in headerline");
					}
				} else {
					TextNode child = new TextNode(type, val);
					header.addChild(child);
				}							
			}						
		}
		
		File temp = new File(in.getParent(), "temp.txt");
		FileUtils.writeLinesToFile(temp, allStrings.subList(i, allStrings.size()), "UTF-8");
		TextNode node = this._docToNode.transform(temp);
		boolean res;
		
		if (node.numChildren(true, "SENTENCE") < XmlCorpusFileEncoder.MIN_SENTENCES) {
			System.out.println("\t Skipping (too short)");
			this._skipped++;
			res = false;

		} else {
			node.addChild(0, header);
			this._tokens += node.numChildren(true, "TOKEN");
			Document xml = this._nodeToXml.transform(node);
			DomUtils.writeDocument(xml, out);
			res = true;
		}

		return res;
	}

	public boolean encode(File in, File out) throws Exception {

		TextNode node = this._docToNode.transform(in);
		boolean res;

		if (node.numChildren(true, "SENTENCE") < XmlCorpusFileEncoder.MIN_SENTENCES) {
			System.out.println("\t Skipping (too short)");
			this._skipped++;
			res = false;

		} else {

			if (this._header != null) {
				node.addChild(0, this._header);
				// TextNode s1 = node.firstChild("SENTENCE");
				//
				// if (s1 != null && s1.hasContent()) {
				// TextNode title = new TextNode("TITLE", s1.getContent());
				// TextNode tempHeader= this._header.copy();
				// tempHeader.addChild(title);
				// node.addChild(0, tempHeader);
				// s1.delete();
				// } else {
				// node.addChild(0, this._header);
				// }
			}

			this._tokens += node.numChildren(true, "TOKEN");
			Document xml = this._nodeToXml.transform(node);
			DomUtils.writeDocument(xml, out);
			res = true;
		}

		return res;
	}

	public static void main(String[] args) throws Exception {
		XmlCorpusFileEncoder encoder = new XmlCorpusFileEncoder();
		String indir = args[0];
		String outdir = args[1];
		String prefix = args[2];
		// String headerFile = args[3];
		int num = Integer.parseInt(args[3]);

		// locate the files in directory
		System.out.append("FINDING FILES...");
		List<File> files = FileFinder.findFiles(indir, ".txt");
		System.out.append(files.size() + "\n");

		for (File in : files) {

			// String name = in.getName();
			// Matcher nameMatcher = Pattern
			// .compile(
			// "maltarightnow_(\\d+)\\.(\\d+)\\.(\\d+)_\\d+-\\d+\\.html-cleaned\\.txt")
			// .matcher(name);
			// set up the header node
			// encoder.readHeader(headerFile);

			// if (nameMatcher.matches()) {
			// int yr = Integer.parseInt(nameMatcher.group(1));
			// int month = Integer.parseInt(nameMatcher.group(2));
			// int day = Integer.parseInt(nameMatcher.group(3));
			// // String t = nameMatcher.group(4);
			// Calendar c = Calendar.getInstance();
			// c.set(yr, month, day);
			// String date = c.getTime().toString();
			// TextNode pub = new TextNode("PUBLISHED", date);
			// // TextNode title = new TextNode("TITLE", t);
			// encoder._header.addChild(pub);
			// // encoder._header.addChild(title);
			// } else {
			// System.out.println("\t NO NAME MATCH");
			// }

			System.out.println(in.getAbsolutePath());
			String outfile = new StringBuffer(prefix).append(num)
					.append(".xml").toString();

			File out = new File(outdir, outfile);

			if (encoder.encodeWithHeader(in, out, "====")) {
				num++;
			}
		}

		System.out.println("TOTAL TOKENS: " + encoder._tokens);
		System.out.println("FILES SKIPPED: " + encoder._skipped);
	}

	public void readHeader(String filename) throws Exception {
		Map<String, String> lines = FileUtils.readFieldsFromFile(filename,
				"UTF-8", ":");
		this._header = new TextNode("HEADER");
		TextNode child;

		for (String s : lines.keySet()) {
			if (s.contains("-")) {
				String[] subs = s.split("-");

				if (subs.length == 2) {
					child = new TextNode(subs[0], lines.get(s));
					child.setValue("TYPE", subs[1]);
					this._header.addChild(child);
				} else {
					throw new RuntimeException(
							"Cannot have more than 2 subfields in headerline");
				}
			} else {
				child = new TextNode(s, lines.get(s));
				this._header.addChild(child);
			}
		}
	}

	@SuppressWarnings("unused")
	private TextNode header(String name) {
		TextNode header = new TextNode("HEADER");
		header.addChild(new TextNode("TITLE", "Maltarightnow.com"));
		TextNode url = new TextNode("IDENTIFIER",
				"http://www.maltarightnow.com");
		url.setValue("TYPE", "url");
		header.addChild(url);
		header.addChild(new TextNode("MEDIUM", "web version of press article"));
		header.addChild(new TextNode("COPYRIGHT", "Maltarightnow"));
		header.addChild(new TextNode("PUBLISHER", "Maltarightnow"));
		header.addChild(new TextNode("INFO",
				"Downloaded from the web. Contributed by user."));

		Matcher matcher = XmlCorpusFileEncoder.DATE.matcher(name);
		if (matcher.matches()) {
			String year = matcher.group(1);
			String month = matcher.group(2);
			String day = matcher.group(3);
			TextNode pub = new TextNode("PUBLISHED", day + "/" + month + "/"
					+ year);
			header.addChild(pub);
		}

		header.addChild(new TextNode("ADDED", "19 April 2011"));
		return header;
	}

}
