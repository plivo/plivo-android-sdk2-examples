package com.plivo.plivoregistration.utils;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

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
}
