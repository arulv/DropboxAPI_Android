package com.codingchallenge.android.dropboximplementation.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.exception.DropboxException;

import java.util.ArrayList;

public class ListPicturesTask extends AsyncTask<Void, Void, ArrayList<String>> {

	private final ProgressDialog progress;
	private DropboxAPI<?> dropboxApi;
	private String path;
	private Handler handler;
	private Context context;

	public ListPicturesTask(Context ctx, DropboxAPI<?> dropboxApi, String path, Handler handler) {
		this.dropboxApi = dropboxApi;
		this.path = path;
		this.handler = handler;
		context=ctx;
		progress = ProgressDialog.show(context, "",
				"Getting Picture Lists");
		progress.setCancelable(false);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progress.show();;
	}

	@Override
	protected ArrayList<String> doInBackground(Void... params) {
		ArrayList<String> files = new ArrayList<String>();
		try {
			Entry directory = dropboxApi.metadata(path, 1000, null, true, null);
			for (Entry entry : directory.contents) {
				files.add(entry.fileName());
			}
		} catch (DropboxException e) {
			e.printStackTrace();
		}
		return files;
	}



	@Override
	protected void onPostExecute(ArrayList<String> result) {
		if(progress.isShowing())
			progress.dismiss();
		Message message = handler.obtainMessage();
		Bundle bundle = new Bundle();
		bundle.putStringArrayList("data", result);
		message.setData(bundle);
		handler.sendMessage(message);
	}
}