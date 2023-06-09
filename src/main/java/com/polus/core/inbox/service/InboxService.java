package com.polus.core.inbox.service;

import com.polus.core.inbox.vo.InboxVO;

public interface InboxService {
		
	/**
	 * 
	 * @param inboxVO
	 * @return
	 */
	String markReadMessage(InboxVO inboxVO);
	
	/**
	 * 
	 * @param inboxVO	
	 * @return
	 */
	String showInbox(InboxVO inboxVO);

	/**
	 * This method will remove Message From Inbox
	 * @param moduleItemKey- moduleItemKey
	 * @param reviewId - id of reviewer
	 * @param moduleCode - moduleCode
	 */
	public void removeMessageFromInbox(Integer moduleItemKey, Integer reviewId, Integer moduleCode);

	/**
	 * This method will remove Message From Inbox
	 * @param moduleItemKey- moduleItemKey
	 * @param subModuleItemKey - subModuleItemKey
	 * @param moduleCode - moduleCode
	 * @param subModuleCode - subModuleCode
	 */
	public void removeTaskMessageFromInbox(Integer moduleItemKey, Integer subModuleItemKey, Integer moduleCode, Integer subModuleCode);

	/**
	 * This method is used to add message 
	 * @param delegationId
	 * @param personId
	 * @param updateUser
	 * @param messageTypeCodes
	 * @param subjectTypeCode
	 * @param userMessage
	 */
	public void addDelegationMessageToInbox(String delegationId, String personId, String updateUser, String messageTypeCode, String subjectTypeCode, String userMessage);

	/**
	 * @param moduleItemKey
	 * @param assigneePersonId
	 * @param messageTypeCode
	 * @param subjectTypeCode
	 * @param subModuleItemKey
	 * @param subModuleCode
	 * @param userMessage
	 * @param moduleCode
	 */
	void addMessageToInbox(String moduleItemKey, String assigneePersonId, String messageTypeCode,
			Integer subModuleItemKey, Integer subModuleCode, String userMessage,
			Integer moduleCode);

}
