package com.codingchallenge.android.dropboximplementation.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.dropbox.client2.DropboxAPI;

import java.io.FileOutputStream;

public class ThumbnailHelper {
    public static Bitmap getThumbnailBitmap( DropboxAPI<?> dropboxApi, String path) {
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
}