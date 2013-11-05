package com.ese2013.mub.test;

import static com.ese2013.mub.test.Util.assertNotEquals;
import java.util.Calendar;
import java.util.Locale;

import junit.framework.TestCase;

import com.ese2013.mub.model.Day;

public class DayTest extends TestCase {

	public void testGetWeekNumber() {
		Day day = new Day(4, 11, 2013);
		Calendar cal = Calendar.getInstance(Locale.GERMAN); // use iso and not
															// U.S. Standards
		cal.set(Calendar.YEAR, 2013);
		cal.set(Calendar.MONTH, 11 - 1); // Calendar has months 0..11
		cal.set(Calendar.DAY_OF_MONTH, 4);
		int isoWeekNumber = cal.get(Calendar.WEEK_OF_YEAR);
		assertEquals(isoWeekNumber, day.getWeekNumber());
	}

	public void testEquals() {
		Day day1 = new Day(4, 11, 2013);
		Day day2 = new Day(4, 11, 2013);
		assertEquals(day1, day1);
		assertEquals(day2, day2);
		assertEquals(day1, day2);

		day2 = new Day(5, 11, 2013);
		assertNotEquals(day1, day2);

		day2 = new Day(4, 12, 2013);
		assertNotEquals(day1, day2);

		day2 = new Day(4, 11, 2012);
		assertNotEquals(day1, day2);
	}
}