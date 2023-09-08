package com.polus.core.roles.dao;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polus.core.pojo.Unit;

@Transactional
@Service
public interface PersonRoleRightDao {

	/**
	 * @param rightName
	 * @param unitNumber
	 * @return
	 */
	List<String> fetchPersonIdsBasedOnRightAndUnitNumber(String unitNumber, String rightName);

	/**
	 * @param personId
	 * @param permissionName
	 * @param unitNumber
	 * @return
	 */
	boolean isPersonHasPermission(String personId, String permissionName, String unitNumber);

	/**
	 * @param rightName
	 * @param personId
	 * @param searchValue
	 * @return
	 */
	List<Unit> fetchUnitsBasedOnPersonAndRight(List<String> rightName, String personId, String searchValue);
}
