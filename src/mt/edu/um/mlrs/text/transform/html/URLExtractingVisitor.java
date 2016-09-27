package mt.edu.um.mlrs.text.transform.html;

import java.util.ArrayList;
import java.util.List;

import org.htmlparser.Tag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.visitors.NodeVisitor;

public class URLExtractingVisitor extends NodeVisitor {

	private String _parentURL;

	private List<String> _urls;

	public URLExtractingVisitor() {
		super(true, true);
		this._urls = new ArrayList<String>();
	}

	public URLExtractingVisitor(String parentURL) {
		this();
		setParentURL(parentURL);
	}

	public void setParentURL(String url) {
		this._parentURL = url;
	}

	public String getParentURL() {
		return this._parentURL;
	}

	@Override
	public void visitTag(Tag tag) {
		if (tag instanceof LinkTag && ((LinkTag) tag).isHTTPLink()) {
			String link = ((LinkTag) tag).getLink();

			if (link != null && checkURL(link)) {
				this._urls.add(link);
			}
		}
	}

	public List<String> getExtractedURLs() {
		return this._urls;
	}
	
	public void reset() {
		this._urls.clear();
	}

	private boolean checkURL(String url) {
		return (this._parentURL != null) ? url.startsWith(this._parentURL)
				: true;
	}

}
