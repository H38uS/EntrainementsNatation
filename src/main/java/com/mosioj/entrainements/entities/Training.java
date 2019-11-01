package com.mosioj.entrainements.entities;

import java.util.Date;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.google.gson.annotations.Expose;
import com.mosioj.entrainements.utils.MySimpleDateFormat;

@Entity(name = "TRAINING")
public class Training {

	private static final MySimpleDateFormat SDF = new MySimpleDateFormat("EEEE d MMMM yyyy");

	@Id
	@Expose
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "coach")
	@Expose
	private Coach coach;
	
	@ManyToOne
	@JoinColumn(name = "createdBy")
	@Expose
	private User createdBy;

	@Column(length = 4000)
	@Expose
	private String text;

	@Column(name = "date_seance")
	@Temporal(TemporalType.DATE)
	@Expose
	private Date dateSeance;

	@Transient
	@Expose
	private String dateSeanceString;

	/** Size of the training, in meters */
	@Column(nullable = false)
	@Expose
	private int size;

	@Column(nullable = false)
	@Expose
	private Boolean isLongCourse = false;

	@Column(nullable = false)
	@Expose
	private Boolean isCourseSizeDefinedForSure = false;

	@Column(updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@CreationTimestamp
	private Date createdAt;

	@Column
	@Temporal(TemporalType.TIMESTAMP)
	@UpdateTimestamp
	private Date updatedAt;

	/**
	 * Default constructor.
	 */
	public Training() {
		// Used by Hibernate
	}

	/**
	 * 
	 * @param trainingText
	 * @param size
	 * @param date
	 * @param coach2
	 * @param poolsizeParam
	 */
	public Training(String trainingText, Optional<Integer> size, Date date, Optional<Coach> coach, String poolsize) {
		text = trainingText.replaceAll("’", "'").replaceAll("–", "-");
		this.size = size.orElse(0);
		dateSeance = date;
		this.coach = coach.isPresent() ? coach.get() : null;
		isLongCourse = "long".equals(poolsize);
		isCourseSizeDefinedForSure = poolsize != null && !poolsize.trim().isEmpty();
	}

	/**
	 * @return the createdBy
	 */
	public User getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the coach
	 */
	public Coach getCoach() {
		return coach;
	}

	/**
	 * @param coach the coach to set
	 */
	public void setCoach(Coach coach) {
		this.coach = coach;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @return the dateSeance
	 */
	public Date getDateSeance() {
		return dateSeance;
	}

	/**
	 * 
	 * @return the dateSeance, human readable
	 */
	public String getDateSeanceString() {
		return dateSeanceString;
	}

	/**
	 * 
	 * @return the dateSeance, human readable
	 */
	public void computeDateSeanceString() {
		dateSeanceString = SDF.format(dateSeance);
		dateSeanceString = dateSeanceString.substring(0, 1).toUpperCase() + dateSeanceString.substring(1);
	}

	/**
	 * @param dateSeance the dateSeance to set
	 */
	public void setDateSeance(Date dateSeance) {
		this.dateSeance = dateSeance;
	}

	/**
	 * @return the size
	 */
	public int getSize() {
		return size;
	}

	/**
	 * @param size the size to set
	 */
	public void setSize(int size) {
		this.size = size;
	}

	/**
	 * @return the isLongCourse
	 */
	public Boolean getIsLongCourse() {
		return isLongCourse;
	}

	/**
	 * @param isLongCourse the isLongCourse to set
	 */
	public void setIsLongCourse(Boolean isLongCourse) {
		this.isLongCourse = isLongCourse;
	}

	/**
	 * @return the isCourseSizeDefinedForSure
	 */
	public Boolean getIsCourseSizeDefinedForSure() {
		return isCourseSizeDefinedForSure;
	}

	/**
	 * @param isCourseSizeDefinedForSure the isCourseSizeDefinedForSure to set
	 */
	public void setIsCourseSizeDefinedForSure(Boolean isCourseSizeDefinedForSure) {
		this.isCourseSizeDefinedForSure = isCourseSizeDefinedForSure;
	}

	/**
	 * @return the createdAt
	 */
	public Date getCreatedAt() {
		return createdAt;
	}

	/**
	 * @param createdAt the createdAt to set
	 */
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	/**
	 * @return the updatedAt
	 */
	public Date getUpdatedAt() {
		return updatedAt;
	}

	/**
	 * @param updatedAt the updatedAt to set
	 */
	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

}
