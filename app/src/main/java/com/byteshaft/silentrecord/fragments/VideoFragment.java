package com.byteshaft.silentrecord.fragments;


import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import com.byteshaft.silentrecord.R;
import com.byteshaft.silentrecord.utils.Helpers;

public class VideoFragment extends Fragment {

    private Helpers mHelpers;
    private View rootView;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.video_fragment, container, false);
        mHelpers = new Helpers(getActivity());
        ListView listView = (ListView) rootView.findViewById(R.id.video_list);
        ArrayList<String> arrayList = mHelpers.getNameFromFolder();
        listView.setAdapter(new ThumbnailCreation(
                getActivity().getApplicationContext(),
                R.layout.row, arrayList));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
        return rootView;
    }

    static class ViewHolder {
        public TextView fileName;
        public ImageView thumbnail;
        public int position;
    }

    class ThumbnailCreation extends ArrayAdapter<String> {

        private ArrayList<String> mVideos;

        public ThumbnailCreation(Context context, int resource, ArrayList<String> videos) {
            super(context, resource, videos);
            mContext = context;
            mVideos = videos;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.row, parent, false);
                holder = new ViewHolder();
                holder.fileName = (TextView) convertView.findViewById(R.id.tv);
                holder.thumbnail = (ImageView) convertView.findViewById(R.id.Thumbnail);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.fileName.setText(mVideos.get(position));
            holder.position = position;
            holder.thumbnail.setImageBitmap(getBitmapFromUri(getPathForVideo(mVideos.get(position))));
            return convertView;
        }
    }

    private String getPathForVideo(String fileName) {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/SpyVideos/" + fileName;
    }

    private Bitmap getBitmapFromUri(String path) {
        return ThumbnailUtils.createVideoThumbnail(path,
                MediaStore.Images.Thumbnails.MICRO_KIND);
    }

}
