package com.goodluck.httpclient.utils;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;

/**
 * InputStream that notifies listeners of its progress.
 */
public class ProgressAwareInputStream extends InputStream {
	private InputStream inputStream;
	private long fileSize;
	private long localSize;
	
	/**
	 * Identify which download is on progress, it can be file URL and so on.
	 */
	private String tag;
	private long lastPercent;
	private OnProgressListener listener;

	public ProgressAwareInputStream(InputStream in, long fileSize, long localSize, String tag) {
		this.inputStream = in;
		this.fileSize = fileSize;
		this.localSize = localSize;
		this.tag = tag;
		
		// init progress
		this.lastPercent = (int) (this.localSize * 100 / this.fileSize);
	}

	public void setOnProgressListener(OnProgressListener listener) {
		this.listener = listener;
	}

	public Object getTag() {
		return tag;
	}

	@Override
	public int read() {
		try{
			int readCount = inputStream.read();
			localSize += readCount;
			checkProgress();
			return readCount;
		} catch (IOException e) {
			e.printStackTrace();
			listener.onError(e.getMessage());
			return -1;
		}
	}

	@Override
	public int read(@NonNull byte[] b) {
		try{
			int readCount = inputStream.read(b);
			localSize += readCount;
			checkProgress();
			return readCount;
		} catch (IOException e) {
			e.printStackTrace();
			listener.onError(e.getMessage());
			return -1;
		}
	}

	@Override
	public int read(@NonNull byte[] b, int offset, int length) {
		try{
			int readCount = inputStream.read(b, offset, length);
			localSize += readCount;
			checkProgress();
			return readCount;
		} catch (IOException e) {
			e.printStackTrace();
			listener.onError(e.getMessage());
			return -1;
		}
	}

	private void checkProgress() {
		int percent = (int) (localSize * 100 / fileSize);
		
		// check whether progress is updated
		if (percent - lastPercent >= 1) {
			lastPercent = percent;
			if (listener != null){
				listener.onProgress(percent);
			}
		}
		
		// check whether download is completed
		if(percent == 100 && listener != null){
			listener.onCompleted();
		}
	}

	@Override
	public void close() {
		try{
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
			listener.onError(e.getMessage());
		}
	}

	@Override
	public int available(){
		try{
			return inputStream.available();
		} catch (IOException e) {
			e.printStackTrace();
			listener.onError(e.getMessage());
			return -1;
		}
	}

	@Override
	public void mark(int readlimit) {
		inputStream.mark(readlimit);
	}

	@Override
	public synchronized void reset() {
		try{
			inputStream.reset();
		} catch (IOException e) {
			e.printStackTrace();
			listener.onError(e.getMessage());
		}
	}

	@Override
	public boolean markSupported() {
		return inputStream.markSupported();
	}

	@Override
	public long skip(long n){
		try{
			return inputStream.skip(n);
		} catch (IOException e) {
			e.printStackTrace();
			listener.onError(e.getMessage());
			return -1;
		}
	}

}