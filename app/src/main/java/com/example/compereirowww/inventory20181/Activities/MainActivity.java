package com.example.compereirowww.inventory20181.Activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.compereirowww.inventory20181.DataBase.DB;
import com.example.compereirowww.inventory20181.DataBase.DB.IT;
import com.example.compereirowww.inventory20181.DataBase.DB.PT;
import com.example.compereirowww.inventory20181.R;
import com.example.compereirowww.inventory20181.Tools.Tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {


    //GUI
    TextView textView;
    Button inventoryBtn, importBtn, insertBtn, searchBtn, readQRBtn, confBtn, makeQRBtn, expBtn;

    //DB
    private DB db;

    //Exporting
    ExportInventoryToCSVAT expAsyncTask;
    private static String EXPORTED_FILE = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Debug
        Log.d("JOSE2", "MainActivity.onCreate +++++++++++++++++++++++++++++++++++++++++++++++++++++");

        //GUI

        textView = (TextView) findViewById(R.id.text_tv);
        inventoryBtn = (Button) findViewById(R.id.inventory_btn);
        inventoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, InventoryActivity.class));
            }
        });
        importBtn = (Button) findViewById(R.id.import_btn);
        importBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ImportActivity.class));
            }
        });
        insertBtn = (Button) findViewById(R.id.insert_btn);
        insertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.setPreference(PT.PNames.NUMBER_TO_EDIT, PT.PDefaultValues.EMPTY_PREFERENCE);
                db.setPreference(PT.PNames.TEMP_NUMBER, PT.PDefaultValues.EMPTY_PREFERENCE);
                db.setPreference(PT.PNames.TEMP_DESCRIPTION, PT.PDefaultValues.EMPTY_PREFERENCE);
                db.setPreference(PT.PNames.TEMP_LOCATION, PT.PDefaultValues.EMPTY_PREFERENCE);
                db.setPreference(PT.PNames.TEMP_FOLLOWING, IT.FollowingValues.NO);
                db.setPreference(PT.PNames.TEMP_STATE, IT.StateValues.LEFTOVER);
                db.setPreference(PT.PNames.TEMP_TYPE, IT.TypeValues.UNKNOWN);
                db.setPreference(PT.PNames.TEMP_OBSERVATION, PT.PDefaultValues.EMPTY_PREFERENCE);
                startActivity(new Intent(MainActivity.this, NewNumberActivity.class));
            }
        });
        searchBtn = (Button) findViewById(R.id.search_btn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.setPreference(PT.PNames.TEMP_SEARCH_CRITERIA, PT.PDefaultValues.EMPTY_PREFERENCE);
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
            }
        });
        readQRBtn = (Button) findViewById(R.id.read_qr);
        readQRBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callQRDecoder(AppStatics.QR_DECODER_REQUEST);
            }
        });
        confBtn = (Button) findViewById(R.id.configuration_btn);
        confBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.setPreference(PT.PNames.TEMP_AREAS_TO_FOLLOW_CSV, PT.PDefaultValues.EMPTY_PREFERENCE);
                startActivity(new Intent(MainActivity.this, ConfigurationActivity.class));
            }
        });
        makeQRBtn = (Button) findViewById(R.id.make_qr);
        makeQRBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, QRFactoryActivity.class));
            }
        });
        expBtn = (Button) findViewById(R.id.export);
        expBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expAsyncTask = new ExportInventoryToCSVAT();
                expAsyncTask.execute();
            }
        });


        //DB
        db = new DB(getApplicationContext());
        AppStatics.db = db;

        //AppStatics
        AppStatics.Area.updateAreas(db);
        AppStatics.Location.updateLocations(db);
        AppStatics.Observation.updateObservations(db);
        AppStatics.AreasToFollow.updateAreasToFollow(db);
        AppStatics.Description.descriptions = new String[]{""};

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

        //Checking out of date
        db.updatePresentToMissingIfOutOfDate(Integer.parseInt(db.getPreference(PT.PNames.UPDATE_CRITERIA)));
        AppStatics.AllNumbers.allNumbers = new String[]{""};

    }

    @Override
    public void onBackPressed() {

        Tools.showDialogMessage(MainActivity.this, "Seguro desea salir?", "Sí",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }, "No", null);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == AppStatics.QR_DECODER_REQUEST) {
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra("SCAN_RESULT");

                if (db.numberExist(result)) {

                    if (db.getNumberState(result) != IT.StateValues.LEFTOVER &&
                            db.getNumberState(result) != IT.StateValues.LEFTOVER_PRESENT) {

                        db.updateState(result, IT.StateValues.PRESENT);
                        db.updateLastChecking(result, Tools.getDate());
                        Tools.showToast(MainActivity.this,
                                "El número ha sido marcado como " +
                                        IT.StateValues.toString(IT.StateValues.PRESENT), false);

                    } else {
                        db.updateState(result, IT.StateValues.LEFTOVER_PRESENT);
                        db.updateLastChecking(result, Tools.getDate());
                        Tools.showToast(MainActivity.this,
                                "El número ha sido marcado como " +
                                        IT.StateValues.toString(IT.StateValues.LEFTOVER_PRESENT), false);
                    }

                    db.setPreference(PT.PNames.NUMBER_TO_EDIT, result);
                    db.setPreference(DB.PT.PNames.TEMP_NUMBER, PT.PDefaultValues.EMPTY_PREFERENCE);
                    startActivity(new Intent(MainActivity.this, EditActivity.class));

                } else {
                    Tools.showToast(MainActivity.this, "Ningún número válido leído!", false);
                    finish();
                }


            }
        }

    }


    private boolean handleAppFiles() {

        //Permissions
        verifyStoragePermissions();

        //SD Card
        File sdcard = Environment.getExternalStorageDirectory();
        if (sdcard == null) {

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

        //QR codes file
        File qrsFile = new File(appFile, AppStatics.APP_QRS_FILE_NAME);
        if (!qrsFile.exists()) {
            if (qrsFile.mkdir()) {
                if (qrsFile.isDirectory()) {
                    Tools.showToast(getApplicationContext(),
                            getString(R.string.text1) + qrsFile.getPath(), true);
                } else {
                    Tools.showInfoDialog(MainActivity.this, getString(R.string.error2) +
                            getString(R.string.text12), "Cerrar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                            System.exit(0);
                        }
                    });
                    qrsFile.delete();
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
        db.setPreference(DB.PT.PNames.ROOT_DIRECTORY_PATH, appFile.getPath());
        db.setPreference(DB.PT.PNames.TO_IMPORT_DIRECTORY_PATH, toImportFile.getPath());
        db.setPreference(DB.PT.PNames.SAVE_DIRECTORY_PATH, toSaveFile.getPath());
        db.setPreference(PT.PNames.QRS_DIRECTORY_PATH, qrsFile.getPath());
        return true;

    }

    private boolean checkingImportation() {

        if (db.getPreference(PT.PNames.APP_STATE).equals(PT.PDefaultValues.IMPORTING) ||
                db.getPreference(PT.PNames.APP_STATE).equals(PT.PDefaultValues.FINISHING_IMPORTATION)) {

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
        } else if (db.getPreference(PT.PNames.APP_STATE).equals(PT.PDefaultValues.IMPORTATION_CANCELLED)) {
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
                            db.setPreference(PT.PNames.TEMP_AREAS_TO_FOLLOW_CSV, PT.PDefaultValues.EMPTY_PREFERENCE);
                            startActivity(new Intent(MainActivity.this, ConfigurationActivity.class));
                        }
                    });
            return false;
        }
        return true;
    }

    /**
     * Checks if the app has permission to write to device storage
     * <p>
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

    private void callQRDecoder(int requestCode) {

        try {
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes
            startActivityForResult(intent, requestCode);

        } catch (Exception e) {

            Toast.makeText(MainActivity.this, "Error: Debe instalar primero la aplicación " +
                            "de escanear código QR: com.google.zxing.client.android-4.7.3-103-minAPI15",
                    Toast.LENGTH_LONG).show();
        }
    }

    //region Getters and Setters


    //endregion

    private class ExportInventoryToCSVAT extends AsyncTask<String, Void, Boolean> {

        StringBuilder data;

        @Override
        protected Boolean doInBackground(String... strings) {

            //CSV Inventory Save Format
            //Head
            //Data: Number, Location, Type, Observation

            File csvFile;
            if (EXPORTED_FILE.equals("")) {
                String fName = "Salva Inventario " + Tools.getFormattedDateForFileNaming() + ".csv";
                csvFile = new File(AppStatics.db.getPreference(PT.PNames.TO_IMPORT_DIRECTORY_PATH),
                        fName);
                EXPORTED_FILE = fName;
            } else {
                csvFile = new File(AppStatics.db.getPreference(PT.PNames.TO_IMPORT_DIRECTORY_PATH),
                        EXPORTED_FILE);
            }

            if (isCancelled()) {
                return false;
            }

            Cursor cursor = AppStatics.db.getAllData();
            data = new StringBuilder();
            String line;
            while (cursor.moveToNext()) {

                if (isCancelled()) {
                    return false;
                }


                line = cursor.getString(IT.Indexes.NUMBER_COLUMN_INDEX);
                line += ",";
                line += cursor.getString(IT.Indexes.LOCATION_COLUMN_INDEX);
                line += ",";
                line += cursor.getString(IT.Indexes.TYPE_COLUMN_INDEX);
                line += ",";
                line += cursor.getString(IT.Indexes.OBSERVATION_COLUMN_INDEX);
                line += "\n";

                data.append(line);
            }

            String head = AppStatics.SALVA_INVENTORY_FILE_HEAD_CODE + "," +
                    Tools.myStringHashCode(data.toString().replaceAll("\n", "")) + "\n";

            if (isCancelled()) {
                return false;
            }

            try {
                Tools.writeFile(csvFile, head + data.toString());
            } catch (IOException e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean r) {
            if (r) {
                Tools.showToast(MainActivity.this, "Inventario Exportado!", false);
                EXPORTED_FILE = "";
            }
        }
    }

}
