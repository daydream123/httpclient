package com.zf.httpclient.method;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public abstract class HttpMethod {
    public static final int CONNECTION_TIME_OUT = 1000 * 40;
    public static final int READ_TIME_OUT = 1000 * 40;

    protected String url;
    protected Map<String, String> headers;
    private String charset = Charset.defaultCharset().name();
    private String userAgent;
    private String cacheControl;

    private int connectTimeout = CONNECTION_TIME_OUT;
    private int readTimeout = READ_TIME_OUT;
    protected boolean haveResponse = true;
    private boolean acceptEncoding = false;
    private boolean keepAlive = false;

    public HttpMethod(String url) {
        this.url = url;
        this.headers = new HashMap<String, String>();
    }

    public String getUrl() {
        return url;
    }

    public abstract String getName();

    public abstract String getRequest();

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getCacheControl() {
        return cacheControl;
    }

    public void setCacheControl(String cacheControl) {
        this.cacheControl = cacheControl;
    }

    public boolean isAcceptEncoding() {
        return acceptEncoding;
    }

    public void setAcceptEncoding(boolean acceptEncoding) {
        this.acceptEncoding = acceptEncoding;
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public void setHeader(String name, String value) {
        this.headers.clear();
        this.headers.put(name, value);
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers.clear();
        this.headers.putAll(headers);
    }

    public void addHeader(String name, String value) {
        this.headers.put(name, value);
    }

    public void addHeaders(Map<String, String> headers) {
        this.headers.putAll(headers);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public boolean haveResponse() {
        return haveResponse;
    }

    public void haveResponse(boolean haveResponse) {
        this.haveResponse = haveResponse;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

}
