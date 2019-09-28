package com.mosioj.entrainements.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.google.gson.annotations.Expose;

@Entity(name = "COACH")
public class Coach {

	@Id
	@Column(length = 50)
	@Expose
	private String name;
	
	@Column(length = 50)
	@Expose
	private String club;
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the club
	 */
	public String getClub() {
		return club;
	}

	/**
	 * @param club the club to set
	 */
	public void setClub(String club) {
		this.club = club;
	}
}
