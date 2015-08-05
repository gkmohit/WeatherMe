package com.gkmohit.unknown.weatherme;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.w3c.dom.Text;

import java.io.IOException;

public class MainActivity extends Activity {

    public static final String TAG = MainActivity.class.getName();
    public TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.textView);

        String apiKey = "58d83f3c25abb4da7c73286532f3264c";
        double latitude = 37.8267;
        double longitude = -122.423;
        String forecastUrl = "https://api.forecast.io/forecast/" + apiKey +  "/" + latitude + "," + longitude;
        Log.v(TAG, "forecastUrl = " + forecastUrl);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(forecastUrl).build();

        Call call = client.newCall(request);
        Log.v(TAG, "about to call call.enque");
        textView.setText("TEST");
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                Log.v(TAG, "in onResponse");
//                try {

//                    if(response.isSuccessful()) {
                        Log.v(TAG, response.body().string());
//                        textView.setText(response.body().string());
//
//                    }
//                } catch (IOException e) {
//                    Log.e(TAG, "Exception caught : " + e);
//                }
            }
        });

    }
}