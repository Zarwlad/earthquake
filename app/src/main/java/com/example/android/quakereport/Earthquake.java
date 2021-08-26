package com.example.android.quakereport;

import java.net.URL;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Earthquake {
    private Double magnitude;
    private String location;
    private String approxPoint;
    private ZonedDateTime dateTime;
    private URL url;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("LLL d, yyyy\nhh:mm");

    public String getDateTime() {
        return formatter.format(dateTime);
    }
}
