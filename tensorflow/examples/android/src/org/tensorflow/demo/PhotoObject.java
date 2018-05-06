package org.tensorflow.demo;

import android.location.Location;

/**
 * Created by vignesh on 5/5/18.
 */

public class PhotoObject {
    public String title;
    public Long date;
    public Location location;
    public String encodedBytes; // XXX This is large, it should use a file

    public PhotoObject () {
    }
    public String getTitle() { return title; }
    public Long getDate() { return date; }
    public Location getLocation(){ return location;}
    public String getEncodedBytes(){ return encodedBytes;}
}
