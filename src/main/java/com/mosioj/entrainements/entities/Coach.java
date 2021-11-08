package com.mosioj.entrainements.entities;

import com.google.gson.annotations.Expose;

import javax.persistence.*;
import java.util.Objects;

@Entity(name = "COACH")
public class Coach {

    @Id
    @Expose
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, unique = true)
    @Expose
    private String name;

    @Column(length = 50)
    @Expose
    private String club;

    public Coach() {
        // Nothing to do, for Hibernate...
    }

    /**
     * Class constructor.
     *
     * @param name The coach name.
     */
    public Coach(String name) {
        this.name = name;
    }

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

    @Override
    public String toString() {
        return "Coach{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", club='" + club + '\'' +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coach coach = (Coach) o;
        return Objects.equals(id, coach.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
