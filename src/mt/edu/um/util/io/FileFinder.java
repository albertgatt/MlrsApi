package mt.edu.um.util.io;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class FileFinder {

	public static List<File> findFiles(File f) {
		List<File> list = new ArrayList<File>();
		try {
			if (f.isDirectory()) {
				for (File sub : f.listFiles()) {
					list.addAll(FileFinder.findFiles(sub));
				}
			} else {
				list.add(f);
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		return list;
	}

	public static List<File> findFiles(String directory) {
		return findFiles(new File(directory));
	}

	public static List<File> findFiles(String s, String extension) {
		return findFiles(new File(s), extension);
	}

	public static List<File> findFiles(File f, String extension,
			String ignorePattern) {
		List<File> list = new ArrayList<File>();
		DefaultFileFilter filter = new DefaultFileFilter(extension);

		try {
			if (!f.getName().matches(ignorePattern)) {
				if (f.isDirectory()) {

					for (File sub : f.listFiles()) {
						list.addAll(findFiles(sub, extension, ignorePattern));
					}

				} else if (filter.accept(f)) {
					list.add(f);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		return list;
	}

	public static List<File> findFiles(File f, String extension) {
		List<File> list = new ArrayList<File>();
		DefaultFileFilter filter = new DefaultFileFilter(extension);

		try {
			if (f.isDirectory()) {

				for (File sub : f.listFiles()) {
					list.addAll(findFiles(sub, extension));
				}

			} else if (filter.accept(f)) {
				list.add(f);
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		return list;
	}
}
