package com.gkmohit.unknown.weatherme.UI;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gkmohit.unknown.weatherme.R;
import com.gkmohit.unknown.weatherme.weather.Current;
import com.gkmohit.unknown.weatherme.weather.Forecast;
import com.gkmohit.unknown.weatherme.weather.MyLocationListener;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Main Activity page.
 *
 * @author gkmohit
 */
public class MainActivity extends Activity {

    private Forecast mForecast;
    private MyLocationListener mMyLocationListener;
    private double mLatitude;
    private double mLongitude;
    public static final String TAG = MainActivity.class.getName();

    @Bind(R.id.timeLabel)
    TextView mTimeLabel;
    @Bind(R.id.temperatureLabel)
    TextView mTemperatureLabel;
    @Bind(R.id.humidityValue)
    TextView mHumidityValue;
    @Bind(R.id.precpValue)
    TextView mPrecipValue;
    @Bind(R.id.summaryLabel)
    TextView mSummaryLabel;
    @Bind(R.id.iconImageView)
    ImageView mIconImageView;
    @Bind(R.id.locationLabel)
    TextView mLocationLabel;
    @Bind(R.id.refreshImageView)
    ImageView mRefreshImageView;
    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;
    @Bind(R.id.unitLabel)
    TextView mUnitLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mProgressBar.setVisibility(View.INVISIBLE);

         /* Use the LocationManager class to obtain GPS locations */
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        mMyLocationListener = new MyLocationListener();
        
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mMyLocationListener);

        //Latitude and Longitude values.
//        final double mLatitude = 43.768;
//        final double mLongitude = -79.345;
        final double mLatitude = 0;
        final double mLongitude = 0;
        //setLatitude(mMyLocationListener.getLatitude());
        //setLongitude(mMyLocationListener.getLongitude());
//        Log.v(TAG, "Lattitude = " + getLatitude() + " Longitude = " + getLongitude());
        Log.v(TAG, "mMyLocationListener Lattitude = " + mMyLocationListener.getLatitude() + " mMyLocationListener Longitude = " + mMyLocationListener.getLongitude());
        mRefreshImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                setLatitude(mMyLocationListener.getLatitude());
//                setLongitude(mMyLocationListener.getLongitude());
                getForecast(mMyLocationListener.getLatitude(), mMyLocationListener.getLongitude());
                Log.v(TAG, "mMyLocationListener Lattitude = " + mMyLocationListener.getLatitude() + "mMyLocationListener Longitude = " + mMyLocationListener.getLongitude());
            }
        });

        getForecast(mMyLocationListener.getLatitude(), mMyLocationListener.getLongitude());

    }

    /**
     * Gets a new forecast when called.
     * <p/>
     * TODO : Pass the queries if needed to change the forecastURL. Example change units
     *
     * @param latitude
     * @param longitude
     */
    private void getForecast(double latitude, double longitude) {
        String apiKey = "58d83f3c25abb4da7c73286532f3264c";
        String units = "?units=si";

        String forecastUrl = "https://api.forecast.io/forecast/" + apiKey + "/" + latitude + "," + longitude + units;
        Log.v(TAG, "forecastUrl = " + forecastUrl);
        if (isNetworkAvailable()) {
            toggleRefresh();
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(forecastUrl)
                    .build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    Log.v(TAG, e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });
                    alertUserAboutError();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });

                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            mForecast = parseForecastDetails(jsonData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateDisplay();
                                }
                            });
                        } else {
                            alertUserAboutError();
                        }

                    } catch (IOException e) {
                        Log.e(TAG, "Exception caught: ", e);
                    } catch (JSONException e) {
                        Log.e(TAG, "Exception caught: ", e);
                    }
                }
            });
        } else {
            Toast.makeText(this, R.string.network_unavailable, Toast.LENGTH_LONG).show();
        }
    }


    private void toggleRefresh() {
        if (mProgressBar.getVisibility() == View.INVISIBLE) {
            mProgressBar.setVisibility(View.VISIBLE);
            mRefreshImageView.setVisibility(View.INVISIBLE);
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
            mRefreshImageView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * TODO update the units based on the loction
     */
    private void updateDisplay() {
        Current current = mForecast.getCurrent();
        mTemperatureLabel.setText(current.getTemperature() + "");
        mTimeLabel.setText("At " + current.getFormattedTime() + " it will be ");
        mHumidityValue.setText(current.getHumidity() + "");
        mPrecipValue.setText(current.getPrecipChance() + "%");
        mSummaryLabel.setText(current.getSummary());

        Drawable drawable = getResources().getDrawable(current.getIconId());
        mIconImageView.setImageDrawable(drawable);
        mUnitLabel.setText("C");

    }


    private boolean isNetworkAvailable() {

        boolean isAvailable = false;
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;

    }

    private Forecast parseForecastDetails(String jsonData) throws JSONException {
        Forecast forecast = new Forecast();

        forecast.setCurrent(getCurrentDetails(jsonData));
        return forecast;
    }

    private Current getCurrentDetails(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        JSONObject currently = forecast.getJSONObject("currently");

        Current current = new Current();
        current.setTime(currently.getLong("time"));
        current.setIcon(currently.getString("icon"));
        current.setSummary(currently.getString("summary"));
        current.setTimeZone(forecast.getString("timezone"));
        current.setHumidity(currently.getDouble("humidity"));
        current.setTemperature(currently.getDouble("temperature"));
        current.setPrecipChance(currently.getInt("precipProbability"));

        Log.d(TAG, "Formatted time : " + current.getFormattedTime());
        return current;
    }

    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog");
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }
}
