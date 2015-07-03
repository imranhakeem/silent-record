package com.byteshaft.silentrecord.fragments;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;

import com.byteshaft.silentrecord.MainActivity;
import com.byteshaft.silentrecord.utils.Helpers;

import java.util.ArrayList;

public class ThumbnailCreationTask extends AsyncTask<Void, Void, Bitmap> {

    private Context mContext;
    private int mPosition;
    private VideoFragment.ViewHolder mHolder;
    private Helpers mHelpers;

    public ThumbnailCreationTask(Context context, VideoFragment.ViewHolder holder, int position) {
        mContext = context;
        mPosition = position;
        mHolder = holder;
        mHelpers = new Helpers(mContext.getApplicationContext());
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        String path = Environment.getExternalStorageDirectory().toString() + "/SpyVideos";
            Bitmap bitmap = getThumbnail(mContext.getContentResolver(), path);
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if (mHolder.position == mPosition) {
            mHolder.thumbnail.setImageBitmap(bitmap);
        }
    }

    public static Bitmap getThumbnail(ContentResolver cr, String path)  {

        Cursor ca = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] { MediaStore.MediaColumns._ID }, MediaStore.MediaColumns.DATA + "=?", new String[] {path}, null);
        if (ca != null && ca.moveToFirst()) {
            int id = ca.getInt(ca.getColumnIndex(MediaStore.MediaColumns._ID));
            ca.close();
            return MediaStore.Images.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails.MICRO_KIND, null );
        }

        ca.close();
        return null;

    }
}
