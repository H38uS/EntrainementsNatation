package com.mosioj.entrainements.service.pub;

import com.mosioj.entrainements.entities.SavedTraining;
import com.mosioj.entrainements.entities.Training;
import com.mosioj.entrainements.entities.User;
import com.mosioj.entrainements.filter.LoginFilter;
import com.mosioj.entrainements.repositories.EntrainementRepository;
import com.mosioj.entrainements.repositories.SavedTrainingRepository;
import com.mosioj.entrainements.repositories.UserRepository;
import com.mosioj.entrainements.service.AbstractServiceTest;
import com.mosioj.entrainements.service.response.EntrainementServiceResponse;
import com.mosioj.entrainements.service.response.ServiceResponse;
import com.mosioj.entrainements.utils.TextUtils;
import com.mosioj.entrainements.utils.db.HibernateUtil;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalMatchers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class EntrainementsSearchServiceTest extends AbstractServiceTest<EntrainementsSearchService> {

    public EntrainementsSearchServiceTest() {
        super(new EntrainementsSearchService());
    }

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

    @Test
    public void shouldFillTheHTMLField(@Mock HttpServletRequest request) throws Exception {

        MyServiceResponse resp = doGet(request, MyServiceResponse.class);

        assertTrue(resp.isOK());
        EntrainementServiceResponse trainings = resp.getMessage();
        trainings.getTrainings().forEach(t -> assertNotNull(t.getHtmlText()));
        Training training = trainings.getTrainings().stream().findAny().orElseThrow(Exception::new);
        Training dbTraining = EntrainementRepository.getById(training.getId()).orElseThrow(Exception::new);
        assertEquals(TextUtils.interpreteMarkDown(TextUtils.transformCodeToSmiley(dbTraining.getText())), training.getHtmlText());
    }

    @Test
    public void userCanFilterInHisFav(@Mock HttpServletRequest r1, @Mock HttpServletRequest r2) throws SQLException {

        // Connected user
        User user = UserRepository.getUser(1).orElseThrow(SQLException::new);
        when(r1.getAttribute(LoginFilter.PARAM_CONNECTED_USER)).thenReturn(user);

        // One saved training
        Training training = EntrainementRepository.getATraining().orElseThrow(SQLException::new);
        SavedTrainingRepository.delete(user, training);
        HibernateUtil.saveit(SavedTraining.of(user, training));

        when(r1.getParameter(Mockito.anyString())).thenReturn("");

        // On a des entrainements qui ne sont pas dans les favoris
        MyServiceResponse resp = doGet(r1, MyServiceResponse.class);
        assertTrue(resp.isOK());
        List<Training> trainings = resp.getMessage()
                                       .getTrainings()
                                       .stream()
                                       .filter(t -> !SavedTrainingRepository.of(user, t).isPresent())
                                       .collect(Collectors.toList());
        assertFalse(trainings.isEmpty(), "Aucun entrainement non favoris...");

        when(r2.getParameter(AdditionalMatchers.not(ArgumentMatchers.eq("only_favs")))).thenReturn("");
        when(r2.getParameter("only_fav")).thenReturn("true");
        assertEquals("true", r2.getParameter("only_fav"));
        resp = doGet(r2, MyServiceResponse.class);
        assertTrue(resp.isOK());

        trainings = resp.getMessage()
                        .getTrainings()
                        .stream()
                        .filter(t -> !SavedTrainingRepository.of(user, t).isPresent())
                        .collect(Collectors.toList());
        assertTrue(trainings.isEmpty(), "Ces entrainements ne sont pas dans les favoris... => " + trainings);
    }

    private static final class MyServiceResponse extends ServiceResponse<EntrainementServiceResponse> {
        /**
         * Class constructor.
         *
         * @param isOK    Whether this call was successful.
         * @param message The message we want to send back.
         * @param request The http request being answered.
         */
        public MyServiceResponse(boolean isOK, EntrainementServiceResponse message, HttpServletRequest request) {
            super(isOK, message, request);
        }
    }
}