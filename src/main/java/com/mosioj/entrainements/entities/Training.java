package com.mosioj.entrainements.entities;

import com.google.gson.annotations.Expose;
import com.mosioj.entrainements.utils.TextUtils;
import com.mosioj.entrainements.utils.date.DateUtils;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

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

    @Transient
    @Expose
    private String htmlText;

    @Column(name = "date_seance")
    @Expose
    private LocalDate dateSeance;

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
        text = trainingText == null ? "" : trainingText.trim();
        text = text.replaceAll("’", "'").replaceAll("–", "-");
        dateSeance = date;
        postLoad();
    }

    @PostLoad
    private void postLoad() {
        htmlText = TextUtils.interpreteMarkDown(text);
        if (dateSeance != null) {
            dateSeanceString = dateSeance.format(DateTimeFormatter.ofPattern("EEEE d MMMM yyyy").withLocale(Locale.FRENCH));
            dateSeanceString = dateSeanceString.substring(0, 1).toUpperCase() + dateSeanceString.substring(1);
        }
    }

    /**
     * @param createdBy the createdBy to set
     */
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * @return The training text as HTML.
     */
    public String getHtmlText() {
        return htmlText;
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
    public Optional<Coach> getCoach() {
        return Optional.ofNullable(coach);
    }

    /**
     * @param coach the coach to set
     */
    public Training withCoach(Coach coach) {
        this.coach = coach;
        return this;
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
     * @return La date de la séance.
     */
    public LocalDate getDateSeance() {
        return dateSeance;
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
    public Training withSize(int size) {
        this.size = size;
        return this;
    }

    /**
     * @param isLongCourse the isLongCourse to set
     */
    public void setIsLongCourse(Boolean isLongCourse) {
        this.isLongCourse = isLongCourse;
        isCourseSizeDefinedForSure = true;
    }

    /**
     * Sets the created at attribute. Only usefull when duplicating the training.
     *
     * @param createdAt The previous create date time.
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * @return The corresponding training.
     */
    public static Training with(String text, String date, int size) {
        return new Training(text, DateUtils.getAsDate(date).orElse(null)).withSize(size);
    }

    /**
     * @return The initial creator.
     */
    public User getCreatedBy() {
        return createdBy;
    }

    /**
     * @return The creation date.
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
