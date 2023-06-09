package com.polus.core.general.pojo;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.polus.core.util.JpaCharBooleanConversion;

@Entity
@Table(name = "LOCK_CONFIGURATION")
public class LockConfiguration implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "MODULE_CODE")
	private String moduleCode;

	@Id
	@Column(name = "SUB_MODULE_CODE")
	private String subModuleCode;

	@JsonIgnore
	@Column(name = "DESCRIPTION")
	private String description;
	
	@Column(name = "IS_ACTIVE")
	@Convert(converter = JpaCharBooleanConversion.class)
	private Boolean isActive;
	
	@Column(name = "IS_CHAT_ENABLED")
	@Convert(converter = JpaCharBooleanConversion.class)
	private Boolean chatActiveFlag;

	@JsonIgnore
	@Column(name = "UPDATE_USER")
	private String updateUser;

	@JsonIgnore
	@Column(name = "UPDATE_TIMESTAMP")
	private Timestamp updateTimestamp;

	public String getModuleCode() {
		return moduleCode;
	}

	public void setModuleCode(String moduleCode) {
		this.moduleCode = moduleCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Boolean getChatActiveFlag() {
		return chatActiveFlag;
	}

	public void setChatActiveFlag(Boolean chatActiveFlag) {
		this.chatActiveFlag = chatActiveFlag;
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

	public String getSubModuleCode() {
		return subModuleCode;
	}

	public void setSubModuleCode(String subModuleCode) {
		this.subModuleCode = subModuleCode;
	}

}
