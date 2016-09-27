package mt.edu.um.util;

import java.util.Map;
import java.util.TreeMap;

/**
 * Utility class to collect command line arguments. Arguments are expected to be
 * prefixed, for example: <code>-i XX</code>. The method
 * {@link #getArgs(String[])} returns a map with prefixes mapped to values.
 * 
 * <P>
 * This class makes it easier to check for the existence of arguments,
 * particularly when a main method can work with argument lists of variable
 * length (i.e. there are optional arguments). Moreover, it obviates the need to
 * supply arguments in a fixed order.
 * 
 * 
 * @author bertugatt
 * 
 */
public class CollectArgs {

	/**
	 * Collect arguments in the supplied array, and arrange them so that
	 * prefixes are mapped to values. The array length must be an even number,
	 * since all arguments are expected to be prefixed.
	 * 
	 * @param args
	 *            the argument list
	 * @return a map, mapping prefixes to values
	 * @throws {@link java.lang.RuntimeException} if the argument array length
	 *         is not even
	 */
	public static Map<String, String> getArgs(String[] args) {
		Map<String, String> map = new TreeMap<String, String>();

		if (args.length % 2 != 0) {
			throw new RuntimeException(
					"Argument list length must be even: all arguments should be preceded by a prefix.");
		}

		String prefix = null;
		String arg = null;
		
		for (int i = 0; i < args.length; i += 2) {
			prefix = args[i];
			arg = args[i+1];
			map.put(prefix, arg);
		}

		return map;
	}
}
