package com.goodluck.httpclient.body.multipart;

import com.goodluck.httpclient.body.HttpBody;

import java.util.ArrayList;
import java.util.List;

class MultipartBodyBuilder {
	private List<WrappedFormBody> bodyParts = new ArrayList<>();
	private String boundary;

	public MultipartBodyBuilder(String boundary) {
		super();
		this.boundary = boundary;
	}

	public MultipartBodyBuilder addPart(String fieldName, final HttpBody bodyPart) {
		if (bodyPart == null) {
			return this;
		}
		this.bodyParts.add(new WrappedFormBody(fieldName, bodyPart));
		return this;
	}

	public HttpBody build() {
		return new MultipartFormBody(boundary, bodyParts);
	}

}
