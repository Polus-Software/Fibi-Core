package com.polus.core.vo;

public class EmailContent {

	public EmailContent() {
		this.error = new StringBuilder();
		this.success = new StringBuilder();
	}

	private StringBuilder error;

	private StringBuilder success;

	public StringBuilder getError() {
		return error;
	}

	public void setError(StringBuilder error) {
		this.error = error;
	}

	public StringBuilder getSuccess() {
		return success;
	}

	public void setSuccess(StringBuilder success) {
		this.success = success;
	}
}
