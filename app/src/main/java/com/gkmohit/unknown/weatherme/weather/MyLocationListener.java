package com.gkmohit.unknown.weatherme.weather;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by gkmohit on 15-09-25.
 */
public class MyLocationListener implements android.location.LocationListener
{

    public static String TAG = MyLocationListener.class.getSimpleName();
    private double mLatitude;
    private double mLongitude;
    private boolean mGpsEnabled;

    @Override
    public void onLocationChanged(Location loc)
    {

        this.setLatitude(loc.getLatitude());
        this.setLongitude(loc.getLongitude());
        Log.v(TAG, "Latitude : " + this.getLatitude() + " Longitude : " + this.getLongitude());

    }



    public void onProviderDisabled(String provider)
    {
        setGpsEnabled(false);
    }


    public void onProviderEnabled(String provider)
    {
        setGpsEnabled(true);
    }

    public void onStatusChanged(String provider, int status, Bundle extras)
    {

    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    public boolean isGpsEnabled() {
        return mGpsEnabled;
    }

    public void setGpsEnabled(boolean gpsEnabled) {
        mGpsEnabled = gpsEnabled;
    }
}
