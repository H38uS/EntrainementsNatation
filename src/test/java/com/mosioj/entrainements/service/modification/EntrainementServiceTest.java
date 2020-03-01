package com.mosioj.entrainements.service.modification;

import com.mosioj.entrainements.entities.Coach;
import com.mosioj.entrainements.entities.Training;
import com.mosioj.entrainements.repositories.CoachRepository;
import com.mosioj.entrainements.repositories.EntrainementRepository;
import com.mosioj.entrainements.service.AbstractServiceTest;
import com.mosioj.entrainements.service.response.ServiceResponse;
import com.mosioj.entrainements.utils.date.DateUtils;
import com.mosioj.entrainements.utils.db.HibernateUtil;
import org.hibernate.Transaction;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class EntrainementServiceTest extends AbstractServiceTest<EntrainementService> {

    private static final Long EXISTING_TRAINING_ID = 1L;
    private static final String EXISTING_TRAINING_TEXT = "does already exist !! ";

    private static final EntrainementService es = new EntrainementService();
    private static final String VALID_TRAINING_TEXT = "my training blablabla";
    private static final Integer VALID_SIZE = 2500;
    private static final String VALID_DATE = "2019-01-15";

    public EntrainementServiceTest() {
        super(new EntrainementService());
    }

    @Test
    public void shouldNotAcceptBlankTrainings() {
        assertTrue(es.checkParameter(Training.with(null, VALID_DATE, VALID_SIZE), true).size() > 0,
                   "Should have detected errors");
        assertTrue(es.checkParameter(Training.with("    ", VALID_DATE, VALID_SIZE), true).size() > 0,
                   "Should have detected errors");
        assertTrue(es.checkParameter(Training.with("", "aaaa", VALID_SIZE), true).size() > 0,
                   "Should have detected errors");
        assertTrue(es.checkParameter(Training.with("", "2019-01-15", VALID_SIZE), true).size() > 0,
                   "Should have detected errors");
        assertTrue(es.checkParameter(Training.with("", "", VALID_SIZE), false).size() > 0,
                   "Should have detected errors");
        assertTrue(es.checkParameter(Training.with("", "aaaa", VALID_SIZE), false).size() > 0,
                   "Should have detected errors");
        assertTrue(es.checkParameter(Training.with("", "2019-01-15", VALID_SIZE), false).size() > 0,
                   "Should have detected errors");
    }

    @Test
    public void shouldNotAcceptInvalidDate() {
        assertTrue(es.checkParameter(Training.with(VALID_TRAINING_TEXT, "", VALID_SIZE), true).size() > 0,
                   "Should have detected errors");
        assertTrue(es.checkParameter(Training.with(VALID_TRAINING_TEXT, "aaaa", VALID_SIZE), true).size() > 0,
                   "Should have detected errors");
        assertTrue(es.checkParameter(Training.with(VALID_TRAINING_TEXT, "le 25 décembre", VALID_SIZE), true).size() > 0,
                   "Should have detected errors");
        assertTrue(es.checkParameter(Training.with(VALID_TRAINING_TEXT, "25/12/2015", VALID_SIZE), true).size() > 0,
                   "Should have detected errors");
        assertTrue(es.checkParameter(Training.with(VALID_TRAINING_TEXT, "25-12-2015", VALID_SIZE), true).size() > 0,
                   "Should have detected errors");
        assertTrue(es.checkParameter(Training.with(VALID_TRAINING_TEXT, "2019--01-15", VALID_SIZE), true).size() > 0,
                   "Should have detected errors");
        assertTrue(es.checkParameter(Training.with(VALID_TRAINING_TEXT, "", VALID_SIZE), false).size() > 0,
                   "Should have detected errors");
        assertTrue(es.checkParameter(Training.with(VALID_TRAINING_TEXT, "aaaa", VALID_SIZE), false).size() > 0,
                   "Should have detected errors");
        assertTrue(es.checkParameter(Training.with(VALID_TRAINING_TEXT, "2019-20-15", VALID_SIZE), false).size() > 0,
                   "Should have detected errors");
    }

    @Test
    public void shouldNotAcceptInvalidSize() throws Exception {
        assertTrue(es.checkParameter(new Training(VALID_TRAINING_TEXT,
                                                  DateUtils.getAsDate(VALID_DATE).orElseThrow(Exception::new)), true)
                     .size() > 0,
                   "Should have detected errors");
        assertTrue(es.checkParameter(Training.with(VALID_TRAINING_TEXT, VALID_DATE, 0), true).size() > 0,
                   "Should have detected errors");
        assertTrue(es.checkParameter(Training.with(VALID_TRAINING_TEXT, VALID_DATE, -20), false).size() > 0,
                   "Should have detected errors");
    }

    @Test
    public void shouldNotAcceptAlreadyExistingOnes() throws Exception {

        // Given the training exists
        Training training = new Training(EXISTING_TRAINING_TEXT,
                                         DateUtils.getAsDate(VALID_DATE).orElseThrow(Exception::new));
        training.withSize(VALID_SIZE);
        HibernateUtil.doSomeWork(s -> {
            Transaction t = s.beginTransaction();
            s.saveOrUpdate(training);
            t.commit();
        });
        assertTrue(EntrainementRepository.getById(EXISTING_TRAINING_ID).isPresent());

        assertTrue(es.checkParameter(Training.with(EXISTING_TRAINING_TEXT, VALID_DATE, VALID_SIZE), true).size() > 0,
                   "Should have detected errors");
        assertEquals(0,
                     es.checkParameter(Training.with(EXISTING_TRAINING_TEXT, VALID_DATE, VALID_SIZE), false).size(),
                     "Should have detected errors");
    }

    @Test
    public void shouldNotAcceptSimilarTrainingWithoutForceToken(@Mock HttpServletRequest request) throws Exception {

        // Training inputs
        final String trainingText = "300 nl dos 200 batt (50 par côté) 100 4n\n" +
                                    "300 pull (2/3/4 temps enchaîné) 200 jbs spé 100 4n\n" +
                                    "\n" +
                                    "8x100 plaque palmes dép 1'20\n" +
                                    "3 allure moyenne 1 vite\n" +
                                    "\n" +
                                    "100 souple \n" +
                                    "\n" +
                                    "3x\n" +
                                    "200 nl pull plaque \n" +
                                    "100 spé éduc \n" +
                                    "2x50 max dép 1'30\n" +
                                    "100 souple";
        final LocalDate date1 = LocalDate.of(2020, 2, 28);
        final LocalDate date2 = LocalDate.of(2020, 2, 27);
        final int size = 3600;
        final Coach coach = CoachRepository.getCoachForName("Alice").orElseThrow(Exception::new);

        // Delete and create it
        Stream.of(date1, date2).forEach(d -> {
            EntrainementRepository.getTrainings(d, size, coach).forEach(HibernateUtil::deleteIt);
            assertEquals(0, EntrainementRepository.getTrainings(d, size, coach).size());
        });
        EntrainementRepository.getTrainings(date1, 3400, coach).forEach(HibernateUtil::deleteIt);
        assertEquals(0, EntrainementRepository.getTrainings(date1, 3400, coach).size());

        // Bind parameters
        Map<String, String []> parameters = new LinkedHashMap<>();
        parameters.put("training", new String[] {trainingText});
        parameters.put("size", new String[] {size+""});
        parameters.put("trainingdate", new String[] {date1.toString()});
        parameters.put("coach", new String[] {coach.getName()});
        when(request.getParameterMap()).thenReturn(parameters);

        // Creating a first training
        ServiceResponse resp = doPost(request);
        assertTrue(resp.isOK(), "Response => " + resp.toString());
        assertEquals(1, EntrainementRepository.getTrainings(date1, size, coach).size());

        // Bind parameters of new post -- similar training => not OK
        parameters.put("training", new String[] {trainingText + " with some more additionals"});
        resp = doPost(request);
        assertFalse(resp.isOK(), "Response => " + resp.toString());
        assertEquals(1, EntrainementRepository.getTrainings(date1, size, coach).size());

        // Bind parameters of new post -- different size => OK
        parameters.put("size", new String[] {"3400"});
        resp = doPost(request);
        assertTrue(resp.isOK(), "Response => " + resp.toString());
        assertEquals(1, EntrainementRepository.getTrainings(date1, size, coach).size());
        assertEquals(1, EntrainementRepository.getTrainings(date1, 3400, coach).size());

        // Bind parameters of new post -- different date => OK
        parameters.put("training", new String[] {trainingText + " with some other additionals"});
        parameters.put("size", new String[] {size+""});
        parameters.put("trainingdate", new String[] {date2.toString()});
        resp = doPost(request);
        assertTrue(resp.isOK(), "Response => " + resp.toString());
        assertEquals(1, EntrainementRepository.getTrainings(date1, size, coach).size());
        assertEquals(1, EntrainementRepository.getTrainings(date2, size, coach).size());
    }
}
