package com.example.compereirowww.inventory20181.Activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.compereirowww.inventory20181.DataBase.DB;
import com.example.compereirowww.inventory20181.DataBase.DB.*;
import com.example.compereirowww.inventory20181.R;
import com.example.compereirowww.inventory20181.Tools.Tools;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    //region fields
    //GUI
    TextView textView;
    FloatingActionButton fab;

    //DB
    private DB db;

    //endregion

    //region life cycle methods...

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Debug
        Log.d("JOSE2", "MainActivity.onCreate +++++++++++++++++++++++++++++++++++++++++++++++++++++");


        //GUI
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        textView = (TextView) findViewById(R.id.textView1);

    }

    @Override
    protected void onResume() {
        super.onResume();

        //TODO Deb
        Log.d("JOSE", "MainActivity.onResume +++++++++++++++++++++++++++++++++++++++++++++++++++++");

        //DB
        db = new DB(getApplicationContext());
        AppStatics.db = db;

        //region Handling files

        if (!handleAppFiles()) {
            //TODO handle state
        }

        //endregion

        //region Checking Importation
        if (db.getPreference(RT.APP_IMPORTING).equals(RT.YES) || db.getPreference(RT.APP_IMPORTING).equals(RT.FINISHING)) {
            Tools.showDialogMessage(MainActivity.this, getString(R.string.text8),
                    "Cerrar",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                            System.exit(0);
                        }
                    },
                    "Continuar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(MainActivity.this, ImportActivity.class));
                        }
                    });
        } else if (db.getPreference(RT.APP_IMPORTING).equals(RT.CANCELLED)) {
            Tools.showDialogMessage(MainActivity.this, getString(R.string.text9),
                    "Cerrar",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                            System.exit(0);
                        }
                    },
                    "Continuar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(MainActivity.this, ImportActivity.class));
                        }
                    });
        }

        //endregion

        //GUI
        //TODO Delete
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ImportActivity.class));
            }
        });

        //TODO test
        textView.setText(String.valueOf(db.getNumberCount()));

    }

    //endregion

    //region override methods...
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.import_inventory) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //endregion

    //region methods...

    private boolean handleAppFiles() {

        //Permissions
        verifyStoragePermissions();

        //SD Card
        File sdcard = Environment.getExternalStorageDirectory();
        if (sdcard == null || !sdcard.isDirectory()) {
            Tools.showToast(getApplicationContext(),
                    getString(R.string.error1), true);
            return false;
        }

        //Root file
        File appFile = new File(sdcard, AppStatics.APP_FILE);
        if (!appFile.exists()) {
            if (appFile.mkdir()) {
                if (appFile.isDirectory()) {
                    Tools.showToast(getApplicationContext(),
                            getString(R.string.text1) + appFile.getPath(), true);

                } else {
                    Tools.showToast(getApplicationContext(),
                            getString(R.string.error2), true);
                    appFile.delete();
                    return false;
                }
            } else {
                Tools.showToast(getApplicationContext(),
                        getString(R.string.error2), true);
                return false;
            }
        }

        //To Import File
        File toImportFile = new File(appFile, AppStatics.APP_TO_IMPORT_FILE);
        if (!toImportFile.exists()) {
            if (toImportFile.mkdir()) {
                if (toImportFile.isDirectory()) {
                    Tools.showToast(getApplicationContext(),
                            getString(R.string.text1) + toImportFile.getPath(), true);

                } else {
                    Tools.showToast(getApplicationContext(),
                            getString(R.string.error2), true);
                    toImportFile.delete();
                    return false;
                }
            } else {
                Tools.showToast(getApplicationContext(),
                        getString(R.string.error2), true);
            }
        }

        //To save file
        File toSaveFile = new File(appFile, AppStatics.APP_SAVE_FILE);
        if (!toSaveFile.exists()) {
            if (toSaveFile.mkdir()) {
                if (toSaveFile.isDirectory()) {
                    Tools.showToast(getApplicationContext(),
                            getString(R.string.text1) + toSaveFile.getPath(), true);
                } else {
                    Tools.showToast(getApplicationContext(),
                            getString(R.string.error2), true);
                    toSaveFile.delete();
                    return false;
                }
            } else {
                Tools.showToast(getApplicationContext(),
                        getString(R.string.error2), true);
            }
        }

        //Updating preference
        db.setPreference(DB.RT.ROOT_DIRECTORY_PATH, appFile.getPath());
        db.setPreference(DB.RT.TO_IMPORT_DIRECTORY_PATH, toImportFile.getPath());
        db.setPreference(DB.RT.SAVE_DIRECTORY_PATH, toSaveFile.getPath());
        return true;

    }

    /**
     * Checks if the app has permission to write to device storage
     * <p/>
     * If the app does not has permission then the user will be prompted to grant permissions
     */
    public void verifyStoragePermissions() {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    AppStatics.PERMISSIONS_STORAGE,
                    AppStatics.REQUEST_EXTERNAL_STORAGE
            );
        }
    }


    //endregion

    //region Getters and Setters


    //endregion

}
