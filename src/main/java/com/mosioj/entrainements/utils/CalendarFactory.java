package com.mosioj.entrainements.utils;

import java.util.Calendar;
import java.util.TimeZone;

public class CalendarFactory {

	private CalendarFactory() {
		// Forbidden
	}

	/**
	 * 
	 * @return The calendar, set up with the Paris timezone.
	 */
	public static Calendar getOne() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
		return cal;
	}
}
