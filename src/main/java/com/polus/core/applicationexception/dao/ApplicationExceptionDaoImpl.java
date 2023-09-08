package com.polus.core.applicationexception.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polus.core.applicationexception.pojo.ApplicationErrorDetails;

@Transactional
@Service(value = "applicationExceptionDao")
public class ApplicationExceptionDaoImpl implements ApplicationExceptionDao {

	@Autowired
	private HibernateTemplate hibernateTemplate;

	@Override
	public void saveErrorDetails(ApplicationErrorDetails applicationErrorDetails) {
		hibernateTemplate.save(applicationErrorDetails);
	}

	@Override
	public List<ApplicationErrorDetails> fetchElasticErrorDetails(Date fromDate, Date toDate) {
		Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<ApplicationErrorDetails> criteriaQuery = criteriaBuilder
				.createQuery(ApplicationErrorDetails.class);
		Root<ApplicationErrorDetails> root = criteriaQuery.from(ApplicationErrorDetails.class);
		Predicate errorCodePredicate = criteriaBuilder.or(criteriaBuilder.equal(root.get("errorCode"), "ELASTIC_SYNC"),
				criteriaBuilder.equal(root.get("errorCode"), "ELASTIC_FIBI"));
		Predicate datePredicate = criteriaBuilder.between(root.get("createTimestamp"), fromDate, toDate);
		criteriaQuery.select(root).where(criteriaBuilder.and(errorCodePredicate, datePredicate));
		return session.createQuery(criteriaQuery).getResultList();
	}

}
