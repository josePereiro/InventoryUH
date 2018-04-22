package com.example.compereirowww.inventory20181.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.compereirowww.inventory20181.DataBase.DB.IT;
import com.example.compereirowww.inventory20181.DataBase.DB.PT.PDefaultValues;
import com.example.compereirowww.inventory20181.DataBase.DB.PT.PNames;
import com.example.compereirowww.inventory20181.R;
import com.example.compereirowww.inventory20181.Tools.Tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static com.example.compereirowww.inventory20181.Activities.AppStatics.APP_TAG;
import static com.example.compereirowww.inventory20181.Activities.AppStatics.AreasToFollow;
import static com.example.compereirowww.inventory20181.Activities.AppStatics.BackupInventoryFile.INVENTORY_BACKUP_FILE_HEAD_CODE;
import static com.example.compereirowww.inventory20181.Activities.AppStatics.UHInventoryFile.ALTA_DATE_INDEX;
import static com.example.compereirowww.inventory20181.Activities.AppStatics.UHInventoryFile.AREA_INDEX;
import static com.example.compereirowww.inventory20181.Activities.AppStatics.UHInventoryFile.CSV_FIRST_DATA_LINE_INDEX;
import static com.example.compereirowww.inventory20181.Activities.AppStatics.UHInventoryFile.DESCRIPTION_INDEX;
import static com.example.compereirowww.inventory20181.Activities.AppStatics.UHInventoryFile.NUMBER_INDEX;
import static com.example.compereirowww.inventory20181.Activities.AppStatics.UHInventoryFile.OFFICIAL_UPDATE_INDEX;
import static com.example.compereirowww.inventory20181.Activities.AppStatics.UHInventoryFile.UH_INVENTORY_FILE_HEAD_CODE;
import static com.example.compereirowww.inventory20181.Activities.AppStatics.db;

public class ImportActivity extends AppCompatActivity {

    //GUI
    TextView detailTV;
    Spinner spinner;
    FloatingActionButton fab;

    //UHInventoryFile Fields
    private AsyncTask<Void, String, Boolean> importAT;
    private String selectedFile;
    protected static ArrayList<File> CSVFiles;
    private ArrayList<String> CSVData;

    //File hash code 1337277359

    //Activity Life cycle methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);

        //TODO deb
        Log.d(APP_TAG, "ImportActivity.onCreate");

        //GUI
        detailTV = (TextView) findViewById(R.id.detail_tv);
        spinner = (Spinner) findViewById(R.id.spinner);
        fab = (FloatingActionButton) findViewById(R.id.fab);

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isAppImporting()) {

            checkAndImport();
            settingUpFabIfImporting();
            settingUpSpinnerIfImporting();

        } else {

            settingUpSpinnerIfNotImporting();
            settingUpFabIfNotImporting();

        }

    }

    @Override
    protected void onStop() {
        super.onStop();

        //stop asyncTask
        if (importAT != null) {
            importAT.cancel(true);
            importAT = null;
        }

    }

    @Override
    public void onBackPressed() {
        if (isAppImporting())
            Tools.showDialogMessage(ImportActivity.this, getString(R.string.text7), "Sí",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Tools.showToast(ImportActivity.this, "El proceso de imporatción se ha cancelado!",
                                    false);
                            db.setPreference(PNames.CURRENT_FILE_TO_IMPORT, PDefaultValues.EMPTY_PREFERENCE);
                            startActivity(new Intent(ImportActivity.this, ImportActivity.class));
                            finish();
                        }
                    }, "No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
        else super.onBackPressed();
    }

    private boolean performAllChecking(File fileToImport, boolean corruptDB) {
        if (!checkIfFileToImportExist(fileToImport, corruptDB)) {
            Log.d(APP_TAG, "checkIfFileToImportExist fail");
            return false;
        }
        if (!readCSVDataFromFile(fileToImport, corruptDB)) {
            Log.d(APP_TAG, "readCSVDataFromFile fail");
            return false;
        }
        if (!checkCSVDataHead(corruptDB)) {
            Log.d(APP_TAG, "readCSVDataFromFile fail");
            return false;
        }
        if (!checkAppHashCodeAndCSVFileHashCode(corruptDB)) {
            Log.d(APP_TAG, "checkAppHashCodeAndCSVFileHashCode fail");
            return false;
        }
        return true;
    }

    private boolean checkIfFileToImportExist(File fileToImport, final boolean corruptDB) {

        if (fileToImport.exists())
            return true;
        else {
            Tools.showDialogMessage(
                    ImportActivity.this,
                    getString(R.string.error9) + " " +
                            getString(R.string.text3) +
                            getCurrentFileToImport(),
                    "Cerrar",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ImportActivity.this.finish();
                            System.exit(0);
                        }
                    },
                    "Reiniciar",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            cancelImportation(corruptDB);
                        }
                    });
            return false;
        }

    }

    private boolean checkCSVDataHead(final boolean corruptDB) {
        try {
            String head = CSVData.get(0).split(",", -1)[0];
            if (head.equals(UH_INVENTORY_FILE_HEAD_CODE) || head.equals(INVENTORY_BACKUP_FILE_HEAD_CODE))
                return true;
            throw new Exception();
        } catch (Exception e) {
            Tools.showInfoDialog(ImportActivity.this, getString(R.string.error5), "Aceptar",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            cancelImportation(corruptDB);
                        }
                    });
            return false;
        }
    }

    private boolean checkAppHashCodeAndCSVFileHashCode(final boolean corruptDB) {

        try {
            String fileHash = CSVData.get(0).split(",", -1)[1];
            String appHash = db.getPreference(PNames.CURRENT_DATA_TO_IMPORT_HASH);
            String dataHash = String.valueOf(getCSVDataHashCode());

            if (!fileHash.equals(dataHash)) throw new Exception();
            if (!fileHash.equals(appHash)) throw new Exception();
            return true;

        } catch (Exception e) {
            Tools.showInfoDialog(ImportActivity.this, getString(R.string.error5), "Aceptar",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            cancelImportation(corruptDB);
                        }
                    });
            return false;
        }

    }

    private boolean checkCSVFileHash(final boolean corruptDB) {

        try {
            String fileHash = CSVData.get(0).split(",", -1)[1];
            String dataHash = String.valueOf(getCSVDataHashCode());

            if (!fileHash.equals(dataHash)) throw new Exception();
            return true;

        } catch (Exception e) {
            Tools.showInfoDialog(ImportActivity.this, getString(R.string.error5), "Aceptar",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            cancelImportation(corruptDB);
                        }
                    });
            return false;
        }

    }

    private boolean isAppImporting() {
        return !db.getPreference(PNames.CURRENT_FILE_TO_IMPORT).equals(PDefaultValues.EMPTY_PREFERENCE);
    }

    private void settingUpSpinnerIfImporting() {

        File file = new File(getCurrentFileToImport());
        spinner.setAdapter(new ArrayAdapter<>(ImportActivity.this,
                android.R.layout.simple_list_item_1,
                new String[]{file.getName()}));
        spinner.setClickable(false);
        spinner.setEnabled(false);

    }

    private void settingUpFabIfImporting() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void checkAndImport() {

        if (!performAllChecking(new File(getCurrentFileToImport()), true)) return;

        if (getCurrentFileToImportHead().equals(UH_INVENTORY_FILE_HEAD_CODE)) {
            importAT = new ImportUHInventoryAT(getCurrentImportationIndex(), CSVData, detailTV);
            importAT.execute();
        } else if (getCurrentFileToImportHead().equals(INVENTORY_BACKUP_FILE_HEAD_CODE)) {
            importAT = new ImportBackUpAT(getCurrentImportationIndex(), CSVData, detailTV);
            importAT.execute();
        }

    }

    private void cancelImportation(boolean corruptDB) {

        Tools.showToast(ImportActivity.this, "El proceso de imporatción se ha cancelado!", false);
        db.setPreference(PNames.CURRENT_FILE_TO_IMPORT, PDefaultValues.EMPTY_PREFERENCE);
        if (!getCurrentFileToImportHead().equals(INVENTORY_BACKUP_FILE_HEAD_CODE)) {
            if (corruptDB)
                db.setPreference(PNames.DB_STATE, PDefaultValues.DB_CORRUPTED);
            db.setPreference(PNames.LAST_IMPORTED_UH_DATA_HASH, PDefaultValues.EMPTY_PREFERENCE);
        } else {
            db.setPreference(PNames.LAST_IMPORTED_UH_DATA_HASH, PDefaultValues.EMPTY_PREFERENCE);
        }
        startActivity(new Intent(ImportActivity.this, ImportActivity.class));
        finish();

    }

    private void settingUpFabIfNotImporting() {

        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (selectedFile == null || selectedFile.equals("")) {
                    Tools.showToast(ImportActivity.this, "Nada que importar!", false);
                    return;
                }

                Tools.showDialogMessage(ImportActivity.this, "Esta por comenzar una importación!!!",
                        "Comenzar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                File file = new File(selectedFile);
                                if (!checkIfFileToImportExist(file, false)) {
                                    Log.d(APP_TAG, "checkIfFileToImportExist fail");
                                    return;
                                }
                                if (!readCSVDataFromFile(file, false)) {
                                    Log.d(APP_TAG, "readCSVDataFromFile fail");
                                    return;
                                }
                                if (!checkCSVDataHead(false)) {
                                    Log.d(APP_TAG, "readCSVDataFromFile fail");
                                    return;
                                }
                                if (!checkCSVFileHash(false)) {
                                    Log.d(APP_TAG, "checkCSVFileHash fail");
                                    return;
                                }

                                prepareForImportation();

                                startActivity(new Intent(ImportActivity.this, ImportActivity.class));
                                finish();

                            }
                        }, "Cancelar", null);
            }
        });

    }

    private void prepareForImportation() {

        File fileToImport = new File(selectedFile);
        if (!readCSVDataFromFile(fileToImport, false)) return;
        setUpPreferencesToStartImportation(fileToImport);

    }

    private void setUpPreferencesToStartImportation(File fileToImport) {

        db.setPreference(PNames.CURRENT_FILE_TO_IMPORT, fileToImport.getPath());
        db.setPreference(PNames.CURRENT_FILE_TO_IMPORT_HEAD, CSVData.get(0).split(",", -1)[0]);
        db.setPreference(PNames.CURRENT_DATA_TO_IMPORT_HASH, CSVData.get(0).split(",", -1)[1]);
        db.setPreference(PNames.CURRENT_IMPORTATION_INDEX, CSV_FIRST_DATA_LINE_INDEX);

    }

    private void settingUpSpinnerIfNotImporting() {

        //Files to import
        if (CSVFiles != null) {
            spinner.setAdapter(new ArrayAdapter<>(ImportActivity.this,
                    android.R.layout.simple_list_item_1,
                    toStringArrayList(CSVFiles)));
        } else {
            startLoadingCSVFilesThread();
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i < CSVFiles.size()) {
                    selectedFile = CSVFiles.get(i).getPath();
                    new ShowPreviewAT().execute();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void startLoadingCSVFilesThread() {

        Tools.showToast(ImportActivity.this, "Buscando archivos para importar!!", false);
        CSVFiles = new ArrayList<>();
        new ListCSVFilesInTheDeviseAT().execute();
    }

    private int getCSVDataHashCode() {

        StringBuilder stringBuilder = new StringBuilder();
        for (int startIndex = CSV_FIRST_DATA_LINE_INDEX;
             startIndex < CSVData.size(); startIndex++) {
            stringBuilder.append(CSVData.get(startIndex));
        }

        return Tools.myStringHashCode(stringBuilder.toString());
    }

    private ArrayList<String> toStringArrayList(ArrayList<File> files) {
        ArrayList<String> toReturn = new ArrayList<>();
        for (File f : files) {
            toReturn.add(f.getName());
        }
        return toReturn;
    }

    private boolean readCSVDataFromFile(File fileToImport, final boolean corruptDB) {
        try {
            CSVData = Tools.readFile(fileToImport);
            return true;
        } catch (IOException e) {
            Tools.showInfoDialog(ImportActivity.this, getString(R.string.error3), "Aceptar",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            cancelImportation(corruptDB);
                        }
                    });
            return false;
        }
    }

    private String getCurrentFileToImport() {
        return db.getPreference(PNames.CURRENT_FILE_TO_IMPORT);
    }

    private String getCurrentFileToImportHead() {
        return db.getPreference(PNames.CURRENT_FILE_TO_IMPORT_HEAD);
    }

    private int getCurrentImportationIndex() {
        return Integer.parseInt(db.getPreference(PNames.CURRENT_IMPORTATION_INDEX));
    }

    private class ImportUHInventoryAT extends AsyncTask<Void, String, Boolean> {

        //import asyncTask
        //GUI
        private final TextView textView;
        private int index;
        ArrayList<String> CSVData;

        public ImportUHInventoryAT(int startIndex, ArrayList<String> CSVData, TextView textView) {

            this.textView = textView;
            this.CSVData = CSVData;
            index = startIndex;

            //TODO deb
            Log.d(APP_TAG, "ImportUHInventoryAT created start index " + index);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            //TODO deb
            Log.d(APP_TAG, "ImportUHInventoryAT doInBackground");


            //everybody to LEFTOVER
            if (index == CSV_FIRST_DATA_LINE_INDEX) {
                db.updateStateColumn(IT.StateValues.LEFTOVER);
                db.updateLastCheckingColumn(Tools.getDate());
                //TODO deb
                Log.d(APP_TAG, "state column to LEFTOVER");
            }

            //Importing
            String number, description, area, altaDate, officialUpdate;
            int numberCount = CSVData.size() - CSV_FIRST_DATA_LINE_INDEX;
            String[] line;
            String action;
            for (; index < CSVData.size(); index++) {

                if (isCancelled()) {
                    return false;
                }

                //getting Data
                line = CSVData.get(index).split(",", -1);

                number = line[NUMBER_INDEX];
                description = line[DESCRIPTION_INDEX];
                area = line[AREA_INDEX];
                altaDate = line[ALTA_DATE_INDEX];
                officialUpdate = line[OFFICIAL_UPDATE_INDEX];


                if (db.updateOfficialDataAndState(
                        number,
                        description,
                        area,
                        altaDate,
                        officialUpdate,
                        IT.StateValues.MISSING) != 0) {

                    action = "(Actualizando Número)";

                } else {

                    db.insertNewNumber(number,
                            description,
                            area,
                            altaDate,
                            officialUpdate,
                            IT.FollowingValues.NO,
                            IT.StateValues.MISSING,
                            Tools.getDate(),
                            IT.TypeValues.UNKNOWN, "", "");

                    action = "(Nuevo número)";

                }

                //Updating Preferences
                db.setPreference(PNames.CURRENT_IMPORTATION_INDEX, index);

                //Publishing
                publishProgress("Importando archivo inventario UH..." + "\n" +
                        index + "/" + numberCount + " --> " + getPerCent(index, numberCount) + "\n" +
                        action + "\n" +
                        "Numero: " + number + "\n" +
                        "Descripción: " + description + "\n" +
                        "Area: " + area);

            }


            return true;
        }

        @Override
        protected void onProgressUpdate(String... values) {

            textView.setText(values[0]);
        }

        @Override
        protected void onPostExecute(Boolean b) {

            if (b) {

                textView.setText(R.string.text11);
                Tools.showInfoDialog(ImportActivity.this, getString(R.string.text10),
                        "Terminar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                terminateImportation();
                                startActivity(new Intent(ImportActivity.this, MainActivity.class));
                                finish();
                            }
                        });

            } else {
                db.setPreference(PNames.CURRENT_IMPORTATION_INDEX, index);
            }
            Log.d(APP_TAG, "Async Task onPostExecute, result " + b);
        }

        private String getPerCent(int part, int total) {
            return String.valueOf((part * 100) / total) + " %";
        }

        private void terminateImportation() {
            db.setPreference(PNames.DB_STATE, PDefaultValues.DB_OK);
            db.setPreference(PNames.LAST_IMPORTED_UH_DATA_HASH,
                    db.getPreference(PNames.CURRENT_DATA_TO_IMPORT_HASH));
            db.setPreference(PNames.CURRENT_FILE_TO_IMPORT, PDefaultValues.EMPTY_PREFERENCE);
            db.setPreference(PNames.AREAS_TO_FOLLOW_CSV, PDefaultValues.EMPTY_PREFERENCE);
            AreasToFollow.updateAreasToFollow();
        }

    }

    private class ImportBackUpAT extends AsyncTask<Void, String, Boolean> {

        //import asyncTask
        //GUI
        private final TextView textView;
        private int index;
        ArrayList<String> CSVData;

        public ImportBackUpAT(int startIndex, ArrayList<String> CSVData, TextView textView) {

            this.textView = textView;
            this.CSVData = CSVData;
            index = startIndex;

            //TODO deb
            Log.d(APP_TAG, "ImportBackUpAT created start index " + index);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            //TODO deb
            Log.d(APP_TAG, "ImportBackUpAT doInBackground");

            //Importing
            String number, location, observation;
            int numberCount = CSVData.size() - CSV_FIRST_DATA_LINE_INDEX;
            int type;
            String[] line;
            for (; index < CSVData.size(); index++) {

                if (isCancelled()) {
                    return false;
                }

                line = CSVData.get(index).split(",", -1);

                number = line[NUMBER_INDEX];
                location = line[AppStatics.BackupInventoryFile.LOCATION_INDEX];
                type = Integer.parseInt(line[AppStatics.BackupInventoryFile.TYPE_INDEX]);
                observation = line[AppStatics.BackupInventoryFile.OBSERVATION_INDEX];

                if (db.updateNonOfficialData(number, location, type, observation) != 0) {

                    publishProgress("Importando archivo de respaldo..." + "\n" +
                            index + "/" + numberCount + " --> " + getPerCent(index, numberCount) + "\n" +
                            "(Actualizando número)" + "\n" +
                            "Numero: " + number + "\n" +
                            "Localización: " + location + "\n" +
                            "Type: " + IT.TypeValues.toString(type) + "\n" +
                            "Observación: " + observation);
                } else {

                    publishProgress("Importando archivo de respaldo..." + "\n" +
                            index + "/" + numberCount + " --> " + getPerCent(index, numberCount) + "\n" +
                            "(número ausente)" + "\n" +
                            "Numero: " + number + "\n" +
                            "Localización: " + location + "\n" +
                            "Type: " + IT.TypeValues.toString(type) + "\n" +
                            "Observación: " + observation);
                }

                //Updating Preferences
                db.setPreference(PNames.CURRENT_IMPORTATION_INDEX, index);

            }

            return true;
        }

        @Override
        protected void onProgressUpdate(String... values) {

            textView.setText(values[0]);
        }

        @Override
        protected void onPostExecute(Boolean b) {

            if (b) {

                textView.setText(R.string.text11);
                Tools.showInfoDialog(ImportActivity.this, getString(R.string.text10),
                        "Terminar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                terminateImportation();
                                startActivity(new Intent(ImportActivity.this, MainActivity.class));
                                finish();
                            }
                        });

            } else {
                db.setPreference(PNames.CURRENT_IMPORTATION_INDEX, index);
            }


            Log.d(APP_TAG, "Async Task onPostExecute, result " + b);


        }

        private String getPerCent(int part, int total) {
            return String.valueOf((part * 100) / total) + " %";
        }

        private void terminateImportation() {

            db.setPreference(PNames.LAST_IMPORTED_BACKUP_DATA_HASH,
                    db.getPreference(PNames.CURRENT_DATA_TO_IMPORT_HASH));
            db.setPreference(PNames.CURRENT_FILE_TO_IMPORT, PDefaultValues.EMPTY_PREFERENCE);
        }
    }

    private class ListCSVFilesInTheDeviseAT extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {

            CSVFiles.clear();
            ArrayList<File> anyCSVFile = new ArrayList<>();
            Tools.listAllFilesAndSubFiles(Environment.getExternalStorageDirectory(), ".csv", anyCSVFile);
            for (File f : anyCSVFile) {
                try {
                    String line = Tools.readFileFirstLine(f);
                    if (checkHeadLine(line)) {
                        CSVFiles.add(f);
                    }

                } catch (IOException e) {
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            settingUpSpinnerIfNotImporting();
            Tools.showToast(ImportActivity.this, "Búsqueda finalizada, encontrados " +
                    CSVFiles.size() + " posibles arhivos .csv con datos de inventario!", true);

        }

        private boolean checkHeadLine(String line) {
            if (line.contains(UH_INVENTORY_FILE_HEAD_CODE)
                    || line.contains(INVENTORY_BACKUP_FILE_HEAD_CODE))
                return true;
            else return false;
        }

    }

    private class ShowPreviewAT extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {

            File file = new File(selectedFile);
            String ms = "Error leyendo el archivo!!!";
            try {
                ms = file.getName();
                if (!readCSVDataFromFile(file)) throw new Exception();
                if (!checkCSVDataHead()) {
                    ms = "Este archivo no es compatible con la aplicación!!!";
                    throw new Exception();
                }
                if (!checkCSVFileHashCode()) {
                    ms = "Este archivo está corrupto!!!";
                    throw new Exception();
                }

                String fileDataHash = String.valueOf(getCSVDataHashCode());
                String lastImportedHash;

                String fileHead = CSVData.get(0).split(",", -1)[0];
                if (fileHead.equals(UH_INVENTORY_FILE_HEAD_CODE)) {

                    ms = "Archivo UH de inventario";
                    lastImportedHash = AppStatics.db.getPreference(PNames.LAST_IMPORTED_UH_DATA_HASH);

                } else {

                    ms = "Archivo de respaldo del inventario";
                    lastImportedHash = AppStatics.db.getPreference(PNames.LAST_IMPORTED_BACKUP_DATA_HASH);
                }

                ms += "\n";
                if (lastImportedHash.equals(fileDataHash)) {
                    ms += "Nota: Este archivo contiene datos idénticos a los últimos importados!!! ";
                } else {
                    ms += "Nota: Contiene datos diferentes a los importados la última vez!!";
                }
                ms += "\n";
                ms += "Números contenidos: " + (CSVData.size() - 1);

                return ms;
            } catch (Exception e) {
                return ms;
            }
        }

        @Override
        protected void onPostExecute(String preview) {
            detailTV.setText(preview);
        }

        private boolean checkCSVDataHead() {
            try {
                String head = CSVData.get(0).split(",", -1)[0];
                if (head.equals(UH_INVENTORY_FILE_HEAD_CODE) ||
                        head.equals(INVENTORY_BACKUP_FILE_HEAD_CODE))
                    return true;
                else throw new Exception();
            } catch (Exception e) {
                return false;
            }
        }

        private boolean checkCSVFileHashCode() {

            try {
                String fileHash = CSVData.get(0).split(",", -1)[1];
                String appHash = db.getPreference(PNames.CURRENT_DATA_TO_IMPORT_HASH);
                String dataHash = String.valueOf(getCSVDataHashCode());

                if (!fileHash.equals(dataHash)) throw new Exception();
                return true;

            } catch (Exception e) {
                return false;
            }

        }

        private boolean readCSVDataFromFile(File fileToImport) {
            try {
                CSVData = Tools.readFile(fileToImport);
                return true;
            } catch (IOException e) {
                return false;
            }
        }
    }

}