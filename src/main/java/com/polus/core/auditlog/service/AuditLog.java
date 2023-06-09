package com.polus.core.auditlog.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polus.core.auditlog.vo.AuditLogInfo;

@Transactional
@Service
public interface AuditLog {
	
	public String log(AuditLogInfo auditLogInfoVO);

}
