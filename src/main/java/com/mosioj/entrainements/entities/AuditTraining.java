package com.mosioj.entrainements.entities;

import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity(name = "AUDIT_TRAINING")
public class AuditTraining {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long trainingId;

    @ManyToOne
    @JoinColumn(name = "coach")
    private Coach coach;

    @ManyToOne
    @JoinColumn(name = "createdBy")
    private User createdBy;

    @Column(length = 4000)
    private String text;

    @Column
    private Boolean requiresPull;

    @Column
    private Boolean requiresPlaques;

    @Column
    private Boolean requiresPalmes;

    @Column(name = "date_seance")
    private LocalDate dateSeance;

    /** Size of the training, in meters */
    @Column(nullable = false)
    private int size;

    @Column(nullable = false)
    private Boolean isLongCourse;

    @Column(nullable = false)
    private Boolean isCourseSizeDefinedForSure;

    @Column
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "updatedBy")
    private User updatedBy;

    @Column(length = 50)
    private String modificationType;

    /** Default constructor. */
    public AuditTraining() {
        // Used by Hibernate
    }

    /**
     * @param training The training.
     * @return The audit instance corresponding to the given training.
     */
    private static AuditTraining from(Training training) {
        AuditTraining auditTraining = new AuditTraining();
        auditTraining.trainingId = training.getId();
        auditTraining.coach = training.getCoach().orElse(null);
        auditTraining.createdBy = training.getCreatedBy();
        auditTraining.text = training.getText();
        auditTraining.requiresPull = training.doesRequirePull();
        auditTraining.requiresPlaques = training.doesRequirePlaques();
        auditTraining.requiresPalmes = training.doesRequirePalmes();
        auditTraining.dateSeance = training.getDateSeance();
        auditTraining.size = training.getSize();
        auditTraining.isLongCourse = training.isLongCourse();
        auditTraining.isCourseSizeDefinedForSure = training.isCourseSizeDefinedForSure();
        return auditTraining;
    }

    /**
     * Creates an audit instance before updating / deleting this training.
     *
     * @param training The training we want to modify.
     * @param user     The user that is about to modify this training.
     * @return The audit instance.
     */
    public static AuditTraining modifiedBy(Training training, User user) {
        AuditTraining auditTraining = from(training);
        auditTraining.updatedBy = user;
        auditTraining.modificationType = "UPDATE";
        return auditTraining;
    }

    /**
     * Creates an audit instance before updating / deleting this training.
     *
     * @param training The training we want to delete.
     * @param user     The user that is about to delete this training.
     * @return The audit instance.
     */
    public static AuditTraining deletedBy(Training training, User user) {
        AuditTraining auditTraining = from(training);
        auditTraining.updatedBy = user;
        auditTraining.modificationType = "DELETE";
        return auditTraining;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuditTraining training = (AuditTraining) o;
        return id.equals(training.id);
    }
}
