package com.wfu.puzzlr;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Home extends AppCompatActivity {
    String user_name;
    String friend_name;
    String user_email;
    JSONArray rawName;
    String photo_url;
    Intent main;
    String facebook_id;
    boolean doubleBackToExitPressedOnce = false;
    String facebook_photo_url;
    String friend_facebook_id;
    String mongo_user_id;
    SwipeRefreshLayout swipeContainer;
    //String base_url = "http://192.168.1.100:3000/";
    String base_url = "http://puzzlr-backend.herokuapp.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        LoginButton loginButton = (LoginButton)findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList(
                "public_profile", "email", "user_friends"));

        new GraphRequest(
            AccessToken.getCurrentAccessToken(),
            "/me/friends",
            null,
            HttpMethod.GET,
            new GraphRequest.Callback() {
                public void onCompleted(GraphResponse response) {
                try {
                    rawName = response.getJSONObject().getJSONArray("data");
//                        Log.d("TOM", rawName.toString());
                    populateFriendsList();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                }
            }
        ).executeAsync();

        GraphRequest request = GraphRequest.newMeRequest(
            AccessToken.getCurrentAccessToken(),
            new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(
                        JSONObject object,
                        GraphResponse response) {
                try{
                    user_email = object.getString("email");
                    facebook_id = object.getString("id");
                    user_name = object.getString("name");
                    Log.d("TOM", "User: http://graph.facebook.com/" + facebook_id + "/picture?type=large");
                    facebook_photo_url = "http://graph.facebook.com/" + facebook_id + "/picture?type=large";
                    getMongoDB_ID_user(facebook_id);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                    }
            });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,email");
        request.setParameters(parameters);
        request.executeAsync();

        loginButton.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LoginManager.getInstance().logOut();
                main = new Intent(Home.this, MainActivity.class);
                startActivity(main);
                finish();
            }
        });
        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new checkPOSTS().execute("exuted");
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }
    private class checkPOSTS extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            getPOSTS(mongo_user_id);
            return "Executed";
        }
        @Override
        protected void onCancelled() {

        }
        @Override
        protected void onPostExecute(String result) {
            swipeContainer.setRefreshing(false);
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    public void getMongoDB_ID_user(String id_lookup) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = base_url + "users/facebook/" + id_lookup;
        Log.d("TOM", "facebook_lookup" + url);

        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d("TOM", "facebok reponse " + response.length());
                        if (response.length() > 2) {
                            Log.d("TOM", "length more than 2");
                        } else {
                            Log.d("TOM", "length less than 2");
                            postUser();
                        }
                        try {
                            JSONArray array = new JSONArray(response);
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject jsonObj = array.getJSONObject(i);
                                mongo_user_id = jsonObj.getString("_id");
//                          Log.d("TOM", "user" + jsonObj.getString("_id"));
                                getPOSTS(mongo_user_id);
                            }
                        } catch (JSONException e) {
                            Log.d("TOM", "erer" + e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d("TOM", "err" + error.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Accept", "application/json");
                return params;
            }
        };
        queue.add(postRequest);
    }

    public void postUser() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = base_url + "users/";
        Log.d("TOM", "add user" + url);

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
            new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response) {
                    Log.d("TOM", "add reponse " + response);
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
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                String[] splited = user_name.split("\\s");
                params.put("firstName", splited[0] );
                params.put("lastName", splited[1]);
                params.put("email", user_email);
                params.put("picture",facebook_photo_url);
                params.put("facebook_id",facebook_id);
                Log.d("TOM", splited[0]+ splited[1] + user_email + facebook_photo_url+facebook_id);
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
        queue.add(postRequest);
    }

    public void getPOSTS(String id) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = base_url + "posts/to/" + id;
        Log.d("TOM", "get url " + url);
        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
//                        Log.d("TOM", "POSTS" + response);
                        if (response != null || !response.isEmpty()){
                            JSONArray friendslist;
                            ArrayList<String> posts = new ArrayList<String>();
                            try {
                                friendslist = new JSONArray(response);
                                for (int l=0; l < friendslist.length(); l++) {
                                    posts.add(friendslist.getJSONObject(l).getString("from"));
                                }
                                for (int l=0; l < friendslist.length(); l++) {
                                    getUserName(friendslist.getJSONObject(l).getString("from"));
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d("TOM", "get " + error.toString());
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

    ArrayList<String> friends_id ;
    ArrayList<String> friends ;
    private void populateFriendsList(){
        String jsondata = rawName.toString(); // receiving data from  MainActivity.java
        JSONArray friendslist;
        friends = new ArrayList<String>();
        JSONArray friendslist_id = null;
        friends_id = new ArrayList<String>();
        try {
            friendslist = new JSONArray(jsondata);
            friendslist_id = new JSONArray(jsondata);
            for (int l=0; l < friendslist.length(); l++) {
                friends.add(friendslist.getJSONObject(l).getString("name"));
                friends_id.add(friendslist_id.getJSONObject(l).getString("id"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        Log.d("TOM", friends_id.toString());
        // adapter which populate the friends in listview
        ArrayAdapter adapter2 = new ArrayAdapter<String>(this, R.layout.activity_listview, friends);
        final ListView listView = (ListView) findViewById(R.id.listView2);
        listView.setAdapter(adapter2);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            friend_name=(String)parent.getItemAtPosition(position);
//          Log.d("TOM", friend_name);
//          friend_facebook_photo_url = "http://graph.facebook.com/" + friends_id.get(position) + "/picture?type=large";
            friend_facebook_id =  friends_id.get(position);
            main = new Intent(Home.this, ProfileView.class);
            main.putExtra("photo", photo_url);
            main.putExtra("friend_name", friend_name);
            main.putExtra("user_email", user_email);
            main.putExtra("facebook_id", facebook_id);
            main.putExtra("user_name", user_name);
            main.putExtra("facebook_photo_url", facebook_photo_url);
            main.putExtra("friend_facebook_id", friend_facebook_id);
            startActivity(main);
            }
        });
    }

    ArrayList<String> puzzlrs = new ArrayList<String>();
    ArrayList<String> puzzlr_nums = new ArrayList<String>();

    private void add_New_Puzzlr(String name){
        if (puzzlrs.isEmpty()){
            puzzlrs.add(name);
        }
        else {
            Set<String> uniqueSet = new HashSet<String>(puzzlrs);
            for (String temp : uniqueSet) {
                Log.d("TOM", temp + ": " + Collections.frequency(puzzlrs, temp));
            }

        }
        for ( int w = 0 ; w<puzzlrs.size();w++){

        }
        ArrayAdapter adapter3 = new ArrayAdapter<String>(this, R.layout.activity_listview, puzzlrs);
        final ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter3);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            friend_name=(String)parent.getItemAtPosition(position);
            Log.d("TOM", friend_name );
            Intent view_pic;
            view_pic = new Intent(Home.this, AnswerQuestion.class);
            view_pic.putExtra("mongo_user_id", mongo_user_id);
            view_pic.putExtra("friend_name", friend_name);
            int i = 0;
            for (String string : friends) {
                if(string.matches(friend_name)){
                    break;
                }
                i++;
            }
            friend_facebook_id = friends_id.get(i);
            view_pic.putExtra("friend_facebook_id", friend_facebook_id);
            startActivity(view_pic);
            }
        });
    }
    public void getUserName(String id) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = base_url + "users/" + id ;
//        Log.d("TOM", url);
        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
            new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response) {
                    Log.d("TOM", response);
                    try {
                        //Do it with this it will work
                        JSONObject person = new JSONObject(response);
                        String firstName = person.getString("firstName");
                        String lastName = person.getString("lastName");
                        String name = firstName + " " + lastName;
                        Log.d("TOM", name);
                        add_New_Puzzlr(name);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
    }

    @Override
    public void onBackPressed() {
    }
}
