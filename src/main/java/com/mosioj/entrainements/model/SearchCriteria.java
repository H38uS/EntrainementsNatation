package com.mosioj.entrainements.model;

import com.google.gson.annotations.Expose;
import com.mosioj.entrainements.entities.Coach;
import com.mosioj.entrainements.entities.User;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "SEARCH_CRITERIA")
public class SearchCriteria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The minimal size of the training. */
    @Column
    @Expose
    private Integer minimalSize;

    /** The maximal size of the training. */
    @Column
    @Expose
    private Integer maximalSize;

    /** The starting month of the search, inclusive. 01 is for January. */
    @Column
    @Expose
    private Integer fromMonthInclusive;

    /** The ending month of the search, inclusive. 01 is for January. */
    @Column
    @Expose
    private Integer toMonthInclusive;

    /** The day of week. 1 = Monday, 2 = Tuesday, etc. */
    @Column
    @Expose
    private Integer dayOfWeek;

    /** This search coach */
    @ManyToOne
    @JoinColumn(name = "coach")
    @Expose
    private Coach coach;

    /** The optional user that might have saved this. */
    @ManyToOne
    @JoinColumn(name = "savedBy", unique = true)
    private User savedBy;

    @Column(updatable = false)
    @CreationTimestamp
    @Expose
    private LocalDateTime createdAt;

    /** Default constructor. */
    private SearchCriteria() {
        // For hibernate & factory.
    }

    /**
     * Filters on a coach.
     *
     * @param coach The coach.
     */
    public void setCoach(Coach coach) {
        this.coach = coach;
    }

    /**
     * Sets a new day of week.
     *
     * @param dayOfWeek The day of week. 1 = Monday, 2 = Tuesday, etc.
     */
    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    /**
     * Link this criteria to a user.
     *
     * @param user The optional user that might have saved this.
     */
    public void setUser(User user) {
        this.savedBy = user;
    }

    /**
     * @return True if and only if we should use OR for months.
     */
    public boolean shouldUseOrOperator() {
        return getFromMonthInclusive() > getToMonthInclusive();
    }

    /**
     * @return The minimal size of the training.
     */
    public int getMinimalSize() {
        return minimalSize == null ? 0 : minimalSize;
    }

    /**
     * @return The maximal size of the training.
     */
    public int getMaximalSize() {
        return maximalSize == null ? Integer.MAX_VALUE : maximalSize;
    }

    /**
     * @return The starting month of the search, inclusive. 01 is for January.
     */
    public int getFromMonthInclusive() {
        if (fromMonthInclusive == null) {
            return toMonthInclusive == null ? 1 : 13;
        }
        return fromMonthInclusive;
    }

    /**
     * @return The ending month of the search, inclusive. 01 is for January.
     */
    public int getToMonthInclusive() {
        if (toMonthInclusive == null) {
            return fromMonthInclusive == null ? 12 : -1;
        }
        return toMonthInclusive;
    }

    /**
     * @return The coach on which we want to filter if any, or null.
     */
    public Coach getCoach() {
        return coach;
    }

    /**
     * MySQL format: 1 = Sunday, 2 = Monday, etc.
     *
     * @return The day of week to filter on in MySQL.
     */
    public Integer getDayOfWeek() {
        if (dayOfWeek != null) {
            if (dayOfWeek == 7) {
                // Sunday
                return 1;
            }
            // Monday goes from 1 to 2, Tuesday from 2 to 3, etc.
            return dayOfWeek + 1;
        }
        return dayOfWeek;
    }

    /**
     * All parameters might be null.
     *
     * @param maximalSize        The minimal size of the training.
     * @param minimalSize        The maximal size of the training.
     * @param toMonthInclusive   The starting month of the search, inclusive. 01 is for January.
     * @param fromMonthInclusive The ending month of the search, inclusive. 01 is for January.
     * @return A new search criteria.
     */
    public static SearchCriteria build(Integer minimalSize,
                                       Integer maximalSize,
                                       Integer fromMonthInclusive,
                                       Integer toMonthInclusive) {
        SearchCriteria criteria = new SearchCriteria();
        criteria.minimalSize = minimalSize;
        criteria.maximalSize = maximalSize;
        criteria.fromMonthInclusive = fromMonthInclusive;
        criteria.toMonthInclusive = toMonthInclusive;
        return criteria;
    }

    @Override
    public String toString() {
        return "SearchCriteria{" +
               "minimalSize=" + minimalSize +
               ", maximalSize=" + maximalSize +
               ", fromMonthInclusive=" + fromMonthInclusive +
               ", toMonthInclusive=" + toMonthInclusive +
               ", dayOfWeek=" + dayOfWeek +
               ", coach=" + coach +
               '}';
    }
}
