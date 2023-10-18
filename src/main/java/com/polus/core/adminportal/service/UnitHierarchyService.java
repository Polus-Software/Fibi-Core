package com.polus.core.adminportal.service;

import org.springframework.stereotype.Service;

import com.polus.core.adminportal.vo.RateLaVO;
import com.polus.core.adminportal.vo.RateVO;
import com.polus.core.adminportal.vo.UnitHierarchyVO;
import com.polus.core.adminportal.vo.UnitVO;

@Service
public interface UnitHierarchyService {

	/**
	 * This method is used to get Unit Hierarchy.
	 * 
	 * @param unit
	 * @return it returns unit hierarchy list with administrator types.
	 */
	public UnitHierarchyVO getUnitHierarchy(String unitNumber);

	/**
	 * This method is used for get search result
	 * 
	 * @param vo
	 * @return
	 */
	public String getUnitsList();

	/**
	 * This method is used for get details of a single unit
	 * 
	 * @param vo
	 * @return
	 */

	public String getUnitDetails(UnitHierarchyVO vo);

	/**
	 * This method is used for add new units
	 * 
	 * @param vo
	 * @return success
	 */
	public String addNewUnit(UnitVO unitVO);

	/**
	 * This method is used for get rates
	 * 
	 * @param vo
	 * @return list contains rate class rate type and institutional rate details
	 */
	public String getRates(RateVO vo);

	/**
	 * This method is used for delete rate by id
	 * 
	 * @param vo
	 * @return success
	 */
	public String deleteRate(RateVO vo);

	/**
	 * This method is used to add a new rate.
	 * 
	 * @Params instituteVo Object of InstituteVO class.
	 * @return Success
	 */
	public String addInstituteRate(RateVO instituteRateVO);

	/**
	 * This method is used for delete Unit Administrator
	 * 
	 * @param vo
	 * @return success
	 */
	public String deleteUnitAdministrator(UnitVO vo);

	/**
	 * This method is used to add a new LA rate.
	 * 
	 * @Params rateLaVO Object of RateLaVO class.
	 * @return Success
	 */
	public String addInstituteLARate(RateLaVO rateLaVO);

	/**
	 * This method is used for get rates
	 * 
	 * @param vo
	 * @return list contains rate class rate type and institutional rate details
	 */
	public String getLARates(RateLaVO rateLaVO);

	/**
	 * This method is used for delete entry from institute Lab allocation rate
	 * 
	 * @param rateLaVO
	 * @return success message
	 */
	public String deleteLARate(RateLaVO rateLaVO);

	/**
	 * This method is used to activate a unit or deactivate a unit and its Child Units
	 * @param unitNumber
	 * @param acType
	 * @return
	 */
	public String activateOrDeactivateUnit(String unitNumber,  String acType);
}
