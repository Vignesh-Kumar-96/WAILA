package org.tensorflow.demo;

import java.io.Serializable;

/**
 * Created by vignesh on 5/5/18.
 */

public class PhotoObject implements Serializable {
    public String title;
    public Long date;
    public double latitude;
    public double longitude;
    public String encodedBytes; // XXX This is large, it should use a file

    public PhotoObject () {
    }

    public PhotoObject (String _title, Long _date, double _latitude, double _longitude, String _encodedBytes) {
        this.title = title;
        this.date = _date;
        this.latitude = _latitude;
        this.longitude = _longitude;
        this.encodedBytes = _encodedBytes;
    }

    public String getTitle() { return title; }
    public Long getDate() { return date; }
    public double getLatitude(){ return latitude;}
    public double getLongitude(){ return longitude;}
    public String getEncodedBytes(){ return encodedBytes;}
}
