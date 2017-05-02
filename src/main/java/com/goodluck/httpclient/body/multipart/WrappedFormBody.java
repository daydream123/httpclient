package com.goodluck.httpclient.body.multipart;

import com.goodluck.httpclient.body.HttpBody;

/**
 * This is part of {@link MultipartFormBody} as a list.
 */
class WrappedFormBody {
	private String fieldName;
	private HttpBody httpBody;

	public WrappedFormBody(String fieldName, HttpBody httpBody) {
		this.fieldName = fieldName;
		this.httpBody = httpBody;
	}

	public HttpBody getHttpBody() {
		return httpBody;
	}

	public void setHttpBody(HttpBody httpBody) {
		this.httpBody = httpBody;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

}
