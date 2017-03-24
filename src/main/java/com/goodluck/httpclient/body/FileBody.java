package com.goodluck.httpclient.body;

import com.goodluck.httpclient.ContentType;
import com.goodluck.httpclient.OnProgressListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileBody extends HttpBody {
	protected final File file;
	private long uploadedSize;
	private OnProgressListener progressListener;
    
    public FileBody(File file){
    	this.file = file;
    }
    
    public FileBody(String filePath){
    	this.file = new File(filePath);
    }

    public FileBody(File file, long uploadedSize, OnProgressListener listener){
    	this.file = file;
    	this.uploadedSize = uploadedSize;
    	this.progressListener = listener;
    }
    
    public FileBody(String filePath, long uploadedSize, OnProgressListener listener){
    	this.file = new File(filePath);
    	this.uploadedSize = uploadedSize;
    	this.progressListener = listener;

		if (!file.exists()) {
			throw new RuntimeException("file to upload does not exist: " + filePath);
		}
    }
    
	@Override
	public String getContentType() {
		return ContentType.DEFAULT_BINARY;
	}

	@Override
	public String getContent() {
		throw new UnsupportedOperationException("FileBody does not implement #getContent().");
	}

	@Override
	public long getContentLength() {
		return file.length();
	}

	@Override
	public void writeTo(OutputStream outputStream) throws IOException {
		FileInputStream fin = new FileInputStream(file);
		copy(fin, outputStream);
		outputStream.flush();
	}
	
	@Override
	public boolean isStreaming() {
		return true;
	}
	
	public File getFile(){
		return file;
	}
	
	public long getUploadedSize(){
		return uploadedSize;
	}
	
	public OnProgressListener getProgressListener(){
		return progressListener;
	}

	private long copy(InputStream input, OutputStream output) throws IOException {
		long count = 0;
		int readCount;
		byte[] buffer = new byte[1024 * 4];
		while ((readCount = input.read(buffer)) != -1) {
			output.write(buffer, 0, readCount);
			count += readCount;
		}
		output.flush();
		return count;
	}

}
