package com.polus.core.auditreport.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polus.core.auditreport.pojo.AuditReportType;
import com.polus.core.auditreport.repository.AuditReportTypeRepository;
import com.polus.core.common.dao.CommonDao;

@Transactional
@Service(value = "auditReportService")
public class AuditReportServiceImpl implements AuditReportService {

	@Autowired
	private AuditReportTypeRepository auditReportTypeRepository;

	@Autowired
	CommonDao commonDao;

	@Override
	public String getAuditReportTypes() {
		Iterable<AuditReportType> data = auditReportTypeRepository.findAll();
		return commonDao.convertObjectToJSON(data);
	}

}
