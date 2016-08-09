package com.wfu.puzzlr;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.facebook.login.widget.ProfilePictureView;

import java.io.File;

/**
 * Created by Gaurav on 4/26/2016.
 */
public class ProfileView extends AppCompatActivity {
    Intent intent;
    String friend_name;
    String user_email;
    String photo_url;
    String facebook_id;
    String user_name;
    String facebook_photo_url;
    String friend_facebook_id;
    Button button;
    String fileName;
    TextView name_friend;
    ProfilePictureView profilePictureView;
    File file;
    Uri photoPath;
    private static final int CAMERA_REQUEST = 1888;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_view);
        verifyStoragePermissions(this);
        profilePictureView = (ProfilePictureView) findViewById(R.id.profile_image);
        name_friend = (TextView) findViewById(R.id.textView6);
        intent = getIntent();
        friend_name = intent.getStringExtra("friend_name");
        facebook_photo_url = intent.getStringExtra("facebook_photo_url");
        friend_facebook_id = intent.getStringExtra("friend_facebook_id");
        name_friend.setText(friend_name);
        profilePictureView.setProfileId(friend_facebook_id);
//        Log.d("TOM", friend_facebook_photo_url);
//        Log.d("TOM", "http://graph.facebook.com/" + friend_facebook_photo_url + "/picture?type=large");

        button = (Button) findViewById(R.id.submit_button);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    // this part to save captured image on provided path
                    file = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + "-puzzlr.jpg");
                    fileName = file.getName();
                    photoPath = Uri.fromFile(file);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoPath);
//                  Log.d("TOM", file.getAbsolutePath());
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            });
        }
        new LongOperation().execute("");

    }

    private class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            user_email = intent.getStringExtra("user_email");
            facebook_id = intent.getStringExtra("facebook_id");
            user_name = intent.getStringExtra("user_name");
            photo_url = intent.getStringExtra("photo");
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
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            AsyncTaskRunner runner = new AsyncTaskRunner();
            runner.execute("run");
        }
        intent = new Intent(ProfileView.this, Questions.class);
        intent.putExtra("friend_name", friend_name);
        intent.putExtra("user_email", user_email);
        intent.putExtra("fileName", fileName);
        intent.putExtra("facebook_id", facebook_id);
        intent.putExtra("friend_facebook_id", friend_facebook_id);
        intent.putExtra("user_name", user_name);
        intent.putExtra("facebook_photo_url", facebook_photo_url);
        startActivity(intent);
        finish();
    }
    private class AsyncTaskRunner extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            AmazonS3Client s3Client =  new AmazonS3Client( new BasicAWSCredentials( "#################", "#################" ) );
            ObjectMetadata metaData = new ObjectMetadata();
            metaData.setContentType("jpeg"); //binary data
//          Log.d("TOM", file.getPath().toString());
            PutObjectRequest por = new PutObjectRequest( "puzzlr-images", file.getName(), new java.io.File(file.getPath())).withCannedAcl(CannedAccessControlList.PublicRead);
            s3Client.putObject( por );
//            photo_url = s3Client.getResourceUrl("puzzlr-images",file.getName() );
            return null;
        }
    }
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
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
