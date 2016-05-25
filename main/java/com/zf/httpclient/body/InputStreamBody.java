package com.zf.httpclient.body;

import com.zf.httpclient.ContentType;
import com.zf.httpclient.IOUtils;

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
		IOUtils.copy(inputStream, outputStream);
		outputStream.flush();
	}
	
	@Override
	public boolean isStreaming() {
		return true;
	}
}
