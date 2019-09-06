package com.mosioj.entrainements.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class MySimpleDateFormat extends SimpleDateFormat {

	private static final long serialVersionUID = 8832157695545963789L;

	/**
	 * 
	 * @param pattern
	 */
	public MySimpleDateFormat(String pattern) {
		super(pattern, Locale.FRANCE);
		setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
	}
}
