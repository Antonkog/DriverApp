package com.redhotapp.driverapp.data.source.net.interceptors;


import com.redhotapp.driverapp.R;
import com.redhotapp.driverapp.data.source.net.exceptions.AbonaHttpException;
import com.redhotapp.driverapp.data.source.net.exceptions.NotFoundException;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class ServerErrorsInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        if (response.code() != 200 || response.body() == null) {
            String error = response.headers().get("Response-Message");
            if (error == null)
                error = "server error"; // App.getInstance().getResources().getString(R.string.error_server);
            if(response.code() == 404){
                throw new NotFoundException(response.code(), error);
            }else {
                throw new AbonaHttpException(response.code(), error);
            }
        }
        return response;
    }
}
