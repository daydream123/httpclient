package com.zf.android.httpclient;

/**
 * Interface for classes that want to monitor this input stream
 */
public interface OnProgressListener {
	/**
	 * This callback should only be used to alert user download failed.
	 * @param tag indicate which file download failed among downloading files 
	 */
	void onError(String errorMsg, String tag);
	
	/**
	 * This callback should only be used to update download progress UI
	 * @param percentage download progress
	 * @param tag indicate progress among downloading files 
	 */
	void onProgress(int percentage, String tag);
	
	/**
	 * This callback should be used to do like open downloaded file.
	 * @param tag indicate which of downloading file is finished.
	 */
	void onCompleted(String tag);
}