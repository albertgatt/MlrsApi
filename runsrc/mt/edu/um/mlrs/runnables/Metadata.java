package mt.edu.um.mlrs.runnables;

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
	
	/*
	 * Produces a short version of the datastructure in string format
	 * containing only filename, category and topic.
	 * 
	 * @return String containing filename, category and topic tab-delimited.
	 */
	public String toShortStringCWB(){
		return filename + "\t" + subcorpus + "\t\"" + topic + "\"";
	}
	
	/*
	 * Produces a full version of the datastructure in string format
	 * tab delimited
	 * 
	 * @return Returns a string containing all the datastructure tab-delimited.
	 */
	public String toFullStringCWB(){
		return filename + "\t" + subcorpus + "\t" + topic + "\t" + title + "\t" + 
			identifier + "\t" + publisher + "\t" + author + "\t" + copyright + "\t" + 
			published + "\t" + added + "\t" + info + "\t" + type;
	}
	
	/*********** GET / SET METHODS   ***************/
	
	public String getFilename(){
		return this.filename;
	}
	
	public void setFilename(String filename){
		this.filename = filename;
	}
	
	public String getSubCorpus(){
		return this.subcorpus;
	}
	
	public void setSubCorpus(String subCorpus){
		this.subcorpus = subCorpus;
	}

	
	public String getTitle(){
		return this.title;
	}
	
	public void setTitle(String title){
		this.title = title;
	}

	public String getIdentifier(){
		return this.identifier;
	}
	
	public void setIdentifier(String identifier){
		this.identifier = identifier;
	}

	public String getPublisher(){
		return this.publisher;
	}
	
	public void setPublisher(String publisher){
		this.publisher = publisher;
	}

	public String getAuthor(){
		return this.author;
	}
	
	public void setAuthor(String author){
		this.author = author;
	}

	public String getCopyright(){
		return this.copyright;
	}
	
	public void setCopyright(String copyright){
		this.copyright = copyright;
	}

	public String getPublished(){
		return this.published;
	}
	
	public void setPublished(String published){
		this.published = published;
	}

	public String getAdded(){
		return this.added;
	}
	
	public void setAdded(String added){
		this.added = added;
	}

	public String getInfo(){
		return this.info;
	}
	
	public void setInfo(String info){
		this.info = info;
	}

	public String getTopic(){
		return this.topic;
	}
	
	public void setTopic(String topic){
		this.topic = topic;
	}

	public String getType(){
		return this.type;
	}
	
	public void setType(String type){
		this.type = type;
	}

	
}
