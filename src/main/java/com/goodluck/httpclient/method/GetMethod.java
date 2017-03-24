package com.goodluck.httpclient.method;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class GetMethod extends HttpMethod {
	public static final String NAME = "GET";

	private Map<String, String> params = new HashMap<>();

	public GetMethod(String url) {
		super(url);
	}
	
	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public URL buildURL() throws MalformedURLException {
		if (params == null || params.size() == 0) {
			return new URL(url);
		}

		StringBuilder builder = new StringBuilder();
		for (String key : params.keySet()) {
			builder.append(key + "=" + params.get(key) + "&");
		}
		return new URL(url + "?" + builder.substring(0, builder.length() - 1));
	}

	public void setParam(String name, String value) {
		params.clear();
		params.put(name, value);
	}

	public void setParams(Map<String, String> formData){
		this.params.clear();
		this.params.putAll(formData);
	}

	public void addParam(String name, String value){
		params.put(name, value);
	}

	public void addParams(Map<String, String> formData){
		this.params.putAll(formData);
	}

	public Map<String, String> getParams(){
		return params;
	}
}
