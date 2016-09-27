package mt.edu.um.mlrs.runnables;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtractMetadata {

	/*
	 * CHANGES REQUIRED: Initial variables need to be changed according to API
	 * Method getHeader() needs to be updated to utilise the textnode & XML
	 * structure metadata datastructure might result to be obsolete afterwards -
	 * if YES: then it might be worthwhile creating a method similar to
	 * toShortStringCWB() to obtain some of the important elements for the
	 * metadata file for CWB from the actual API datastructure once this is
	 * integrated. The ArrayList metadataList could easily become of Type
	 * <String>
	 */

	// VARIABLES:
	/************************ CHANGEABLE VARIABLES *************************/
	private static final int MAXSIZE = 10000000;
	private static final String FILEEXTENSION = ".txt";
	/*
	 * Folder where we will find the different folders containing the corpus,
	 * i.e.: ./academic ./law ... and the endFolderName where we will re-write
	 * the corpus in CWB format
	 * 
	 * The code can handle both windows and unix file strings :) should!
	 */
	private static String startFolderName = "/home/maintain/corpus/level2-mt/";
	private static String endFolderName = "/home/maintain/corpus/CWBFormat/";

	// private static String startFolderName =
	// "/Users/claudiaborg/Documents/Work/University/Server/level2/";
	// private static String endFolderName =
	// "/Users/claudiaborg/Documents/Work/University/Server/CWBtest/";
	/*
	 * Boolean: Should the output be created in the similar subdirectory
	 * structure? true = create ./academic, ./law, ect for the new files under
	 * the endFolderName false = dump all files directly under endFolderName to
	 * make it easier to upload to CWB
	 * 
	 * We will still have the subcorpus information in the metadata available so
	 * this is not important to CWB
	 */
	private static Boolean subDirectories = false;

	// Fielname for Metadata to write final output to.
	private static String metadataFilename = "metadata.txt";

	/********************** NON CHANGEABLE VARIABLES *************************/
	// ArrayList storing paths to each of the subfolders in the main
	// startFolderName
	private static ArrayList<File> subfolders;

	// ArrayList to store metadata for each file
	private static ArrayList<Metadata> metadataList;

	// MAIN

	/*
	 * ExtractMetadata.java Main Method The purpose of this programme is to 1.
	 * Transform XML files into the required CWB vertical format 2. Extract the
	 * header info and place it into the required CWB metadata format
	 */
	public static void main(String[] args) {
		// Initialise Variables
		metadataList = new ArrayList<Metadata>();

		// Main Directory
		File startDirectory;
		File endDirectory;

		if (args.length == 0) {			
			startDirectory = new File(startFolderName);
			endDirectory = new File(endFolderName);
	
		} else {
			startDirectory = new File(args[0]);
			endDirectory = new File(args[1]);
		}

		// 1. check that startFolderName & endFolderName are actually
		// directories
		if (!startDirectory.isDirectory()) {
			System.out
					.println("Error: Please recheck folder startFolderName - "
							+ startFolderName);
			System.exit(0);
		}

		if (!endDirectory.exists()) {
			endDirectory.mkdirs();
		}

		// 2. get list of subfolders only.
		subfolders = new ArrayList<File>();
		String[] tempsubfolders = startDirectory.list();
		File temp = null;

		for (String s : tempsubfolders) {
			temp = new File(startFolderName + s);
			if (temp.isDirectory())
				subfolders.add(temp);
		}

		subfolders.trimToSize();

		/*
		 * MAIN FUNCTION: 1. Iterate through each subfolder 2. Get a list of
		 * files, and iterate through them A. For each file, read it to memory
		 * B. Extract header and build metadata, append it to metadataList C.
		 * Fix file structure for CWB, and write out to a different folder/file
		 */

		String[] fileList = null;
		File currFile = null;
		StringBuffer allText = null;
		String fixedText = null;
		String headerText = null;
		String bodyText = null;
		String currSubcorpus = null;
		int sizeCount = 0;
		int fileCount = 0;
		Pattern pHeader = null;
		Matcher mHeader = null;

		// Iterate subfolders
		for (File s : subfolders) {
			// get name of current subCorpus
			currSubcorpus = s.getName();
			sizeCount = 0;
			fileCount = 0;
			checkAndDelete(currSubcorpus, fileCount);
			// get file list
			fileList = s.list();

			// Iterate filelist
			for (String f : fileList) {
				/******************** PART A - READ FILE *********************/
				// Read File
				currFile = new File(s + File.separator + f);
				allText = readFiletoStringBuffer(currFile);

				/********************* PART B - EXTRACT HEADER INFO **********************/
				// Extract Header - Use the same Regex to extract then delete
				// the header
				pHeader = Pattern.compile(".*?</HEADER>");
				mHeader = pHeader.matcher(allText);
				if (mHeader.find())
					headerText = mHeader.group().trim();
				else
					System.out.println("Potential Error in Part B with file: "
							+ currFile.toString());

				/*
				 * CALLING here getHeader, which would need to be updated if
				 * datastructure is fixed.
				 */
				metadataList.add(getHeader(headerText, f, currSubcorpus));

				// removing header part from mainText, and placing it in
				// bodyText
				bodyText = mHeader.replaceFirst("");

				/********************* PART C _ FIX FILE FORMAT FOR CWB **********************/
				// Fix format of mainText
				fixedText = fixFormat(bodyText, f);
				sizeCount += fixedText.length();
				appendDataToFile(endDirectory, fixedText, currSubcorpus, fileCount);

				if (sizeCount > MAXSIZE) {
					/*
					 * increment fileCount so that next time round, we write to
					 * a new file BUT!!! check for the existance of the new
					 * file, and if it exists DELETE it because we want to use
					 * the append function!
					 */
					fileCount++;
					sizeCount = 0;
					checkAndDelete(currSubcorpus, fileCount);
				}

			} // end filelist iteration
		}// end folder list iteration

		// write metadata to file
		writeMetaData(endDirectory);
	}

	private static void appendDataToFile(File outdir, String fixedText, String subCorpus,
			int fileCount) {
		// We know the file exists, we just want to open it and append fixedText
		// to it
		String fileName = "";
		try {
			File currFile = null;
			
			if (subDirectories == true)
				currFile = new File(outdir, endFolderName + subCorpus + File.separator
						+ subCorpus + fileCount + FILEEXTENSION);
			
			else
				currFile = new File(outdir, endFolderName + File.separator + subCorpus
						+ fileCount + FILEEXTENSION);

			FileWriter fstream = new FileWriter(currFile, true);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(fixedText);
			// Close the output stream
			out.close();
		
		} catch (Exception e) {// Catch exception if any
			System.out.println("Error writing to file" + fileName + ": "
					+ e.getMessage());
			e.printStackTrace();
		}
	}

	private static void checkAndDelete(String subCorpus, int filenumber) {
		String fileName = "";
		try {
			File currFile = null;
			if (subDirectories == true)
				currFile = new File(endFolderName + subCorpus + File.separator
						+ subCorpus + filenumber + FILEEXTENSION);
			else
				currFile = new File(endFolderName + File.separator + subCorpus
						+ filenumber + FILEEXTENSION);
			if (currFile.exists()) {
				currFile.delete();
			}
			currFile.createNewFile();
		} catch (Exception e) {// Catch exception if any
			System.out.println("Error when checking/deleting existance of "
					+ fileName + ": " + e.getMessage());
			e.printStackTrace();
		}
	}

	/*
	 * private static Boolean writeDataToFile(String subCorpus, int filenumber,
	 * StringBuffer data){ String fileName = ""; try { // Create directory File
	 * currDir = null; if(subDirectories == true) currDir = new
	 * File(endFolderName + subCorpus); else currDir = new File(endFolderName);
	 * if (!currDir.exists()) currDir.mkdirs(); fileName = subCorpus +
	 * filenumber + ".txt"; File currFile = new File(currDir.toString() +
	 * File.separator + fileName); FileWriter fstream = new
	 * FileWriter(currFile); BufferedWriter out = new BufferedWriter(fstream);
	 * out.write(data.toString()); //Close the output stream out.close(); return
	 * true; }catch (Exception e){//Catch exception if any
	 * System.out.println("Error writing to file" + fileName + ": " +
	 * e.getMessage()); e.printStackTrace(); return false; } }
	 */
	private static void writeMetaData(File outdir) {
		File metaFile = new File(outdir, metadataFilename);
		
		try {
			FileWriter fstream = new FileWriter(metaFile);
			BufferedWriter out = new BufferedWriter(fstream);
		
			for (Metadata m : metadataList) {
				out.write(m.toShortStringCWB() + "\n");
			}
			// Close the output stream
			out.close();
		
		} catch (Exception e) {// Catch exception if any
			System.out.println("Error writing to file" + metaFile.toString()
					+ ": " + e.getMessage());
		}
	}

	private static String fixFormat(String mainText, String filename) {

		String mainBody = mainText;

		/******************** SOME REMOVAL TRANSFORMATIONS ******************/
		mainBody = mainBody.replaceAll("</LIST-ITEM>", "");
		mainBody = mainBody.replaceAll("<LIST-ITEM.*?>", "");
		mainBody = mainBody.replaceAll("</LIST>", "");
		mainBody = mainBody.replaceAll("<LIST>", "");
		mainBody = mainBody.replaceAll("<PARAGRAPH/>", "");
		mainBody = mainBody.replaceAll("</SECTION>", "");
		mainBody = mainBody.replaceAll("<SECTION>", "");
		mainBody = mainBody.replaceAll("<TURN.*?>", "");
		mainBody = mainBody.replaceAll("</TURN>", "");
		mainBody = mainBody.replaceAll("<HEADING>", "<PARAGRAPH><SENTENCE>");
		mainBody = mainBody.replaceAll("</HEADING>", "</SENTENCE></PARAGRAPH>");
		mainBody = mainBody.replaceAll("<BODY>", "<TEXT>");
		mainBody = mainBody.replaceAll("</BODY>", "</TEXT>");

		/************************ TRANSFORMATIONS TO mainBody ********************/

		// to remove some extra tabs & spaces noted in some files - two or more
		String pTabs = "\\s{2,}";
		// to replace paragraph tag with <p>
		String pPara = "</PARAGRAPH><PARAGRAPH>";
		// to replace <SENTENCE> with <s> and <TOKEN> with newline:
		// \n</s>\n<s>\n
		String pSentLang = "</TOKEN></SENTENCE><SENTENCE.*?><TOKEN>";
		// to replace <TOKEN> with newline: \n
		String pTokens = "</TOKEN><TOKEN>";
		// to replace </SENTENCE> with </s> and </TOKEN> with newline: \n</s>
		String pTokSentEnds = "</TOKEN></SENTENCE>";
		// to replace <SENTENCE> with <s> and <TOKEN> with newline: <s>\n
		String pSentTokBegins = "<SENTENCE.*?><TOKEN>";

		Pattern currP = Pattern.compile(pTabs);
		Matcher currM = currP.matcher(mainBody);
		if (currM.find()) {
			mainBody = currM.replaceAll("");
		}

		currP = Pattern.compile(pPara);
		currM = currP.matcher(mainBody);
		if (currM.find()) {
			mainBody = currM.replaceAll("\n</p>\n<p>\n");
		}

		currP = Pattern.compile(pSentLang);
		currM = currP.matcher(mainBody);
		if (currM.find()) {
			mainBody = currM.replaceAll("\n</s>\n<s>\n");
		}

		currP = Pattern.compile(pTokens);
		currM = currP.matcher(mainBody);
		if (currM.find()) {
			mainBody = currM.replaceAll("\n");
		}

		currP = Pattern.compile(pTokSentEnds);
		currM = currP.matcher(mainBody);
		if (currM.find()) {
			mainBody = currM.replaceAll("\n</s>");
		}

		currP = Pattern.compile(pSentTokBegins);
		currM = currP.matcher(mainBody);
		if (currM.find()) {
			mainBody = currM.replaceAll("<s>\n");
		}

		/******************** TEXTID TRANSFORMATION **************/
		String textID = "<text id=\""
				+ filename.substring(0, filename.indexOf('.')).replaceAll("-",
						"_") + "\">\n<p>\n";
		int i = mainBody.indexOf("<s>");
		String rid = "";
		try {
			rid = mainBody.substring(0, i);
			// get rid of junk in the beginning and replace with textID
			mainBody = mainBody.replaceFirst(rid, textID);
			// get rid of junk at the end
			mainBody = mainBody.replace("</PARAGRAPH></TEXT></DOCUMENT>",
					"\n</p>\n</text>\n");
			// to deal with wiki documents:
			mainBody = mainBody.replace("</TEXT></DOCUMENT>",
					"\n</p>\n</text>\n");
			mainBody = mainBody.replaceAll("</PARAGRAPH>", "\n</p>\n<p>\n");
			mainBody = mainBody.replaceAll("<PARAGRAPH>", "\n</p>\n<p>\n");

		} catch (StringIndexOutOfBoundsException e) {
			// OH DEAR! This must be an empy file!
			System.out.println("It is probable that the file " + filename
					+ " is empty of content!");
			mainBody = mainBody.replaceAll("<TEXT></TEXT></DOCUMENT>", textID
					+ "</p>\n</text>\n");
		}

		/******************** WRITE TO FILE ***********************/
		/*
		 * String fileName = filename.substring(0, filename.indexOf('.')) +
		 * ".txt"; try { // Create directory File currDir = null; if(subfolders
		 * == true) currDir = new File(endFolderName + subcorpus); else currDir
		 * = new File(endFolderName); if (!currDir.exists()) currDir.mkdirs();
		 * File currFile = new File(currDir.toString() + File.separator +
		 * fileName); FileWriter fstream = new FileWriter(currFile);
		 * BufferedWriter out = new BufferedWriter(fstream);
		 * out.write(mainBody); //Close the output stream out.close(); }catch
		 * (Exception e){//Catch exception if any
		 * System.err.println("Error writing to file" + fileName + ": " +
		 * e.getMessage()); e.printStackTrace(); }
		 */
		return mainBody;
	}

	public static Metadata getHeader(String mainHeader, String filename,
			String subcorpus) {
		/*
		 * FUTURE WORK XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX USE
		 * APPROPRIATE DTD & Structure to load everything immediately
		 */

		String mHeader = mainHeader;
		Metadata md = new Metadata();
		String fileName = filename.substring(0, filename.indexOf('.'))
				.replaceAll("-", "_");
		md.setFilename(fileName);
		md.setSubCorpus(subcorpus);

		String pTitle = "<TITLE>(.*?)</TITLE>";
		String pTopic = "<TOPIC>(.*?)</TOPIC>";

		Pattern currP = Pattern.compile(pTitle);
		Matcher currM = currP.matcher(mHeader);
		if (currM.find()) {
			md.setTitle(currM.group(1).trim());
		}

		currP = Pattern.compile(pTopic);
		currM = currP.matcher(mHeader);
		if (currM.find()) {
			md.setTopic(currM.group(1).trim());
		}

		/*******************
		 * 
		 * BLOODY OVERKILL RE-IMPLEMENT STUFF WITH PROPER XML EXTRACTION
		 * 
		 * 
		 ********************/

		String pIdentifier = "<IDENTIFIER>(.*?)</IDENTIFIER>";
		String pPublisher = "<PUBLISHER>(.*?)</PUBLISHER>";
		String pAuthor = "<AUTHOR>(.*?)</AUTHOR>";
		String pCopyright = "<COPYRIGHT>(.*?)</COPYRIGHT>";
		String pPublished = "<PUBLISHED>(.*?)</PUBLISHED>";
		String pAdded = "<ADDED>(.*?)</ADDED>";
		String pInfo = "<INFO>(.*?)</INFO>";
		String pType = "<TYPE>(.*?)</TYPE>";

		currP = Pattern.compile(pIdentifier);
		currM = currP.matcher(mHeader);
		if (currM.find()) {
			md.setIdentifier(currM.group(1).trim());
		}

		currP = Pattern.compile(pPublisher);
		currM = currP.matcher(mHeader);
		if (currM.find()) {
			md.setPublisher(currM.group(1).trim());
		}

		currP = Pattern.compile(pAuthor);
		currM = currP.matcher(mHeader);
		if (currM.find()) {
			md.setAuthor(currM.group(1).trim());
		}

		currP = Pattern.compile(pCopyright);
		currM = currP.matcher(mHeader);
		if (currM.find()) {
			md.setCopyright(currM.group(1).trim());
		}

		currP = Pattern.compile(pPublished);
		currM = currP.matcher(mHeader);
		if (currM.find()) {
			md.setPublished(currM.group(1).trim());
		}

		currP = Pattern.compile(pAdded);
		currM = currP.matcher(mHeader);
		if (currM.find()) {
			md.setAdded(currM.group(1).trim());
		}

		currP = Pattern.compile(pInfo);
		currM = currP.matcher(mHeader);
		if (currM.find()) {
			md.setInfo(currM.group(1).trim());
		}

		currP = Pattern.compile(pType);
		currM = currP.matcher(mHeader);
		if (currM.find()) {
			md.setType(currM.group(1).trim());
		}
		return md;
	}

	/*
	 * Method readFiletoStringBuffer opens the File f and appends its content to
	 * a StringBuffer that is returned to the calling method.
	 * 
	 * @params File File to be read
	 * 
	 * @return StringBuffer containing the all the text found in the file
	 */
	private static StringBuffer readFiletoStringBuffer(File f) {
		String line = "";
		StringBuffer BigText = new StringBuffer();
		try {
			FileInputStream fin = new FileInputStream(f);
			BufferedReader in = new BufferedReader(new InputStreamReader(fin,
					"utf-8"));
			if (!in.ready())
				throw new IOException();
			while ((line = in.readLine()) != null) {
				BigText.append(" " + line + " ");
			}
			in.close();
		} catch (Exception e) {
			System.out.println("Error reading from File " + f.toString()
					+ " to StringBuffer");
			e.printStackTrace();
			System.exit(-1);
		}
		return BigText;
	}
}
