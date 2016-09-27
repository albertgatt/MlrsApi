package mt.edu.um.mlrs.text.transform;

import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import mt.edu.um.mlrs.exception.TransformerException;
import mt.edu.um.mlrs.text.TextNode;

import org.apache.commons.lang.StringUtils;
import org.pdfbox.pdfparser.PDFParser;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.pdmodel.PDDocumentInformation;
import org.pdfbox.pdmodel.PDPage;
import org.pdfbox.pdmodel.encryption.AccessPermission;
import org.pdfbox.util.PDFTextStripper;
import org.pdfbox.util.PDFTextStripperByArea;

public class PdfToTextNode extends TextTransformer<File, TextNode> {

	/**
	 * 
	 * @author Albert Gatt
	 * 
	 */
	private class PDFStripper extends PDFTextStripper {

		private List<String> _paragraphs;

		public PDFStripper() throws IOException {
			super();
			super.setSortByPosition(true);
			super.setShouldSeparateByBeads(true);
			this._paragraphs = new ArrayList<String>();
		}

		@Override
		public void startParagraph() {
			this.output = new StringWriter();
		}

		@Override
		public void endParagraph() {
			String parText = ((StringWriter) getOutput()).toString();
			this._paragraphs.add(parText);
		}

		List<String> getParagraphs() {
			return this._paragraphs;
		}
	}

	private int startPage;

	private PdfTextRegion region;

	public PdfToTextNode() {
		super();
		this.startPage = 0;
		this.region = PdfTextRegion.ALL;
	}

	public int getStartPage() {
		return startPage;
	}

	public void setStartPage(int startPage) {
		this.startPage = startPage;
	}

	public PdfTextRegion getRegion() {
		return region;
	}

	public void setRegion(PdfTextRegion region) {
		this.region = region;
	}

	@Override
	public TextNode transform(File file) throws TransformerException {
		FileInputStream inputStream = null;
		PDDocument document = null;
		TextNode node = null;
		List<String> paragraphs;

		try {
			inputStream = new FileInputStream(file);
			PDFParser parser = new PDFParser(inputStream);
			parser.parse();
			document = parser.getPDDocument();

			switch (this.region) {
			case ALL:
				paragraphs = extractAll(document);
				break;
			default:
				paragraphs = extractRegion(document, this.region);
				break;
			}

			// close everything
			inputStream.close();
			document.close();

			// join parags into a single text representation
			node = constructTextNode(document, paragraphs);

		} catch (Exception e) {
			throw new TransformerException("Could not read document.", e);
		}

		return node;
	}

	public List<String> extractRegion(PDDocument document, PdfTextRegion region)
			throws Exception {
		List<String> strings = new ArrayList<String>();

		try {
			PDFTextStripperByArea stripper = new PDFTextStripperByArea();
			stripper.setSortByPosition(true);
			@SuppressWarnings("unchecked")
			List<PDPage> pages = document.getDocumentCatalog().getAllPages();
			Rectangle rect;
			PDPage page;

			for (int i = 0; i < pages.size(); i++) {
				page = pages.get(i);

				if (i >= this.startPage) {
					rect = region.getRectangle(page);
					stripper.addRegion("region", rect);
					stripper.extractRegions(page);
					strings.add(stripper.getTextForRegion("region"));
				}
			}

			return strings;

		} catch (IOException ioe) {
			throw new TransformerException("Error reading document", ioe);
		}
	}

	private List<String> extractAll(PDDocument document) throws Exception {
		PDFStripper stripper = new PDFStripper();
		stripper.setStartPage(this.startPage);
		stripper.getText(document);
		List<String> paragraphs = stripper.getParagraphs();
		return paragraphs;
	}

	private TextNode constructTextNode(PDDocument document,
			List<String> paragraphs) {
		TextNode node = new TextNode("DOCUMENT");
		PDDocumentInformation info = document.getDocumentInformation();
		node.addChild(buildHeader(info));
		TextNode text = new TextNode("TEXT");
		node.addChild(text);

		for (String par : paragraphs) {
			par = StringUtils.cleanWithin(StringUtils.trim(par));
			TextNode parag = this.runProcessors(new TextNode("PARAGRAPH", par));
			text.addChild(parag);
		}

		return node;
	}

	private TextNode buildHeader(PDDocumentInformation info) {
		TextNode header = this.hasDefaultHeader() ? this.getDefaultHeader()
				: new TextNode("HEADER");
		String title = info.getTitle();
		String author = info.getAuthor();
		// Calendar creation = info.getCreationDate();
		String key = info.getKeywords();
		String subject = info.getSubject();

		if (title != null) {
			header.addChild(new TextNode("TITLE", title));
		}

		if (author != null) {
			header.addChild(new TextNode("AUTHOR", author));
			header.addChild(new TextNode("COPYRIGHT", author));
		}

		if (key != null) {
			header.addChild(new TextNode("KEYWORDS", key));
		}

		// if (creation != null) {
		// try {
		// String date = formatDate(creation);
		// header.addChild(new TextNode("DATE", date));
		// } catch (Exception e) {
		// ;// do nothing
		// }
		// }

		if (subject != null) {
			TextNode subj = new TextNode("TOPIC", subject);
			header.addChild(subj);
		}

		return header;
	}

	@SuppressWarnings("unused")
	private String formatDate(Calendar date) {
		String retval = null;
		if (date != null) {
			SimpleDateFormat formatter = new SimpleDateFormat();
			retval = formatter.format(date.getTime());
		}

		return retval;
	}

	@SuppressWarnings("unused")
	private boolean canExtract(PDDocument document) {
		boolean extract = false;

		try {
			// DecryptionMaterial decryptionMaterial = new
			// StandardDecryptionMaterial("");
			// document.openProtection(decryptionMaterial);
			AccessPermission ap = document.getCurrentAccessPermission();
			extract = ap.canExtractContent();

		} catch (Exception e) {

		}

		return extract;
	}

}
