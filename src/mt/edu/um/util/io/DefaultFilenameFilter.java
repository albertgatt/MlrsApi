package mt.edu.um.util.io;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class DefaultFilenameFilter implements FilenameFilter {

	private List<String> fileExtensions;

	DefaultFilenameFilter() {
		this.fileExtensions = new ArrayList<String>();
	}

	public DefaultFilenameFilter(String... extensions) {
		this();

		for (String ext : extensions) {
			this.fileExtensions.add(ext.trim());
		}
	}

	public DefaultFilenameFilter(Collection<String> extensions) {
		this();
		this.fileExtensions.addAll(extensions);
	}

	@Override
	public boolean accept(File arg0, String arg1) {
		boolean accept = false;
		Iterator<String> extensionIter = this.fileExtensions.iterator();

		while (extensionIter.hasNext() && !accept) {
			accept = arg1.endsWith(extensionIter.next());
		}

		return accept;
	}

}
