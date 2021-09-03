package com.example.android.quakereport.network;

import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface EarthquakeApi {
    @GET("fdsnws/event/1/query")
    Observable<EarthquakeResponseBody> request(@QueryMap Map<String, String> params);
}
