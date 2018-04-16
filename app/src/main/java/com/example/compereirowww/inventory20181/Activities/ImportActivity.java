package com.example.compereirowww.inventory20181.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.compereirowww.inventory20181.DataBase.DB;
import com.example.compereirowww.inventory20181.DataBase.DB.IT;
import com.example.compereirowww.inventory20181.DataBase.DB.PT.PDefaultValues;
import com.example.compereirowww.inventory20181.DataBase.DB.PT.PNames;
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

public class ImportActivity extends AppCompatActivity {

    //DEB
    static final String ACT_TAG_ = "ImportActivity ";
    static final String ON_CREATE_TAG_ = "OnCreate ";
    static final String ON_RESUME_TAG_ = "OnResume ";
    static final String ON_PAUSE_TAG_ = "OnPause ";
    static final String SEPARATOR_TAG = "+++++++++++++++++++++++++++++++++++++++++++++";

    //GUI
    TextView detailTV;
    Spinner spinner;
    FloatingActionButton fab;


    //Importation Fields
    private AsyncTask<Void, String, Boolean> importAT;
    private String selectedFile;
    private ArrayList<String> data;

    //DB
    private DB db;

    //Activity Life cycle methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);

        //TODO deb
        Log.d(AppStatics.APP_TAG, "ImportActivity.onCreate");

        //DB
        db = AppStatics.db;

        //GUI
        detailTV = (TextView) findViewById(R.id.detail_tv);
        spinner = (Spinner) findViewById(R.id.spinner);
        fab = (FloatingActionButton) findViewById(R.id.fab);


    }

    @Override
    protected void onResume() {
        super.onResume();


        //TODO deb
        Log.d(AppStatics.APP_TAG, ACT_TAG_ + ON_RESUME_TAG_ + SEPARATOR_TAG);
        Log.d(AppStatics.APP_TAG, "Handling Importation");


        if (db.getPreference(PNames.APP_STATE).equals(PDefaultValues.IMPORTING)) {

            //region if AppImporting == IMPORTING

            //TODO deb
            Log.d(AppStatics.APP_TAG, "If AppImporting = Importing");

            //Import File
            File importingFile = new File(db.getPreference(PNames.CURRENT_IMPORTING_FILE_PATH));
            if (importingFile.exists()) {

                //region if importingFile.exists() == true

                //TODO deb
                Log.d(AppStatics.APP_TAG, "If ImportFile exist");

                //region Reading CSVData
                try {
                    data = Tools.readFile(importingFile);

                    //TODO deb
                    Log.d(AppStatics.APP_TAG, "ImportFile CSVData reading succeed");

                } catch (IOException e) {

                    //TODO deb
                    Log.d(AppStatics.APP_TAG, "Error reading importFile CSVData");

                    Tools.showInfoDialog(ImportActivity.this, getString(R.string.error3), "Aceptar",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Tools.showToast(ImportActivity.this, "El proceso de imporatción se ha cancelado!",
                                            false);
                                    db.setPreference(PNames.APP_STATE, PDefaultValues.IMPORTATION_CANCELLED);
                                    startActivity(new Intent(ImportActivity.this, ImportActivity.class));
                                    finish();
                                }
                            });
                }
                //endregion

                //region Checking HashCode
                if (String.valueOf(getDataHashCode(data, AppStatics.Importation.CSV_FIRST_DATA_LINE_INDEX)).
                        equals(db.getPreference(PNames.CURRENT_IMPORTATION_FILE_HASH))) {

                    //TODO deb
                    Log.d(AppStatics.APP_TAG, "Hash Code test succeed");

                    //Start Index
                    int startIndex = Integer.
                            parseInt(db.getPreference(PNames.CURRENT_IMPORTATION_INDEX));

                    //Starting Importing
                    if (data.get(0).split(",", -1)[0].equals(AppStatics.
                            UH_INVENTORY_FILE_HEAD_CODE)) {

                        db.setPreference(PNames.DB_STATE, PDefaultValues.DB_CORRUPTED);

                        //TODO deb
                        Log.d(AppStatics.APP_TAG, "Importing an UH inventory file, start index " + startIndex);

                        importAT = new ImportUHInventoryAT(startIndex, data, detailTV);
                        importAT.execute();

                    } else if (data.get(0).split(",", -1)[0].equals(AppStatics.
                            SALVA_INVENTORY_FILE_HEAD_CODE)) {

                        //TODO deb
                        Log.d(AppStatics.APP_TAG, "Importing a salva file, start index " + startIndex);

                        importAT = new ImportSaveAT(startIndex, data, detailTV);
                        importAT.execute();

                    } else {

                        //TODO deb
                        Log.d(AppStatics.APP_TAG, "Corrupted Head, precess cancelled");

                        Tools.showInfoDialog(ImportActivity.this, getString(R.string.error6),
                                "Aceptar",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Tools.showToast(ImportActivity.this, "El proceso de imporatción se ha cancelado!",
                                                false);
                                        db.setPreference(PNames.APP_STATE, PDefaultValues.IMPORTATION_CANCELLED);
                                        startActivity(new Intent(ImportActivity.this, ImportActivity.class));
                                        finish();
                                    }
                                });

                    }


                } else {

                    //TODO deb
                    Log.d(AppStatics.APP_TAG, "Hash Code test failed");

                    Tools.showInfoDialog(ImportActivity.this, getString(R.string.error5), "Aceptar",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Tools.showToast(ImportActivity.this, "El proceso de imporatción se ha cancelado!",
                                            false);
                                    db.setPreference(PNames.APP_STATE, PDefaultValues.IMPORTATION_CANCELLED);
                                    startActivity(new Intent(ImportActivity.this, ImportActivity.class));
                                    finish();
                                }
                            });
                }

                //endregion Checking Hash

                //endregion if importingFile.exists() == true

            } else {

                //region if importingFile.exists() == false

                //TODO deb
                Log.d(AppStatics.APP_TAG, "ImportFile do not exist");

                Tools.showDialogMessage(ImportActivity.this, getString(R.string.text3) +
                        db.getPreference(PNames.CURRENT_IMPORTING_FILE_PATH), "Cerrar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ImportActivity.this.finish();
                        System.exit(0);

                    }
                }, "Reiniciar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        db.setPreference(PNames.APP_STATE, PDefaultValues.IMPORTATION_CANCELLED);
                        startActivity(new Intent(ImportActivity.this, ImportActivity.class));
                        finish();
                    }
                });

                //endregion if importingFile.exists() == false

            }

            //GUI
            //region Spinner
            spinner.setAdapter(new ArrayAdapter<>(ImportActivity.this,
                    android.R.layout.simple_list_item_1,
                    new String[]{new File(db.getPreference(PNames.CURRENT_IMPORTING_FILE_PATH)).getName()}));
            spinner.setClickable(false);

            //endregion Spinner

            //region Fab
            fab.setBackgroundResource(R.drawable.ic_media_stop);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Tools.showDialogMessage(ImportActivity.this, getString(R.string.text7), "Sí",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Tools.showToast(ImportActivity.this, "El proceso de imporatción se ha cancelado!",
                                            false);
                                    db.setPreference(PNames.APP_STATE, PDefaultValues.IMPORTATION_CANCELLED);
                                    startActivity(new Intent(ImportActivity.this, ImportActivity.class));
                                    finish();
                                }
                            }, "No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                }
            });

            //endregion

            //endregion if AppImporting == Yes

        } else if (db.getPreference(PNames.APP_STATE).equals(PDefaultValues.FINISHING_IMPORTATION)) {

            //region if AppImporting == FINISHING
            detailTV.setText(R.string.text11);

            Tools.showInfoDialog(ImportActivity.this, getString(R.string.text10),
                    "Terminar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            db.setPreference(PNames.APP_STATE, PDefaultValues.NO_IMPORTING);
                            startActivity(new Intent(ImportActivity.this, MainActivity.class));
                        }
                    });

            //endregion

        } else {

            //region if AppImporting == NO_IMPORTING or IMPORTATION_CANCELLED

            //TODO
            Log.d(AppStatics.APP_TAG, "AppImporting = NO_IMPORTING");

            //GUI
            //region Spinner
            File toImportDirectory = new File(db.getPreference(PNames.TO_IMPORT_DIRECTORY_PATH));
            if (toImportDirectory.list().length != 0) {

                //TODO deb
                Log.d(AppStatics.APP_TAG, "toImportDirectory contain files");

                spinner.setAdapter(new ArrayAdapter<String>(ImportActivity.this,
                        android.R.layout.simple_list_item_1, toImportDirectory.list()));
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        selectedFile = (String) adapterView.getSelectedItem();

                        //TODO deb
                        Log.d(AppStatics.APP_TAG, "selectedFile = " + selectedFile);

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

            } else {

                //TODO deb
                Log.d(AppStatics.APP_TAG, "toImportDirectory is empty");

                spinner.setAdapter(new ArrayAdapter<String>(ImportActivity.this,
                        android.R.layout.simple_list_item_1, new String[]{"No hay archivos para importar"}));
                Tools.showToast(ImportActivity.this,
                        getString(R.string.text5) +
                                db.getPreference(PNames.TO_IMPORT_DIRECTORY_PATH), false);

            }

            //endregion Spinner

            //region fab
            fab.setBackgroundResource(R.drawable.ic_media_play);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Tools.showDialogMessage(ImportActivity.this, "Esta por comenzar una importación!!!",
                            "Comenzar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    if (selectedFile == null || selectedFile.equals("")) {
                                        Tools.showToast(ImportActivity.this, "Nada que importar!", false);
                                        return;
                                    }

                                    String filePath = db.getPreference(PNames.TO_IMPORT_DIRECTORY_PATH) +
                                            File.separator + selectedFile;

                                    //Reading CSVData
                                    try {
                                        data = Tools.readFile(filePath);

                                        //Checking CSVData internal hash Code
                                        String fileInternalHashCode = data.get(0).split(",", -1)[1];
                                        if (!fileInternalHashCode.equals(String.valueOf(getDataHashCode(data,
                                                AppStatics.Importation.CSV_FIRST_DATA_LINE_INDEX)))) {

                                            //TODO deb
                                            Log.d(AppStatics.APP_TAG, "Error Internal hash code mismatch");

                                            Tools.showInfoDialog(ImportActivity.this, getString(R.string.error6), "Aceptar",
                                                    new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            startActivity(new Intent(ImportActivity.this, ImportActivity.class));
                                                            finish();
                                                        }
                                                    });
                                            return;

                                        }

                                        //TODO deb
                                        Log.d(AppStatics.APP_TAG, "ImportFile CSVData reading succeed");

                                    } catch (IOException e) {

                                        //TODO deb
                                        Log.d(AppStatics.APP_TAG, "Error reading importFile CSVData");

                                        Tools.showInfoDialog(ImportActivity.this, getString(R.string.error3), "Aceptar",
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        Tools.showToast(ImportActivity.this, getString(R.string.text13),
                                                                false);
                                                        startActivity(new Intent(ImportActivity.this, ImportActivity.class));
                                                        finish();
                                                    }
                                                });
                                        return;
                                    }

                                    db.setPreference(PNames.CURRENT_IMPORTATION_FILE_HASH, data.get(0).split(",", -1)[1]);
                                    db.setPreference(PNames.CURRENT_IMPORTING_FILE_PATH, filePath);
                                    db.setPreference(PNames.CURRENT_IMPORTATION_INDEX,
                                            AppStatics.Importation.CSV_FIRST_DATA_LINE_INDEX);
                                    db.setPreference(PNames.APP_STATE, PDefaultValues.IMPORTING);

                                    startActivity(new Intent(ImportActivity.this, ImportActivity.class));
                                    finish();

                                }
                            }, "Cancelar", null);

                }
            });

            //endregion fab

            //endregion if AppImporting == NO or IMPORTATION_CANCELLED

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


    //Methods

    /**
     * return a hash code of a given subPart of the ArrayList, it first concatenate all
     * the CSVData as an String, and use the myStringHashCode method of the Tools class to return.
     *
     * @param data       the CSVData
     * @param startIndex the startIndex to considerate
     * @return the hashcode
     */
    private int getDataHashCode(ArrayList<String> data, int startIndex) {

        StringBuilder stringBuilder = new StringBuilder();

        for (; startIndex < data.size(); startIndex++) {
            stringBuilder.append(data.get(startIndex));
        }

        return Tools.myStringHashCode(stringBuilder.toString());
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
            Log.d(AppStatics.APP_TAG, "ImportUHInventoryAT created start index " + index);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            //TODO deb
            Log.d(AppStatics.APP_TAG, "ImportUHInventoryAT doInBackground");


            //everybody to LEFTOVER
            if (index == AppStatics.Importation.CSV_FIRST_DATA_LINE_INDEX) {
                db.updateStateColumn(IT.StateValues.LEFTOVER);
                db.updateLastCheckingColumn(Tools.getDate());
                //TODO deb
                Log.d(AppStatics.APP_TAG, "state column to LEFTOVER");
            }

            //Importing
            String number, description, area, altaDate, officialUpdate;
            int numberCount = CSVData.size() - AppStatics.Importation.CSV_FIRST_DATA_LINE_INDEX;
            String[] line;
            String action;
            for (; index < CSVData.size(); index++) {

                if (isCancelled()) {
                    return false;
                }

                //getting Data
                line = CSVData.get(index).split(",", -1);

                number = line[AppStatics.Importation.NUMBER_INDEX];
                description = line[AppStatics.Importation.DESCRIPTION_INDEX];
                area = line[AppStatics.Importation.AREA_INDEX];
                altaDate = line[AppStatics.Importation.ALTA_DATE_INDEX];
                officialUpdate = line[AppStatics.Importation.OFFICIAL_UPDATE_INDEX];


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

                //Generating bitmap
                try {
                    Tools.saveImage(Tools.codeTextToQRAndGetBitmap(number, 125), db.getPreference(PNames.QRS_DIRECTORY_PATH),
                            number + ".jpg");
                } catch (WriterException e) {
                    Log.d(AppStatics.APP_TAG, e.getMessage());
                }

                //Updating Preferences
                db.setPreference(PNames.CURRENT_IMPORTATION_INDEX, index);

                //Publishing
                publishProgress("Importando archivo inventario UH..." + "\n" +
                        index + "/" + numberCount + " --> " + getPerCent(index, numberCount) + "\n" +
                        action + "\n" +
                        "Numero: " + number + "\n" +
                        "Descripción: " + description + "\n" +
                        "Area: " + area + "\n" +
                        "AltaDate: " + altaDate + "\n" +
                        "Última Actualización oficial: " + officialUpdate);

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


                db.setPreference(PNames.APP_STATE, PDefaultValues.FINISHING_IMPORTATION);
                db.setPreference(PNames.DB_STATE, PDefaultValues.DB_OK);
                textView.setText(R.string.text11);
                Tools.showInfoDialog(ImportActivity.this, getString(R.string.text10),
                        "Terminar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                db.setPreference(PNames.APP_STATE, PDefaultValues.NO_IMPORTING);
                                db.setPreference(PNames.AREAS_TO_FOLLOW_CSV, PDefaultValues.EMPTY_PREFERENCE);
                                AppStatics.AreasToFollow.updateAreasToFollow(db);
                                startActivity(new Intent(ImportActivity.this, MainActivity.class));
                                finish();
                            }
                        });

            } else {
                db.setPreference(PNames.CURRENT_IMPORTATION_INDEX, index);
            }
            Log.d(AppStatics.APP_TAG, "Async Task onPostExecute, result " + b);
        }


        private String getPerCent(int part, int total) {
            return String.valueOf((part * 100) / total) + " %";
        }


    }

    private class ImportSaveAT extends AsyncTask<Void, String, Boolean> {


        //import asyncTask
        //GUI
        private final TextView textView;
        private int index;
        ArrayList<String> CSVData;

        public ImportSaveAT(int startIndex, ArrayList<String> CSVData, TextView textView) {

            this.textView = textView;
            this.CSVData = CSVData;
            index = startIndex;

            //TODO deb
            Log.d(AppStatics.APP_TAG, "ImportSaveAT created start index " + index);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            //TODO deb
            Log.d(AppStatics.APP_TAG, "ImportSaveAT doInBackground");

            //Importing
            String number, location, observation;
            int numberCount = CSVData.size() - AppStatics.Importation.CSV_FIRST_DATA_LINE_INDEX;
            int type;
            String[] line;
            for (; index < CSVData.size(); index++) {

                if (isCancelled()) {
                    return false;
                }

                line = CSVData.get(index).split(",", -1);

                number = line[AppStatics.Importation.NUMBER_INDEX];
                location = line[AppStatics.Importation.LOCATION_INDEX];
                type = Integer.parseInt(line[AppStatics.Importation.TYPE_INDEX]);
                observation = line[AppStatics.Importation.OBSERVATION_INDEX];

                if (db.updateNonOfficialData(number, location, type, observation) != -1) {

                    publishProgress("Importando archivo salva..." + "\n" +
                            index + "/" + numberCount + " --> " + getPerCent(index, numberCount) + "\n" +
                            "(Actualizando úmero)" + "\n" +
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

                db.setPreference(PNames.APP_STATE, PDefaultValues.FINISHING_IMPORTATION);
                textView.setText(R.string.text11);
                Tools.showInfoDialog(ImportActivity.this, getString(R.string.text10),
                        "Terminar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                db.setPreference(PNames.APP_STATE, PDefaultValues.NO_IMPORTING);
                                startActivity(new Intent(ImportActivity.this, MainActivity.class));
                                finish();
                            }
                        });

            } else {
                db.setPreference(PNames.CURRENT_IMPORTATION_INDEX, index);
            }



            Log.d(AppStatics.APP_TAG, "Async Task onPostExecute, result " + b);


        }

        private String getPerCent(int part, int total) {
            return String.valueOf((part * 100) / total) + " %";
        }
    }

}