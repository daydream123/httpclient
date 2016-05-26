package com.zf.android.httpclient.method;

import com.zf.android.httpclient.body.HttpBody;

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
	public String getRequest() {
		if(httpBody == null){
			return "";
		}
		return httpBody.getContent();
	}
	
	public <T extends HttpBody> void setBody(T httpBody) {
		this.httpBody = httpBody;
	}

	public HttpBody getBody() {
		return httpBody;
	}

}
