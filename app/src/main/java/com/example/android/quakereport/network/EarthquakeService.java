package com.example.android.quakereport.network;

import android.util.Log;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class EarthquakeService {
    private static final String BASE_URL = "https://earthquake.usgs.gov/";
    private static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build();


    EarthquakeApi earthquakeApi = retrofit.create(EarthquakeApi.class);

    public Observable<String> earthquakesObservable() {
        return Observable.create(x -> {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)
                    .connectTimeout(Duration.ofSeconds(60))
                    .build();

            Request request = new Request.Builder()
                    .url("https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2016-01-01&endtime=2017-01-31&minmag=6&limit=10000")
                    .get()
                    .build();
            Response response = okHttpClient.newCall(request).execute();
            String body = response.body().string();
            response.close();
            Log.i("", "body: " + body.substring(0, 100));

            x.onNext(body);
        });
    }

    public Observable<EarthquakeResponseBody> earthquakeResponseBody(
            String starttime,
            String endtime,
            String limit,
            String minMagn){
        Map<String, String> params = new HashMap<>();
        params.put("format", "geojson");
        params.put("starttime", starttime);
        params.put("endtime", endtime);
        params.put("minmag", minMagn);
        params.put("limit", limit);

        return earthquakeApi.request(params);
    }
}
