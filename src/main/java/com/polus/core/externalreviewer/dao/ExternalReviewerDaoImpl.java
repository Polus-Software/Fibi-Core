package com.polus.core.externalreviewer.dao;

import java.math.BigInteger;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.internal.SessionImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polus.core.common.dao.CommonDao;
import com.polus.core.constants.Constants;
import com.polus.core.externalreviewer.pojo.CoiWithPerson;
import com.polus.core.externalreviewer.pojo.ExtReviewerAcademicArea;
import com.polus.core.externalreviewer.pojo.ExtReviewerAcademicRank;
import com.polus.core.externalreviewer.pojo.ExtReviewerAcademicSubArea;
import com.polus.core.externalreviewer.pojo.ExtReviewerAffiliation;
import com.polus.core.externalreviewer.pojo.ExtReviewerAttachment;
import com.polus.core.externalreviewer.pojo.ExtReviewerAttachmentFile;
import com.polus.core.externalreviewer.pojo.ExtReviewerCira;
import com.polus.core.externalreviewer.pojo.ExtReviewerOriginality;
import com.polus.core.externalreviewer.pojo.ExtReviewerThoroughness;
import com.polus.core.externalreviewer.pojo.ExternalReviewer;
import com.polus.core.externalreviewer.pojo.ExternalReviewerAttachmentType;
import com.polus.core.externalreviewer.pojo.ExternalReviewerExt;
import com.polus.core.externalreviewer.pojo.ExternalReviewerRights;
import com.polus.core.externalreviewer.pojo.ExternalReviewerSpecialization;
import com.polus.core.externalreviewer.pojo.ReviewerRights;
import com.polus.core.externalreviewer.pojo.SpecialismKeyword;
import com.polus.core.externalreviewer.vo.ExternalReviewerVo;
import com.polus.core.pojo.Country;
import com.polus.core.security.AuthenticatedUser;
import com.polus.core.vo.CommonVO;

import oracle.jdbc.OracleTypes;


@Transactional
@Service(value = "externalReviewerDao")
public class ExternalReviewerDaoImpl implements ExternalReviewerDao {

	protected static Logger logger = LogManager.getLogger(ExternalReviewerDaoImpl.class.getName());
	
	private static final String EXTERNAL_REVIEWER_ID = "externalReviewerId";
	private static final String UPDATE_TIMESTAMP = "updateTimeStamp";
	@Autowired
	private HibernateTemplate hibernateTemplate;
	
	@Autowired
	private CommonDao commonDao;
	

	@Value("${oracledb}")
	private String oracledb;
	
	@Override
	public ExternalReviewer saveOrUpdateExtReviewer(ExternalReviewer extReviewer) {
		hibernateTemplate.saveOrUpdate(extReviewer);
		return extReviewer;
	}
	
	@Override
	public ExternalReviewerExt saveOrUpdateExternalReviewerExt(ExternalReviewerExt externalReviewerExt) {
		hibernateTemplate.saveOrUpdate(externalReviewerExt);
		return externalReviewerExt;
	}
	
	@Override
	public ExternalReviewerSpecialization saveOrUpdateExtReviewerSpecialization(ExternalReviewerSpecialization extReviewerSpecialization) {
		hibernateTemplate.saveOrUpdate(extReviewerSpecialization);
		return extReviewerSpecialization;
	}
	
	@Override
	public ExternalReviewerRights saveOrUpdateExternalReviewerRights(ExternalReviewerRights externalReviewerRight) {
		hibernateTemplate.saveOrUpdate(externalReviewerRight);
		return externalReviewerRight;
	}
	
	@Override
	public ExtReviewerAttachment saveOrUpdateExtAttachment(ExtReviewerAttachment externalReviewerAttachment) {
		hibernateTemplate.saveOrUpdate(externalReviewerAttachment);
		return externalReviewerAttachment;
	}
	
	@Override
	public ExternalReviewer getExtReviewerDetailById(Integer extReviewerId) {
		return hibernateTemplate.get(ExternalReviewer.class, extReviewerId);
	}

	@Override
	public ExternalReviewerExt getExternalReviewerExts(Integer extReviewerId) {
		Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<ExternalReviewerExt> query = builder.createQuery(ExternalReviewerExt.class);
		Root<ExternalReviewerExt> rootExternalReviewerExt = query.from(ExternalReviewerExt.class);
		query.where(builder.equal(rootExternalReviewerExt.get(EXTERNAL_REVIEWER_ID), extReviewerId));
		query.orderBy(builder.desc(rootExternalReviewerExt.get(UPDATE_TIMESTAMP)));
		return session.createQuery(query).uniqueResult();
	}
	
	@Override
	public List<ExternalReviewerSpecialization> getExternalReviewerSpecializations(Integer extReviewerId) {
		Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<ExternalReviewerSpecialization> query = builder.createQuery(ExternalReviewerSpecialization.class);
		Root<ExternalReviewerSpecialization> rootExternalReviewerExt = query.from(ExternalReviewerSpecialization.class);
		query.where(builder.equal(rootExternalReviewerExt.get(EXTERNAL_REVIEWER_ID), extReviewerId));
		query.orderBy(builder.desc(rootExternalReviewerExt.get(UPDATE_TIMESTAMP)));
		return session.createQuery(query).getResultList();
	}

	@Override
	public ExternalReviewerRights getExternalReviewerRights(Integer extReviewerId) {
		Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<ExternalReviewerRights> query = builder.createQuery(ExternalReviewerRights.class);
		Root<ExternalReviewerRights> rootExternalReviewerExt = query.from(ExternalReviewerRights.class);
		query.where(builder.equal(rootExternalReviewerExt.get(EXTERNAL_REVIEWER_ID), extReviewerId));
		query.orderBy(builder.desc(rootExternalReviewerExt.get(UPDATE_TIMESTAMP)));
		return session.createQuery(query).uniqueResult();
	}
	
	@Override
	public String getexternalReviewerPassword(Integer externalReviewerId) {
	    Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<String> query = builder.createQuery(String.class);
		Root<ExternalReviewer> rootExternalReviewerExt = query.from(ExternalReviewer.class);
		query.where(builder.equal(rootExternalReviewerExt.get(EXTERNAL_REVIEWER_ID), externalReviewerId));
		query.select(rootExternalReviewerExt.get("password"));
		return session.createQuery(query).uniqueResult();
	}
	
	@Override
	public boolean checkUniqueUserName(String principalName) {
		Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<String> query = builder.createQuery(String.class);
		Root<ExternalReviewer> rootExternalReviewerExt = query.from(ExternalReviewer.class);
		query.where(builder.equal(rootExternalReviewerExt.get("principalName"), principalName));
		query.select(rootExternalReviewerExt.get("principalName"));
		if (session.createQuery(query).uniqueResult() != null) {
			return true;
		}
		return false;
	}
	
	@Override
	public List<ExternalReviewerAttachmentType> fetchExternalReviewerAttachmentTypes() {
		return hibernateTemplate.loadAll(ExternalReviewerAttachmentType.class);
	}

	@Override
	public List<ExtReviewerAcademicArea> fetchExtReviewerAcademicArea() {
		return hibernateTemplate.loadAll(ExtReviewerAcademicArea.class);
	}
	
	@Override
	public List<ExtReviewerAcademicRank> fetchExtReviewerAcademicRank() {
		return hibernateTemplate.loadAll(ExtReviewerAcademicRank.class);
	}
	
	@Override
	public List<ExtReviewerAffiliation> fetchExtReviewerAffilation() {
		return hibernateTemplate.loadAll(ExtReviewerAffiliation.class);
	}
	
	@Override
	public List<ExtReviewerCira> fetchExtReviewerCira() {
		return hibernateTemplate.loadAll(ExtReviewerCira.class);
	}
	
	@Override
	public List<ExtReviewerOriginality> fetchExtReviewerOriginality() {
		return hibernateTemplate.loadAll(ExtReviewerOriginality.class);
	}
	
	@Override
	public List<ExtReviewerThoroughness> fetchExtReviewerThoroughness() {
		return hibernateTemplate.loadAll(ExtReviewerThoroughness.class);
	}
	
	@Override
	public List<ReviewerRights> fetchReviewerRights() {
		return hibernateTemplate.loadAll(ReviewerRights.class);
	}
	
	@Override
	public ExtReviewerAttachmentFile saveFileData(ExtReviewerAttachmentFile fileData) {
		hibernateTemplate.save(fileData);
		return fileData;
	}
	
	@Override
	public ExtReviewerAttachment getExtReviewerAttachmentById(Integer externalReviewerAttachmentId) {
		return hibernateTemplate.get(ExtReviewerAttachment.class, externalReviewerAttachmentId);
	}

	@Override
	public ExtReviewerAttachmentFile getFileDataById(String fileDataId) {
		return hibernateTemplate.get(ExtReviewerAttachmentFile.class, fileDataId);
	}
	
	@Override
	public void deleteExtReviewerSpecialization(Integer extReviewerSpecializationId) {
		Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaDelete<ExternalReviewerSpecialization> delete = builder.createCriteriaDelete(ExternalReviewerSpecialization.class);
		Root<ExternalReviewerSpecialization> root = delete.from(ExternalReviewerSpecialization.class);
		delete.where(builder.equal(root.get("extReviewerSpecializationId"), extReviewerSpecializationId));
		session.createQuery(delete).executeUpdate();
	}
	
	@Override
	public void deleteFileData(ExtReviewerAttachmentFile fileData) {
		hibernateTemplate.delete(fileData);
	}
	
	@Override
	public void deleteExtReviewerAttachment(ExtReviewerAttachment extReviewerAttachment) {
		hibernateTemplate.delete(extReviewerAttachment);
	}
	
	@Override
	public List<ExtReviewerAttachment> fetchExtReviewerAttachment(Integer externalReviewerId) {
		Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<ExtReviewerAttachment> query = builder.createQuery(ExtReviewerAttachment.class);
		Root<ExtReviewerAttachment> rootExternalReviewerAttachment = query.from(ExtReviewerAttachment.class);
		query.where(builder.equal(rootExternalReviewerAttachment.get(EXTERNAL_REVIEWER_ID), externalReviewerId));
		return session.createQuery(query).getResultList();
	}
	
	@Override
	public void updateExtAttachment(String description, Integer externalReviewerAttachmentId) {
		Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaUpdate<ExtReviewerAttachment> criteriaUpdate = criteriaBuilder.createCriteriaUpdate(ExtReviewerAttachment.class);
		Root<ExtReviewerAttachment> root = criteriaUpdate.from(ExtReviewerAttachment.class);
		criteriaUpdate.set("description", description);
		criteriaUpdate.set("updateTimestamp", commonDao.getCurrentTimestamp());
		criteriaUpdate.set("updateUser", AuthenticatedUser.getLoginUserName());
		criteriaUpdate.where(criteriaBuilder.equal(root.get("externalReviewerAttachmentId"),externalReviewerAttachmentId));		 		
		session.createQuery(criteriaUpdate).executeUpdate();
	}
	
	@Override
	public ExternalReviewerVo getAllExtReviewer(ExternalReviewerVo vo) {
		Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
		SessionImpl sessionImpl = (SessionImpl) session;
		Connection connection = sessionImpl.connection();
		CallableStatement statement = null;
		ResultSet resultSet = null;
		List<ExternalReviewer> extReviewerList = new ArrayList<ExternalReviewer>();
		String property1 = vo.getProperty1();
		String property2 = vo.getProperty2();
		String property3 = vo.getProperty3();
		String property4 = vo.getProperty4();
		String property5 = vo.getProperty5();
		String property6 = vo.getProperty6();
		String property7 = vo.getProperty7();
		String property8 = vo.getProperty8();
		String property9 = vo.getProperty9();
		List<String> property10 = vo.getProperty10();
		List<String> property11 = vo.getProperty11();
		String property12 = vo.getProperty12();
		List<String> property13 = vo.getProperty13();
		List<String> property14 = vo.getProperty14();
		List<String> property15 = vo.getProperty15();
		Integer startIndex = 0;
		Integer itemsPerPage = 0;
		Map<String, String> sort = vo.getSort();
		try {
			if (oracledb.equalsIgnoreCase("N")) {
				statement = connection
						.prepareCall("{call GET_EXT_REVIEWER_DASHBOARD(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
				startIndex = vo.getCurrentPage() == null ? 0 : vo.getCurrentPage() - 1;
				itemsPerPage = vo.getPageNumber() == null ? 0 : vo.getPageNumber();
				statement.setString(1, property1);
				statement.setString(2, property2);
				statement.setString(3, property3);
				statement.setString(4, property4);
				statement.setString(5, property5);
				statement.setString(6, property6);
				statement.setString(7, property7);
				statement.setString(8, property8);
				statement.setString(9, property9);
				statement.setString(10, String.join(",", property10));
				statement.setString(11, String.join(",", property11));
				statement.setString(12, property12);
				statement.setString(13, String.join(",", property13));
				statement.setString(14, String.join(",", property14));
				statement.setString(15, String.join(",", property15));
				statement.setString(16, setSortOrder(sort));
				statement.setInt(17, startIndex);
				statement.setInt(18, itemsPerPage);
				statement.setBoolean(19, false);
				statement.execute();
				resultSet = statement.getResultSet();
			} else if (oracledb.equalsIgnoreCase("Y")) {
				statement = connection
						.prepareCall("{call GET_EXT_REVIEWER_DASHBOARD(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
				startIndex = vo.getCurrentPage() - 1;
				itemsPerPage = vo.getPageNumber();
				statement.registerOutParameter(1, OracleTypes.CURSOR);
				statement.setString(2, property1);
				statement.setString(3, property2);
				statement.setString(4, property3);
				statement.setString(5, property4);
				statement.setString(6, property5);
				statement.setString(7, property6);
				statement.setString(8, property7);
				statement.setString(9, property8);
				statement.setString(10, property9);
				statement.setString(11, String.join(",", property10));
				statement.setString(12, String.join(",", property11));
				statement.setString(13, property12);
				statement.setString(14, String.join(",", property13));
				statement.setString(15, String.join(",", property14));
				statement.setString(16, String.join(",", property15));
				statement.setString(17, setSortOrder(sort));
				statement.setInt(18, startIndex);
				statement.setInt(19, itemsPerPage);
				statement.setBoolean(20, false);
				statement.execute();
				resultSet = (ResultSet) statement.getObject(1);
			}
		} catch (SQLException e) {
			logger.info("exception in getAllExtReviewer : " + e);
			e.printStackTrace();
		}
		Integer count = getAllExtReviewerCount(vo);
		vo.setTotalExtReviewer(count);
		if (resultSet != null) {
			try {
				while (resultSet.next()) {
					ExternalReviewer externalReviewer = new ExternalReviewer();
					ExtReviewerAcademicRank extReviewerAcademicRank = new ExtReviewerAcademicRank();
					Country country = new Country();
					externalReviewer.setExternalReviewerId(resultSet.getInt("EXTERNAL_REVIEWER_ID"));
					externalReviewer.setPassportName(resultSet.getString("FULL_NAME"));
					extReviewerAcademicRank.setDescription(resultSet.getString("ACADEMIC_RANK_DESCRIPTION"));
					externalReviewer.setAcademicRank(extReviewerAcademicRank);
					externalReviewer.setStatus(resultSet.getString("STATUS"));
					externalReviewer.setAgreementEndDate(resultSet.getTimestamp("AGREEMENT_END_DATE"));
					externalReviewer.setSpecialismKeywords(resultSet.getString("SPECIALISM_KEYWORD"));
					externalReviewer.setHindex(resultSet.getString("HI_INDEX"));
					country.setCountryName(resultSet.getString("COUNTRY_NAME"));
					externalReviewer.setCountryDetails(country);
					extReviewerList.add(externalReviewer);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		vo.setExtReviewers(extReviewerList);
		return vo;
	}
	
	private Integer getAllExtReviewerCount(ExternalReviewerVo vo) {
		Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
		SessionImpl sessionImpl = (SessionImpl) session;
		Connection connection = sessionImpl.connection();
		CallableStatement statement = null;
		ResultSet resultSet = null;
		String property1 = vo.getProperty1();
		String property2 = vo.getProperty2();
		String property3 = vo.getProperty3();
		String property4 = vo.getProperty4();
		String property5 = vo.getProperty5();
		String property6 = vo.getProperty6();
		String property7 = vo.getProperty7();
		String property8 = vo.getProperty8();
		String property9 = vo.getProperty9();
		List<String> property10 = vo.getProperty10();
		List<String> property11 = vo.getProperty11();
		String property12 = vo.getProperty12();
		List<String> property13 = vo.getProperty13();
		List<String> property14 = vo.getProperty14();
		List<String> property15 = vo.getProperty15();
		Integer startIndex = 0;
		Integer itemsPerPage = 0;
		try {
			if (oracledb.equalsIgnoreCase("N")) {
				statement = connection
						.prepareCall("{call GET_EXT_REVIEWER_DASHBOARD_COUNT(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
				startIndex = vo.getCurrentPage() == null ? 0 : vo.getCurrentPage() - 1;
				itemsPerPage = vo.getPageNumber() == null ? 0 : vo.getPageNumber();
				statement.setString(1, property1);
				statement.setString(2, property2);
				statement.setString(3, property3);
				statement.setString(4, property4);
				statement.setString(5, property5);
				statement.setString(6, property6);
				statement.setString(7, property7);
				statement.setString(8, property8);
				statement.setString(9, property9);
				statement.setString(10, String.join(",", property10));
				statement.setString(11, String.join(",", property11));
				statement.setString(12, property12);
				statement.setString(13, String.join(",", property13));
				statement.setString(14, String.join(",", property14));
				statement.setString(15, String.join(",", property15));
				statement.setInt(16, startIndex);
				statement.setInt(17, itemsPerPage);
				statement.setBoolean(18, false);
				statement.execute();
				resultSet = statement.getResultSet();
			} else if (oracledb.equalsIgnoreCase("Y")) {
				statement = connection
						.prepareCall("{call GET_EXT_REVIEWER_DASHBOARD_COUNT(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
				startIndex = vo.getCurrentPage() - 1;
				itemsPerPage = vo.getPageNumber();
				statement.registerOutParameter(1, OracleTypes.CURSOR);
				statement.setString(2, property1);
				statement.setString(3, property2);
				statement.setString(4, property3);
				statement.setString(5, property4);
				statement.setString(6, property5);
				statement.setString(7, property6);
				statement.setString(8, property7);
				statement.setString(9, property8);
				statement.setString(10, property9);
				statement.setString(11, String.join(",", property10));
				statement.setString(12, String.join(",", property11));
				statement.setString(13, property12);
				statement.setString(14, String.join(",", property13));
				statement.setString(15, String.join(",", property14));
				statement.setString(16, String.join(",", property15));
				statement.setInt(17, startIndex);
				statement.setInt(18, itemsPerPage);
				statement.setBoolean(19, false);
				statement.execute();
				resultSet = (ResultSet) statement.getObject(1);
			}
		} catch (SQLException e) {
			logger.info("exception in getAllExtReviewerCount : " + e);
			e.printStackTrace();
		}
		Integer count = 0;
		if (resultSet != null) {
			try {
				while(resultSet.next()){
					count = resultSet.getInt(1);
				}
			} catch (NumberFormatException | SQLException e) {
				e.printStackTrace();
			}
		}
		return count;
	}
	
	private String setSortOrder(Map<String, String> sort) {
		String sortOrder = null;
		if (!sort.isEmpty()) {
			for (Map.Entry<String, String> mapElement : sort.entrySet()) {
				if (mapElement.getKey().equals("externalReviewerId")) {
					sortOrder = (sortOrder == null ? "T.EXTERNAL_REVIEWER_ID " + mapElement.getValue() : sortOrder + ", T.EXTERNAL_REVIEWER_ID " + mapElement.getValue());
				}
				if (mapElement.getKey().equals("hindex")) {
					sortOrder = (sortOrder == null ? "T.HI_INDEX " + mapElement.getValue() : sortOrder + ", T.HI_INDEX " + mapElement.getValue());
				}
				if (mapElement.getKey().equals("passportName")) {
					sortOrder = (sortOrder == null ? "T.FULL_NAME " + mapElement.getValue() : sortOrder + ", T.FULL_NAME " + mapElement.getValue());
				}
				if (mapElement.getKey().equals("primaryEmail")) {
					sortOrder = (sortOrder == null ? "T.EMAIL_ADDRESS_PRIMARY " + mapElement.getValue() : sortOrder + ", T.EMAIL_ADDRESS_PRIMARY " + mapElement.getValue());
				}
				if (mapElement.getKey().equals("agreementEndDate")) {
					sortOrder = (sortOrder == null ? "T.AGREEMENT_END_DATE " + mapElement.getValue() : sortOrder + ", T.AGREEMENT_END_DATE " + mapElement.getValue());
				}
				if (mapElement.getKey().equals("academicRank.description")) {
					sortOrder = (sortOrder == null ? "T.ACADEMIC_RANK_DESCRIPTION " + mapElement.getValue() : sortOrder + ", T.ACADEMIC_RANK_DESCRIPTION " + mapElement.getValue());
				}
				if (mapElement.getKey().equals("status")) {
					sortOrder = (sortOrder == null ? "T.STATUS " + mapElement.getValue() : sortOrder + ", T.STATUS " + mapElement.getValue());
				}
				if (mapElement.getKey().equals("countryDetails.countryName")) {
					sortOrder = (sortOrder == null ? "T.COUNTRY_NAME " + mapElement.getValue() : sortOrder + ", T.COUNTRY_NAME " + mapElement.getValue());
				}
			}
		}
		return sortOrder;
	}
	
	public <R> Expression<R> generateExpression(Root<R> root, String key) {
		if (!key.contains(".")) {
			return root.get(key);
		}
		Integer index = key.indexOf('.');
		return root.get(key.substring(0, index)).get(key.substring(index + 1, key.length()));
	}

	@Override
	public List<SpecialismKeyword> findSpecialismKeywords(CommonVO vo) {
		Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<SpecialismKeyword> query = builder.createQuery(SpecialismKeyword.class);
		Root<SpecialismKeyword> rootScienceKeyword = query.from(SpecialismKeyword.class);
		Predicate isActive = builder.equal(rootScienceKeyword.get("isActive"), Boolean.TRUE);
		Predicate description = builder.like(builder.lower(rootScienceKeyword.get("description")), "%" + vo.getSearchString().toLowerCase() + "%");
		query.where(builder.and(isActive, description));
		query.orderBy(builder.asc(rootScienceKeyword.get(Constants.DESCRIPTION)));
		if (vo.getFetchLimit() != null) {
			return session.createQuery(query).setMaxResults(vo.getFetchLimit()).getResultList();
		} else {
			return session.createQuery(query).getResultList();
		}
	}
	
	@Override
	public void updateExternalReviewerPassword(String password,Integer externalReviewerId) {
		Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaUpdate<ExternalReviewer> criteriaUpdate = criteriaBuilder.createCriteriaUpdate(ExternalReviewer.class);
		Root<ExternalReviewer> root = criteriaUpdate.from(ExternalReviewer.class);
		criteriaUpdate.set("password", password);
		criteriaUpdate.set("updateTimeStamp", commonDao.getCurrentTimestamp());
		criteriaUpdate.set("updateUser", AuthenticatedUser.getLoginUserName());
		criteriaUpdate.where(criteriaBuilder.equal(root.get(EXTERNAL_REVIEWER_ID),externalReviewerId));		 		
		session.createQuery(criteriaUpdate).executeUpdate();
	}
	
	@Override
	public String getEmailAddress(Integer externalReviewerId) {
		Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<String> outerQuery = builder.createQuery(String.class);
		Root<ExternalReviewer> rootModuleVariableSection = outerQuery.from(ExternalReviewer.class);
		outerQuery.select(rootModuleVariableSection.get("primaryEmail"));
		outerQuery.where(builder.equal(rootModuleVariableSection.get(EXTERNAL_REVIEWER_ID), externalReviewerId));
		return session.createQuery(outerQuery).uniqueResult();
	}


	@Override
	public List<ExtReviewerAcademicSubArea> fetchExtReviewerAcademicSubArea() {
		return hibernateTemplate.loadAll(ExtReviewerAcademicSubArea.class);
	}

	@Override
	public Boolean isKeywordExist(String specialismKeyword) {
		Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
		String count = "SELECT count(*) FROM  SpecialismKeyword WHERE description=:specialismKeyword";
		Query query = session.createQuery(count);
		query.setParameter("specialismKeyword", specialismKeyword);
		return Integer.parseInt(query.getSingleResult().toString()) > 0;
	}

	@Override
	public SpecialismKeyword saveOrUpdateSpecialismKeyword(SpecialismKeyword specialismKeyword) {
		try {
			hibernateTemplate.saveOrUpdate(specialismKeyword);
		} catch (Exception e) {
			logger.error("exception in saveOrUpdateSpecialismKeyword: {} ", e.getMessage());
		}
		return specialismKeyword;
	}


	@Override
	public String getMaxSpecialismKeyword() {
		Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
		String queryForMax = "SELECT MAX(CAST(SPECIALISM_KEYWORD_CODE AS UNSIGNED INTEGER))+1 as VALUE FROM EXT_SPECIALISM_KEYWORD";
		Query query = session.createSQLQuery(queryForMax);
		BigInteger maxValue = (BigInteger) query.getResultList().get(0);
		return maxValue + "";
	}
	
	@Override
	public List<ExtReviewerAffiliation> findAffilationInstitution(CommonVO vo) {
		Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<ExtReviewerAffiliation> query = builder.createQuery(ExtReviewerAffiliation.class);
		Root<ExtReviewerAffiliation> rootScienceKeyword = query.from(ExtReviewerAffiliation.class);
		query.where(builder.like(builder.lower(rootScienceKeyword.get("description")), "%" + vo.getSearchString().toLowerCase() + "%"));
		query.orderBy(builder.asc(rootScienceKeyword.get(Constants.DESCRIPTION)));
		if (vo.getFetchLimit() != null) {
			return session.createQuery(query).setMaxResults(vo.getFetchLimit()).getResultList();
		} else {
			return session.createQuery(query).getResultList();
		}
	}

	@Override
	public ExtReviewerAffiliation saveOrUpdateAffilationInstitution(ExtReviewerAffiliation extReviewerAffilation) {
		try {
			hibernateTemplate.saveOrUpdate(extReviewerAffilation);
		} catch (Exception e) {
			logger.error("exception in saveOrUpdateSpecialismKeyword: {} ", e.getMessage());
		}
		return extReviewerAffilation;
	}


	@Override
	public String getMaxAffilationInstitution() {
		Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
		String queryForMax = "SELECT MAX(CAST(AFFILATION_INSTITUITION_CODE AS UNSIGNED INTEGER))+1 as VALUE FROM EXT_REVIEWER_AFFILIATION";
		Query query = session.createSQLQuery(queryForMax);
		BigInteger maxValue = (BigInteger) query.getResultList().get(0);
		return maxValue.toString();
	}

	@Override
	public CoiWithPerson saveOrUpdateCoiWithPerson(CoiWithPerson coiWithPerson) {
		hibernateTemplate.saveOrUpdate(coiWithPerson);
		return coiWithPerson;
	}
	
	@Override
	public void deleteCoiWithPerson(Integer coiWithPersonId) {
		Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaDelete<CoiWithPerson> delete = builder.createCriteriaDelete(CoiWithPerson.class);
		Root<CoiWithPerson> root = delete.from(CoiWithPerson.class);
		delete.where(builder.equal(root.get("coiWithPersonId"), coiWithPersonId));
		session.createQuery(delete).executeUpdate();
	}
	
	@Override
	public List<CoiWithPerson> getCoiWithPerson(Integer externalReviewerId) {
		Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<CoiWithPerson> query = builder.createQuery(CoiWithPerson.class);
		Root<CoiWithPerson> rootExternalReviewerExt = query.from(CoiWithPerson.class);
		query.where(builder.equal(rootExternalReviewerExt.get(EXTERNAL_REVIEWER_ID), externalReviewerId));
		query.orderBy(builder.desc(rootExternalReviewerExt.get(UPDATE_TIMESTAMP)));
		return session.createQuery(query).getResultList();
	}
	
	@Override
	public Boolean isAffilationInstitutionExist(String affilationInstitution) {
		Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
		String count = "SELECT count(*) FROM  ExtReviewerAffiliation WHERE description=: affilationInstitution";
		Query query = session.createQuery(count);
		query.setParameter("affilationInstitution", affilationInstitution);
		return Integer.parseInt(query.getSingleResult().toString()) > 0;
	}

	@Override
	public void externalReviewerExpire() {
		Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaUpdate<ExternalReviewer> updateQuery = builder.createCriteriaUpdate(ExternalReviewer.class);
		Root<ExternalReviewer> root = updateQuery.from(ExternalReviewer.class);
		Predicate isActive = builder.equal(root.get("status"), "A");
		Predicate expireDate = builder.lessThan(root.get("agreementEndDate"), commonDao.getCurrentTimestamp());
		Predicate expireDateNotNull = builder.isNotNull(root.get("agreementEndDate"));
		updateQuery.set(root.get("status"), "I");
		updateQuery.where(builder.and(isActive,expireDate,expireDateNotNull));
		session.createQuery(updateQuery).executeUpdate();
	}

}
