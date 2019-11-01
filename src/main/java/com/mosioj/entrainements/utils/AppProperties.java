package com.mosioj.entrainements.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AppProperties {

	private static final Logger logger = LogManager.getLogger(AppProperties.class);

	/**
	 * Class constructor.
	 */
	private AppProperties() {
		p = new Properties();
		try {
			InputStream input = EmailSender.class.getResourceAsStream("/app.properties");
			p.load(new InputStreamReader(input, "UTF-8"));
			logger.debug("host: " + p.getProperty("host"));
			logger.debug("from: " + p.getProperty("from"));
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	/**
	 * Singleton instance.
	 */
	private static AppProperties instance;

	/**
	 * Internal map.
	 */
	private Properties p;

	/**
	 * 
	 * @return The properties.
	 */
	public static AppProperties get() {
		if (instance == null) {
			instance = new AppProperties();
		}
		return instance;
	}
	
	/**
	 * Shortcut to retrieve a property.
	 * 
	 * @param propertyName The key.
	 * @return The property value, or null if not found.
	 */
	public static String getAProperty(String propertyName) {
		return get().getProperty(propertyName);
	}

	/**
	 * 
	 * @param propertyName The key.
	 * @return The property value, or null if not found.
	 */
	public String getProperty(String propertyName) {
		return p.getProperty(propertyName);
	}
}
