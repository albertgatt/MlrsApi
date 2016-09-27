package mt.edu.um.mlrs.split;

import java.util.Arrays;
import java.util.List;

public class WhiteSpaceTokeniser extends Tokeniser {

	@Override
	public List<String> split(String string) {
		String[] split = string.split("\\s+");
		return Arrays.asList(split);
	}

}
