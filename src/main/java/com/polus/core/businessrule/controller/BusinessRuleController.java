package com.polus.core.businessrule.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.polus.core.businessrule.service.BusinessRuleService;
import com.polus.core.businessrule.vo.EvaluateValidationRuleVO;
import com.polus.core.common.service.CommonService;
import com.polus.core.common.service.ElasticSyncOperation;
import com.polus.core.constants.Constants;
import com.polus.core.workflow.pojo.Workflow;

import io.jsonwebtoken.Claims;

@RestController
public class BusinessRuleController {

	protected static Logger logger = LogManager.getLogger(BusinessRuleController.class.getName());

	@Autowired
	@Qualifier(value = "businessRuleService")
	private BusinessRuleService businessRuleService;

	@Autowired
	private CommonService commonService;

	@Autowired
	ElasticSyncOperation elasticSyncOperation;

	private static final String MODULE_CODE = "moduleCode : {}";
	private static final String UPDATE_USER = "updateUser : {}";
	private static final String MODULE_ITEM_KEY = "moduleItemKey : {}";
	private static final String SUB_MODULE_ITEM_KEY = "subModuleItemKey : {}";
	private static final String LOGIN_PERSON_ID = "loginPersonId : {}";
	private static final String SUB_MODULE_CODE = "subModuleCode : {}";

	@PostMapping(value = "/buildWorkFlow", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public Integer buildWorkFlow(@RequestBody EvaluateValidationRuleVO evaluateValidationRuleVO, HttpServletRequest request, HttpServletResponse response) {
		logger.info("Requesting for buildWorkFlow");
		logger.info(MODULE_CODE, evaluateValidationRuleVO.getModuleCode());
		logger.info(SUB_MODULE_CODE, evaluateValidationRuleVO.getSubModuleCode());
		logger.info(MODULE_ITEM_KEY, evaluateValidationRuleVO.getModuleItemKey());
		logger.info(LOGIN_PERSON_ID, evaluateValidationRuleVO.getLogginPersonId());
		logger.info(UPDATE_USER, evaluateValidationRuleVO.getUpdateUser());
		return businessRuleService.buildWorkFlow(evaluateValidationRuleVO);
	}

	@PostMapping(value = "/evaluateValidationRule", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String evaluateValidationRule(@RequestBody EvaluateValidationRuleVO evaluateValidationRuleVO, HttpServletRequest request, HttpServletResponse response) {
		logger.info("Requesting for evaluateValidationRule");
		Claims claims = commonService.getLoginPersonDetailFromJWT(request);
		evaluateValidationRuleVO.setLogginPersonId(claims.get(Constants.LOGIN_PERSON_ID).toString());
		evaluateValidationRuleVO.setUpdateUser(claims.getSubject());
		logger.info(MODULE_CODE, evaluateValidationRuleVO.getModuleCode());
		logger.info(SUB_MODULE_CODE, evaluateValidationRuleVO.getSubModuleCode());
		logger.info(MODULE_ITEM_KEY, evaluateValidationRuleVO.getModuleItemKey());
		logger.info(LOGIN_PERSON_ID, evaluateValidationRuleVO.getLogginPersonId());
		logger.info(UPDATE_USER, evaluateValidationRuleVO.getUpdateUser());
		logger.info(SUB_MODULE_ITEM_KEY, evaluateValidationRuleVO.getSubModuleItemKey());
		return businessRuleService.evaluateValidationRule(evaluateValidationRuleVO);
	}

	@PostMapping(value = "/evaluateNotificationRule", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String evaluateNotificationRule(@RequestBody EvaluateValidationRuleVO evaluateValidationRuleVO, HttpServletRequest request, HttpServletResponse response) {
		logger.info("Requesting for evaluateNotificationRule");
		logger.info(MODULE_CODE, evaluateValidationRuleVO.getModuleCode());
		logger.info(SUB_MODULE_CODE, evaluateValidationRuleVO.getSubModuleCode());
		logger.info(MODULE_ITEM_KEY, evaluateValidationRuleVO.getModuleItemKey());
		logger.info(LOGIN_PERSON_ID, evaluateValidationRuleVO.getLogginPersonId());
		logger.info(UPDATE_USER, evaluateValidationRuleVO.getUpdateUser());
		logger.info(SUB_MODULE_ITEM_KEY, evaluateValidationRuleVO.getSubModuleItemKey());
		return businessRuleService.evaluateNotificationRule(evaluateValidationRuleVO);
	}

	@PostMapping(value = "/ruleEvaluate", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String ruleEvaluate(@RequestBody EvaluateValidationRuleVO evaluateValidationRuleVO, HttpServletRequest request, HttpServletResponse response) {
		logger.info("Requesting for ruleEvaluate");
		logger.info(MODULE_CODE, evaluateValidationRuleVO.getModuleCode());
		logger.info(SUB_MODULE_CODE, evaluateValidationRuleVO.getSubModuleCode());
		logger.info(MODULE_ITEM_KEY, evaluateValidationRuleVO.getModuleItemKey());
		logger.info(LOGIN_PERSON_ID, evaluateValidationRuleVO.getLogginPersonId());
		logger.info(UPDATE_USER, evaluateValidationRuleVO.getUpdateUser());
		logger.info("ruleId : {}", evaluateValidationRuleVO.getRuleId());
		return businessRuleService.ruleEvaluate(evaluateValidationRuleVO);
	}

	@PostMapping(value = "/getWorkFlowRouteLog", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String getWorkFlowRouteLog(@RequestBody EvaluateValidationRuleVO evaluateValidationRuleVO, HttpServletRequest request, HttpServletResponse response) {
		logger.info("Requesting for getWorkFlowRouteLog");
		logger.info(MODULE_ITEM_KEY, evaluateValidationRuleVO.getModuleItemKey());
		logger.info(MODULE_CODE, evaluateValidationRuleVO.getModuleCode());
		return businessRuleService.getWorkFlowRouteLog(evaluateValidationRuleVO.getModuleItemKey(), evaluateValidationRuleVO.getModuleCode());
	}

	@PostMapping(value = "/workflowfinalApproval", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String workflowfinalApproval(@RequestBody EvaluateValidationRuleVO evaluateValidationRuleVO, HttpServletRequest request, HttpServletResponse response) {
		logger.info("Requesting for workflowfinalApproval");
		logger.info(MODULE_ITEM_KEY, evaluateValidationRuleVO.getModuleItemKey());
		logger.info("logginPersonId : {}", evaluateValidationRuleVO.getLogginPersonId());
		logger.info(MODULE_CODE, evaluateValidationRuleVO.getModuleCode());
		logger.info(SUB_MODULE_CODE, evaluateValidationRuleVO.getSubModuleCode());
		logger.info(SUB_MODULE_ITEM_KEY, evaluateValidationRuleVO.getSubModuleItemKey());
		return businessRuleService.workflowfinalApproval(evaluateValidationRuleVO.getModuleItemKey(), evaluateValidationRuleVO.getLogginPersonId(), evaluateValidationRuleVO.getModuleCode(), evaluateValidationRuleVO.getSubModuleCode(), evaluateValidationRuleVO.getSubModuleItemKey());
	}

	@PostMapping(value = "/getWorkFlow", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public Workflow getWorkFlow(@RequestBody EvaluateValidationRuleVO evaluateValidationRuleVO, HttpServletRequest request, HttpServletResponse response) {
		logger.info("Requesting for getWorkFlowRouteLog ");
		logger.info(MODULE_ITEM_KEY, evaluateValidationRuleVO.getModuleItemKey());
		logger.info(MODULE_CODE, evaluateValidationRuleVO.getModuleCode());
		return businessRuleService.getWorkFlow(evaluateValidationRuleVO.getModuleItemKey(), evaluateValidationRuleVO.getModuleCode());
	}

	@GetMapping(value = "/downloadWorkflowsAttachments")
	public ResponseEntity<byte[]> downloadWorkflowsAttachments(HttpServletResponse response, @RequestHeader("attachmentId") String attachmentId) {
		logger.info("Requesting for downloadNegotiationAttachment");
		logger.info("negotiationsAttachmentId : {}", attachmentId);
		Integer workflowAttachmentId = Integer.parseInt(attachmentId);
		return businessRuleService.downloadWorkflowsAttachments(workflowAttachmentId);
	}

}
