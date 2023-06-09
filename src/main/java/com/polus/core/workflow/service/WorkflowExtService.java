package com.polus.core.workflow.service;

import org.springframework.stereotype.Service;

@Service
public interface WorkflowExtService {

	public boolean checkForEvaluationPanelRank(Integer mapId, Integer moduleCode,Integer moduleItemId);

	public Integer getTheNotificationIdForMapPersonMissing(Integer moduleCode);

}
