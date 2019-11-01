package com.mosioj.entrainements.utils.db;

import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class HibernateUtil {

	private static StandardServiceRegistry registry;
	private static SessionFactory sessionFactory;

	private static final Logger logger = LogManager.getLogger(HibernateUtil.class);

	private HibernateUtil() {
		// Nothing to do
	}
	
	// ================ Main class interface
	// ================ Takes care of closing the session after the work

	/**
	 * Performs some work on a new brand session.
	 * 
	 * @param operation Operations to perform.
	 */
	public static void doSomeWork(HibernateSessionOperation operation) {
		try (Session session = HibernateUtil.getASession()) {
			operation.work(session);
		}
	}

	/**
	 * 
	 * @param <T> The type of result.
	 * @param operation The operation to perform.
	 * @return The optional row found.
	 */
	public static <T> List<T> doQueryFetch(HibernateSessionQueryFetch<T> operation) {
		try (Session session = HibernateUtil.getASession()) {
			return operation.fetch(session);
		}
	}

	/**
	 * 
	 * @param <T> The type of result.
	 * @param operation The operation to perform.
	 * @return The list of rows found.
	 */
	public static <T> Optional<T> doQueryOptional(HibernateSessionQueryOptional<T> operation) {
		try (Session session = HibernateUtil.getASession()) {
			return operation.fetch(session);
		}
	}

	/**
	 * 
	 * @param <T> The type of result.
	 * @param operation The operation to perform.
	 * @return The list of rows found.
	 */
	public static <T> T doQuerySingle(HibernateSessionQuerySingleResult<T> operation) {
		try (Session session = HibernateUtil.getASession()) {
			return operation.fetch(session);
		}
	}
	
	// ================ Shortcut to save / delete an object
	
	/**
	 * Saves the given object in a transaction.
	 * 
	 * @param object
	 */
	public static void saveit(Object object) {
		doSomeWork(s -> {
			Transaction t = s.beginTransaction();
			s.save(object);
			t.commit();
		});
	}

	/**
	 * Delete the given object in a transaction.
	 * 
	 * @param object
	 */
	public static void deleteIt(Object object) {
		doSomeWork(s -> {
			Transaction t = s.beginTransaction();
			s.delete(object);
			t.commit();
		});
	}
	
	// ================ End of interface
	// ================ Class own utilities

	/**
	 * 
	 * @return A new session
	 */
	private static Session getASession() {
		return getSessionFactory().withOptions().jdbcTimeZone(TimeZone.getTimeZone("Europe/Paris")).openSession();
	}

	/**
	 * Initialized once.
	 * 
	 * @return The session factory.
	 */
	private static SessionFactory getSessionFactory() {
		if (sessionFactory == null) {
			try {
				registry = new StandardServiceRegistryBuilder().configure().build();
				MetadataSources sources = new MetadataSources(registry);
				Metadata metadata = sources.getMetadataBuilder().build();
				sessionFactory = metadata.buildSessionFactory();
			} catch (Exception e) {
				logger.error(e);
				e.printStackTrace();
				if (registry != null) {
					StandardServiceRegistryBuilder.destroy(registry);
				}
			}
		}
		return sessionFactory;
	}
}
