package com.zf.httpclient.body.multipart;

import com.zf.httpclient.body.HttpBody;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Random;

public class MultipartBody extends HttpBody {

	/**
	 * The pool of ASCII chars to be used for generating a multipart boundary.
	 */
	private final static char[] MULTIPART_CHARS = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
			.toCharArray();

	private final MultipartBodyBuilder builder;
	private String boundary;
	private String contentType;

	public MultipartBody() {
		this.boundary = generateBoundary();
		this.contentType = generateContentType();
		this.builder = new MultipartBodyBuilder(boundary);
	}

	protected String generateContentType() {
		final StringBuilder buffer = new StringBuilder();
		buffer.append("multipart/form-data; boundary=");
		buffer.append(boundary);
		buffer.append("; charset=");
		buffer.append(Charset.defaultCharset().name());
		return buffer.toString();
	}

	protected String generateBoundary() {
        final StringBuilder buffer = new StringBuilder();
        final Random rand = new Random();
        final int count = rand.nextInt(11) + 30; // a random size from 30 to 40
        for (int i = 0; i < count; i++) {
            buffer.append(MULTIPART_CHARS[rand.nextInt(MULTIPART_CHARS.length)]);
        }
        return buffer.toString();
    }

	public MultipartBodyBuilder addPart(String key, HttpBody httpBody) {
		return builder.addPart(key, httpBody);
	}

	public HttpBody getBody() {
		return builder.build();
	}

	public long getContentLength() {
		return getBody().getContentLength();
	}

	public String getContent() {
		throw new UnsupportedOperationException("Multipart form body does not implement #getContent()");
	}

	public void writeTo(final OutputStream outstream) throws IOException {
		getBody().writeTo(outstream);
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public boolean isStreaming() {
		return getBody().isStreaming();
	}

}
