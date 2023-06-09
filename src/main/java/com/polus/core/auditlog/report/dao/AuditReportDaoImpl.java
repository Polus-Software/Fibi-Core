package com.polus.core.auditlog.report.dao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polus.core.auditlog.pojo.AuditLogConfig;
import com.polus.core.auditlog.report.service.AuditReportQueryBuilder;
import com.polus.core.auditlog.vo.AuditLogInfo;

@Transactional
@Service
public class AuditReportDaoImpl implements AuditReportDao {

	@Autowired
	private HibernateTemplate hibernateTemplate;
	
	@Autowired
	private AuditReportQueryBuilder auditReportQueryBuilder;

	@SuppressWarnings("unchecked")
	@Override
	public List<AuditLogInfo> fetchReport() {
		List<AuditLogInfo> auditLogInfoList = new ArrayList<>();
		Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
		Query query = session.createSQLQuery(auditReportQueryBuilder.getQuery());
		Date fromDate = java.sql.Timestamp.valueOf(auditReportQueryBuilder.getActionFrom().atTime(00, 00, 00));
		Date toDate = java.sql.Timestamp.valueOf(auditReportQueryBuilder.getActionTo().atTime(23, 59, 59));
		query.setParameter("fromDate", fromDate);
		query.setParameter("toDate", toDate);
		List<Object[]> resultSet = query.getResultList();
		for (Object[] auditLogInfoObject : resultSet) {
			AuditLogInfo auditLogInfo = new AuditLogInfo();
			auditLogInfo.setLogId(Integer.parseInt(Objects.toString(auditLogInfoObject[0], null)));
			auditLogInfo.setModule(Objects.toString(auditLogInfoObject[1], null));
			auditLogInfo.setSubModule(Objects.toString(auditLogInfoObject[2], null));
			auditLogInfo.setModuleItemKey(Objects.toString(auditLogInfoObject[3], null));
			auditLogInfo.setActionPersonId(Objects.toString(auditLogInfoObject[4], null));
			auditLogInfo.setChanges(Objects.toString(auditLogInfoObject[5], null));
			auditLogInfo.setActionType(Objects.toString(auditLogInfoObject[6], null));
			auditLogInfo.setUpdateUser(Objects.toString(auditLogInfoObject[7], null));
			if (auditLogInfoObject[8] != null)
				auditLogInfo.setUpdateTimestamp((Timestamp) auditLogInfoObject[8]);
			auditLogInfoList.add(auditLogInfo);
		}
		return auditLogInfoList;
	}

	@Override
	public String fetchModuleDescriptionByModule(String module) {
		Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<AuditLogConfig> criteriaQuery = criteriaBuilder.createQuery(AuditLogConfig.class);
		Root<AuditLogConfig> root = criteriaQuery.from(AuditLogConfig.class);
		criteriaQuery.select(root).where(root.get("module").in(module));
		return session.createQuery(criteriaQuery).getSingleResult().getDescription();
	}

}
