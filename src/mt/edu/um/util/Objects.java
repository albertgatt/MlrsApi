package mt.edu.um.util;

public class Objects {

	/*
	 * check whether two strings are both null or equal
	 */
	public static boolean equalOrNull(Object o1, Object o2) {
		boolean eq = false;

		if (o1 == null && o2 == null) {
			eq = true;
		} else if (o1 == null || o2 == null) {
			eq = false;
		} else {
			eq = o1.equals(o2);
		}

		return eq;
	}
	
}
