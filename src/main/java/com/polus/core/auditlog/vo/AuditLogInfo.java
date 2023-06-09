package com.polus.core.auditlog.vo;

import java.sql.Timestamp;
import java.util.Map;

public class AuditLogInfo {

	private Integer logId;
	
	private String module;
	
	private String subModule;
	
	private String moduleItemKey;
	
	private String actionPersonId;
	
	private String actionType;
	
	private String systemMessage;
		
	private Map<String, Object> action;
	
	private String updateUser;
	
	private Timestamp updateTimestamp;
	
	private String after;
	
	private String before;
	
	private String changes;

	public String getChanges() {
		return changes;
	}

	public void setChanges(String changes) {
		this.changes = changes;
	}

	public String getAfter() {
		return after;
	}

	public void setAfter(String after) {
		this.after = after;
	}

	public String getBefore() {
		return before;
	}

	public void setBefore(String before) {
		this.before = before;
	}

	public Integer getLogId() {
		return logId;
	}

	public void setLogId(Integer logId) {
		this.logId = logId;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getSubModule() {
		return subModule;
	}

	public void setSubModule(String subModule) {
		this.subModule = subModule;
	}

	public String getModuleItemKey() {
		return moduleItemKey;
	}

	public void setModuleItemKey(String moduleItemKey) {
		this.moduleItemKey = moduleItemKey;
	}

	public String getActionPersonId() {
		return actionPersonId;
	}

	public void setActionPersonId(String actionPersonId) {
		this.actionPersonId = actionPersonId;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public String getSystemMessage() {
		return systemMessage;
	}

	public void setSystemMessage(String systemMessage) {
		this.systemMessage = systemMessage;
	}

	public Map<String, Object> getAction() {
		return action;
	}

	public void setAction(Map<String, Object> action) {
		this.action = action;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public Timestamp getUpdateTimestamp() {
		return updateTimestamp;
	}

	public void setUpdateTimestamp(Timestamp updateTimestamp) {
		this.updateTimestamp = updateTimestamp;
	}

	public AuditLogInfo() {
		super();
	}

}
