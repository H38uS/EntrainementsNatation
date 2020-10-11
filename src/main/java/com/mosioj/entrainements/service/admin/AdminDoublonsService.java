package com.mosioj.entrainements.service.admin;

import com.mosioj.entrainements.repositories.EntrainementRepository;
import com.mosioj.entrainements.service.AbstractService;
import com.mosioj.entrainements.service.response.ServiceResponse;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Set;

@WebServlet("/admin/service/doublons")
public class AdminDoublonsService extends AbstractService {

    private static final long serialVersionUID = -5812331498447194238L;

    @Override
    protected void serviceGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // Getting the doublons
        Set<LocalDate> dates = EntrainementRepository.getDoublons();

        // Sending the response
        ServiceResponse.ok(dates, request).sentItAsJson(response);
    }

}
