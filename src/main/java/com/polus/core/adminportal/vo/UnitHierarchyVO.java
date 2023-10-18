package com.polus.core.adminportal.vo;

import java.util.List;

import com.polus.core.adminportal.dto.UnitHierarchy;
import com.polus.core.pojo.UnitAdministratorType;

public class UnitHierarchyVO {
	
	private String unitNumber;
	private List<UnitHierarchy> unitHierarchyList;
	private List<UnitAdministratorType> unitAdministratorTypeList;
	private String acType;
	private  List<String> childUnitNumber;

	public String getUnitNumber() {
		return unitNumber;
	}
	
	public void setUnitNumber(String unitNumber) {
		this.unitNumber = unitNumber;
	}
	
	public List<UnitHierarchy> getUnitHierarchyList() {
		return unitHierarchyList;
	}
	
	public void setUnitHierarchyList(List<UnitHierarchy> unitHierarchyList) {
		this.unitHierarchyList = unitHierarchyList;
	}
	
	public List<UnitAdministratorType> getUnitAdministratorTypeList() {
		return unitAdministratorTypeList;
	}
	
	public void setUnitAdministratorTypeList(List<UnitAdministratorType> unitAdministratorTypeList) {
		this.unitAdministratorTypeList = unitAdministratorTypeList;
	}

	public String getAcType() {
		return acType;
	}

	public void setAcType(String acType) {
		this.acType = acType;
	}

	public List<String> getChildUnitNumber() {
		return childUnitNumber;
	}

	public void setChildUnitNumber(List<String> childUnitNumber) {
		this.childUnitNumber = childUnitNumber;
	}
	
	}
