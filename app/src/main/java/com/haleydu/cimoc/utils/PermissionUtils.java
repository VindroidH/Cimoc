package com.haleydu.cimoc.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

/**
 * Created by Hiroshi on 2016/10/20.
 */

public class PermissionUtils {

    public static boolean hasStoragePermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            int manageResult = checkPermission(activity, Manifest.permission.MANAGE_EXTERNAL_STORAGE);
            return manageResult == PackageManager.PERMISSION_GRANTED;
        } else {
            int writeResult = checkPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return writeResult == PackageManager.PERMISSION_GRANTED;
        }
    }

    public static boolean hasAllPermissions(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            int manageResult = checkPermission(activity, Manifest.permission.MANAGE_EXTERNAL_STORAGE);
            return manageResult == PackageManager.PERMISSION_GRANTED;
        } else {
            int readResult = checkPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
            int writeResult = checkPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return readResult == PackageManager.PERMISSION_GRANTED &&
                    writeResult == PackageManager.PERMISSION_GRANTED;
        }
    }

    public static int checkPermission(@NonNull Activity activity, @NonNull String permission) {
        return ContextCompat.checkSelfPermission(activity, permission);
    }
}
