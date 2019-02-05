package ru.nordpage.elaritestapp.api;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.nordpage.elaritestapp.utils.Constant;

public class Service {
    private static Retrofit.Builder builder = new Retrofit.Builder().baseUrl(Constant.API_BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create());
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
    private static Retrofit retrofit = builder.build();
    public static APIService service;

    public static APIService getService() {
        new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.client(httpClient.build());
        retrofit = builder.build();
        APIService aPIService = retrofit.create(APIService.class);
        service = aPIService;
        return aPIService;
    }
}