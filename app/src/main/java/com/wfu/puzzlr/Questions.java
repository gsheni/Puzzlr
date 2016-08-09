package com.wfu.puzzlr;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Questions extends AppCompatActivity {
    int choice = -1;
    String questionstring;
    String friend_name;
    String email;
    String photo_url;
    Button button;
    Switch A_button;
    Switch B_button;
    Switch C_button;
    EditText question;
    EditText answerA;
    EditText answerB;
    EditText answerC;
    Intent intent;
    String mongo_user_id;
    String friend_mongo_user_id;
    String facebook_id;
    String user_name;
    String facebook_photo_url;
    String base_url = "http://puzzlr-backend.herokuapp.com/";
//    String base_url = "http://192.168.1.100:3000/";
    String fileName;
    String friend_facebook_id;
    String result;
    String answer;
    String id;
    String[] answers = new String[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);
        question = (EditText)findViewById(R.id.question);
        question = (EditText) findViewById(R.id.editText2);
        answerA = (EditText) findViewById(R.id.editText2);
        answerB = (EditText) findViewById(R.id.editText3);
        answerC = (EditText) findViewById(R.id.editText4);
        question = (EditText) findViewById(R.id.question);
        intent = getIntent();
        friend_name = intent.getStringExtra("friend_name");
        email = intent.getStringExtra("user_email");
        facebook_id = intent.getStringExtra("facebook_id");
        user_name = intent.getStringExtra("user_name");
        fileName = intent.getStringExtra("fileName");
        facebook_photo_url = intent.getStringExtra("facebook_photo_url");
        friend_facebook_id = intent.getStringExtra("friend_facebook_id");
        new LongOperation().execute("");

        button = (Button) findViewById(R.id.submit);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (check_user_data_entered()){
                        if (A_button.isChecked()){
                            answer = "A";
                        }
                        else if(B_button.isChecked()){
                            answer = "B";
                        }
                        else if (C_button.isChecked()){
                            answer = "C";
                        }

                        questionstring = question.getText().toString();
                        answers[0] = answerA.getText().toString();
                        answers[1] = answerB.getText().toString();
                        answers[2] = answerC.getText().toString();

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                postUserQuestion();
                            }
                        }, 12000);

                        Intent main = new Intent(Questions.this, Home.class);
                        startActivity(main);
                        finish();

                    }
                }
            });
        }
        A_button = (Switch) findViewById(R.id.switch1);
        A_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                B_button.setChecked(false);
                C_button.setChecked(false);
                // Perform action on click
            }
        });
        B_button = (Switch) findViewById(R.id.switch2);
        B_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                A_button.setChecked(false);
                C_button.setChecked(false);
            }
        });
        C_button = (Switch) findViewById(R.id.switch3);
        C_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                B_button.setChecked(false);
                A_button.setChecked(false);
            }
        });
        requestWithSomeHttpHeaders(email);
        getMongoDB_ID_user(facebook_id);
        getMongoDB_ID_friend(friend_facebook_id);
    }
    public String getMongoDB_ID_friend(String id_lookup) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = base_url + "users/facebook/" + id_lookup;
        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.d("TOM", response);
                        try{
                            JSONArray array = new JSONArray(response);
                            for(int i=0; i<array.length(); i++){
                                JSONObject jsonObj  = array.getJSONObject(i);
                                friend_mongo_user_id = jsonObj.getString("_id");
                                Log.d("TOM", "friend" + jsonObj.getString("_id"));
                            }
                        }
                        catch (JSONException e){
                            Log.d("TOM", "erer" + e.toString());
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d("TOM", "err" + error.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Accept", "application/json");
                return params;
            }
        };
        queue.add(postRequest);
        return result;
    }

    public String getMongoDB_ID_user(String id_lookup) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = base_url + "users/facebook/" + id_lookup;
        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
//                        Log.d("TOM", response);
                        try{
                            JSONArray array = new JSONArray(response);
                            for(int i=0; i<array.length(); i++){
                                JSONObject jsonObj  = array.getJSONObject(i);
                                mongo_user_id = jsonObj.getString("_id");
                                Log.d("TOM", "user" + jsonObj.getString("_id"));
                            }
                        }
                        catch (JSONException e){
                            Log.d("TOM", "erer" + e.toString());
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d("TOM", "err" + error.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Accept", "application/json");
                return params;
            }
        };
        queue.add(postRequest);

        return result;
    }
    public void postUserQuestion() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = base_url + "posts/";
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("TOM", "UserPOST RESPONSE:" + response);
                //This code is executed if the server responds, whether or not the response contains data.
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TOM", "UserPOST error:" + error);
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                Log.d("TOM", mongo_user_id + friend_mongo_user_id + photo_url+ questionstring + answer);
                params.put("from",mongo_user_id);
                params.put("to",friend_mongo_user_id);
                params.put("picture",photo_url);
                params.put("question",questionstring);
                Log.d("TOM", "quesiotn is " + questionstring);
                JSONArray json = new JSONArray(Arrays.asList(answers));
                params.put("choices", json.toString());
                params.put("answer",answer);
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers=new HashMap<String,String>();
                headers.put("Accept","application/json");
                headers.put("Content-Type","application/x-www-form-urlencoded");
                return headers;
            }
        };
        queue.add(MyStringRequest);

    }
    private class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            AmazonS3Client s3Client =  new AmazonS3Client( new BasicAWSCredentials( "############", "#################" ) );
            photo_url = s3Client.getResourceUrl("puzzlr-images", fileName );
            Log.d("TOM", "photourl "+ photo_url);
            Log.d("TOM", fileName.toString());
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
    public void createNewUser() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = base_url + "users/";
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //This code is executed if the server responds, whether or not the response contains data.
                Log.d("TOM",  "new user:" + response);
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TOM", "2 Errr RESPONSE:" + error);
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                String[] splited = user_name.split("\\s+");
//              Log.d("TOM", splited[0] + splited[1] + email + photo_url + facebook_id );
                params.put("firstName",splited[0]);
                params.put("lastName",splited[1]);
                params.put("email",email);
                params.put("picture",facebook_photo_url);
                params.put("facebook_id",facebook_id);
                params.put("posts_received","");
                params.put("posts_sent","");
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers=new HashMap<String,String>();
                headers.put("Accept","application/json");
                headers.put("Content-Type","application/x-www-form-urlencoded");
                return headers;
            }
        };
        queue.add(MyStringRequest);

    }

    public void requestWithSomeHttpHeaders(String email) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = base_url + "users/email/" + email;
        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
            new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response) {
                    if (response == null){
                        createNewUser();
                    }
                    else{
//                      Log.d("TOM", "old user" +  response);
                    }
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
                params.put("Accept", "application/json");
                return params;
            }
        };
        queue.add(postRequest);
    }
    private boolean check_user_data_entered() {
        if (question.getText().toString().matches("")){
            Toast bread = Toast.makeText(getApplicationContext(), "No Question Entered", Toast.LENGTH_LONG);
            bread.show();
            return false;
        }
        if (answerA.getText().toString().matches("") || answerB.getText().toString().matches("") || answerC.getText().toString().matches("")){
            Toast bread = Toast.makeText(getApplicationContext(), "Answers Not Filled", Toast.LENGTH_LONG);
            bread.show();
            return false;
        }
        if (A_button.isChecked()|| B_button.isChecked() || C_button.isChecked()){
            return true;
        }
        Toast bread = Toast.makeText(getApplicationContext(), "No Correct Choice Selected", Toast.LENGTH_LONG);
        bread.show();
        return false;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
    }
    protected void onStop() {
        super.onStop();
    }
}

