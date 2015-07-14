package com.byteshaft.silentrecord.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import com.byteshaft.silentrecord.AppGlobals;

import org.apache.commons.io.FilenameUtils;

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
        boolean success = false;
        File directory = getDirectoryByContentType(mContentType);
        String extension = getExtensionByContentType(mContentType);
        File file1 = new File(directory, fileName);
        if (fileName.endsWith(extension)) {
            File hiddenFile = new File(FilenameUtils.removeExtension(file1.getAbsolutePath()));
            success = file1.renameTo(hiddenFile);
            if (success) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    Helpers.updateFile(file1);
                } else {
                    Helpers.refreshMediaScan(directory);
                }
            }
        }
        return success;
    }

    public boolean unHideFile(String fileName) {
        boolean success = false;
        File directory = getDirectoryByContentType(mContentType);
        String extension = getExtensionByContentType(mContentType);

        File file1 = new File(directory, fileName);
        if (!fileName.endsWith(extension)) {
            File file2 = new File(directory, fileName + extension);
            success = file1.renameTo(file2);
            if (success) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    Helpers.updateFile(file1);
                    Helpers.updateFile(file2);
                } else {
                    Helpers.refreshMediaScan(directory);
                }
            }
        }
        return success;
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
        if (!fileName.endsWith(getExtensionByContentType(mContentType))) {
            return AppConstants.TEXT_FILE_SHOW;
        } else {
            return AppConstants.TEXT_FILE_HIDE;
        }
    }

    public static String getExtensionByContentType(String contentType) {
        switch (contentType) {
            case AppGlobals.DIRECTORY.VIDEOS:
                return ".mp4";
            case AppGlobals.DIRECTORY.PICTURES:
                return ".jpg";
            default:
                return null;
        }
    }

    public static File getDirectoryByContentType(String contentType) {
        switch (contentType) {
            case AppGlobals.DIRECTORY.VIDEOS:
                return AppGlobals.getVideosDirectory();
            case AppGlobals.DIRECTORY.PICTURES:
                return AppGlobals.getPicturesDirectory();
            default:
                return null;
        }
    }
}
