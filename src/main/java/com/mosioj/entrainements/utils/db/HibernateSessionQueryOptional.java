package com.mosioj.entrainements.utils.db;

import java.util.Optional;

import org.hibernate.Session;

@FunctionalInterface
public interface HibernateSessionQueryOptional<T> {

	/**
	 * Do some stuff using a session before returning the result.
	 * The session is closed after the operation (could end in error).
	 * 
	 * @param session The session opened for this work.
	 * @return The optional row for this query.
	 */
	public Optional<T> fetch(Session session);
}
