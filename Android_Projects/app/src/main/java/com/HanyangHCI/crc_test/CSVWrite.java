package com.HanyangHCI.crc_test;

import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import au.com.bytecode.opencsv.CSVWriter;

public class CSVWrite {

    public CSVWrite() {}

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void writeCsv(List<String[]> data, String filename) {
        try {
            CSVWriter cw = new CSVWriter(new FileWriter(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + filename), ',',  '"');
            Iterator<String[]> it = data.iterator();
            try {
                while (it.hasNext()) {
                    String[] s = (String[]) it.next();
                    cw.writeNext(s);
                }
            } finally {
                cw.close();
            }
        } catch (IOException e) {
            Log.e("LOG", "Can't Save");
            e.printStackTrace();
        }
    }
}
