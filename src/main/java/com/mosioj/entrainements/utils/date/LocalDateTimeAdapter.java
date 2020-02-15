package com.mosioj.entrainements.utils.date;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.util.Locale;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_TIME;
import static java.time.temporal.ChronoField.*;

public class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime> {

    private static final DateTimeFormatter MyFormat = new DateTimeFormatterBuilder().appendValue(DAY_OF_MONTH, 2)
                                                                                    .appendLiteral('/')
                                                                                    .appendValue(MONTH_OF_YEAR, 2)
                                                                                    .appendLiteral('/')
                                                                                    .appendValue(YEAR,
                                                                                                 4,
                                                                                                 10,
                                                                                                 SignStyle.EXCEEDS_PAD)
                                                                                    .appendLiteral(" à ")
                                                                                    .append(ISO_LOCAL_TIME)
                                                                                    .toFormatter(Locale.FRENCH);

    @Override
    public JsonElement serialize(LocalDateTime date, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(date.format(MyFormat)); // "yyyy-mm-dd"
    }

}
