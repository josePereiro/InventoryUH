package com.example.compereirowww.inventory20181.Tools;


import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.example.compereirowww.inventory20181.DataBase.DB;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

public class Tools {

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

    public static String[] addElement(String[] array, String element) {
        String[] newArray = new String[array.length + 1];
        for (int i = 0; i < array.length; i++) {
            newArray[i] = array[i];
        }
        newArray[newArray.length - 1] = element;
        return newArray;
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

    public static int getIndexOf(String[] array, String element) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(element)) {
                return i;
            }
        }
        return -1;
    }

    public static ArrayList<String> readFile(String filePath) throws IOException {

        File fileToLoad = new File(filePath);
        ArrayList<String> toReturn = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(fileToLoad));
        String temp;
        while ((temp = br.readLine()) != null) {
            toReturn.add(temp);
        }
        br.close();
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
        br.close();
        return toReturn.get(line);
    }

    public static String readFileFirstLine(File file) throws IOException, IndexOutOfBoundsException {

        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = br.readLine();
        br.close();
        if (line == null) return "";
        else return line;

    }

    public static void writeFile(File file, String data) throws IOException {
        FileWriter fw = new FileWriter(file);
        fw.write(data);
        fw.close();
    }

    public static int myStringHashCode(String s) {

        int hash = 0;
        for (int i = 0; i < s.length(); i++) {
            hash += s.charAt(i) * 31 ^ (s.length() - i + 1);
        }
        return hash;

    }

    public static boolean contain(String[] array, String element) {
        for (String s : array) {
            if (element.equals(s)) return true;
        }
        return false;
    }

    public static void saveImage(Bitmap image, String directory, String imageNameWithExtension) throws IOException {

        File dir = new File(directory);
        File imageFile = new File(dir, imageNameWithExtension);

        if (!imageFile.exists()) {
            FileOutputStream out = new FileOutputStream(imageFile);
            image.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        }

    }

    /**
     * @param qrCodeText the text that you want to encode
     * @param size       the size of the image that you want to produce
     * @return a Bitmap with the QRCode produced
     * @throws WriterException an error in the writing occurred
     */
    public static Bitmap codeTextToQRAndGetBitmap(String qrCodeText, int size) throws WriterException {
        // Create the ByteMatrix for the QR-Code that encodes the given String
        Hashtable hintMap = new Hashtable();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix byteMatrix = qrCodeWriter.encode(qrCodeText,
                BarcodeFormat.QR_CODE, size, size, hintMap);
        // Make the Image that are to hold the QRCode
        int matrixWidth = byteMatrix.getWidth();
        Bitmap image = Bitmap.createBitmap(matrixWidth, matrixWidth, Bitmap.Config.RGB_565);
        //Filling the image with the byteMatrix CSVData
        for (int i = 0; i < matrixWidth; i++) {
            for (int j = 0; j < matrixWidth; j++) {
                if (byteMatrix.get(i, j)) {
                    image.setPixel(i, j, Color.BLACK);
                } else {
                    image.setPixel(i, j, Color.WHITE);
                }
            }
        }
        return image;
    }

    public static Bitmap getQRCodeLabeledBitmap(String toCode) throws WriterException {

        int size = 120;
        int toCut = 17;
        int labelH = 12;

        // Create the ByteMatrix for the QR-Code that encodes the given String
        Hashtable hintMap = new Hashtable();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix byteMatrix = qrCodeWriter.encode(toCode,
                BarcodeFormat.QR_CODE, size, size, hintMap);

        // Create the BufferImage
        int fw = size - 2 * toCut;
        int fh = size - 2 * toCut + labelH;
        Bitmap image = Bitmap.createBitmap(fw, fh, Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(image);
        canvas.drawColor(Color.WHITE);
        Paint paint = new Paint();
        for (int i = toCut; i < byteMatrix.getWidth() - toCut; i++) {
            for (int j = toCut; j < byteMatrix.getWidth() - toCut; j++) {
                if (byteMatrix.get(i, j)) {
                    canvas.drawRect(i - toCut, j - toCut, i - toCut + 1, j - toCut + 1, paint);
                }
            }
        }
        canvas.drawText(toCode, 0, fh - 2, new Paint());

        return image;
    }

    public static Bitmap getGrid(Bitmap[] images, int rawCount, int margin) {

        //region startPoints

        ArrayList<Point> startsPoints = new ArrayList<>();
        int r = 0;
        int c = 0;
        for (Bitmap image : images) {
            startsPoints.add(new Point(r * (image.getWidth() + margin) + margin,
                    c * (image.getHeight() + margin) + margin));

            if (r < rawCount - 1) {
                r++;
            } else {
                r = 0;
                c++;
            }
        }

        //endregion

        int bw = startsPoints.get(rawCount - 1).x + images[0].getWidth() + margin;
        int bh = startsPoints.get(startsPoints.size() - 1).y + images[0].getHeight() + margin;

        Bitmap gridImage = Bitmap.createBitmap(bw, bh, Bitmap.Config.RGB_565);
        new Canvas(gridImage).drawColor(Color.GRAY);

        //drawing images
        for (int i = 0; i < images.length; i++) {
            drawImage(startsPoints.get(i), gridImage, images[i]);
        }

        return gridImage;
    }

    public static void drawImage(Point p, Bitmap bigImage, Bitmap image) {
        int sx = p.x;
        int sy = p.y;

        for (int x = 0; x < image.getWidth() - 1; x++) {
            for (int y = 0; y < image.getHeight() - 1; y++) {
                bigImage.setPixel(x + sx, y + sy, image.getPixel(x, y));
            }
        }
    }


    public static void listAllFilesAndSubFiles(File rootFile, String ext,
                                               ArrayList<File> listedFilesContainer) {
        if (rootFile.isDirectory()) {
            File[] files = rootFile.listFiles();
            for (File f : files) {
                //Filter
                if (f.isDirectory()) {
                    listAllFilesAndSubFiles(f, ext, listedFilesContainer);
                } else {
                    if (ext.equals("") || f.getName().contains(ext)) {
                        listedFilesContainer.add(f);
                    }
                }
            }
        }
    }

    public abstract class DataThree {

        //Areas -> Locations -> Types -> States
        private ArrayList<ArrayList<ArrayList<ArrayList<ArrayList<String>>>>> three;

        //AREA
        private ArrayList<String> areas;

        //LOCATION
        private ArrayList<ArrayList<String>> locations;

        //TYPE
        private int EQUIPMENT_INDEX = 0;
        private int FURNISHING_INDEX = 1;
        private int UNKNOWN_INDEX = 2;
        private int TYPE_COUNT = 3;

        //STATE
        private int MISSING_INDEX = 0;
        private int PRESENT_INDEX = 1;
        private int LEFTOVER_INDEX = 2;
        private int IGNORED_MISSING_INDEX = 3;
        private int LEFTOVER_PRESENT_INDEX = 4;
        private int STATES_COUNT = 5;

        //Progress
        private long progress;

        //Constructor
        public DataThree(Cursor data) {
            progress = 0;
            initializeAreasAndLocations(data);
            initializeThree();
            fillThreeWithData(data);
        }

        //region privateMethods...

        private void fillThreeWithData(Cursor data) {

            String number, numberArea, numberLocation;
            int numberType, numberState;
            data.moveToPosition(-1);
            while (data.moveToNext()) {

                number = data.getString(DB.IT.Indexes.NUMBER_COLUMN_INDEX);
                numberArea = data.getString(DB.IT.Indexes.AREA_COLUMN_INDEX);
                numberLocation = data.getString(DB.IT.Indexes.AREA_COLUMN_INDEX);
                numberType = data.getInt(DB.IT.Indexes.TYPE_COLUMN_INDEX);
                numberState = data.getInt(DB.IT.Indexes.STATE_COLUMN_INDEX);

                //Area
                for (int a = 0; a < areas.size(); a++) {
                    if (numberArea.equals(areas.get(a))) {

                        //Location
                        for (int l = 0; l < locations.get(a).size(); l++) {
                            if (numberLocation.equals(locations.get(a).get(l))) {

                                //Types
                                for (int t = 0; t < TYPE_COUNT; t++) {
                                    if (numberType == getTypeValueByIndex(t)) {

                                        //State
                                        for (int s = 0; s < STATES_COUNT; s++) {
                                            if (numberState == getStateValueByIndex(s)) {

                                                //Adding number
                                                three.get(a).get(l).get(t).get(s).add(number);

                                                updateProgress();

                                            }
                                        }

                                    }
                                }
                            }
                        }
                    }

                }

            }

        }

        private int getTypeValueByIndex(int index) {
            if (index == EQUIPMENT_INDEX) return DB.IT.TypeValues.EQUIPMENT;
            if (index == FURNISHING_INDEX) return DB.IT.TypeValues.FURNISHING;
            else return DB.IT.TypeValues.UNKNOWN;
        }

        private int getStateValueByIndex(int index) {
            if (index == MISSING_INDEX) return DB.IT.StateValues.MISSING;
            if (index == PRESENT_INDEX) return DB.IT.StateValues.PRESENT;
            if (index == LEFTOVER_INDEX) return DB.IT.StateValues.LEFTOVER;
            if (index == IGNORED_MISSING_INDEX) return DB.IT.StateValues.IGNORED_MISSING;
            else return DB.IT.StateValues.LEFTOVER_PRESENT;
        }

        private void initializeThree() {

            three = new ArrayList<>();

            //Areas
            for (int a = 0; a < areas.size(); a++) {

                ArrayList<ArrayList<ArrayList<ArrayList<String>>>> area = new ArrayList<>();

                //Location
                for (int l = 0; l < locations.get(a).size(); l++) {

                    ArrayList<ArrayList<ArrayList<String>>> location = new ArrayList<>();

                    for (int t = 0; t < TYPE_COUNT; t++) {

                        ArrayList<ArrayList<String>> type = new ArrayList<>();

                        for (int s = 0; s < STATES_COUNT; s++) {

                            ArrayList<String> state = new ArrayList<>();
                            type.add(state);

                        }

                        location.add(type);

                    }

                    area.add(location);
                }

                three.add(area);

            }


        }

        private void initializeAreasAndLocations(Cursor data) {

            areas = new ArrayList<>();
            this.locations = new ArrayList<>();

            data.moveToPosition(-1);
            while (data.moveToNext()) {
                String area = data.getString(DB.IT.Indexes.AREA_COLUMN_INDEX);
                if (!areas.contains(area)) {
                    areas.add(area);
                    updateProgress();
                }
            }

            for (int a = 0; a < areas.size(); a++) {

                ArrayList<String> currentAreaLocations = new ArrayList<>();
                data.moveToPosition(-1);
                while (data.moveToNext()) {
                    String area = data.getString(DB.IT.Indexes.AREA_COLUMN_INDEX);
                    if (area.equals(areas.get(a))) {
                        String location = data.getString(DB.IT.Indexes.LOCATION_COLUMN_INDEX);
                        if (!currentAreaLocations.contains(location)) {
                            currentAreaLocations.add(location);
                            updateProgress();
                        }
                    }
                }

                this.locations.add(currentAreaLocations);
            }


        }

        private void updateProgress() {

            progress++;
            onThreeProgressUpdate(progress);
        }

        private ArrayList<String> getBranch(int area, int location, int type, int state) {
            return three.get(area).get(location).get(type).get(state);
        }

        private ArrayList<ArrayList<String>> getBranch(int area, int location, int type) {
            return three.get(area).get(location).get(type);
        }

        private ArrayList<ArrayList<ArrayList<String>>> getBranch(int area, int location) {
            return three.get(area).get(location);
        }

        private ArrayList<ArrayList<ArrayList<ArrayList<String>>>> getBranch(int area) {
            return three.get(area);
        }

        //endregion privateMethods...

        //region publicMethods...

        public abstract void onThreeProgressUpdate(long progress);

        public int getAreaNumbersCount(int area) {

            int numberCount = 0;
            for (int l = 0; l < locations.get(area).size(); l++) {
                numberCount += getLocationNumbersCount(area, l);
            }

            return numberCount;
        }

        public ArrayList<String> getAreaNumers(int area) {
            ArrayList<String> numbers = new ArrayList<>();
            for (int l = 0; l < locations.get(area).size(); l++) {
                for (String number : getLocationNumbers(area, l)) {
                    numbers.add(number);
                }
            }
            return numbers;
        }

        public int getLocationNumbersCount(int area, int location) {

            int numberCount = 0;
            for (int t = 0; t < TYPE_COUNT; t++) {
                numberCount += getTypeNumberCount(area, location, t);
            }

            return numberCount;
        }

        public ArrayList<String> getLocationNumbers(int area, int location) {
            ArrayList<String> numbers = new ArrayList<>();
            for (int t = 0; t < TYPE_COUNT; t++) {
                for (String number : getTypeNumbers(area, location, t)) {
                    numbers.add(number);
                }
            }
            return numbers;
        }

        public int getTypeNumberCount(int type) {

            int numberCount = 0;
            for (int a = 0; a < areas.size(); a++) {
                for (int l = 0; l < locations.get(a).size(); l++) {
                    numberCount += getTypeNumberCount(a, l, type);
                }
            }

            return numberCount;
        }

        public ArrayList<String> getTypeNumbers(int type) {
            ArrayList<String> numbers = new ArrayList<>();
            for (int a = 0; a < areas.size(); a++) {
                for (int l = 0; l < locations.get(a).size(); l++) {
                    for (String number : getTypeNumbers(a, l, type)) {
                        numbers.add(number);
                    }
                }
            }
            return numbers;
        }

        public int getTypeNumberCount(int area, int location, int type) {

            int numberCount = 0;
            for (int s = 0; s < STATES_COUNT; s++) {
                numberCount += getBranch(area, location, type, s).size();
            }

            return numberCount;

        }

        public ArrayList<String> getTypeNumbers(int area, int location, int type) {
            ArrayList<String> numbers = new ArrayList<>();
            for (int s = 0; s < STATES_COUNT; s++) {
                for (String number : getStateNumbers(area, location, type, s)) {
                    numbers.add(number);
                }
            }
            return numbers;
        }

        public int getStateNumberCount(int state) {
            int numberCount = 0;
            for (int a = 0; a < areas.size(); a++) {
                for (int l = 0; l < locations.get(a).size(); l++) {
                    for (int t = 0; t < TYPE_COUNT; t++) {
                        numberCount += getStateNumberCount(a, l, t, state);
                    }
                }
            }
            return numberCount;
        }

        public int getStateNumberCount(int area, int location, int type, int state) {
            return getBranch(area, location, type, state).size();
        }

        public ArrayList<String> getStateNumbers(int area, int location, int type, int state) {
            return getBranch(area, location, type, state);
        }

        public ArrayList<String> getStateNumbers(int state) {
            ArrayList<String> numbers = new ArrayList<>();
            for (int a = 0; a < areas.size(); a++) {
                for (int l = 0; l < locations.get(a).size(); l++) {
                    for (int t = 0; t < TYPE_COUNT; t++) {
                        for (String number : getStateNumbers(a, l, t, state)) {
                            numbers.add(number);
                        }
                    }
                }
            }
            return numbers;
        }

        public int getAreasCount() {
            return areas.size();
        }

        public int getLocationsCount(int area) {
            return locations.get(area).size();
        }

        public ArrayList<String> getAreas() {
            return areas;
        }

        public ArrayList<String> getLocations(int area) {
            return locations.get(area);
        }

        //endregion publicMethods...

    }
}
