package org.tensorflow.demo;

/**
 * Created by vignesh on 5/5/18.
 */

public class PhotoObject {
    public String title;
    public Long date;
    public String encodedBytes; // XXX This is large, it should use a file

    public PhotoObject () {
    }
    public String getTitle() { return title; }
    public Long getDate() { return date; }
    public String getEncodedBytes(){ return encodedBytes;}
}
