package com.codingchallenge.android.dropboximplementation.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.codingchallenge.android.dropboximplementation.constant.AppConstant;
import com.codingchallenge.android.dropboximplementation.R;
import com.codingchallenge.android.dropboximplementation.tasks.UploadPictureTask;
import com.codingchallenge.android.dropboximplementation.utils.SharedContainer;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.TokenPair;

import java.io.File;

public class MainActivity extends Activity implements OnClickListener {

	private DropboxAPI<AndroidAuthSession> dropboxApi;
	private boolean isUserLoggedIn;
	private Button loginBtn;
	private Button takePictureBtn;
	private Button showPictureBtn;
	private String currentPictureName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		loginBtn = (Button) findViewById(R.id.loginBtn);
		loginBtn.setOnClickListener(this);
		takePictureBtn = (Button)findViewById(R.id.takePicture);
		takePictureBtn.setOnClickListener(this);
		showPictureBtn = (Button)findViewById(R.id.showPicture);
		showPictureBtn.setOnClickListener(this);

		
		loggedIn(false);

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
		SharedContainer.getInstance().setDropboxApi(dropboxApi);
	}

	@Override
	protected void onResume() {
		super.onResume();

		AndroidAuthSession session = SharedContainer.getInstance().getDropboxApi().getSession();
		if (session.authenticationSuccessful()) {
			try {
				session.finishAuthentication();

				TokenPair tokens = session.getAccessTokenPair();
				SharedPreferences prefs = getSharedPreferences(AppConstant.DROPBOX_NAME, 0);
				Editor editor = prefs.edit();
				editor.putString(AppConstant.ACCESS_KEY, tokens.key);
				editor.putString(AppConstant.ACCESS_SECRET, tokens.secret);
				editor.commit();

				loggedIn(true);
			} catch (IllegalStateException e) {
				Toast.makeText(this, "Error during Dropbox authentication",
						Toast.LENGTH_SHORT).show();
			}
		}
	}



	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.loginBtn:
			if (isUserLoggedIn) {
				dropboxApi.getSession().unlink();
				loggedIn(false);
			} else {
				dropboxApi.getSession().startAuthentication(MainActivity.this);
			}
			break;

			case R.id.takePicture:
				currentPictureName ="DrpBox_"+System.currentTimeMillis()+".jpg";
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				File f = new File(android.os.Environment.getExternalStorageDirectory(), currentPictureName);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
				startActivityForResult(intent, 1);
				break;

			case R.id.showPicture:
				Intent anIntent = new Intent(MainActivity.this, PictureListActivity.class);
				startActivity(anIntent);
				break;
		default:
			break;
		}
	}
	
	private void loggedIn(boolean userLoggedIn) {
		isUserLoggedIn = userLoggedIn;
		showPictureBtn.setEnabled(userLoggedIn);
		showPictureBtn.setBackgroundColor(userLoggedIn ? Color.BLUE : Color.GRAY);
		takePictureBtn.setEnabled(userLoggedIn);
		takePictureBtn.setBackgroundColor(userLoggedIn ? Color.BLUE : Color.GRAY);
		loginBtn.setText(userLoggedIn ? "Logout" : "Log in");
		loginBtn.setBackgroundColor(userLoggedIn ? Color.BLUE : Color.GRAY);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == 1) {
				File f = new File(Environment.getExternalStorageDirectory().toString()+"/"+currentPictureName);
				if(f.exists())
				{
					UploadPictureTask uploadFile = new UploadPictureTask(MainActivity.this, dropboxApi,
							f,AppConstant.DROPBOX_PICTURE_DIR);
					uploadFile.execute();
				}
			}
		}
	}
}