package com.plivo.plivoaddressbook.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import static androidx.core.content.PermissionChecker.checkSelfPermission;

public class AlertUtils {
    private Context context;

    public AlertUtils(Context viewContext) {
        context = viewContext;
    }

    public AlertDialog showAlertTwoButton(String title,
                                          String message,
                                          String positiveButtontext,
                                          DialogInterface.OnClickListener positiveButtonClickListener,
                                          String negativeButtonText,
                                          DialogInterface.OnClickListener negativeButtonClickListener) {
        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButtontext, positiveButtonClickListener)
                .setNegativeButton(negativeButtonText, negativeButtonClickListener)
                .show();
    }

    public void showToast(String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public void requestDialPermissionsRequired(int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ((Activity) context).requestPermissions(new String[]{
                            Manifest.permission.READ_CONTACTS,
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.READ_PHONE_STATE
                    }, requestCode);
        }
    }

    public boolean checkAllPermissionsGranted() {
        return checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
    }
}
