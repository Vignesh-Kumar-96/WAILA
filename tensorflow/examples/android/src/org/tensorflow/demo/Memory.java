package org.tensorflow.demo;

/**
 * Created by vignesh on 5/5/18.
 */

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by vignesh on 3/5/18.
 */

public class Memory extends AppCompatActivity implements OnMapReadyCallback{
    String title;
    String byteArray;
    Double lat;
    Double lng;
    Long date;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memory_layout);
        // Get the Intent that called for this Activity to open
        Intent activityThatCalled = getIntent();
        // Get the data that was sent
        Bundle callingBundle = activityThatCalled.getExtras();
        if( callingBundle != null ) {
            title = callingBundle.getString("title");
            byteArray = callingBundle.getString("bitmap");
            lat = callingBundle.getDouble("latitude");
            lng = callingBundle.getDouble("longitude");
            date = callingBundle.getLong("date");
        }

        DateFormat formatter1 = new SimpleDateFormat("EEE, d MMM yyyy");
        DateFormat formatter2 = new SimpleDateFormat("HH:mm:ss");

        String formatted_date = formatter1.format(date);
        String formatted_time = formatter2.format(date);
        byte[] decoded = android.util.Base64.decode(byteArray, android.util.Base64.DEFAULT);
        Bitmap image = BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
        TextView object = (TextView) findViewById(R.id.text_object);
        TextView date = (TextView) findViewById(R.id.text_date);
        object.setText("You were looking at this " + title);
        date.setText("on " + formatted_date +" at " +
                formatted_time);
        ImageView iV = (ImageView) findViewById(R.id.picPost);
        iV.setImageBitmap(image);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        //googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        LatLng loc = new LatLng(lat, lng);
        googleMap.addMarker(new MarkerOptions().position(loc).title("marker"));
        //googleMap.moveCamera(CameraUpdateFactory.zoomBy(15));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 16
        ));

    }



}
