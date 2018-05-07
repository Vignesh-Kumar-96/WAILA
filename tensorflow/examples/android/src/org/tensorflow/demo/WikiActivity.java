package org.tensorflow.demo;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class WikiActivity extends AppCompatActivity implements WikiJSON.IWikiJSON {
    static public String AppName = "Waila";
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar pb;
    private TextView wikiText;
    private ImageView wikiImage;
    protected VolleyFetch volleyFetch;
    private TextView searchText;
    private Button save_button;
    private ImageView bitmapView;
    private PhotoManager photoManager;
    private Bitmap bitmapimage;
    private Button database_button;
    private String title;
    private View line;
    private View line2;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 99;
    String locationProvider;
    LocationManager locationManager;
    Location lastKnownLocation;
    private final String BASE_URL =
            "https://en.wikipedia.org/w/api.php?action=query&format=json&prop=extracts%7Cpageimages&list=&meta=&continue=&redirects=1&titles=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.wiki_main);
        Intent activityThatCalled = getIntent();
        Bundle callingBundle = activityThatCalled.getExtras();
        String query = "";
        if( callingBundle != null ) {
             query = callingBundle.getString("item");
             bitmapimage = callingBundle.getParcelable("image");


        }
        pb = (ProgressBar) findViewById(R.id.progressBar);
        wikiText = (TextView) findViewById(R.id.picTextRowText);
        wikiImage = (ImageView) findViewById(R.id.picTextRowPic);
        searchText = (TextView) findViewById(R.id.search_term);

        save_button = (Button) findViewById(R.id.save_button);

        save_button.setVisibility(View.INVISIBLE);

        wikiText.setMovementMethod(new ScrollingMovementMethod());
        bitmapView = (ImageView) findViewById(R.id.bitmap_view);
        database_button = (Button) findViewById(R.id.database_button);
        database_button.setVisibility(View.INVISIBLE);
        line = (View) findViewById(R.id.line);
        line2 = (View) findViewById(R.id.line2);
        line.setVisibility(View.INVISIBLE);
        line2.setVisibility(View.INVISIBLE);





        volleyFetch = new VolleyFetch();
        newSearch(query);
        Net.init(getApplicationContext());
    }

    // https://www.reddit.com/dev/api#GET_search
    protected void newSearch(String searchTerm) {
        Log.d(AppName, "Search for "+ searchTerm);
        String url = BASE_URL;
        if(searchTerm.toLowerCase().equals("keyboard"))
            searchTerm = "computer keyboard";
        if(searchTerm.toLowerCase().equals("remote"))
            searchTerm = "remote control";
        if(!searchTerm.equals("") && searchTerm.length()<512){
            url += searchTerm ;
        }
        title = searchTerm;

        url += "&exintro=1&explaintext=1&pithumbsize=200";
        try {
            URL u = new URL(url);
            volleyFetch.add(this,u);
            searchText.setText(searchTerm.toUpperCase());
            searchText.setVisibility(View.GONE);

            save_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    get_location();
                    photoManager = PhotoManager.getInstance();
                    byte[] photo_bytes = photoManager.convertBitmapToBytes(bitmapimage, 100);
                    double latitude;
                    double longitude;
                    if (lastKnownLocation != null){
                        latitude = lastKnownLocation.getLatitude();
                        longitude = lastKnownLocation.getLongitude();
                    }

                    else{
                        latitude = 30.0;
                        longitude = -97.7;
                    }

                    if(photoManager.uploadPhoto(title, photo_bytes, latitude, longitude)){
                        save_button.setText("Memory recorded");
                        save_button.setClickable(false);
                    }


                }
            });


            database_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent todbView = new Intent(getApplicationContext(), dbView.class);
                    startActivity(todbView);
                }
            });
            fetchStart();
        }
        catch (MalformedURLException me){
            me.printStackTrace();
        }
    }

    public void fetchStart(){
        pb.setVisibility(View.VISIBLE);
    }

    public void fetchComplete(WikiItem wikiItem){
        if(wikiItem.thumbnailURL != null){
            Glide.with(wikiImage.getContext()).load(wikiItem.thumbnailURL)
                    .placeholder(R.drawable.ic_cloud_download_black_50dp).dontTransform()
                    .error(new ColorDrawable(Color.RED)).into(wikiImage);
        }
        wikiText.setText(wikiItem.extract);
        searchText.setVisibility(View.VISIBLE);
        save_button.setVisibility(View.VISIBLE);
        bitmapView.setImageBitmap(bitmapimage);
        database_button.setVisibility(View.VISIBLE);
        pb.setVisibility(View.GONE);
        line.setVisibility(View.VISIBLE);
        line2.setVisibility(View.VISIBLE);

    }

    public void fetchCancel(String url){
        pb.setVisibility(View.GONE);
    }

    @Override
    protected void onStop () {
        super.onStop();
        Net.getInstance().cancelPendingRequests(WikiActivity.AppName);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.d("WIKI", "PRESSED HOME");
                Intent i = new Intent(getApplicationContext(),DetectorActivity.class);
                startActivity(i);

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        Intent i = new Intent(getApplicationContext(),DetectorActivity.class);
        startActivity(i);
        super.onBackPressed();
        finish();
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    get_location();
                } else {
                    Log.v("Location", "Not Granted");
                }
                return;
            }

        }
    }

    public void get_location(){
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        }

        else{

            locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            locationProvider = LocationManager.NETWORK_PROVIDER;
            lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);

            if(lastKnownLocation == null){
                Log.v("LOCATION", "NULL");
            }

            else{
                Log.v("Location", locationStringFromLocation(lastKnownLocation));
            }



        }
    }

    public static String locationStringFromLocation(final Location location) {
        return Location.convert(location.getLatitude(), Location.FORMAT_DEGREES) + " " + Location.convert(location.getLongitude(), Location.FORMAT_DEGREES);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }



}
