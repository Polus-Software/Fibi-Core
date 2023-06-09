package com.polus.core.notification.email.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.internal.SessionImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polus.core.dbengine.DBEngine;
import com.polus.core.dbengine.DBEngineConstants;
import com.polus.core.dbengine.Parameter;
import com.polus.core.notification.email.vo.EmailMaintenanceVO;
import com.polus.core.notification.pojo.NotificationLog;
import com.polus.core.notification.pojo.NotificationLogRecipient;
import com.polus.core.notification.pojo.NotificationRecipient;
import com.polus.core.notification.pojo.NotificationType;
import com.polus.core.person.pojo.PersonPreference;

import oracle.jdbc.OracleTypes;

@Transactional
@Service(value = "emailMaintenanceDao")
public class EmailMaintenanceDaoImpl implements EmailMaintenanceDao{

	protected static Logger logger = LogManager.getLogger(EmailMaintenanceDaoImpl.class.getName());

	@Value("${oracledb}")
	private String oracledb;

	@Autowired
	private HibernateTemplate hibernateTemplate;

	@Autowired
	private DBEngine dbEngine;

	@Override
	public NotificationType saveOrUpdateNotificationType(NotificationType notificationType) {
		try {
			hibernateTemplate.saveOrUpdate(notificationType);
		} catch (Exception e) {
			logger.error("Error occured in saveOrUpdateNotificationType : {}", e.getMessage());
		}
		return notificationType;
	}

	@Override
	public EmailMaintenanceVO fetchAllNotifications(EmailMaintenanceVO vo) {
		Map<String, String> sort = vo.getSort();
		Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<NotificationType> query = builder.createQuery(NotificationType.class);
		Root<NotificationType> notificationType = query.from(NotificationType.class);
		List<Order> orderList = new ArrayList<>();
		for (Map.Entry<String, String> mapElement : sort.entrySet()) {
			if (mapElement.getValue().equals("desc")) {
				orderList.add((Order) builder.desc(generateExpression(notificationType, mapElement.getKey())));
			} else {
				orderList.add((Order) builder.asc(generateExpression(notificationType, mapElement.getKey())));
			}
		}
		query.orderBy(orderList);
		vo.setNotificationTypes(session.createQuery(query).getResultList());
		return vo;
	}

	public <R> Expression<R> generateExpression(Root<R> root, String key) {
		if (!key.contains(".")) {
			return root.get(key);
		}
		Integer index = key.indexOf('.');
		return root.get(key.substring(0, index)).get(key.substring(index + 1, key.length()));
	}

	@Override
	public NotificationType fetchNotificationById(Integer notificationTypeId) {
		return hibernateTemplate.get(NotificationType.class, notificationTypeId);
	}

	@Override
	public NotificationRecipient removeNotificationRecipient(NotificationRecipient notificationRecipient) {
		hibernateTemplate.delete(notificationRecipient);
		return notificationRecipient;
	}

	@Override
	public NotificationRecipient fetchNotificationRecipient(Integer notificationRecipientId) {
		return hibernateTemplate.get(NotificationRecipient.class, notificationRecipientId);
	}

	@Override
	public NotificationType removeNotificationType(NotificationType notificationType) {
		hibernateTemplate.delete(notificationType);
		return notificationType;
	}

	@Override
	public Set<NotificationRecipient> getRoleEmail(Integer roleTypeCode, String recipientType, Integer moduleCode, String moduleItemKey, Integer notificationTypeId, Integer subModuleCode, String subModuleItemKey) {
		Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
		Set<NotificationRecipient> recipients = new HashSet<>();
		SessionImpl sessionImpl = (SessionImpl) session;
		Connection connection = sessionImpl.connection();
		CallableStatement statement = null;
		ResultSet resultSet = null;
		try {
			if (oracledb.equalsIgnoreCase("N")) {
				statement = connection.prepareCall("{call GET_APPROVERS_FOR_ROLE_TYPE(?,?,?,?,?)}");
				statement.setInt(1, moduleCode);
				statement.setString(2, moduleItemKey);
				statement.setInt(3, roleTypeCode);
				statement.setInt(4, subModuleCode);
				statement.setString(5, subModuleItemKey);
				statement.execute();
				resultSet = statement.getResultSet();
			} else if (oracledb.equalsIgnoreCase("Y")) {
				String procedureName = "GET_APPROVERS_FOR_ROLE_TYPE";
				String functionCall = "{call " + procedureName + "(?,?,?,?,?,?)}";
				statement = connection.prepareCall(functionCall);
				statement.setInt(1, moduleCode);
				statement.setString(2, moduleItemKey);
				statement.setInt(3, roleTypeCode);
				statement.setInt(4, subModuleCode);
				statement.setString(5, subModuleItemKey);
				statement.registerOutParameter(6, OracleTypes.CURSOR);
				statement.executeUpdate();
				resultSet = (ResultSet) statement.getObject(6);
			}
			if (resultSet != null) {
				while (resultSet.next()) {
					setNotificationRecipient(resultSet, recipientType, notificationTypeId, recipients);
				}
			}
		} catch (SQLException e) {
			logger.error("Error occured in getRoleEmail : {}", e.getMessage());
		}
		return recipients;
	}

	private Set<NotificationRecipient> setNotificationRecipient(ResultSet resultSet, String recipientType, Integer notificationTypeId, Set<NotificationRecipient> recipients) {
		try {
		NotificationRecipient recipient = new NotificationRecipient();
		ResultSetMetaData metaData = resultSet.getMetaData();
		String columnName  = metaData.getColumnLabel(1);
	    if (columnName.equals("PERSON_ID")) {
			recipient.setRecipientPersonId(resultSet.getString("PERSON_ID"));
			NotificationLogRecipient notificationLogRecipient = getEmailAndFlag(resultSet.getString("PERSON_ID"), notificationTypeId);
			recipient.setEmailAddress(notificationLogRecipient.getToUserEmailId());
			recipient.setSendFlag(notificationLogRecipient.getMailSentFlag());
			recipient.setRecipientType(recipientType);
			recipients.add(recipient);
		} else {
			String[] singleEmailTOAddress = resultSet.getString("RECIPIENT_ADDRESS").split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
			if (singleEmailTOAddress.length > 0) {
				for (String recipeientMailTOAddress : singleEmailTOAddress) {
					NotificationRecipient recipientData = new NotificationRecipient();
					recipientData.setEmailAddress(recipeientMailTOAddress);
					recipientData.setRecipientType(recipientType);
					recipientData.setSendFlag("Y");
					recipients.add(recipientData);
				}
			}
		}
		} catch (SQLException e) {
			logger.error("Error occured in setNotificationRecipient : {}", e.getMessage());
		}
		return recipients;
	}

	@Override
	public NotificationLog createNotificationLog(NotificationLog notificationLog) {
		try {
			hibernateTemplate.saveOrUpdate(notificationLog);
		} catch (Exception e) {
			logger.error("Error occured in createNotificationLog : {}", e.getMessage());
		}
		return notificationLog;
	}

	@Override
	public NotificationLogRecipient getEmailAndFlag(String recipientPersonId, Integer notificationTypeId) {
		NotificationLogRecipient notificationLogRecipient = new NotificationLogRecipient();
		ArrayList<HashMap<String, Object>> dataList = new ArrayList<>();
		try {
			String query = "SELECT T1.EMAIL_ADDRESS,T2.VALUE \r\n" + "FROM PERSON T1 \r\n"
					+ "LEFT OUTER JOIN PERSON_PREFERENCE T2 ON T2.PERSON_ID = T1.PERSON_ID AND T2.PREFERENCES_TYPE_CODE  = 101 AND VALUE = "
					+ notificationTypeId + "\r\n" + "WHERE T1.PERSON_ID = '" + recipientPersonId + "'";
			dataList = dbEngine.executeQuerySQL(new ArrayList<Parameter>(), query);
			if (dataList != null && !dataList.isEmpty()) {
				notificationLogRecipient.setToUserEmailId(dataList.get(0).get("EMAIL_ADDRESS") == null ? null
						: dataList.get(0).get("EMAIL_ADDRESS").toString());
				notificationLogRecipient.setMailSentFlag(dataList.get(0).get("VALUE") == null ? "Y" : "N");
			}
		} catch (Exception e) {
			logger.info("exception in getEmailAndFlag :{}", e.getMessage());
		}
		return notificationLogRecipient;
	}

	@Override
	public void saveOrUpdate(NotificationLogRecipient notificationLogRecipient) {
		hibernateTemplate.saveOrUpdate(notificationLogRecipient);
	}

	@Override
	public ArrayList<HashMap<String, Object>> fetchNotificationType(EmailMaintenanceVO emailMaintenanceVO) {
		ArrayList<HashMap<String, Object>> output = new ArrayList<>();
		try {
			ArrayList<Parameter> inParam = new ArrayList<>();
			inParam.add(new Parameter("<<PERSON_ID>>", DBEngineConstants.TYPE_STRING, emailMaintenanceVO.getPersonId()));
			return dbEngine.executeQuery(inParam, "get_user_notification_type");
		} catch (Exception e) {
			logger.error("Exception in fetchNotificationType :{}", e.getMessage());
		}
		return output;
	}

	@Override
	public void deactivateUserNotification(PersonPreference personPreference) {
		try {
			hibernateTemplate.saveOrUpdate(personPreference);
		} catch (Exception e) {
			logger.error("Exception in deactivateUserNotification :{}", e.getMessage());
		}
	}

	@Override
	public void activateUserNotification(Integer preferenceId) {
		try {
			hibernateTemplate.delete(hibernateTemplate.get(PersonPreference.class, preferenceId));
		} catch (Exception e) {
			logger.error("Exception in activateUserNotification :{}", e.getMessage());
		}
	}

	@Override
	public void deleteUserNotificationById(Integer notificationTypeId, String personId) {
		try {
			Query query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery("delete from PersonPreference d where d.personId = :personId AND d.value = :value");
			query.setParameter("personId", personId);
			query.setParameter("value", notificationTypeId);
			query.executeUpdate();
		} catch (Exception e) {
			logger.error("Exception in deleteUserNotificationById :{}", e.getMessage());
		}
	}

	@Override
	public void removePersonPreferenceByNotificationTypeId(Integer notificationTypeId) {
		try {
			Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
			String hqlQuery = "delete from PersonPreference d where d.value = :value";
			Query query = session.createQuery(hqlQuery);
			query.setParameter("value", notificationTypeId);
			query.executeUpdate();
		} catch (Exception e) {
			logger.error("Exception in removePersonPreferenceByNotificationTypeId :{}", e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> getNotificationLogForPerson(Integer moduleCode, String moduleItemKey, String personId,
			Integer notificationTypeCode) {
		StringBuilder hqlQuery = new StringBuilder()
				.append("select t1.sendDate, t2.mailSentFlag, t1.errorMessage from NotificationLog t1 ");
		hqlQuery.append(" inner join NotificationLogRecipient t2 on t1.notificationLogId = t2.notificationLogId ");
		hqlQuery.append(
				" where t1.moduleCode =:moduleCode and t1.moduleItemKey =:moduleItemKey and t1.notificationTypeId =:notificationTypeCode ");
		hqlQuery.append(" and t2.toUserEmailId in (select emailAddress from Person where personId =:personId) order by t1.notificationLogId asc ");
		Query query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(hqlQuery.toString());
		query.setParameter("moduleCode", moduleCode);
		query.setParameter("moduleItemKey", moduleItemKey);
		query.setParameter("notificationTypeCode", notificationTypeCode);
		query.setParameter("personId", personId);
		return query.getResultList();
	}

}
