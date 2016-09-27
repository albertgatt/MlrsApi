package mt.edu.um.util.io;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class DefaultFileFilter implements FileFilter {

	private List<String> _fileExtensions;

	DefaultFileFilter() {
		this._fileExtensions = new ArrayList<String>();
	}

	public DefaultFileFilter(String... extensions) {
		this();

		for (String s : extensions) {
			this._fileExtensions.add(s);
		}
	}

	public DefaultFileFilter(Collection<String> extensions) {
		this();
		this._fileExtensions.addAll(extensions);
	}

	@Override
	public boolean accept(File arg0) {
		boolean accept = false;
		Iterator<String> extensionIter = this._fileExtensions.iterator();

		while (extensionIter.hasNext() && !accept) {
			accept = arg0.getName().endsWith(extensionIter.next());
		}

		return accept;
	}

}
