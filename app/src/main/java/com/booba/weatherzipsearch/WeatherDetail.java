package com.booba.weatherzipsearch;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.booba.common.DownloadByteArrayOutputStreamUtil;
import com.booba.weatherzipsearch.model.CurrentObservation;
import com.booba.weatherzipsearch.model.DisplayLocation;
import com.booba.weatherzipsearch.model.Response;
import com.booba.weatherzipsearch.model.WeatherData;
import com.google.gson.Gson;


/**
 * Created by donna on 6/21/2016.
 */
public class WeatherDetail extends AppCompatActivity {

    private SparseArray<String> stringSparseArray;
    private String input_zip;
    private RelativeLayout mRelativeLayout;
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_detail);
        //getWindow().setWindowAnimations(R.style.AppTheme_Slide_in_right);

        String jsonString = getIntent().getStringExtra(MainActivity.JSON_RESULT);
        input_zip = getIntent().getStringExtra(MainActivity.INPUT_ZIP_CODE);

        AsyncTask<String, Void, SparseArray> asyncTask = taskOfPopulateView();
        asyncTask.execute(jsonString);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.weather_content);
        mRelativeLayout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_right));
    }

    private AsyncTask<String, Void, SparseArray> taskOfPopulateView() {
        return new AsyncTask<String, Void, SparseArray>() {
                Bitmap bitmap;
                @Override
                protected SparseArray<String> doInBackground(String[] params) {
                    byte[] bytes;
                    String url;
                    stringSparseArray = parseJsonString(params[0]);
                    if (stringSparseArray != null) {
                        url = stringSparseArray.get(R.id.icon_url);
                        if(url!=null&&!url.isEmpty()) {
                            bytes = DownloadByteArrayOutputStreamUtil.getHttpResponse(url).toByteArray();
                            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        }
                    }
                    return stringSparseArray;
                }

                @Override
                protected void onPostExecute(SparseArray o) {
                    if (stringSparseArray != null) {
                        TextView textView;
                        for (int i = 0; i < stringSparseArray.size(); i++) {
                            int id = stringSparseArray.keyAt(i);
                            if (id != R.id.icon_url) {
                                textView = (TextView) findViewById(id);
                                if (textView != null)
                                    textView.setText(stringSparseArray.get(id));
                            } else {
                                ImageView imageView = (ImageView) findViewById(id);
                                if (imageView != null) {
                                    DownloadByteArrayOutputStreamUtil.viewScaler(imageView, 5.0f); //scale up the thumb for higher density display on mobile
                                    imageView.setImageBitmap(bitmap);
                                }
                            }
                        }

                        setResult(RESULT_OK, (input_zip!=null) ? new Intent().putExtra(MainActivity.INPUT_ZIP_CODE, input_zip):null);
                        findViewById(R.id.weather).setSelected(true); //enable the weather summary marquee to scroll if not fitting in the space
                        findViewById(R.id.weather_content).setVisibility(View.VISIBLE);
                        findViewById(R.id.progressbar).setVisibility(View.INVISIBLE);

                    } else {
                        Toast.makeText(WeatherDetail.this, "your search didn't get any result, please choose a different zip code", Toast.LENGTH_LONG).show();
                            finish();
                    }
                }
            };
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        getWindow().setWindowAnimations(R.style.AppTheme_Slide_out_right);
       // mRelativeLayout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_out_right));
        super.onBackPressed();

    }

    private SparseArray<String> parseJsonString(String jsonString) {
        final SparseArray<String> stringSparseArray = new SparseArray<>();
        WeatherData weatherData = new Gson().fromJson(jsonString, WeatherData.class);
        CurrentObservation currentObservation = null;
        DisplayLocation displayLocation = null;
        Response response;
        if(weatherData!=null){
            response = weatherData.getResponse();
            if(response.getError()!=null)
                return null;
            currentObservation = weatherData.getCurrentObservation();
        }
        if(currentObservation!=null) {
            displayLocation = currentObservation.getDisplayLocation();
            stringSparseArray.put(R.id.temp_f, currentObservation.getTempF());
            stringSparseArray.put(R.id.dewpoint_f, currentObservation.getDewpointF());
            stringSparseArray.put(R.id.relative_humidity, currentObservation.getRelativeHumidity());
            stringSparseArray.put(R.id.feelslike_f, currentObservation.getFeelslikeF()  );
            stringSparseArray.put(R.id.weather, currentObservation.getWeather());
            stringSparseArray.put(R.id.icon_url, currentObservation.getIconUrl());
        }
        if(displayLocation!=null) {
            stringSparseArray.put(R.id.city, displayLocation.getCity());
            stringSparseArray.put(R.id.state, displayLocation.getState());
            stringSparseArray.put(R.id.zip, displayLocation.getZip());

        }
        return stringSparseArray;

    }
}
