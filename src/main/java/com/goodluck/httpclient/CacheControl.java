package com.goodluck.httpclient;

import android.annotation.TargetApi;
import android.net.http.HttpResponseCache;
import android.os.Build;
import android.util.Log;

import java.io.File;

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

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static void enableHttpCache(long cacheSize, File httpCacheDir) {
        try {
            Class.forName("android.net.http.HttpResponseCache").getMethod("install", File.class, long.class).invoke(null, httpCacheDir, cacheSize);
        } catch (Exception httpResponseCacheNotAvailable) {
            Log.d(TAG, "android.net.http.HttpResponseCache not available, " +
                    "probably because we're running on a pre-ICS version of Android." +
                    "Using com.integralblue.httpresponsecache.HttpHttpResponseCache.");
            try{
                HttpResponseCache.install(httpCacheDir, cacheSize);
            }catch(Exception e){
                Log.e(TAG, "Failed to set up com.integralblue.httpresponsecache.HttpResponseCache");
            }
        }
    }
}
