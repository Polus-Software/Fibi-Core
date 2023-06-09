package com.polus.core.auditlog.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.polus.core.auditlog.service.AdminActionAuditLogger;
import com.polus.core.auditlog.vo.AuditLogInfo;

@RestController
public class AuditLogController {

	@Autowired
	private AdminActionAuditLogger adminActionAuditLogger;

	@PostMapping(value = "/auditLogAdminModule")
	public String auditLogAdminModule(@RequestBody AuditLogInfo auditLogInfoVO, HttpServletRequest request) {
		return adminActionAuditLogger.log(auditLogInfoVO);
	}

	@GetMapping(value = "/isAuditLogEnabled/{moduleName}")
	public ResponseEntity<String> isAuditLogEnabled(@PathVariable final String moduleName) {
		return new ResponseEntity<>(adminActionAuditLogger.isLogEnabled(moduleName), HttpStatus.OK);
	}

}
