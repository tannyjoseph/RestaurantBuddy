package com.g.apitask.Activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.g.apitask.Activities.MainActivity;
import com.g.apitask.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;


public class RestaurantProfile extends Activity {

    ImageView imageView;
    TextView r_name, r_address, r_timings, r_ratings;
    Button r_phone;
    String r_id, res_address, res_name, res_timings;
    String res_phone;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listofcategories);

        imageView = findViewById(R.id.rest_image);
        r_address = findViewById(R.id.rest_address);
        r_name = findViewById(R.id.rest_name);
        r_phone = findViewById(R.id.phone);
        r_ratings = findViewById(R.id.rate);
        r_timings = findViewById(R.id.timings);

        Intent intent = getIntent();

        r_id = intent.getStringExtra("id");
        res_address = intent.getStringExtra("Key_address");
        res_name = intent.getStringExtra("KEY_NAME");

        r_address.setText(res_address);
        r_name.setText(res_name);
        r_ratings.setText(intent.getStringExtra("rating"));


        RequestQueue queue = Volley.newRequestQueue(this);
        if (r_id != null) {
            String url = "https://developers.zomato.com/api/v2.1/restaurant?res_id=" + r_id;

            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    new com.android.volley.Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // response
                            try {

                                res_timings = response.getString("timings");
                                res_phone = response.getString("phone_numbers");

                                final String[] str = res_phone.split(",");


                                r_timings.setText("Timings: " + res_timings);
                                r_phone.setText(str[0]);

                                if (!response.getString("featured_image").equals("")) {
                                    Picasso.with(getApplicationContext())
                                            .load(response.getString("featured_image"))
                                            .into(imageView);
                                }

                                r_phone.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent callIntent = new Intent(Intent.ACTION_DIAL);

                                        callIntent.setData(Uri.parse("tel:" + str[0]));

                                        startActivity(callIntent);
                                    }
                                });

                            } catch (Exception e) {
                                e.getMessage();
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
    }
}
