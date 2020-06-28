package com.mosioj.entrainements.utils.date;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Optional;

public class DateUtils {

    /** The date time pattern. */
    public static final String DATE_TIME_PATTERN = "dd/MM/yyyy à HH:mm:ss";

    /** The date time formatter. */
    public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)
                                                                                .withLocale(Locale.FRENCH);

    /** The date pattern. */
    public static final String DATE_PATTERN = "yyyy-MM-dd";

    /** The date formatter. */
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN)
                                                                            .withLocale(Locale.FRENCH);

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
