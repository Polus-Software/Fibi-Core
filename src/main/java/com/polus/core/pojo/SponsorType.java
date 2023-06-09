package com.polus.core.pojo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.polus.core.util.JpaCharBooleanConversion;

@Entity
@Table(name = "SPONSOR_TYPE")
public class SponsorType implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "SPONSOR_TYPE_CODE", updatable = false, nullable = false)
	private String code;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "BUDGET_CATEGORY_CODE")
	private String budgetCategoryCode;

	@Column(name = "FROM_GL_MAPPING")
	private String fromGlMapping;

	@Column(name = "TO_GL_MAPPING")
	private String toGlMapping;

	@Column(name = "IS_ACTIVE")
	@Convert(converter = JpaCharBooleanConversion.class)
	private Boolean isActive;

	public SponsorType() {
		super();
	}

	public SponsorType(String sponsorTypeCode, String sponsorType) {
		super();
		this.code = sponsorTypeCode;
		this.description = sponsorType;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getBudgetCategoryCode() {
		return budgetCategoryCode;
	}

	public void setBudgetCategoryCode(String budgetCategoryCode) {
		this.budgetCategoryCode = budgetCategoryCode;
	}

	public String getFromGlMapping() {
		return fromGlMapping;
	}

	public void setFromGlMapping(String fromGlMapping) {
		this.fromGlMapping = fromGlMapping;
	}

	public String getToGlMapping() {
		return toGlMapping;
	}

	public void setToGlMapping(String toGlMapping) {
		this.toGlMapping = toGlMapping;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
}
