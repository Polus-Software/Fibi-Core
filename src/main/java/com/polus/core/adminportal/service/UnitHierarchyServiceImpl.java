package com.polus.core.adminportal.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polus.core.adminportal.dao.UnitHierarchyDao;
import com.polus.core.adminportal.pojo.InstituteLARate;
import com.polus.core.adminportal.pojo.InstituteRate;
import com.polus.core.adminportal.vo.RateLaVO;
import com.polus.core.adminportal.vo.RateVO;
import com.polus.core.adminportal.vo.UnitHierarchyVO;
import com.polus.core.adminportal.vo.UnitVO;
import com.polus.core.common.dao.CommonDao;
import com.polus.core.constants.Constants;
import com.polus.core.pojo.Unit;
import com.polus.core.pojo.UnitAdministrator;
import com.polus.core.pojo.UnitAdministratorType;

@Transactional
@Service(value = "unitHierarchyService")
public class UnitHierarchyServiceImpl implements UnitHierarchyService {

	protected static Logger logger = LogManager.getLogger(UnitHierarchyServiceImpl.class.getName());

	@Autowired
	private UnitHierarchyDao unitHierarchyDao;

	@Autowired
	private CommonDao commonDao;

	@Autowired
	private HibernateTemplate hibernateTemplate;
	
	private static final String FAILURE = "failure";

	@Override
	public UnitHierarchyVO getUnitHierarchy(String unitNumber) {
		logger.info("-------- getUnitHierarchy serviceimpl---------");
		UnitHierarchyVO unitHierarchyVO = new UnitHierarchyVO();
		unitHierarchyVO.setUnitHierarchyList((unitHierarchyDao.getUnitHierarchy(unitNumber)));
		unitHierarchyVO.setUnitAdministratorTypeList(unitHierarchyDao.getUnitAdministratorTypesList());
		unitHierarchyVO.setUnitNumber(unitNumber);
		return unitHierarchyVO;	
	}

	@Override
	public String getUnitsList() {
		logger.info("-------- getUnitList serviceimpl ---------");
		return unitHierarchyDao.convertObjectToJSON(unitHierarchyDao.getUnitsList());
	}

	@Override
	public String getUnitDetails(UnitHierarchyVO vo) {
		logger.info("-------- getUnitDetails serviceimpl---------");
		return unitHierarchyDao.convertObjectToJSON(unitHierarchyDao.getUnitDetails(vo));
	}

	@Override
	public String addNewUnit(UnitVO unitVO) {
		logger.info("-------- addNewUnit serviceimpl---------");
		Unit unit = unitVO.getUnit();
		List<UnitAdministrator> unitAdministrators = unit.getUnitAdministrators();
		String response = null;
		String acType = unitVO.getAcType();
		if (acType.equalsIgnoreCase("I")) {
			response = saveUnit(unit);
		}
		if (unit.getUnitAdministrators() != null && !unit.getUnitAdministrators().isEmpty()) {
			for (UnitAdministrator unitAdministrator : unitAdministrators) {
				if (acType.equalsIgnoreCase("I")) {
					unitAdministrator.setUnit(unit);
					UnitAdministratorType unitAdministratorType = unitHierarchyDao.getUnitAdministratorTypeByCode(unitAdministrator.getUnitAdministratorTypeCode());
					unitAdministrator.setUnitAdministratorType(unitAdministratorType);
					unitAdministrator.setUnitNumber(unit.getUnitNumber());
				}
				response = unitHierarchyDao.addNewUnitAdministrator(unitAdministrator, acType);
			}
		}
		if (acType.equalsIgnoreCase("U")) {
			response = saveUnit(unit);
		}
		if(unit.getParentUnitNumber() != null && (Boolean.TRUE.equals(unitVO.getParentUnitChanged()) || Constants.acTypeInsert.equals(unitVO.getAcType()))) {
			unitHierarchyDao.syncUnitWithChildrenAndPersonRole(unit.getParentUnitNumber(), unit.getUnitNumber(), unitVO.getParentUnitChanged());
		}
		return response;
	}

	private String saveUnit(Unit unit) {
		return unitHierarchyDao.addNewUnit(unit);
	}

	@Override
	public String addInstituteRate(RateVO rateVO) {
		logger.info("-------- addInstituteRate serviceimpl---------");
		Timestamp updateTimeStamp = commonDao.getCurrentTimestamp();
		InstituteRate instituteRate = rateVO.getInstituteRate();
		instituteRate.setUpdateTimestamp(updateTimeStamp);
		if(rateVO.getCampusFlag().equalsIgnoreCase("ON")){
			instituteRate.setOnOffCampusFlag("N");
		}
		else if(rateVO.getCampusFlag().equalsIgnoreCase("OFF")){
			instituteRate.setOnOffCampusFlag("F");
		}
		return unitHierarchyDao.addInstituteRate(instituteRate);
	}

	public Date getCurrentDate() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		return c.getTime();
	}

	public Timestamp getCurrentTimestamp() {
		return new Timestamp(this.getCurrentDate().getTime());
	}

	@Override
	public String getRates(RateVO vo) {
		logger.info("-------- getRates serviceimpl---------");
		RateVO rateVO = new RateVO();
		if (vo.getUnitNumber() != null) {
			Unit unit = hibernateTemplate.get(Unit.class, vo.getUnitNumber());
			vo.setUnitName(unit.getUnitName());
			rateVO.setUnitName(vo.getUnitName());
			rateVO.setUnitNumber(vo.getUnitNumber());
			rateVO.setRateClassList(unitHierarchyDao.getAllRateClass());
			rateVO.setRateTypeList(unitHierarchyDao.getAllRateTypes());
			rateVO.setInstituteRatesList(unitHierarchyDao.getRates(vo.getUnitNumber()));
			rateVO.setActivityTypeList(unitHierarchyDao.getAtivityTypeList());
		}
		return unitHierarchyDao.convertObjectToJSON(rateVO);
	}

	@Override
	public String deleteRate(RateVO vo) {
		logger.info("-------- deleteRate serviceimpl---------");
		if (vo != null) {
			InstituteRate instituteRate = vo.getInstituteRate();
			if(instituteRate !=null){
				return unitHierarchyDao.deleteRate(instituteRate);
			}
		}
		return unitHierarchyDao.convertObjectToJSON(FAILURE);
	}

	@Override
	public String deleteUnitAdministrator(UnitVO vo) {
		logger.info("-------- delete UnitAdministrator serviceimpl---------");
		if (vo != null) {
			List<UnitAdministrator> unitAdministratorsList = vo.getUnitAdministrators();
			if(unitAdministratorsList!=null && !unitAdministratorsList.isEmpty()){
				return unitHierarchyDao.deleteUnitAdministrator(unitAdministratorsList);
			}
		}
		return unitHierarchyDao.convertObjectToJSON(FAILURE);
	}

	@Override
	public String deleteLARate(RateLaVO rateLaVO) {
		logger.info("-------- delete LA Rate serviceimpl---------");
		if (rateLaVO != null) {
			InstituteLARate instituteLARate = rateLaVO.getInstituteLARate();
			logger.info("delete LA Rate service : UnitNumber - {}",instituteLARate.getUnitNumber());
			return unitHierarchyDao.deleteLARate(instituteLARate);
		}
		return unitHierarchyDao.convertObjectToJSON(FAILURE);
	}

	@Override
	public String addInstituteLARate(RateLaVO rateLaVO) {
		logger.info("-------- add Institute LA Rate serviceimpl---------");
		if (rateLaVO != null) {
			Timestamp updateTimeStamp = commonDao.getCurrentTimestamp();
			InstituteLARate instituteLARateAdd = rateLaVO.getInstituteLARate();
			instituteLARateAdd.setUpdateTimestamp(updateTimeStamp);
			if (rateLaVO.getCampusFlag().equalsIgnoreCase("ON")) {
				instituteLARateAdd.setOnOffCampusFlag("N");
			} else {
				instituteLARateAdd.setOnOffCampusFlag("F");
			}
			return unitHierarchyDao.addInstituteLARate(instituteLARateAdd);
		}
		return unitHierarchyDao.convertObjectToJSON(FAILURE);
	}

	@Override
	public String getLARates(RateLaVO rateLaVO) {
		logger.info("-------- get LA Rates serviceimpl---------");
		RateLaVO laRateVO = new RateLaVO();
		Unit unitName = hibernateTemplate.get(Unit.class, rateLaVO.getUnitNumber());
		rateLaVO.setUnitName(unitName.getUnitName());
		laRateVO.setUnitName(rateLaVO.getUnitName());
		laRateVO.setUnitNumber(rateLaVO.getUnitNumber());
		laRateVO.setRateClassLaList(unitHierarchyDao.getAllLARateClass());
		laRateVO.setRateTypeLaList(unitHierarchyDao.getAllLARateTypes());
		List<InstituteLARate> lArateList = unitHierarchyDao.getLARates(rateLaVO.getUnitNumber());
		laRateVO.setInstituteLARatesList(lArateList);
		return unitHierarchyDao.convertObjectToJSON(laRateVO);
	}

	@Override
	public String activateOrDeactivateUnit(String unitNumber, String acType) {
		List<String> childUnitNumbers = new ArrayList<>();
		if (acType.equalsIgnoreCase("D")) {
			childUnitNumbers = unitHierarchyDao.getChildUnitNumbersBasedOnParentUnitNumber(unitNumber);
			unitHierarchyDao.deactivateParentAndChildUnits(childUnitNumbers);
			if (childUnitNumbers != null && !childUnitNumbers.isEmpty()) {
				childUnitNumbers.remove(unitNumber);
			}
		} else {
			unitHierarchyDao.activateUnit(unitNumber);
		}
		UnitHierarchyVO vo = getUnitHierarchy(Constants.ROOT_UNIT);
		vo.setChildUnitNumber(childUnitNumbers);
		return commonDao.convertObjectToJSON(vo);
	}
}
