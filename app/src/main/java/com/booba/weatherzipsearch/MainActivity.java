package com.booba.weatherzipsearch;

import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.http.HttpResponseCache;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.booba.common.DownloadByteArrayOutputStreamUtil;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final int WEATHER_DETAIL_REQUEST_CODE=2016;
    private static final String ENDPOINT = "http://api.wunderground.com/api/ce3f590ecbd24175/conditions/q/~.json";
    private static final String TAG = "MainActivity";
    public static final String JSON_RESULT="weather_json_data";
    private static final String WEATHER_ZIP_PREF = "weather_zip_pref";
    private static Set<String> mZipSet = new HashSet<>();
    private static final String mZipPrefName = "zipPref";
    private static SharedPreferences mWeatherPreferences;
    private static ArrayAdapter mAdapter;
    private static ArrayList<String> mPrefArrayList;

    static {
        mZipSet.add("78754");
        mZipSet.add("49781");
        mZipSet.add("96815");
    }

    public static final String INPUT_ZIP_CODE = "input_zip";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setWindowAnimations(R.style.AppTheme_Push_up_anim);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(" " + getString(R.string.app_name));
        toolbar.setLogo(R.mipmap.weather_icon);
        setSupportActionBar(toolbar);

        mWeatherPreferences = getSharedPreferences(WEATHER_ZIP_PREF, MODE_PRIVATE); //get the preference file or create it if not existing

        if (!mWeatherPreferences.contains(mZipPrefName)) { //init zip preference if not existing
            DownloadByteArrayOutputStreamUtil.applyPrefSetChanges(mZipPrefName, mZipSet, mWeatherPreferences);
        } else //get the preference from the storage
            mZipSet = mWeatherPreferences.getStringSet(mZipPrefName, mZipSet);

        mPrefArrayList = new ArrayList<>(mZipSet);
        mAdapter = new ArrayAdapter<>(this, R.layout.list_item, mPrefArrayList);
        ListView listView = ((ListView) findViewById(R.id.listView_zip_code));
        if (listView != null) {
            listView.setAdapter(mAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String zip = ((TextView) view).getText().toString();
                    makeWeatherApiCall(ENDPOINT.replace("~", zip), null);
                }
            });
        }
        /*listView.clearAnimation();
        listView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_top_to_bottom ));*/
        DownloadByteArrayOutputStreamUtil.enableHttpResponseCache(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        HttpResponseCache cache = HttpResponseCache.getInstalled();
        if (cache != null) {
            cache.flush();
        }
    }

    private void makeWeatherApiCall(String url, String zip) {
        new ProcessWeatherHttpRequestTask().execute(url, zip);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_zip:
                DialogFragment zipfrag = ZipInputDialogFragment.newInstance();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                zipfrag.show(fragmentTransaction, "FRAG_ADD_ZIP");
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==WEATHER_DETAIL_REQUEST_CODE)
            if(data!=null) {
                String zip = data.getStringExtra(INPUT_ZIP_CODE);
                    if (zip!=null&&mZipSet.add(zip)) {
                        mPrefArrayList.add(zip);
                        if (DownloadByteArrayOutputStreamUtil.applyPrefSetChanges(mZipPrefName, mZipSet, mWeatherPreferences)) {
                            mAdapter.notifyDataSetChanged();
                            Snackbar.make(findViewById(R.id.coordinator_snack), "Zip code, " + zip + ", has been added.", Snackbar.LENGTH_LONG).show();
                        }
                }
            }
        getWindow().setWindowAnimations(R.style.AppTheme_Slide_in_left);
    }
    /*
    Using a Dialog fragment floating on top of the main screen is non-intrusive so user still can see the main screen below it
     */
    public static class ZipInputDialogFragment extends DialogFragment {

        static ZipInputDialogFragment newInstance() {
            return new ZipInputDialogFragment();
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogTheme);
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.dialog_zipcode_input, container, false);
            final EditText editText = (EditText) view.findViewById(R.id.editText_zip_input);
            final Button button = (Button) view.findViewById(R.id.button_add_zip);
            button.setEnabled(editText.isDirty());
            editText.addTextChangedListener( DownloadByteArrayOutputStreamUtil.TextChangedWatcher(button)); //enable search button only if user entered zip info
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String input_zip = editText.getText().toString();
                    if (!input_zip.isEmpty() && input_zip.length() == 5) { //assuming all zip codes are 5 digits
                        ((MainActivity) getActivity()).makeWeatherApiCall(ENDPOINT.replace("~", input_zip), input_zip);
                        dismiss();
                    } else
                        Toast.makeText(getActivity(), "please enter a valid 5 digit zip code", Toast.LENGTH_LONG).show();

                }
            });

            return view;
        }

        @Override
        public void onActivityCreated(Bundle bundle) {
            super.onActivityCreated(bundle);
            getDialog().getWindow().setWindowAnimations(R.style.DialogAnim);

        }
    }

    private class ProcessWeatherHttpRequestTask extends AsyncTask<String, Void, String> {
        String url = "";
        String zip;
        @Override
        protected String doInBackground(String... params) {
            url = params[0];
            zip = params[1];
            ByteArrayOutputStream httpResponse = DownloadByteArrayOutputStreamUtil.getHttpResponse(url);

            if (httpResponse != null) {
                return httpResponse.toString();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String jsonString) {

            if (jsonString == null) {
                Toast.makeText(MainActivity.this, "The server is busy, please wait for 10 minutes and search again...", Toast.LENGTH_LONG).show();
                finish();
            }
            else {
                Log.i(TAG, "url: " + url);
                Intent intent = new Intent(MainActivity.this, WeatherDetail.class);
                intent.putExtra(JSON_RESULT, jsonString);
                intent.putExtra(INPUT_ZIP_CODE, zip);
                startActivityForResult(intent, WEATHER_DETAIL_REQUEST_CODE);

            }
            DownloadByteArrayOutputStreamUtil.LogHttpCacheHits(TAG);
        }
    }

}
