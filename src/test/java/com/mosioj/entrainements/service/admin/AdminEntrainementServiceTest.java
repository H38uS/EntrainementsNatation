package com.mosioj.entrainements.service.admin;

import com.mosioj.entrainements.entities.Training;
import com.mosioj.entrainements.repositories.EntrainementRepository;
import com.mosioj.entrainements.service.AbstractServiceTest;
import com.mosioj.entrainements.utils.db.HibernateUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class AdminEntrainementServiceTest extends AbstractServiceTest<AdminEntrainementService> {

    public AdminEntrainementServiceTest() {
        super(new AdminEntrainementService());
    }

    @Test
    public void adminDeleteTrainings(@Mock HttpServletRequest request) throws Exception {

        // Given the training exists
        Training training = new Training("mon entrainement créé", LocalDate.now());
        HibernateUtil.saveit(training);
        assertTrue(EntrainementRepository.getById(training.getId()).isPresent());
        when(request.getInputStream()).thenReturn(stringParametersToIS("id=" + training.getId()));

        // And we try to delete it as Admin
        StringServiceResponse resp = doDelete(request);

        // Then it does exist anymore...
        assertTrue(resp.isOK(), resp.toString());
        assertFalse(EntrainementRepository.getById(training.getId()).isPresent());
    }
}