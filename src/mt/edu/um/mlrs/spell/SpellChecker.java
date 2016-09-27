package mt.edu.um.mlrs.spell;

import java.util.Collection;
import java.util.List;

public interface SpellChecker {

	public String getBest(String text);
	
	public List<String> getNBest(String text);
	
	public String[] getBest(String[] tokens);
	
	public List<String> getBest(Collection<String> tokens);
	
	public void setNBest(int n);
	
	public int getNBest();
	
	public void setCaseSensitive(boolean cs);
	
	public boolean isCaseSensitive();
	
}
