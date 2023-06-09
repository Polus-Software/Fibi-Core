package com.polus.core.useractivitylog.dao;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polus.core.pojo.Unit;
import com.polus.core.useractivitylog.pojo.PersonLoginDetail;
import com.polus.core.useractivitylog.vo.UserActivityLogVO;

@Transactional
@Service
public interface UserActivityLogDao {

	public List<Unit> getUnitsListByPersonIdAndRights(String personId, List<String> systemRights);
	
	public UserActivityLogVO getPersonLoginDetails(UserActivityLogVO loginVO);

	/**
	 * Get recent login details of a person by Person Id
	 * @param personIds
	 * @return
	 */
	public PersonLoginDetail getRecentPersonLoginDetailByUserName(String userName);

	/**
	 * This method is to save Person login details
	 * @param personLoginDetail
	 */
	public void savePersonLoginDetail(PersonLoginDetail personLoginDetail);

	/**
	 * This method is used to fetch PersonLoginDetail object.
	 * @param personLoginDetailId - PersonLoginDetail id.
	 * @return personLoginDetail.
	 */
	public PersonLoginDetail getPersonLoginDetailById(Integer personLoginDetailId);

	/**
	 * Fetch All person login details
	 * @param loginVO
	 * @return
	 */
	public UserActivityLogVO fetchAllPersonLoginDetails(UserActivityLogVO userActivityLogVO);

}
