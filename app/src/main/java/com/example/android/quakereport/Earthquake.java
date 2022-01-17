package com.example.android.quakereport;

public class Earthquake {

    private double mMag;
    private String mLocation;
    private long mDate;
    private String mUrl;

    public Earthquake() {
    }

    public Earthquake(double mag, String location, long date, String url) {
        mMag = mag;
        mLocation = location;
        mDate = date;
        mUrl = url;
    }

    public double getMag() {
        return mMag;
    }

    public void setMag(double mag) {
        mMag = mag;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        mLocation = location;
    }

    public long getDate() {
        return mDate;
    }

    public void setDate(long date) {
        mDate = date;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }
}
