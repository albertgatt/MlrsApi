package mt.edu.um.mlrs.text.transform.html;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.tags.TitleTag;

public class TitleNodeFilter implements NodeFilter {

	private static final long serialVersionUID = 6147098562583682649L;

	public TitleNodeFilter() {
		super();
	}
	
	public boolean accept(Node node) {
		return node instanceof TitleTag;
	}
	
}
