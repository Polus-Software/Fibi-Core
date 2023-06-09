package com.polus.core.auditlog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polus.core.auditlog.dao.AuditLogEngine;
import com.polus.core.auditlog.pojo.AuditLogger;
import com.polus.core.auditlog.vo.AuditLogInfo;
import com.polus.core.common.dao.CommonDao;
import com.polus.core.security.AuthenticatedUser;

@Transactional
@Service
public class AdminActionAuditLogger implements AdminActionAuditLog, AuditLog{
	
	@Autowired
	private AuditLogEngine auditLogEngine;
	
	@Autowired
	private AuditLogger auditLogger;
	
	@Autowired
	private CommonDao commonDao;

	@Override
	public String log(AuditLogInfo auditLogInfoVO) {
		auditLogger.setModule(auditLogInfoVO.getModule());
		auditLogger.setSubModule(auditLogInfoVO.getSubModule());
		auditLogger.setModuleItemKey(auditLogInfoVO.getModuleItemKey());
		auditLogger.setActionpersonId(AuthenticatedUser.getLoginPersonId());
		auditLogger.setActionType(auditLogInfoVO.getActionType());
		auditLogger.setAction(commonDao.convertObjectToJSON(auditLogInfoVO.getAction()));
		auditLogger.setUpdateUser(AuthenticatedUser.getLoginUserName());
		auditLogger.setUpdateTimesatmp(commonDao.getCurrentTimestamp());
		auditLogger.setChanges(auditLogInfoVO.getChanges());
		return commonDao.convertObjectToJSON(auditLogEngine.log(auditLogger));
	}

	@Override
	public String isLogEnabled(String moduleName) {
		return commonDao.convertObjectToJSON(auditLogEngine.isLogEnabled(moduleName));
	}

}
