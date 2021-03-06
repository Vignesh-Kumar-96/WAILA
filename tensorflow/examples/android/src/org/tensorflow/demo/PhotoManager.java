package org.tensorflow.demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vignesh on 5/5/18.
 */

public class PhotoManager {

    protected Activity parent;
    protected static String TAG = "FPhotoManager";
    protected String baseDir;
    protected String userName;
    protected DatabaseReference userDB;
    private FirebaseUser currentUser;
    private ArrayList<PhotoObject> photoObjectList;



    static PhotoManager photoManagerInstance = null;

    public interface getDataListener {
        void getDataCallback(ArrayList<PhotoObject> photos);
    }


    private PhotoManager() {
    }

    // NB: Must be called on every authentication change
    // Input: Display name  Output: sanitized name for root of Firebase data
    public void updateCurrentUserName(String _userName) {
        if( _userName != null ) {
            // . is illegal in Firebase key, and more than one @ is illegal in email address
            _userName = _userName.replaceAll("\\.", "@"); // . illegal in Firebase key
            if (_userName != userName || userDB == null) {
                userDB = FirebaseDatabase.getInstance().getReference(_userName);
                currentUser = FirebaseAuth.getInstance().getCurrentUser();
            }
        } else {
            userDB = null;
        }
        userName = _userName;
    }


    public byte[] convertBitmapToBytes(Bitmap bitmap, int compression) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, compression, stream);
        byte[] bitmapdata = stream.toByteArray();
        return bitmapdata;
    }

    public Boolean uploadPhoto(String title, byte[] data, double latitude, double longitude) {

        if (data == null) return false;
        Log.d("PhotoManager", "uploading photo titled " + title + " user: " + userName);

        if (userDB != null) {
            // XXX Write me: store photo in firebase (one line)
            PhotoObject p = new PhotoObject();
            p.title = title;
            p.latitude = latitude;
            p.longitude = longitude;
            p.date= System.currentTimeMillis();
            String encodedBytes = Base64.encodeToString(data, Base64.DEFAULT);
            p.encodedBytes = encodedBytes;
            userDB.child("photos").push().setValue(p);

            return true;
        } else {
            Log.d(TAG, "userDB is null!");
            return false;
        }
    }

    public void getUserData(final getDataListener listener) {
        Log.d(TAG, "ENTERING GET USER DATA");
        if (userDB == null) {
            Log.d(TAG, "userDB is null!");
            return;
        }
        photoObjectList = new ArrayList<PhotoObject>();
        // XXX Write a query that returns all of the photos with a given name
        // and calls the listener with the results
        Query query = userDB.child("photos");
        query.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Log.d("CHILDREN", Long.toString(dataSnapshot.getChildrenCount()));
                        for (DataSnapshot photoSnapshot : dataSnapshot.getChildren()) {
                            //Getting the data from snapshot
                            PhotoObject photo = photoSnapshot.getValue(PhotoObject.class);
                            photoObjectList.add(photo);
                            //Log.v("TITLE", photo.title);
                        }

                        listener.getDataCallback(photoObjectList);
                    }
                    @Override
                    public void onCancelled(DatabaseError firebaseError) {
                        Log.d(TAG, "Date query cancelled");
                    }
                });



    }

    public static PhotoManager getInstance(){
        if(PhotoManager.photoManagerInstance == null){
            photoManagerInstance = new PhotoManager();
        }

        return photoManagerInstance;
    }
}
