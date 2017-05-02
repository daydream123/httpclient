package com.goodluck.httpclient.body.multipart;

import com.goodluck.httpclient.body.HttpBody;

import java.util.ArrayList;
import java.util.List;

/**
 * A builder like StringBuilder but it's data type is WrappedFormBody
 * and allowed to convert it into {@link MultipartFormBody} through {@link #build()}.
 */
class MultipartBodyBuilder {
	private List<WrappedFormBody> bodyParts = new ArrayList<>();
	private String boundary;

	public MultipartBodyBuilder(String boundary) {
		super();
		this.boundary = boundary;
	}

	public MultipartBodyBuilder addPart(String fieldName, final HttpBody bodyPart) {
		if (bodyPart == null) {
			throw new IllegalArgumentException("body part cannot be null");
		}

		if (bodyPart instanceof MultipartBody) {
			throw new IllegalArgumentException("multipart body cannot accept anther multipart body");
		}
		this.bodyParts.add(new WrappedFormBody(fieldName, bodyPart));
		return this;
	}

	public HttpBody build() {
		return new MultipartFormBody(boundary, bodyParts);
	}

}
