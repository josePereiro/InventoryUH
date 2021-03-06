package com.example.compereirowww.inventory20181.Activities;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.compereirowww.inventory20181.R;
import com.example.compereirowww.inventory20181.Tools.Tools;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.Hashtable;

public class QRViewerActivity extends AppCompatActivity {

    //GUI
    private EditText numberToCodeET;
    private ImageView qrIV;
    private CreateQRAT createQRAT;


    private static String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_viewer);

        //GUI
        AppStatics.formatView((TextView)findViewById(R.id.textView));
        numberToCodeET = (EditText) findViewById(R.id.number_et);
        AppStatics.formatView(numberToCodeET);
        qrIV = (ImageView) findViewById(R.id.qr_iv);
        numberToCodeET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                //TODO deb
                Log.d(AppStatics.APP_TAG, "EditText afterTextChanged...");


                //QR
                if (!numberToCodeET.getText().toString().equals("")) {

                    //TODO implement Asyc Task
                    if (createQRAT != null) {
                        createQRAT.cancel(true);
                        createQRAT = new CreateQRAT();
                        createQRAT.execute(numberToCodeET.getText().toString());
                    } else {
                        createQRAT = new CreateQRAT();
                        createQRAT.execute(numberToCodeET.getText().toString());
                    }

                    //TODO deb
                    Log.d(AppStatics.APP_TAG, "Text Changed and Not Empty, qr generated, spinner selected...");

                } else {
                    if (createQRAT != null) {
                        createQRAT.cancel(true);
                        createQRAT = null;
                    }
                    qrIV.setImageDrawable(null);
                }


            }
        });
        if (!text.equals("")) {
            numberToCodeET.setText(text);
            text = "";
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
            Tools.showToast(QRViewerActivity.this, "No hay ayuda!!! Por ahora...", false);
            return super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }

    public static void setText(String text) {
        QRViewerActivity.text = text;
    }

    private class CreateQRAT extends AsyncTask<String, String, Bitmap> {

        private final int QR_IMAGE_SIZE = 1200;

        @Override
        protected Bitmap doInBackground(String... strings) {

            try {
                // Create the ByteMatrix for the QR-Code that encodes the given String
                Hashtable hintMap = new Hashtable();
                hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
                QRCodeWriter qrCodeWriter = new QRCodeWriter();
                BitMatrix byteMatrix;
                if (isCancelled()) return null;
                byteMatrix = qrCodeWriter.encode(strings[0],
                        BarcodeFormat.QR_CODE, QR_IMAGE_SIZE, QR_IMAGE_SIZE, hintMap);

                if (isCancelled()) return null;
                // Make the Image that are to hold the QRCode
                int matrixWidth = byteMatrix.getWidth();
                Bitmap image = Bitmap.createBitmap(matrixWidth, matrixWidth, Bitmap.Config.RGB_565);
                new Canvas(image).drawColor(Color.WHITE);
                //Filling the image with the byteMatrix backUpSB
                for (int i = 0; i < matrixWidth; i++) {
                    for (int j = 0; j < matrixWidth; j++) {
                        if (isCancelled()) return null;
                        if (byteMatrix.get(i, j)) {
                            image.setPixel(i, j, Color.BLACK);
                        }
                    }
                }
                return image;
            } catch (WriterException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null)
                qrIV.setImageBitmap(bitmap);
        }

    }

}