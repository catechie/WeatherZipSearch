package com.booba.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.http.HttpResponseCache;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Set;

public class DownloadByteArrayOutputStreamUtil {
    private static final String TAG = "DownloadByteArr...Util";
    private static final int NET_CONNECT_TIMEOUT_MILLIS = 15000;
    private static final int NET_READ_TIMEOUT_MILLIS = 10000;

    public static ByteArrayOutputStream getHttpResponse(String urlString) {
        HttpURLConnection urlConnection = null;
        ByteArrayOutputStream out = null;
        BufferedInputStream in = null;

        if (urlString != null && !urlString.isEmpty()) {
            try {
                final URL url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(NET_READ_TIMEOUT_MILLIS);
                urlConnection.setConnectTimeout(NET_CONNECT_TIMEOUT_MILLIS);

                in = new BufferedInputStream(urlConnection.getInputStream());
                out = new ByteArrayOutputStream();

                int b;
                while ((b = in.read()) != -1) {

                    out.write(b);
                }
                return out;

            } catch (final IOException e) {
                Log.i(TAG, ": Error in getting http response - " + e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                try {
                    if (out != null) {
                        out.close();
                    }
                    if (in != null) {
                        in.close();
                    }
                } catch (final IOException e) {
                    Log.e(TAG, "Error occurred in IO: " + e.getMessage());
                }
            }
        }
        return null;
    }
    public static void enableHttpResponseCache(Context context) {
        try {
            File httpCacheDir = new File(context.getCacheDir(), "http");
            long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
            HttpResponseCache.install(httpCacheDir, httpCacheSize);
            Log.i(TAG, "HTTP response cache is enabled.");
        } catch (IOException e) {
            Log.i(TAG, "HTTP response cache installation failed:" + e);
        }
    }
    public static TextWatcher TextChangedWatcher(final View view) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                view.setEnabled(true);
            }
        };
    }

    public static void viewScaler(View view, float scale_factor) {
        view.setScaleX(scale_factor);
        view.setScaleY(scale_factor);
    }
/*
This is a special case to handle Set preferences changes due to a known bug in SDK, have to remove it first then add it back on
 */
    public static boolean applyPrefSetChanges(String prefName, Set<String> prefValueSet, SharedPreferences preferencesRef) {
        SharedPreferences.Editor editor = preferencesRef.edit();
        editor.remove(prefName); //have to do this before effectively save the new value
        editor.apply();
        editor.putStringSet(prefName, prefValueSet);
        editor.apply();
        return editor.commit();
    }

    public static void LogHttpCacheHits(String tag) {
        HttpResponseCache httpResponseCache = HttpResponseCache.getInstalled();
        if(httpResponseCache!=null) {
            Log.i(tag, "HttpResponseCache cache hit count: " + httpResponseCache.getHitCount());
            Log.i(tag, "HttpResponseCache total request count: " + httpResponseCache.getRequestCount());
            Log.i(tag, "HttpResponseCache total network call count: " + httpResponseCache.getNetworkCount());
        }
    }
}
