package com.goodluck.httpclient;

import java.io.IOException;
import java.io.OutputStream;

public class ProgressAwareOutputStream extends OutputStream {
	private OutputStream outputStream;
	private long fileSize;
	private long uploadedSize;
	
	private long lastPercent;
	private OnProgressListener listener;
	
	public ProgressAwareOutputStream(OutputStream out, long fileSize, long uploadedSize){
		this.outputStream = out;
		this.fileSize = fileSize;
		this.uploadedSize = uploadedSize;
	}
	
	public void setOnProgressListener(OnProgressListener listener) {
		this.listener = listener;
	}

	@Override
	public void write(int oneByte) throws IOException {
		try{
			outputStream.write(oneByte);
			uploadedSize += 1;
			checkProgress();
		} catch (IOException e) {
			e.printStackTrace();
			listener.onError(e.getMessage());
		}
	}
	
	@Override
	public void write(byte[] buffer, int offset, int count) throws IOException {
		try{
			outputStream.write(buffer, offset, count);
			uploadedSize += count;
			checkProgress();
		} catch (IOException e){
			listener.onError(e.getMessage());
		}
	}
	
	@Override
	public void write(byte[] buffer) throws IOException {
		try{
			outputStream.write(buffer);
			uploadedSize += buffer.length;
			checkProgress();
		}catch(IOException e){
			listener.onError(e.getMessage());
		}
	}
	
	@Override
	public void close() throws IOException {
		try{
			outputStream.close();
		}catch(IOException e){
			listener.onError(e.getMessage());
		}
	}
	
	public OutputStream getInnerOutputStream(){
		return outputStream;
	}
	
	private void checkProgress() {
		int percent = (int) (uploadedSize * 100 / fileSize);
		
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

}
