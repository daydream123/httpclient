package com.goodluck.httpclient.body;

import com.goodluck.httpclient.ContentType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class InputStreamBody extends HttpBody {
	protected final InputStream inputStream;

	public InputStreamBody(InputStream inputStream) {
		this.inputStream = inputStream;
	}
	
	@Override
	public String getContentType() {
		return ContentType.DEFAULT_BINARY;
	}

	@Override
	public String getContent() {
		throw new UnsupportedOperationException("InputStreamBody does not implement #getContent().");
	}

	@Override
	public long getContentLength() {
		try {
			return inputStream.available();
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public void writeTo(OutputStream outputStream) throws IOException {
		copy(inputStream, outputStream);
		outputStream.flush();
	}
	
	@Override
	public boolean isStreaming() {
		return true;
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
