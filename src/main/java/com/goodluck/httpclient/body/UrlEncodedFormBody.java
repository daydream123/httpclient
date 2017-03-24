package com.goodluck.httpclient.body;

import com.goodluck.httpclient.ContentType;
import com.goodluck.httpclient.NameValuePair;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UrlEncodedFormBody extends HttpBody {
	private List<NameValuePair> nameValuePairs = new ArrayList<>();
	
	@Override
	public String getContentType() {
		return ContentType.APPLICATION_FORM_URLENCODED;
	}

	public void setNameValuePairs(List<NameValuePair> nameValuePairs) {
		this.nameValuePairs = nameValuePairs;
	}

	public void setNameValuePairs(NameValuePair ... nameValuePairs) {
		this.nameValuePairs.addAll(Arrays.asList(nameValuePairs));
	}

	@Override
	public String getContent() {
		try {
			return buildFormDataParams();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		}
	}

	@Override
	public long getContentLength() {
		try {
			String content = buildFormDataParams();
			return content.getBytes().length;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public void writeTo(OutputStream outputStream) throws IOException {
		String dataToWrite = buildFormDataParams();
		if(dataToWrite != null && dataToWrite.length() > 0){
			outputStream.write(dataToWrite.getBytes());
			outputStream.flush();
		}
	}

	@Override
	public boolean isStreaming() {
		return false;
	}

	private String buildFormDataParams() throws UnsupportedEncodingException {
		if(nameValuePairs != null && nameValuePairs.size() > 0){
			StringBuilder builder = new StringBuilder();
			for (NameValuePair nameValue : nameValuePairs) {
				String encodedKey = URLEncoder.encode(nameValue.getName(), "utf-8");
				String encodedValue = URLEncoder.encode(nameValue.getValue(), "utf-8");
				builder.append(encodedKey + "=" + encodedValue + "&");
			}
			return builder.substring(0, builder.length() - 1);
		}
		
		return "";
	}
}
