package com.mosioj.entrainements.service;

import com.mosioj.entrainements.service.response.ServiceResponse;
import com.mosioj.entrainements.utils.GsonFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
public abstract class AbstractServiceTest<T extends AbstractService> {

    /** Class logger. */
    private static final Logger logger = LogManager.getLogger(AbstractServiceTest.class);

    /**
     * Tested service.
     */
    protected final T testedService;

    public AbstractServiceTest(T testedService) {
        this.testedService = testedService;
    }

    public <S> S doGet(HttpServletRequest request, Class<S> clazz) {
        try {
            initResponse();
            testedService.doGet(request, response);
            final String json = writer.message;
            logger.debug(json);
            return GsonFactory.getIt().fromJson(json, clazz);
        } catch (IOException e) {
            fail(e);
            return null;
        }
    }

    public <S> S doPost(HttpServletRequest request, Class<S> clazz) {
        try {
            initResponse();
            testedService.doPost(request, response);
            final String json = writer.message;
            logger.debug(json);
            return GsonFactory.getIt().fromJson(json, clazz);
        } catch (IOException e) {
            fail(e);
            return null;
        }
    }

    public StringServiceResponse doPost(HttpServletRequest request) {
        return doPost(request, StringServiceResponse.class);
    }

    public StringServiceResponse doDelete(HttpServletRequest request) {
        try {
            initResponse();
            testedService.doDelete(request, response);
            final String json = writer.message;
            logger.debug(json);
            return GsonFactory.getIt().fromJson(json, StringServiceResponse.class);
        } catch (IOException e) {
            fail(e);
            return null;
        }
    }

    //////////////////////////// Utilities for reading the response

    @Mock
    protected HttpServletResponse response;

    private final MyServiceWriter writer = new MyServiceWriter();

    public void initResponse() throws IOException {
        lenient().when(response.getCharacterEncoding()).thenReturn(StandardCharsets.UTF_8.toString());
        lenient().when(response.getWriter()).thenReturn(writer);
    }

    private static class MyServiceWriter extends PrintWriter {

        public MyServiceWriter() {
            super(new OutputStream() {
                @Override
                public void write(int b) {
                }
            });
        }

        String message;

        @Override
        public void print(String s) {
            message = s;
        }
    }

    //////////////////////////// Utilities for providing paramaters (PUT and DELETE)

    /**
     * @param parameters The string parameter.
     * @return The equivalent of this String as an {@link javax.servlet.ServletOutputStream}.
     */
    protected static ServletInputStream stringParametersToIS(String parameters) {
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

    protected static final class StringServiceResponse extends ServiceResponse<String> {

        /**
         * Class constructor.
         *
         * @param isOK    Whether this call was successful.
         * @param message The message we want to send back.
         * @param request The http request being answered.
         */
        public StringServiceResponse(boolean isOK, String message, HttpServletRequest request) {
            super(isOK, message, request);
        }
    }
}
