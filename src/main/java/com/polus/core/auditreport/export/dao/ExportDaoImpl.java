package com.polus.core.auditreport.export.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.internal.SessionImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polus.core.vo.CommonVO;

@SuppressWarnings("deprecation")
@Transactional
@Service(value = "exportDao")
public class ExportDaoImpl implements ExportDao {

	protected static Logger logger = LogManager.getLogger(ExportDaoImpl.class.getName());

	@Autowired
	private HibernateTemplate hibernateTemplate;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<Object[]> getExportDataOfPersonForDownload(CommonVO vo, List<Object[]> personData) {
		try {
			logger.info("----------- getDashBoardDataOfPersonForDownload ------------");
			Query personList = null;
			String likeQuery = "";
			String mainQuery = "SELECT T1.PERSON_ID, T1.FIRST_NAME, T1.LAST_NAME, T1.MIDDLE_NAME, T1.USER_NAME, T1.EMAIL_ADDRESS, T1.PRIMARY_TITLE, T1.DIRECTORY_TITLE, T1.STATE, T1.HOME_UNIT, T3.UNIT_NAME FROM PERSON T1 INNER JOIN UNIT T3 ON T1.HOME_UNIT = T3.UNIT_NUMBER\n"
					+ "";
			Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
			if (vo.getProperty1() != null && !vo.getProperty1().isEmpty()) {
				likeQuery = " and lower(T1.FIRST_NAME) like lower(:firstName) ";
			}
			if (vo.getProperty2() != null && !vo.getProperty2().isEmpty()) {
				likeQuery = likeQuery + " and lower(T1.LAST_NAME) like lower(:lastName) ";
			}
			personList = session.createSQLQuery(mainQuery + likeQuery);
			personData = personList.list();
			logger.info("persons : " + personData);
		} catch (Exception e) {
			logger.error("Error in method getDashBoardDataOfPersonForDownload");
			e.printStackTrace();
		}
		return personData;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<Object[]> getDataOfRolodexForDownload(CommonVO vo, List<Object[]> rolodexData) {
		try {
			logger.info("----------- getDataOfRolodexForDownload ------------");
			Query rolodexList = null;
			String likeQuery = "";
			String mainQuery = "SELECT T1.ROLODEX_ID, T1.LAST_NAME, T1.FIRST_NAME, T1.MIDDLE_NAME, T1.SPONSOR_CODE, T1.CITY, T1.ACTV_IND, T1.ORGANIZATION, T1.STATE, T1.COUNTY FROM ROLODEX T1 ;";
			Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
			if (vo.getProperty1() != null && !vo.getProperty1().isEmpty()) {
				likeQuery = " and lower(T1.ROLODEX_ID) like lower(:rolodexId) ";
			}
			if (vo.getProperty2() != null && !vo.getProperty2().isEmpty()) {
				likeQuery = likeQuery + " and lower(T1.FULL_NAME) like lower(:fullName) ";
			}
			rolodexList = session.createSQLQuery(mainQuery + likeQuery);

			if (vo.getProperty1() != null && !vo.getProperty1().isEmpty()) {
				rolodexList.setString("rolodexId", "%" + vo.getProperty1() + "%");
			}
			if (vo.getProperty2() != null && !vo.getProperty2().isEmpty()) {
				rolodexList.setString("firstName", "%" + vo.getProperty2() + "%");
			}
			rolodexData = rolodexList.list();
			logger.info("persons : " + rolodexData);
		} catch (Exception e) {
			logger.error("Error in method getDataOfRolodexForDownload");
			e.printStackTrace();
		}
		return rolodexData;
	}

	@Override
	public List<Object[]> administrativeDetails(CommonVO vo) {
		Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
		SessionImpl sessionImpl = (SessionImpl) session;
		Connection connection = sessionImpl.connection();
		List<Object[]> reportData = new ArrayList<>();
		try {
			CallableStatement statement = connection.prepareCall("{call GET_ADMINISTRATIVE_DETAILS(?)}");
			statement.setString(1, vo.getType());
			statement.execute();
			ResultSet resultSet = statement.getResultSet();
			while (resultSet.next()) {
				int colCount = resultSet.getMetaData().getColumnCount();
				Object[] objArray = new Object[colCount];
				for (int i = 0; i < colCount; i++) {
					objArray[i] = resultSet.getObject(i + 1);
				}
				reportData.add(objArray);
			}
		} catch (Exception e) {
			logger.info("Exception in administrative details {} ", e.getMessage());
		}
		return reportData;
	}

}
