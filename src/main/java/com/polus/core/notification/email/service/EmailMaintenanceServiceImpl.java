package com.polus.core.notification.email.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polus.core.common.dao.CommonDao;
import com.polus.core.constants.Constants;
import com.polus.core.notification.email.dao.EmailMaintenanceDao;
import com.polus.core.notification.email.vo.EmailMaintenanceVO;
import com.polus.core.notification.pojo.NotificationRecipient;
import com.polus.core.notification.pojo.NotificationType;
import com.polus.core.person.pojo.PersonPreference;
import com.polus.core.pojo.ArgValueLookup;
import com.polus.core.security.AuthenticatedUser;
import com.polus.core.vo.CommonVO;

@Transactional
@Service(value = "emailMaintenanceService")
public class EmailMaintenanceServiceImpl implements EmailMaintenanceService {

	protected static Logger logger = LogManager.getLogger(EmailMaintenanceServiceImpl.class.getName());

	@Autowired
	@Qualifier(value = "emailMaintenanceDao")
	private EmailMaintenanceDao emailMaintenanceDao;
	
	@Autowired
	private CommonDao commonDao;

	@Value("${spring.mail.username}")
	private String username;

	@Override
	public String saveOrUpdateNotification(EmailMaintenanceVO vo) {
		NotificationType notificationType = vo.getNotificationType();
		notificationType = emailMaintenanceDao.saveOrUpdateNotificationType(notificationType);
		vo.setNotificationType(notificationType);
		if (notificationType.getIsSystemSpecific().equals("Y")) {
			emailMaintenanceDao.deleteUserNotificationById(notificationType.getNotificationTypeId(), vo.getPersonId());
		}
		return commonDao.convertObjectToJSON(vo);
	}

	@Override
	public String getAllNotification(EmailMaintenanceVO vo) {
		vo = emailMaintenanceDao.fetchAllNotifications(vo);
		return commonDao.convertObjectToJSON(vo);
	}

	@Override
	public String removeNotificationRecipient(EmailMaintenanceVO vo) {
		NotificationRecipient notificationRecipient = emailMaintenanceDao
				.fetchNotificationRecipient(vo.getNotificationRecipientId());
		notificationRecipient = emailMaintenanceDao.removeNotificationRecipient(notificationRecipient);
		return commonDao.convertObjectToJSON(notificationRecipient);
	}

	@Override
	public String getNotificationById(EmailMaintenanceVO vo) {
		NotificationType notificationType = emailMaintenanceDao.fetchNotificationById(vo.getNotificationTypeId());
		vo.setNotificationType(notificationType);
		return commonDao.convertObjectToJSON(vo);
	}

	@Override
	public String removeNotificationById(EmailMaintenanceVO vo) {
		NotificationType notificationType = emailMaintenanceDao.fetchNotificationById(vo.getNotificationTypeId());
		emailMaintenanceDao.removeNotificationType(notificationType);
		emailMaintenanceDao.removePersonPreferenceByNotificationTypeId(vo.getNotificationTypeId());
		vo.setMessage("Successfully Deactivated");
		return commonDao.convertObjectToJSON(vo);
	}

	@Override
	public String fetchUserNotification(String personId) {
		EmailMaintenanceVO vo = new EmailMaintenanceVO();
		vo.setPersonId(personId);
		HashMap<String, List<HashMap<String, Object>>> notification = new HashMap<>();
		List<HashMap<String, Object>> getUserNotification = emailMaintenanceDao.fetchNotificationType(vo);
		for (int i = 0; i < getUserNotification.size(); i++) {
			List<HashMap<String, Object>> notificationList = new ArrayList<>();
			for (int j = 0; j < getUserNotification.size(); j++) {
				if (getUserNotification.get(i).get("MODULE_CODE").equals(getUserNotification.get(j).get("MODULE_CODE"))) {
					notificationList.add(getUserNotification.get(j));
				}
			}
			notification.put(getUserNotification.get(i).get("MODULE_DESCRIPTION").toString(), notificationList);
		}
		return commonDao.convertObjectToJSON(notification);
	}

	@Override
	public String activateOrDeactivateUserNotification(EmailMaintenanceVO vo) {
		if (vo.getPreferenceId() != null) {
			emailMaintenanceDao.activateUserNotification(vo.getPreferenceId());
			vo.setActivated(true);
		} else {
			PersonPreference personPreference = new PersonPreference();
			personPreference.setPersonId(AuthenticatedUser.getLoginPersonId());
			personPreference.setUpdateUser(AuthenticatedUser.getLoginUserName());
			personPreference.setPreferencesTypeCode(Constants.PREFERENCE_TYPE_CODE);
			personPreference.setUpdateTimestamp(commonDao.getCurrentTimestamp());
			personPreference.setValue(vo.getNotificationTypeCode());
			emailMaintenanceDao.deactivateUserNotification(personPreference);
			vo.setPreferenceId(personPreference.getPreferenceId());
			vo.setActivated(false);
		}
		return commonDao.convertObjectToJSON(vo);
	}

	@Override
	public String getNotificationPlaceholder(Integer moduleCode) {
		List<ArgValueLookup> argumentList = commonDao.getArgValueLookupData(getPlaceholderForModule(moduleCode));
		return commonDao.convertObjectToJSON(argumentList);
	}

	private String getPlaceholderForModule(Integer moduleCode) {
		switch (moduleCode) {
		case 1:
			return Constants.PLACEHOLDER_AWARD;
		case 2:
			return Constants.PLACEHOLDER_INSTITUTE_PROPOSAL;
		case 3:
			return Constants.PLACEHOLDER_DEV_PROPOSAL;
		case 5:
			return Constants.PLACEHOLDER_NEGOTIATION;
		case 15:
			return Constants.PLACEHOLDER_GRANT_CALL;
		case 20:
			return Constants.PLACEHOLDER_SERVICE_REQUEST;
		case 14:
			return Constants.PLACEHOLDER_CLAIM;
		case 21:
			return Constants.PLACEHOLDER_EXTERNAL_USER;
		case 16:
			return Constants.PLACEHOLDER_PROGRESS_REPORT;
		case 13:
			return Constants.PLACEHOLDER_AGREEMENT;
		case 22:
			return Constants.PLACEHOLDER_EXTERNAL_REVIEWER;
		}
		return "";
	}

	@Override
	public String personCertificationMailLog(CommonVO commonVO) {
		List<Object[]> notificationLogList = null;
		notificationLogList = emailMaintenanceDao.getNotificationLogForPerson(Constants.DEV_PROPOSAL_MODULE_CODE,
				commonVO.getModuleItemKey(), commonVO.getProperty1(), Constants.NOTIFICATION_PROPOSAL_PERSONS);
		List<Map<String, Object>> finalResult = new ArrayList<>();
		notificationLogList.forEach(log -> {
			Map<String, Object> notification = new HashMap<>();
			notification.put("sendDate", log[0]);
			notification.put("mailSentFlag", log[1]);
			notification.put("errorMsg", log[2]);
			finalResult.add(notification);
		});
		return commonDao.convertObjectToJSON(finalResult);
	}

}
