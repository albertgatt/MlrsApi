/**
 * 
 */
package mt.edu.um.rules;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;

public enum BasicOperator implements Operator {

	EQ(Object.class),

	STRINGEQ(Object.class),

	GE(Comparable.class),

	GEQ(Comparable.class),

	LE(Comparable.class),

	LEQ(Comparable.class),

	ELEMENT_OF(Collection.class),

	DEFINED(Comparable.class),

	NEQ(Object.class),

	SUFFIX(Object.class),

	PREFIX(Object.class),

	REGEX(Object.class),

	STRLENGTH(Object.class);

	Set<Class<?>> datatypes;

	BasicOperator(Class<?>... types) {
		this.datatypes = new HashSet<Class<?>>();

		for (Class<?> c : types) {
			this.datatypes.add(c);
		}
	}

	public boolean appliesTo(Object value) {
		boolean applies = false;
		Iterator<Class<?>> iter = this.datatypes.iterator();

		while (iter.hasNext() && !applies) {
			applies = iter.next().isInstance(value);
		}

		return applies;
	}

	public boolean evaluate(Object value1, Object value2) {
		boolean satisfied = false;

		switch (this) {
		case STRINGEQ:
			satisfied = value1 == null || value2 == null ? false : value1
					.toString().equals(value2.toString());
			break;
		case EQ:
			satisfied = value1 == null || value2 == null ? false : value1
					.equals(value2);
			break;
		case LE:
			satisfied = value1 == null || value2 == null ? false : compare(-1,
					value1, value2);
			break;
		case LEQ:
			satisfied = value1 == null || value2 == null ? false : compare(-1,
					value1, value2)
					|| compare(0, value1, value2);
			break;
		case GE:
			satisfied = value1 == null || value2 == null ? false : compare(1,
					value1, value2);
			break;
		case GEQ:
			satisfied = value1 == null || value2 == null ? false : compare(0,
					value1, value2)
					|| compare(1, value1, value2);
			break;
		case ELEMENT_OF:
			satisfied = value1 == null || value2 == null ? false
					: ((Collection<?>) value2).contains(value1);
			break;
		case NEQ:
			satisfied = value1 == null && value2 != null || value1 != null
					&& value2 == null || !value1.equals(value2);
			break;
		case DEFINED:
			satisfied = value1 != null;
			break;
		case SUFFIX:
			satisfied = (value1 == null || value2 == null) ? false : value1
					.toString().endsWith(value2.toString());
			break;

		case PREFIX:
			satisfied = (value1 == null || value2 == null) ? false : value1
					.toString().startsWith(value2.toString());
			break;
		case REGEX:
			satisfied = (value1 == null || value2 == null) ? false : Pattern
					.compile(value2.toString()).matcher(value1.toString())
					.find();
			break;		
		}

		return satisfied;
	}

	@SuppressWarnings("unchecked")
	private boolean compare(int expectedValue, Object value1, Object value2) {
		boolean success = false;
		try {
			int c = Comparable.class.cast(value1).compareTo(
					Comparable.class.cast(value2));
			success = expectedValue < 0 ? c < 0 : expectedValue == 0 ? c == 0
					: c > 0;
		} catch (ClassCastException cce) {
			;// do nothing
		}

		return success;
	}

}