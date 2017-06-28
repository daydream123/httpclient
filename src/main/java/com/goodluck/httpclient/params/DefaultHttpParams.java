package com.goodluck.httpclient.params;
import android.os.Build;

/**
 * Created by zhangfei on 2017/3/12.
 */

public class DefaultHttpParams extends HttpParams {

    public DefaultHttpParams(){
        // have bugs in early os version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            addHttpParam(new KeepAlive());
        }

        addHttpParam(new CacheControl("no-cache"));
    }
}
