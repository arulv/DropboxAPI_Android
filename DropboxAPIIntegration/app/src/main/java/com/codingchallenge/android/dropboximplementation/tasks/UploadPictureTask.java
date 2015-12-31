package com.codingchallenge.android.dropboximplementation.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class UploadPictureTask extends AsyncTask<Void, Long, Boolean> {

	private DropboxAPI<?> dropboxApi;
	private File path;
	private Context context;
	private String dropBoxFolderName;
	ProgressDialog progress;

	public UploadPictureTask(Context context, DropboxAPI<?> dropboxApi,
							 File path, String aDroboxFolderName) {
		this.context = context.getApplicationContext();
		this.dropboxApi = dropboxApi;
		this.dropBoxFolderName = aDroboxFolderName;
		this.path = path;
		progress = ProgressDialog.show(context, "",
				"Uploading Picture");
		progress.setCancelable(false);

	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progress.show();

	}

	@Override
	protected Boolean doInBackground(Void... params) {
		try {
			// Uploading the newly created file to Dropbox.
			FileInputStream fileInputStream = new FileInputStream(this.path);
			dropboxApi.putFile(dropBoxFolderName+path.getName(), fileInputStream,
					path.length(), null, null);
			path.delete();
			
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DropboxException e) {
			e.printStackTrace();
		}
		
		return false;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		if(progress.isShowing())
		    progress.dismiss();
		if (result) {
			Toast.makeText(context, "File has been successfully uploaded!",
					Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(context, "An error occured while processing the upload request.",
					Toast.LENGTH_LONG).show();
		}
	}
}