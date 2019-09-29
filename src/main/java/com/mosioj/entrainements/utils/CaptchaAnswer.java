package com.mosioj.entrainements.utils;

import java.util.List;

import com.google.gson.annotations.Expose;

public class CaptchaAnswer {

	@Expose
	private boolean success;

	@Expose
	private String challenge_ts;

	@Expose
	private String hostname;

	@Expose
	private List<String> errorCodes;

	/**
	 * @return the success
	 */
	@SuppressWarnings("unused")
	public boolean isSuccess() {
		return success;
	}

	/**
	 * @param success the success to set
	 */
	@SuppressWarnings("unused")
	public void setSuccess(boolean success) {
		this.success = success;
	}

	/**
	 * @return the challenge_ts
	 */
	@SuppressWarnings("unused")
	public String getChallenge_ts() {
		return challenge_ts;
	}

	/**
	 * @param challenge_ts the challenge_ts to set
	 */
	@SuppressWarnings("unused")
	public void setChallenge_ts(String challenge_ts) {
		this.challenge_ts = challenge_ts;
	}

	/**
	 * @return the hostname
	 */
	@SuppressWarnings("unused")
	public String getHostname() {
		return hostname;
	}

	/**
	 * @param hostname the hostname to set
	 */
	@SuppressWarnings("unused")
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	/**
	 * @return the errorCodes
	 */
	@SuppressWarnings("unused")
	public List<String> getErrorCodes() {
		return errorCodes;
	}

	/**
	 * @param errorCodes the errorCodes to set
	 */
	@SuppressWarnings("unused")
	public void setErrorCodes(List<String> errorCodes) {
		this.errorCodes = errorCodes;
	}

	@Override
	public String toString() {
		return success + " / " + challenge_ts + " / " + hostname + " / " + errorCodes;
	}
}
