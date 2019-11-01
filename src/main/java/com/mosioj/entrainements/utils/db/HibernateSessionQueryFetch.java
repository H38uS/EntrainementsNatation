package com.mosioj.entrainements.utils.db;

import java.util.List;

import org.hibernate.Session;

@FunctionalInterface
public interface HibernateSessionQueryFetch<T> {

	/**
	 * Do some stuff using a session before returning the result.
	 * The session is closed after the operation (could end in error).
	 * 
	 * @param session The session opened for this work.
	 * @return The rows for this query.
	 */
	public List<T> fetch(Session session);
}
