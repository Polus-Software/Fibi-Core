/**
 * @author NikhilPrabha & AjithPeter 
 */

package com.polus.core.adminportal.dao;

import java.util.List;

import org.springframework.stereotype.Service;

import com.polus.core.adminportal.dto.UnitHierarchy;
import com.polus.core.adminportal.pojo.InstituteLARate;
import com.polus.core.adminportal.pojo.InstituteRate;
import com.polus.core.adminportal.pojo.RateClass;
import com.polus.core.adminportal.pojo.RateType;
import com.polus.core.adminportal.vo.UnitHierarchyVO;
import com.polus.core.adminportal.vo.UnitVO;
import com.polus.core.pojo.ActivityType;
import com.polus.core.pojo.Unit;
import com.polus.core.pojo.UnitAdministrator;
import com.polus.core.pojo.UnitAdministratorType;

@Service
public interface UnitHierarchyDao {

	/**
	 * This method is used to get unit hierarchy.
	 * 
	 * @param unit
	 *            number
	 * @return it returns unit hierarchy object which contains unit hierarchy
	 *         list.
	 */
	public List<UnitHierarchy> getUnitHierarchy(String unitNumber);

	/**
	 * This method is used to convert Object into JSON format.
	 * 
	 * @param object
	 *            - request object.
	 * @return response - JSON data.
	 */
	public String convertObjectToJSON(Object object);

	/**
	 * This method is used to get Unit Administrator Types.
	 * 
	 * @param nothing
	 * @return it returns unit hierarchy object which contains unit hierarchy
	 *         list.
	 */
	public List<UnitAdministratorType> getUnitAdministratorTypesList();

	/**
	 * This method is used for unit Hierarchy search.
	 * 
	 * @param vo
	 *            - Object of UnitHierarchyVO class.
	 * @return it returns unit hierarchy list.
	 */
	public List<Unit> getUnitsList();

	/**
	 * This method is used for unit Hierarchy search.
	 * 
	 * @param vo
	 *            - Object of UnitHierarchyVO class.
	 * @return it returns unit hierarchy list.
	 */
	public UnitVO getUnitDetails(UnitHierarchyVO vo);

	/**
	 * This method is used for add new units
	 * 
	 * @param vo
	 * @return success
	 */
	public String addNewUnit(Unit unit);

	/**
	 * This method is used for add new unit administrator
	 * 
	 * @param unitAdministrator
	 * @return success
	 */
	public String addNewUnitAdministrator(UnitAdministrator unitAdministrator,String acType);

	/**
	 * This method is used for get unit administrator type by code
	 * 
	 * @param code
	 * @return unitAdministratorType
	 */
	public UnitAdministratorType getUnitAdministratorTypeByCode(String code);

	/**
	 * This method is used for get Institution rates
	 * 
	 * @param unitNumber
	 * @return it returns Institution rates details list.
	 */
	public List<InstituteRate> getRates(String unitNumber);

	/**
	 * This method is used for get rate class
	 * 
	 * @param
	 * @return it returns rate class list.
	 */
	public List<RateClass> getAllRateClass();

	/**
	 * This method is used for get rate type
	 * 
	 * @param
	 * @return it returns rate type list.
	 */
	public List<RateType> getAllRateTypes();

	/**
	 * This method is used for delete rate
	 * 
	 * @param rateVo
	 * @return success
	 */
	public String deleteRate(InstituteRate instituteRate);

	/**
	 * his method is used for get activity type
	 * 
	 * @param rateVo
	 */
	public List<ActivityType> getAtivityTypeList();

	/**
	 * This method is used to add a new rate.
	 * 
	 * @Params instituteRate Object of InstituteRate class.
	 * @return Success
	 */
	public String addInstituteRate(InstituteRate instituteRate);

	/**
	 * This method is used for delete Unit Administrator
	 * 
	 * @param unitAdministratorsList
	 * @return success
	 */
	public String deleteUnitAdministrator(List<UnitAdministrator> unitAdministratorsList);

	/**
	 * This method is used for get LA rate type
	 * 
	 * @param
	 * @return it returns LA rate type list.
	 */
	public List<RateType> getAllLARateTypes();

	/**
	 * This method is used for get la rate class
	 * 
	 * @param
	 * @return it returns LA rate class list.
	 */
	public List<RateClass> getAllLARateClass();

	/**
	 * This method is used for get Institute LA rates
	 * 
	 * @param unitNUmber
	 * @return it returns Institution rates details list.
	 */
	public List<InstituteLARate> getLARates(String unitNUmber);

	/**
	 * This method is used to add a new LA rate.
	 * 
	 * @Params instituteRate Object of InstituteRate class.
	 * @return Success
	 */
	public String addInstituteLARate(InstituteLARate instituteLARate);

	/**
	 * This method is used for delete LA rate
	 * 
	 * @param rateVo
	 * @return success
	 */
	public String deleteLARate(InstituteLARate instituteLARate);

	/**
	 * This method is used to fetch parent unit number by unit number
	 * @param unitNumber - unitNumber
	 * @return parentUnitNumber
	 */
	public String fetchParentUnitNumberByUnitNumber(String unitNumber);

	/**
	 * This method is used to add a new unit, we will add data to unit_with_children and person_role_rt tables
	 * @param unitNumber
	 * @param childUnitNumber
	 * @param parentUnitChanged 
	 * @param unitName
	 * @param childUnitName
	 */
	void syncUnitWithChildrenAndPersonRole(String unitNumber, String childUnitNumber, Boolean parentUnitChanged);

	/**
	 * This method is used to activate a particular unit
	 * @param unitNumber
	 */
	public void activateUnit(String unitNumber);

	/**
	 * This method is used to deactivate parent and child units
	 * @param unitNumbers
	 * @param acType
	 */
	public void deactivateParentAndChildUnits(List<String> unitNumbers);

	/**
	 * This method is used to fetch child units of a particular parent
	 * @param unitNumber
	 * @return
	 */
	public List<String> getChildUnitNumbersBasedOnParentUnitNumber(String unitNumber);

}
