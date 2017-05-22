package com.goodluck.httpclient;

import android.content.Context;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.goodluck.httpclient.body.FileBody;
import com.goodluck.httpclient.body.JsonBody;
import com.goodluck.httpclient.body.TextBody;
import com.goodluck.httpclient.body.UrlEncodedFormBody;
import com.goodluck.httpclient.body.XmlBody;
import com.goodluck.httpclient.body.multipart.MultipartBody;
import com.goodluck.httpclient.method.GetMethod;
import com.goodluck.httpclient.method.PostMethod;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class ExampleInstrumentedTest {
    private Context mContext = InstrumentationRegistry.getTargetContext();

    private static final String RESPONSE = "done";
    private static final String TAG = "http_test";

    @Test
    public void testHttHeader() throws Exception {
        GetMethod method = new GetMethod("http://10.1.158.59:8088/header");
        method.addHeader("info", "hello world");

        HttpClient httpClient = new HttpClient();
        int code = httpClient.executeMethod(method);
        Assert.assertEquals(code, HttpURLConnection.HTTP_OK);

        String response = method.getResponseBodyAsString();
        Assert.assertEquals(response, RESPONSE);
    }

    @Test
    public void testHttpGetParams() throws Exception {
        GetMethod method = new GetMethod("http://10.1.158.59:8088/get/params");
        method.addParam("name", "hello world");
        method.addParam("age", "10");

        HttpClient httpClient = new HttpClient();

        int code = httpClient.executeMethod(method);
        Assert.assertEquals(code, HttpURLConnection.HTTP_OK);

        String response = method.getResponseBodyAsString();
        Assert.assertEquals(response, RESPONSE);
    }

    @Test
    public void testHttpPostUrlEncoded() throws IOException {
        PostMethod method = new PostMethod("http://10.1.158.59:8088/post/urlencoded");

        UrlEncodedFormBody body = new UrlEncodedFormBody();
        body.setNameValuePairs(new NameValuePair("name", "hello"), new NameValuePair("age", "111"));
        method.setBody(body);

        HttpClient httpClient = new HttpClient();
        int code = httpClient.executeMethod(method);
        Assert.assertEquals(code, HttpURLConnection.HTTP_OK);

        String response = method.getResponseBodyAsString();
        Assert.assertEquals(response, RESPONSE);
    }

    @Test
    public void testHttpPostText() throws IOException {
        PostMethod method = new PostMethod("http://10.1.158.59:8088/post/text");
        method.setBody(new TextBody("hello world, this is test log"));
        HttpClient httpClient = new HttpClient();

        int code = httpClient.executeMethod(method);
        Assert.assertEquals(code, HttpURLConnection.HTTP_OK);

        String response = method.getResponseBodyAsString();
        Assert.assertEquals(response, RESPONSE);
    }

    @Test
    public void testHttpPostJSON() throws IOException {
        PostMethod method = new PostMethod("http://10.1.158.59:8088/post/json");
        method.setBody(new JsonBody("{\"name\":\"hello\",\"age\":10}"));
        HttpClient httpClient = new HttpClient();

        int code = httpClient.executeMethod(method);
        Assert.assertEquals(code, HttpURLConnection.HTTP_OK);

        String response = method.getResponseBodyAsString();
        Assert.assertEquals(response, RESPONSE);
    }

    @Test
    public void testHttpPostFile() throws IOException {
        PostMethod method = new PostMethod("http://192.168.31.169:8088/post/file");
        method.setBody(new FileBody(Environment.getExternalStorageDirectory() + "/Download/mm.zip", 0, new OnProgressListener() {
            @Override
            public void onError(String errorMsg) {
                Log.d(TAG, "upload error: " + errorMsg);
            }

            @Override
            public void onProgress(int percentage) {
                Log.d(TAG, "upload percentage: " + percentage);
            }

            @Override
            public void onCompleted() {
                Log.d(TAG, "upload complete");
            }
        }));
        HttpClient httpClient = new HttpClient();

        int code = httpClient.executeMethod(method);
        Assert.assertEquals(code, HttpURLConnection.HTTP_OK);

        String response = method.getResponseBodyAsString();
        Assert.assertEquals(response, RESPONSE);
    }

    @Test
    public void testHttpPostMultiPart() throws IOException {
        PostMethod method = new PostMethod("http://192.168.31.169:8088/post/multipart");
        MultipartBody body = new MultipartBody();
        body.addPart("key1", new TextBody("multipart-text"));
        body.addPart("key2", new XmlBody("<html><head><title>hello world</title></head></html>"));
        body.addPart("key3", new FileBody(Environment.getExternalStorageDirectory() + "/Download/mm.zip"));

        method.setBody(body);

        HttpClient httpClient = new HttpClient();
        int code = httpClient.executeMethod(method);
        Assert.assertEquals(code, HttpURLConnection.HTTP_OK);

        String response = method.getResponseBodyAsString();
        Assert.assertEquals(response, RESPONSE);
    }

}
