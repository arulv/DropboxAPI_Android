package com.codingchallenge.android.dropboximplementation.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.codingchallenge.android.dropboximplementation.constant.AppConstant;
import com.codingchallenge.android.dropboximplementation.R;
import com.codingchallenge.android.dropboximplementation.tasks.GetPictureDetailTask;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.TokenPair;

public class PictureDetailActivity extends Activity {

    private LinearLayout container;
    private DropboxAPI<AndroidAuthSession> dropboxApi;
    private ImageView imageView;
    private String fileName;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_detail_layout);
        fileName = AppConstant.DROPBOX_PICTURE_DIR + getIntent().getExtras().getString("FILE_NAME");

        imageView = (ImageView) findViewById(R.id.imageDetailView);


        AppKeyPair appKeyPair = new AppKeyPair(AppConstant.ACCESS_KEY, AppConstant.ACCESS_SECRET);
        AndroidAuthSession session;

        SharedPreferences prefs = getSharedPreferences(AppConstant.DROPBOX_NAME, 0);
        String key = prefs.getString(AppConstant.ACCESS_KEY, null);
        String secret = prefs.getString(AppConstant.ACCESS_SECRET, null);

        if (key != null && secret != null) {
            AccessTokenPair token = new AccessTokenPair(key, secret);
            session = new AndroidAuthSession(appKeyPair, AppConstant.ACCESS_TYPE, token);
        } else {
            session = new AndroidAuthSession(appKeyPair, AppConstant.ACCESS_TYPE);
        }

        dropboxApi = new DropboxAPI<AndroidAuthSession>(session);
    }

    @Override
    protected void onResume() {
        super.onResume();

        AndroidAuthSession session = dropboxApi.getSession();
        if (session.authenticationSuccessful()) {
            try {
                session.finishAuthentication();

                TokenPair tokens = session.getAccessTokenPair();
                SharedPreferences prefs = getSharedPreferences(AppConstant.DROPBOX_NAME, 0);
                Editor editor = prefs.edit();
                editor.putString(AppConstant.ACCESS_KEY, tokens.key);
                editor.putString(AppConstant.ACCESS_SECRET, tokens.secret);
                editor.commit();

                GetPictureDetailTask getPictureDetailTask = new GetPictureDetailTask(this, dropboxApi, fileName, imageView);
                getPictureDetailTask.execute();

            } catch (IllegalStateException e) {
                Toast.makeText(this, "Error during Dropbox authentication",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

}