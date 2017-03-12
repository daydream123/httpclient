package com.goodluck.httpclient.params;

/**
 * Created by zhangfei on 2017/3/12.
 */

public class CacheControl extends HttpParam {

    public CacheControl(String value) {
        super("cache-control", value);
    }
}
