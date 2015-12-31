package com.codingchallenge.android.dropboximplementation.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.ImageView;

import com.dropbox.client2.DropboxAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class GetPictureDetailTask extends AsyncTask<Void, Long, Bitmap> {

	private final ProgressDialog progress;
	private DropboxAPI<?> dropboxApi;
	private String path;
	private Context context;
	private ImageView mImageView;

	public GetPictureDetailTask(Context context, DropboxAPI<?> dropboxApi,
								String path, ImageView imv) {
		this.context = context.getApplicationContext();
		this.dropboxApi = dropboxApi;
		this.path = path;
		this.mImageView=imv;

		progress = ProgressDialog.show(context, "",
				"Getting Picture Details");
		progress.setCancelable(false);
	}


	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progress.show();
	}

	@Override
	protected Bitmap doInBackground(Void... params) {
		FileOutputStream outputStream = null;
		DropboxAPI.DropboxFileInfo info=null;
		DropboxAPI.DropboxInputStream dbInputStream = null;
		File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/tmp.jpg");
		try {
			 outputStream = new FileOutputStream(file);
		}
		catch(FileNotFoundException e) {
			e.printStackTrace();
		}
			try {
				 info = dropboxApi.getFile(path, null, outputStream, null);
				if (info !=null)
				    dbInputStream =dropboxApi.getFileStream(info.getMetadata().path,
						 info.getMetadata().rev);

			}catch (com.dropbox.client2.exception.DropboxException e)
			{
				e.printStackTrace();
			}

		Bitmap bitmap = null;
		if (dbInputStream !=null) {
			bitmap = BitmapFactory.decodeStream(dbInputStream);
		}

		return bitmap;
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		if(progress.isShowing())
			progress.dismiss();
		if (result !=null) {
					mImageView.setImageBitmap(result);
		}
	}
}