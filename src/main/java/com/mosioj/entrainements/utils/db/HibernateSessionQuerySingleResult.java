package com.mosioj.entrainements.utils.db;

import org.hibernate.Session;

@FunctionalInterface
public interface HibernateSessionQuerySingleResult<T> {

	/**
	 * 
	 * @param session
	 * @return The single fetch done by this operation.
	 */
	public T fetch(Session session);
}
