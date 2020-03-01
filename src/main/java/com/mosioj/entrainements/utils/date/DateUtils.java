package com.mosioj.entrainements.utils.date;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public class DateUtils {

    private DateUtils() {
        // Forbidden
    }

    /**
     * @param date The date string to parse.
     * @return The corresponding date if it succeeds to parse it.
     */
    public static Optional<LocalDate> getAsDate(String date) {
        try {
            return Optional.of(LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE)); // yyyy-MM-dd
        } catch (NullPointerException | DateTimeParseException e) {
            return Optional.empty();
        }
    }
}
