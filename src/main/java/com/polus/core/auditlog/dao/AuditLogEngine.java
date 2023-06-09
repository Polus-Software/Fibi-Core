package com.polus.core.auditlog.dao;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polus.core.applicationexception.dto.ApplicationException;
import com.polus.core.auditlog.pojo.AuditLogConfig;
import com.polus.core.auditlog.pojo.AuditLogger;
import com.polus.core.constants.Constants;

@Transactional
@Service
@Scope("singleton")
public class AuditLogEngine {

	@Autowired
	private HibernateTemplate hibernateTemplate;

	public AuditLogConfig isLogEnabled(String moduleName) {
		AuditLogConfig auditLogConfig = new AuditLogConfig();
		Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<AuditLogConfig> criteriaQuery = criteriaBuilder.createQuery(AuditLogConfig.class);
		Root<AuditLogConfig> root = criteriaQuery.from(AuditLogConfig.class);
		criteriaQuery.select(root).where(root.get("module").in(moduleName));
		auditLogConfig.setIsActive(session.createQuery(criteriaQuery).getSingleResult().getIsActive());
		return auditLogConfig;
	}

	public AuditLogger log(AuditLogger auditLogger) {
		try {
			hibernateTemplate.save(auditLogger);
		} catch (Exception e) {
			throw new ApplicationException("Error in log", e, Constants.JAVA_ERROR);
		}
		return auditLogger;
	}

	public boolean isFeedLogEnabled() {
		return false;
	}

}
