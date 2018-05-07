package org.tensorflow.demo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextThemeWrapper;
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

    protected static String ANON_USER = "Anonymous User";
    private LinearLayoutManager rv_layout_mgr;
    private ArrayList<PhotoObject> memories;
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
        memories = new ArrayList<>();
        rv_layout_mgr = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(rv_layout_mgr);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


    }

    @Override
    public void getDataCallback(ArrayList<PhotoObject> photoObjects) {
        //Log.d("PhotoFragment", "got some photos for name query " + name);
        if (photoObjects == null || photoObjects.size() == 0) {
            Toast.makeText(this, "No memories :(", Toast.LENGTH_SHORT).show();
            pb.setVisibility(View.GONE);
            return;
        }
        memories = photoObjects;
        adapter = new DynamicAdapter(memories, getApplicationContext());
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
                if(!photoManager.userName.equals(ANON_USER)) {
                    if (memories.size() > 0) {
                        confirmDialog();
                    } else {
                        Toast.makeText(getApplicationContext(), "No memories to delete!", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "You do not have the permissions to delete the public database!", Toast.LENGTH_SHORT).show();
                }
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
            memories.clear();
        }
    }

    private void confirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
        builder
                .setMessage("Are you sure?")
                .setPositiveButton("Yes",  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        deleteAllRows();
                        ArrayList<PhotoObject> empty = new ArrayList<PhotoObject>();
                        adapter = new DynamicAdapter(empty, getApplicationContext());
                        recyclerView.setAdapter(adapter);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                })
                .show();
    }
}
