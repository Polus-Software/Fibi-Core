package com.polus.core.auditlog.report.service;

import java.text.ParseException;

import org.springframework.http.ResponseEntity;

import com.polus.core.auditlog.report.vo.AuditReportParam;

public interface AuditReportService {

	public boolean isAuthorized(AuditReportParam auditReportParamVO);

	public ResponseEntity<byte[]> generateReport(AuditReportParam auditReportParamVO) throws ParseException;

}
