package com.shoppr.shoper.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class RuntimePermission {
    static String[] REQUEST_PERMISSION = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NETWORK_STATE
    };
    private static int REQUEST_PERMISION_CODE = 101;

    public static boolean checkPermission(Context context) {

        if (REQUEST_PERMISSION != null) {
            for (String permission : REQUEST_PERMISSION) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;

    }

    private static void requestPermission(Context context) {

        ActivityCompat.requestPermissions((AppCompatActivity) context, REQUEST_PERMISSION, REQUEST_PERMISION_CODE);

    }

    public static boolean checkRunTimePermission(Context context){
        if (!checkPermission(context)) {
            requestPermission(context);
            return false;
        }
        else
            return true;
    }

}
