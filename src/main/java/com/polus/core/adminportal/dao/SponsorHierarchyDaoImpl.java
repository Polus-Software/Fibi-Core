package com.polus.core.adminportal.dao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polus.core.adminportal.pojo.SponsorHierarchy;
import com.polus.core.pojo.Sponsor;

@Transactional
@Service(value = "sponsorHierarchyDao")
public class SponsorHierarchyDaoImpl implements SponsorHierarchyDao{

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private HibernateTemplate hibernateTemplate;

    @Override
    public void saveSponsorHierarchy(SponsorHierarchy sponHiearachy) {
        hibernateTemplate.saveOrUpdate(sponHiearachy);
    }

    @Override
    public void deleteSponsorHierarchyById(Integer sponsorGroupId) {
        hibernateTemplate.delete(hibernateTemplate.load(SponsorHierarchy.class, sponsorGroupId));
    }

    @Override
    public List<SponsorHierarchy> getAllSponsorHierarchyFromRoot() {
        Query query = entityManager.createQuery("SELECT hierarchy FROM SponsorHierarchy hierarchy WHERE " +
                "hierarchy.sponsorRootGroupId IS NULL ORDER BY hierarchy.orderNumber DESC");
        return query.getResultList();
    }

    @Override
    public SponsorHierarchy findSponsorHierarchyBySponsorGroupIdOrderByOrderNumberDesc(Integer sponsorGroupId) {
        Query query = entityManager.createQuery("SELECT hierarchy FROM SponsorHierarchy hierarchy WHERE " +
                "hierarchy.sponsorGroupId = :sponsorGroupId ORDER BY hierarchy.orderNumber DESC");
        query.setParameter("sponsorGroupId", sponsorGroupId);
        return (SponsorHierarchy)query.getSingleResult();
    }

    @Override
    public void updateSponsorHierarchy(String sponsorGroupName, Integer orderNumber, Timestamp updateTimestamp, String updateUser, Integer sponsorGroupId) {
        Query query = entityManager.createQuery("UPDATE SponsorHierarchy hierarchy SET hierarchy.sponsorGroupName = :sponsorGroupName, hierarchy.orderNumber = :orderNumber, " +
        "hierarchy.updateTimestamp = :updateTimestamp, hierarchy.updateUser = :updateUser WHERE hierarchy.sponsorGroupId = :sponsorGroupId");
        query.setParameter("sponsorGroupName", sponsorGroupName);
        query.setParameter("orderNumber", orderNumber);
        query.setParameter("updateTimestamp", updateTimestamp);
        query.setParameter("updateUser", updateUser);
        query.setParameter("sponsorGroupId", sponsorGroupId);
        query.executeUpdate();
    }

    @Override
	public List<Object> findAllSponsors(Map<String, String> voObj) {
		final String likeCriteria = "%" + voObj.get("searchString") + "%";
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Object[]> criteriaQuery = builder.createQuery(Object[].class);
		Root<Sponsor> sponsor = criteriaQuery.from(Sponsor.class);
		criteriaQuery.multiselect(sponsor.get("sponsorCode"), sponsor.get("sponsorName"),
				sponsor.get("sponsorType").get("description"), sponsor.get("acronym"));
		Predicate likeCondition = builder.or(builder.like(sponsor.get("sponsorCode"), likeCriteria),
				builder.like(sponsor.get("sponsorName"), likeCriteria),
				builder.like(sponsor.get("acronym"), likeCriteria));
		criteriaQuery.where(likeCondition);
		TypedQuery<Object[]> query = entityManager.createQuery(criteriaQuery);
		if (voObj.get("fetchLimit") != null) {
			query.setMaxResults(Integer.parseInt(voObj.get("fetchLimit")));
		}
		List<Object[]> resultList = query.getResultList();
		List<Object> result = new ArrayList<>(resultList.size());
		for (Object[] obj : resultList) {
			result.add(obj);
		}
		return result;
	}

    @Override
    public List<Object> findAllDistinctSponsorGroupNameBySearchWord(String searchWord) {
        Query query = entityManager.createQuery("SELECT DISTINCT hierarchy.sponsorGroupName, hierarchy.sponsorGroupId FROM SponsorHierarchy hierarchy WHERE " +
                "hierarchy.sponsorCode IS NULL AND hierarchy.sponsorGroupName LIKE :searchWord");
        query.setParameter("searchWord", searchWord);
        return query.getResultList();
    }

    @Override
    public List<Object> findAllDistinctSponsorGroupName() {
        Query query = entityManager.createQuery("SELECT DISTINCT hierarchy.sponsorGroupName, hierarchy.sponsorGroupId " +
                "FROM SponsorHierarchy hierarchy WHERE hierarchy.sponsorCode IS NULL");
        return query.getResultList();
    }
}
