package com.polus.core.applicationexception.dao;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.polus.core.applicationexception.pojo.ApplicationErrorDetails;

@Transactional
@Service
public interface ApplicationExceptionDao {

	void saveErrorDetails(ApplicationErrorDetails applicationErrorDetails);

}
