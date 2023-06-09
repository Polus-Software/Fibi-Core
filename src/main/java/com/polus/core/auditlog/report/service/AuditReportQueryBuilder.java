package com.polus.core.auditlog.report.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class AuditReportQueryBuilder {

	private List<String> personIds;

	private List<String> modules;

	private LocalDate actionFrom;

	private LocalDate actionTo;
	
	private String query;

	public List<String> getPersonIds() {
		return personIds;
	}

	public void setPersonIds(List<String> personIds) {
		this.personIds = personIds;
	}

	public List<String> getModules() {
		return modules;
	}

	public void setModules(List<String> modules) {
		this.modules = modules;
	}

	public LocalDate getActionFrom() {
		return actionFrom;
	}

	public void setActionFrom(LocalDate actionFrom) {
		this.actionFrom = actionFrom;
	}

	public LocalDate getActionTo() {
		return actionTo;
	}

	public void setActionTo(LocalDate actionTo) {
		this.actionTo = actionTo;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public void buildSQL() {
		StringBuilder queryBuilder = new StringBuilder();
		String personIdsString = null;
		String modulesString = null;
		queryBuilder.append(
				"SELECT LOG_ID, MODULE, SUB_MODULE, MODULE_ITEM_KEY, ACTION_PERSON_ID, CHANGES, ACTION_TYPE, UPDATE_USER, UPDATE_TIMESTAMP FROM AUDIT_LOG WHERE ");
		if (personIds != null && modules != null) {
			StringBuilder personIdsSB = new StringBuilder();
			for (String s : personIds) {
				personIdsSB.append("'" + s + "'" + ",");
			}
			personIdsString = personIdsSB.toString().substring(0, personIdsSB.toString().length() - 1);
			StringBuilder modulesSB = new StringBuilder();
			for (String s : modules) {
				modulesSB.append("'" + s + "'" + ",");
			}
			modulesString = modulesSB.toString().substring(0, modulesSB.toString().length() - 1);
			queryBuilder.append("ACTION_PERSON_ID in (" + personIdsString + ") AND MODULE in ("+ modulesString + ") AND UPDATE_TIMESTAMP BETWEEN :fromDate AND :toDate ");
			this.setQuery(queryBuilder.toString());
		} else if (personIds == null && modules == null) {
			queryBuilder.append("UPDATE_TIMESTAMP BETWEEN :fromDate AND :toDate ");
			this.setQuery(queryBuilder.toString());
		} else if (personIds != null && modules == null) {
			StringBuilder personIdsSB = new StringBuilder();
			for (String s : personIds) {
				personIdsSB.append('"' + s + '"' + ",");
			}
			personIdsString = personIdsSB.toString().substring(0, personIdsSB.toString().length() - 1);
			queryBuilder.append("ACTION_PERSON_ID in (" + personIdsString + ") AND UPDATE_TIMESTAMP BETWEEN :fromDate AND :toDate ");
			this.setQuery(queryBuilder.toString());
		} else if (personIds == null && modules != null) {
			StringBuilder modulesSB = new StringBuilder();
			for (String s : modules) {
				modulesSB.append('"' + s + '"' + ",");
			}
			modulesString = modulesSB.toString().substring(0, modulesSB.toString().length() - 1);
			queryBuilder.append("MODULE in (" + modulesString + ") AND UPDATE_TIMESTAMP BETWEEN :fromDate AND :toDate ");
			this.setQuery(queryBuilder.toString());
		}
	}
	
	public void clearDataMembers() {
		this.setActionFrom(null);
		this.setActionTo(null);
		this.setModules(null);
		this.setPersonIds(null);
		this.setQuery(null);
	}

}
