package com.mosioj.entrainements.service.secured;

import com.mosioj.entrainements.entities.Coach;
import com.mosioj.entrainements.entities.SearchCriteria;
import com.mosioj.entrainements.entities.User;
import com.mosioj.entrainements.filter.LoginFilter;
import com.mosioj.entrainements.repositories.CoachRepository;
import com.mosioj.entrainements.repositories.SearchCriteriaRepository;
import com.mosioj.entrainements.repositories.UserRepository;
import com.mosioj.entrainements.service.AbstractServiceTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

public class SearchCriteriaServiceTest extends AbstractServiceTest<SearchCriteriaService> {

    public SearchCriteriaServiceTest() {
        super(new SearchCriteriaService());
    }

    @Test
    public void weCanCreateAndDeleteASearchCriteria(@Mock HttpServletRequest request) throws SQLException {

        // Required objects
        Coach coach = CoachRepository.getCoach().stream().findAny().orElseThrow(SQLException::new);
        User user = UserRepository.getUser(1).orElseThrow(SQLException::new);
        SearchCriteriaRepository.deleteTheOneOf(user);
        assertEquals(Optional.empty(), SearchCriteriaRepository.of(user));

        // Crafting the request
        when(request.getParameter("minsize")).thenReturn("3500");
        when(request.getParameter("maxsize")).thenReturn("4400");
        when(request.getParameter("from")).thenReturn("1");
        when(request.getParameter("to")).thenReturn("2");
        when(request.getParameter("coach")).thenReturn(coach.getName());
        when(request.getParameter("day")).thenReturn("3");
        when(request.getAttribute(LoginFilter.PARAM_CONNECTED_USER)).thenReturn(user);

        // Process & checks
        StringServiceResponse resp = doPost(request);
        assertTrue(resp.isOK());
        assertTrue(SearchCriteriaRepository.of(user).isPresent());
        assertEquals(Optional.of(3500), SearchCriteriaRepository.of(user).map(SearchCriteria::getMinimalSize));
        assertEquals(Optional.of(4400), SearchCriteriaRepository.of(user).map(SearchCriteria::getMaximalSize));
        assertEquals(Optional.of(1), SearchCriteriaRepository.of(user).map(SearchCriteria::getFromMonthInclusive));
        assertEquals(Optional.of(2), SearchCriteriaRepository.of(user).map(SearchCriteria::getToMonthInclusive));
        assertEquals(Optional.of(coach), SearchCriteriaRepository.of(user).map(SearchCriteria::getCoach));
        // +1 à cause de MySQL
        assertEquals(Optional.of(3+1), SearchCriteriaRepository.of(user).map(SearchCriteria::getDayOfWeek));

        // Deleting it
        resp = doDelete(request);
        assertTrue(resp.isOK(), resp.toString());
        assertEquals(Optional.empty(), SearchCriteriaRepository.of(user));
    }

    @Test
    public void itIsPossibleToOverrideOneCriteria(@Mock HttpServletRequest request) throws SQLException {


        // Required objects
        Coach coach = CoachRepository.getCoach().stream().findAny().orElseThrow(SQLException::new);
        User user = UserRepository.getUser(1).orElseThrow(SQLException::new);
        SearchCriteriaRepository.deleteTheOneOf(user);
        assertEquals(Optional.empty(), SearchCriteriaRepository.of(user));

        // Crafting the request
        lenient().when(request.getParameter("minsize")).thenReturn("");
        when(request.getParameter("maxsize")).thenReturn("4400");
        lenient().when(request.getParameter("from")).thenReturn("");
        when(request.getParameter("to")).thenReturn("4");
        lenient().when(request.getParameter("coach")).thenReturn("");
        when(request.getParameter("day")).thenReturn("3");
        when(request.getAttribute(LoginFilter.PARAM_CONNECTED_USER)).thenReturn(user);

        // Process & checks
        StringServiceResponse resp = doPost(request);
        assertTrue(resp.isOK());
        assertTrue(SearchCriteriaRepository.of(user).isPresent());
        assertEquals(Optional.of(0), SearchCriteriaRepository.of(user).map(SearchCriteria::getMinimalSize));
        assertEquals(Optional.of(4400), SearchCriteriaRepository.of(user).map(SearchCriteria::getMaximalSize));
        assertEquals(Optional.of(13), SearchCriteriaRepository.of(user).map(SearchCriteria::getFromMonthInclusive));
        assertEquals(Optional.of(4), SearchCriteriaRepository.of(user).map(SearchCriteria::getToMonthInclusive));
        assertEquals(Optional.empty(), SearchCriteriaRepository.of(user).map(SearchCriteria::getCoach));
        // +1 à cause de MySQL
        assertEquals(Optional.of(3+1), SearchCriteriaRepository.of(user).map(SearchCriteria::getDayOfWeek));

        // Updating it to set the missing parameters
        when(request.getParameter("minsize")).thenReturn("3500");
        when(request.getParameter("from")).thenReturn("1");
        when(request.getParameter("coach")).thenReturn(coach.getName());

        resp = doPost(request);
        assertTrue(resp.isOK());
        assertTrue(SearchCriteriaRepository.of(user).isPresent());
        assertEquals(Optional.of(3500), SearchCriteriaRepository.of(user).map(SearchCriteria::getMinimalSize));
        assertEquals(Optional.of(4400), SearchCriteriaRepository.of(user).map(SearchCriteria::getMaximalSize));
        assertEquals(Optional.of(1), SearchCriteriaRepository.of(user).map(SearchCriteria::getFromMonthInclusive));
        assertEquals(Optional.of(4), SearchCriteriaRepository.of(user).map(SearchCriteria::getToMonthInclusive));
        assertEquals(Optional.of(coach), SearchCriteriaRepository.of(user).map(SearchCriteria::getCoach));
        // +1 à cause de MySQL
        assertEquals(Optional.of(3+1), SearchCriteriaRepository.of(user).map(SearchCriteria::getDayOfWeek));
    }

}