package com.polus.core.auditlog.pojo;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.stereotype.Component;

@Entity
@Component
@Table(name = "AUDIT_LOG")
public class AuditLogger implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "LOG_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer logId;

	@Column(name = "MODULE")
	private String module;

	@Column(name = "SUB_MODULE")
	private String subModule;

	@Column(name = "MODULE_ITEM_KEY")
	private String moduleItemKey;

	@Column(name = "ACTION_PERSON_ID")
	private String actionpersonId;

	@Column(name = "ACTION_TYPE")
	private String actionType;

	@Column(name = "ACTION")
	private String action;

	@Column(name = "UPDATE_USER")
	private String updateUser;

	@Column(name = "UPDATE_TIMESTAMP")
	private Timestamp updateTimesatmp;
	
	@Column(name = "CHANGES")
	private String changes;

	public String getChanges() {
		return changes;
	}

	public void setChanges(String changes) {
		this.changes = changes;
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

	public String getActionpersonId() {
		return actionpersonId;
	}

	public void setActionpersonId(String actionpersonId) {
		this.actionpersonId = actionpersonId;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public Timestamp getUpdateTimesatmp() {
		return updateTimesatmp;
	}

	public void setUpdateTimesatmp(Timestamp updateTimesatmp) {
		this.updateTimesatmp = updateTimesatmp;
	}

}
