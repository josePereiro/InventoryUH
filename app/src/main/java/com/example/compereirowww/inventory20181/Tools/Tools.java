package com.example.compereirowww.inventory20181.Tools;


import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Tools {

    private static AlertDialog heavyTaskDialog;

    public static String formatDate(long date) {
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyHHmm");
        Date date1 = new Date();
        date1.setTime(date);
        String currentDateAndTime = sdf.format(date1);
        currentDateAndTime = currentDateAndTime.substring(0, 2) + "/" +
                currentDateAndTime.substring(2, 4) + "/" +
                currentDateAndTime.substring(4, 8) + " (" +
                currentDateAndTime.substring(8, 10) + ":" +
                currentDateAndTime.substring(10, 12) + ")";
        return currentDateAndTime;
    }

    public static String formatDate(String dateLong) {
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyHHmm");
        Date date1 = new Date();
        date1.setTime(Long.parseLong(dateLong));
        String currentDateAndTime = sdf.format(date1);
        currentDateAndTime = currentDateAndTime.substring(0, 2) + "/" +
                currentDateAndTime.substring(2, 4) + "/" +
                currentDateAndTime.substring(4, 8) + " (" +
                currentDateAndTime.substring(8, 10) + ":" +
                currentDateAndTime.substring(10, 12) + ")";
        return currentDateAndTime;
    }

    public static Cursor get0CountCursor() {
        return new Cursor() {
            @Override
            public int getCount() {
                return 0;
            }

            @Override
            public int getPosition() {
                return 0;
            }

            @Override
            public boolean move(int i) {
                return false;
            }

            @Override
            public boolean moveToPosition(int i) {
                return false;
            }

            @Override
            public boolean moveToFirst() {
                return false;
            }

            @Override
            public boolean moveToLast() {
                return false;
            }

            @Override
            public boolean moveToNext() {
                return false;
            }

            @Override
            public boolean moveToPrevious() {
                return false;
            }

            @Override
            public boolean isFirst() {
                return false;
            }

            @Override
            public boolean isLast() {
                return false;
            }

            @Override
            public boolean isBeforeFirst() {
                return false;
            }

            @Override
            public boolean isAfterLast() {
                return false;
            }

            @Override
            public int getColumnIndex(String s) {
                return 0;
            }

            @Override
            public int getColumnIndexOrThrow(String s) throws IllegalArgumentException {
                return 0;
            }

            @Override
            public String getColumnName(int i) {
                return "";
            }

            @Override
            public String[] getColumnNames() {
                return null;
            }

            @Override
            public int getColumnCount() {
                return 0;
            }

            @Override
            public byte[] getBlob(int i) {
                return null;
            }

            @Override
            public String getString(int i) {
                return "";
            }

            @Override
            public void copyStringToBuffer(int i, CharArrayBuffer charArrayBuffer) {

            }

            @Override
            public short getShort(int i) {
                return 0;
            }

            @Override
            public int getInt(int i) {
                return 0;
            }

            @Override
            public long getLong(int i) {
                return 0;
            }

            @Override
            public float getFloat(int i) {
                return 0;
            }

            @Override
            public double getDouble(int i) {
                return 0;
            }

            @Override
            public int getType(int i) {
                return 0;
            }

            @Override
            public boolean isNull(int i) {
                return false;
            }

            @Override
            public void deactivate() {

            }

            @Override
            public boolean requery() {
                return false;
            }

            @Override
            public void close() {

            }

            @Override
            public boolean isClosed() {
                return false;
            }

            @Override
            public void registerContentObserver(ContentObserver contentObserver) {

            }

            @Override
            public void unregisterContentObserver(ContentObserver contentObserver) {

            }

            @Override
            public void registerDataSetObserver(DataSetObserver dataSetObserver) {

            }

            @Override
            public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

            }

            @Override
            public void setNotificationUri(ContentResolver contentResolver, Uri uri) {

            }

            @Override
            public Uri getNotificationUri() {
                return null;
            }

            @Override
            public boolean getWantsAllOnMoveCalls() {
                return false;
            }

            @Override
            public void setExtras(Bundle bundle) {

            }

            @Override
            public Bundle getExtras() {
                return null;
            }

            @Override
            public Bundle respond(Bundle bundle) {
                return null;
            }
        };
    }

    public static String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyHHmm");
        String currentDateAndTime = sdf.format(new Date());
        currentDateAndTime = currentDateAndTime.substring(0, 2) + "/" +
                currentDateAndTime.substring(2, 4) + "/" +
                currentDateAndTime.substring(4, 8) + " (" +
                currentDateAndTime.substring(8, 10) + ":" +
                currentDateAndTime.substring(10, 12) + ")";
        return currentDateAndTime;
    }

    public static String getFormattedDateForFileNaming() {
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyHHmm");
        String currentDateAndTime = sdf.format(new Date());
        currentDateAndTime = currentDateAndTime.substring(0, 2) + "-" +
                currentDateAndTime.substring(2, 4) + "-" +
                currentDateAndTime.substring(4, 8) + "  " +
                currentDateAndTime.substring(8, 10) + "." +
                currentDateAndTime.substring(10, 12) + "";
        return currentDateAndTime;
    }

    public static long getDate() {
        return new Date().getTime();
    }


    public static void showToast(Context context, String message, boolean LENGTH_LONG) {
        if (LENGTH_LONG) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    public static void showDialogMessage(Context context, String message, String button1Text,
                                         DialogInterface.OnClickListener button1Listener,
                                         String button2Text, DialogInterface.OnClickListener button2Listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setPositiveButton(button1Text, button1Listener).
                setNegativeButton(button2Text, button2Listener).
                setCancelable(false);
        builder.create().show();
    }

    public static void showInfoDialog(Context context, String message, String buttonText,
                                         DialogInterface.OnClickListener buttonListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setPositiveButton(buttonText, buttonListener).
                setCancelable(false);
        builder.create().show();
    }

    public static int getIndexOf(ArrayList<String> arrayList, String element) {
        for (int i = 0; i < arrayList.size(); i++) {
            if (arrayList.get(i).equals(element)) {
                return i;
            }
        }
        return -1;
    }

    public static int getIndexOf(String[] arrayList, String element) {
        for (int i = 0; i < arrayList.length; i++) {
            if (arrayList[i].equals(element)) {
                return i;
            }
        }
        return -1;
    }

    public static AlertDialog getHeavyTaskDialog(Context context, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setIcon(android.R.drawable.progress_horizontal);
        heavyTaskDialog = builder.create();
        return heavyTaskDialog;

    }

    public static ArrayList<String> readFile(String filePath) throws IOException {

        File fileToLoad = new File(filePath);
        ArrayList<String> toReturn = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(fileToLoad));
        String temp;
        while ((temp = br.readLine()) != null) {
            toReturn.add(temp);
        }

        return toReturn;
    }

    public static ArrayList<String> readFile(File fileToLoad) throws IOException {

        ArrayList<String> toReturn = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(fileToLoad));
        String temp;
        while ((temp = br.readLine()) != null) {
            toReturn.add(temp);
        }

        return toReturn;
    }

    public static String readFileLine(String filePath, int line) throws IOException, IndexOutOfBoundsException {

        File fileToLoad = new File(filePath);
        ArrayList<String> toReturn = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(fileToLoad));
        String temp;
        while ((temp = br.readLine()) != null) {
            toReturn.add(temp);
        }
        return toReturn.get(line);
    }


    public static void writeFile(File file, String data) throws IOException {
        FileWriter fw = new FileWriter(file);
        fw.write(data);
        fw.close();
    }


}
