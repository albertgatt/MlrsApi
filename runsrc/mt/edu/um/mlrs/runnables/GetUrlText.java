package mt.edu.um.mlrs.runnables;

import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;

public class GetUrlText {

	private Parser _parser;
	private StringBuffer buffer;
	private Map<String,String> textMap;

	public GetUrlText() {
		textMap = new HashMap<String,String>();

	}

	public String getPage(String urlstring) {

		String result = null;
		
		try {
			URL url = new URL(urlstring);
			URLConnection urlConnection = url.openConnection();
			this._parser = new Parser(urlConnection);
			NodeIterator iterator = this._parser.elements();
			buffer = new StringBuffer();

			while (iterator.hasMoreNodes()) {
				Node next = iterator.nextNode();
				processNode(next);				
			}

			result =  buffer.toString();

		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

		this.textMap.put(urlstring, result);
		
		return result;
	}

	
	public Map<String,String> getTexts() {
		return this.textMap;
	}
	
	private void processNode(Node node) throws Exception
	 {
	     if (node instanceof TextNode)
	     {
	         // downcast to TextNode
	         TextNode text = (TextNode)node;
	         //REMOVE HTML ELEMENTS HERE
	         buffer.append(text.getText());
	     }
	     
	     
	     if (node instanceof TagNode)
	     {
	         // downcast to TagNode
	         TagNode tag = (TagNode)node;
	         // do whatever processing you want with the tag itself
	         // ...
	         // process recursively (nodes within nodes) via getChildren()
	         NodeList nl = tag.getChildren ();
	         if (null != nl)
	             for (NodeIterator i = nl.elements(); i.hasMoreNodes(); )
	                 processNode (i.nextNode ());
	     }
	 }

	
	public static void main(String[] args) {
		GetUrlText gt = new GetUrlText();
		String text = gt
				.getPage("https://www.gnu.org/software/wget/manual/wget.html");
		System.out.println(text);

	}

}
