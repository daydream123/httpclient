package com.zf.android.httpclient.body;

import com.zf.android.httpclient.ContentType;

public class XmlBody extends TextBody {
	private String xmlType = ContentType.APPLICATION_XML;

	public XmlBody(String text) {
		super(text);
	}
	
	public void setXmlType(String xmlType){
		this.xmlType = xmlType;
	}

	@Override
	public String getContent() {
		return xmlType;
	}
}
