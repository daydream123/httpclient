package com.goodluck.httpclient;

import android.os.Build;

import com.goodluck.httpclient.body.HttpBody;
import com.goodluck.httpclient.method.HttpMethod;
import com.goodluck.httpclient.method.PostMethod;
import com.goodluck.httpclient.params.DefaultHttpParams;
import com.goodluck.httpclient.params.HttpParam;
import com.goodluck.httpclient.params.HttpParams;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by zhangfei on 2017/3/12.
 */

public class HttpClient {
    private static final String TAG = "httpclient";

    private int timeout;

    private HttpParams httpParams = new DefaultHttpParams();

    public void setHttpParams(HttpParams httpParams) {
        this.httpParams = httpParams;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * Called once http url connection was established.
     *
     * @param connection
     */
    protected void onUrlConnectionEstablished(HttpURLConnection connection) {
    }

    public int executeMethod(HttpMethod httpMethod) throws IOException {
        URL url = httpMethod.buildURL();

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        onUrlConnectionEstablished(connection);

        // only "POST" method need setDoInput(true) and setDoOutput(true)
        if (PostMethod.NAME.equals(httpMethod.getName())) {
            connection.setDoInput(true);
            connection.setDoOutput(true);
        }

        connection.setRequestMethod(httpMethod.getName());
        connection.setUseCaches(false);
        connection.setInstanceFollowRedirects(false);

        connection.setReadTimeout(timeout);
        connection.setConnectTimeout(timeout);

        for (HttpParam param : httpParams.getParams()) {
            connection.setRequestProperty(param.getName(), param.getValue());
        }

        if (PostMethod.NAME.equals(httpMethod.getName())) {
            // set content type for POST
            PostMethod httpPost = (PostMethod) httpMethod;
            HttpBody httpBody = httpPost.getBody();
            connection.setRequestProperty("content-type", httpBody.getContentType());
            connection.setRequestProperty("content-length", String.valueOf(httpBody.getContentLength()));

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

        httpMethod.setConnection(connection);
        return httpMethod.getStatusCode();
    }

    /**
     * Trust all host and ignore certificate.
     *
     * @param connection http url connection
     */
    public void trustAllHosts(HttpURLConnection connection) {
        if (connection instanceof HttpsURLConnection) {
            HttpsURLConnection httpsConnection = (HttpsURLConnection) connection;
            httpsConnection.setHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            // Create a trust manager that does not validate certificate chains Android use X509 cert
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[]{};
                }

                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }
            }};

            // install the all-trusting trust manager
            try {
                SSLContext sc = SSLContext.getInstance("TLS");
                sc.init(null, trustAllCerts, new java.security.SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
