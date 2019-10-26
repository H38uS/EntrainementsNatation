package com.mosioj.entrainements.utils;

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

	/**
	 * 
	 * @return A new session
	 */
	public static Session getASession() {
		return getSessionFactory().withOptions().jdbcTimeZone(TimeZone.getTimeZone("Europe/Paris")).openSession();
	}

	/**
	 * Saves the given object in a transaction.
	 * 
	 * @param object
	 */
	public static void saveit(Object object) {
		try (Session session = HibernateUtil.getASession()) {
			Transaction t = session.beginTransaction();
			session.save(object);
			t.commit();
		}
	}

	/**
	 * Delete the given object in a transaction.
	 * 
	 * @param object
	 */
	public static void deleteIt(Object object) {
		try (Session session = HibernateUtil.getASession()) {
			Transaction t = session.beginTransaction();
			session.delete(object);
			t.commit();
		}
	}

	public static SessionFactory getSessionFactory() {
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
