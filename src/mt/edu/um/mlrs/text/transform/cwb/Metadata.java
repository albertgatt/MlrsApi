package mt.edu.um.mlrs.text.transform.cwb;

import org.apache.commons.lang.StringUtils;

import mt.edu.um.mlrs.text.TextNode;

public class Metadata {

	private String filename = "";
	private String subcorpus = "";
	private String title = "";
	private String identifier = "";
	private String publisher = "";
	private String author = "";
	private String copyright = "";
	private String published = "";
	private String added = "";
	private String info = "";
	private String topic = "";
	private String type = "";

	public static Metadata fromNode(TextNode header, String filename,
			String subcorpus) {
		Metadata md = new Metadata();
		String fileName = filename.substring(0, filename.indexOf('.'))
				.replaceAll("-", "_");
		md.setFilename(fileName);
		md.setSubCorpus(subcorpus);
		TextNode title = header.firstChild("TITLE");
		TextNode topic = header.firstChild("TOPIC");
		TextNode ident = header.firstChild("IDENTIFIER");
		TextNode publisher = header.firstChild("PUBLISHER");
		TextNode author = header.firstChild("AUTHOR");
		TextNode copyright = header.firstChild("COPYRIGHT");
		TextNode published = header.firstChild("PUBLISHED");
		TextNode added = header.firstChild("ADDED");
		TextNode info = header.firstChild("INFO");
		TextNode type = header.firstChild("TYPE");

		if (title != null)
			md.setTitle(StringUtils.defaultString(title.getContent()));

		if (topic != null)
			md.setTopic(StringUtils.defaultString(topic.getContent()));

		if (ident != null)
			md.setIdentifier(StringUtils.defaultString(ident.getContent()));

		if (publisher != null)
			md.setPublisher(StringUtils.defaultString(publisher.getContent()));

		if (author != null)
			md.setAuthor(StringUtils.defaultString(author.getContent()));

		if (copyright != null)
			md.setCopyright(StringUtils.defaultString(copyright.getContent()));

		if (published != null)
			md.setPublished(StringUtils.defaultString(published.getContent()));

		if (added != null)
			md.setAdded(StringUtils.defaultString(added.getContent()));

		if (info != null)
			md.setInfo(StringUtils.defaultString(info.getContent()));

		if (type != null)
			md.setType(StringUtils.defaultString(type.getContent()));
		
		return md;
	}

	public Metadata() {

	}

	/*
	 * Produces a short version of the datastructure in string format containing
	 * only filename, category and topic.
	 * 
	 * @return String containing filename, category and topic tab-delimited.
	 */
	public String toShortStringCWB() {
		return this.filename + "\t" + this.subcorpus + "\t\"" + this.topic
				+ "\"";
	}

	/*
	 * Produces a full version of the datastructure in string format tab
	 * delimited
	 * 
	 * @return Returns a string containing all the datastructure tab-delimited.
	 */
	public String toFullStringCWB() {
		return this.filename + "\t" + this.subcorpus + "\t" + this.topic + "\t"
				+ this.title + "\t" + this.identifier + "\t" + this.publisher
				+ "\t" + this.author + "\t" + this.copyright + "\t"
				+ this.published + "\t" + this.added + "\t" + this.info + "\t"
				+ this.type;
	}

	/*********** GET / SET METHODS ***************/

	public String getFilename() {
		return this.filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getSubCorpus() {
		return this.subcorpus;
	}

	public void setSubCorpus(String subCorpus) {
		this.subcorpus = subCorpus;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getIdentifier() {
		return this.identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getPublisher() {
		return this.publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getAuthor() {
		return this.author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getCopyright() {
		return this.copyright;
	}

	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}

	public String getPublished() {
		return this.published;
	}

	public void setPublished(String published) {
		this.published = published;
	}

	public String getAdded() {
		return this.added;
	}

	public void setAdded(String added) {
		this.added = added;
	}

	public String getInfo() {
		return this.info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getTopic() {
		return this.topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
