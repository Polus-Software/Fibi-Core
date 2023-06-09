package com.polus.core.applicationexception.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.polus.core.applicationexception.dto.ApplicationException;

@Service
public interface ApplicationExceptionService {

	Object saveErrorDetails(ApplicationException ex, HttpServletRequest request);

}
