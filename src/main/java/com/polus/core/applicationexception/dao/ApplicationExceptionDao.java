package com.polus.core.applicationexception.dao;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polus.core.applicationexception.pojo.ApplicationErrorDetails;

@Transactional
@Service
public interface ApplicationExceptionDao {

	void saveErrorDetails(ApplicationErrorDetails applicationErrorDetails);

	/**
	* This method is used to get error details
	*
	* @param from and to date
	* @return list of error details
	*/
	public List<ApplicationErrorDetails> fetchElasticErrorDetails(Date utilFromDate, Date utilToDate);

}
