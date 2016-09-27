package mt.edu.um.mlrs.runnables;

import java.sql.PreparedStatement;

import mt.edu.um.util.xml.DomUtils;
import mt.edu.um.util.xml.XPathUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import uk.ac.abdn.ban.DatabaseHandler;
import uk.ac.abdn.ban.JDBCDatabaseHandler;

/*
 * Class to read in an XML file with SketchEngine collocations and export them to a DB.
 */

public class ExportSECollocationsToDB {
	public static String FILE = "A:/mlrs/sketchengine-collocations/cesar_mt.xml";
	public static String ENTRY = "dict/collo_entry";
	public static String HEADATT = "headword";
	public static String POS = "pos";
	public static String COLLOC_TYPE = "collo_type";
	public static String TYPEATT = "type";
	public static String COLLOC = "collo";
	public static String COLLOCATION = "collocation";

	// db stuff
	private static String driver = "com.mysql.jdbc.Driver";
	private static String url = "jdbc:mysql://127.0.0.1:3306/mlrscollocations";
	private static String username = "root";
	private static String password = "root";
	private static String table = "collocations";
	private static PreparedStatement statement;

	public static DatabaseHandler handler;

	public static void main(String[] args) throws Exception {
		int heads = 0;
		handler = getDBHandler();
		statement = handler.prepareStatement("INSERT INTO " + table
				+ " (headword, relation, word) VALUES(?,?,?)");
		
		Document doc = DomUtils.loadDocument(FILE);
		NodeList entries = XPathUtils.getChildNodes(doc, ENTRY);

		for (int i = 0; i < entries.getLength(); i++) {
			Node entry = entries.item(i);
			String headword = XPathUtils.getAttributeValue(entry, HEADATT);
			System.out.println(headword);
			heads++;
			NodeList types = XPathUtils.getChildNodes(entry, COLLOC_TYPE);

			for (int j = 0; j < types.getLength(); j++) {
				Node type = types.item(j);
				String collotype = XPathUtils.getAttributeValue(type, TYPEATT);
				NodeList collocations = XPathUtils.getChildNodes(type, COLLOC);

				for (int k = 0; k < collocations.getLength(); k++) {
					Node collocation = collocations.item(k);
					String word = XPathUtils.getAttributeValue(collocation,
							COLLOCATION);					
					System.out.println("\t " + collotype + "\t" + word);
					insert(headword, collotype, word);
				}

			}
		}
		
		System.out.println(heads + "unique headwords");

	}

	private static DatabaseHandler getDBHandler() throws Exception {
		DatabaseHandler handler = new JDBCDatabaseHandler();
		handler.configureDatabase(driver, url, username, password);
		handler.openDatabase();
		return handler;
	}

	private static void insert(String headword, String relation, String word)
			throws Exception {
		statement.clearParameters();
		statement.setString(1, headword);
		statement.setString(2, relation);
		statement.setString(3, word);
		statement.execute();
	}

}
