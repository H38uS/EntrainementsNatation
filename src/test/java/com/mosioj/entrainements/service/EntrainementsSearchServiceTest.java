package com.mosioj.entrainements.service;

import org.junit.Test;

import static org.junit.Assert.*;

public class EntrainementsSearchServiceTest {

    @Test
    public void validOrderClauseShouldBeRecognized() {
        assertTrue(EntrainementsSearchService.isAValidSortText("date_seance asc"));
        assertTrue(EntrainementsSearchService.isAValidSortText("  date_seance"));
        assertTrue(EntrainementsSearchService.isAValidSortText("size, date_seance desc"));
        assertTrue(EntrainementsSearchService.isAValidSortText("date_seance,date_seance,size"));
        assertTrue(EntrainementsSearchService.isAValidSortText("date_seance desc  ,      size asc"));
    }

    @Test
    public void invalidOrderClauseShouldBeRejected() {
        assertFalse(EntrainementsSearchService.isAValidSortText("  daTE_SEance"));
        assertFalse(EntrainementsSearchService.isAValidSortText("date_seance toto"));
        assertFalse(EntrainementsSearchService.isAValidSortText("date_seance asc asc"));
        assertFalse(EntrainementsSearchService.isAValidSortText("  unknown_column"));
        assertFalse(EntrainementsSearchService.isAValidSortText("size date_seance"));
        assertFalse(EntrainementsSearchService.isAValidSortText("date_seance;size"));
        assertFalse(EntrainementsSearchService.isAValidSortText("   "));
        assertFalse(EntrainementsSearchService.isAValidSortText(","));
    }
}