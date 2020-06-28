package com.mosioj.entrainements.service.pub;

import com.mosioj.entrainements.entities.Training;
import com.mosioj.entrainements.repositories.EntrainementRepository;
import com.mosioj.entrainements.service.AbstractServiceTest;
import com.mosioj.entrainements.service.response.EntrainementServiceResponse;
import com.mosioj.entrainements.service.response.ServiceResponse;
import com.mosioj.entrainements.utils.TextUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;

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
        assertEquals(TextUtils.interpreteMarkDown(dbTraining.getText()), training.getHtmlText());
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