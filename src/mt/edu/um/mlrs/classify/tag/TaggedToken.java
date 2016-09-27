/**
 * 
 */
package mt.edu.um.mlrs.classify.tag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class TaggedToken {

	private String _pTag;

	private Map<String, Double> _tags;

	private String _token;
	
	private Comparator<String> _comparator = new Comparator<String>() {
		public int compare(String s1, String s2) {
			return TaggedToken.this._tags.get(s1).compareTo(TaggedToken.this._tags.get(s2));
		}
	};

	TaggedToken(String token) {
		this._token = token;
		this._tags = new HashMap<String, Double>();		
	}
	
	TaggedToken(String token, String...tags) {
		this(token);
		
		for(String t: tags) {
			addTag(t, 1.0);
		}
	}

	boolean hasTag(String tag) {
		return this._tags.containsKey(tag);
	}

	void addTag(String tag, double frequency) {
		double freq = hasTag(tag) ? this._tags.get(tag) + frequency : frequency;
		this._tags.put(tag, freq);

		if (this._pTag == null) {
			this._pTag = tag;
			
		} else if (freq > this._tags.get(this._pTag)) {
			this._pTag = tag;
		}
	}
	
	String getBestTag() {
		return this._pTag;
	}				

	String getToken() {
		return this._token;
	}
	
	List<String> getTags() {
		List<String> tags = new ArrayList<String>(this._tags.keySet());
		Collections.sort(tags, this._comparator);
		return tags;
	}

}