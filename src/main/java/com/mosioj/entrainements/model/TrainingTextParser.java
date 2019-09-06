package com.mosioj.entrainements.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TrainingTextParser {

	private static final String START_WITH_A_NUMBER = "(\\d\\d+|\\d[xX])(.*\\r?\\n?)*";
	private static final String DOES_NOT_START_WITH_A_NUMBER = "\\D+(.*\\r?\\n?)*";
	private static final String CONTAINS_NUMBER = "(.*\\r?\\n?)*(\\d\\d+|\\d[xX])(.*\\r?\\n?)*";

	private static final Logger logger = LogManager.getLogger(TrainingTextParser.class);

	private final String text;
	private String remaining;

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
	 * @param text The training text.
	 * @return True if the text can be entered.
	 */
	public boolean isTextValid() {
		return !text.contains("’");
	}

	/**
	 * 
	 * @return The possible size of the training, based on the text.
	 */
	public int getTrainingSize() {

		// Init et suppression des départs
		remaining = text;
		int runningTotal = 0;
		remaining = remaining.replaceAll("\\d+'\\d*", "");
		remaining = remaining.replaceAll("PAR \\d+", "");
		remaining = remaining.replaceAll("par \\d+", "");

		logger.info("Parsing: " + remaining);
		while (remaining.matches(CONTAINS_NUMBER)) {
			avoidParenthesis();
			moveToNextNumberStart();
			runningTotal += checkAndReadSubPartIfFound(readNextNumber());
		}

		logger.info("Found: " + runningTotal);
		return runningTotal;
	}

	/**
	 * Must be called just have read a number!
	 * 
	 * @param lastNumberRead The number just read.
	 * @return Either the current number if there is no subpart, or the current number times the subpart.
	 */
	protected int checkAndReadSubPartIfFound(int lastNumberRead) {
		char nextChar = remaining.charAt(0);
		remaining = remaining.substring(1);
		if (nextChar == 'x' || nextChar == 'X') {
			int total = readSubPart();
			logger.debug("SubPart with factor: " + (lastNumberRead * total));
			return lastNumberRead * total;
		} else {
			// On est pas dans une sub part
			// On évite donc les parenthèses
			avoidParenthesis();
		}
		return lastNumberRead;
	}

	/**
	 * If there are parenthesis before the next number, skip the full parenthesis.
	 */
	protected void avoidParenthesis() {
		if (remaining.contains("(") && !containsNumberBeforeParenthesis('(')) {
			remaining = remaining.substring(remaining.indexOf('('));
			if (!remaining.contains(")")) {
				logger.warn("Cannot find closing parenthesis... Remaining: " + remaining);
				return;
			}
			remaining = remaining.substring(remaining.indexOf(')'));
		}
	}

	/**
	 * Consumes char until we match the next number.
	 */
	protected void moveToNextNumberStart() {
		while (!remaining.matches(START_WITH_A_NUMBER)) {
			logger.trace("Remaining: " + remaining);
			logger.trace("Matches: " + remaining.matches(START_WITH_A_NUMBER));
			remaining = remaining.substring(1);
		}
	}

	/**
	 * 
	 * @return The next number read from remaining.
	 */
	protected int readNextNumber() {
		StringBuilder sb = new StringBuilder();
		while (!remaining.matches(DOES_NOT_START_WITH_A_NUMBER) && remaining.length() > 0) {
			logger.trace("Remaining: " + remaining);
			logger.trace("Matches: " + remaining.matches(DOES_NOT_START_WITH_A_NUMBER));
			sb.append(remaining.charAt(0));
			remaining = remaining.substring(1);
		}
		int factor = Integer.parseInt(sb.toString());
		logger.debug("Number: " + factor);
		return factor;
	}

	/**
	 * 
	 * @return True if and only if there is a number before the next parenthesis. False if there is no number.
	 */
	private boolean containsNumberBeforeParenthesis(char parenthesis) {
		for (int i = 0; i < remaining.length(); i++) {
			char c = remaining.charAt(i);
			if (c == parenthesis) {
				return false;
			}
			if (Character.isDigit(c)) {
				i++;
				if (i < remaining.length()) {
					char nextOne = remaining.charAt(i);
					if (nextOne == 'x' || nextOne == 'X' || Character.isDigit(nextOne)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 
	 * @return True if and only if the next number is before the next double carriage return.
	 */
	private boolean containsNextNumberBeforeNextDoubleCarriageReturn() {
		for (int i = 0; i < remaining.length(); i++) {
			char c = remaining.charAt(i);
			if (c == '\r') {
				i++;
				if (i == remaining.length()) return false;
				c = remaining.charAt(i);
				if (c == '\n') {
					i++;
					if (i == remaining.length()) return false;
					c = remaining.charAt(i);
					while (c == ' ' || c == '\t') {
						i++;
						c = remaining.charAt(i);
					}
					if (c == '\r') {
						i++;
						if (i == remaining.length()) return false;
						c = remaining.charAt(i);
						if (c == '\n') {
							return false;
						}
					}
					if (c == '\n') {
						return false;
					}
				}
			}
			if (c == '\n') {
				i++;
				if (i == remaining.length()) return false;
				c = remaining.charAt(i);
				while (c == ' ' || c == '\t') {
					i++;
					if (i == remaining.length()) return false;
					c = remaining.charAt(i);
				}
				if (c == '\r') {
					i++;
					if (i == remaining.length()) return false;
					c = remaining.charAt(i);
					if (c == '\n') {
						return false;
					}
					continue;
				}
				if (c == '\n') {
					return false;
				}
			}
			if (Character.isDigit(c)) {
				i++;
				if (i == remaining.length()) return false;
				char nextOne = remaining.charAt(i);
				if (nextOne == 'x' || nextOne == 'X' || Character.isDigit(nextOne)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 
	 * @return The size in meters of this subpart.
	 */
	private int readSubPart() {

		int runningTotal = 0;
		
		if (remaining.matches(START_WITH_A_NUMBER)) {
			runningTotal = readNextNumber();
			logger.debug("Number start matched! Sub part returned: " + runningTotal);
			return runningTotal;
		}

		// Remove spaces
		while (remaining.charAt(0) == ' ') {
			remaining = remaining.substring(1);
		}

		if (remaining.charAt(0) == '(') {
			int pos = remaining.indexOf(")");
			if (pos == -1) {
				logger.warn("Cannot find matching parenthesis... Remaining: " + remaining);
				return 0;
			}
			while (containsNumberBeforeParenthesis(')')) {
				moveToNextNumberStart();
				runningTotal += readNextNumber();
			}
			remaining = remaining.substring(remaining.indexOf(")"));
			logger.debug("Parenthesis mode... Sub part returned: " + runningTotal);
			return runningTotal;
		}

		while (containsNextNumberBeforeNextDoubleCarriageReturn()) {
			moveToNextNumberStart();
			runningTotal += checkAndReadSubPartIfFound(readNextNumber());
		}
		logger.debug("Sub part returned: " + runningTotal);
		return runningTotal;
	}
}
