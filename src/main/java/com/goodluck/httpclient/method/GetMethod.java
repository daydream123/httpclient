package com.goodluck.httpclient.method;

public class GetMethod extends HttpMethod {
	public static final String NAME = "GET";

	public GetMethod(String url) {
		super(url);
	}
	
	@Override
	public String getName() {
		return NAME;
	}
}
