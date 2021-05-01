package com.mosioj.entrainements.service.pub;

import com.mosioj.entrainements.entities.User;
import com.mosioj.entrainements.repositories.UserRepository;
import com.mosioj.entrainements.service.AbstractServiceTest;
import com.mosioj.entrainements.service.response.ServiceResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class CreationCompteServiceTest extends AbstractServiceTest<CreationCompteService> {

    public CreationCompteServiceTest() {
        super(new CreationCompteService());
    }

    @Test
    public void cannotCreateAnExistingAccount(@Mock HttpServletRequest request) {

        // Given the account exists
        User user = UserRepository.getUsers().get(0);
        when(request.getParameter("j_username")).thenReturn(user.getEmail());
        when(request.getParameter("j_password")).thenReturn("tototutu");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8080"));

        // Try the creation
        ServiceResponse<?> resp = doPost(request, ServiceResponse.class);

        // Validate the answer
        assertFalse(resp.isOK());
        assertEquals(Collections.singletonList("Cet email est déjà utilisé."), resp.getMessage());
    }

    @Test
    public void cannotCreateWithAShortPwd(@Mock HttpServletRequest request) {

        // Given the account does not exist
        final String email = "quhduzhdfqudgh@djiqzjdz.com";
        assertFalse(UserRepository.getUser(email).isPresent());
        when(request.getParameter("j_username")).thenReturn(email);
        when(request.getParameter("j_password")).thenReturn("toto");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8080"));

        // Try the creation
        ServiceResponse<?> resp = doPost(request, ServiceResponse.class);

        // Validate the answer
        assertFalse(resp.isOK());
        assertEquals(Collections.singletonList(
                "Le mot de passe doit faire au moins 8 caractère. Un peu de sérieux tout de même !"),
                     resp.getMessage());
    }

    @Test
    public void userCanCreateANewAccount(@Mock HttpServletRequest request, @Mock HttpSession session) {

        // Given the account does not exist
        final String email = "dyqzgdqzygd@djiqzjdz.com";
        UserRepository.delete(email);
        assertFalse(UserRepository.getUser(email).isPresent());
        when(request.getParameter("j_username")).thenReturn(email);
        when(request.getParameter("j_password")).thenReturn("tototutu");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8080"));
        when(request.getSession()).thenReturn(session);

        // Try the creation
        ServiceResponse<?> resp = doPost(request, ServiceResponse.class);

        // Validate the answer
        assertTrue(resp.isOK());
        assertTrue(UserRepository.getUser(email).isPresent());
    }
}