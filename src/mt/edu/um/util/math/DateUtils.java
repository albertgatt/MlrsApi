package mt.edu.um.util.math;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DateUtils {

	private static final int[] fields = { Calendar.MILLISECOND,
			Calendar.SECOND, Calendar.MINUTE, Calendar.HOUR };

	private static final Map<Integer, Integer> maxima = new HashMap<Integer, Integer>();

	static {
		maxima.put(Calendar.SECOND, 60);
		maxima.put(Calendar.MINUTE, 60);
		maxima.put(Calendar.HOUR, 24);
	}
	
	public static Date getMidPoint(Date d1, Date d2) {
		Date midpoint = null;

		if (d1 != null && d2 != null) {

			if (d1.equals(d2)) {
				midpoint = d1;
				
			} else {				
				long time1 = d1.getTime();
				long time2 = d2.getTime();
				long diff = (time2-time1)/2;
				midpoint = new Date(time1+diff);
			}
		}

		return midpoint;
	}

	public static Date truncate(Date date, int dateField, int interval) {
		boolean done = false;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		for (int i = 0; i < fields.length && !done; i++) {
			int field = fields[i];

			if (dateField == field) {
				int fieldValue = calendar.get(field);
				int mod = fieldValue % interval;

				if (mod > 0) {
					calendar.add(field, -mod);
				}

				done = true;

			} else {
				calendar.set(field, 0);
			}
		}

		return calendar.getTime();
	}

	public static Date round(Date date, int dateField, int interval) {
		boolean done = false;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		for (int i = 0; i < fields.length && !done; i++) {
			int field = fields[i];

			if (dateField == field) {
				int fieldValue = calendar.get(field);
				int mod = fieldValue % interval;

				if (mod > 0) {
					calendar.add(field, (interval - mod));
				}

				done = true;

			} else {
				calendar.set(field, 0);
			}
		}

		return calendar.getTime();
	}

}
