package mt.edu.um.mlrs.classify.tag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import mt.edu.um.util.io.FileUtils;

public class TagDictionary {
	
	private Map<String,TaggedToken> _items;
	
	
	public static TagDictionary fromFile(String filepath, String charset) throws IOException {
		TagDictionary dictionary = new TagDictionary();
		Map<String,String> map = FileUtils.readFieldsFromFile(filepath, charset, "\t"); 
		
		for(String s: map.keySet()) {
			dictionary.addEntry(s, map.get(s));
		}
		
		return dictionary;
	}
	
	
	public TagDictionary() {
		this._items = new TreeMap<String,TaggedToken>();
	}
		
	
	public boolean hasEntry(String token) {
		return this._items.containsKey(token);
	}
	
	public void addEntry(String token, String tag) {
		if(hasEntry(token)) {
			this._items.get(token).addTag(tag, 1.0);
		} else {
			this._items.put(token, new TaggedToken(token, tag));
		}
	}
	
	public void addEntry(String token, String tag, double frequency) {
		if(hasEntry(token)) {
			this._items.get(token).addTag(tag, frequency);
		}
	}
	
	public List<String> getTags(String token) {
		return hasEntry(token) ? this._items.get(token).getTags() : new ArrayList<String>();		
	}
	
	public String getMostFrequentTag(String token) {
		return hasEntry(token) ? this._items.get(token).getBestTag() : null;
	}
	
	public boolean hasTag(String token, String tag) {
		return hasEntry(token) ? this._items.get(token).hasTag(tag) : false;
	}
	
	public void writeToFile(String outputFile) throws IOException {
		TreeSet<String> lines = new TreeSet<String>();
		
		for(TaggedToken tok: this._items.values()) {
			
			for(String tag: tok.getTags()) {
				lines.add(tag);
			}
		}
		
		FileUtils.writeLinesToFile(outputFile, lines, "UTF-8");
	}
}
