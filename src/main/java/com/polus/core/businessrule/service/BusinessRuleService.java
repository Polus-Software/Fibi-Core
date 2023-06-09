package com.polus.core.businessrule.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.polus.core.businessrule.vo.EvaluateValidationRuleVO;
import com.polus.core.workflow.pojo.Workflow;

@Service
public interface BusinessRuleService {

	/**
	 * evaluateValidationRule workflow 
	 * @param vO
	 * @return
	 */
	public Integer buildWorkFlow(EvaluateValidationRuleVO vO);

	/**
	 * this method is used for evaluate validation rule
	 * @param evaluateValidationRuleVO
	 * @return
	 */
	public String evaluateValidationRule(EvaluateValidationRuleVO evaluateValidationRuleVO);

	/**
	 * this method is used for evaluate notification rule
	 * @param evaluateValidationRuleVO
	 * @return
	 */
	public String evaluateNotificationRule(EvaluateValidationRuleVO evaluateValidationRuleVO);

	/**
	 * this method is used for evaluate a rule based on input rule id and module data.
	 * @param evaluateValidationRuleVO
	 * @return
	 */
	public String ruleEvaluate(EvaluateValidationRuleVO evaluateValidationRuleVO);

	/**
	 * this method is used for display the user whether he can approve or disapprove the Routing
	 * @param moduleItemKey
	 * @param moduleCode
	 * @param subModuleCode
	 * @param subModuleItemKey
	 * @param loginPersonId
	 * @return
	 */
	public Integer canApproveRouting(String moduleItemKey, String loginPersonId, Integer moduleCode, String subModuleItemKey, Integer subModuleCode);

	/**
	 * this method is used to get workflow routing log with respect to the proposal id
	 * @param moduleItemId
	 * @param moduleCode
	 * @return
	 */
	public String getWorkFlowRouteLog(String moduleItemId,Integer moduleCode);

	/**
	 * this method is used to check current login user is the final approver or not
	 * @param moduleItemKey
	 * @param personId
	 * @return
	 */
	public String workflowfinalApproval(String moduleItemKey, String personId, Integer moduleCode, Integer subModuleCode, String subModuleItemKey);

	/**
	 * @param moduleItemKey
	 * @param moduleCode
	 * @return
	 */
	public Workflow getWorkFlow(String moduleItemKey, Integer moduleCode);

	/**
	 * 
	 * @param workflowAttachmentId
	 * @return
	 */
	public ResponseEntity<byte[]> downloadWorkflowsAttachments(Integer workflowAttachmentId);

	/**
	 * This method is used to send notification based on the business rule configuration
	 * @param moduleCode
	 * @param subModuleCode
	 * @param moduleItemKey
	 * @param string
	 * @param personId
	 * @param updateUser
	 */
	public void evaluateAndSentNotification(Integer moduleCode, Integer subModuleCode, String moduleItemKey,
			String string, String personId, String updateUser, Map<String, String> placeHolder);

}
