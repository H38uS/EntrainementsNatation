package com.mosioj.entrainements.service;

import com.mosioj.entrainements.service.pub.EntrainementsSearchService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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