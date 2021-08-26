package com.example.android.quakereport;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.net.URI;
import java.util.List;

public class EarthquakeListAdapter extends ArrayAdapter<Earthquake> {

    public EarthquakeListAdapter(@NonNull Context context, List<Earthquake> earthquakes) {
        super(context, 0, earthquakes);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater
                    .from(getContext())
                    .inflate(R.layout.item_layout, parent, false);
        }

        Earthquake earthquake = getItem(position);

        View layout = convertView.findViewById(R.id.itemLayout);
        layout.setOnClickListener(x -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(earthquake.getUrl().toString()));
            getContext().startActivity(intent);
        });

        TextView magnitude = convertView.findViewById(R.id.magnTextView);
        magnitude.setText(String.valueOf(earthquake.getMagnitude()));

        GradientDrawable magCircle = (GradientDrawable) magnitude.getBackground();
        magCircle.setColor(getMagnitudeColor(earthquake.getMagnitude()));

        TextView approxLocation = convertView.findViewById(R.id.approxLocationTextView);
        approxLocation.setText(earthquake.getApproxPoint().toUpperCase());

        TextView area = convertView.findViewById(R.id.areaTextView);
        area.setText(earthquake.getLocation());

        TextView dateTime = convertView.findViewById(R.id.dateTextView);
        dateTime.setText(earthquake.getDateTime());

        return convertView;
    }

    private int getMagnitudeColor(double magnitude) {
        int magnitudeColorResourceId;
        int magnitudeFloor = (int) Math.floor(magnitude);
        switch (magnitudeFloor) {
            case 0:
            case 1:
                magnitudeColorResourceId = R.color.magnitude1;
                break;
            case 2:
                magnitudeColorResourceId = R.color.magnitude2;
                break;
            case 3:
                magnitudeColorResourceId = R.color.magnitude3;
                break;
            case 4:
                magnitudeColorResourceId = R.color.magnitude4;
                break;
            case 5:
                magnitudeColorResourceId = R.color.magnitude5;
                break;
            case 6:
                magnitudeColorResourceId = R.color.magnitude6;
                break;
            case 7:
                magnitudeColorResourceId = R.color.magnitude7;
                break;
            case 8:
                magnitudeColorResourceId = R.color.magnitude8;
                break;
            case 9:
                magnitudeColorResourceId = R.color.magnitude9;
                break;
            default:
                magnitudeColorResourceId = R.color.magnitude10plus;
                break;
        }
        return ContextCompat.getColor(getContext(), magnitudeColorResourceId);
    }

}
