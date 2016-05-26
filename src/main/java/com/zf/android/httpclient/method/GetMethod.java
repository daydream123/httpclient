package com.zf.android.httpclient.method;

import java.util.HashMap;
import java.util.Map;

public class GetMethod extends HttpMethod {
	public static final String NAME = "GET";
	protected Map<String, String> formData;

	public GetMethod(String url) {
		super(url);
		this.formData = new HashMap<>();
	}
	
	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getRequest() {
		return buildGetRequest(formData);
	}
	
	public void setFormData(String name, String value) {
		formData.clear();
		formData.put(name, value);
	}
	
	public void setFormData(Map<String, String> formData){
		this.formData.clear();
		this.formData.putAll(formData);
	}
	
	public void addFormData(String name, String value){
		formData.put(name, value);
	}
	
	public void addFormData(Map<String, String> formData){
		this.formData.putAll(formData);
	}
	
	public Map<String, String> getFormData(){
		return formData;
	}
	
	private String buildGetRequest(Map<String, String> properties) {
		if(properties != null && properties.size() > 0){
			StringBuilder builder = new StringBuilder();
			for (String key : properties.keySet()) {
				builder.append(key + "=" + properties.get(key) + "&");
			}
			return builder.substring(0, builder.length() - 1);
		}else{
			return "";
		}
	}

}
