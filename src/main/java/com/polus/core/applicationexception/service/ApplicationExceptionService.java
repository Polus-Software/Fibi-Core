package com.polus.core.applicationexception.service;

import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.polus.core.applicationexception.dto.ApplicationException;
import com.polus.core.applicationexception.vo.ApplicationExceptionVO;

@Service
public interface ApplicationExceptionService {

	Object saveErrorDetails(ApplicationException ex, HttpServletRequest request);

	/**
	* This method used to fetch elastic error details
	*
	* @param date range
	* @return error details
	* @throws ParseException 
	*/
	public String getElasticErrorDetails(ApplicationExceptionVO applicationExceptionVO) throws ParseException;

}
