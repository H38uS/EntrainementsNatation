package com.mosioj.entrainements.repositories;

import java.util.List;

import org.hibernate.query.Query;

import com.mosioj.entrainements.entities.UserRole;
import com.mosioj.entrainements.utils.HibernateUtil;

public class UserRoleRepository {


	/**
	 * 
	 * @param roleName Access right role.
	 * @return All user role objects that have this particular role.
	 */
	public static List<UserRole> getUserRole(String roleName) {

		StringBuilder sb = new StringBuilder();
		sb.append(" FROM USER_ROLES ");
		sb.append("WHERE role = :role ");

		Query<UserRole> query = HibernateUtil.getASession().createQuery(sb.toString(), UserRole.class);
		query.setParameter("role", roleName);

		return query.list();
	}
}
