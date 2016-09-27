package mt.edu.um.mlrs.text.transform.html;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import mt.edu.um.mlrs.split.MTRegex;
import mt.edu.um.mlrs.split.RegexTokeniser;
import mt.edu.um.mlrs.split.SentenceSplitter;
import mt.edu.um.util.io.FileUtils;

public class TextWebGetter {

	public static void main(String[] args) throws Exception {
		TextWebGetter getter = new TextWebGetter();
		List<String> urls = FileUtils.readLinesFromFile(args[0], "UTF-8");

		for (String url : urls) {
			getter.addSeed(url);
		}

		getter.excludeSuffix(".*\\.pdf$", ".*\\.wmv$", ".*\\.jpg$",
				".*\\.jpeg$", ".*\\.gif$", ".*\\.mpg$", ".*\\.mpeg$",
				".*\\.avi$", ".*\\.mov$", ".*\\.aspx#$", ".*\\.doc$",
				".*\\.docx$", ".*\\.mp3$", ".*\\.asf$", ".*\\.pps$",
				".*\\.ppt$", ".*\\.pptx$", ".*#tab\\d+$");
		getter.followLinks(false);
		getter.setFilenamePrefix(args[1]);
		getter.setOutputDirectory(args[2]);
		getter.run();
		getter.printURLs();
	}

	private Set<String> _urls;

	private List<String> _seeds;

	private Set<String> _excludedSuffixes, _searchPrefixes, _printPrefixes;

	private HTMLToText _transformer;

	private int _maxPages;

	private boolean _followLinks;

	private String _outputDirectory, _outputFilePrefix;

	private int _printCount;

	public TextWebGetter() {
		this._printCount = 0;
		this._urls = new HashSet<String>();
		this._seeds = new ArrayList<String>();
		this._transformer = new HTMLToText(new SentenceSplitter(
				new RegexTokeniser(MTRegex.TOKEN)));
		this._followLinks = true;
		this._excludedSuffixes = new HashSet<String>();
		this._searchPrefixes = new HashSet<String>();
		this._printPrefixes = new HashSet<String>();
		this.setMaxPages(30000);
	}

	public void setCounter(int count) {
		this._printCount = count;
	}

	public int getCounter() {
		return this._printCount;
	}

	public void setOutputDirectory(String directory) {
		this._outputDirectory = directory;
	}

	public String getOutputDirectory() {
		return this._outputDirectory;
	}

	public void setFilenamePrefix(String pref) {
		this._outputFilePrefix = pref;
	}

	public String getFilenamePrefix() {
		return this._outputFilePrefix;
	}

	public void excludeSuffix(String suffix1, String... othersuffixes) {
		this._excludedSuffixes.add(suffix1);

		for (String suffix : othersuffixes) {
			this._excludedSuffixes.add(suffix);
		}
	}

	public void excludeSuffixes(Collection<String> suffixes) {
		this._excludedSuffixes.addAll(suffixes);
	}

	public Set<String> getExcludedSuffixes() {
		return this._excludedSuffixes;
	}

	public void addURLs(Collection<String> urls) {
		this._urls.addAll(urls);
	}

	public void addURL(String url) {
		this._urls.add(url);
	}

	public void addSeeds(Collection<String> seeds) {
		this._seeds.addAll(seeds);
	}

	public void addSeed(String url) {
		this._seeds.add(url);
	}

	public Collection<String> getSeeds() {
		return this._seeds;
	}

	public Collection<String> getURLs() {
		return this._urls;
	}

	public void setMaxPages(int max) {
		this._maxPages = max;
	}

	public int getMaxPages() {
		return this._maxPages;
	}

	public void followLinks(boolean follow) {
		this._followLinks = follow;
	}

	public void searchURLMatching(String prefix1, String... otherprefixes) {
		this._followLinks = true;
		this._searchPrefixes.add(prefix1);

		for (String other : otherprefixes) {
			this._searchPrefixes.add(other);
		}
	}

	public void searchURLMatching(Collection<String> patterns) {
		this._followLinks = true;
		this._searchPrefixes.addAll(patterns);
	}

	public void printURLMatching(Collection<String> patterns) {
		this._printPrefixes.addAll(patterns);
	}

	public void printURLMatching(String prefix1, String... otherprefixes) {
		this._printPrefixes.add(prefix1);

		for (String other : otherprefixes) {
			this._printPrefixes.add(other);
		}
	}

	public boolean followsLinks() {
		return this._followLinks;
	}

	public void printURLs() throws IOException {
		FileUtils.writeLinesToFile(new File(this._outputDirectory,
				this._outputFilePrefix + "-urls.txt"), this._urls, "UTF-8");
	}

	public void printNode(CharSequence output) throws Exception {		
		String filename = new StringBuffer(this._outputFilePrefix).append(
				this._printCount).append(".txt").toString();
		File file = new File(this._outputDirectory, filename);
		FileUtils.writeStringToFile(file, output.toString(), "UTF-8");
		this._printCount++;
	}

	public void run() {
		Stack<String> stack = new Stack<String>();
		stack.addAll(this._seeds);
		List<String> done = new ArrayList<String>();

		while (!stack.isEmpty() && (done.size() < this._maxPages)) {
			String url = stack.pop();

			if (!done.contains(url)) {
				done.add(url);
				System.err.append("Processing: " + url + "...");

				try {
					if (canPrint(url)) {
						CharSequence text = this._transformer.transform(url);

						if (text != null) {
							System.err.append("printing...");
							printNode(text);
							this._urls.add(url);
						}

					} else {
						System.err.print("no text...");
					}

					if (followsLinks() && canSearch(url)) {
						System.err.append("extracting links...");
						stack.addAll(this._transformer.getURLs(url));
					}

					System.err.append("done! \n");

				} catch (Exception pe) {
					System.err.append("failed: " + pe.getMessage() + "\n");
				}
			}
		}
	}

	private boolean canPrint(String href) {
		return (this._printPrefixes.isEmpty()
				|| (matchesAny(href, this._printPrefixes) && !matchesAny(href,
						this._excludedSuffixes)) || matchesAny(href,
				this._seeds));
	}

	private boolean canSearch(String href) {
		return (this._searchPrefixes.isEmpty()
				|| matchesAny(href, this._searchPrefixes) || matchesAny(href,
				this._seeds))
				&& (this._excludedSuffixes.isEmpty() || !matchesAny(href,
						this._excludedSuffixes));
	}

	private boolean matchesAny(String string, Collection<String> strings) {
		boolean match = false;

		for (Iterator<String> iter = strings.iterator(); iter.hasNext()
				&& !match;) {
			String suffix = iter.next();
			match = string.matches(suffix);
		}

		return match;
	}

}
