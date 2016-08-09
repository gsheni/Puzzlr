package com.wfu.puzzlr;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.login.widget.ProfilePictureView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gaurav on 4/27/2016.
 */
public class AnswerQuestion extends AppCompatActivity {

    String base_url = "http://puzzlr-backend.herokuapp.com/";
    Intent intent;
    String user_mongo_id;
    String question;
    String answer;
    String choices;
    String picture;
    TextView questionfill;
    String friend_name;
    TextView set_friend_name;
    ProfilePictureView profilePictureView;
    String friend_facebook_id;
    Button A_button;
    Button B_button;
    Button C_button;
    String question_mongo_id;
    Intent see_pic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.answer);
        intent = getIntent();
        user_mongo_id = intent.getStringExtra("mongo_user_id");
        friend_name = intent.getStringExtra("friend_name");
        friend_facebook_id = intent.getStringExtra("friend_facebook_id");
        getPOSTS(user_mongo_id);
        profilePictureView = (ProfilePictureView) findViewById(R.id.friend_profile);
        questionfill = (TextView)findViewById(R.id.question_fill);
        A_button = (Button)findViewById(R.id.choiceA);
        B_button = (Button)findViewById(R.id.choiceB);
        C_button = (Button)findViewById(R.id.choiceC);
        set_friend_name = (TextView) findViewById(R.id.friend_name);
        set_friend_name.setText(friend_name );
        profilePictureView.setProfileId(friend_facebook_id);

        A_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (answer.matches("A")){
                    see_pic = new Intent(AnswerQuestion.this, PictureView.class);
                    see_pic.putExtra("picture", picture);
                    see_pic.putExtra("question_mongo_id", question_mongo_id);
                    startActivity(see_pic);
                }
                else{
                    new LongOperation().execute("");
                    Intent main = new Intent(AnswerQuestion.this, Home.class);
                    startActivity(main);
                    finish();
                }
            }
        });
        Log.d("TOM", "BB11B");
        B_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (answer.matches("B")){
                    see_pic = new Intent(AnswerQuestion.this, PictureView.class);
                    see_pic.putExtra("picture", picture);
                    see_pic.putExtra("question_mongo_id", question_mongo_id);
                    startActivity(see_pic);
                }
                else{
                    new LongOperation().execute("");
                    Intent main = new Intent(AnswerQuestion.this, Home.class);
                    startActivity(main);
                    finish();
                }
            }
        });
        C_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (answer.matches("C")){
                    see_pic = new Intent(AnswerQuestion.this, PictureView.class);
                    see_pic.putExtra("picture", picture);
                    see_pic.putExtra("question_mongo_id", question_mongo_id);
                    startActivity(see_pic);
                }
                else{
                    new LongOperation().execute("");
                    Intent main = new Intent(AnswerQuestion.this, Home.class);
                    startActivity(main);
                    finish();
                }
            }
        });
    }

    public void getPOSTS(String id) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = base_url + "posts/to/" + id;
        Log.d("TOM", "url " + url);
        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
            new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response) {
                    Log.d("TOM", "1-POSTS" + response);
                    try{
                        JSONArray array = new JSONArray(response);
                        for(int i=0; i<array.length(); i++){
                            JSONObject jsonObj  = array.getJSONObject(i);
                            question_mongo_id = jsonObj.getString("_id");
                            question = jsonObj.getString("question");
                            answer = jsonObj.getString("answer");
                            choices = jsonObj.getString("choices");
                            String[] choiceArray = choices.split(",");
                            picture = jsonObj.getString("picture");
                            String temp = choiceArray[0].replace("\"", "");
                            A_button.setText(temp.replaceAll("\\p{P}",""));
                            temp = choiceArray[1].replace("\"", "");
                            B_button.setText(temp.replaceAll("\\p{P}",""));
                            temp = choiceArray[2].replace("\"", "");
                            C_button.setText(temp.replaceAll("\\p{P}",""));
                            questionfill.setText(question);
                            Log.d("TOM", "ans" + answer);
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
