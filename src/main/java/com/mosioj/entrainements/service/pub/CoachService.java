package com.mosioj.entrainements.service.pub;

import com.mosioj.entrainements.entities.Coach;
import com.mosioj.entrainements.repositories.CoachRepository;
import com.mosioj.entrainements.service.response.ServiceResponse;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/public/service/coach")
public class CoachService extends HttpServlet {

    private static final long serialVersionUID = 4621325733817065848L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // Getting the coachs
        List<Coach> coach = CoachRepository.getCoach();

        // Sending the response
        ServiceResponse.ok(coach, request).sentItAsJson(response);
    }

}
