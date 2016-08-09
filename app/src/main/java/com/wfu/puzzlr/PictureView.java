package com.wfu.puzzlr;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gaurav on 4/27/2016.
 */
public class PictureView extends AppCompatActivity {
    String photo_url;
    Intent intent;
    ImageView imageView;
    TextView timerdown;
    String question_mongo_id;
    String base_url = "http://puzzlr-backend.herokuapp.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pic_view);
        imageView = (ImageView) findViewById(R.id.imageView);
        intent = getIntent();
        photo_url = intent.getStringExtra("picture");
        question_mongo_id = intent.getStringExtra("question_mongo_id");
        timerdown = (TextView) findViewById(R.id.textView3);
        timerdown.setText("Loading...");
        Picasso.with(PictureView.this).load(photo_url)
        .into(imageView, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                Log.d("TOM", "good");
                timerdown.setText("");


            }

            @Override
            public void onError() {
                Log.d("TOM", "erer");

            }
        });
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new LongOperation().execute("");
                Intent main = new Intent(PictureView.this, Home.class);
                startActivity(main);
                finish();
            }
        }, 15 * 1000); // afterDelay will be executed after (secs*1000) milliseconds.
    }
    private class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            deletePOSTS(question_mongo_id);
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
    public void deletePOSTS(String id) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = base_url + "posts/delete/" + id;
        Log.d("TOM", "delete url:" + url);
        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
            new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response) {
                    Log.d("TOM", "1-DELETE" + response);
                }
            },
            new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // TODO Auto-generated method stub
                    Log.d("TOM", error.toString());
                }
            }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Accept","application/json");
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        queue.add(postRequest);
    }
}
