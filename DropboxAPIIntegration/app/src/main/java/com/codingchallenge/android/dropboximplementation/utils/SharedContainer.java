package com.codingchallenge.android.dropboximplementation.utils;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;

public class SharedContainer {

    private static SharedContainer instance;
    private DropboxAPI<AndroidAuthSession> dropboxApi;

    public static synchronized SharedContainer getInstance() {
        if (instance == null) {
            // Create the instance
            instance = new SharedContainer();
        }
        return instance;
    }

    public DropboxAPI<AndroidAuthSession> getDropboxApi() {
        return dropboxApi;
    }

    public void setDropboxApi(DropboxAPI<AndroidAuthSession> dropboxApi) {
        this.dropboxApi = dropboxApi;
    }

}