package com.byteshaft.silentrecord.fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ListFragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import com.byteshaft.silentrecord.R;
import com.byteshaft.silentrecord.utils.Helpers;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class VideoFragment extends ListFragment {

    private Helpers mHelpers;
    private View rootView;
    private Context mContext;
    private ArrayList<String> mVideoFilesNames;
    private ThumbnailCreation mListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.video_fragment, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mHelpers = new Helpers(getActivity());
        mVideoFilesNames = mHelpers.getNameFromFolder();
        mListAdapter = new ThumbnailCreation(getActivity().getApplicationContext(),
                R.layout.row, mVideoFilesNames);
        getListView().setAdapter(mListAdapter);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String path = getPathForVideo(mVideoFilesNames.get(i));
                playVideo(path);
            }
        });
        getListView().setDivider(null);
        registerForContextMenu(getListView());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle(mVideoFilesNames.get(info.position));
        String[] menuItems = {"Play", "Delete" , "Hide"};
        for (int i = 0; i < menuItems.length; i++) {
            menu.add(Menu.NONE, i, i, menuItems[i]);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)
                item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        String[] menuItems = {"Play", "Delete" , "Hide"};
        String menuItemName = menuItems[menuItemIndex];
        switch (menuItemName) {
            case "Play":
                playVideo(getPathForVideo(mVideoFilesNames.get(info.position)));
                break;
            case "Delete":
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Are You Sure");
                builder.setMessage("Deleted");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (deleteFile(getPathForVideo(mVideoFilesNames.get(info.position)))) {
                            mListAdapter.remove(mListAdapter.getItem(info.position));
                            mListAdapter.notifyDataSetChanged();
                        }
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.create();
                builder.show();
                break;
            case "Hide":
                if (hideFile(getPathForVideo(mVideoFilesNames.get(info.position)))) {
                    mListAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Could not delete file", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onContextItemSelected(item);
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
        return getExternalLocation() + "/SpyVideos/" + fileName;
    }

    private String getExternalLocation() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    private void playVideo(String filePath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data = Uri.parse("file://" + filePath);
        intent.setDataAndType(data, "video/*");
        startActivity(intent);
    }

    private boolean deleteFile(String filePath) {
        File file = new File(filePath);
        return file.delete();
    }

    private boolean hideFile(String filePath) {
        String directory = getExternalLocation();
        File file1 = new File(filePath);
        String fileNameOld = file1.getName();
        File file2 = new File(directory, "." + fileNameOld);
        return file1.renameTo(file2);
    }
}
