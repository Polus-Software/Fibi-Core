package com.polus.core.common.dao;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.procedure.ProcedureCall;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.polus.core.applicationexception.dto.ApplicationException;
import com.polus.core.constants.Constants;
import com.polus.core.dbengine.DBEngine;
import com.polus.core.dbengine.Parameter;
import com.polus.core.general.pojo.DynamicSubSectionConfig;
import com.polus.core.general.pojo.LockConfiguration;
import com.polus.core.general.pojo.WebSocketConfiguration;
import com.polus.core.person.pojo.Person;
import com.polus.core.person.service.PersonService;
import com.polus.core.pojo.ActivityType;
import com.polus.core.pojo.ArgValueLookup;
import com.polus.core.pojo.CommentType;
import com.polus.core.pojo.Country;
import com.polus.core.pojo.Currency;
import com.polus.core.pojo.DocumentStatus;
import com.polus.core.pojo.FileData;
import com.polus.core.pojo.FileType;
import com.polus.core.pojo.LetterTemplateType;
import com.polus.core.pojo.LookupWindow;
import com.polus.core.pojo.Module;
import com.polus.core.pojo.NarrativeStatus;
import com.polus.core.pojo.Organization;
import com.polus.core.pojo.ParameterBo;
import com.polus.core.pojo.ResearchType;
import com.polus.core.pojo.ResearchTypeArea;
import com.polus.core.pojo.ResearchTypeSubArea;
import com.polus.core.pojo.Rolodex;
import com.polus.core.pojo.SpecialReviewApprovalType;
import com.polus.core.pojo.Sponsor;
import com.polus.core.pojo.Unit;
import com.polus.core.roles.pojo.AdminGroup;
import com.polus.core.util.GsonSingleton;
import com.polus.core.util.Truth;
import com.polus.core.vo.CommonVO;
import com.polus.core.vo.LookUp;

@Transactional
@Service(value = "commonDao")
public class CommonDaoImpl implements CommonDao {

	protected static Logger logger = LogManager.getLogger(CommonDaoImpl.class.getName());

	@Autowired
	private HibernateTemplate hibernateTemplate;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private DBEngine dbEngine;

	@Autowired
	private PersonService personService;

	@Value("${oracledb}")
	private String oracledb;

	@Value("${system.timezone}")
	private String timezone;

	@Override
	public Long getNextSequenceNumber(String sequenceName) {
		Query update = hibernateTemplate.getSessionFactory().getCurrentSession().createSQLQuery("update SEQ_IP_ID_GNTR set NEXT_VAL = NEXT_VAL+1");
		update.executeUpdate();
		Query select = hibernateTemplate.getSessionFactory().getCurrentSession().createSQLQuery("select MAX(NEXT_VAL) from SEQ_IP_ID_GNTR");
		return ((BigInteger) select.getSingleResult()).longValue();
	}

	@Override
	public boolean getParameterValueAsBoolean(String parameterName) {
		ParameterBo parameterBo = getParameterBO(parameterName);
		String value = parameterBo != null ? parameterBo.getValue() : null;
		if (value == null) {
			logger.info("Parameter is value is not set or empty. Parameter Name is : {}", parameterName);
			value = "false";
		}
		return Truth.strToBooleanIgnoreCase(value);
	}

	@Override
	public boolean isDynSubSectionEnabled(String subSectionCode) {
		DynamicSubSectionConfig dynamicSubSectionConfig = getDynamicSubSectionConfig(subSectionCode);
		Boolean value = dynamicSubSectionConfig != null ? dynamicSubSectionConfig.getIsActive() : null;
		if (value == null) {
			logger.info("Dynamic sub section is empty. sub section code is : {}", subSectionCode);
			value = Boolean.FALSE;
		}
		return value;
	}

	private DynamicSubSectionConfig getDynamicSubSectionConfig(String subSectionCode) {
		Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<DynamicSubSectionConfig> query = builder.createQuery(DynamicSubSectionConfig.class);
		Root<DynamicSubSectionConfig> dynamicSubSectionConfig = query.from(DynamicSubSectionConfig.class);
		Predicate predicateSubSectionCode = builder.equal(dynamicSubSectionConfig.get("subSectionCode"),
				subSectionCode);
		query.where(builder.and(predicateSubSectionCode));
		return session.createQuery(query).uniqueResult();
	}

	@Override
	public Integer getParameter(String parameterName) {
		ParameterBo parameterBo = getParameterBO(parameterName);
		return Integer.parseInt(parameterBo.getValue());
	}

	@Override
	public String getParameterValueAsString(String parameterName) {
		ParameterBo parameterBo = getParameterBO(parameterName);
		return parameterBo != null ? parameterBo.getValue() : null;
	}

	private ParameterBo getParameterBO(String parameterName) {
		Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<ParameterBo> query = builder.createQuery(ParameterBo.class);
		Root<ParameterBo> parameterBo = query.from(ParameterBo.class);
		Predicate predicateParameterName = builder.equal(parameterBo.get("parameterName"), parameterName);
		query.where(builder.and(predicateParameterName));
		return session.createQuery(query).uniqueResult();
	}

	@Override
	public DocumentStatus getDocumentStatusById(Integer documentStatusCode) {
		return hibernateTemplate.get(DocumentStatus.class, documentStatusCode);
	}

	@Override
	public FileData saveFileData(FileData fileData) {
		try {
			hibernateTemplate.save(fileData);
		} catch (Exception e) {
			logger.error("Error occured in saveFileData : {}", e.getMessage());
			throw new ApplicationException("Error in saveFileData", e, Constants.JAVA_ERROR);
		}
		return fileData;
	}

	@Override
	public FileData getFileDataById(String fileDataId) {
		return hibernateTemplate.get(FileData.class, fileDataId);
	}

	@Override
	public void deleteFileData(FileData fileData) {
		try {
			hibernateTemplate.delete(fileData);
		} catch (Exception e) {
			logger.error("Error occured in deleteFileData : {}", e.getMessage());
			throw new ApplicationException("Error in deleteFileData", e, Constants.JAVA_ERROR);
		}
	}

	@Override
	public Date getCurrentDate() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		return c.getTime();
	}

	@Override
	public Long getNumberOfDays(Date startDate, Date endDate) {
		return ChronoUnit.DAYS.between(startDate.toInstant(), endDate.toInstant()) + 1;
	}

	@Override
	public String getUnitName(String unitNumber) {
		String unitName = "";
		try {
			Query query = entityManager.createNativeQuery("SELECT UNIT_NAME FROM UNIT where UNIT_NUMBER = ?1");
			query.setParameter(1, unitNumber);
			unitName = (String) query.getSingleResult();
		} catch (Exception e) {
			logger.error("Error occured in getUnitName : {}", e.getMessage());
		}
		return unitName;
	}

	@Override
	public Timestamp getCurrentTimestamp() {
		return new Timestamp(this.getCurrentDate().getTime());
	}

	@Override
	public String convertObjectToJSON(Object object) {
		String response = "";
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
			response = mapper.writeValueAsString(object);
		} catch (Exception e) {
			logger.error("Error occured in convertObjectToJSON : {}", e.getMessage());
		}
		return response;
	}

	@Override
	public Unit getLeadUnitByUnitNumber(String leadUnitNumber) {
		return hibernateTemplate.get(Unit.class, leadUnitNumber);
	}

	@Override
	public String getSponsorName(String sponsorCode) {
		String sponsorName = "";
		try {
			Query query = entityManager.createNativeQuery("SELECT SPONSOR_NAME FROM SPONSOR where SPONSOR_CODE = ?1");
			query.setParameter(1, sponsorCode);
			sponsorName = (String) query.getSingleResult();
		} catch (Exception e) {
			logger.error("Error occured in getSponsorName : {}", e.getMessage());
		}
		return sponsorName;
	}

	@Override
	public Unit getUnitByUnitNumber(String unitNumber) {
		return hibernateTemplate.get(Unit.class, unitNumber);
	}

	@Override
	public String getUnitAcronym(String unitNumber) {
		String acronym = "";
		Unit unit = hibernateTemplate.get(Unit.class, unitNumber);
		if (unit != null) {
			acronym = unit.getAcronym();
		}
		return acronym;
	}

	@Override
	public Date adjustTimezone(Date date) {
		try {
			SimpleDateFormat newFormat = new SimpleDateFormat("dd-MM-yyyy");
			DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:SS z");
//			df.setTimeZone(TimeZone.getTimeZone(timezone));
			return newFormat.parse(df.format(date));
		} catch (Exception e) {
			logger.error("Error in adusting adjustTimezone, input date is %s. Error : %s", date, e.getMessage());
		}
		return date;
	}

	@Override
	public String convertToJSON(Object object) {
		String response = "";
		try {
			Gson gson = GsonSingleton.getInstance();
			response = gson.toJson(object);
		} catch (Exception e) {
			logger.error("Error occured in convertToJSON : {}", e.getMessage());
		}
		return response;
	}

	@Override
	public List<ArgValueLookup> getArgValueLookupData(String argumentName) {
		try {
			Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<ArgValueLookup> query = builder.createQuery(ArgValueLookup.class);
			Root<ArgValueLookup> argValue = query.from(ArgValueLookup.class);
			Predicate predicate1 = builder.equal(argValue.get("argumentName"), argumentName);
			query.where(builder.and(predicate1));
			return session.createQuery(query).list();
		} catch (Exception e) {
			logger.error("Error occured in getArgValueLookupData : {}", e.getMessage());
			return new ArrayList<>();
		}
	}

	@Override
	public List<ActivityType> fetchAllActivityTypes() {
		Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<ActivityType> query = builder.createQuery(ActivityType.class);
		Root<ActivityType> rootUnit = query.from(ActivityType.class);
		query.orderBy(builder.asc(rootUnit.get("description")));
		return session.createQuery(query).getResultList();
	}

	@Override
	public List<Unit> fetchAllUnits() {
		return hibernateTemplate.loadAll(Unit.class);
	}

	@Override
	public List<NarrativeStatus> fetchAllNarrativeStatus() {
		Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<NarrativeStatus> query = builder.createQuery(NarrativeStatus.class);
		Root<NarrativeStatus> rootUnit = query.from(NarrativeStatus.class);
		query.orderBy(builder.asc(rootUnit.get("description")));
		return session.createQuery(query).getResultList();
	}

	@Override
	public Sponsor getSponsorById(String sponsorCode) {
		return hibernateTemplate.get(Sponsor.class, sponsorCode);
	}

	@Override
	public List<SpecialReviewApprovalType> fetchAllApprovalStatusTypes() {
		Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<SpecialReviewApprovalType> query = builder.createQuery(SpecialReviewApprovalType.class);
		Root<SpecialReviewApprovalType> rootSpecialReviewApprovalType = query.from(SpecialReviewApprovalType.class);
		query.where(builder.equal(rootSpecialReviewApprovalType.get("isActive"), true));
		query.orderBy(builder.asc(rootSpecialReviewApprovalType.get(Constants.DESCRIPTION)));
		return session.createQuery(query).getResultList();
	}

	@Override
	public List<Organization> fetchOrganizationList(CommonVO vo) {
		Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Organization> query = builder.createQuery(Organization.class);
		Root<Organization> organization = query.from(Organization.class);
		Predicate organizationName = builder.like(organization.get("organizationName"), "%" + vo.getSearchString() + "%");
		Predicate isActive = builder.equal(organization.get("isActive"), true);
		query.where(organizationName, isActive);
		query.orderBy(builder.asc(organization.get("organizationName")));
		if (vo.getFetchLimit() != null) {
			return session.createQuery(query).setMaxResults(vo.getFetchLimit()).getResultList();
		} else {
			return session.createQuery(query).getResultList();
		}
	}

	@Override
	public List<Country> fetchCountryList(CommonVO vo) {
		Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Country> query = builder.createQuery(Country.class);
		Root<Country> country = query.from(Country.class);
		query.where(builder.like(country.get("countryName"), vo.getSearchString() + "%"));
		query.orderBy(builder.asc(country.get("countryName")));
		if (vo.getFetchLimit() != null) {
			return session.createQuery(query).setMaxResults(vo.getFetchLimit()).getResultList();
		} else {
			return session.createQuery(query).getResultList();
		}
	}

	@Override
	public ActivityType fetchActivityTypeByActivityTypeCode(String activityTypeCode) {
		return hibernateTemplate.get(ActivityType.class, activityTypeCode);
	}

	@Override
	public SpecialReviewApprovalType fetchSpecialReviewApprovalTypeByTypeCode(String specialReviewApprovalTypeCode) {
		return hibernateTemplate.get(SpecialReviewApprovalType.class, specialReviewApprovalTypeCode);
	}

	@Override
	public NarrativeStatus getNarrativeStatusByCode(String code) {
		return hibernateTemplate.get(NarrativeStatus.class, code);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<LookUp> getLookUpDatas(String lookUpTableName, String lookUpTableColumnName) {
		List<LookUp> lookups = new ArrayList<>();
		List<LookUp> lookUpView = new ArrayList<>();
		try {
			Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
			String sql = null;
			if (lookUpTableName.equals("ARG_VALUE_LOOKUP")) {
				sql = "SELECT ARGUMENT_VALUE,DESCRIPTION FROM ARG_VALUE_LOOKUP WHERE ARGUMENT_NAME = '"
						+ lookUpTableColumnName + "'";
			} else if (lookUpTableName.equals("TEMP")) {
				Set<Person> personDetails = personService.getPersonBasedOnRoleAndRight();
				for (Person person : personDetails) {
					LookUp lookUp = new LookUp();
					lookUp.setCode(person.getPersonId().toString());
					lookUp.setDescription(person.getFullName().toString());
					lookUpView.add(lookUp);
				}
				return lookUpView;
			} else if (lookUpTableName.equals("AGREEMENT_ADMIN_GROUPS")) {
				sql = "SELECT  " + lookUpTableColumnName + "," + "ADMIN_GROUP_NAME" + " FROM  " + lookUpTableName;
			} else {
				sql = "SELECT  " + lookUpTableColumnName + "," + "DESCRIPTION" + " FROM  " + lookUpTableName
						+ " ORDER BY DESCRIPTION";
			}
			Query query = session.createSQLQuery(sql);
			List<Object[]> results = query.getResultList();
			for (Object[] result : results) {
				LookUp lookUp = new LookUp();
				lookUp.setCode(result[0].toString());
				lookUp.setDescription(result[1].toString());
				lookups.add(lookUp);
			}
		} catch (Exception e) {
			logger.error("Error occured in getLookUpDatas : {}", e.getMessage());
		}
		return lookups;
	}

	@Override
	public String getDateFormat(Date date, String dateFormat) {
		String changeDate = "";
		try {
			DateFormat df = new SimpleDateFormat(dateFormat);
			changeDate = df.format(date);
		} catch (Exception e) {
			logger.error("Error occured in getDateFormat : {}", e.getMessage());
		}
		return changeDate;
	}

	@Override
	public List<Currency> fetchCurrencyDetails() {
		Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Currency> query = builder.createQuery(Currency.class);
		Root<Currency> root = query.from(Currency.class);
		query.select(builder.construct(Currency.class, root.get("currencyCode"), root.get("currency"),
				root.get("currencySymbol")));
		return session.createQuery(query).list();
	}

	@Override
	public List<Module> getModules() {
		Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Module> query = builder.createQuery(Module.class);
		Root<Module> rootModule = query.from(Module.class);
		query.orderBy(builder.asc(rootModule.get(Constants.DESCRIPTION)));
		return session.createQuery(query).getResultList();
	}

	@Override
	public Currency getCurrencyByCurrencyCode(String currencyCode) {
		return hibernateTemplate.get(Currency.class, currencyCode);
	}

	@Override
	public List<Module> getFilteredModules(List<Integer> moduleCodes) {
		Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Module> query = builder.createQuery(Module.class);
		Root<Module> rootServiceRequestModule = query.from(Module.class);
		query.orderBy(builder.asc(rootServiceRequestModule.get(Constants.DESCRIPTION)));
		query.where(rootServiceRequestModule.get("moduleCode").in(moduleCodes));
		return session.createQuery(query).getResultList();
	}

	@Override
	public Integer fetchMaxOrganizationId() {
		Integer max = 0;
		try {
			String query = "SELECT max(CAST(ORGANIZATION_ID AS SIGNED)) as max_data FROM ORGANIZATION";
			ArrayList<HashMap<String, Object>> dataList = dbEngine.executeQuerySQL(new ArrayList<Parameter>(), query);
			if (dataList.get(0).get("MAX_DATA") != null) {
				max = Integer.parseInt(dataList.get(0).get("MAX_DATA").toString());
			}
			return ++max;
		} catch (Exception e) {
			logger.error("Error occured in fetchMaxOrganizationId : {}", e.getMessage());
			return null;
		}
	}

	@Override
	public List<CommentType> fetchCommentTypes(Integer moduleCode) {
		Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<CommentType> commentType = builder.createQuery(CommentType.class);
		Root<CommentType> rootCommentType = commentType.from(CommentType.class);
		commentType.where(builder.equal(rootCommentType.get("moduleCode"), moduleCode));
		commentType.orderBy(builder.asc(rootCommentType.get("description")));
		return session.createQuery(commentType).getResultList();
	}

	@Override
	public List<Person> getPersonDetailByUserName(List<String> userNames) {
		Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Person> criteria = builder.createQuery(Person.class);
		Root<Person> root = criteria.from(Person.class);
		Expression<String> codes = root.get("principalName");
		Predicate predicateOne = codes.in(userNames);
		criteria.where(builder.and(predicateOne));
		return session.createQuery(criteria).getResultList();
	}

	@Override
	public List<Person> getPersonDetailByPersonId(List<String> personId) {
		Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Person> criteria = builder.createQuery(Person.class);
		Root<Person> root = criteria.from(Person.class);
		criteria.where(root.get("personId").in(personId));
		return session.createQuery(criteria).getResultList();
	}

	@Override
	public List<Rolodex> getRolodexDetailByRolodexId(List<Integer> rolodexId) {
		Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Rolodex> criteria = builder.createQuery(Rolodex.class);
		Root<Rolodex> root = criteria.from(Rolodex.class);
		criteria.where(root.get("rolodexId").in(rolodexId));
		return session.createQuery(criteria).getResultList();
	}

	@Override
	public Timestamp getCurrentDateBasedOnTimeZone(String timeZone) throws ParseException {
		return Timestamp.valueOf(ZonedDateTime.now(ZoneId.of(timeZone)).toLocalDateTime());
	}

	@Override
	public void saveOrUpdateOrganization(Organization organization) {
		try {
			hibernateTemplate.saveOrUpdate(organization);
		} catch (Exception e) {
			throw new ApplicationException("Error in saveOrUpdateOrganization", e, Constants.JAVA_ERROR);
		}
	}

	@Override
	public boolean checkIsOrganizationIdUnique(String organizationId) {
		try {
			return hibernateTemplate.execute(session -> {
				CriteriaBuilder builder = session.getCriteriaBuilder();
				CriteriaQuery<Organization> criteria = builder.createQuery(Organization.class);
				Root<Organization> proposalRoot = criteria.from(Organization.class);
				criteria.where(builder.equal(proposalRoot.get("organizationId"), organizationId));
				return session.createQuery(criteria).getSingleResult() == null;
			});
		} catch (Exception e) {
			return true;
		}
	}

	@Override
	public Organization loadOrganizationDetails(String organizationId) {
		return hibernateTemplate.get(Organization.class, organizationId);
	}

	@Override
	public String getOrganizationOfUnit(String unitNumber) {
		try {
			Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
			return (String) session.createStoredProcedureCall("GET_UNIT_ORGANIZATION")
					.registerStoredProcedureParameter("AV_UNIT_NUMBER", String.class, ParameterMode.IN)
					.setParameter("AV_UNIT_NUMBER", unitNumber).getSingleResult();
		} catch (Exception e) {
			logger.error("Error occurred in getOrganizationOfUnit : {}", e.getMessage());
			return null;
		}
	}

	@Override
	public void detachEntityFromSession(Object entity) {
		hibernateTemplate.getSessionFactory().getCurrentSession().detach(entity);
	}

	@Override
	public List<ResearchType> fetchAllResearchTypes() {
		Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<ResearchType> query = builder.createQuery(ResearchType.class);
		Root<ResearchType> rootResearchType = query.from(ResearchType.class);
		query.where(builder.equal(rootResearchType.get(Constants.ISACTIVE), true));
		query.orderBy(builder.asc(rootResearchType.get(Constants.DESCRIPTION)));
		return session.createQuery(query).getResultList();
	}

	@Override
	public List<ResearchTypeArea> findResearchTypeArea(CommonVO vo) {
		Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<ResearchTypeArea> query = builder.createQuery(ResearchTypeArea.class);
		Root<ResearchTypeArea> rootResearchTypeArea = query.from(ResearchTypeArea.class);
		Predicate predicate1 = builder.like(builder.lower(rootResearchTypeArea.get(Constants.DESCRIPTION)), "%" + vo.getSearchString().toLowerCase() + "%");
		Predicate predicate2 = builder.equal(rootResearchTypeArea.get(Constants.ISACTIVE), true);
		Predicate predicate3 = builder.equal(rootResearchTypeArea.get("researchTypeCode"), vo.getResearchTypeCode());
		query.where(builder.and(predicate1, predicate2, predicate3));
		query.orderBy(builder.asc(rootResearchTypeArea.get(Constants.DESCRIPTION)));
		if (vo.getFetchLimit() == null) {
			return session.createQuery(query).getResultList();
		} else {
			return session.createQuery(query).setMaxResults(vo.getFetchLimit()).getResultList();
		}

	}

	@Override
	public List<ResearchTypeSubArea> findResearchTypeSubArea(CommonVO vo) {
		Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<ResearchTypeSubArea> query = builder.createQuery(ResearchTypeSubArea.class);
		Root<ResearchTypeSubArea> rootResearchTypeSubArea = query.from(ResearchTypeSubArea.class);
		Predicate predicate1 = null;
		Predicate researchSubAreaIsActive = builder.equal(rootResearchTypeSubArea.get(Constants.ISACTIVE), true);
		Predicate researchAreaIsActive = builder.equal(rootResearchTypeSubArea.get("researchTypeArea").get(Constants.ISACTIVE), true);
		Predicate predicate3 = builder.equal(rootResearchTypeSubArea.get("researchTypeArea").get("researchTypeCode"), vo.getResearchTypeCode());
		if (vo.getResearchTypeAreaCode() != null && !vo.getResearchTypeAreaCode().isEmpty()) {
			predicate1 = builder.equal(rootResearchTypeSubArea.get("researchTypeAreaCode"), vo.getResearchTypeAreaCode());
		}
		Predicate predicate2 = builder.like(builder.lower(rootResearchTypeSubArea.get(Constants.DESCRIPTION)),	"%" + vo.getSearchString().toLowerCase() + "%");
		if (vo.getResearchTypeAreaCode() != null && !vo.getResearchTypeAreaCode().isEmpty()) {
			query.where(builder.and(predicate3, predicate1, predicate2, researchAreaIsActive, researchSubAreaIsActive));
		} else {
			query.where(builder.and(predicate3, predicate2, researchAreaIsActive, researchSubAreaIsActive));
		}
		query.orderBy(builder.asc(rootResearchTypeSubArea.get("description")));
		if (vo.getPageNumber() != null) {
			return session.createQuery(query).setMaxResults(vo.getFetchLimit()).getResultList();
		} else {
			return session.createQuery(query).getResultList();
		}
	}

	@Override
	public Boolean checkPersonHasRightInModule(Integer moduleCode, Integer awardId, List<String> rightName,
			String personId) {
		try {
			Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
			ProcedureCall procedure = session.createStoredProcedureCall("CHECK_USER_HAS_RIGHT_IN_DOC");
			procedure.registerParameter(1, Integer.class, ParameterMode.IN).bindValue(moduleCode);
			procedure.registerParameter(2, String.class, ParameterMode.IN).bindValue(awardId.toString());
			procedure.registerParameter(3, String.class, ParameterMode.IN).bindValue(String.join(",", rightName));
			procedure.registerParameter(4, String.class, ParameterMode.IN).bindValue(personId);
			procedure.execute();
			return procedure.getSingleResult().equals("TRUE") ? Boolean.TRUE : Boolean.FALSE;
		} catch (Exception e) {
			logger.error("Exception in checkPersonHasRightInModule : {}", e.getMessage());
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getUserDeffinedLookup() {
		Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
		Query query = session
				.createQuery("SELECT argumentName FROM ArgValueLookup GROUP BY argumentName ORDER BY argumentName ASC");
		return query.getResultList();
	}

	@Override
	public List<LookupWindow> getAllLookUpWindows() {
		Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<LookupWindow> lookupWindow = builder.createQuery(LookupWindow.class);
		Root<LookupWindow> rootLookupWindow = lookupWindow.from(LookupWindow.class);
		lookupWindow.where(builder.and(rootLookupWindow.get("dataTypeCode").isNotNull()));
		lookupWindow.orderBy(builder.asc(rootLookupWindow.get("description")));
		return session.createQuery(lookupWindow).getResultList();
	}

	@Override
	public Timestamp getStartTimeOfCurrentDay() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getCurrentDate());
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return new Timestamp(cal.getTime().getTime());
	}

	@Override
	public WebSocketConfiguration getWebSocketConfigurationValue(String configKey) {
		Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<WebSocketConfiguration> query = builder.createQuery(WebSocketConfiguration.class);
		Root<WebSocketConfiguration> webSocketConfigurationData = query.from(WebSocketConfiguration.class);
		Predicate predicateConfigurationKey = builder.equal(webSocketConfigurationData.get("configurationKey"),
				configKey);
		query.where(builder.and(predicateConfigurationKey));
		return session.createQuery(query).uniqueResult();
	}

	@Override
	public String getDateFromTimestampZoneFormat(String timeZone, String dateFormat) {
		try {
			Timestamp ts = Timestamp.valueOf(ZonedDateTime.now(ZoneId.of(timeZone)).toLocalDateTime());
			DateFormat newFormat = new SimpleDateFormat(dateFormat);
			return newFormat.format(new Date(ts.getTime()));
		} catch (Exception e) {
			logger.error("Exception in getDateFromTimestampZoneFormat {}", e.getMessage());
		}
		return "";
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object[] fetchModuleDetailsBasedOnId(Integer moduleCode, Integer moduleItemKey) {
		try {
			Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
			List<Object[]> moduleDetails = session.createStoredProcedureCall("GET_MODULE_DETAILS_FOR_AGREEMENT")
					.registerStoredProcedureParameter("AV_MODULE_CODE", String.class, ParameterMode.IN)
					.setParameter("AV_MODULE_CODE", moduleCode.toString())
					.registerStoredProcedureParameter("AV_MODULE_ITEM_KEY", String.class, ParameterMode.IN)
					.setParameter("AV_MODULE_ITEM_KEY", moduleItemKey.toString()).getResultList();
			return moduleDetails.get(0);
		} catch (Exception e) {
			logger.error("Error in fetchModuleDetailsBasedOnId  {}", e.getMessage());
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Integer> getAdminGroupIdsBasedOnPersonId(String personId) {
		Query query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(
				"Select t1.adminGroupId from AdminGroup t1 where t1.roleId IN (Select t2.roleId from PersonRoles t2 where t2.personId = :personId) ");
		query.setParameter("personId", personId);
		return query.getResultList();
	}

	@Override
	public AdminGroup getAdminGroupByGroupId(Integer adminGroupId) {
		return hibernateTemplate.get(AdminGroup.class, adminGroupId);
	}

	@Override
	public List<AdminGroup> fetchAdminGroupsBasedOnModuleCode(Integer moduleCode) {
		Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<AdminGroup> query = builder.createQuery(AdminGroup.class);
		Root<AdminGroup> rootAdminGroup = query.from(AdminGroup.class);
		Predicate predicateOne = builder.equal(rootAdminGroup.get("isActive"), "Y");
		Predicate predicateTwo = builder.equal(rootAdminGroup.get("moduleCode"), moduleCode);
		query.where(builder.and(predicateOne, predicateTwo));
		return session.createQuery(query).getResultList();
	}

	@Override
	public String getAdminGroupEmailAddressByAdminGroupId(Integer adminGroupId) {
		Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<String> criteria = builder.createQuery(String.class);
		Root<AdminGroup> root = criteria.from(AdminGroup.class);
		criteria.select(root.get("email"));
		criteria.where(builder.and(builder.equal(root.get("adminGroupId"), adminGroupId)));
		return session.createQuery(criteria).getSingleResult();
	}

	@Override
	public List<Country> getCountryLookUp() {
		Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Country> query = builder.createQuery(Country.class);
		Root<Country> root = query.from(Country.class);
		query.select(builder.construct(Country.class, root.get("countryCode"), root.get("countryName")));
		return session.createQuery(query).list();
	}

	@Override
	public LetterTemplateType getLetterTemplate(String templateTypeCode) {
		Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<LetterTemplateType> query = builder.createQuery(LetterTemplateType.class);
		Root<LetterTemplateType> root = query.from(LetterTemplateType.class);
		query.where(builder.and(builder.equal(root.get("letterTemplateTypeCode"), templateTypeCode)));
		return session.createQuery(query).getSingleResult();
	}

	@Override
	public List<Object> getAllLetterTemplateTypes(Integer moduleCode) {
		Query query = entityManager.createQuery("SELECT template.letterTemplateTypeCode, template.fileName, "
				+ "template.contentType, template.printFileType, template.moduleCode FROM LetterTemplateType template WHERE "
				+ "template.moduleCode = :moduleCode AND template.subModuleCode IS NULL AND template.isActive = 'Y' ORDER BY template.updateTimestamp DESC");
		query.setParameter("moduleCode", moduleCode);
		return query.getResultList();
	}

	@Override
	public List<Object> fetchAllLetterTemplateTypes(String searchKeyword) {
		StringBuilder queryBuilder = new StringBuilder(
				"SELECT template.letterTemplateTypeCode, template.fileName, template.contentType, ");
		queryBuilder.append("template.printFileType FROM LetterTemplateType template WHERE ");
		queryBuilder.append(
				"(template.letterTemplateTypeCode LIKE :searchKeyword OR template.fileName LIKE :searchKeyword) AND ");
		queryBuilder.append("template.isActive = 'Y' ORDER BY template.updateTimestamp DESC");
		Query query = entityManager.createQuery(queryBuilder.toString());
		query.setParameter("searchKeyword", "%" + searchKeyword + "%");
		return query.getResultList();
	}

	@Override
	public void doflush() {
		hibernateTemplate.getSessionFactory().getCurrentSession().flush();

	}

	@Override
	public List<LockConfiguration> getLockDetails() {
		return hibernateTemplate.loadAll(LockConfiguration.class);
	}

	public List<FileType> getAcceptedExtensions(String fileType) {
		Session session = hibernateTemplate.getSessionFactory().getCurrentSession();
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<FileType> criteriaQuery = criteriaBuilder.createQuery(FileType.class);
		Root<FileType> root = criteriaQuery.from(FileType.class);
		criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("fileType"), fileType));
		return session.createQuery(criteriaQuery).getResultList();
	}

}
