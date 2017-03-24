package com.goodluck.httpclient.method;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public abstract class HttpMethod {
    protected String url;
    protected Map<String, String> headers = new HashMap<>();

    private HttpURLConnection connection = null;

    public HttpMethod(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public abstract String getName();

    public abstract URL buildURL() throws MalformedURLException;

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

    public void setConnection(HttpURLConnection connection) {
        this.connection = connection;
    }

    /**
     * Release the execution of this method.
     */
    public void releaseConnection() {
        if (connection == null) {
            return;
        }

        connection.disconnect();
    }

    /**
     * Returns the response status code.
     *
     * @return the status code associated with the latest response.
     */
    public int getStatusCode() throws IOException {
        if (connection == null) {
            return -1;
        }

        return connection.getResponseCode();
    }

    public InputStream getResponseBodyAsStream() throws IOException {
        if (connection != null) {
            return connection.getInputStream();
        }
        return null;
    }

    public byte[] getResponseBody() throws IOException {
        if (connection == null) {
            return null;
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int len;

        InputStream inputStream = connection.getInputStream();
        while ((len = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, len);
        }
        outputStream.close();
        return outputStream.toByteArray();
    }

    public String getResponseBodyAsString() throws IOException {
        byte[] body = getResponseBody();
        if (body == null) {
            return "";
        }

        return new String(body, Charset.forName("utf-8"));
    }
}
