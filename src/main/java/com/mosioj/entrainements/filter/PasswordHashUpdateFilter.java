package com.mosioj.entrainements.filter;

import com.mosioj.entrainements.entities.User;
import com.mosioj.entrainements.repositories.UserRepository;
import com.mosioj.entrainements.utils.UserUtils;
import com.mosioj.entrainements.utils.db.HibernateUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Transaction;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

public class PasswordHashUpdateFilter extends GenericFilterBean {

    /** Class logger. */
    private static final Logger logger = LogManager.getLogger(PasswordHashUpdateFilter.class);

    /** Password hashing. */
    private static MessageDigest md;

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest detailRequest = (HttpServletRequest) request;
        String url = detailRequest.getRequestURL().toString();

        if (url.endsWith("/login") && md != null) {

            final String email = request.getParameter("j_username");
            final String pwd = request.getParameter("j_password");
            Optional<User> user = UserRepository.getUser(email);

            if (user.isPresent() && !StringUtils.isBlank(pwd)) {
                boolean matching = user.map(User::getPassword).map(oldHash(pwd)::equals).orElse(false);
                if (matching) {
                    logger.info("Migrating hash algorithm for {}...", email);
                    User updated = user.map(u -> u.setPassword(UserUtils.hashPwd(pwd))).orElse(null);
                    HibernateUtil.doSomeWork(s -> {
                        Transaction t = s.beginTransaction();
                        s.update(updated);
                        t.commit();
                    });
                }
            }
        }
        chain.doFilter(request, response);
    }

    /**
     * @param rawPassword The password as text.
     * @return The password hashed, using the old way
     */
    private String oldHash(String rawPassword) {
        StringBuilder hashPwd = new StringBuilder();
        md.update(rawPassword.getBytes());
        byte[] digest = md.digest();
        for (byte b : digest) {
            hashPwd.append(String.format("%02x", b & 0xff));
        }
        return hashPwd.toString();
    }

    static {
        try {
            md = MessageDigest.getInstance("SHA");
        } catch (NoSuchAlgorithmException e) {
            logger.error("Fail to initialize the password hasher.", e);
        }
    }
}