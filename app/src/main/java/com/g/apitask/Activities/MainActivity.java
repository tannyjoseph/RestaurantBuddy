package com.g.apitask.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.g.apitask.Adapters.MyListAdapter;
import com.g.apitask.Adapters.StringMatchAdapter;
import com.g.apitask.Classes.MyList;
import com.g.apitask.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    RecyclerView recyclerView;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    static double currentLatitude;
    static double currentLongitude;
    double lat1, long1;
    String addr1;
    EditText etlocation;
    private RecyclerView.Adapter adapter;
    private List<MyList> list;
    private List<String> names;
    SearchView auto;
    androidx.appcompat.widget.SearchView.SearchAutoComplete searchAutoComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkLocationPermission();


        etlocation = findViewById(R.id.city);
        etlocation.setEnabled(false);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        names = new ArrayList<>();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                // The next two lines tell the new client that “this” current class will handle connection stuff
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                //fourth line adds the LocationServices API endpoint from GooglePlayServices
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds


        auto = findViewById(R.id.search_view);


    }

    @Override
    protected void onResume() {
        super.onResume();
        //Now lets connect to the API
        mGoogleApiClient.connect();

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(this.getClass().getSimpleName(), "onPause()");

        //Disconnect from API onPause()
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }


    }

    /**
     * If connected get lat and long
     */
    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(MainActivity.this);

        if (location == null) {
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            } else {
                checkLocationPermission();
            }

        } else {
            //If everything went fine lets get latitude and longitude
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();

            client.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                    Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());


                    List<Address> addressList1 = null;
                    try {

                        addressList1 = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                        lat1 = location.getLatitude();
                        long1 = location.getLongitude();


                        if (addressList1 != null && addressList1.size() > 0) {
                            Log.i("PlaceInfo", addressList1.get(0).getLocality());

                            addr1 = addressList1.get(0).getLocality();
                            etlocation.setText(addr1);
                            list.clear();
                            loadUrlData();

                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            System.out.println(currentLatitude + " " + currentLongitude);

        }
    }


    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            Log.e("Error", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }


    @Override
    public void onLocationChanged(Location location) {

    }

    public double getCurrentLatitude() {
        return currentLatitude;
    }

    public double getCurrentLongitude() {
        return currentLongitude;
    }

    private void loadUrlData() {

        mGoogleApiClient.connect();

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://developers.zomato.com/api/v2.1/geocode?lat=" + getCurrentLatitude() + "&lon=" + getCurrentLongitude();
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // response
                        System.out.println("res" + response);

                        try {

                            JSONArray restaurants = response.getJSONArray("nearby_restaurants");


                            for (int i = 0; i < restaurants.length(); i++) {

                                JSONObject jo = restaurants.getJSONObject(i);

                                String id = jo.getJSONObject("restaurant").getJSONObject("R").getString("res_id");
                                System.out.println("ky" + id);
                                MyList myList = new MyList(jo.getJSONObject("restaurant").getString("name"),
                                        jo.getJSONObject("restaurant").getString("thumb"),
                                        jo.getJSONObject("restaurant").getJSONObject("location").getString("locality") + " , " + jo.getJSONObject("restaurant").getJSONObject("location").getString("city"),
                                        jo.getJSONObject("restaurant").getJSONObject("user_rating").getString("aggregate_rating")
                                        , jo.getJSONObject("restaurant").getJSONObject("R").getString("res_id"));

                                names.add(jo.getJSONObject("restaurant").getString("name"));
                                list.add(myList);

                            }
                            System.out.println("names" + names);

                            adapter = new MyListAdapter(list, getApplicationContext());
                            recyclerView.setAdapter(adapter);


                            searchAutoComplete = auto.findViewById(androidx.appcompat.R.id.search_src_text);
                            StringMatchAdapter dataAdapter = new StringMatchAdapter(MainActivity.this, android.R.layout.simple_dropdown_item_1line, names);
                            searchAutoComplete.setAdapter(dataAdapter);

                            SearchManager searchManager =
                                    (SearchManager) getSystemService(SEARCH_SERVICE);
                            auto.setSearchableInfo(
                                    searchManager.getSearchableInfo(getComponentName()));


                            searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    String searchString = (String) parent.getItemAtPosition(position);
                                    searchAutoComplete.setText("" + searchString);
                                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d("ERROR", "error => " + error.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user-key", "5f718be08d069711b39c5f38c2b7ebb1");
                params.put("Accept", "application/json");

                return params;
            }
        };
        queue.add(postRequest);
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        99);
                            }
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        99);
            }
        }
    }


}