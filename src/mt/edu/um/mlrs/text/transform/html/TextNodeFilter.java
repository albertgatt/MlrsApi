package mt.edu.um.mlrs.text.transform.html;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.nodes.TextNode;

public class TextNodeFilter implements NodeFilter {

	private static final long serialVersionUID = 7444096394785124696L;

	public TextNodeFilter() {
		super();
	}
	
	public boolean accept(Node node) {
		return node instanceof TextNode;
	}
	
}
