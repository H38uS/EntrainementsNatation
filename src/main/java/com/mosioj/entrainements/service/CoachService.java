package com.mosioj.entrainements.service;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.entrainements.entities.Coach;
import com.mosioj.entrainements.repositories.CoachRepository;
import com.mosioj.entrainements.service.response.ServiceResponse;

@WebServlet("/public/service/coach")
public class CoachService extends HttpServlet {

	private static final long serialVersionUID = 4621325733817065848L;
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// Getting the coachs
		List<Coach> coach = CoachRepository.getCoach();
		
		// Sending the response
		response.getOutputStream().print(new ServiceResponse(true, coach, request).asJSon(response));
	}
	
}
