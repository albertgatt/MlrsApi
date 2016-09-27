package mt.edu.um.mlrs.classify.tag.rules;

import mt.edu.um.mlrs.classify.tag.TagDictionary;

public class MorphologicalRule extends TagReplacementRule {

	private TagDictionary _dict;
	
	private String _prefix, _suffix;
	
	private String _hasTag, _targetTag;
	
	
	public MorphologicalRule(String prefix, String suffix, String hasTag, String targetTag) {
		this._hasTag = hasTag;
		this._targetTag = targetTag;
	}
	
	public void setPrefix(String prefix ) {
		this._prefix =prefix;				
	}
	
	public void setSuffix(String suffix) {
		this._suffix = suffix;
	}
	
	public String getPrefix() {
		return this._prefix;
	}
	
	public String getSuffix() {
		return this._suffix;
	}
	
	public boolean applies(String token) {
		return checkPrefix(token) && checkSuffix(token);
	}
	
	private boolean checkPrefix(String token) {
		return this._prefix == null ? false : token.startsWith(this._prefix);
	}
	
	private boolean checkSuffix(String token) {
		return this._suffix == null ? false : token.endsWith(this._suffix);
	}

	public String apply(String token) {
		StringBuffer buffer = new StringBuffer(token);
		String tag = null;
		
		if(checkPrefix(token)) {
			buffer.delete(0, this._prefix.length());
		}
		
		if(checkSuffix(token)) {
			buffer.delete(token.length()-this._suffix.length(), token.length());
		}
		
		String truncated = buffer.toString();
		
		if(!truncated.equals(token) && this._dict.hasTag(truncated, this._hasTag)) {
			tag = this._targetTag;
		}

		return tag;
	}
	
			
	
}
