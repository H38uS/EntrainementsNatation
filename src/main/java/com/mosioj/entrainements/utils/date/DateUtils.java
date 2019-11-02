package com.mosioj.entrainements.utils.date;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
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
	public static Optional<LocalDate> getAsDate(String date) {
		try {
			return Optional.of(LocalDate.parse(date));
		} catch (DateTimeParseException e) {
			return Optional.empty();
		}
	}
}
