package com.example.android.quakereport.network;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.List;

import lombok.Data;

@Data
public class EarthquakeResponseBody {
    private List<EarthquakeFeatureResponseBody> features;

    @Data
    public static class EarthquakeFeatureResponseBody {
        private EarthquakePropertiesResponseBody properties;
    }

    @Data
    public static class EarthquakePropertiesResponseBody{
        private Double mag;
        private String place;
        private Long time;
        private String alert;
        private String title;
        private URL url;
    }
}
