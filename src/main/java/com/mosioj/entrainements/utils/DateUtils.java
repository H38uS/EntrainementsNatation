package com.mosioj.entrainements.utils;

import java.text.ParseException;
import java.util.Date;
import java.util.Optional;

public class DateUtils {

	public static final String DATE_FORMAT = "yyyy-MM-dd";

	private DateUtils() {
		// Forbidden
	}

	/**
	 * 
	 * @param date
	 * @return The corresponding date if it succeeds to parse it.
	 */
	public static Optional<Date> getAsDate(String date) {
		MySimpleDateFormat format = new MySimpleDateFormat(DATE_FORMAT);
		try {
			return Optional.of(format.parse(date));
		} catch (ParseException e) {
			return Optional.empty();
		}
	}
}
