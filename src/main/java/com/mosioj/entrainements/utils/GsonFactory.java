package com.mosioj.entrainements.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
			instance = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat(DATE_FORMAT).create();
		}
		return instance;
	}
}
