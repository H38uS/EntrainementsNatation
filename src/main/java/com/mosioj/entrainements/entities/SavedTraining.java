package com.mosioj.entrainements.entities;

import com.google.gson.annotations.Expose;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity(name = "SAVED_TRAINING")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "trainingId"}))
public class SavedTraining {

    @Id
    @Expose
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "userId")
    @Expose
    private User byUser;

    @ManyToOne
    @JoinColumn(name = "trainingId")
    @Expose
    private Training training;

    @Column(updatable = false)
    @CreationTimestamp
    @Expose
    private LocalDateTime createdAt;

    /**
     * Used by hibernate.
     */
    public SavedTraining() {
        // Used by hibernate
    }

    /**
     * @param user     The user that wants to of this training.
     * @param training The training.
     */
    public SavedTraining(User user, Training training) {
        this.byUser = user;
        this.training = training;
    }

    /**
     * @param training The new training.
     */
    public void setTraining(Training training) {
        this.training = training;
    }

    /**
     * @return The corresponding training.
     */
    public Training getTraining() {
        return training;
    }

    /**
     * @param user     The user that wants to of this training.
     * @param training The training.
     * @return The new instance.
     */
    public static SavedTraining of(User user, Training training) {
        return new SavedTraining(user, training);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SavedTraining that = (SavedTraining) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
