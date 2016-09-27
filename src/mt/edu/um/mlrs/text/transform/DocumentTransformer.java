package mt.edu.um.mlrs.text.transform;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import mt.edu.um.mlrs.classify.Classifier;
import mt.edu.um.mlrs.classify.lang.LanguageClassifier;
import mt.edu.um.mlrs.exception.TransformerException;
import mt.edu.um.mlrs.split.MTRegex;
import mt.edu.um.mlrs.split.RegexTokeniser;
import mt.edu.um.mlrs.split.SentenceSplitter;
import mt.edu.um.mlrs.split.Tokeniser;
import mt.edu.um.mlrs.text.TextNode;
import mt.edu.um.util.CollectArgs;
import mt.edu.um.util.io.FileFinder;
import mt.edu.um.util.io.FileUtils;
import mt.edu.um.util.xml.DomUtils;

import org.apache.poi.hwpf.HWPFDocument;
import org.w3c.dom.Document;

public class DocumentTransformer {
	private MSWordToTextNode wordToNode;
	private PdfToTextNode pdfToNode;
	private TextNodeToXml nodeToXml;
	private SentenceSplitter splitter;
	private Tokeniser tokeniser;
	private Classifier classifier;

	public static void main(String[] args) throws Exception {
		Map<String, String> allArgs = CollectArgs.getArgs(args);
		System.err.println(allArgs);
		DocumentTransformer trans = new DocumentTransformer();
		List<File> inputFiles;
		File outputDir;
		PdfTextRegion region = null;
		TextNode header = null;
		Collection<String> urlList = null;

		if (allArgs.containsKey("-r")) {
			region = PdfTextRegion.valueOf(allArgs.get("-r"));
			trans.setRegion(region);
		}

		if (allArgs.containsKey("-c")) {
			trans.setClassifier(new LanguageClassifier(allArgs.get("-c")));
		}

		if (allArgs.containsKey("-i")) {
			inputFiles = FileFinder.findFiles(allArgs.get("-i"));

		} else {
			throw new RuntimeException("No input directory specified");
		}

		if (allArgs.containsKey("-o")) {
			outputDir = new File(allArgs.get("-o"));

		} else {
			throw new RuntimeException("No output directory specified");
		}

		if (allArgs.containsKey("-page")) {
			trans.setStartPage(Integer.parseInt(allArgs.get("-page")));
		}

		if (allArgs.containsKey("-h")) {
			header = buildHeader(allArgs.get("-h"));
		}

		if (allArgs.containsKey("-u")) {
			urlList = FileUtils.readFieldsFromFile(allArgs.get("-u"), "UTF-8",
					" ").keySet();
		}

		String outFilePrefix = allArgs.containsKey("-p") ? allArgs.get("-p")
				: "file";
		int counter = allArgs.containsKey("-counter") ? Integer
				.parseInt(allArgs.get("-counter")) : 1;

		for (File f : inputFiles) {
			File outFile = new File(outputDir, outFilePrefix + counter + ".xml");
			System.err.append("Processing: " + f.getAbsolutePath() + " to "
					+ outFile + "...");

			if (trans.transform(f, outFile, region, header, getUrl(f.getName(),
					urlList))) {
				System.err.append("success\n");

			} else {
				System.err.append("failure\n");
			}

			counter++;
		}
	}

	private static String getUrl(String filename, Collection<String> urls) {
		filename = filename.replaceAll(" ", "%20");
		
		if (urls != null) {
			for (String url : urls) {
				if (url.endsWith(filename)) {
					return url;
				}
			}
		}
		
		return null;
	}

	private static TextNode buildHeader(String file) throws Exception {
		Map<String, String> fields = FileUtils.readFieldsFromFile(file,
				"UTF-8", ":");
		TextNode node = new TextNode("HEADER");

		for (String f : fields.keySet()) {
			node.addChild(new TextNode(f, fields.get(f)));
		}

		return node;

	}

	public DocumentTransformer() {
		this.wordToNode = new MSWordToTextNode();
		this.pdfToNode = new PdfToTextNode();
		this.nodeToXml = new TextNodeToXml();
		this.tokeniser = new RegexTokeniser(MTRegex.TOKEN);
		this.splitter = new SentenceSplitter(this.tokeniser);
		this.wordToNode.addProcessor(this.splitter);
		this.pdfToNode.addProcessor(this.splitter);
	}

	public void setStartPage(int page) {
		this.pdfToNode.setStartPage(page);
	}

	public void setClassifier(Classifier classifier) {
		this.classifier = classifier;
	}

	public void setRegion(PdfTextRegion region) {
		this.pdfToNode.setRegion(region);
	}

	public boolean transform(File f, File out, PdfTextRegion region,
			TextNode header, String url) throws TransformerException {
		String path = f.getAbsolutePath();
		TextNode result = null;
		TextNode thisHeader;
		
		if (path.endsWith(".doc") || path.endsWith(".DOC")) {
			result = this.transformDoc(f);

		} else if (path.endsWith(".pdf") || path.endsWith(".PDF")) {
			result = this.transformPDF(f);
		}

		// } else if (path.endsWith(".txt") || path.endsWith(".TXT")) {
		// result = this.transformTxt(f);

		if (result != null) {

			if (header != null) {
				thisHeader = header.copy();
				
				if (url != null) {
					TextNode id = new TextNode("IDENTIFIER", url);
					id.setValue("TYPE", "url");
					thisHeader.addChild(id);
				}

				result.addChild(0, thisHeader);

			} else if (url != null) {
				TextNode id = new TextNode("IDENTIFIER", url);
				id.setValue("TYPE", "url");
				thisHeader = new TextNode("HEADER");
				thisHeader.addChild(id);
				result.addChild(0, thisHeader);
			}

			Document doc = this.nodeToXml.transform(result);

			try {
				DomUtils.writeDocument(doc, out);

			} catch (Exception e) {
				throw new TransformerException("Error writing xml file", e);
			}

			return true;
		}

		return false;
	}

	public TextNode transformPDF(File f) throws TransformerException {
		return this.pdfToNode.transform(f);
	}

	// public TextNode transformTxt(File f) throws Exception {
	// List<String> lines = FileUtils.readLinesFromFile(f, "UTF-8");
	// TextNode doc = new TextNode("DOCUMENT");
	// TextNode header = header(lines.get(0));
	// TextNode author = new TextNode("AUTHOR", lines.get(1));
	// header.addChild(author);
	// header.addChild(new TextNode("COPYRIGHT", lines.get(1)));
	// doc.addChild(header);
	// TextNode text = new TextNode("TEXT");
	// doc.addChild(text);
	//
	// for (int i = 2; i < lines.size(); i++) {
	// String nextLine = StringUtils.cleanWithin(lines.get(i));
	//
	// if (!(StringUtils.isWhitespace(nextLine) || StringUtils
	// .isEmpty(nextLine))) {
	// nextLine = Translate.decode(nextLine);
	//
	// TextNode paragraphNode = runProcessors(new TextNode(
	// "PARAGRAPH", nextLine));
	//
	// for (TextNode s : paragraphNode.getChildren(true, "SENTENCE")) {
	// String lang = classifier.classify(s.getContent());
	// s.setValue("LANG", lang);
	// }
	//
	// text.addChild(paragraphNode);
	// }
	//
	// }
	//
	// return doc;
	// }

	public TextNode transformDoc(File file) throws TransformerException {
		try {
			return this.wordToNode.transform(new HWPFDocument(
					new FileInputStream(file)));
		} catch (IOException ioe) {
			throw new TransformerException("Error reading file", ioe);
		}
	}

	public TextNode header(String title) {
		TextNode header = new TextNode("HEADER");
		header.addChild(new TextNode("TITLE", title));
		header.addChild(new TextNode("PUBLISHER", "unknown"));
		// header.addChild("AUTHOR", "Trevor Żahra");
		// header.addChild("COPYRIGHT", "Trevor Żahra");
		// TextNode ident = new TextNode("IDENTIFIER", getURL(filename));
		// ident.setValue("TYPE", "url");
		// header.addChild(ident);
		header.addChild(new TextNode("ADDED", new Date().toString()));
		header.addChild("TYPE", "Document; fiction");
		header.addChild(new TextNode("MEDIUM", "document"));
		header.addChild("INFO", "User-contributed document.");
		return header;
	}

}
