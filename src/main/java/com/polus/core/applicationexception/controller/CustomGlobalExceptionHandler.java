package com.polus.core.applicationexception.controller;

import java.sql.SQLTransactionRollbackException;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.PessimisticLockException;
import javax.servlet.http.HttpServletRequest;

import org.hibernate.exception.LockAcquisitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.polus.core.applicationexception.dto.ApplicationException;
import com.polus.core.applicationexception.service.ApplicationExceptionService;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@Autowired
	private ApplicationExceptionService applicationExceptionService;

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});
		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ApplicationException.class)
	protected ResponseEntity<Object> handleApplicationException(ApplicationException ex, WebRequest request,
			HttpServletRequest servletRequest) {
		if (ex.getE() instanceof SQLTransactionRollbackException
				|| ex.getE().getCause() instanceof SQLTransactionRollbackException) {
			if (ex.getE().getMessage().contains("try restarting transaction")) {
				ex.getApplicationErrorDetails().setErrorMessage("Deadlock");
			}
		}
		if (ex.getE() instanceof PessimisticLockException || ex.getE() instanceof LockAcquisitionException
				|| ex.getE() instanceof CannotAcquireLockException) {
			ex.getApplicationErrorDetails().setErrorMessage("Deadlock");
		}
		return new ResponseEntity<>(applicationExceptionService.saveErrorDetails(ex, servletRequest),
				ex.getStatusCode());
	}

	@ExceptionHandler(RuntimeException.class)
	protected ResponseEntity<Object> handleRuntimeException(RuntimeException ex, WebRequest request,
			HttpServletRequest servletRequest) {
		ApplicationException applicationException = new ApplicationException(
				"oops! something went wrong please try again", ex, "handleRuntimeException");
		if (applicationException.getE() instanceof SQLTransactionRollbackException
				|| applicationException.getE().getCause() instanceof SQLTransactionRollbackException) {
			if (applicationException.getE().getMessage().contains("try restarting transaction")) {
				applicationException.getApplicationErrorDetails().setErrorMessage("Deadlock");
			}
		}
		if (applicationException.getE() instanceof PessimisticLockException
				|| applicationException.getE() instanceof LockAcquisitionException
				|| applicationException.getE() instanceof CannotAcquireLockException) {
			applicationException.getApplicationErrorDetails().setErrorMessage("Deadlock");
		}
		return new ResponseEntity<>(applicationExceptionService.saveErrorDetails(applicationException, servletRequest),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(Exception.class)
	protected ResponseEntity<Object> handleException(Exception ex, WebRequest request,
			HttpServletRequest servletRequest) {
		ApplicationException applicationException = new ApplicationException(
				"oops! something went wrong please try again", ex, "handlesException");
		if (applicationException.getE() instanceof SQLTransactionRollbackException
				|| applicationException.getE().getCause() instanceof SQLTransactionRollbackException) {
			if (applicationException.getE().getMessage().contains("try restarting transaction")) {
				applicationException.getApplicationErrorDetails().setErrorMessage("Deadlock");
			}
		}
		if (applicationException.getE() instanceof PessimisticLockException
				|| applicationException.getE() instanceof LockAcquisitionException
				|| applicationException.getE() instanceof CannotAcquireLockException) {
			applicationException.getApplicationErrorDetails().setErrorMessage("Deadlock");
		}
		return new ResponseEntity<>(applicationExceptionService.saveErrorDetails(applicationException, servletRequest),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
