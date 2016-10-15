package com.tokoku.pos.async;

public interface HttpAsyncListener {
	
	public void setSyncProgress(int progress);
	
	public void setSyncMessage(String message);
	
	public void onTimeOut();
	
	public void onSyncError();
	
	public void onSyncError(String message);
}
