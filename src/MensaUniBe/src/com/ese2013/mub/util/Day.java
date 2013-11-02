package com.ese2013.mub.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Day {
	private int year, month, day;

	public Day(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		year = cal.get(Calendar.YEAR);
		month = cal.get(Calendar.MONTH);
		day = cal.get(Calendar.DAY_OF_MONTH);
	}

	private Date getDate() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(0);
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, day);
		return cal.getTime();
	}
	
	public String format(SimpleDateFormat fm) {
		return fm.format(getDate());
	}
	
	public String getDayOfWeekString() {
		SimpleDateFormat df = new SimpleDateFormat("EEEE", Locale.getDefault());
		return format(df);
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Day) {
			Day otherDay = (Day) other;
			if (otherDay.day != this.day || otherDay.month != this.month || otherDay.year != this.year)
				return false;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		final int multiplier = 29;
		int hashCode = 17;
		hashCode += year;
		hashCode *= multiplier;
		hashCode += month;
		hashCode *= multiplier;
		hashCode += day;
		return hashCode;
	}
}
