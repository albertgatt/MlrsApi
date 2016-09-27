package mt.edu.um.mlrs.text.transform.cwb;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import mt.edu.um.mlrs.text.TextNode;
import mt.edu.um.mlrs.text.transform.XmlToTextNode;
import mt.edu.um.util.io.FileFinder;
import mt.edu.um.util.io.FileUtils;
import mt.edu.um.util.xml.DomUtils;

public class MetadataExtractor {

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
	private static String startFolderName = //"A:\\mlrs\\corpus-local\\filesToAdd\\level2\\religion"; 
		"/media/work/mlrs/corpus-local/mlrs.v2/level2.xml/literature";
		
	private static String endFolderName = "/media/work/mlrs/corpus-local/mlrs.v2/cwb.temp";
	// "A:\\mlrs\\corpus-local\\mlrs.v2\\cwb-temp";
	private static String ignoreList = "A:\\mlrs\\corpus\\filesToAdd\\slavomir.ceplo\\duplicates.txt";
	private static String errors = "errors.txt"; 
			//"A:\\mlrs\\corpus-local\\mlrs.v2\\errors\\xml-to-cwb-religion.err.27-10-2011.txt";
	private static String subcorpus = "literature";
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

	// transformer from xml to canonical rep
	private static XmlToTextNode _xml2Node = new XmlToTextNode();

	// transformer from canonical rep to CWB string
	private static TextNodeToCwb _node2Cwb = new TextNodeToCwb();

	// files to ignore (duplicates)
	private static List<String> ignore = new ArrayList<String>();

	// MAIN

	/**
	 * ExtractMetadata.java Main Method The purpose of this programme is to 1.
	 * Transform XML files into the required CWB vertical format 2. Extract the
	 * header info and place it into the required CWB metadata format
	 */
	public static void main(String[] args) throws Exception {
		// error file (for xml errors)
		BufferedWriter errorFile = FileUtils.getBufferedWriter(new File(MetadataExtractor.errors), "UTF-8");

		// files to ignore
		// ExtractMetadata.ignore =
		// FileUtils.readLinesFromFile(ExtractMetadata.ignoreList, "UTF-8");

		// Initialise Variables
		MetadataExtractor.metadataList = new ArrayList<Metadata>();
		List<File> fileList = FileFinder.findFiles(
				MetadataExtractor.startFolderName, ".xml");
		int sizeCount = 0;
		int fileCount = 0;

		// Iterate filelist
		for (File f : fileList) {
			System.err.append(f.getName() + "...");

			// ignore these files
			// if (ignore.contains(f.getName())) {
			// System.err.append("ignoring\n");
			// continue;
			// }

			try {
				TextNode node = _xml2Node.transform(DomUtils.loadDocument(f));
				_node2Cwb.setCurrentID(f.getName().replaceAll(".xml", "")
						.replaceAll("-", "_"));
				String cwbString = _node2Cwb.transform(node);

				if (cwbString == null) {
					System.err.println("skipped");
					continue;
				}

				MetadataExtractor.metadataList.add(Metadata.fromNode(
						node.firstChild("HEADER"), f.getName(), subcorpus));
				sizeCount += cwbString.length();
				MetadataExtractor.appendDataToFile(cwbString, subcorpus,
						fileCount);

				if (sizeCount > MetadataExtractor.MAXSIZE) {
					/*
					 * increment fileCount so that next time round, we write to
					 * a new file BUT!!! check for the existance of the new
					 * file, and if it exists DELETE it because we want to use
					 * the append function!
					 */
					fileCount++;
					sizeCount = 0;
					MetadataExtractor.checkAndDelete(subcorpus, fileCount);
				}

				System.err.println("done\n");

			} catch (Exception e) {
				System.err.append("error: " + e.getMessage() + "\n");
				errorFile.write(f + "\n");
			}
		} // end filelist iteration

		// write metadata to file
		MetadataExtractor.writeMetaData();
		System.out.println("PARS:" + _node2Cwb.getTotalPars());
		System.out.println("SENTS:" + _node2Cwb.getTotalSentences());
	}

	private static void appendDataToFile(String fixedText, String subCorpus,
			int fileCount) {
		// We know the file exists, we just want to open it and append fixedText
		// to it
		String fileName = "";
		try {
			File currFile = null;
			if (MetadataExtractor.subDirectories == true) {
				currFile = new File(MetadataExtractor.endFolderName + subCorpus
						+ File.separator + subCorpus + fileCount
						+ MetadataExtractor.FILEEXTENSION);
			} else {
				currFile = new File(MetadataExtractor.endFolderName
						+ File.separator + subCorpus + fileCount
						+ MetadataExtractor.FILEEXTENSION);
			}

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
			if (MetadataExtractor.subDirectories == true) {
				currFile = new File(MetadataExtractor.endFolderName + subCorpus
						+ File.separator + subCorpus + filenumber
						+ MetadataExtractor.FILEEXTENSION);
			} else {
				currFile = new File(MetadataExtractor.endFolderName
						+ File.separator + subCorpus + filenumber
						+ MetadataExtractor.FILEEXTENSION);
			}
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
	private static void writeMetaData() {
		File metaFile = new File(MetadataExtractor.endFolderName,
				MetadataExtractor.subcorpus + "."
						+ MetadataExtractor.metadataFilename);
		try {
			FileWriter fstream = new FileWriter(metaFile);
			BufferedWriter out = new BufferedWriter(fstream);
			for (Metadata m : MetadataExtractor.metadataList) {
				out.write(m.toShortStringCWB() + "\n");
			}
			// Close the output stream
			out.close();
		} catch (Exception e) {// Catch exception if any
			System.out.println("Error writing to file" + metaFile.toString()
					+ ": " + e.getMessage());
		}
	}

}
