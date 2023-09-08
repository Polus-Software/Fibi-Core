package com.polus.core.roles.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.internal.SessionImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polus.core.applicationexception.dto.ApplicationException;
import com.polus.core.constants.Constants;
import com.polus.core.pojo.Unit;

@Transactional
@Service(value = "personRoleRightDao")
public class PersonRoleRightDaoImpl implements PersonRoleRightDao {

	protected static Logger logger = LogManager.getLogger(PersonRoleRightDaoImpl.class.getName());

	@Autowired
	private HibernateTemplate hibernateTemplate;

	@Override
	public List<String> fetchPersonIdsBasedOnRightAndUnitNumber(String unitNumber, String rightName) {
		try {
			Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
			StoredProcedureQuery query = session.createStoredProcedureQuery("GET_PERSON_BASED_RIGHT_AND_UNIT")
					.registerStoredProcedureParameter("AV_RIGHT_NAME", String.class, ParameterMode.IN)
					.setParameter("AV_RIGHT_NAME", rightName)
					.registerStoredProcedureParameter("AV_UNIT_NUMBER", String.class, ParameterMode.IN)
					.setParameter("AV_UNIT_NUMBER", unitNumber);
			query.execute();
			List<?> result = query.getResultList();
			List<String> personIds = new ArrayList<>();
			for (Object obj : result) {
				if (obj instanceof String) {
					personIds.add((String) obj);
				}
			}
			return personIds;
		} catch (Exception e) {
			throw new ApplicationException("fetchPersonIdsBasedOnRightAndUnitNumber", e, Constants.JAVA_ERROR);
		}
	}

	@Override
	public boolean isPersonHasPermission(String personId,String permissionName,String unitNumber) {
	    return isPersonHasPermission(personId, Arrays.asList(permissionName.split(",")), unitNumber);
	}

	private boolean isPersonHasPermission(String personId, List<String> rightName, String unitNumber) {
		try {
			Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
			StoredProcedureQuery query = session.createStoredProcedureQuery("GET_PERSON_HAS_PERMISSION")
					.registerStoredProcedureParameter("AV_RIGHT_NAME", String.class, ParameterMode.IN)
					.setParameter("AV_RIGHT_NAME", String.join(",", rightName))
					.registerStoredProcedureParameter("AV_UNIT_NUMBER", String.class, ParameterMode.IN)
					.setParameter("AV_UNIT_NUMBER", unitNumber)
					.registerStoredProcedureParameter("AV_PERSON_ID", String.class, ParameterMode.IN)
					.setParameter("AV_PERSON_ID", personId);
			return query.getSingleResult().equals("TRUE") ? Boolean.TRUE : Boolean.FALSE;
		} catch (Exception e) {
			throw new ApplicationException("isPersonHasPermission", e, Constants.JAVA_ERROR);
		}
	}

	@Override
	public List<Unit> fetchUnitsBasedOnPersonAndRight(List<String> rightName, String personId, String searchValue) {
		try {
			Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
			SessionImpl sessionImpl = (SessionImpl) session;
			Connection connection = sessionImpl.connection();
			CallableStatement statement = null;
			ResultSet resultSet = null;
			List<Unit> units = new ArrayList<>();
			statement = connection.prepareCall("{call GET_UNIT_BASED_PERSON_AND_RIGHT(?, ?, ?)}");
			statement.setString(1, String.join(",", rightName));
			statement.setString(2, personId);
			statement.setString(3, searchValue != null ? searchValue : "");
			statement.execute();
			resultSet = statement.getResultSet();
			while (resultSet.next()) {
				Unit unit = new Unit();
				if (resultSet.getString("UNIT_NUMBER") != null) {
					unit.setUnitNumber(resultSet.getString("UNIT_NUMBER"));
				}
				if (resultSet.getString("UNIT_NAME") != null) {
					unit.setUnitName(resultSet.getString("UNIT_NAME"));
				}
				units.add(unit);
			}
			return units;
		} catch (Exception e) {
			throw new ApplicationException("fetchPersonIdsBasedOnRightAndUnitNumber", e, Constants.JAVA_ERROR);
		}
	}
}
