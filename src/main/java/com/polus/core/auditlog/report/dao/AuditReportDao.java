package com.polus.core.auditlog.report.dao;

import java.util.List;

import com.polus.core.auditlog.vo.AuditLogInfo;

public interface AuditReportDao {

	public String fetchModuleDescriptionByModule(String module);
	
	public List<AuditLogInfo> fetchReport();

}
