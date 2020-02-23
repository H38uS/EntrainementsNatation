package com.mosioj.entrainements.service.admin;

import com.mosioj.entrainements.entities.Training;
import com.mosioj.entrainements.repositories.EntrainementRepository;
import com.mosioj.entrainements.utils.db.HibernateUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdminEntrainementServiceTest {

    private final AdminEntrainementService aes = new AdminEntrainementService();

    @Test
    public void nonAdminCannotDeleteTrainings(@Mock HttpServletRequest request,
                                              @Mock HttpServletResponse response,
                                              @Mock ServletOutputStream sos) throws Exception {

        // Given the training exists
        Training training = new Training("mon entrainement créé", LocalDate.now());
        HibernateUtil.saveit(training);
        assertTrue(EntrainementRepository.getById(training.getId()).isPresent());
        when(request.getInputStream()).thenReturn(stringParametersToIS("id=" + training.getId()));
        when(response.getCharacterEncoding()).thenReturn(StandardCharsets.UTF_8.toString());
        when(response.getOutputStream()).thenReturn(sos);

        // And we try to delete it as non Admin
        aes.doDelete(request, response);

        // Then it still exists...
        assertFalse(EntrainementRepository.getById(training.getId()).isPresent());
    }

    /**
     * @param parameters The string parameter.
     * @return The equivalent of this String as an {@link javax.servlet.ServletOutputStream}.
     */
    private static ServletInputStream stringParametersToIS(String parameters) {
        return new DelegatingServletInputStream(new ByteArrayInputStream(parameters.getBytes()));
    }

    public static class DelegatingServletInputStream extends ServletInputStream {

        private final InputStream sourceStream;

        /**
         * Create a DelegatingServletInputStream for the given source stream.
         *
         * @param sourceStream the source stream (never <code>null</code>)
         */
        public DelegatingServletInputStream(InputStream sourceStream) {
            assertNotNull(sourceStream, "Source InputStream must not be null");
            this.sourceStream = sourceStream;
        }

        public int read() throws IOException {
            return this.sourceStream.read();
        }

        public void close() throws IOException {
            super.close();
            this.sourceStream.close();
        }

        @Override
        public boolean isFinished() {
            try {
                return sourceStream.available() == 0;
            } catch (IOException e) {
                e.printStackTrace();
                return true;
            }
        }

        @Override
        public boolean isReady() {
            try {
                return sourceStream.available() > 0;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            // Ignored
        }
    }
}