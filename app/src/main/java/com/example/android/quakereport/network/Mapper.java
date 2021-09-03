package com.example.android.quakereport.network;

import com.example.android.quakereport.Earthquake;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Mapper {
    private static Gson gson = new GsonBuilder().create();



    public static List<Earthquake> fromJsonToListPojos(EarthquakeResponseBody json){
        return getEarthquakes(json);
    }

    public static List<Earthquake> fromJsonToListPojos(String json){
        EarthquakeResponseBody responseBody = gson.fromJson(json, EarthquakeResponseBody.class);

        return getEarthquakes(responseBody);
    }

    private static List<Earthquake> getEarthquakes(EarthquakeResponseBody responseBody) {
        Pattern pattern = Pattern.compile("(.* of)?(.*)");

        return responseBody.getFeatures()
                .stream()
                .map(x -> {

                    String approxLocation = "";
                    String location = "";

                    if (x.getProperties().getPlace() != null) {
                        Matcher matcher = pattern.matcher(x.getProperties().getPlace());
                        matcher.matches();

                        approxLocation = matcher.group(1);
                        location = matcher.group(2);

                        if (approxLocation == null || approxLocation.equals("")) {
                            approxLocation = "0 km of";
                        } else if (!approxLocation.contains("km")) {
                            approxLocation = "0 km " + approxLocation;
                        }
                    } else {
                        location = "unknown location";
                    }

                    return new Earthquake(
                            x.getProperties().getMag(),
                            location,
                            approxLocation,
                            ZonedDateTime.ofInstant(Instant.ofEpochMilli(x.getProperties().getTime()), ZoneId.systemDefault()),
                            x.getProperties().getUrl()
                    );
                })
                .sorted(Comparator.comparing(Earthquake::getDateTime))
                .collect(Collectors.toList());
    }
}
