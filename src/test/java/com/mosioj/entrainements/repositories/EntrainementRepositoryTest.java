package com.mosioj.entrainements.repositories;

import org.junit.jupiter.api.Test;

class EntrainementRepositoryTest {

    @Test
    public void getDoublonsShouldNotThrow() {
        EntrainementRepository.getDoublons();
    }
}