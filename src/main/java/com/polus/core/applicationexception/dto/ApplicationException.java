package com.polus.core.applicationexception.dto;

import java.io.Serializable;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.polus.core.applicationexception.pojo.ApplicationErrorDetails;

public class ApplicationException extends RuntimeException implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonIgnore
	private final Exception e;

	private final ApplicationErrorDetails applicationErrorDetails;

	private HttpStatus statusCode;

	public ApplicationException(String message, Exception e, String errorCode) {
		super();
		this.e = e;
		this.applicationErrorDetails = new ApplicationErrorDetails(errorCode, message);
		this.statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
	}

	public ApplicationException(String message, String errorCode) {
		super();
		this.e = new Exception();
		this.applicationErrorDetails = new ApplicationErrorDetails(errorCode, message);
		this.statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
	}

	public ApplicationException(String message, String errorCode, HttpStatus statusCode) {
		super();
		this.e = new Exception();
		this.applicationErrorDetails = new ApplicationErrorDetails(errorCode, message);
		this.statusCode = statusCode;
	}

	public Exception getE() {
		return e;
	}

	public ApplicationErrorDetails getApplicationErrorDetails() {
		return applicationErrorDetails;
	}

	public HttpStatus getStatusCode() {
		return statusCode;
	}

}
