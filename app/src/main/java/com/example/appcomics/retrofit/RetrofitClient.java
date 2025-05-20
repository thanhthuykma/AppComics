package com.example.appcomics.retrofit;


import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = "http://192.168.1.6:3000";
    private static Retrofit retrofit = null;
    public static Retrofit getClient(){
        if(retrofit == null){
            // Tạo OkHttpClient với timeout
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.MINUTES)    // timeout khi kết nối
                    .readTimeout(5, TimeUnit.MINUTES)       // timeout khi đọc dữ liệu
                    .writeTimeout(5, TimeUnit.MINUTES)      // timeout khi ghi dữ liệu
                    .build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
