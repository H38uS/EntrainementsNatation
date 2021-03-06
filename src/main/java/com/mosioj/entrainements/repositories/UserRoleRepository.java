package com.mosioj.entrainements.repositories;

import java.util.List;

import org.hibernate.query.Query;

import com.mosioj.entrainements.entities.UserRole;
import com.mosioj.entrainements.utils.db.HibernateUtil;

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

		return HibernateUtil.doQueryFetch(s -> {
			Query<UserRole> query = s.createQuery(sb.toString(), UserRole.class);
			query.setParameter("role", roleName);
			return query.list();
		});
	}
}
