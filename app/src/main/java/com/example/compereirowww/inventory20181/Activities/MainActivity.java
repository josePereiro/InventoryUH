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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.compereirowww.inventory20181.DataBase.DB;
import com.example.compereirowww.inventory20181.DataBase.DB.IT;
import com.example.compereirowww.inventory20181.DataBase.DB.PT;
import com.example.compereirowww.inventory20181.R;
import com.example.compereirowww.inventory20181.Tools.Tools;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import static com.example.compereirowww.inventory20181.Activities.AppStatics.APP_BACKUP_FILE_NAME;
import static com.example.compereirowww.inventory20181.Activities.AppStatics.APP_FILE_NAME;
import static com.example.compereirowww.inventory20181.Activities.AppStatics.APP_QRS_FILE_NAME;
import static com.example.compereirowww.inventory20181.Activities.AppStatics.APP_REPORT_FILE_NAME;
import static com.example.compereirowww.inventory20181.Activities.AppStatics.Area;
import static com.example.compereirowww.inventory20181.Activities.AppStatics.AreasToFollow;
import static com.example.compereirowww.inventory20181.Activities.AppStatics.Description;
import static com.example.compereirowww.inventory20181.Activities.AppStatics.Location;
import static com.example.compereirowww.inventory20181.Activities.AppStatics.Observation;
import static com.example.compereirowww.inventory20181.Activities.AppStatics.PERMISSIONS_STORAGE;
import static com.example.compereirowww.inventory20181.Activities.AppStatics.QR_DECODER_REQUEST;
import static com.example.compereirowww.inventory20181.Activities.AppStatics.REQUEST_EXTERNAL_STORAGE;
import static com.example.compereirowww.inventory20181.Activities.AppStatics.db;

public class MainActivity extends AppCompatActivity {


    //GUI
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.title_activity_main);

        //Debug
        Log.d("JOSE2", "MainActivity.onCreate +++++++++++++++++++++++++++++++++++++++++++++++++++++");

        //DB
        db = new DB(getApplicationContext());

        //GUI
        textView = (TextView) findViewById(R.id.text_tv);
        AppStatics.formatView(textView);

    }

    @Override
    protected void onResume() {
        super.onResume();

        //TODO Deb
        Log.d("JOSE", "MainActivity.onResume +++++++++++++++++++++++++++++++++++++++++++++++++++++");

        if (!verifyStoragePermissions()) {
            askForStoragePermissions();
        }
        if (!handleAppFiles()) return;
        if (!checkingImportation()) return;
        if (!checkingAreaToFollow()) return;

        db.updatePresentToMissingIfOutOfDate(Integer.parseInt(db.getPreference(PT.PNames.UPDATE_CRITERIA)));
        new MakeReportTXTAT().execute(db.getAllDataIfFollowing(IT.FollowingValues.YES));
        new UpdateBigStatics().execute();
    }

    @Override
    public void onBackPressed() {

        Tools.showDialogMessage(MainActivity.this, "Seguro desea salir?", "Sí",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        System.exit(0);
                    }
                }, "No", null);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == QR_DECODER_REQUEST) {
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

                    showNumberDetailDialog(result);

                } else {
                    Tools.showToast(MainActivity.this, "Ningún número válido leído!", false);
                }


            }
        }

    }

    private void showNumberDetailDialog(final String number) {
        String ms = "Número: " + number + "\n";
        Cursor numberData = db.getAllNumberData(number);
        numberData.moveToNext();
        ms += "Description: " + numberData.getString(IT.Indexes.DESCRIPTION_COLUMN_INDEX) + "\n";
        ms += "Área: " + numberData.getString(IT.Indexes.AREA_COLUMN_INDEX) + "\n";
        ms += "Estado: " +
                IT.StateValues.toString(Integer.
                        parseInt(numberData.getString(IT.Indexes.STATE_COLUMN_INDEX))) + "\n";
        ms += "Tipo: " +
                IT.TypeValues.toString(Integer.
                        parseInt(numberData.getString(IT.Indexes.TYPE_COLUMN_INDEX))) + "\n";
        ms += "Localización: " + numberData.getString(IT.Indexes.LOCATION_COLUMN_INDEX) + "\n";
        ms += "Observación: " + numberData.getString(IT.Indexes.OBSERVATION_COLUMN_INDEX) + "\n";
        numberData.close();

        Tools.showDialogMessage(MainActivity.this, ms, "Editar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                callEditActivity(number);
            }
        }, "Continuar", null);
    }

    private void callEditActivity(String number) {
        db.setPreference(PT.PNames.NUMBER_TO_EDIT, number);
        db.setPreference(DB.PT.PNames.TEMP_NUMBER, PT.PDefaultValues.EMPTY_PREFERENCE);
        startActivity(new Intent(MainActivity.this, EditActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.see_inventory) {
            callInventoryActivity();
            return super.onOptionsItemSelected(item);
        }
        if (id == R.id.import_inventory) {
            callImportActivity();
            return super.onOptionsItemSelected(item);
        }
        if (id == R.id.insert_new_number) {
            callNewNumberActivity();
            return super.onOptionsItemSelected(item);
        }
        if (id == R.id.search) {
            callSearchActivity();
            return super.onOptionsItemSelected(item);
        }
        if (id == R.id.qr_viewer) {
            callQRViewerActivity();
            return super.onOptionsItemSelected(item);
        }
        if (id == R.id.search_by_qr) {
            callQRDecoder(QR_DECODER_REQUEST);
            return super.onOptionsItemSelected(item);
        }
        if (id == R.id.configuration) {
            callConfigurationActivity();
            return super.onOptionsItemSelected(item);
        }
        if (id == R.id.help) {
            Tools.showToast(MainActivity.this, "No hay ayuda!!! Por ahora...", false);
            return super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean handleAppFiles() {

        if (!verifyStoragePermissions()) return false;

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
        File appFile = new File(sdcard, APP_FILE_NAME);
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
        File reportFile = new File(appFile, APP_REPORT_FILE_NAME);
        if (!reportFile.exists()) {
            if (reportFile.mkdir()) {
                if (reportFile.isDirectory()) {
                    Tools.showToast(getApplicationContext(),
                            getString(R.string.text1) + reportFile.getPath(), true);

                } else {
                    Tools.showInfoDialog(MainActivity.this, getString(R.string.error2) +
                            getString(R.string.text12), "Cerrar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                            System.exit(0);
                        }
                    });
                    reportFile.delete();
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
        File backupFile = new File(appFile, APP_BACKUP_FILE_NAME);
        if (!backupFile.exists()) {
            if (backupFile.mkdir()) {
                if (backupFile.isDirectory()) {
                    Tools.showToast(getApplicationContext(),
                            getString(R.string.text1) + backupFile.getPath(), true);
                } else {
                    Tools.showInfoDialog(MainActivity.this, getString(R.string.error2) +
                            getString(R.string.text12), "Cerrar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                            System.exit(0);
                        }
                    });
                    backupFile.delete();
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
        File qrsFile = new File(appFile, APP_QRS_FILE_NAME);
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
        db.setPreference(DB.PT.PNames.REPORTS_DIRECTORY_PATH, reportFile.getPath());
        db.setPreference(DB.PT.PNames.BACKUPS_DIRECTORY_PATH, backupFile.getPath());
        db.setPreference(PT.PNames.QRS_DIRECTORY_PATH, qrsFile.getPath());
        return true;

    }

    private boolean checkingImportation() {

        if (!db.getPreference(PT.PNames.CURRENT_FILE_TO_IMPORT).equals("")) {
            Tools.showToast(MainActivity.this, "La aplicación debe terminar de importar!!!", false);
            callImportActivity();
            return false;
        }
        if (db.getAllData().getCount() == 0) {
            Tools.showInfoDialog(MainActivity.this, "La base de datos está vacía, necesita importar!",
                    "Importar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            callImportActivity();
                        }
                    });
            return false;
        }
        if (db.getPreference(PT.PNames.DB_STATE).equals(PT.PDefaultValues.DB_CORRUPTED)) {

            Tools.showInfoDialog(MainActivity.this, "La base está corrupta, por favor importe un archivo de datos del inventario UH!!",
                    "Importar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            callImportActivity();
                        }
                    });
            return false;
        }

        return true;
    }

    private boolean checkingAreaToFollow() {
        AreasToFollow.updateAreasToFollow();
        if (Arrays.equals(AreasToFollow.areasToFollow, new String[]{""})) {
            Tools.showInfoDialog(MainActivity.this, "Debe seleccionar un área a seguir!", "Seleccionar",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            db.setPreference(PT.PNames.TEMP_AREAS_TO_FOLLOW_CSV, PT.PDefaultValues.EMPTY_PREFERENCE);
                            startActivity(new Intent(MainActivity.this, MainConfigurationActivity.class));
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
    public boolean verifyStoragePermissions() {

        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    public void askForStoragePermissions() {
        // We don't have permission so prompt the user
        ActivityCompat.requestPermissions(
                this,
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
        );
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

    private void callImportActivity() {
        ImportActivity.CSVFiles = null;
        startActivity(new Intent(MainActivity.this, ImportActivity.class));
    }

    private void callQRViewerActivity() {
        QRViewerActivity.setText("");
        startActivity(new Intent(MainActivity.this, QRViewerActivity.class));
    }

    private void callNewNumberActivity() {
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

    private void callConfigurationActivity() {
        db.setPreference(PT.PNames.TEMP_UPDATE_CRITERIA, PT.PDefaultValues.EMPTY_PREFERENCE);
        db.setPreference(PT.PNames.TEMP_TEXT_SIZE, PT.PDefaultValues.EMPTY_PREFERENCE);
        db.setPreference(PT.PNames.TEMP_AREAS_TO_FOLLOW_CSV, PT.PDefaultValues.EMPTY_PREFERENCE);
        startActivity(new Intent(MainActivity.this, MainConfigurationActivity.class));
    }

    private void callSearchActivity() {
        db.setPreference(PT.PNames.TEMP_SEARCH_CRITERIA, PT.PDefaultValues.EMPTY_PREFERENCE);
        startActivity(new Intent(MainActivity.this, SearchActivity.class));
    }

    private void callInventoryActivity() {
        startActivity(new Intent(MainActivity.this, InventoryActivity.class));
    }

    private class MakeReportTXTAT extends AsyncTask<Cursor, String, Boolean> {

        StringBuilder report;
        String creationDate;

        private String TAB = "   ";
        DataThree three;

        @Override
        protected void onPreExecute() {
            //AppStatics
            textView.setText("Creando Reporte...");
        }

        @Override
        protected Boolean doInBackground(Cursor... data) {

            initialising(data[0]);

            if (isCancelled()) {
                report = new StringBuilder();
                return false;
            }

            //report
            addHead();
            addGeneralDetailsSection();
            addAreasDetailsSection();

            return true;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            textView.setText(values[0]);
        }

        @Override
        protected void onPostExecute(Boolean r) {

            if (r)
                if (report.length() < 5000) {
                    textView.setText(report);
                } else {
                    textView.setText(report);
                    //textView.setText(report.substring(0, 4900) + " ...");
                }
        }

        private void initialising(Cursor data) {
            report = new StringBuilder();
            creationDate = Tools.getFormattedDateForFileNaming();
            three = new DataThree(data) {
                @Override
                public boolean onThreeProgressUpdate(long progress) {
                    long rest = progress % 3;
                    if (rest == 0) {
                        publishProgress("Creando reporte.");
                    } else if (rest == 1) {
                        publishProgress("Creando reporte..");
                    } else {
                        publishProgress("Creando reporte...");
                    }

                    if (isCancelled()) {
                        report = new StringBuilder();
                        return false;
                    }

                    return true;
                }
            };
        }

        private void addHead() {
            //REPORTE DEL INVENTARIO ##/##/## ##:##
            //

            report.append("REPORTE DE NÚMEROS EN SEGUIMIENTO ");
            report.append(creationDate);
            report.append("\n");
            report.append("\n");
        }

        private void addGeneralDetailsSection() {
            //---- DETALLES GENERALES ----
            //TOTAL DE NÚMEROS: ####
            //  TYPE: ###
            //      STATES: ###
            //
            //

            report.append("---- DETALLES GENERALES ----");
            report.append("\n");
            report.append("TOTAL DE NÚMEROS EN SEGUIMIENTO: ");
            report.append(three.getNumberCount());
            report.append("\n");
            addTypeSection(three.EQUIPMENT_INDEX);
            addTypeSection(three.FURNISHING_INDEX);
            addTypeSection(three.UNKNOWN_INDEX);
            report.append("\n");
            report.append("\n");
        }

        /**
         * Add the whole Area Details secction, that is, all the Areas one next to others
         * and a Detailed look of it!!!
         */
        private void addAreasDetailsSection() {
            //---- DETALLES POR ÁREAS ----
            //AREAS ANALIZADAS: ##
            //  AREAS
            //
            //

            report.append("---- DETALLES POR ÁREAS ----");
            report.append("\n");
            report.append("AREAS EN SEGUIMIENTO: ");
            report.append(three.getAreasCount());
            report.append("\n");
            for (int a = 0; a < three.getAreasCount(); a++) {
                report.append(TAB);
                report.append(three.getArea(a));
                report.append("\n");
            }
            report.append("\n");

            //NOMBRE DEL ÁREA
            //TOTAL DE NÚMEROS: ###
            //  TYPES: ###
            //      STATES: ###
            //
            //NOMBRE DEL AREA
            //LOCATIONS:
            //TOTAL DE NÚMEROS
            //  TYPES: ###
            //      STATE: ###
            //
            //

            for (int a = 0; a < three.getAreasCount(); a++) {
                addAreaSection(a);
            }
            report.append("\n");
        }

        /**
         * Add the details of a single Area
         *
         * @param area
         */
        private void addAreaSection(int area) {

            //NOMBRE DEL ÁREA
            //TOTAL DE NÚMEROS: ###
            //  TYPES: ###
            //      STATES: ###
            //


            report.append(three.getArea(area));
            report.append("\n");
            report.append("TOTAL DE NÚMEROS: ");
            report.append(three.getAreaNumbersCount(area));
            report.append("\n");
            addTypeSection(area, three.EQUIPMENT_INDEX);
            addTypeSection(area, three.FURNISHING_INDEX);
            addTypeSection(area, three.UNKNOWN_INDEX);
            report.append("\n");

            //NOMBRE DEL AREA
            //LOCATIONS:
            //TOTAL DE NÚMEROS
            //  TYPES: ###
            //      STATE: ###
            //
            //

            for (int l = 0; l < three.getLocationsCount(area); l++) {
                addLocationSection(area, l);
            }
        }

        /**
         * Add the details of an Location.
         *
         * @param areaIndex
         * @param locationIndex
         */
        private void addLocationSection(int areaIndex, int locationIndex) {

            //NOMBRE DEL AREA
            //LOCATIONS:
            //TOTAL DE NÚMEROS
            //  TYPES: ###
            //      STATE: ###
            //
            //

            report.append(three.getArea(areaIndex));
            report.append("\n");
            report.append("Localización: ");
            String location = three.getLocations(areaIndex).get(locationIndex);
            if (location.equals("")) report.append("Vacía");
            else report.append(location);
            report.append("\n");
            report.append("TOTAL DE NÚMEROS: ");
            report.append(three.getLocationNumbersCount(areaIndex, locationIndex));
            report.append("\n");
            addTypeSection(areaIndex, locationIndex, three.EQUIPMENT_INDEX);
            addTypeSection(areaIndex, locationIndex, three.FURNISHING_INDEX);
            addTypeSection(areaIndex, locationIndex, three.UNKNOWN_INDEX);
            report.append("\n");
        }

        /**
         * Add the details af a Type inside a Location
         *
         * @param areaIndex
         * @param locationIndex
         * @param typeIndex
         */
        private void addTypeSection(int areaIndex, int locationIndex, int typeIndex) {
            //  TYPES: ###
            //      STATE: ###
            //
            //

            report.append(TAB);
            report.append(three.getTypeLabel(typeIndex));
            report.append(": ");
            report.append(three.getTypeNumberCount(areaIndex, locationIndex, typeIndex));
            report.append("\n");
            addStateSection(areaIndex, locationIndex, typeIndex, three.PRESENT_INDEX);
            report.append(TAB);
            addStateSection(areaIndex, locationIndex, typeIndex, three.MISSING_INDEX);
            report.append(TAB);
            addStateSection(areaIndex, locationIndex, typeIndex, three.LEFTOVER_INDEX);
            report.append("\n");
            addStateSection(areaIndex, locationIndex, typeIndex, three.IGNORED_MISSING_INDEX);
            report.append(TAB);
            addStateSection(areaIndex, locationIndex, typeIndex, three.LEFTOVER_PRESENT_INDEX);
            report.append("\n");
        }

        /**
         * Add the details of a Type inside an Area, It will ignore the
         * locations clarification.
         *
         * @param areaIndex
         * @param typeIndex
         */
        private void addTypeSection(int areaIndex, int typeIndex) {
            //  TYPE: ###
            //      STATE: ###
            //

            report.append(TAB);
            report.append(three.getTypeLabel(typeIndex));
            report.append(": ");
            report.append(three.getTypeNumberCount(areaIndex, typeIndex));
            report.append("\n");
            addStateSection(areaIndex, typeIndex, three.PRESENT_INDEX);
            report.append(TAB);
            addStateSection(areaIndex, typeIndex, three.MISSING_INDEX);
            report.append(TAB);
            addStateSection(areaIndex, typeIndex, three.LEFTOVER_INDEX);
            report.append("\n");
            addStateSection(areaIndex, typeIndex, three.IGNORED_MISSING_INDEX);
            report.append(TAB);
            addStateSection(areaIndex, typeIndex, three.LEFTOVER_PRESENT_INDEX);
            report.append("\n");
        }


        private void addTypeSection(int typeIndex) {
            //  TYPE: ###
            //      STATES: ###

            report.append(TAB);
            report.append(three.getTypeLabel(typeIndex));
            report.append(": ");
            report.append(three.getTypeNumberCount(typeIndex));
            report.append("\n");
            addStateSection(typeIndex, three.PRESENT_INDEX);
            addStateSection(typeIndex, three.MISSING_INDEX);
            addStateSection(typeIndex, three.IGNORED_MISSING_INDEX);
            addStateSection(typeIndex, three.LEFTOVER_INDEX);
            addStateSection(typeIndex, three.LEFTOVER_PRESENT_INDEX);
        }

        private void addStateSection(int areaIndex, int locationIndex, int typeIndex, int stateIndex) {
            //      STATE: ###

            report.append(TAB);
            report.append(TAB);
            report.append(three.getStateLabel(stateIndex));
            report.append(": ");
            report.append(three.getStateNumbersCount(areaIndex, locationIndex, typeIndex, stateIndex));
        }

        /**
         * Add the details of a State, It will ignore the
         * locations clarification.
         *
         * @param areaIndex
         * @param typeIndex
         * @param stateIndex
         */
        private void addStateSection(int areaIndex, int typeIndex, int stateIndex) {
            //      STATE: ###
            report.append(TAB);
            report.append(TAB);
            report.append(three.getStateLabel(stateIndex));
            report.append(": ");
            report.append(three.getStateNumbersCount(areaIndex, typeIndex, stateIndex));

        }

        private void addStateSection(int typeIndex, int stateIndex) {
            //      STATE: ###

            report.append(TAB);
            report.append(TAB);
            report.append(three.getStateLabel(stateIndex));
            report.append(": ");
            report.append(three.getStateNumbersCountOfAType(typeIndex, stateIndex));
            report.append("\n");
        }

        private abstract class DataThree {

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
            private int numberCount;

            //Constructor
            public DataThree(Cursor data) {
                progress = 0;
                initializeAreasAndLocations(data);
                initializeThree();
                fillThreeWithData(data);
                numberCount = data.getCount();
            }

            //region privateMethods...

            private void fillThreeWithData(Cursor data) {

                String number, numberArea, numberLocation;
                int numberType, numberState;
                data.moveToPosition(-1);
                while (data.moveToNext()) {

                    number = data.getString(DB.IT.Indexes.NUMBER_COLUMN_INDEX);
                    numberArea = data.getString(DB.IT.Indexes.AREA_COLUMN_INDEX);
                    numberLocation = data.getString(DB.IT.Indexes.LOCATION_COLUMN_INDEX);
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

                                                    if (!updateProgress()) return;

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
                        if (!updateProgress()) return;
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
                                if (!updateProgress()) return;
                            }
                        }
                    }

                    this.locations.add(currentAreaLocations);
                }


            }

            private boolean updateProgress() {

                progress++;
                return onThreeProgressUpdate(progress);
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

            public abstract boolean onThreeProgressUpdate(long progress);

            /**
             * @param area
             * @return The total numbers contains in the given Area
             */
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

            /**
             * @param type
             * @return the count of numbers with this type
             */
            public int getTypeNumberCount(int type) {

                int numberCount = 0;
                for (int a = 0; a < areas.size(); a++) {
                    for (int l = 0; l < locations.get(a).size(); l++) {
                        numberCount += getTypeNumberCount(a, l, type);
                    }
                }

                return numberCount;
            }

            /**
             * @param typeIndex
             * @param areaIndex
             * @return the count of numbers with this type in a given area
             */
            public int getTypeNumberCount(int areaIndex, int typeIndex) {

                int numberCount = 0;
                for (int l = 0; l < locations.get(areaIndex).size(); l++) {
                    numberCount += getTypeNumberCount(areaIndex, l, typeIndex);
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

            /**
             * @param area
             * @param location
             * @param type
             * @return the count of numbers with this type in this location
             */
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

            public int getStateNumbersCount(int state) {
                int numberCount = 0;
                for (int a = 0; a < areas.size(); a++) {
                    for (int l = 0; l < locations.get(a).size(); l++) {
                        for (int t = 0; t < TYPE_COUNT; t++) {
                            numberCount += getStateNumbersCount(a, l, t, state);
                        }
                    }
                }
                return numberCount;
            }

//        /**
//         * @param area
//         * @param state
//         * @return the count of numbers with this state in an Area
//         */
//        public int getStateNumbersCount(int area, int state) {
//            int numberCount = 0;
//            for (int l = 0; l < locations.get(area).size(); l++) {
//                for (int t = 0; t < TYPE_COUNT; t++)
//                    numberCount += getStateNumbersCount(area, l, t, state);
//            }
//            return numberCount;
//        }

            /**
             * @param type
             * @param state
             * @return get the count Number of a given stape of a type in all the three.
             */
            public int getStateNumbersCountOfAType(int type, int state) {
                int numberCount = 0;
                for (int a = 0; a < areas.size(); a++) {
                    for (int l = 0; l < locations.get(a).size(); l++)
                        numberCount += getStateNumbersCount(a, l, type, state);
                }

                return numberCount;
            }

            public int getStateNumbersCount(int area, int location, int type, int state) {
                return getBranch(area, location, type, state).size();
            }

            /**
             * @param area
             * @param type
             * @param state
             * @return the Count of numbers with this type and this state in an Area.
             */
            public int getStateNumbersCount(int area, int type, int state) {
                int numbersCount = 0;
                for (int l = 0; l < locations.get(area).size(); l++) {
                    numbersCount += getBranch(area, l, type, state).size();
                }
                return numbersCount;
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

            public ArrayList<String> getStateNumbers(int type, int state) {
                ArrayList<String> numbers = new ArrayList<>();
                for (int a = 0; a < areas.size(); a++) {
                    for (int l = 0; l < locations.get(a).size(); l++) {
                        for (String number : getStateNumbers(a, l, type, state)) {
                            numbers.add(number);
                        }
                    }
                }
                return numbers;
            }

            /**
             * @return the numbers of Areas in the three
             */
            public int getAreasCount() {
                return areas.size();
            }

            public int getLocationsCount(int area) {
                return locations.get(area).size();
            }

            public String getArea(int area) {
                return areas.get(area);
            }

            public ArrayList<String> getLocations(int area) {
                return locations.get(area);
            }

            public int getNumberCount() {
                return numberCount;
            }

            /**
             * @param type
             * @return the type as a String
             */
            public String getTypeLabel(int type) {
                if (type == EQUIPMENT_INDEX) {
                    return "EQUIPOS";
                } else if (type == FURNISHING_INDEX) {
                    return "MUEBLES";
                } else return "CON TIPO DESCONOCIDO";
            }

            /**
             * @param state
             * @return the state as a String
             */
            public String getStateLabel(int state) {
                if (state == MISSING_INDEX) {
                    return "Faltantes";
                } else if (state == IGNORED_MISSING_INDEX) {
                    return "Faltantes Ignorados";
                } else if (state == LEFTOVER_INDEX) {
                    return "Sobrantes";
                } else if (state == LEFTOVER_PRESENT_INDEX) {
                    return "Sobrantes Presentes";
                } else return "Presentes";
            }

            //endregion publicMethods...

        }
    }

    private class UpdateBigStatics extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            Area.updateAreas();
            Location.updateLocations();
            Observation.updateObservations();
            Description.descriptions = new String[]{""};
            return null;
        }
    }

}
