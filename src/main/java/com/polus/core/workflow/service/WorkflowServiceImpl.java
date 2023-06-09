package com.polus.core.workflow.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.polus.core.common.dao.CommonDao;
import com.polus.core.common.service.CommonService;
import com.polus.core.person.dao.PersonDao;
import com.polus.core.pojo.FileData;
import com.polus.core.workflow.comparator.WorkflowDetailComparator;
import com.polus.core.workflow.dao.WorkflowDao;
import com.polus.core.workflow.pojo.Workflow;
import com.polus.core.workflow.pojo.WorkflowAttachment;
import com.polus.core.workflow.pojo.WorkflowDetail;
import com.polus.core.workflow.pojo.WorkflowDetailExt;
import com.polus.core.workflow.vo.WorkflowVO;

@Transactional
@Service(value = "workflowService")
public class WorkflowServiceImpl implements WorkflowService {

	protected static Logger logger = LogManager.getLogger(WorkflowServiceImpl.class.getName());

	@Autowired
	private WorkflowDao workflowDao;

	@Autowired
	private CommonDao commonDao;

	@Autowired
	private CommonService commonService;

	@Autowired
	private WorkflowExtService workflowExtService;

	@Autowired
	private PersonDao personDao;

	public WorkflowDetail addWorkflowAttachments(String personId, String approverComment, MultipartFile[] files, WorkflowDetail workflowDetail) throws IOException {
		List<WorkflowAttachment> workflowAttachments = new ArrayList<WorkflowAttachment>();
		for (int i = 0; i < files.length; i++) {
			WorkflowAttachment workflowAttachment = new WorkflowAttachment();
			workflowAttachment.setDescription(approverComment);
			workflowAttachment.setUpdateTimeStamp(commonDao.getCurrentTimestamp());
			workflowAttachment.setUpdateUser(personId);			
			FileData fileData = new FileData();
			fileData.setAttachment(files[i].getBytes());
			fileData = commonDao.saveFileData(fileData);
			workflowAttachment.setFileDataId(fileData.getFileDataId());
			workflowAttachment.setFileName(files[i].getOriginalFilename());
			workflowAttachment.setMimeType(files[i].getContentType());
			workflowAttachment.setWorkflowDetail(workflowDetail);
			workflowAttachments.add(workflowAttachment);
		}
		workflowDetail.getWorkflowAttachments().addAll(workflowAttachments);
		return workflowDetail;	
	}

	@Override
	public ResponseEntity<byte[]> downloadWorkflowAttachment(Integer attachmentId) {
		WorkflowAttachment attachment = workflowDao.fetchWorkflowAttachmentById(attachmentId);
		ResponseEntity<byte[]> attachmentData = null;
		try {
			attachmentData = commonService.setAttachmentContent(attachment.getFileName(), attachment.getAttachment());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return attachmentData;
	}

	@Override
	public WorkflowDetail getCurrentWorkflowDetail(Integer workflowId, String personId, Integer roleCode) {
		return workflowDao.getCurrentWorkflowDetail(workflowId, personId, roleCode);
	}

	@Override
	public Set<String> getEmailAdressByUserType(String roleTypeCode) {
		return workflowDao.fetchEmailAdressByUserType(roleTypeCode);
	}

	@Override
	public void prepareWorkflowDetails(Workflow workflow) {
		Map<Integer, List<WorkflowDetail>> workflowDetailMap = new HashMap<Integer, List<WorkflowDetail>>();
		List<WorkflowDetail> workflowDetails = workflow.getWorkflowDetails();
		if (workflowDetails != null && !workflowDetails.isEmpty()) {
			Collections.sort(workflowDetails, new WorkflowDetailComparator());
			for (WorkflowDetail workflowDetail : workflowDetails) {

				if (workflowDetail.getApprovalStatusCode().equals("W")) {
					workflow.setCurrentStopName(workflowDao.fetchStopNameBasedMapIdAndStop(workflowDetail.getMapId(), workflowDetail.getApprovalStopNumber()));
				}
				if (workflowDetail.getUpdateUser() != null) {
					workflowDetail.setUpdateUserFullName(personDao.getUserFullNameByUserName(workflowDetail.getUpdateUser()));
				}
				if (workflow.getMapType() != null && workflow.getMapType().equals("E")) {
					if (workflowExtService.checkForEvaluationPanelRank(workflowDetail.getMapId(), workflow.getModuleCode(),Integer.parseInt(workflow.getModuleItemId()))) {
						workflowDetail.setIsReviewerCanScore(true);
					}
				}
				WorkflowDetailExt workflowDetailExt = workflowDao.fetchWorkflowExtBasedOnWorkflowDetailId(workflowDetail.getWorkflowDetailId());
				if (workflowDetailExt != null) {
					workflowDetail.setWorkflowDetailExt(workflowDetailExt);
				}
				if (workflowDetailMap.get(workflowDetail.getApprovalStopNumber()) != null) {
					workflowDetailMap.get(workflowDetail.getApprovalStopNumber()).add(workflowDetail);
				} else {
					List<WorkflowDetail> details = new ArrayList<>();
					details.add(workflowDetail);
					workflowDetailMap.put(workflowDetail.getApprovalStopNumber(), details);
				}
			}
		}
			workflow.setWorkflowDetailMap(workflowDetailMap);
	}

	@Override
	public void prepareWorkflowDetailsList(List<Workflow> workflowList) {
		for (Workflow workflow : workflowList) {
			Map<Integer, List<WorkflowDetail>> workflowDetailMap = new HashMap<Integer, List<WorkflowDetail>>();
			workflow.setWorkflowCreatedBy(personDao.getPersonFullNameByPersonId(workflow.getWorkflowStartPerson()));
			prepareWorkflowDetails(workflow);
			List<WorkflowDetail> workflowDetails = workflow.getWorkflowDetails();
			if (workflowDetails != null && !workflowDetails.isEmpty()) {
				for (WorkflowDetail workflowDetail : workflowDetails) {
					if (workflowDetail.getUpdateUser() != null) {
						workflowDetail.setUpdateUserFullName(personDao.getUserFullNameByUserName(workflowDetail.getUpdateUser()));
					}
					WorkflowDetailExt workflowDetailExt = workflowDao.fetchWorkflowExtBasedOnWorkflowDetailId(workflowDetail.getWorkflowDetailId());
					if (workflowDetailExt != null) {
						workflowDetail.setWorkflowDetailExt(workflowDetailExt);
					}
					if (workflowDetailMap.get(workflowDetail.getApprovalStopNumber()) != null) {
						workflowDetailMap.get(workflowDetail.getApprovalStopNumber()).add(workflowDetail);
					} else {
						List<WorkflowDetail> details = new ArrayList<>();
						details.add(workflowDetail);
						workflowDetailMap.put(workflowDetail.getApprovalStopNumber(), details);
					}
					if (workflowDetail.getDelegatedByPersonId() != null) {
						workflowDetail.setDelegatedPersonName(personDao.getPersonFullNameByPersonId(workflowDetail.getDelegatedByPersonId()));
					}
				}
			}
			workflow.setWorkflowDetailMap(workflowDetailMap);
		}
	}

	@Override
	public String fetchWorkflowMapType() {
		WorkflowVO vo=new WorkflowVO();
        vo.setWorkflowMapType(workflowDao.fetchAllWorkflowMapTypes());
		return commonDao.convertObjectToJSON(vo);
	}

	@Override
	public String getPlaceHolderDataForRouting(Integer approvalStopNumber, Integer mapId, Integer workflowdetailId) {
		String stopName = null;
		if (approvalStopNumber != null && mapId != null) {
			stopName = workflowDao.fetchStopNameBasedMapIdAndStop(mapId, approvalStopNumber);
		} else if (workflowdetailId != null) {
			WorkflowDetail workFlowDetails = workflowDao.getWorkFlowDetails(workflowdetailId);
			if (workFlowDetails.getMapId() != null && workFlowDetails.getApprovalStopNumber() != null) {
				stopName = workflowDao.fetchStopNameBasedMapIdAndStop(workFlowDetails.getMapId(),
						workFlowDetails.getApprovalStopNumber());
				approvalStopNumber = workFlowDetails.getApprovalStopNumber();
			}
		}
		if (approvalStopNumber != null && (stopName == null || "".equals(stopName))) {
			stopName = "Stop " + approvalStopNumber;
		}
		return stopName;
	}

}
