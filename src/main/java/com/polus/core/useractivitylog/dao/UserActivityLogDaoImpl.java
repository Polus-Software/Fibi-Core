package com.polus.core.useractivitylog.dao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polus.core.constants.Constants;
import com.polus.core.pojo.Unit;
import com.polus.core.useractivitylog.pojo.PersonLoginDetail;
import com.polus.core.useractivitylog.vo.UserActivityLogVO;

@Transactional
@Service(value = "userActivityLogDao")
public class UserActivityLogDaoImpl implements UserActivityLogDao {

	protected static Logger logger = LogManager.getLogger(UserActivityLogDaoImpl.class.getName());
	private static final String END_TIME = "23:59:59.0";
	@Autowired
	private HibernateTemplate hibernateTemplate;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<Unit> getUnitsListByPersonIdAndRights(String personId, List<String> systemRights) {
		Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
		String hqlQuery = "SELECT T1.unitNumber, T1.unitName From Unit T1 where T1.unitNumber IN (SELECT T2.personRoleRTAttributes.unitNumber from PersonRoleRT T2 where T2.personRoleRTAttributes.personId =:personId and T2.personRoleRTAttributes.rightName IN (:rightName))";
		Query query = session.createQuery(hqlQuery);
		query.setParameter("personId", personId);
		query.setParameter("rightName", systemRights);
		if (query.getResultList() != null && !(query.getResultList()).isEmpty()) {
			return query.getResultList();
		}
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public UserActivityLogVO getPersonLoginDetails(UserActivityLogVO vo) {
		List<String> unitList = vo.getUnitList();
		List<PersonLoginDetail> personLoginDetails = new ArrayList<>();
		Timestamp endDate = null;
		Timestamp startDay = null;
		if (vo.getEndDate() != null) {
			endDate = Timestamp.valueOf((vo.getEndDate().toString().substring(0, 11)).concat(END_TIME));
		}
		if (vo.getStartDate() != null) {
			startDay = vo.getStartDate();
		}
		StringBuilder hqlQuery = new StringBuilder();
		hqlQuery.append(" select T1 from PersonLoginDetail T1 ");
		hqlQuery.append(" inner join Person T2 ON T2.personId = T1.personId ");
		hqlQuery.append(" left join Unit T3 ON T3.unitNumber = T2.homeUnit where ");
		if (!vo.getLoginStatus().isEmpty() && vo.getPersonId() == null) {
			hqlQuery.append(" T1.loginStatus = (:loginStatus) and T1.updateTimestamp BETWEEN  :startDay AND :endDate");
		} else if (vo.getPersonId() != null && vo.getLoginStatus().isEmpty()) {
			hqlQuery.append(" T1.personId=:personId and T1.updateTimestamp BETWEEN  :startDay AND :endDate ");
		} else if (!vo.getLoginStatus().isEmpty() && vo.getPersonId() != null) {
			hqlQuery.append(
					" T1.personId=:personId and T1.loginStatus = (:loginStatus) and T1.updateTimestamp BETWEEN  :startDay AND :endDate ");
		} else if (vo.getLoginStatus().isEmpty() && vo.getPersonId() == null) {
			hqlQuery.append(" T1.updateTimestamp BETWEEN  :startDay AND :endDate ");
		}
		if (unitList != null && !unitList.isEmpty()) {
			hqlQuery.append(" and (T1.personId in (select personId from Person  where homeUnit in (:homeUnit)))");
		}
		if (vo.getReverse().equals("ASC")) {
			if (vo.getSortBy().equals("personId")) {
				hqlQuery.append(" order by T1.personId asc ");
			} else if (vo.getSortBy().equals("person.fullName")) {
				hqlQuery.append(" order by T2.fullName asc ");
			} else if (vo.getSortBy().equals("dateAndTime") || vo.getSortBy().equals("")) {
				hqlQuery.append(" order by T1.updateTimestamp asc ");
			} else if (vo.getSortBy().equals("loginStatus")) {
				hqlQuery.append(" order by T1.loginStatus asc ");
			} else if (vo.getSortBy().equals("unit.unitName")) {
				hqlQuery.append(" order by T3.unitName asc ");
			}
		} else {
			if (vo.getSortBy().equals("personId")) {
				hqlQuery.append(" order by T1.personId desc ");
			} else if (vo.getSortBy().equals("person.fullName")) {
				hqlQuery.append(" order by T2.fullName desc ");
			} else if (vo.getSortBy().equals("dateAndTime") || vo.getSortBy().equals("")) {
				hqlQuery.append(" order by T1.updateTimestamp desc ");
			} else if (vo.getSortBy().equals("loginStatus")) {
				hqlQuery.append(" order by T1.loginStatus desc ");
			} else if (vo.getSortBy().equals("unit.unitName")) {
				hqlQuery.append(" order by  T3.unitName desc ");
			}
		}
		Query findPersonDetail = hibernateTemplate.getSessionFactory().getCurrentSession()
				.createQuery(hqlQuery.toString());
		if (unitList != null && !unitList.isEmpty()) {
			findPersonDetail.setParameter("homeUnit", unitList);
		}
		if (vo.getLoginStatus() != null && !vo.getLoginStatus().equals("")) {
			findPersonDetail.setParameter("loginStatus", vo.getLoginStatus());
		}
		if (vo.getPersonId() != null && !vo.getPersonId().equals("")) {
			findPersonDetail.setParameter("personId", vo.getPersonId());
		}
		findPersonDetail.setParameter("startDay", startDay);
		findPersonDetail.setParameter("endDate", endDate);
		Integer count = findPersonDetail.getResultList().size();
		vo.setUserActivityCount(count);
		if (Boolean.TRUE.equals(vo.getIsDownload())) {
			personLoginDetails = findPersonDetail.getResultList();
		} else {
			personLoginDetails = findPersonDetail.setFirstResult((vo.getCurrentPage() - 1) * vo.getItemsPerPage())
					.setMaxResults(vo.getItemsPerPage()).getResultList();
		}
		vo.setPersonLoginDetails(personLoginDetails);
		return vo;
	}

	@SuppressWarnings({"rawtypes" })
	@Override
	public PersonLoginDetail getRecentPersonLoginDetailByUserName(String userName) {
		PersonLoginDetail loginDetails = null;
		Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
		String hqlQuery = "SELECT t1 FROM PersonLoginDetail t1 WHERE t1.loginDetailId = (SELECT max(t2.loginDetailId) FROM PersonLoginDetail t2 WHERE t2.personId=(SELECT t3.personId FROM Person t3 WHERE t3.principalName =:userName) AND t2.loginStatus=:status)";
		Query query = session.createQuery(hqlQuery);
		query.setParameter("userName", userName);
		query.setParameter("status", "IN");
		loginDetails = (PersonLoginDetail) query.uniqueResult();
		return loginDetails;
  }

	@Override
	public void savePersonLoginDetail(PersonLoginDetail personLoginDetail) {
		try {
			hibernateTemplate.save(personLoginDetail);
		} catch (Exception e) {
			logger.error("Exception in savePersonLoginDetail : {}", e.getMessage());
		}
	}

	@Override
	public PersonLoginDetail getPersonLoginDetailById(Integer personLoginDetailId) {
		return hibernateTemplate.get(PersonLoginDetail.class, personLoginDetailId);
	}

	@Override
	public UserActivityLogVO fetchAllPersonLoginDetails(UserActivityLogVO vo) {
		List<PersonLoginDetail> personLoginDetails = new ArrayList<>();
		Timestamp endDate = null ;
		Timestamp startDay = null;
		if (vo.getEndDate() != null) {
			endDate = Timestamp.valueOf((vo.getEndDate().toString().substring(0, 11)).concat(Constants.END_TIME));
		}
		if( vo.getStartDate() != null) {
			startDay = vo.getStartDate();
		}
		Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<PersonLoginDetail> query = builder.createQuery(PersonLoginDetail.class);
		Root<PersonLoginDetail> rootPersonLoginDetail = query.from(PersonLoginDetail.class);
		Predicate predicateOne = builder.equal(rootPersonLoginDetail.get("loginStatus"), vo.getLoginStatus());
		Predicate predicateThree ;
		Predicate predicateFour = builder.equal(rootPersonLoginDetail.get("personId"), vo.getPersonId());
			if (!vo.getLoginStatus().isEmpty() && vo.getPersonId() == null) {
				predicateThree= builder.between(rootPersonLoginDetail.get("updateTimestamp"), startDay, endDate);
				query.where(builder.and(predicateOne, predicateThree));
			} else if (vo.getPersonId() != null && vo.getLoginStatus().isEmpty() ) {
				predicateThree= builder.between(rootPersonLoginDetail.get("updateTimestamp"), startDay, endDate);
				query.where(builder.and(predicateThree, predicateFour));
			} else if (!vo.getLoginStatus().isEmpty() && vo.getPersonId() != null) {
				predicateThree= builder.between(rootPersonLoginDetail.get("updateTimestamp"), startDay, endDate);
				query.where(builder.and(predicateOne, predicateThree, predicateFour));
			} else if (vo.getLoginStatus().isEmpty() && vo.getPersonId() == null) {
				predicateThree= builder.between(rootPersonLoginDetail.get("updateTimestamp"), startDay, endDate);
				query.where(builder.and(predicateThree));
			}
		if (vo.getReverse().equals("ASC")) {
			if (vo.getSortBy().equals("personId")) {
				query.orderBy(builder.asc(rootPersonLoginDetail.get("personId")));
			} else if (vo.getSortBy().equals("person.fullName")) {
				query.orderBy(builder.asc(rootPersonLoginDetail.get("fullName")));
			} else if (vo.getSortBy().equals("dateAndTime") || vo.getSortBy().equals("")) {
				query.orderBy(builder.asc(rootPersonLoginDetail.get("updateTimestamp")));
			}
		} else {
			 if (vo.getSortBy().equals("personId")) {
				query.orderBy(builder.desc(rootPersonLoginDetail.get("personId")));
			} else if (vo.getSortBy().equals("person.fullName")) {
				query.orderBy(builder.desc(rootPersonLoginDetail.get("fullName")));
			} else if (vo.getSortBy().equals("dateAndTime") || vo.getSortBy().equals("")) {
				query.orderBy(builder.desc(rootPersonLoginDetail.get("updateTimestamp")));
			}
		}
		Integer count = (session.createQuery(query).getResultList()).size();
		vo.setUserActivityCount(count);
		if (Boolean.TRUE.equals(vo.getIsDownload())) {
			personLoginDetails = session.createQuery(query).getResultList();
		} else {
			personLoginDetails = session.createQuery(query).setFirstResult((vo.getCurrentPage() - 1) * vo.getItemsPerPage()).setMaxResults(vo.getItemsPerPage()).getResultList();
		}
		vo.setPersonLoginDetails(personLoginDetails);
		return vo;
	}
}
