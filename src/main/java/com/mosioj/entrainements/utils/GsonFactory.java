package com.mosioj.entrainements.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mosioj.entrainements.utils.date.LocalDateAdapter;
import com.mosioj.entrainements.utils.date.LocalDateTimeAdapter;

public class GsonFactory {

	public static final String DATE_FORMAT = "dd/MM/YYYY";

	/**
	 * The only instance.
	 */
	private static Gson instance;

	private GsonFactory() {
		// Not allowed
	}

	/**
	 * 
	 * @return The GSon object used to serialize.
	 */
	public static Gson getIt() {
		if (instance == null) {
			instance = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
										.setDateFormat(DATE_FORMAT)
										.registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
										.registerTypeAdapter(LocalDateTime.class,  new LocalDateTimeAdapter())
										.create();
		}
		return instance;
	}
}
