package com.example.compereirowww.inventory20181.Activities;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

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
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;

public class PrintableQRsFactoryActivity extends AppCompatActivity {

    //GUI
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printable_qrs_factory);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String[] toCode = new String[72];
                Random R = new Random();
                for (int i = 0; i < toCode.length; i++) {
                    toCode[i] = String.valueOf(R.nextInt(999999999));
                }

                new CreateQR8x9PageAT().execute(toCode);

            }
        });
        imageView = (ImageView) findViewById(R.id.imageView);

    }

    private class CreateQR8x9PageAT extends AsyncTask<String, Bitmap, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... toCode) {

            //region startPoints

            ArrayList<Point> startsPoints = new ArrayList<>();
            int r = 0;
            int c = 0;
            for (int i = 0; i < toCode.length; i++) {
                startsPoints.add(new Point(r * 88 + 2,
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

            Bitmap gridImage = Bitmap.createBitmap(bw, bh, Bitmap.Config.RGB_565);
            new Canvas(gridImage).drawColor(Color.GRAY);

            publishProgress(gridImage);

            //drawing images
            for (int i = 0; i < toCode.length && i < 72; i++) {
                try {
                    drawQRInPosition(gridImage, toCode[i], startsPoints.get(i));
                    publishProgress(gridImage);
                } catch (WriterException e) {
                    return null;
                }
            }

            return gridImage;
        }

        @Override
        protected void onProgressUpdate(Bitmap... bitmaps) {
            imageView.setImageBitmap(bitmaps[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

            File imageFile = new File(AppStatics.db.getPreference(DB.PT.PNames.QRS_DIRECTORY_PATH),
                    Tools.getFormattedDateForFileNaming() + ".jpg");

            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            } else {
                Bitmap gridImage = Bitmap.createBitmap(100, 100, Bitmap.Config.RGB_565);
                new Canvas(gridImage).drawColor(Color.GRAY);
                Tools.showToast(PrintableQRsFactoryActivity.this,
                        "Error durante el proceso de construccuón!!!", false);
            }


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
                    } else {
                        bigImage.setPixel(position.x + mX - toCut, position.y + mY - toCut, Color.WHITE);
                    }
                }
            }
            new Canvas(bigImage).drawText(toCode, position.x,
                    position.y + size - 2 * toCut + labelH - 2,
                    new Paint());

        }
    }
}
