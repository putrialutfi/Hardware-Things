package com.putrialutfi.hardwarethings.API;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "http://192.168.43.75/hardware/";
    private static Retrofit mRetrofit;

    public static Retrofit getApiClient() {
        if (mRetrofit == null) {
            mRetrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
        }
        return mRetrofit;
    }
}
