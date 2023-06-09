package com.polus.core.useractivitylog.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polus.core.useractivitylog.vo.UserActivityLogVO;

@Transactional
@Service
public interface UserActivityLogService {

	public String getUnitByPersonId(UserActivityLogVO vo, String personId);
	
	public String fetchPersonLoginDetails(UserActivityLogVO userActivityLogVO);

	public ResponseEntity<byte[]> exportUserActivityDatas(UserActivityLogVO userActivityLogVO);

	/**
	 * This method is used to savePersonLoginDetails.
	 * @param loginStatus
	 * @param userName
	 * @param unitNumber 
	 */
	public void savePersonLoginDetails(String personID, String fullName, String loginStatus, String userName);
}
