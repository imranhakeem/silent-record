package com.byteshaft.silentrecord.fragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class VideoFragment extends Fragment {

    private Helpers mHelpers;
    private View rootView;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.video_fragment, container, false);
        mHelpers = new Helpers(getActivity());
        ListView listView = (ListView) rootView.findViewById(R.id.video_list);
        final ArrayList<String> arrayList = mHelpers.getNameFromFolder();
        listView.setAdapter(new ThumbnailCreation(getActivity().getApplicationContext(),
                R.layout.row, arrayList));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String path = getPathForVideo(arrayList.get(i));
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri data = Uri.parse("file://" + path);
                intent.setDataAndType(data, "video/*");
                startActivity(intent);
            }
        });
        return rootView;
    }

    static class ViewHolder {
        public TextView fileName;
        public ImageView thumbnail;
        public ImageLoader imageLoader;
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
            final ViewHolder holder;
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.row, parent, false);
                holder = new ViewHolder();
                holder.fileName = (TextView) convertView.findViewById(R.id.tv);
                holder.thumbnail = (ImageView) convertView.findViewById(R.id.Thumbnail);
                DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).build();
                ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getActivity().getApplicationContext()).defaultDisplayImageOptions(defaultOptions).build();
                ImageLoader.getInstance().init(config);
                holder.imageLoader = ImageLoader.getInstance();
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.position = position;
            holder.fileName.setText(mVideos.get(position));
            String path = "file://" + getPathForVideo(mVideos.get(position));
            ImageSize targetSize = new ImageSize(80, 50);
            holder.imageLoader.loadImage(path, targetSize, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    holder.thumbnail.setImageBitmap(loadedImage);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });
            return convertView;
        }
    }

    private String getPathForVideo(String fileName) {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/SpyVideos/" + fileName;
    }
}
