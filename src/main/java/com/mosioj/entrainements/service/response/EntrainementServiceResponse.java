package com.mosioj.entrainements.service.response;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.mosioj.entrainements.entities.Training;

public class EntrainementServiceResponse {

	@Expose
	private final List<Training> trainings;

	@Expose
	private final int maxResultPerPage;

	@Expose
	private final long totalNbOfResults;

	/**
	 * 
	 * @param trainings Les entrainemenst envoyés dans cette réponse.
	 * @param totalNbOfResults Le nombre total d'entrainements correspondant aux critères demandés.
	 * @param maxResultPerPage Le nombre maximum de résultats renvoyés par page à chaque requête.
	 */
	public EntrainementServiceResponse(List<Training> trainings, long totalNbOfResults, int maxResultPerPage) {
		this.trainings = trainings;
		this.totalNbOfResults = totalNbOfResults;
		this.maxResultPerPage = maxResultPerPage;
	}

	/**
	 * @return the list of training
	 */
	public List<Training> getTrainings() {
		return trainings;
	}

	/**
	 * @return the totalNbOfResults
	 */
	public long getTotalNbOfResults() {
		return totalNbOfResults;
	}
}
