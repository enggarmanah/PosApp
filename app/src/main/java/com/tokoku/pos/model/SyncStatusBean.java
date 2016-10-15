package com.tokoku.pos.model;

public class SyncStatusBean {
	
	public static final String SUCCESS = "SUCCESS";
	public static final String FAIL = "FAIL";
	
	protected Long remoteId;
	protected String status;
	
	public Long getRemoteId() {
		return remoteId;
	}
	public void setRemoteId(Long remoteId) {
		this.remoteId = remoteId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
