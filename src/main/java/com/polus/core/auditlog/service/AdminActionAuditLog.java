package com.polus.core.auditlog.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public interface AdminActionAuditLog {
	
	public String isLogEnabled(String moduleName);

}
