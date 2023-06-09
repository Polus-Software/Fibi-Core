package com.polus.core.inbox.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polus.core.common.dao.CommonDao;
import com.polus.core.constants.Constants;
import com.polus.core.inbox.dao.InboxDao;
import com.polus.core.inbox.pojo.Inbox;
import com.polus.core.inbox.vo.InboxVO;
import com.polus.core.security.AuthenticatedUser;

@Transactional
@Service(value = "inboxService")
public class InboxServiceImpl implements InboxService {

	@Autowired
	private InboxDao inboxDao;

	@Autowired
	private CommonDao commonDao;

	@Override
	public String markReadMessage(InboxVO inboxVO) {
		inboxDao.markReadMessage(inboxVO);
		return commonDao.convertObjectToJSON("Success");
	}

	@Override
	public String showInbox(InboxVO inboxVO) {
		inboxVO.setInboxDetails(inboxDao.showInbox(inboxVO));
		inboxVO.setModules(commonDao.getModules());
		return commonDao.convertObjectToJSON(inboxVO);
	}

	public void removeMessageFromInbox(Integer moduleItemKey, Integer reviewId, Integer moduleCode) {
		inboxDao.removeFromInbox(moduleItemKey, reviewId, moduleCode);
	}

	public void removeTaskMessageFromInbox(Integer moduleItemKey, Integer subModuleItemKey, Integer moduleCode, Integer subModuleCode) {
		inboxDao.removeTaskMessageFromInbox(moduleItemKey, subModuleItemKey, moduleCode, subModuleCode);
	}

	@Override
	public void addDelegationMessageToInbox(String delegationId, String personId, String updateUser, String messageTypeCode, String subjectTypeCode, String userMessage) {
		Inbox inbox = new Inbox();
		inbox.setArrivalDate(commonDao.getCurrentTimestamp());
		inbox.setMessageTypeCode(messageTypeCode);
		inbox.setModuleItemKey(personId);
		inbox.setModuleCode(Constants.PERSON_MODULE_CODE);
		inbox.setSubModuleCode(Constants.PERSON_SUBMODULE_CODE);
		inbox.setSubModuleItemKey(delegationId);
		inbox.setOpenedFlag(Constants.NO);
		inbox.setSubjectType(subjectTypeCode);
		inbox.setToPersonId(personId);
		inbox.setUpdateTimeStamp(commonDao.getCurrentTimestamp());
		inbox.setUpdateUser(updateUser);
		inbox.setUserMessage(userMessage);
		inboxDao.saveToInbox(inbox);
	}

	@Override
	public void addMessageToInbox(String moduleItemKey, String assigneePersonId,
			String messageTypeCode, Integer subModuleItemKey, Integer subModuleCode,
			String userMessage, Integer moduleCode) {
		Inbox inbox = new Inbox();
		inbox.setArrivalDate(commonDao.getCurrentTimestamp());
		inbox.setMessageTypeCode(messageTypeCode);
		inbox.setModuleCode(moduleCode);
		inbox.setSubModuleCode(subModuleCode);
		inbox.setModuleItemKey(moduleItemKey);
		inbox.setSubModuleItemKey(subModuleItemKey.toString());
		inbox.setOpenedFlag(Constants.NO);
		inbox.setSubjectType("P");
		inbox.setToPersonId(assigneePersonId);
		inbox.setUpdateTimeStamp(commonDao.getCurrentTimestamp());
		inbox.setUpdateUser(AuthenticatedUser.getLoginUserName());
		inbox.setUserMessage(userMessage);
		inboxDao.saveToInbox(inbox);
	}
}
