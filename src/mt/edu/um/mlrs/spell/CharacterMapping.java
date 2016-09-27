package mt.edu.um.mlrs.spell;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import mt.edu.um.util.io.FileUtils;

public class CharacterMapping {

	private List<String> characters;
	private List<String> replacements;
	
	public static CharacterMapping fromFile(String filename, String charset, String separator) throws IOException {
		CharacterMapping mapping = new CharacterMapping();
		Map<String,String> fields = FileUtils.readFieldsFromFile(filename, charset, separator);
		
		for(String f: fields.keySet() ){
			mapping.setReplacement(f, fields.get(f));
		}
		
		return mapping;
	}
	
	public CharacterMapping() {
		this.characters = new ArrayList<String>();
		this.replacements = new ArrayList<String>();
	}
	
	public boolean hasReplacement(String string) {
		return this.characters.contains(string);
	}
	
	public boolean hasReplacement(char c) {
		return hasReplacement(String.valueOf(c));
	}
	
	public String getReplacement(char c) {
		return getReplacement(String.valueOf(c));
	}
	
	public String getReplacement(String string) {
		int index = characters.indexOf(string);
		String repl = null;
		
		if(index > -1) {
			repl = replacements.get(index);
		}
		
		return repl;
	}
	
	public void setReplacement(String string, String replacement) {
		this.characters.add(string);
		this.replacements.add(replacement);
	}
	
	public String replaceAll(String string) {
		
		for(int i = 0; i < characters.size(); i++) {
			string = string.replaceAll(Pattern.quote(characters.get(i)), replacements.get(i));
		}
		
		return string;
	}
	
	
	
}
