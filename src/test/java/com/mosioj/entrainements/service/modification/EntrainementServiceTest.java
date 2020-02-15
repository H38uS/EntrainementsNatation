package com.mosioj.entrainements.service.modification;

import com.mosioj.entrainements.entities.Training;
import com.mosioj.entrainements.repositories.EntrainementRepository;
import com.mosioj.entrainements.utils.date.DateUtils;
import com.mosioj.entrainements.utils.db.HibernateUtil;
import org.hibernate.Transaction;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EntrainementServiceTest {

    private static final Long EXISTING_TRAINING_ID = 1L;
    private static final String EXISTING_TRAINING_TEXT = "does already exist !! ";

    private static final EntrainementService es = new EntrainementService();
    private static final String VALID_TRAINING_TEXT = "my training blablabla";
    private static final Integer VALID_SIZE = 2500;
    private static final String VALID_DATE = "2019-01-15";

    @Test
    public void shouldNotAcceptBlankTrainings() {
        assertTrue("Should have detected errors", es.checkParameter(null, VALID_DATE, VALID_SIZE, true).size() > 0);
        assertTrue("Should have detected errors", es.checkParameter("    ", VALID_DATE, VALID_SIZE, true).size() > 0);
        assertTrue("Should have detected errors", es.checkParameter("", "aaaa", VALID_SIZE, true).size() > 0);
        assertTrue("Should have detected errors", es.checkParameter("", "2019-01-15", VALID_SIZE, true).size() > 0);
        assertTrue("Should have detected errors", es.checkParameter("", "", VALID_SIZE, false).size() > 0);
        assertTrue("Should have detected errors", es.checkParameter("", "aaaa", VALID_SIZE, false).size() > 0);
        assertTrue("Should have detected errors", es.checkParameter("", "2019-01-15", VALID_SIZE, false).size() > 0);
    }

    @Test
    public void shouldNotAcceptInvalidDate() {
        assertTrue("Should have detected errors",
                   es.checkParameter(VALID_TRAINING_TEXT, "", VALID_SIZE, true).size() > 0);
        assertTrue("Should have detected errors",
                   es.checkParameter(VALID_TRAINING_TEXT, "aaaa", VALID_SIZE, true).size() > 0);
        assertTrue("Should have detected errors",
                   es.checkParameter(VALID_TRAINING_TEXT, "le 25 décembre", VALID_SIZE, true).size() > 0);
        assertTrue("Should have detected errors",
                   es.checkParameter(VALID_TRAINING_TEXT, "25/12/2015", VALID_SIZE, true).size() > 0);
        assertTrue("Should have detected errors",
                   es.checkParameter(VALID_TRAINING_TEXT, "25-12-2015", VALID_SIZE, true).size() > 0);
        assertTrue("Should have detected errors",
                   es.checkParameter(VALID_TRAINING_TEXT, "2019--01-15", VALID_SIZE, true).size() > 0);
        assertTrue("Should have detected errors",
                   es.checkParameter(VALID_TRAINING_TEXT, "", VALID_SIZE, false).size() > 0);
        assertTrue("Should have detected errors",
                   es.checkParameter(VALID_TRAINING_TEXT, "aaaa", VALID_SIZE, false).size() > 0);
        assertTrue("Should have detected errors",
                   es.checkParameter(VALID_TRAINING_TEXT, "2019-20-15", VALID_SIZE, false).size() > 0);
    }

    @Test
    public void shouldNotAcceptInvalidSize() {
        assertTrue("Should have detected errors",
                   es.checkParameter(VALID_TRAINING_TEXT, VALID_DATE, null, true).size() > 0);
        assertTrue("Should have detected errors",
                   es.checkParameter(VALID_TRAINING_TEXT, VALID_DATE, 0, true).size() > 0);
        assertTrue("Should have detected errors",
                   es.checkParameter(VALID_TRAINING_TEXT, VALID_DATE, -20, false).size() > 0);
    }

    @Test
    public void shouldNotAcceptAlreadyExistingOnes() throws Exception {

        // Given the training exists
        Training training = new Training(EXISTING_TRAINING_TEXT,
                                         DateUtils.getAsDate(VALID_DATE).orElseThrow(Exception::new));
        training.setSize(VALID_SIZE);
        HibernateUtil.doSomeWork(s -> {
            Transaction t = s.beginTransaction();
            s.saveOrUpdate(training);
            t.commit();
        });
        assertTrue(EntrainementRepository.getById(EXISTING_TRAINING_ID).isPresent());

        assertTrue("Should have detected errors",
                   es.checkParameter(EXISTING_TRAINING_TEXT, VALID_DATE, VALID_SIZE, true).size() > 0);
        assertEquals("Should have detected errors",
                     0,
                     es.checkParameter(EXISTING_TRAINING_TEXT, VALID_DATE, VALID_SIZE, false).size());
    }
}
