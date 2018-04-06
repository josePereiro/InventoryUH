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
import android.widget.TextView;

import com.example.compereirowww.inventory20181.DataBase.DB;
import com.example.compereirowww.inventory20181.DataBase.DB.RT;
import com.example.compereirowww.inventory20181.R;
import com.example.compereirowww.inventory20181.Tools.Tools;

import java.io.File;
import java.util.Arrays;

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
        textView = (TextView) findViewById(R.id.textView1);

        //DB
        db = new DB(getApplicationContext());
        AppStatics.db = db;

        //AppStatics
        AppStatics.Area.updateAreas(db);
        AppStatics.Location.updateLocations(db);
        AppStatics.Observation.updateObservations(db);
        AppStatics.AreasToFollow.updateAreasToFollow(db);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //TODO Deb
        Log.d("JOSE", "MainActivity.onResume +++++++++++++++++++++++++++++++++++++++++++++++++++++");

        //files
        if (handleAppFiles())
            if (checkingImportation())
                checkingAreaToFollow();

        //TODO test
        textView.setText(db.getPreference(RT.UPDATE_CRITERIA));

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
        } else if (id == R.id.configuration) {
            db.setPreference(RT.TEMP_AREAS_TO_FOLLOW_CSV, RT.EMPTY_PREFERENCE);
            startActivity(new Intent(MainActivity.this, ConfigurationActivity.class));
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

    private boolean handleAppFiles() {

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
            return false;
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
                    return false;
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
                return false;
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
                    return false;
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
                return false;
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
                    return false;
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
                return false;
            }
        }

        //Updating preference
        db.setPreference(DB.RT.ROOT_DIRECTORY_PATH, appFile.getPath());
        db.setPreference(DB.RT.TO_IMPORT_DIRECTORY_PATH, toImportFile.getPath());
        db.setPreference(DB.RT.SAVE_DIRECTORY_PATH, toSaveFile.getPath());
        return true;

    }

    private boolean checkingImportation() {
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
            return false;
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
            return false;
        } else if (db.getAllData().getCount() == 0) {
            Tools.showInfoDialog(MainActivity.this, "La base de datos está vacía, necesita importar!",
                    "Importar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(MainActivity.this, ImportActivity.class));
                        }
                    });
            return false;
        }
        return true;
    }

    private boolean checkingAreaToFollow() {
        //area
        if (Arrays.equals(AppStatics.AreasToFollow.areasToFollow, new String[]{""})) {
            Tools.showInfoDialog(MainActivity.this, "Debe seleccionar un área a seguir!", "Seleccionar",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            db.setPreference(RT.TEMP_AREAS_TO_FOLLOW_CSV, RT.EMPTY_PREFERENCE);
                            startActivity(new Intent(MainActivity.this, ConfigurationActivity.class));
                        }
                    });
            return false;
        }
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
