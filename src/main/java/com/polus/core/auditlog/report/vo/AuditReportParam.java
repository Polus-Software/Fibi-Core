package com.polus.core.auditlog.report.vo;

import java.util.List;

public class AuditReportParam {

	private List<String> personIds;

	private List<String> moduleNames;
	
	private List<String> moduleDescriptions;

	private String actionFrom;

	private String actionTo;

	private String requesterPersonId;

	private String criteria;

	private List<String> personNames;

	public List<String> getPersonNames() {
		return personNames;
	}

	public void setPersonNames(List<String> personNames) {
		this.personNames = personNames;
	}

	public List<String> getPersonIds() {
		return personIds;
	}

	public void setPersonIds(List<String> personIds) {
		this.personIds = personIds;
	}

	public String getActionFrom() {
		return actionFrom;
	}

	public List<String> getModuleNames() {
		return moduleNames;
	}

	public void setModuleNames(List<String> moduleNames) {
		this.moduleNames = moduleNames;
	}

	public List<String> getModuleDescriptions() {
		return moduleDescriptions;
	}

	public void setModuleDescriptions(List<String> moduleDescriptions) {
		this.moduleDescriptions = moduleDescriptions;
	}

	public void setActionFrom(String actionFrom) {
		this.actionFrom = actionFrom;
	}

	public String getActionTo() {
		return actionTo;
	}

	public void setActionTo(String actionTo) {
		this.actionTo = actionTo;
	}

	public String getRequesterPersonId() {
		return requesterPersonId;
	}

	public void setRequesterPersonId(String requesterPersonId) {
		this.requesterPersonId = requesterPersonId;
	}

	public String getCriteria() {
		return criteria;
	}

	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}

}
