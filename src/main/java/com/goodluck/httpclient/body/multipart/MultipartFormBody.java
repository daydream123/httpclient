package com.goodluck.httpclient.body.multipart;

import com.goodluck.httpclient.OnProgressListener;
import com.goodluck.httpclient.ProgressAwareOutputStream;
import com.goodluck.httpclient.body.FileBody;
import com.goodluck.httpclient.body.HttpBody;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Accept a list of {@link WrappedFormBody} and boundary from construction parameters,
 * and it finally was used to write multipart body into OutputStream of {@link java.net.HttpURLConnection}
 */
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
		long totalLength = 0;
		if (bodyParts != null && bodyParts.size() > 0) {
			for (WrappedFormBody body : bodyParts) {
				totalLength += body.getHttpBody().getContentLength();
			}
		}
		return totalLength;
	}

	@Override
	public String getContent() {
		throw new UnsupportedOperationException("Multipart form body does not implement #getContent()");
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
			String builder = ("--" + boundary + "\r\n")
					+ "Content-Disposition: form-data; name=\"" + body.getFieldName() + "\r\n\r\n" +
					body.getHttpBody().getContent() + "\r\n";
			outputStream.write(builder.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void addBinaryPart(OutputStream outputStream, WrappedFormBody body) {
		try {
			StringBuilder builder = new StringBuilder();
			builder.append("--").append(boundary).append("\r\n");
			
			if(body.getHttpBody() instanceof FileBody){
				FileBody fileBody = (FileBody) body.getHttpBody();
				builder.append("Content-Disposition: form-data; name=\"").append(body.getFieldName()).append("\"; filename=\"").append(fileBody.getFile().getName()).append("\r\n");
			}else{
				builder.append("Content-Disposition: form-data; name=\"").append(body.getFieldName()).append("\r\n");
			}
			
			builder.append("Content-Type: ").append(body.getHttpBody().getContentType()).append("\r\n\r\n\r\n");
			outputStream.write(builder.toString().getBytes());
			
			if(body.getHttpBody() instanceof FileBody){
				FileBody fileBody = (FileBody) body.getHttpBody();
				File uploadFile = fileBody.getFile();
				OnProgressListener listener = fileBody.getProgressListener();
				
				if(listener != null){
					ProgressAwareOutputStream progressAwareOutputStream = new ProgressAwareOutputStream(
							outputStream,
							uploadFile.length(),
							fileBody.getUploadedSize());
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
