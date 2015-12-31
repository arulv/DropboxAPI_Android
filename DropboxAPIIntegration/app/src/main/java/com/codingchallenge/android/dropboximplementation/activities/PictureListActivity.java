package com.codingchallenge.android.dropboximplementation.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.codingchallenge.android.dropboximplementation.constant.AppConstant;
import com.codingchallenge.android.dropboximplementation.R;
import com.codingchallenge.android.dropboximplementation.tasks.ListPicturesTask;
import com.codingchallenge.android.dropboximplementation.utils.SharedContainer;
import com.codingchallenge.android.dropboximplementation.adapter.PictureListArrayAdapter;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.TokenPair;

import java.util.ArrayList;

public class PictureListActivity extends Activity {

    private LinearLayout container;
    private DropboxAPI<AndroidAuthSession> dropboxApi;
    private ListView listView;
    private PictureListArrayAdapter customArrayAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_list_layout);
        listView = (ListView) findViewById(R.id.pictureList);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent anIntent = new Intent(PictureListActivity.this, PictureDetailActivity.class);
                anIntent.putExtra("FILE_NAME", ((TextView) view).getText());
                startActivity(anIntent);
            }
        });

        dropboxApi = SharedContainer.getInstance().getDropboxApi();
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

                ListPicturesTask listPicturesTask = new ListPicturesTask(PictureListActivity.this, dropboxApi, AppConstant.DROPBOX_PICTURE_DIR,
                        handlerPictureList);
                listPicturesTask.execute();

            } catch (IllegalStateException e) {
                Toast.makeText(this, "Error during Dropbox authentication",
                        Toast.LENGTH_SHORT).show();
                gotoLogin();
            }
        } else
            gotoLogin();
    }


    private void gotoLogin() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private final Handler handlerPictureList = new Handler() {
        public void handleMessage(Message message) {
            ArrayList<String> result = message.getData().getStringArrayList("data");
            if(result !=null && result.size()>0) {
                ((TextView) findViewById(R.id.emptyPicturesTv)).setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
                customArrayAdapter = new PictureListArrayAdapter(PictureListActivity.this, R.layout.simple_row_item, result, dropboxApi);
                listView.setAdapter(customArrayAdapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        Intent anIntent = new Intent(PictureListActivity.this, PictureDetailActivity.class);
                        anIntent.putExtra("FILE_NAME", ((TextView) view.findViewById(R.id.fileName)).getText());
                        startActivity(anIntent);
                    }
                });
                customArrayAdapter.notifyDataSetChanged();
            }
            else {
                ((TextView) findViewById(R.id.emptyPicturesTv)).setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
            }
        }
    };


}