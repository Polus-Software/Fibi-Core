package com.polus.core.externalreviewer.pojo;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.polus.core.util.JpaCharBooleanConversion;

@Entity
@Table(name = "EXT_REVIEWER_ACADEMIC_SUB_AREA")
public class ExtReviewerAcademicSubArea implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ACADEMIC_SUB_AREA_CODE")
	private String academicSubAreaCode;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "ACADEMIC_AREA_CODE")
	private String academicAreaCode;

	@ManyToOne(optional = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "ACADEMIC_SUB_AREA_FK1"), name = "ACADEMIC_AREA_CODE", referencedColumnName = "ACADEMIC_AREA_CODE", insertable = false, updatable = false)
	private ExtReviewerAcademicArea academicArea;

	@Column(name = "UPDATE_TIMESTAMP")
	private Timestamp updateTimestamp;

	@Column(name = "UPDATE_USER")
	private String updateUser;

	@Column(name = "IS_ACTIVE")
	@Convert(converter = JpaCharBooleanConversion.class)
	private Boolean isActive;

	public String getAcademicSubAreaCode() {
		return academicSubAreaCode;
	}

	public void setAcademicSubAreaCode(String academicSubAreaCode) {
		this.academicSubAreaCode = academicSubAreaCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAcademicAreaCode() {
		return academicAreaCode;
	}

	public void setAcademicAreaCode(String academicAreaCode) {
		this.academicAreaCode = academicAreaCode;
	}

	public ExtReviewerAcademicArea getAcademicArea() {
		return academicArea;
	}

	public void setAcademicArea(ExtReviewerAcademicArea academicArea) {
		this.academicArea = academicArea;
	}

	public Timestamp getUpdateTimestamp() {
		return updateTimestamp;
	}

	public void setUpdateTimestamp(Timestamp updateTimestamp) {
		this.updateTimestamp = updateTimestamp;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

}
