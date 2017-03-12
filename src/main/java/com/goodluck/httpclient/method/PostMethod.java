package com.goodluck.httpclient.method;

import com.goodluck.httpclient.body.HttpBody;

public class PostMethod extends HttpMethod {
	public static final String NAME = "POST";
	private HttpBody httpBody;

	public PostMethod(String url) {
		super(url);
	}

	@Override
	public String getName() {
		return NAME;
	}
	
	public <T extends HttpBody> void setBody(T httpBody) {
		this.httpBody = httpBody;
	}

	public HttpBody getBody() {
		return httpBody;
	}

}
