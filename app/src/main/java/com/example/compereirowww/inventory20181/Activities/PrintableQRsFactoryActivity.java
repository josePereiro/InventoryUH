package com.example.compereirowww.inventory20181.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.compereirowww.inventory20181.DataBase.DB;
import com.example.compereirowww.inventory20181.R;
import com.example.compereirowww.inventory20181.Tools.Tools;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;

public class PrintableQRsFactoryActivity extends AppCompatActivity {

    //GUI
    ImageView imageView;
    TextView textView;
    Spinner formatS;
    FloatingActionButton fab;
    static String selectedFormat;

    private static Cursor data;
    private static String[] numbers;
    private static boolean importing;
    private AsyncTask<String, String, Boolean> currentAT;

    private static String F8X9 = "8 x 9";
    private static String F8X10 = "8 x 10";
    private static String F6X7 = "6 x 7";
    private static String F1X1 = "1 X 1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printable_qrs_factory);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!importing) {
                    fab.setImageResource(R.drawable.stop);
                    deleteOldFiles();
                    setStartIndex(0);
                    importing = true;
                    formatS.setEnabled(false);
                    startAsyncTask(selectedFormat);
                } else {
                    fab.setImageResource(R.drawable.next);
                    currentAT.cancel(true);
                    currentAT = null;
                    importing = false;
                    formatS.setEnabled(true);
                }
            }
        });
        imageView = (ImageView) findViewById(R.id.imageView);
        textView = (TextView) findViewById(R.id.textView);
        AppStatics.formatView(textView);
        AppStatics.formatView((TextView)findViewById(R.id.textView2));
        formatS = (Spinner) findViewById(R.id.format_s);
        AppStatics.formatView(PrintableQRsFactoryActivity.this,
                new String[]{F1X1, F6X7, F8X9, F8X10}, formatS );
        formatS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = (String) adapterView.getItemAtPosition(i);
                selectedFormat = selectedItem;
                new ShowPreviewAT().execute(selectedFormat);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (importing) {
            startAsyncTask(selectedFormat);
            formatS.setEnabled(false);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (currentAT != null) {
            currentAT.cancel(true);
            currentAT = null;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_help, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.help) {
            Tools.showToast(PrintableQRsFactoryActivity.this, "No hay ayuda!!! Por ahora...", false);
            return super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }

    private static String[] getNumbersFromData() {

        ArrayList<String> numbers = new ArrayList<>();
        data.moveToPosition(-1);
        while (data.moveToNext()) {
            numbers.add(data.getString(0));
        }

        return numbers.toArray(new String[]{""});

    }

    public static void setImporting(boolean importing) {
        PrintableQRsFactoryActivity.importing = importing;
    }

    private void startAsyncTask(String format) {

        fab.setImageResource(R.drawable.stop);
        if (format.equals(F1X1)) {
            currentAT = new CreateSingleCodesAT(getStartIndex());
            currentAT.execute(numbers);
            return;
        }
        if (format.equals(F8X9)) {
            currentAT = new CreateQR8x9PagesAT(getStartIndex());
            currentAT.execute(numbers);
            return;
        }
        if (format.equals(F6X7)) {
            currentAT = new CreateQR6x7PagesAT(getStartIndex());
            currentAT.execute(numbers);
            return;
        }
        if (format.equals(F8X10)) {
            currentAT = new CreateQR8x10PagesAT(getStartIndex());
            currentAT.execute(numbers);
        }
    }

    private int getStartIndex() {
        return Integer.parseInt(AppStatics.db.getPreference(DB.PT.PNames.P_QR_CURRENT_INDEX));
    }

    private void setStartIndex(int startIndex) {
        AppStatics.db.setPreference(DB.PT.PNames.P_QR_CURRENT_INDEX, startIndex);
    }

    public static void setData(Cursor data) {
        PrintableQRsFactoryActivity.data = data;
        numbers = getNumbersFromData();
        data.close();
    }

    private class CreateQR8x10PagesAT extends AsyncTask<String, String, Boolean> {

        Bitmap pageImage;
        int page = 0;
        private int startIndex;

        public CreateQR8x10PagesAT(int startIndex) {
            this.startIndex = startIndex;
        }

        @Override
        protected Boolean doInBackground(String... toCode) {

            if (toCode == null) return false;

            //region qrPositions

            ArrayList<Point> qrPositions = new ArrayList<>();
            int r = 0;
            int c = 0;
            for (int i = 0; i < 80; i++) {
                qrPositions.add(new Point(r * 88 + 2,
                        c * 100 + 2));
                if (r < 7) {
                    r++;
                } else {
                    r = 0;
                    c++;
                }
            }

            //endregion

            int bw = 706;
            int bh = 1002;

            if (isCancelled()) return false;

            for (int totalIndex = 0; totalIndex < toCode.length; page++) {

                if (totalIndex < startIndex) {
                    totalIndex += 80;
                    continue;
                }

                if (isCancelled()) return false;

                updateStartIndex(totalIndex);

                pageImage = Bitmap.createBitmap(bw, bh, Bitmap.Config.RGB_565);
                new Canvas(pageImage).drawColor(Color.WHITE);

                //qrs
                try {
                    for (int qrCounter = 0; totalIndex < toCode.length && qrCounter < 80; totalIndex++, qrCounter++) {
                        if (isCancelled()) return false;

                        drawQRInPosition(pageImage, toCode[totalIndex], qrPositions.get(qrCounter));

                    }

                    publishProgress("Creando página: " + (page + 1));
                    savePage(pageImage, page);

                } catch (WriterException e) {
                    return false;
                } catch (IOException e) {
                    return false;
                }
            }

            return true;
        }

        @Override
        protected void onProgressUpdate(String... strings) {

            textView.setText(strings[0]);
            if (pageImage != null) {
                imageView.setImageBitmap(pageImage);
            }

        }

        @Override
        protected void onPostExecute(Boolean r) {

            if (r) {
                textView.setText("Se produgeron " + page + " páginas, disfrutelas!!!");
                Tools.showToast(PrintableQRsFactoryActivity.this, "Proceso terminado!!!", false);
            } else {
                textView.setText("Se produgeron solo " + page + " páginas, pero el proceso no finalizó!!!");
                Tools.showToast(PrintableQRsFactoryActivity.this, "Proceso cancelado!!!", false);
            }
            importing = false;
            formatS.setEnabled(true);
            fab.setImageResource(R.drawable.next);

        }

        private void savePage(Bitmap pageBitmap, int page) throws IOException {
            Tools.saveImage(pageBitmap,
                    AppStatics.db.getPreference(DB.PT.PNames.QRS_DIRECTORY_PATH),
                    "Page " + (page + 1) + ".jpg");
        }

        private void drawQRInPosition(Bitmap bigImage, String toCode, Point position) throws WriterException {

            int size = 120;
            int toCut = 17;
            int labelH = 12;

            //Code Qr
            Hashtable hintMap = new Hashtable();
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix byteMatrix = qrCodeWriter.encode(toCode,
                    BarcodeFormat.QR_CODE, size, size, hintMap);

            for (int mX = toCut; mX < byteMatrix.getWidth() - toCut; mX++) {
                for (int mY = toCut; mY < byteMatrix.getWidth() - toCut; mY++) {
                    if (byteMatrix.get(mX, mY)) {
                        bigImage.setPixel(position.x + mX - toCut, position.y + mY - toCut, Color.BLACK);
                    }
                    //else {
                    //  bigImage.setPixel(position.x + mX - toCut, position.y + mY - toCut, Color.WHITE);
                    //}
                }
            }
            new Canvas(bigImage).drawText(toCode, position.x,
                    position.y + size - 2 * toCut + labelH - 2,
                    new Paint());

        }

        private void updateStartIndex(int newIndex) {
            AppStatics.db.setPreference(DB.PT.PNames.P_QR_CURRENT_INDEX,
                    String.valueOf(newIndex));
        }

    }

    private class CreateQR8x9PagesAT extends AsyncTask<String, String, Boolean> {

        Bitmap pageImage;
        int page = 0;
        private int startIndex;

        public CreateQR8x9PagesAT(int startIndex) {
            this.startIndex = startIndex;
        }

        @Override
        protected Boolean doInBackground(String... toCode) {

            if (toCode == null) return false;

            //region qrPositions

            ArrayList<Point> qrPositions = new ArrayList<>();
            int r = 0;
            int c = 0;
            for (int i = 0; i < 72; i++) {
                qrPositions.add(new Point(r * 88 + 2,
                        c * 100 + 2));
                if (r < 7) {
                    r++;
                } else {
                    r = 0;
                    c++;
                }
            }

            //endregion

            int bw = 706;
            int bh = 902;

            if (isCancelled()) return false;

            for (int totalIndex = 0; totalIndex < toCode.length; page++) {

                if (totalIndex < startIndex) {
                    totalIndex += 72;
                    continue;
                }

                if (isCancelled()) return false;

                updateStartIndex(totalIndex);

                pageImage = Bitmap.createBitmap(bw, bh, Bitmap.Config.RGB_565);
                new Canvas(pageImage).drawColor(Color.WHITE);

                //qrs
                try {
                    for (int qrCounter = 0; totalIndex < toCode.length && qrCounter < 72; totalIndex++, qrCounter++) {
                        if (isCancelled()) return false;

                        drawQRInPosition(pageImage, toCode[totalIndex], qrPositions.get(qrCounter));

                    }

                    publishProgress("Creando página: " + (page + 1));
                    savePage(pageImage, page);

                } catch (WriterException e) {
                    return false;
                } catch (IOException e) {
                    return false;
                }
            }

            return true;
        }

        @Override
        protected void onProgressUpdate(String... strings) {

            textView.setText(strings[0]);
            if (pageImage != null) {
                imageView.setImageBitmap(pageImage);
            }

        }

        @Override
        protected void onPostExecute(Boolean r) {

            if (r) {
                textView.setText("Se produgeron " + page + " páginas, disfrutelas!!!");
                Tools.showToast(PrintableQRsFactoryActivity.this, "Proceso terminado!!!", false);
            } else {
                textView.setText("Se produgeron solo " + page + " páginas, pero el proceso no finalizó!!!");
                Tools.showToast(PrintableQRsFactoryActivity.this, "Proceso cancelado!!!", false);
            }
            importing = false;
            formatS.setEnabled(true);
            fab.setImageResource(R.drawable.next);


        }

        private void savePage(Bitmap pageBitmap, int page) throws IOException {
            Tools.saveImage(pageBitmap,
                    AppStatics.db.getPreference(DB.PT.PNames.QRS_DIRECTORY_PATH),
                    "Page " + (page + 1) + ".jpg");
        }

        private void drawQRInPosition(Bitmap bigImage, String toCode, Point position) throws WriterException {

            int size = 120;
            int toCut = 17;
            int labelH = 12;

            //Code Qr
            Hashtable hintMap = new Hashtable();
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix byteMatrix = qrCodeWriter.encode(toCode,
                    BarcodeFormat.QR_CODE, size, size, hintMap);

            for (int mX = toCut; mX < byteMatrix.getWidth() - toCut; mX++) {
                for (int mY = toCut; mY < byteMatrix.getWidth() - toCut; mY++) {
                    if (byteMatrix.get(mX, mY)) {
                        bigImage.setPixel(position.x + mX - toCut, position.y + mY - toCut, Color.BLACK);
                    }
                    //else {
                    //  bigImage.setPixel(position.x + mX - toCut, position.y + mY - toCut, Color.WHITE);
                    //}
                }
            }
            new Canvas(bigImage).drawText(toCode, position.x,
                    position.y + size - 2 * toCut + labelH - 2,
                    new Paint());

        }

        private void updateStartIndex(int newIndex) {
            AppStatics.db.setPreference(DB.PT.PNames.P_QR_CURRENT_INDEX,
                    String.valueOf(newIndex));
        }

    }

    private class CreateQR6x7PagesAT extends AsyncTask<String, String, Boolean> {

        Bitmap pageImage;
        int page = 0;
        private int startIndex;

        public CreateQR6x7PagesAT(int startIndex) {
            this.startIndex = startIndex;
        }

        @Override
        protected Boolean doInBackground(String... toCode) {

            if (toCode == null) return false;

            //region qrPositions

            ArrayList<Point> qrPositions = new ArrayList<>();
            int r = 0;
            int c = 0;
            for (int i = 0; i < 42; i++) {
                qrPositions.add(new Point(r * 88 + 2,
                        c * 100 + 2));
                if (r < 5) {
                    r++;
                } else {
                    r = 0;
                    c++;
                }
            }

            //endregion

            int bw = 442 + 88;//86
            int bh = 702;//98

            if (isCancelled()) return false;

            for (int totalIndex = 0; totalIndex < toCode.length; page++) {

                if (totalIndex < startIndex) {
                    totalIndex += 42;
                    continue;
                }

                if (isCancelled()) return false;

                updateStartIndex(totalIndex);

                pageImage = Bitmap.createBitmap(bw, bh, Bitmap.Config.RGB_565);
                new Canvas(pageImage).drawColor(Color.WHITE);

                //qrs
                try {
                    for (int qrCounter = 0; totalIndex < toCode.length && qrCounter < 42; totalIndex++, qrCounter++) {
                        if (isCancelled()) return false;

                        drawQRInPosition(pageImage, toCode[totalIndex], qrPositions.get(qrCounter));

                    }

                    publishProgress("Creando página: " + (page + 1));
                    savePage(pageImage, page);

                } catch (WriterException e) {
                    return false;
                } catch (IOException e) {
                    return false;
                }
            }

            scanFiles();

            return true;
        }

        @Override
        protected void onProgressUpdate(String... strings) {

            textView.setText(strings[0]);
            if (pageImage != null) {
                imageView.setImageBitmap(pageImage);
            }

        }

        @Override
        protected void onPostExecute(Boolean r) {

            if (r) {
                textView.setText("Se produgeron " + page + " páginas, disfrutelas!!!");
                Tools.showToast(PrintableQRsFactoryActivity.this, "Proceso terminado!!!", false);
            } else {
                textView.setText("Se produgeron solo " + page + " páginas, pero el proceso no finalizó!!!");
                Tools.showToast(PrintableQRsFactoryActivity.this, "Proceso cancelado!!!", false);
            }
            importing = false;
            formatS.setEnabled(true);
            fab.setImageResource(R.drawable.next);
        }

        private void savePage(Bitmap pageBitmap, int page) throws IOException {
            Tools.saveImage(pageBitmap,
                    AppStatics.db.getPreference(DB.PT.PNames.QRS_DIRECTORY_PATH),
                    "Page " + (page + 1) + ".jpg");
        }

        private void drawQRInPosition(Bitmap bigImage, String toCode, Point position) throws WriterException {

            int size = 120;
            int toCut = 17;
            int labelH = 12;

            //Code Qr
            Hashtable hintMap = new Hashtable();
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix byteMatrix = qrCodeWriter.encode(toCode,
                    BarcodeFormat.QR_CODE, size, size, hintMap);

            for (int mX = toCut; mX < byteMatrix.getWidth() - toCut; mX++) {
                for (int mY = toCut; mY < byteMatrix.getWidth() - toCut; mY++) {
                    if (byteMatrix.get(mX, mY)) {
                        bigImage.setPixel(position.x + mX - toCut, position.y + mY - toCut, Color.BLACK);
                    }
                    //else {
                    //  bigImage.setPixel(position.x + mX - toCut, position.y + mY - toCut, Color.WHITE);
                    //}
                }
            }
            new Canvas(bigImage).drawText(toCode, position.x,
                    position.y + size - 2 * toCut + labelH - 2,
                    new Paint());

        }

        private void scanFiles() {

            Log.d(AppStatics.APP_TAG, "scanning bla bla");

            File QrDir = new File(AppStatics.db.getPreference(DB.PT.PNames.QRS_DIRECTORY_PATH));
            File[] qRsFiles = QrDir.listFiles();

            for (int i = 0; i < qRsFiles.length; i++) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    final Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    final Uri contentUri = Uri.fromFile(qRsFiles[i]);
                    scanIntent.setData(contentUri);
                    sendBroadcast(scanIntent);
                } else {
                    final Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory()));
                    sendBroadcast(intent);
                }
            }
        }

        private void updateStartIndex(int newIndex) {
            AppStatics.db.setPreference(DB.PT.PNames.P_QR_CURRENT_INDEX,
                    String.valueOf(newIndex));
        }

    }

    private class CreateSingleCodesAT extends AsyncTask<String, String, Boolean> {

        Bitmap qrImage;
        private int startIndex;
        private int index;

        public CreateSingleCodesAT(int startIndex) {
            this.startIndex = startIndex;
        }

        @Override
        protected Boolean doInBackground(String... toCode) {

            if (toCode == null) return false;

            if (isCancelled()) return false;

            for (index = startIndex; index < toCode.length; index++) {

                if (isCancelled()) return false;

                updateStartIndex(index);

                try {

                    publishProgress("Creando imágen: " + (index + 1));
                    qrImage = Tools.getQRCodeLabeledBitmap(toCode[index]);
                    savePage(qrImage, index, toCode[index]);

                } catch (WriterException e) {
                    return false;
                } catch (IOException e) {
                    return false;
                }
            }

            return true;
        }

        @Override
        protected void onProgressUpdate(String... strings) {

            textView.setText(strings[0]);
            if (qrImage != null) {
                imageView.setImageBitmap(qrImage);
            }
        }

        @Override
        protected void onPostExecute(Boolean r) {

            if (r) {
                textView.setText("Se produgeron " + index + " imágenes, disfrutelas!!!");
                Tools.showToast(PrintableQRsFactoryActivity.this, "Proceso terminado!!!", false);
            } else {
                textView.setText("Se produgeron solo " + index + " imágenes, pero el proceso no finalizó!!!");
                Tools.showToast(PrintableQRsFactoryActivity.this, "Proceso cancelado!!!", false);
            }
            importing = false;
            formatS.setEnabled(true);
            fab.setImageResource(R.drawable.next);
        }

        private void savePage(Bitmap pageBitmap, int index, String toCode) throws IOException {
            Tools.saveImage(pageBitmap,
                    AppStatics.db.getPreference(DB.PT.PNames.QRS_DIRECTORY_PATH),
                    index + ".) " + toCode + ".jpg");
        }

        private void updateStartIndex(int newIndex) {
            AppStatics.db.setPreference(DB.PT.PNames.P_QR_CURRENT_INDEX,
                    String.valueOf(newIndex));
        }

    }

    private class ShowPreviewAT extends AsyncTask<String, String, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... format) {

            if (format[0].equals(F1X1)) {
                try {
                    return Tools.getQRCodeLabeledBitmap("Ej 123");
                } catch (WriterException e) {
                    return null;
                }
            }
            if (format[0].equals(F6X7)) {
                return get6x7Example();
            }
            if (format[0].equals(F8X9)) {
                return get8x9Example();
            }
            if (format[0].equals(F8X10)) {
                return get8x10Example();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null)
                imageView.setImageBitmap(bitmap);
            textView.setText(getMessage());
        }

        private String getMessage() {

            int numberCount = numbers.length;
            String m = "Se codificarán " + numberCount + " números" +
                    " en ";

            if (selectedFormat.equals(F1X1)) {
                return m + numberCount + " imágenes independientes.";
            }
            if (selectedFormat.equals(F6X7)) {
                int ps;
                if (numberCount % (6 * 7) == 0) {
                    ps = numberCount / (6 * 7);
                } else {
                    ps = numberCount / (6 * 7) + 1;
                }

                return m + ps + " páginas";
            }
            if (selectedFormat.equals(F8X9)) {
                int ps;
                if (numberCount % (8 * 9) == 0) {
                    ps = numberCount / (8 * 9);
                } else {
                    ps = numberCount / (8 * 9) + 1;
                }

                return m + ps + " páginas";
            }
            if (selectedFormat.equals(F8X10)) {

                int ps;
                if (numberCount % (8 * 10) == 0) {
                    ps = numberCount / (8 * 10);
                } else {
                    ps = numberCount / (8 * 10) + 1;
                }

                return m + ps + " páginas";
            }
            return m;
        }

        private Bitmap get6x7Example() {

            int w = 6;
            int h = 7;

            String[] toCode = new String[w * h];
            Random ran = new Random();
            for (int i = 0; i < toCode.length; i++) {
                toCode[i] = "Ej " + ran.nextInt(9999);
            }

            //region qrPositions

            ArrayList<Point> qrPositions = new ArrayList<>();
            int r = 0;
            int c = 0;
            for (int i = 0; i < w * h; i++) {
                qrPositions.add(new Point(r * 88 + 2,
                        c * 100 + 2));
                if (r < w - 1) {
                    r++;
                } else {
                    r = 0;
                    c++;
                }
            }

            //endregion

            int bw = 442 + 88;//86
            int bh = 702;//98

            Bitmap image = Bitmap.createBitmap(bw, bh, Bitmap.Config.RGB_565);
            new Canvas(image).drawColor(Color.WHITE);

            //qrs
            try {
                for (int qrCounter = 0; qrCounter < w * h; qrCounter++) {
                    drawQRInPosition(image, toCode[qrCounter], qrPositions.get(qrCounter));
                }

            } catch (WriterException e) {
                return null;
            }

            return image;

        }

        private Bitmap get8x9Example() {

            int w = 8;
            int h = 9;

            String[] toCode = new String[w * h];
            Random ran = new Random();
            for (int i = 0; i < toCode.length; i++) {
                toCode[i] = "Ej " + ran.nextInt(9999);
            }

            //region qrPositions

            ArrayList<Point> qrPositions = new ArrayList<>();
            int r = 0;
            int c = 0;
            for (int i = 0; i < w * h; i++) {
                qrPositions.add(new Point(r * 88 + 2,
                        c * 100 + 2));
                if (r < w - 1) {
                    r++;
                } else {
                    r = 0;
                    c++;
                }
            }

            //endregion

            int bw = 706;
            int bh = 902;

            Bitmap image = Bitmap.createBitmap(bw, bh, Bitmap.Config.RGB_565);
            new Canvas(image).drawColor(Color.WHITE);

            //qrs
            try {
                for (int qrCounter = 0; qrCounter < w * h; qrCounter++) {
                    drawQRInPosition(image, toCode[qrCounter], qrPositions.get(qrCounter));
                }

            } catch (WriterException e) {
                return null;
            }

            return image;

        }

        private Bitmap get8x10Example() {

            int w = 8;
            int h = 10;

            String[] toCode = new String[w * h];
            Random ran = new Random();
            for (int i = 0; i < toCode.length; i++) {
                toCode[i] = "Ej " + ran.nextInt(9999);
            }

            //region qrPositions

            ArrayList<Point> qrPositions = new ArrayList<>();
            int r = 0;
            int c = 0;
            for (int i = 0; i < w * h; i++) {
                qrPositions.add(new Point(r * 88 + 2,
                        c * 100 + 2));
                if (r < w - 1) {
                    r++;
                } else {
                    r = 0;
                    c++;
                }
            }

            //endregion

            int bw = 706;
            int bh = 1002;

            Bitmap image = Bitmap.createBitmap(bw, bh, Bitmap.Config.RGB_565);
            new Canvas(image).drawColor(Color.WHITE);

            //qrs
            try {
                for (int qrCounter = 0; qrCounter < w * h; qrCounter++) {
                    drawQRInPosition(image, toCode[qrCounter], qrPositions.get(qrCounter));
                }

            } catch (WriterException e) {
                return null;
            }

            return image;

        }

        private void drawQRInPosition(Bitmap bigImage, String toCode, Point position) throws WriterException {

            int size = 120;
            int toCut = 17;
            int labelH = 12;

            //Code Qr
            Hashtable hintMap = new Hashtable();
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix byteMatrix = qrCodeWriter.encode(toCode,
                    BarcodeFormat.QR_CODE, size, size, hintMap);

            for (int mX = toCut; mX < byteMatrix.getWidth() - toCut; mX++) {
                for (int mY = toCut; mY < byteMatrix.getWidth() - toCut; mY++) {
                    if (byteMatrix.get(mX, mY)) {
                        bigImage.setPixel(position.x + mX - toCut, position.y + mY - toCut, Color.BLACK);
                    }
                    //else {
                    //  bigImage.setPixel(position.x + mX - toCut, position.y + mY - toCut, Color.WHITE);
                    //}
                }
            }
            new Canvas(bigImage).drawText(toCode, position.x,
                    position.y + size - 2 * toCut + labelH - 2,
                    new Paint());

        }
    }

    private boolean deleteOldFiles() {

        File[] files = new File(AppStatics.db.getPreference(DB.PT.PNames.QRS_DIRECTORY_PATH)).listFiles();
        for (File f : files) {
            if (!f.delete()) return false;
        }
        return true;
    }

}
