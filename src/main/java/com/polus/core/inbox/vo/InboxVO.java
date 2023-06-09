package com.polus.core.inbox.vo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.polus.core.inbox.pojo.Inbox;
import com.polus.core.pojo.Module;

public class InboxVO extends Inbox {

	private static final long serialVersionUID = 1L;

	private String isViewAll;

	private Timestamp toDate;

	private Timestamp fromDate;

	private List<Inbox> inboxDetails;

	private List<Module> modules;

	private boolean isProcessed = false;

	public InboxVO() {
		inboxDetails = new ArrayList<>();
		modules = new ArrayList<>();
	}

	public List<Module> getModules() {
		return modules;
	}

	public void setModules(List<Module> modules) {
		this.modules = modules;
	}

	public Timestamp getToDate() {
		return toDate;
	}

	public void setToDate(Timestamp toDate) {
		this.toDate = toDate;
	}

	public Timestamp getFromDate() {
		return fromDate;
	}

	public void setFromDate(Timestamp fromDate) {
		this.fromDate = fromDate;
	}

	public String getIsViewAll() {
		return isViewAll;
	}

	public void setIsViewAll(String isViewAll) {
		this.isViewAll = isViewAll;
	}

	public boolean isProcessed() {
		return isProcessed;
	}

	public void setProcessed(boolean isProcessed) {
		this.isProcessed = isProcessed;
	}

	public List<Inbox> getInboxDetails() {
		return inboxDetails;
	}

	public void setInboxDetails(List<Inbox> inboxDetails) {
		this.inboxDetails = inboxDetails;
	}

}
