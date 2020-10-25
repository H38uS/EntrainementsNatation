package com.mosioj.entrainements.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mosioj.entrainements.utils.date.LocalDateAdapter;
import com.mosioj.entrainements.utils.date.LocalDateDeserializer;
import com.mosioj.entrainements.utils.date.LocalDateTimeAdapter;
import com.mosioj.entrainements.utils.date.LocalDateTimeDeserializer;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class GsonFactory {

    public static final String DATE_FORMAT = "dd/MM/YYYY";

    /**
     * The only instance.
     */
    private static final Gson instance = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
                                                          .setDateFormat(DATE_FORMAT)
                                                          .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                                                          .registerTypeAdapter(LocalDate.class,
                                                                               new LocalDateDeserializer())
                                                          .registerTypeAdapter(LocalDateTime.class,
                                                                               new LocalDateTimeAdapter())
                                                          .registerTypeAdapter(LocalDateTime.class,
                                                                               new LocalDateTimeDeserializer())
                                                          .create();

    private GsonFactory() {
        // Not allowed
    }

    /**
     * @return The GSon object used to serialize.
     */
    public static Gson getIt() {
        return instance;
    }
}
