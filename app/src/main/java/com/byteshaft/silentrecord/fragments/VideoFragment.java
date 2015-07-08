package com.byteshaft.silentrecord.fragments;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
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

import com.byteshaft.silentrecord.R;
import com.byteshaft.silentrecord.utils.AppConstants;
import com.byteshaft.silentrecord.utils.Helpers;
import com.byteshaft.silentrecord.utils.VideoFragmentHelpers;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

public class VideoFragment extends ListFragment {

    private Context mContext;
    private ArrayList<String> mFilesNames;
    private ThumbnailCreation mListAdapter;
    private String mContentType;
    private VideoFragmentHelpers mHelpers;

    public VideoFragment() {
        super();
    }

    @SuppressLint("ValidFragment")
    public VideoFragment(String contentType) {
        this();
        mContentType = contentType;
        mHelpers = new VideoFragmentHelpers(mContentType);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.video_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String path = mHelpers.getPathForFile(mFilesNames.get(i));
                mHelpers.openContent(getActivity(), path);
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
        String[] menuItems = {
                mHelpers.getExecuteText(),
                "Delete",
                mHelpers.getVisibilityText(mFilesNames.get(info.position))
        };
        for (int i = 0; i < menuItems.length; i++) {
            menu.add(Menu.NONE, i, i, menuItems[i]);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)
                item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        String[] menuItems = {
                mHelpers.getExecuteText(),
                "Delete",
                mHelpers.getVisibilityText(mFilesNames.get(info.position))
        };
        String menuItemName = menuItems[menuItemIndex];
        switch (menuItemName) {
            case "Play" :
            case "View":
                mHelpers.openContent(getActivity(), mHelpers.getPathForFile(mFilesNames.get(info.position)));
                break;
            case "Delete":
                showFileDeleteDialog(info);
                break;
            case AppConstants.TEXT_FILE_HIDE:
                if (mHelpers.hideFile(mFilesNames.get(info.position))) {
                    mListAdapter.notifyDataSetChanged();
                }
                break;
            case AppConstants.TEXT_FILE_SHOW:
                if (mHelpers.unHideFile(mFilesNames.get(info.position))) {
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
            String path = "file://" + mHelpers.getPathForFile(mFiles.get(position));
            mImageLoader.displayImage(path, holder.thumbnail, mOptions, null);
            return convertView;
        }
    }

    private void showFileDeleteDialog(final AdapterView.AdapterContextMenuInfo info) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Confirm Delete");
        builder.setMessage("Do you want to delete this file ?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (mHelpers.deleteFile(mHelpers.getPathForFile(mFilesNames.get(info.position)))) {
                    mListAdapter.remove(mListAdapter.getItem(info.position));
                    mListAdapter.notifyDataSetChanged();
                    Toast.makeText(mContext, "file deleted", Toast.LENGTH_SHORT).show();
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
    }

    @Override
    public void onResume() {
        super.onResume();
        mFilesNames = Helpers.getFileNamesFromDirectory(mContentType);
        mListAdapter = new ThumbnailCreation(getActivity().getApplicationContext(),
                R.layout.row, mFilesNames);
        getListView().setAdapter(mListAdapter);
    }
}
