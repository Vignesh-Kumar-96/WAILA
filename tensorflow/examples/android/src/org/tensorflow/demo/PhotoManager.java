package org.tensorflow.demo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;

/**
 * Created by vignesh on 5/5/18.
 */

public class PhotoManager {

    protected Activity parent;
    protected static String TAG = "FPhotoManager";
    protected String baseDir;
    protected String userName;
    protected DatabaseReference userDB;

    static PhotoManager photoManagerInstance = null;


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
            }
        } else {
            userDB = null;
        }
        userName = _userName;
    }

    public byte[] convertResourceIDToBytes(int resourceID, int compression) {
        //compression should be between 0 and 100 (inclusive)
        Drawable drawable = ContextCompat.getDrawable(this.parent.getApplicationContext(), resourceID);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, compression, stream);
        byte[] bitmapdata = stream.toByteArray();
        return bitmapdata;
    }

    public byte[] convertBitmapToBytes(Bitmap bitmap, int compression) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, compression, stream);
        byte[] bitmapdata = stream.toByteArray();
        return bitmapdata;
    }

    public Boolean uploadPhoto(String title, byte[] data) {
        if (data == null) return false;
        Log.d("PhotoManager", "uploading photo titled " + title + " user: " + userName);

        if (userDB != null) {
            // XXX Write me: store photo in firebase (one line)
            PhotoObject p = new PhotoObject();
            p.title = title;
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

    public static PhotoManager getInstance(){
        if(PhotoManager.photoManagerInstance == null){
            photoManagerInstance = new PhotoManager();
        }

        return photoManagerInstance;
    }
}
