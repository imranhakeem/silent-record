package com.byteshaft.silentrecord.fragments;


import android.annotation.SuppressLint;
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
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import com.byteshaft.silentrecord.AppGlobals;
import com.byteshaft.silentrecord.R;
import com.byteshaft.silentrecord.utils.Helpers;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class VideoFragment extends ListFragment {

    private Context mContext;
    private ArrayList<String> mFilesNames;
    private ThumbnailCreation mListAdapter;
    private String mContentType;
    private final String TEXT_FILE_SHOW = "Show in other Apps";
    private final String TEXT_FILE_HIDE = "Hide in other Apps";

    public VideoFragment() {
        super();
    }

    @SuppressLint("ValidFragment")
    public VideoFragment(String contentType) {
        this();
        mContentType = contentType;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.video_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFilesNames = Helpers.getFileNamesFromDirectory(mContentType);
        mListAdapter = new ThumbnailCreation(getActivity().getApplicationContext(),
                R.layout.row, mFilesNames);
        getListView().setAdapter(mListAdapter);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String path = getPathForFile(mFilesNames.get(i));
                openContent(path);
            }
        });
        getListView().setDivider(null);
        registerForContextMenu(getListView());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle(mFilesNames.get(info.position));
        String[] menuItems = {getExecuteText(), "Delete" , getVisibilityText(mFilesNames.get(info.position))};
        for (int i = 0; i < menuItems.length; i++) {
            menu.add(Menu.NONE, i, i, menuItems[i]);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)
                item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        String[] menuItems = {getExecuteText(), "Delete" , getVisibilityText(mFilesNames.get(info.position))};
        String menuItemName = menuItems[menuItemIndex];
        switch (menuItemName) {
            case "Play" :
            case "View":
                openContent(getPathForFile(mFilesNames.get(info.position)));
                break;
            case "Delete":
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Are You Sure");
                builder.setMessage("Delete");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (deleteFile(getPathForFile(mFilesNames.get(info.position)))) {
                            mListAdapter.remove(mListAdapter.getItem(info.position));
                            mListAdapter.notifyDataSetChanged();
                            Toast.makeText(mContext, "Deleted", Toast.LENGTH_SHORT).show();
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
            case TEXT_FILE_HIDE:
                if (hideFile(mFilesNames.get(info.position))) {
                    mListAdapter.notifyDataSetChanged();
                }
                break;
            case TEXT_FILE_SHOW:
                if (unHideFile(mFilesNames.get(info.position))) {
                    mListAdapter.notifyDataSetChanged();
                }
                break;


        }
        return super.onContextItemSelected(item);
    }

    static class ViewHolder {
        public TextView fileName;
        public ImageView thumbnail;
        public int position;
    }

    class ThumbnailCreation extends ArrayAdapter<String> {

        private ArrayList<String> mFiles;
        private DisplayImageOptions mOptions;
        private ImageLoader mImageLoader;

        public ThumbnailCreation(Context context, int resource, ArrayList<String> files) {
            super(context, resource, files);
            mContext = context;
            mFiles = files;
            mOptions = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .build();
            mImageLoader = ImageLoader.getInstance();
            mImageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
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
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.position = position;
            holder.fileName.setText(mFiles.get(position));
            holder.thumbnail.setImageBitmap(null);
            String path = "file://" + getPathForFile(mFiles.get(position));
            mImageLoader.displayImage(path, holder.thumbnail, mOptions, null);
            return convertView;
        }
    }

    private String getPathForFile(String fileName) {
        String directory = null;
        switch (mContentType) {
            case AppGlobals.DIRECTORY.VIDEOS:
                directory = AppGlobals.DIRECTORY.VIDEOS;
                break;
            case AppGlobals.DIRECTORY.PICTURES:
                directory = AppGlobals.DIRECTORY.PICTURES;
                break;
        }
        return getExternalLocation()
                + File.separator
                + directory
                + File.separator
                + fileName;
    }

    private String getExternalLocation() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    private void openContent(String filePath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data = Uri.parse("file://" + filePath);
        switch (mContentType) {
            case AppGlobals.DIRECTORY.VIDEOS:
                intent.setDataAndType(data, "video/*");
                break;
            case AppGlobals.DIRECTORY.PICTURES:
                intent.setDataAndType(data, "image/*");
                break;
        }
        startActivity(intent);
    }

    private boolean deleteFile(String filePath) {
        File file = new File(filePath);
        return file.delete();
    }

    private boolean hideFile(String fileName) {
        File directory = null;
        switch (mContentType) {
            case AppGlobals.DIRECTORY.VIDEOS:
                directory = AppGlobals.getVideosDirectory();
                break;
            case AppGlobals.DIRECTORY.PICTURES:
                directory = AppGlobals.getPicturesDirectory();
                break;
        }
        File file1 = new File(directory, fileName);
        if (!fileName.startsWith(".")) {
            File file2 = new File(directory, "." + fileName);
            return file1.renameTo(file2);
        } else {
            return false;
        }
    }

    private boolean unHideFile(String fileName) {
        File directory = null;
        switch (mContentType) {
            case AppGlobals.DIRECTORY.VIDEOS:
                directory = AppGlobals.getVideosDirectory();
                break;
            case AppGlobals.DIRECTORY.PICTURES:
                directory = AppGlobals.getPicturesDirectory();
                break;
        }
        File file1 = new File(directory, fileName);
        if (fileName.startsWith(".")) {
            File file2 = new File(directory, fileName.substring(1));
            return file1.renameTo(file2);
        } else {
            return false;
        }
    }

    private String getExecuteText() {
        switch (mContentType) {
            case AppGlobals.DIRECTORY.VIDEOS:
                return "Play";
            case AppGlobals.DIRECTORY.PICTURES:
                return "View";
            default:
                return null;
        }
    }

    private String getVisibilityText(String fileName) {
        if (fileName.startsWith(".")) {
            return TEXT_FILE_SHOW;
        } else {
            return TEXT_FILE_HIDE;
        }
    }
}
