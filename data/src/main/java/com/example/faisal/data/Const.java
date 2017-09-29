/*
 * Copyright (c) 2017. Faisal Jamil
 */

package com.example.faisal.data;


import android.os.Environment;

import java.io.File;

public class Const {

    public static final String filename  = "mylivn.json";

    public static final File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

    public static final int PERM_REQUEST_EXTERNAL_STORAGE = 10;

}
