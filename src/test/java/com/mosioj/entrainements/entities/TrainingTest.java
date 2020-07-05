package com.mosioj.entrainements.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TrainingTest {

    @Test
    public void shouldDetectPull() {
        Training training = new Training("toto à la plage avec ses palmes.", null);
        assertFalse(training.doesRequirePull());
        training = new Training("toto à la plage avec ses palmes et son pUll quand même !", null);
        assertTrue(training.doesRequirePull());
    }

    @Test
    public void shouldDetectPlaques() {
        Training training = new Training("toto à la plage avec ses palmEs et ses pliQues.", null);
        assertFalse(training.doesRequirePlaques());
        training = new Training("toto à la plage avec ses plmes et ses plaQuessss.", null);
        assertTrue(training.doesRequirePlaques());
    }

    @Test
    public void shouldDetectPalmes() {
        Training training = new Training("toto à la plage avec ses palmEs.", null);
        assertTrue(training.doesRequirePalmes());
        training = new Training("toto à la plage avec ses plmes.", null);
        assertFalse(training.doesRequirePalmes());
    }
}