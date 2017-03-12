package com.goodluck.httpclient;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.goodluck.httpclient.body.TextBody;
import com.goodluck.httpclient.httpclient.HttpClient;
import com.goodluck.httpclient.method.PostMethod;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class ExampleInstrumentedTest {
    private Context mContext = InstrumentationRegistry.getTargetContext();

    @Test
    public void testHttpGet() throws Exception {
        PostMethod method = new PostMethod("http://192.168.31.169:8088/crashLog/save");
        method.setBody(new TextBody("hello world, this is test log"));
        HttpClient httpClient = new HttpClient();

        int code = httpClient.executeMethod(method);
        Log.d("tag", "code:" + code);
    }
}
