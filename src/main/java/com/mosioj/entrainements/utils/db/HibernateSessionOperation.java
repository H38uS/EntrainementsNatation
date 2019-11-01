package com.mosioj.entrainements.utils.db;

import org.hibernate.Session;

@FunctionalInterface
public interface HibernateSessionOperation {

	/**
	 * Do some stuff using a session.
	 * The session is closed after the operation (could end in error).
	 * 
	 * @param session The session opened for this work.
	 */
	public void work(Session session);
}
