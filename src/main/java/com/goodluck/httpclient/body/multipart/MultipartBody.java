package com.goodluck.httpclient.body.multipart;

import com.goodluck.httpclient.body.HttpBody;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

public class MultipartBody extends HttpBody {

	/**
	 * The pool of ASCII chars to be used for generating a multipart boundary.
	 */
	private final static char[] MULTIPART_CHARS = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
			.toCharArray();

	private final MultipartBodyBuilder builder;
	private String contentType;

	public MultipartBody() {
		String boundary = generateBoundary();
		this.contentType = buildContentType(boundary);
		this.builder = new MultipartBodyBuilder(boundary);
	}

	private String buildContentType(String boundary) {
		String contentType = "multipart/form-data; boundary=";
		contentType += "----" + boundary;
		return contentType;
	}

	private String generateBoundary() {
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

	@Override
	public long getContentLength() {
		return getBody().getContentLength();
	}

	@Override
	public String getContent() {
		throw new UnsupportedOperationException("Multipart form body does not implement #getContent()");
	}

	@Override
	public void writeTo(final OutputStream outputStream) throws IOException {
		getBody().writeTo(outputStream);
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
