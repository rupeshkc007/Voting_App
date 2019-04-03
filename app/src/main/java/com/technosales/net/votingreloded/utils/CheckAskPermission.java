package com.technosales.net.votingreloded.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class CheckAskPermission {

    public static boolean isReadStorageAllowed(Context context) {
        int readStorage = ContextCompat.checkSelfPermission(context, READ_EXTERNAL_STORAGE);
        return readStorage == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isWriteStorageAllowed(Context context) {
        int writeStorage = ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE);
        return writeStorage == PackageManager.PERMISSION_GRANTED;
    }


    public static void askReadStorage(Activity context) {
        ActivityCompat.requestPermissions(context, new String[]{READ_EXTERNAL_STORAGE}, UtilStrings.READ_STORAGE_CODE);

    }

    public static void askWriteStorage(Activity context) {
        ActivityCompat.requestPermissions(context, new String[]{WRITE_EXTERNAL_STORAGE}, UtilStrings.WRITE_STORAGE_CODE);

    }

}
