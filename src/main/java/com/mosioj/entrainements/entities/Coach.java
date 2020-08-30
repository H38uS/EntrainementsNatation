package com.mosioj.entrainements.entities;

import com.google.gson.annotations.Expose;

import javax.persistence.*;

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
}
