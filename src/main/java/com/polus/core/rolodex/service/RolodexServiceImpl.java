package com.polus.core.rolodex.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polus.core.common.dao.CommonDao;
import com.polus.core.common.dto.ElasticQueueRequest;
import com.polus.core.common.service.ElasticSyncOperation;
import com.polus.core.constants.Constants;
import com.polus.core.person.pojo.Person;
import com.polus.core.pojo.Country;
import com.polus.core.pojo.Rolodex;
import com.polus.core.rolodex.dao.RolodexDao;
import com.polus.core.rolodex.vo.RolodexSearchResult;
import com.polus.core.rolodex.vo.RolodexVO;
import com.polus.core.security.AuthenticatedUser;
import com.polus.core.vo.CommonVO;

@Transactional
@Service(value = "rolodexService")
public class RolodexServiceImpl implements RolodexService {

	protected static Logger logger = LogManager.getLogger(RolodexServiceImpl.class.getName());

	@Autowired
	private RolodexDao rolodexDao;

	@Autowired
	private CommonDao commonDao;

	@Autowired
	private ElasticQueueRequest elasticQueueRequest;
		
	@Autowired
	private ElasticSyncOperation elasticSyncOperation;

	private static final Boolean TRUE = true;
	private static final Boolean FALSE = false;

	@Override
	public List<RolodexSearchResult> findRolodex(CommonVO vo) {
		return rolodexDao.findRolodex(vo);
	}

	@Override
	public String getRolodexDetailById(RolodexVO vo) {
		Rolodex rolodex = rolodexDao.getRolodexDetailById(vo.getRolodexId());
		if (rolodex.getCountryCode() != null) {
			rolodex.setCountry(rolodexDao.fetchCountryByCountryCode(rolodex.getCountryCode()));
		}
		vo.setRolodex(rolodex);
		return commonDao.convertObjectToJSON(vo);
	}

	@Override
	public String saveOrUpdateRolodex(RolodexVO vo) {
		Rolodex rolodex = vo.getRolodex();
		String acType = vo.getAcType();
		logger.info("acType : {} " , acType);
		Boolean emailExist = FALSE;
		if(rolodex.getRolodexId() != null) {
			 emailExist = rolodexDao.checkPersonEmailAddress(rolodex.getRolodexId(), rolodex.getEmailAddress()) == TRUE; 
		}
	 	if (acType != null &&( acType.equals("I") || acType.equals("U"))) {
			if (Boolean.TRUE.equals(rolodexDao.checkUniqueEmailAddress(rolodex.getEmailAddress()) == FALSE || Boolean.TRUE.equals(emailExist))) {
				if(Boolean.TRUE.equals(vo.getIsAddToAddressBook()) && commonDao.getParameterValueAsBoolean(Constants.ADDTO_ADDRESSBOOK_ACTIVE_BY_DEFAULT)) {
					rolodex.setActive(true);
				}
				if (acType.equals(Constants.acTypeInsert)) {
					rolodex.setCreateUserFullName(AuthenticatedUser.getLoginUserFullName());
				}
				vo.setRolodex(rolodexDao.saveOrUpdateRolodex(rolodex));
				vo.setMessage("Rolodex saved successfully.");
				vo.setAcType("U");
			} else {
				vo.setMessage("Email Address already exists.");
				vo.setRolodex(null);
			}
		} else if (acType != null && acType.equals("D")) {
			rolodexDao.deleteRolodex(rolodex);
			vo.setMessage("Rolodex deleted successfully.");
		}
		if (vo.getRolodex() != null) {
			setElasticRequestForRolodexSync(vo, acType);
		}
		return commonDao.convertObjectToJSON(vo);
	}

	private void setElasticRequestForRolodexSync(RolodexVO vo, String acType) {
		if (Boolean.TRUE.equals(vo.getRolodex().isActive())) {
			if (acType.equals("I")) {
				elasticSyncOperation.addElasticQueueRequestToList(elasticQueueRequest, vo.getRolodex().getRolodexId().toString(), Constants.ELASTIC_ACTION_INSERT, Constants.ELASTIC_INDEX_ROLODEX);
			} else if (acType.equals("D")) {
				elasticSyncOperation.addElasticQueueRequestToList(elasticQueueRequest, vo.getRolodex().getRolodexId().toString(), Constants.ELASTIC_ACTION_DELETE, Constants.ELASTIC_INDEX_ROLODEX);
			} else {
				elasticSyncOperation.addElasticQueueRequestToList(elasticQueueRequest, vo.getRolodex().getRolodexId().toString(), Constants.ELASTIC_ACTION_DELETE, Constants.ELASTIC_INDEX_ROLODEX);
				elasticSyncOperation.addElasticQueueRequestToList(elasticQueueRequest, vo.getRolodex().getRolodexId().toString(), Constants.ELASTIC_ACTION_INSERT, Constants.ELASTIC_INDEX_ROLODEX);
			}
		} else {
			if (acType.equals("U")) {
				elasticSyncOperation.addElasticQueueRequestToList(elasticQueueRequest, vo.getRolodex().getRolodexId().toString(), Constants.ELASTIC_ACTION_DELETE, Constants.ELASTIC_INDEX_ROLODEX);
			}
		}
	}

	@Override
	public String getAllRolodexes(RolodexVO vo) {
		vo = rolodexDao.getAllRolodexes(vo);
		getFullNameOfCreateUser(vo.getRolodexes());
		return commonDao.convertObjectToJSON(vo);
	}

	@Override
	public List<Country> findCountryList(CommonVO vo) {
		return commonDao.fetchCountryList(vo);
	}

	private void getFullNameOfCreateUser(List<Rolodex> rolodexes) {
		Set<String> userName = rolodexes.stream().map(Rolodex::getCreateUser).collect(Collectors.toSet());
		if (!userName.isEmpty()) {
			List<Person> personDetails = commonDao.getPersonDetailByUserName(new ArrayList<>(userName));
			Map<String, String> collect = personDetails.stream().collect(Collectors.toMap(person -> person.getPrincipalName().toUpperCase(), person -> person.getFullName()));
			rolodexes.stream().filter(item -> item.getCreateUser() != null).filter(item ->
					collect.containsKey(item.getCreateUser().toUpperCase())).forEach(item ->
					item.setCreateUserFullName(collect.get(item.getCreateUser().toUpperCase())));
		}
	}

}
