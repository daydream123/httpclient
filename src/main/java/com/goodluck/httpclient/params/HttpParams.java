package com.goodluck.httpclient.params;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangfei on 2017/3/12.
 */

public class HttpParams {
    private List<HttpParam> httpParams = new ArrayList<>();

    public void addHttpParam(HttpParam httpParam) {
        httpParams.add(httpParam);
    }

    public List<HttpParam> getParams(){
        return httpParams;
    }

}
