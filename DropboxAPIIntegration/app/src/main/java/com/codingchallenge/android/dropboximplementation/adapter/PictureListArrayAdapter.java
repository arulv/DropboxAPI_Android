package com.codingchallenge.android.dropboximplementation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codingchallenge.android.dropboximplementation.R;
import com.codingchallenge.android.dropboximplementation.constant.AppConstant;
import com.codingchallenge.android.dropboximplementation.tasks.GetPictureThumbnailTask;
import com.codingchallenge.android.dropboximplementation.utils.BitmapManager;
import com.dropbox.client2.DropboxAPI;

import java.util.ArrayList;

public class PictureListArrayAdapter extends ArrayAdapter<String> {
    private GetPictureThumbnailTask getThumbnailTask;
    private ArrayList<String> objects;
    private Context ctx;
    private DropboxAPI<?> dropboxApi;

    public PictureListArrayAdapter(Context context, int textViewResourceId, ArrayList<String> objects, DropboxAPI<?> dropboxApi) {
        super(context, textViewResourceId, objects);
        this.objects = objects;
        this.dropboxApi = dropboxApi;
        ctx = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderItem viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.simple_row_item, null);

            viewHolder = new ViewHolderItem();
            viewHolder.textViewItem = (TextView) convertView.findViewById(R.id.fileName);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.thumbnail);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderItem) convertView.getTag();
        }


        String aFileName = objects.get(position);
        if (aFileName != null) {

            viewHolder.textViewItem.setText(aFileName);
            viewHolder.textViewItem.setTag(position);
            viewHolder.imageView.setImageResource(R.drawable.place_holder);

            BitmapManager.INSTANCE.loadBitmap(AppConstant.DROPBOX_PICTURE_DIR+aFileName, viewHolder.imageView, 64,
                    64, dropboxApi);

        }
        return convertView;
    }


    static class ViewHolderItem {
        ImageView imageView;
        TextView textViewItem;
    }


}