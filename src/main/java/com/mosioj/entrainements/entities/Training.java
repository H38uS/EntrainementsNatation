package com.mosioj.entrainements.entities;

import com.google.gson.annotations.Expose;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Entity(name = "TRAINING")
public class Training {

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
    @Expose
    private LocalDate dateSeance;

    @Transient
    @Expose
    private String dateSeanceString;

    /**
     * Size of the training, in meters
     */
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
    @CreationTimestamp
    @Expose
    private LocalDateTime createdAt;

    @Column
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    /**
     * Default constructor.
     */
    public Training() {
        // Used by Hibernate
    }

    public Training(String trainingText, LocalDate date) {
        text = trainingText.replaceAll("’", "'").replaceAll("–", "-");
        dateSeance = date;
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

    public void computeDateSeanceString() {
        dateSeanceString = dateSeance.format(DateTimeFormatter.ofPattern("EEEE d MMMM yyyy").withLocale(Locale.FRENCH));
        dateSeanceString = dateSeanceString.substring(0, 1).toUpperCase() + dateSeanceString.substring(1);
    }

    /**
     * @param dateSeance the dateSeance to set
     */
    public void setDateSeance(LocalDate dateSeance) {
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
     * @param isLongCourse the isLongCourse to set
     */
    public void setIsLongCourse(Boolean isLongCourse) {
        this.isLongCourse = isLongCourse;
        isCourseSizeDefinedForSure = true;
    }

    /**
     * @param isCourseSizeDefinedForSure the isCourseSizeDefinedForSure to set
     */
    public void setIsCourseSizeDefinedForSure(Boolean isCourseSizeDefinedForSure) {
        this.isCourseSizeDefinedForSure = isCourseSizeDefinedForSure;
    }
}
