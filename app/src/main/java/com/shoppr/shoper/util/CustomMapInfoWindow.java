package com.shoppr.shoper.util;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;
import com.shoppr.shoper.R;

public class CustomMapInfoWindow implements InfoWindowAdapter {
    private Activity context;

    public CustomMapInfoWindow(Activity context){
        this.context=context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view=context.getLayoutInflater().inflate(R.layout.map_info_window, null);
        TextView tvTitle = (TextView) view.findViewById(R.id.markerTitle);
        tvTitle.setText(marker.getTitle());

        return view;
    }
}
