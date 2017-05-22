package com.goodluck.httpclient.params;

/**
 * Created by zhangfei on 2017/3/12.
 */

public class KeepAlive extends HttpParam {

    public KeepAlive() {
        super("connection", "keep-alive");
    }
}
