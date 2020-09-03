package com.mosioj.entrainements.service.admin;

import com.mosioj.entrainements.entities.SavedTraining;
import com.mosioj.entrainements.entities.Training;
import com.mosioj.entrainements.entities.User;
import com.mosioj.entrainements.repositories.EntrainementRepository;
import com.mosioj.entrainements.repositories.SavedTrainingRepository;
import com.mosioj.entrainements.repositories.UserRepository;
import com.mosioj.entrainements.service.AbstractServiceTest;
import com.mosioj.entrainements.utils.TextUtils;
import com.mosioj.entrainements.utils.db.HibernateUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
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

    @Test
    public void testDeleteWithSmiley(@Mock HttpServletRequest request) throws Exception {

        // Given the training exists
        final String trainingText = "mon 👩‍🦰👱‍♂️🧒entrainement créé";
        Training training = new Training(trainingText, LocalDate.now());
        training.setText(TextUtils.transformSmileyToCode(training.getText()));
        HibernateUtil.saveit(training);

        // Reloading it
        training = EntrainementRepository.getById(training.getId()).orElseThrow(SQLException::new);
        assertEquals(trainingText, training.getText());
        when(request.getInputStream()).thenReturn(stringParametersToIS("id=" + training.getId()));

        // And we try to delete it as Admin
        StringServiceResponse resp = doDelete(request);

        // Then it does exist anymore...
        assertTrue(resp.isOK(), resp.toString());
        assertFalse(EntrainementRepository.getById(training.getId()).isPresent());
    }

    @Test
    public void testDeleteIsRemovingTheAttachedSavedTraining(@Mock HttpServletRequest request) throws Exception {

        // Given the training and the user exists
        final User user = UserRepository.getUser(1).orElseThrow(SQLException::new);
        final String trainingText = "mon 👩‍🦰👱‍♂️🧒entrainement créé";
        Training training = new Training(trainingText, LocalDate.now());
        training.setText(TextUtils.transformSmileyToCode(training.getText()));
        HibernateUtil.saveit(training);
        HibernateUtil.saveit(SavedTraining.of(user, training));

        // Reloading it
        training = EntrainementRepository.getById(training.getId()).orElseThrow(SQLException::new);
        assertEquals(trainingText, training.getText());
        assertTrue(SavedTrainingRepository.of(user, training).isPresent());
        when(request.getInputStream()).thenReturn(stringParametersToIS("id=" + training.getId()));

        // And we try to delete it as Admin
        StringServiceResponse resp = doDelete(request);

        // Then it does exist anymore...
        assertTrue(resp.isOK(), resp.toString());
        assertFalse(EntrainementRepository.getById(training.getId()).isPresent());
        assertFalse(SavedTrainingRepository.of(user, training).isPresent());
    }
}