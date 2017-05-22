package com.goodluck.httpclient.body;

import com.goodluck.httpclient.ContentType;

import java.io.IOException;
import java.io.OutputStream;

public class TextBody extends HttpBody {
	protected String text;
	
	public TextBody(String text) {
		this.text = text;
	}
	
	@Override
	public String getContentType() {
		return ContentType.DEFAULT_TEXT;
	}

	@Override
	public String getContent() {
		return text;
	}

	@Override
	public long getContentLength() {
		return text.getBytes().length;
	}

	@Override
	public void writeTo(OutputStream outputStream) throws IOException {
		outputStream.write(text.getBytes());
		outputStream.flush();
	}

	@Override
	public boolean isStreaming() {
		return false;
	}

}
