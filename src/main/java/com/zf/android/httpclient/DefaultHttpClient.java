package com.zf.android.httpclient;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.zf.android.httpclient.body.HttpBody;
import com.zf.android.httpclient.method.GetMethod;
import com.zf.android.httpclient.method.HttpMethod;
import com.zf.android.httpclient.method.PostMethod;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class DefaultHttpClient {
    private static final String TAG = "http client";
    protected final int CACHE_SIZE = 10 * 1024 * 1024; // 10 MiB

    public HttpResponse execute(HttpMethod httpMethod) throws IOException {
        URL url;
        if (GetMethod.NAME.equals(httpMethod.getName())) {
            url = new URL(httpMethod.getUrl() + "?" + httpMethod.getRequest());
        } else {
            url = new URL(httpMethod.getUrl());
        }

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // only "POST" method need setDoInput(true) and setDoOutput(true)
        if (PostMethod.NAME.equals(httpMethod.getName())) {
            connection.setDoInput(true);
            connection.setDoOutput(true);
        }

        connection.setRequestMethod(httpMethod.getName());
        connection.setUseCaches(false);
        connection.setInstanceFollowRedirects(false);
        connection.setReadTimeout(httpMethod.getReadTimeout());
        connection.setConnectTimeout(httpMethod.getConnectTimeout());
        connection.setRequestProperty("Charset", httpMethod.getCharset());

        if (httpMethod.isKeepAlive()) {
            connection.setRequestProperty("Connection", "Keep-Alive");
        }

        if (!TextUtils.isEmpty(httpMethod.getCacheControl())) {
            connection.setRequestProperty("Cache-control", httpMethod.getCacheControl());
        }

        if (!TextUtils.isEmpty(httpMethod.getUserAgent())) {
            connection.setRequestProperty("User-Agent", httpMethod.getUserAgent());
        }

        if (httpMethod.isAcceptEncoding()) {
            connection.setRequestProperty("Accept-Encoding", "gzip");
        }

        if (PostMethod.NAME.equals(httpMethod.getName())) {
            // set content type for POST
            PostMethod httpPost = (PostMethod) httpMethod;
            HttpBody httpBody = httpPost.getBody();
            connection.setRequestProperty("Content-Type", httpBody.getContentType());
            connection.setRequestProperty("Content-Length", String.valueOf(httpBody.getContentLength()));

            // disable cache for write output stream
            if (httpBody.isStreaming()) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                    connection.setFixedLengthStreamingMode(0);
                } else {
                    connection.setChunkedStreamingMode(0);
                }
            }
        }

        // set extra headers
        Map<String, String> headers = httpMethod.getHeaders();
        if (headers != null && headers.size() > 0) {
            for (String key : headers.keySet()) {
                connection.setRequestProperty(key, headers.get(key));
            }
        }

        // write data for POST
        if (PostMethod.NAME.equals(httpMethod.getName())) {
            // do connect
            connection.connect();

            // write request
            PostMethod httpPost = (PostMethod) httpMethod;
            HttpBody httpBody = httpPost.getBody();
            httpBody.writeTo(connection.getOutputStream());
        }

        return new HttpResponse(connection);
    }

    protected void printLog(HttpMethod httpMethod, String response) {
        String request = null;
        try {
            request = new String(httpMethod.getRequest());
        } catch (UnsupportedOperationException e) {
            // since some body are streaming, have no text content
        }

        if (request != null && request.length() > 0) {
            Log.d(TAG, "http url:" + httpMethod.getUrl());
            Log.d(TAG, "http request:" + request);
            Log.d(TAG, "http response:" + response);
        }
    }

    protected void printLog(HttpMethod httpMethod) {
        String request = null;
        try {
            request = new String(httpMethod.getRequest());
        } catch (UnsupportedOperationException e) {
            // since some body are streaming, have no text content
        }

        if (request != null && request.length() > 0) {
            Log.d(TAG, "http url:" + httpMethod.getUrl());
            Log.d(TAG, "http request:" + request);
        }
    }

}
