package com.byteshaft.silentrecord.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import com.byteshaft.silentrecord.AppGlobals;

import java.io.File;

public class VideoFragmentHelpers {

    private String mContentType;

    public VideoFragmentHelpers(String contentType) {
        mContentType = contentType;
    }

    public String getPathForFile(String fileName) {
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

    public void openContent(Context context, String filePath) {
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
        context.startActivity(intent);
    }

    public boolean deleteFile(String filePath) {
        File file = new File(filePath);
        return file.delete();
    }

    public boolean hideFile(String fileName) {
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

    public boolean unHideFile(String fileName) {
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

    public String getExecuteText() {
        switch (mContentType) {
            case AppGlobals.DIRECTORY.VIDEOS:
                return "Play";
            case AppGlobals.DIRECTORY.PICTURES:
                return "View";
            default:
                return null;
        }
    }

    public String getVisibilityText(String fileName) {
        if (fileName.startsWith(".")) {
            return AppConstants.TEXT_FILE_SHOW;
        } else {
            return AppConstants.TEXT_FILE_HIDE;
        }
    }
}
