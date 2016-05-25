package com.zf.httpclient.body.multipart;

import com.zf.httpclient.OnProgressListener;
import com.zf.httpclient.ProgressAwareOutputStream;
import com.zf.httpclient.body.FileBody;
import com.zf.httpclient.body.HttpBody;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

class MultipartFormBody extends HttpBody {
	private List<WrappedFormBody> bodyParts;
	private String boundary;

	public MultipartFormBody(String boundary, List<WrappedFormBody> bodyParts) {
		this.boundary = boundary;
		this.bodyParts = bodyParts;
	}

	@Override
	public String getContentType() {
		throw new UnsupportedOperationException("Multipart form body does not implement #getConentType()");
	}

	@Override
	public long getContentLength() {
		throw new UnsupportedOperationException("Multipart form body does not implement #getContentLength()");
	}

	@Override
	public String getContent() {
		throw new UnsupportedOperationException("Multipart form body does not implement #getContent()");
	}

	public List<WrappedFormBody> getBodyPorts() {
		return bodyParts;
	}

	@Override
	public void writeTo(OutputStream outputStream) throws IOException {
		if (bodyParts != null && bodyParts.size() > 0) {
			for (WrappedFormBody body : bodyParts) {
				if(body.getHttpBody().isStreaming()){
					addBinaryPart(outputStream, body);
				}else{
					addTextPart(outputStream, body);
				}
			}
			outputStream.write(("--" + boundary + "--\r\n").getBytes());
			outputStream.flush();
		}
	}

	@Override
	public boolean isStreaming() {
		for(WrappedFormBody body : bodyParts){
			if(body.getHttpBody().isStreaming()){
				return true;
			}
		}
		return false;
	}

	private void addTextPart(OutputStream outputStream, WrappedFormBody body) {
		try {
			StringBuilder builder = new StringBuilder();
			builder.append("--" + boundary + "\r\n");
			builder.append("Content-Disposition: form-data; name=\"" + body.getFieldName() + "\r\n");
			builder.append("\r\n");
			builder.append(body.getHttpBody().getContent());
			builder.append("\r\n");
			outputStream.write(builder.toString().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void addBinaryPart(OutputStream outputStream, WrappedFormBody body) {
		try {
			StringBuilder builder = new StringBuilder();
			builder.append("--" + boundary + "\r\n");
			
			if(body.getHttpBody() instanceof FileBody){
				FileBody fileBody = (FileBody) body.getHttpBody();
				builder.append("Content-Disposition: form-data; name=\"" + body.getFieldName() + "\"; filename=\"" + fileBody.getFile().getName() + "\r\n");
			}else{
				builder.append("Content-Disposition: form-data; name=\"" + body.getFieldName() + "\";\r\n");
			}
			
			builder.append("Content-Type: " + body.getHttpBody().getContentType() + "\r\n");
			builder.append("\r\n");
			outputStream.write(builder.toString().getBytes());
			
			if(body.getHttpBody() instanceof FileBody){
				FileBody fileBody = (FileBody) body.getHttpBody();
				File uploadFile = fileBody.getFile();
				OnProgressListener listener = fileBody.getProgressListener();
				
				if(listener != null){
					ProgressAwareOutputStream progressAwareOutputStream = new ProgressAwareOutputStream(outputStream,
							uploadFile.length(), fileBody.getUploadedSize(), uploadFile.getAbsolutePath());
					progressAwareOutputStream.setOnProgressListener(listener);
					body.getHttpBody().writeTo(progressAwareOutputStream);
				}else{
					body.getHttpBody().writeTo(outputStream);
				}
			}else{
				body.getHttpBody().writeTo(outputStream);				
			}
			outputStream.write("\r\n".getBytes());
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
	
}
