package com.zf.httpclient;

import android.text.TextUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.zip.GZIPInputStream;

/**
 * Created by zhangfei on 16/4/24.
 */
public class HttpResponse {
    private HttpURLConnection connection;

    public HttpResponse(HttpURLConnection connection) {
        this.connection = connection;
    }

    public InputStream getInputStream() throws IOException {
        InputStream inputStream = connection.getInputStream();
        String encoding = connection.getContentEncoding();
        if (!TextUtils.isEmpty(encoding) && encoding.contains("gzip")) {
            return new GZIPInputStream(inputStream);
        } else {
            return inputStream;
        }
    }

    public HttpURLConnection getConnection(){
        return connection;
    }

    public void disconnect(){
        if (connection != null) {
            connection.disconnect();
        }
    }

    public int getStatusCode() throws IOException {
        return connection.getResponseCode();
    }
}
