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

import static com.example.compereirowww.inventory20181.Activities.AppStatics.APP_FILE_NAME;
import static com.example.compereirowww.inventory20181.Activities.AppStatics.APP_QRS_FILE_NAME;
import static com.example.compereirowww.inventory20181.Activities.AppStatics.APP_SAVE_FILE_NAME;
import static com.example.compereirowww.inventory20181.Activities.AppStatics.APP_TO_IMPORT_FILE_NAME;
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

        //Debug
        Log.d("JOSE2", "MainActivity.onCreate +++++++++++++++++++++++++++++++++++++++++++++++++++++");

        //GUI
        textView = (TextView) findViewById(R.id.text_tv);

        //DB
        db = new DB(getApplicationContext());

        //AppStatics
        Area.updateAreas();
        Location.updateLocations();
        Observation.updateObservations();
        AreasToFollow.updateAreasToFollow();
        Description.descriptions = new String[]{""};

    }

    private void callInventoryActivity() {
        startActivity(new Intent(MainActivity.this, InventoryActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();

        //TODO Deb
        Log.d("JOSE", "MainActivity.onResume +++++++++++++++++++++++++++++++++++++++++++++++++++++");

        if (!handleAppFiles()) return;
        if (!checkingImportation()) return;
        if (!checkingAreaToFollow()) return;

        db.updatePresentToMissingIfOutOfDate(Integer.parseInt(db.getPreference(PT.PNames.UPDATE_CRITERIA)));
        new MakeReportAT().execute();

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
        File toImportFile = new File(appFile, APP_TO_IMPORT_FILE_NAME);
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
        File toSaveFile = new File(appFile, APP_SAVE_FILE_NAME);
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
        db.setPreference(DB.PT.PNames.REPORTS_DIRECTORY_PATH, toImportFile.getPath());
        db.setPreference(DB.PT.PNames.BACKUPS_DIRECTORY_PATH, toSaveFile.getPath());
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
        //area
        if (Arrays.equals(AreasToFollow.areasToFollow, new String[]{""})) {
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
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
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
        startActivity(new Intent(MainActivity.this, ConfigurationActivity.class));
    }

    private void callSearchActivity() {
        db.setPreference(PT.PNames.TEMP_SEARCH_CRITERIA, PT.PDefaultValues.EMPTY_PREFERENCE);
        startActivity(new Intent(MainActivity.this, SearchActivity.class));
    }

    private class MakeReportAT extends AsyncTask<Void, Void, String> {

        ArrayList<String> areasWithFollowedNumbers;
        ArrayList<Cursor> numbersInAreas;

        @Override
        protected String doInBackground(Void... voids) {

            Cursor data = db.getAllDataIfFollowing(IT.FollowingValues.YES);
            StringBuilder report = new StringBuilder();
            report.append("Reporte ").append(Tools.getFormattedDate()).
                    append("\n").
                    append("\n").
                    append("DETALLES GENERALES").
                    append("\n").
                    append("Números (Total): ").append(db.getNumberCount()).
                    append("\n").
                    append("Números en seguimiento: ").append(data.getCount()).append("\n").
                    append("    Faltantes: ").
                    append(getFollowingIfState(IT.StateValues.MISSING)).
                    append("\n").
                    append("    Presentes: ").
                    append(getFollowingIfState(IT.StateValues.PRESENT)).
                    append("\n").
                    append("    Sobrantes: ").
                    append(getFollowingIfState(IT.StateValues.LEFTOVER)).
                    append("\n").
                    append("    Faltantes Ignorados: ").
                    append(getFollowingIfState(IT.StateValues.IGNORED_MISSING)).
                    append("\n").
                    append("    Sobrantes Presentes: ").
                    append(getFollowingIfState(IT.StateValues.LEFTOVER_PRESENT)).
                    append("\n").
                    append("\n").
                    append("DETALLES POR ÁREA").
                    append("\n");

            fillAreasWithFollowedNumbers(data);
            fillNumbersInAreas();

            for (int i = 0; i < areasWithFollowedNumbers.size(); i++) {
                report.append(i + 1).
                        append(". ").
                        append(areasWithFollowedNumbers.get(i)).
                        append("\n").
                        append("Números (total): ").
                        append(getNumbersIfArea(areasWithFollowedNumbers.get(i))).
                        append("\n").
                        append("Números en seguimiento: ").
                        append(numbersInAreas.get(i).getCount()).
                        append("\n").
                        append("    Faltantes: ").
                        append(getNumbersWithState(IT.StateValues.MISSING, numbersInAreas.get(i))).
                        append("\n").
                        append("    Presentes: ").
                        append(getNumbersWithState(IT.StateValues.PRESENT, numbersInAreas.get(i))).
                        append("\n").
                        append("    Sobrantes: ").
                        append(getNumbersWithState(IT.StateValues.LEFTOVER, numbersInAreas.get(i))).
                        append("\n").
                        append("    Faltantes Ignorados: ").
                        append(getNumbersWithState(IT.StateValues.IGNORED_MISSING, numbersInAreas.get(i))).
                        append("\n").
                        append("    Sobrantes Presentes: ").
                        append(getNumbersWithState(IT.StateValues.LEFTOVER_PRESENT, numbersInAreas.get(i))).
                        append("\n").
                        append("");

            }

            data.close();
            return report.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            textView.setText(s);
        }

        private int getFollowingIfState(int state) {
            return db.getAllDataIfFollowingAndState(IT.FollowingValues.YES,
                    IT.StateValues.toString(state)).getCount();
        }

        private int getNumbersIfArea(String area) {
            return db.getAllDataIfArea(area).getCount();
        }

        private int getNumbersWithState(int state, Cursor data) {
            data.moveToPosition(-1);
            int c = 0;
            while (data.moveToNext()) {
                int s = data.getInt(IT.Indexes.STATE_COLUMN_INDEX);
                if (state == s) {
                    c++;
                }
            }
            return c;
        }

        private void fillAreasWithFollowedNumbers(Cursor data) {
            areasWithFollowedNumbers = new ArrayList<>();
            while (data.moveToNext()) {
                String area = data.getString(IT.Indexes.AREA_COLUMN_INDEX);
                if (!areasWithFollowedNumbers.contains(area)) {
                    areasWithFollowedNumbers.add(area);
                }
            }
        }

        private void fillNumbersInAreas() {
            numbersInAreas = new ArrayList<>();
            for (String area : areasWithFollowedNumbers) {
                numbersInAreas.add(db.getAllDataIfFollowingAndArea(IT.FollowingValues.YES, area));
            }
        }

    }

}
