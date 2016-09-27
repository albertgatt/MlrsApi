package mt.edu.um.mlrs.runnables;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import mt.edu.um.mlrs.classify.lemma.Lemmatiser;
import mt.edu.um.mlrs.lexicon.JSONLexicon;
import mt.edu.um.mlrs.split.MTRegex;
import mt.edu.um.mlrs.split.RegexTokeniser;
import mt.edu.um.mlrs.split.SentenceSplitter;
import mt.edu.um.mlrs.split.Tokeniser;
import mt.edu.um.mlrs.text.TextNode;
import mt.edu.um.mlrs.text.transform.PdfToTextNode;
import mt.edu.um.mlrs.text.transform.TextDocumentToTextNode;
import mt.edu.um.mlrs.text.transform.TextNodeToXml;
import mt.edu.um.mlrs.text.transform.XmlToTextNode;
import mt.edu.um.mlrs.text.transform.cwb.TextNodeToCwb;
import mt.edu.um.util.CollectArgs;
import mt.edu.um.util.io.FileUtils;
import mt.edu.um.util.xml.DomUtils;

import org.w3c.dom.Document;

public class CreateCorpusFiles {

	static String HEADER_SEP = "====";
	static String HEADER_SUBSEP = "|";

	static String MODE_ARG = "-m";
	static String XML_MODE = "xml";
	static String XCWB_MODE = "xcwb";
	static String TCWB_MODE = "tcwb";
	static String PDF_MODE = "pdf";
	static String LEMMA_MODE = "lemma";
	static String IN_ARG = "-i";
	static String OUT_ARG = "-o";
	static String OUT_PREFIX = "-p";
	static String OUT_INT = "-n";
	static String METADATA_FILE = "metadata.cwb";
	static String HEADER_REGEX = "^\\s*[\\w\\s]+:.+\\s*$";

	static String LEMM_URL = "http://mlrs.research.um.edu.mt/resources/gabra/ws/lemmatise.json?surface_form=";

	static TextDocumentToTextNode docToNode = new TextDocumentToTextNode();
	static XmlToTextNode xmlToNode = new XmlToTextNode();
	static TextDocumentToTextNode textToNode = new TextDocumentToTextNode();
	static PdfToTextNode pdfToNode = new PdfToTextNode();
	static TextNodeToXml nodeToXml = new TextNodeToXml();
	static TextNodeToCwb nodeToCwb = new TextNodeToCwb();
	static SentenceSplitter ssplitter = new SentenceSplitter(
			new RegexTokeniser(MTRegex.TOKEN));
	static Tokeniser tok = new RegexTokeniser(MTRegex.TOKEN);
	static Lemmatiser lemmatiser = new Lemmatiser(new JSONLexicon(LEMM_URL));

	static {
		docToNode.addProcessor(ssplitter);
		docToNode.addProcessor(tok);
		pdfToNode.addProcessor(ssplitter);
		pdfToNode.addProcessor(tok);
		textToNode.addProcessor(ssplitter);
		textToNode.addProcessor(tok);
	}

	static List<String> errors = new ArrayList<String>();

	public static void main(String[] args) throws Exception {
		Map<String, String> a = CollectArgs.getArgs(args);
		String mode = a.get(MODE_ARG);
		String in = a.get(IN_ARG);
		String out = a.get(OUT_ARG);
		String prefix = a.get(OUT_PREFIX);

		if (XML_MODE.equalsIgnoreCase(mode)) {
			int outnum = Integer.parseInt(a.get(OUT_INT));
			createXML(in, out, prefix, outnum);

		} else if (XCWB_MODE.equalsIgnoreCase(mode)) {
			createCWBFromXML(in, out, prefix);

		} else if (TCWB_MODE.equalsIgnoreCase(mode)) {
			createCWBFromText(in, out, prefix);

		} else if (PDF_MODE.equalsIgnoreCase(mode)) {
			int outnum = Integer.parseInt(a.get(OUT_INT));
			createTxtFromPdf(in, out, prefix, outnum);

		} else if (LEMMA_MODE.equalsIgnoreCase(mode)) {
			lemmatiseCorpus(in, out);

		} else {
			errors.add("Unknown or missing -m argument: " + mode);
		}

		if (errors.isEmpty()) {
			System.out.println("\tJava: " + mode
					+ "conversion completed successfully");
		} else {
			System.out.println("\tJava: " + mode
					+ " conversion encountered the following errors:");

			for (String s : errors) {
				System.out.println("\t\t" + s);
			}
		}
	}

	private static void lemmatiseCorpus(String indir, String outdir) {
		try {
			List<File> files = FileUtils.getAllFiles(new File(indir));

			for (File f : files) {
				Document xml = DomUtils.loadDocument(f);
				TextNode node = lemmatiser.process(xmlToNode.transform(xml));
				Document lemmatisedXML = nodeToXml.transform(node);
				File out = new File(outdir, f.getName());
				DomUtils.writeDocument(lemmatisedXML, out);
			}

		} catch (Exception e) {
			e.printStackTrace();
			errors.add(e.getClass() + ": " + e.getMessage());
		}
	}

	private static void createTxtFromPdf(String indir, String outdir,
			String prefix, int outnum) {

		try {
			List<File> files = FileUtils.getAllFiles(new File(indir));

			for (File f : files) {
				TextNode node = pdfToNode.transform(f);
				Document xml = nodeToXml.transform(node);
				File out = new File(outdir, prefix + outnum + ".xml");
				DomUtils.writeDocument(xml, out);
			}

		} catch (Exception e) {
			e.printStackTrace();
			errors.add(e.getClass() + ": " + e.getMessage());
		}

	}

	private static void createXML(String infile, String outdir, String prefix,
			int outnum) {
		File in = new File(infile);
		File out = new File(outdir, prefix + outnum + ".xml");

		// create XML version
		encodeXmlWithHeader(in, out, HEADER_SEP, HEADER_SUBSEP);
	}

	private static void createCWBFromText(String indir, String outdir,
			String prefix) throws Exception {
		File in = new File(indir);
		File out = new File(outdir);

		if (!in.isDirectory()) {
			errors.add("Check input directory for CWB conversion");
			return;
		}

		if (!out.isDirectory()) {
			errors.add("Check output directory for CWB conversion");
			return;
		}

		File cwbFile = new File(outdir, prefix + ".txt");
		BufferedWriter writer = null;
		Metadata md = new Metadata();
		md.setSubCorpus(prefix);
		TextNode node = null;
		TextNode header = null;
		String contents = null;
		List<File> files = null;
		List<String> headers = null;
		StringBuffer metabuffer = null;
		FileFilter filter = new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.getName().endsWith(".txt");
			}
		};

		try {
			writer = new BufferedWriter(new FileWriter(cwbFile));
			files = FileUtils.getAllFiles(in, filter);
			headers = new ArrayList<String>();
			metabuffer = new StringBuffer();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.exit(1);
		}

		for (File f : files) {
			try {
				System.err.println("\t" + f.getName());
				md.setFilename(FileUtils.getFileNameWithoutExt(f));
				node = CreateCorpusFiles.textToNode.transform(f);

				header = node.firstChild("HEADER");
				addMetadata(header, md);
				nodeToCwb.setCurrentID(FileUtils.getFileNameWithoutExt(f));
				contents = nodeToCwb.transform(node.firstChild("TEXT"));
				writer.write(contents);
				headers.add(md.toShortStringCWB());
				metabuffer.append(md.toShortStringCWB() + "\n");
			} catch (Exception e) {
				System.err.println("\t --Exception caught, file skipped");
				errors.add("--File " + f + " caused exception " + e.getClass()
						+ ": " + e.getMessage() + "\n");
				continue;

			}
		}

		
		writer.close();
		
		try {
			Files.write(Paths.get(outdir, METADATA_FILE), metabuffer.toString()
					.getBytes(), StandardOpenOption.APPEND);
		} catch (IOException e) {
			errors.add("IO Exception appending to metadata file: "
					+ e.getMessage());
			
		}

	}

	private static void createCWBFromXML(String indir, String outdir,
			String prefix) {
		File in = new File(indir);
		File out = new File(outdir);

		if (!in.isDirectory()) {
			errors.add("Check input directory for CWB conversion");
			return;
		}

		if (!out.isDirectory()) {
			errors.add("Check output directory for CWB conversion");
			return;
		}

		try {
			StringBuffer buffer = new StringBuffer();
			List<String> headers = new ArrayList<String>();
			StringBuffer metabuffer = new StringBuffer();

			List<File> files = FileUtils.getAllFiles(in);
			TextNode header;
			String contents;
			TextNode node;
			Document xml;

			for (File f : files) {
				System.err.println("\t" + f.getName());
				header = null;
				contents = null;
				node = null;
				xml = null;

				try {
					xml = DomUtils.loadDocument(f);
					node = xmlToNode.transform(xml);
					header = node.firstChild("HEADER");
					nodeToCwb.setCurrentID(FileUtils.getFileNameWithoutExt(f));
					contents = nodeToCwb.transform(node.firstChild("TEXT"));

				} catch (Exception e) {
					System.err.println("\t --Exception caught");
					errors.add("--File " + f + " caused exception "
							+ e.getClass() + ": " + e.getMessage() + "\n");
					continue;
				}

				buffer.append(contents);
				Metadata md = new Metadata();
				md.setFilename(FileUtils.getFileNameWithoutExt(f));
				md.setSubCorpus(prefix);
				addMetadata(header, md);

				headers.add(md.toShortStringCWB());
				metabuffer.append(md.toShortStringCWB() + "\n");
			}

			try {
				Files.write(Paths.get(outdir, METADATA_FILE), metabuffer
						.toString().getBytes(), StandardOpenOption.APPEND);
			} catch (IOException e) {
				errors.add("IO Exception appending to metadata file: "
						+ e.getMessage());
			}

			// File mdFile = new File(outdir, METADATA_FILE);
			// FileUtils.writeLinesToFile(mdFile, headers, "UTF-8", "\n");
			File cwbFile = new File(outdir, prefix + ".txt");
			FileUtils.writeStringToFile(cwbFile, buffer.toString(), "UTF-8");

		} catch (Exception e) {
			errors.add(e.getClass() + ": " + e.getMessage());
		}
	}

	private static void addMetadata(TextNode header, Metadata data) {
		Iterator<TextNode> iter = header.preorder();

		while (iter.hasNext()) {
			TextNode child = iter.next();
			String node = child.getType();
			String content = child.getContent();
			if (node.equalsIgnoreCase("AUTHOR"))
				data.setAuthor(content);
			else if (node.equalsIgnoreCase("TITLE"))
				data.setTitle(content);
			else if (node.equalsIgnoreCase("TOPIC"))
				data.setTopic(content);
			else if (node.equalsIgnoreCase("PUBLISHER"))
				data.setPublisher(content);
			else if (node.equalsIgnoreCase("PUBLISHED"))
				data.setPublished(content);
			else if (node.equalsIgnoreCase("COPYRIGHT"))
				data.setCopyright(content);
			else if (node.equalsIgnoreCase("ADDED"))
				data.setAdded(content);
		}
	}

	private static void encodeXmlWithHeader(File in, File out, String sep,
			String subsep) {

		try {
			TextNode node = CreateCorpusFiles.textToNode.transform(in);

			// write xml file
			Document xml = nodeToXml.transform(node);
			DomUtils.writeDocument(xml, out);

		} catch (Exception e) {
			e.printStackTrace();
			errors.add(e.getMessage());
		}
	}

}
