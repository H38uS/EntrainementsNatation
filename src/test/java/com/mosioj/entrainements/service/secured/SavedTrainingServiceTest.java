package com.mosioj.entrainements.service.secured;

import com.mosioj.entrainements.entities.Training;
import com.mosioj.entrainements.entities.User;
import com.mosioj.entrainements.filter.LoginFilter;
import com.mosioj.entrainements.repositories.EntrainementRepository;
import com.mosioj.entrainements.repositories.SavedTrainingRepository;
import com.mosioj.entrainements.repositories.UserRepository;
import com.mosioj.entrainements.service.AbstractServiceTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class SavedTrainingServiceTest extends AbstractServiceTest<SavedTrainingService> {

    public SavedTrainingServiceTest() {
        super(new SavedTrainingService());
    }

    @Test
    public void testInsertAndThenDelete(@Mock HttpServletRequest request) throws SQLException, IOException {

        Training training = EntrainementRepository.getATraining().orElseThrow(SQLException::new);
        User user = UserRepository.getUser(1).orElseThrow(SQLException::new);
        when(request.getAttribute(LoginFilter.PARAM_CONNECTED_USER)).thenReturn(user);
        SavedTrainingRepository.delete(user, training);
        assertFalse(SavedTrainingRepository.of(user, training).isPresent());

        when(request.getParameter("trainingId")).thenReturn(training.getId() + "");
        StringServiceResponse resp = doPost(request);
        assertTrue(resp.isOK(), resp.toString());
        assertTrue(SavedTrainingRepository.of(user, training).isPresent());

        when(request.getInputStream()).thenReturn(stringParametersToIS("trainingId=" + training.getId()));

        resp = doDelete(request);
        assertTrue(resp.isOK(), resp.toString());
        assertFalse(SavedTrainingRepository.of(user, training).isPresent());
    }
}