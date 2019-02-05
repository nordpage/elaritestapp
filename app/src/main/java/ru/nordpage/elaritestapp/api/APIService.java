package ru.nordpage.elaritestapp.api;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface APIService {

    @FormUrlEncoded
    @POST("adviator/index.php")
    Observable<Response> sendRequest(@Field("id") String id);
}
