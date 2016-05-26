package com.zf.android.httpclient.body;

import com.zf.android.httpclient.ContentType;

import java.io.IOException;
import java.io.OutputStream;

public abstract class HttpBody {

	public HttpBody() {
	}

	/**
	 * MIMI-TYPE @see {@link ContentType}
	 */
	public abstract String getContentType();

	public abstract long getContentLength();

	public abstract String getContent() throws UnsupportedOperationException;

	/**
	 * Write request body content(Text, JSON, XML or bytes of File) into
	 * OutputStream of HttpUrlConnection.
	 */
	public abstract void writeTo(final OutputStream outputStream) throws IOException;

	/**
	 * If it was stream request like File, Byte, InputStream and so on, the
	 * default cache should be set disabled before write data, otherwise cannot
	 * know the real transmission speed.
	 * 
	 * @return whether stream request or not.
	 */
	public abstract boolean isStreaming();

}
