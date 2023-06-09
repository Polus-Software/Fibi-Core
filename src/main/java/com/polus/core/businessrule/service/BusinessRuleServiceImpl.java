package com.polus.core.businessrule.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polus.core.businessrule.dao.BusinessRuleDao;
import com.polus.core.businessrule.vo.EvaluateValidationRuleVO;
import com.polus.core.common.dao.CommonDao;
import com.polus.core.common.service.CommonService;
import com.polus.core.notification.email.service.EmailService;
import com.polus.core.notification.email.vo.EmailServiceVO;
import com.polus.core.workflow.pojo.Workflow;
import com.polus.core.workflow.pojo.WorkflowAttachment;
import com.polus.core.workflow.service.WorkflowExtService;

@Transactional
@Service(value = "businessRuleService")
public class BusinessRuleServiceImpl implements BusinessRuleService {

	protected static Logger logger = LogManager.getLogger(BusinessRuleServiceImpl.class.getName());

	@Autowired
	private BusinessRuleDao businessRuleDao;

	@Autowired
	private CommonDao commonDao;

	@Autowired
	private CommonService commonService;

	@Autowired
	private EmailService emailService;

	@Autowired
	private WorkflowExtService workflowExtService;

	@Override
	public Integer buildWorkFlow(EvaluateValidationRuleVO evaluateValidationRuleVO) {
		Integer workflowStatus = businessRuleDao.buildWorkFlow(evaluateValidationRuleVO);
		List<HashMap<String, Object>> details = businessRuleDao.getRoleNameAndMapName(evaluateValidationRuleVO.getModuleCode(), evaluateValidationRuleVO.getModuleItemKey(), evaluateValidationRuleVO.getSubModuleCode(), evaluateValidationRuleVO.getSubModuleItemKey());
		sendNotificationForNoRoleBasedPersonAssigned(details, evaluateValidationRuleVO);
		return workflowStatus;
	}

	@Override
	public String evaluateValidationRule(EvaluateValidationRuleVO evaluateValidationRuleVO) {
		evaluateValidationRuleVO.setWorkFlowResultList(businessRuleDao.evaluateValidationRule(evaluateValidationRuleVO));
		return commonDao.convertObjectToJSON(evaluateValidationRuleVO.getWorkFlowResultList());
	}

	@Override
	public String evaluateNotificationRule(EvaluateValidationRuleVO evaluateValidationRuleVO) {
		return commonDao.convertObjectToJSON(businessRuleDao.evaluateNotificationRule(evaluateValidationRuleVO));
	}

	@Override
	public String ruleEvaluate(EvaluateValidationRuleVO evaluateValidationRuleVO) {
		return businessRuleDao.ruleEvaluate(evaluateValidationRuleVO);
	}

	@Override
	public Integer canApproveRouting(String moduleItemKey, String loginPersonId, Integer moduleCode, String subModuleItemKey, Integer subModuleCode) {
		return businessRuleDao.canApproveRouting(moduleItemKey, loginPersonId, moduleCode, subModuleItemKey, subModuleCode);
	}

	@Override
	public String getWorkFlowRouteLog(String moduleItemId, Integer moduleCode) {
		return businessRuleDao.getWorkFlowRouteLog(moduleItemId, moduleCode);
	}

	@Override
	public String workflowfinalApproval(String moduleItemKey, String personId, Integer moduleCode, Integer subModuleCode, String subModuleItemKey) {
		return businessRuleDao.workflowfinalApproval(moduleItemKey, personId, moduleCode, subModuleItemKey, subModuleCode);
	}

	private void sendNotificationForNoRoleBasedPersonAssigned(List<HashMap<String, Object>> details, EvaluateValidationRuleVO evaluateValidationRuleVO) {
		if (details != null && !details.isEmpty()) {
			for (HashMap<String, Object> detail : details) {
				EmailServiceVO emailServiceVO = new EmailServiceVO();
				emailServiceVO.setNotificationTypeId(workflowExtService.getTheNotificationIdForMapPersonMissing(evaluateValidationRuleVO.getModuleCode()));
				emailServiceVO.setModuleCode(evaluateValidationRuleVO.getModuleCode());
				emailServiceVO.setModuleItemKey(evaluateValidationRuleVO.getModuleItemKey());
				emailServiceVO.setPlaceHolder(getDynamicPlaceholders(detail));
				emailServiceVO.setSubModuleCode(evaluateValidationRuleVO.getSubModuleCode().toString());
				emailServiceVO.setSubModuleItemKey(evaluateValidationRuleVO.getSubModuleItemKey());
				emailServiceVO.setRecipients(new HashSet<>());
				emailService.sendEmail(emailServiceVO);
			}
		}
	}

	private Map<String, String> getDynamicPlaceholders(HashMap<String, Object> detail) {
		Map<String, String> placeHolder = new HashMap<>();
		placeHolder.put("{MAP_NAME}", detail.get("MAP_NAME").toString() == null ? "" : detail.get("MAP_NAME").toString());
		placeHolder.put("{ROLE_NAME}", detail.get("ROLE_TYPE").toString() == null ? "" : detail.get("ROLE_TYPE").toString());
		return placeHolder;
	}

	@Override
	public Workflow getWorkFlow(String moduleItemKey, Integer moduleCode) {
		return businessRuleDao.getWorkFlow(moduleItemKey, moduleCode);
	}

	@Override
	public ResponseEntity<byte[]> downloadWorkflowsAttachments(Integer workflowAttachmentId) {
		logger.info("-------- downloadNegotiationAttachment serviceimpl ---------");
		WorkflowAttachment attachment = businessRuleDao.fetchAttachmentById(workflowAttachmentId);
		ResponseEntity<byte[]> attachmentData = null;
		try {
			attachmentData = commonService.setAttachmentContent(attachment.getFileName(), attachment.getAttachment());
		} catch (Exception e) {
			logger.error("exception in downloadWorkflowsAttachments: {} ", e.getMessage());
		}
		return attachmentData;
	}

	@Override
	public void evaluateAndSentNotification(Integer moduleCode, Integer subModuleCode, String moduleItemKey, String subModuleItemKey, String personId, String updateUser, 
			Map<String, String> placeHolder) {
			EvaluateValidationRuleVO evaluateValidationRuleVO = new EvaluateValidationRuleVO();
			evaluateValidationRuleVO.setModuleCode(moduleCode);
			evaluateValidationRuleVO.setSubModuleCode(subModuleCode);
			evaluateValidationRuleVO.setModuleItemKey(moduleItemKey);
			evaluateValidationRuleVO.setSubModuleItemKey(subModuleItemKey);
			evaluateValidationRuleVO.setLogginPersonId(personId);
			evaluateValidationRuleVO.setUpdateUser(updateUser);
			List<String> notifications = businessRuleDao.evaluateNotificationRule(evaluateValidationRuleVO);
			if (notifications != null && !notifications.isEmpty()) {
				for (String notficationId : notifications) {
					EmailServiceVO emailServiceVO = new EmailServiceVO();
					emailServiceVO.setModuleCode(moduleCode);
					emailServiceVO.setModuleItemKey(moduleItemKey);
					emailServiceVO.setSubModuleCode(subModuleCode.toString());
					emailServiceVO.setSubModuleItemKey(subModuleItemKey);
					emailServiceVO.setPlaceHolder(placeHolder);
					emailServiceVO.setNotificationTypeId(Integer.parseInt(notficationId));
					emailService.sendEmail(emailServiceVO);
				}
			}
	}

}
