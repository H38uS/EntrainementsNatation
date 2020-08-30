package com.mosioj.entrainements.model;

import com.mosioj.entrainements.entities.Coach;

public class SearchCriteria {

    /** The minimal size of the training. */
    private final int minimalSize;
    // TODO peut-être mettre des Integer ici pour pouvoir sauvegarder en bdd

    /** The maximal size of the training. */
    private final int maximalSize;

    /** The starting month of the search, inclusive. 01 is for January. */
    private final int fromMonthInclusive;

    /** The ending month of the search, inclusive. 01 is for January. */
    private final int toMonthInclusive;

    /** The day of week. 1 = Monday, 2 = Tuesday, etc. */
    private Integer dayOfWeek;

    /** This search coach */
    private Coach coach;

    /**
     * @param maximalSize        The minimal size of the training.
     * @param minimalSize        The maximal size of the training.
     * @param toMonthInclusive   The starting month of the search, inclusive. 01 is for January.
     * @param fromMonthInclusive The ending month of the search, inclusive. 01 is for January.
     */
    private SearchCriteria(int minimalSize, int maximalSize, int fromMonthInclusive, int toMonthInclusive) {
        this.minimalSize = minimalSize;
        this.maximalSize = maximalSize;
        this.fromMonthInclusive = fromMonthInclusive;
        this.toMonthInclusive = toMonthInclusive;
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
     * @return True if and only if we should use OR for months.
     */
    public boolean shouldUseOrOperator() {
        return fromMonthInclusive > toMonthInclusive;
    }

    /**
     * @return The minimal size of the training.
     */
    public int getMinimalSize() {
        return minimalSize;
    }

    /**
     * @return The maximal size of the training.
     */
    public int getMaximalSize() {
        return maximalSize;
    }

    /**
     * @return The starting month of the search, inclusive. 01 is for January.
     */
    public int getFromMonthInclusive() {
        return fromMonthInclusive;
    }

    /**
     * @return The ending month of the search, inclusive. 01 is for January.
     */
    public int getToMonthInclusive() {
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

        // Copy the values - might be null
        Integer from = fromMonthInclusive;
        Integer to = toMonthInclusive;

        // Handling null cases
        if (fromMonthInclusive == null) {
            from = toMonthInclusive == null ? 1 : 13;
        }
        if (toMonthInclusive == null) {
            to = fromMonthInclusive == null ? 12 : -1;
        }

        int min = minimalSize == null ? 0 : minimalSize;
        int max = maximalSize == null ? Integer.MAX_VALUE : maximalSize;

        return new SearchCriteria(min, max, from, to);
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
