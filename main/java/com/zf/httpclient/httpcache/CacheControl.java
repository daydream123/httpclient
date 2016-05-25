package com.zf.httpclient.httpcache;

import android.net.http.HttpResponseCache;

import java.io.File;
import java.io.IOException;

/**
 * Created by zf08526 on 2015/5/27.
 */
public class CacheControl {
    private static final String TAG = "CacheControl";
    public static final String ONLY_IF_CACHE_PREFIX = "only-if-cached, max-stale=";
    public static final String VALIDATE_MAX_AGE_PREFIX = "max-age=";

    /**
     * Retrieve http response from server directly instead of cache.
     */
    public static final String NO_CACHE = "no-cache";

    /**
     * Return cache content if they are available immediately, but not otherwise.
     */
    public static String ONLY_IF_CACHED(int second){
        return ONLY_IF_CACHE_PREFIX + second;
    }

    /**
     * Always validate cache's expiration.
     */
    public static final String VALIDATE_ALWAYS = "max-age=0";

    /**
     Cached content and set expire duration.
     */
    public static String VALIDATE_MAX_AGE(int second){
        return VALIDATE_MAX_AGE_PREFIX + second;
    }

    public static void enableHttpCache(File httpCacheDir, long cacheSize) {
        try {
            HttpResponseCache.install(httpCacheDir, cacheSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
