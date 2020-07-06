package com.redhotapp.driverapp.data.source.net.interceptors;


import android.util.Log;

import com.google.firebase.crashlytics.internal.common.CommonUtils;
import com.redhotapp.driverapp.BuildConfig;
import com.redhotapp.driverapp.Constants;
import com.redhotapp.driverapp.DriverApp;

import java.io.IOException;
import java.util.Timer;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MockRespondInterceptor implements Interceptor {
    private final String TAG = "MockRespondInterceptor";

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        if (BuildConfig.DEBUG && DriverApp.nonReleaseTesting()) {
            String uri = chain.request().url().uri().toString();
            String responseString;


            if (uri.contains("commItem")) responseString = Constants.testString;
            else if (uri.contains("driver")) responseString = Constants.testStringDriver;
            else responseString = "";

            Log.d(TAG, "testing, intercepting respond, " + responseString);

            return chain.proceed(chain.request())
                    .newBuilder()
                    .code(200)
                    .protocol(Protocol.HTTP_2)
                    .message(responseString)
                    .body(ResponseBody.create(MediaType.parse("application/json"),
                            responseString))
                    .addHeader("content-type", "application/json")
                    .build();
        } else {
            //just to be on safe side.
            throw new IllegalAccessError("MockInterceptor is only meant for Testing Purposes and " +
                    "bound to be used only with DEBUG mode");
        }
    }
}
