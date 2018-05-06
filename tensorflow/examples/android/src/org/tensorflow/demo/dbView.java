package org.tensorflow.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
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
    private ProgressBar pb;
    private RecyclerView.Adapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        photoManager.getUserData(this);
        setContentView(R.layout.db_results);
        pb = (ProgressBar) findViewById(R.id.progressBar2);
        pb.setVisibility(View.VISIBLE);
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
            pb.setVisibility(View.GONE);
            return;
        }

        adapter = new DynamicAdapter(photoObjects, getApplicationContext());
        pb.setVisibility(View.GONE);

        recyclerView.setAdapter(adapter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            case R.id.delete:
                deleteAllRows();
                ArrayList<PhotoObject> empty = new ArrayList<PhotoObject>();
                adapter = new DynamicAdapter(empty, getApplicationContext());
                recyclerView.setAdapter(adapter);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.db_menu, menu);
        return true;
    }


    public void deleteAllRows() {
        if (photoManager.userDB != null) {
            photoManager.userDB.child("photos").setValue(null);
        }
    }
}
