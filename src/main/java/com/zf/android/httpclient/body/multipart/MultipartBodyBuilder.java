package com.zf.android.httpclient.body.multipart;
import com.zf.android.httpclient.body.HttpBody;

import java.util.ArrayList;
import java.util.List;

class MultipartBodyBuilder {
	private List<WrappedFormBody> bodyParts = null;
	private String boundary;

	public static MultipartBodyBuilder create(String boundary) {
		return new MultipartBodyBuilder(boundary);
	}

	MultipartBodyBuilder(String boundary) {
		super();
		this.boundary = boundary;
	}

	public MultipartBodyBuilder addPart(String fieldName, final HttpBody bodyPart) {
		if (bodyPart == null) {
			return this;
		}
		if (this.bodyParts == null) {
			this.bodyParts = new ArrayList<WrappedFormBody>();
		}
		this.bodyParts.add(new WrappedFormBody(fieldName, bodyPart));
		return this;
	}

	public HttpBody build() {
		return new MultipartFormBody(boundary, bodyParts);
	}

}
