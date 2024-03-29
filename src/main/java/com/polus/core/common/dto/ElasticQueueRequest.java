package com.polus.core.common.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ElasticQueueRequest {

	private String moduleItemKey;

	private String actionType;

	private String indexName;
	
	private List<ElasticQueueRequest> elasticQueueRequestList;

	public String getIndexName() {
		return indexName;
	}
	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}
	public String getActionType() {
		return actionType;
	}
	public void setActionType(String actionType) {
		this.actionType = actionType;
	}
	
	public ElasticQueueRequest(String moduleItemKey,String actionType, String indexName) {
		super();
		this.moduleItemKey = moduleItemKey;
		this.actionType = actionType;
		this.indexName = indexName;
	}

	public ElasticQueueRequest(ElasticQueueRequest source) {
		super();
		this.moduleItemKey = source.moduleItemKey;
		this.actionType = source.actionType;
		this.indexName = source.indexName;
	}

	public ElasticQueueRequest() {
		setElasticQueueRequestList(new ArrayList<>());
	}
	
	public String getModuleItemKey() {
		return moduleItemKey;
	}
	public void setModuleItemKey(String moduleItemKey) {
		this.moduleItemKey = moduleItemKey;
	}

	public List<ElasticQueueRequest> getElasticQueueRequestList() {
		return elasticQueueRequestList;
	}

	public void setElasticQueueRequestList(List<ElasticQueueRequest> elasticQueueRequestList) {
		this.elasticQueueRequestList = elasticQueueRequestList;
	}

}
