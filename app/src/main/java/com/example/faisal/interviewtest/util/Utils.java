/*
 * Copyright (c) 2017. Faisal Jamil
 */

package com.example.faisal.interviewtest.util;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.View;


/* Used as a mix of several smaller utilities to avoid large bunch of codes */
public class Utils {
    public static AlertDialog.Builder showDialog (Context context, String title, String message, View rootView){
        AlertDialog.Builder alert = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message);
        if(rootView != null)
            alert.setView(rootView);

        return alert;
    }
}
