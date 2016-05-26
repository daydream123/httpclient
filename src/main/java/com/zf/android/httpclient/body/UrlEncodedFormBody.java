package com.zf.android.httpclient.body;

import com.zf.android.httpclient.ContentType;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class UrlEncodedFormBody extends TextBody {
	private Map<String, String> formData;
	
	public UrlEncodedFormBody(){
		super("");
		this.formData = new HashMap<String, String>();
	}
	
	@Override
	public String getContentType() {
		return ContentType.APPLICATION_FORM_URLENCODED;
	}

	public void addFormData(String name, String value) {
		formData.put(name, value);
	}

	public void addAllFormData(Map<String, String> formData) {
		formData.putAll(formData);
	}
	
	public void setFormData(String name, String value){
		formData.clear();
		formData.put(name, value);
	}
	
	public void setFormData(Map<String, String> formData){
		formData.clear();
		formData.putAll(formData);
	}

	public String removeFormData(String name) {
		return formData.remove(name);
	}
	
	@Override
	public String getContent() {
		try {
			return text = buildFormDataParams(formData);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void writeTo(OutputStream outputStream) throws IOException {
		String dataToWrite = buildFormDataParams(formData);
		if(dataToWrite != null && dataToWrite.length() > 0){
			outputStream.write(dataToWrite.getBytes());
			outputStream.flush();
		}
	}
	
	private String buildFormDataParams(Map<String, String> properties) throws UnsupportedEncodingException {
		if(properties != null && properties.size() > 0){
			StringBuilder builder = new StringBuilder();
			for (String key : properties.keySet()) {
				String encodedKey = URLEncoder.encode(key, "utf-8");
				String encodedValue = URLEncoder.encode(properties.get(key), "utf-8");
				builder.append(encodedKey + "=" + encodedValue + "&");
			}
			return builder.substring(0, builder.length() - 1);
		}
		
		return "";
	}
}
