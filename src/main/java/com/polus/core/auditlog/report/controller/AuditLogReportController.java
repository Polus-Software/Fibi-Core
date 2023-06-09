package com.polus.core.auditlog.report.controller;

import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.polus.core.auditlog.report.service.AuditReportService;
import com.polus.core.auditlog.report.vo.AuditReportParam;

@RestController
public class AuditLogReportController {

	@Autowired
	private AuditReportService auditReportService;

	@PostMapping(value = "/generateAuditLogReport")
	public ResponseEntity<byte[]> auditLogAdminModule(@RequestBody AuditReportParam auditReportParamVO, HttpServletRequest request) throws ParseException {
		if (auditReportService.isAuthorized(auditReportParamVO)) {
			return auditReportService.generateReport(auditReportParamVO);
		}
		return null;
	}

}
