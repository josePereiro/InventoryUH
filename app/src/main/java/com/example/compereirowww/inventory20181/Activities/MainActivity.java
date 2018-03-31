package com.example.compereirowww.inventory20181.Activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.compereirowww.inventory20181.DataBase.DB;
import com.example.compereirowww.inventory20181.DataBase.DB.IT;
import com.example.compereirowww.inventory20181.DataBase.DB.RT;
import com.example.compereirowww.inventory20181.R;
import com.example.compereirowww.inventory20181.Tools.Tools;

import java.io.File;

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

        //DB
        db = new DB(getApplicationContext());
        AppStatics.db = db;

        //AppStatics
        AppStatics.Area.updateAreas(db);
        AppStatics.Location.updateLocations(db);
        AppStatics.Observation.updateObservations(db);


        //TODO Test
        db.setPreference(RT.NUMBER_TO_EDIT, "0321824");
        startActivity(new Intent(MainActivity.this, EditActivity.class));
        if (!db.numberExist("1")) {
            db.insertNewNumber("1", "", "Deb", "1/1/2013", "1/1/2015", true, IT.StateValues.MISSING, 100
                    , IT.TypeValues.EQUIPMENT, "loc1", "");
            db.insertNewNumber("2", "", "Deb", "1/1/2013", "1/1/2015", false, IT.StateValues.MISSING, 100
                    , IT.TypeValues.FURNISHING, "loc1", "");
            db.insertNewNumber("3", "", "Deb", "1/1/2013", "1/1/2015", false, IT.StateValues.LEFTOVER, 100
                    , IT.TypeValues.EQUIPMENT, "loc2", "");
            db.insertNewNumber("4", "", "Deb", "1/1/2013", "1/1/2015", true, IT.StateValues.IGNORED_MISSING, 100
                    , IT.TypeValues.FURNISHING, "loc1", "");
            db.insertNewNumber("5", "", "Deb", "1/1/2013", "1/1/2015", true, IT.StateValues.MISSING, 100
                    , IT.TypeValues.FURNISHING, "loc2", "");
            db.insertNewNumber("6", "", "Deb", "1/1/2013", "1/1/2015", false, IT.StateValues.PRESENT, 100
                    , IT.TypeValues.EQUIPMENT, "loc1", "");
            db.insertNewNumber("7", "", "Deb", "1/1/2013", "1/1/2015", true, IT.StateValues.PRESENT, 100
                    , IT.TypeValues.EQUIPMENT, "loc2", "");
            db.insertNewNumber("8", "", "Deb", "1/1/2013", "1/1/2015", false, IT.StateValues.MISSING, 100
                    , IT.TypeValues.EQUIPMENT, "loc1", "");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        //TODO Deb
        Log.d("JOSE", "MainActivity.onResume +++++++++++++++++++++++++++++++++++++++++++++++++++++");

        //files
        handleAppFiles();

        //importation
        checkingImportation();


        //GUI
        //TODO Delete
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ImportActivity.class));
            }
        });

        //TODO test
        //textView.setText(String.valueOf(db.getAllDataIfState(IT.StateValues.
        //        toString(IT.StateValues.IGNORED_MISSING)).getCount()));
        //textView.setText(Arrays.toString(AppStatics.Location.locations));
        textView.setText(String.valueOf(Tools.myStringHashCode("Hola Jose")));

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

            startActivity(new Intent(MainActivity.this, ImportActivity.class));
            return true;
        } else if (id == R.id.see_inventory) {
            startActivity(new Intent(MainActivity.this, InventoryActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        Tools.showDialogMessage(MainActivity.this, "Seguro desea salir?", "Sí",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                        System.exit(0);
                    }
                }, "No", null);

    }

    //endregion

    //region methods...

    private void handleAppFiles() {

        //Permissions
        verifyStoragePermissions();

        //SD Card
        File sdcard = Environment.getExternalStorageDirectory();
        if (sdcard == null || !sdcard.isDirectory()) {

            Tools.showInfoDialog(MainActivity.this, getString(R.string.error1) +
                    getString(R.string.text12), "Cerrar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                    System.exit(0);
                }
            });
        }

        //Root file
        File appFile = new File(sdcard, AppStatics.APP_FILE_NAME);
        if (!appFile.exists()) {
            if (appFile.mkdir()) {
                if (appFile.isDirectory()) {
                    Tools.showToast(getApplicationContext(),
                            getString(R.string.text1) + appFile.getPath(), true);

                } else {

                    Tools.showInfoDialog(MainActivity.this, getString(R.string.error2) +
                            getString(R.string.text12), "Cerrar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                            System.exit(0);
                        }
                    });
                    appFile.delete();
                }
            } else {
                Tools.showInfoDialog(MainActivity.this, getString(R.string.error2) +
                        getString(R.string.text12), "Cerrar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                        System.exit(0);
                    }
                });
            }
        }

        //To Import File
        File toImportFile = new File(appFile, AppStatics.APP_TO_IMPORT_FILE_NAME);
        if (!toImportFile.exists()) {
            if (toImportFile.mkdir()) {
                if (toImportFile.isDirectory()) {
                    Tools.showToast(getApplicationContext(),
                            getString(R.string.text1) + toImportFile.getPath(), true);

                } else {

                    Tools.showInfoDialog(MainActivity.this, getString(R.string.error2) +
                            getString(R.string.text12), "Cerrar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                            System.exit(0);
                        }
                    });
                    toImportFile.delete();
                }
            } else {
                Tools.showInfoDialog(MainActivity.this, getString(R.string.error2) +
                        getString(R.string.text12), "Cerrar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                        System.exit(0);
                    }
                });
            }
        }

        //To save file
        File toSaveFile = new File(appFile, AppStatics.APP_SAVE_FILE_NAME);
        if (!toSaveFile.exists()) {
            if (toSaveFile.mkdir()) {
                if (toSaveFile.isDirectory()) {
                    Tools.showToast(getApplicationContext(),
                            getString(R.string.text1) + toSaveFile.getPath(), true);
                } else {
                    Tools.showInfoDialog(MainActivity.this, getString(R.string.error2) +
                            getString(R.string.text12), "Cerrar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                            System.exit(0);
                        }
                    });
                    toSaveFile.delete();
                }
            } else {
                Tools.showInfoDialog(MainActivity.this, getString(R.string.error2) +
                        getString(R.string.text12), "Cerrar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                        System.exit(0);
                    }
                });
            }
        }

        //Updating preference
        db.setPreference(DB.RT.ROOT_DIRECTORY_PATH, appFile.getPath());
        db.setPreference(DB.RT.TO_IMPORT_DIRECTORY_PATH, toImportFile.getPath());
        db.setPreference(DB.RT.SAVE_DIRECTORY_PATH, toSaveFile.getPath());

    }

    private void checkingImportation() {
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
        } else if (db.getAllData().getCount() == 0) {
            Tools.showInfoDialog(MainActivity.this, "La base de datos está vacía, necesita importar!",
                    "Importar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(MainActivity.this, ImportActivity.class));
                        }
                    });
        }
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
