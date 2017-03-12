package com.goodluck.httpclient.params;

/**
 * Created by zhangfei on 2017/3/12.
 */

public abstract class HttpParam {
    private String name;
    private String value;

    public HttpParam(String name, String value){
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue(){
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HttpParam)) return false;

        HttpParam param = (HttpParam) o;

        return name.equals(param.name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
