package com.goodluck.httpclient;

/**
 * Interface for classes that want to monitor this input stream
 */
public interface OnProgressListener {
	/**
	 * This callback should only be used to alert user download failed.
	 */
	void onError(String errorMsg);
	
	/**
	 * This callback should only be used to update download progress UI
	 * @param percentage download progress
	 */
	void onProgress(int percentage);
	
	/**
	 * This callback should be used to do like open downloaded file.
	 */
	void onCompleted();
}