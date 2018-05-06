package org.tensorflow.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by vignesh on 5/5/18.
 */

public class dbView extends AppCompatActivity implements PhotoManager.getDataListener{

    private LinearLayoutManager rv_layout_mgr;
    private ArrayList<PhotoObject> photoObjects;
    private PhotoManager photoManager = PhotoManager.getInstance();
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        photoManager.getUserData(this);
        setContentView(R.layout.db_results);

        recyclerView = (RecyclerView) findViewById(R.id.searchResults);

        rv_layout_mgr = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(rv_layout_mgr);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


    }

    @Override
    public void getDataCallback(ArrayList<PhotoObject> photoObjects) {
        //Log.d("PhotoFragment", "got some photos for name query " + name);
        if (photoObjects == null || photoObjects.size() == 0) {
            Toast.makeText(this, "No memories :(", Toast.LENGTH_LONG);
            return;
        }

        RecyclerView.Adapter adapter = new DynamicAdapter(photoObjects, getApplicationContext());
        recyclerView.setAdapter(adapter);

    }
}
