package com.goodluck.httpclient.method;

import com.goodluck.httpclient.body.HttpBody;

import java.net.MalformedURLException;
import java.net.URL;

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

	@Override
	public URL buildURL() throws MalformedURLException {
		return new URL(url);
	}

	public <T extends HttpBody> void setBody(T httpBody) {
		this.httpBody = httpBody;
	}

	public HttpBody getBody() {
		return httpBody;
	}

}
