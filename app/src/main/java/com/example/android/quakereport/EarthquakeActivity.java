/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.quakereport.network.EarthquakeService;
import com.example.android.quakereport.network.Mapper;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static java.time.ZonedDateTime.now;

public class EarthquakeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        ListView earthquakeListView = findViewById(R.id.list);

        EditText startDateEdit = findViewById(R.id.startDateEdit);
        EditText endDateEdit = findViewById(R.id.endDateEdit);
        EditText limitEdit = findViewById(R.id.limitEdit);
        EditText minMagn = findViewById(R.id.minMagn);

        Button okButton = findViewById(R.id.requestButton);
        ProgressBar progressBar = findViewById(R.id.progressBar);

        Calendar endDateCalendar = Calendar.getInstance();
        Calendar startDateCalendar = Calendar.getInstance();
        startDateCalendar.set(Calendar.DAY_OF_YEAR, endDateCalendar.get(Calendar.DAY_OF_YEAR) - 1);

        bindCalendarToEditText(startDateEdit, startDateCalendar);
        bindCalendarToEditText(endDateEdit, endDateCalendar);

        DateTimeFormatter requestFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        List<Earthquake> earthquakes = new ArrayList<>();
        ArrayAdapter<Earthquake> adapter = new EarthquakeListAdapter(this, earthquakes);
        earthquakeListView.setAdapter(adapter);


        okButton.setOnClickListener(event -> {
            progressBar.setVisibility(View.VISIBLE);
            earthquakeListView.setVisibility(View.GONE);

            EarthquakeService earthquakeService = new EarthquakeService();
            earthquakeService
                    .earthquakeResponseBody(
                            getFormat(startDateCalendar, requestFormatter),
                            getFormat(endDateCalendar, requestFormatter),
                            limitEdit.getText().toString(),
                            minMagn.getText().toString())
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(x -> {
                        earthquakes.clear();
                        earthquakes.addAll(Mapper.fromJsonToListPojos(x)
                                .stream()
                                .sorted(Comparator.comparing(Earthquake::getDateTime).reversed())
                                .collect(Collectors.toList()));
                        adapter.notifyDataSetChanged();
                            },
                            y -> Toast
                                    .makeText(getApplicationContext(), EarthquakeActivity.class.getName() + " " + y.getMessage(), Toast.LENGTH_LONG)
                                    .show());

            progressBar.setVisibility(View.GONE);
            earthquakeListView.setVisibility(View.VISIBLE);
        });

    }

    private void bindCalendarToEditText(EditText dateEditView, Calendar calendar) {
        String datePattern = "dd.MM.yyyy";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(datePattern);

        dateEditView.setText(getFormat(calendar, dateTimeFormatter));

        DatePickerDialog.OnDateSetListener onDateSetListener = (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            dateEditView.setText(getFormat(calendar, dateTimeFormatter));
        };

        dateEditView.setOnClickListener(v -> new DatePickerDialog(
            this,
            onDateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)).show()
        );

    }

    private String getFormat(Calendar calendar, DateTimeFormatter dateTimeFormatter) {
        return dateTimeFormatter
                .format(calendar.toInstant()
                        .atZone(ZoneId.systemDefault()));
    }
}
