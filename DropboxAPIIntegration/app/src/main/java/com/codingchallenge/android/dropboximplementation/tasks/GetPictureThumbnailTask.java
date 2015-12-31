package com.codingchallenge.android.dropboximplementation.tasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.dropbox.client2.DropboxAPI;

import java.io.FileOutputStream;

public class GetPictureThumbnailTask extends AsyncTask<Void, Long, Bitmap> {
    private DropboxAPI<?> dropboxApi;
    private String path;
    private Context context;
    private ImageView mImageView;

    public GetPictureThumbnailTask(Context context, DropboxAPI<?> dropboxApi,
                                   String path, ImageView imv) {
        this.context = context.getApplicationContext();
        this.dropboxApi = dropboxApi;
        this.path = path;
        this.mImageView = imv;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        FileOutputStream outputStream = null;
        DropboxAPI.DropboxFileInfo info = null;
        DropboxAPI.DropboxInputStream dbThumbnailInputStream = null;

        try {
            dbThumbnailInputStream = dropboxApi.getThumbnailStream(path,
                    DropboxAPI.ThumbSize.ICON_128x128, DropboxAPI.ThumbFormat.JPEG);


        } catch (com.dropbox.client2.exception.DropboxException e) {
            e.printStackTrace();
        }

        Bitmap bitmap = null;
        if (dbThumbnailInputStream != null) {
            bitmap = BitmapFactory.decodeStream(dbThumbnailInputStream);
        }

        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        if (result != null) {
            mImageView.setImageBitmap(result);
        }
    }
}