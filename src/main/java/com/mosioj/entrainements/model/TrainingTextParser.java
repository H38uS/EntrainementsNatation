package com.mosioj.entrainements.model;

import java.text.MessageFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TrainingTextParser {

	private static final Logger logger = LogManager.getLogger(TrainingTextParser.class);

	private final String text;

	/**
	 * Class constructor.
	 * 
	 * @param text The initial text.
	 */
	public TrainingTextParser(String text) {
		this.text = text;
	}

	/**
	 * 
	 * @return The possible size of the training, based on the text.
	 */
	public int getTrainingSize() {

		// Init et suppression des départs
		String remaining = text.toLowerCase();
		logger.info("Parsing: " + remaining);

		remaining = remaining.replaceAll("\\d+'\\d*", "");
		remaining = remaining.replaceAll("par \\d+", "");
		remaining = remaining.replaceAll("\\(\\d\\..*", "");
		remaining = remaining.replaceAll("\\d\\..*", "");
		remaining = remaining.replaceAll("^\\s*\\d/.*", "");
		remaining = remaining.replaceAll("4n", "");

		String[] parts = remaining.split("\\r?\\n\\s*\\r?\\n");
		logger.debug(MessageFormat.format("Found {0} block(s).", parts.length));

		int runningTotal = 0;
		for (String part : parts) {
			part = part.trim();
			if (!part.isEmpty()) {
				TextBlockParser parser = new TextBlockParser(part);
				runningTotal += parser.getBlockSize();
			}
		}

		logger.info("Found: " + runningTotal);
		return runningTotal;
	}
}
