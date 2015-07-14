package com.byteshaft.silentrecord.fragments;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.byteshaft.silentrecord.AppGlobals;
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
    private int mPosition;
    private ArrayList<Integer> mItemsToBeDeleted;

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
        mItemsToBeDeleted = new ArrayList<>();
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        getListView().setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {
                View view = getListView().getChildAt(i);
                if (mItemsToBeDeleted.contains(i)) {
                    mItemsToBeDeleted.remove(i);
                    view.setBackgroundColor(Color.WHITE);
                } else {
                    mItemsToBeDeleted.add(i);
                    view.setBackgroundColor(Color.LTGRAY);

                }
                if (mItemsToBeDeleted.size() == 0) {
                    mItemsToBeDeleted.clear();
                    actionMode.finish();
                }

            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                MenuInflater inflater = actionMode.getMenuInflater();
                inflater.inflate(R.menu.delete, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                System.out.println("onPrepareActionMode");
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.delete:
                        for (int i =0; i>= mItemsToBeDeleted.size(); i++) {
                            if (mHelpers.deleteFile(mHelpers.getPathForFile(mFilesNames.get(i)))) {
                                mListAdapter.remove(mListAdapter.getItem(i));
                                mListAdapter.notifyDataSetChanged();
                                Toast.makeText(mContext, "file deleted", Toast.LENGTH_SHORT).show();
                            }
                            Log.i("debug", "delete stuff");
                        }
                        actionMode.finish();
                        return true;
                    default:
                        return false;

                    case R.id.hide:
                        if (mHelpers.hideFile(mFilesNames.get(mPosition))) {
                            mFilesNames.set(mPosition, "." + mFilesNames.get(mPosition));
                            mListAdapter.notifyDataSetChanged();
                            actionMode.finish();
                        }
                        break;
                    case R.id.show:
                        if (mHelpers.unHideFile(mFilesNames.get(mPosition))) {
                            mFilesNames.set(mPosition, mFilesNames.get(mPosition).substring(1));
                            mListAdapter.notifyDataSetChanged();
                            actionMode.finish();
                        }
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {

            }
        });
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String path = mHelpers.getPathForFile(mFilesNames.get(i));
                mHelpers.openContent(getActivity(), path);
            }
        });
        getListView().setDivider(null);
//        registerForContextMenu(getListView());
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
                    mFilesNames.set(info.position, "." + mFilesNames.get(info.position));
                    mListAdapter.notifyDataSetChanged();
                }
                break;
            case AppConstants.TEXT_FILE_SHOW:
                if (mHelpers.unHideFile(mFilesNames.get(info.position))) {
                    mFilesNames.set(info.position, mFilesNames.get(info.position).substring(1));
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
    @Override
    public void onPause() {
        super.onPause();
        AppGlobals.setIsUnlocked(true);
    }
}
