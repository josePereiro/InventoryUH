package com.example.compereirowww.inventory20181.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.compereirowww.inventory20181.DataBase.DB;
import com.example.compereirowww.inventory20181.DataBase.DB.IT;
import com.example.compereirowww.inventory20181.DataBase.DB.RT;
import com.example.compereirowww.inventory20181.R;
import com.example.compereirowww.inventory20181.Tools.Tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ImportActivity extends AppCompatActivity {

    //DEB
    static final String ACT_TAG_ = "ImportActivity ";
    static final String ON_CREATE_TAG_ = "OnCreate ";
    static final String ON_RESUME_TAG_ = "OnResume ";
    static final String ON_PAUSE_TAG_ = "OnPause ";
    static final String SEPARATOR_TAG = "+++++++++++++++++++++++++++++++++++++++++++++";

    //GUI
    TextView detailTV;
    ProgressBar progressBar;
    Spinner spinner;
    FloatingActionButton fab;


    //Importation Fields
    private AsyncTask<Void, String, Integer> importAT;
    private String selectedFile;
    private ArrayList<String> data;

    //DB
    private DB db;


    //region life cycle  methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //TODO deb
        Log.d(AppStatics.APP_TAG, "ImportActivity.onCreate");

        //DB
        db = AppStatics.db;

        //GUI
        detailTV = (TextView) findViewById(R.id.detail_tv);
        spinner = (Spinner) findViewById(R.id.spinner);
        progressBar = (ProgressBar) findViewById(R.id.importation_pb);
        fab = (FloatingActionButton) findViewById(R.id.fab);


    }

    @Override
    protected void onResume() {
        super.onResume();


        //TODO deb
        Log.d(AppStatics.APP_TAG, ACT_TAG_ + ON_RESUME_TAG_ + SEPARATOR_TAG);
        Log.d(AppStatics.APP_TAG, "Handling Importation");

        if (db.getPreference(RT.APP_IMPORTING).equals(RT.FINISHING)) {

            detailTV.setText(R.string.text11);
            progressBar.setMax(1);
            progressBar.setProgress(1);

            Tools.showInfoDialog(ImportActivity.this, getString(R.string.text10),
                    "Terminar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            db.setPreference(RT.APP_IMPORTING, RT.NO);
                            startActivity(new Intent(ImportActivity.this, MainActivity.class));
                        }
                    });

        }

        if (db.getPreference(RT.APP_IMPORTING).equals(RT.YES)) {

            //region if AppImporting == Yes

            //TODO deb
            Log.d(AppStatics.APP_TAG, "If AppImporting = YES");

            data = new ArrayList<>();

            //Import File
            File importingFile = new File(db.getPreference(RT.CURRENT_IMPORTING_FILE_PATH));
            if (importingFile.exists()) {

                //region if importingFile.exists() == true

                //TODO deb
                Log.d(AppStatics.APP_TAG, "If ImportFile exist");

                //region Reading data
                try {
                    data = Tools.readFile(importingFile);

                    //TODO deb
                    Log.d(AppStatics.APP_TAG, "ImportFile data reading succeed");

                } catch (IOException e) {

                    //TODO deb
                    Log.d(AppStatics.APP_TAG, "Error reading importFile data");

                    Tools.showInfoDialog(ImportActivity.this, getString(R.string.error3), "Aceptar",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Tools.showToast(ImportActivity.this, "El proceso de imporatción se ha cancelado!",
                                            false);
                                    db.setPreference(RT.APP_IMPORTING, RT.CANCELLED);
                                    startActivity(new Intent(ImportActivity.this, ImportActivity.class));
                                    finish();
                                }
                            });
                }
                //endregion

                //region Checking HashCode
                if (String.valueOf(getDataHashCode(data, AppStatics.Importation.FIRTS_IMPORT_VALUE_INDEX)).
                        equals(db.getPreference(RT.CURRENT_IMPORTATION_FILE_HASH))) {

                    //TODO deb
                    Log.d(AppStatics.APP_TAG, "Hash Code test succeed");

                    //Starting Importing
                    if (data.get(0).split(",", -1)[0].equals(AppStatics.
                            Importation.UH_INVENTORY_FILE_HEAD_CODE)) {

                        int startIndex = Integer.
                                parseInt(db.getPreference(RT.CURRENT_IMPORTATION_INDEX));

                        //TODO deb
                        Log.d(AppStatics.APP_TAG, "Importing an UH inventory file, start index " + startIndex);

                        importAT = new ImportUHInventoryAT(startIndex, data, detailTV, progressBar);
                        importAT.execute();

                    } else if (data.get(0).split(",", -1)[0].equals(AppStatics.
                            Importation.SALVA_INVENTORY_FILE_HEAD_CODE)) {

                        int startIndex = Integer.
                                parseInt(db.getPreference(RT.CURRENT_IMPORTATION_INDEX));

                        //TODO deb
                        Log.d(AppStatics.APP_TAG, "Importing a salva file, start index " + startIndex);

                        importAT = new ImportSaveAT();
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
                                        db.setPreference(RT.APP_IMPORTING, RT.CANCELLED);
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
                                    db.setPreference(RT.APP_IMPORTING, RT.CANCELLED);
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
                        db.getPreference(RT.CURRENT_IMPORTING_FILE_PATH), "Cerrar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ImportActivity.this.finish();
                        System.exit(0);

                    }
                }, "Reiniciar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        db.setPreference(RT.APP_IMPORTING, RT.CANCELLED);
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
                    new String[]{new File(db.getPreference(RT.CURRENT_IMPORTING_FILE_PATH)).getName()}));
            spinner.setClickable(false);

            //endregion Spinner

            //region Fab

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Tools.showDialogMessage(ImportActivity.this, getString(R.string.text7), "Sí",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Tools.showToast(ImportActivity.this, "El proceso de imporatción se ha cancelado!",
                                            false);
                                    db.setPreference(RT.APP_IMPORTING, RT.CANCELLED);
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

        } else {

            //region if AppImporting == NO or CANCELLED

            //TODO
            Log.d(AppStatics.APP_TAG, "AppImporting = NO");

            //GUI
            //region Spinner
            File toImportDirectory = new File(db.getPreference(RT.TO_IMPORT_DIRECTORY_PATH));
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
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    int c = -1;

                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                        if (c % 2 == 0) {
                            Tools.showToast(ImportActivity.this,
                                    getString(R.string.text5) +
                                            db.getPreference(RT.TO_IMPORT_DIRECTORY_PATH), false);
                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

            }

            //endregion Spinner

            //region fab
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

                                    //region Updating Preferences
                                    String filePath = db.getPreference(RT.TO_IMPORT_DIRECTORY_PATH) +
                                            File.separator + selectedFile;

                                    //Reading data
                                    try {
                                        data = Tools.readFile(filePath);

                                        //Checking data internal hash Code
                                        String fileInternalHashCode = data.get(0).split(",", -1)[1];
                                        if (!fileInternalHashCode.equals(String.valueOf(getDataHashCode(data,
                                                AppStatics.Importation.FIRTS_IMPORT_VALUE_INDEX)))) {

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
                                        Log.d(AppStatics.APP_TAG, "ImportFile data reading succeed");

                                    } catch (IOException e) {

                                        //TODO deb
                                        Log.d(AppStatics.APP_TAG, "Error reading importFile data");

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

                                    db.setPreference(RT.CURRENT_IMPORTATION_FILE_HASH, data.get(0).split(",", -1)[1]);
                                    db.setPreference(RT.CURRENT_IMPORTING_FILE_PATH, filePath);
                                    db.setPreference(RT.CURRENT_IMPORTATION_INDEX,
                                            AppStatics.Importation.FIRTS_IMPORT_VALUE_INDEX);
                                    db.setPreference(RT.APP_IMPORTING, RT.YES);

                                    //endregion updating preferences

                                    startActivity(new Intent(ImportActivity.this, ImportActivity.class));
                                    finish();

                                }
                            }, "Cancelar", null);

                }
            });

            //endregion fab

            //endregion if AppImporting == NO or CANCELLED

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

    //endregion

    //region methods

    /**
     * return a hash code of a given subPart of the ArrayList, it first concatenate all
     * the data as an String, and use the myStringHashCode method of the Tools class to return.
     *
     * @param data       the data
     * @param startIndex the startIndex to considerate
     * @return the hashcode
     */
    private static int getDataHashCode(ArrayList<String> data, int startIndex) {

        StringBuilder stringBuilder = new StringBuilder();

        for (; startIndex < data.size(); startIndex++) {
            stringBuilder.append(data.get(startIndex));
        }

        return Tools.myStringHashCode(stringBuilder.toString());
    }

    //endregion

    private class ImportUHInventoryAT extends AsyncTask<Void, String, Integer> {

        //import asyncTask
        //GUI
        private final TextView textView;
        private final ProgressBar progressBar;
        private int index;
        ArrayList<String> data;

        public ImportUHInventoryAT(int startIndex, ArrayList<String> data, TextView textView,
                                   ProgressBar progressBar) {

            this.textView = textView;
            this.progressBar = progressBar;
            this.progressBar.setMax(data.size() -
                    AppStatics.Importation.FIRTS_IMPORT_VALUE_INDEX);
            this.data = data;
            index = startIndex;

            //TODO deb
            Log.d(AppStatics.APP_TAG, "ImportUHInventoryAT created start index " + index);
        }

        @Override
        protected Integer doInBackground(Void... voids) {

            //TODO deb
            Log.d(AppStatics.APP_TAG, "ImportUHInventoryAT doInBackground");

            //everybody to LEFTOVER
            if (index == AppStatics.Importation.FIRTS_IMPORT_VALUE_INDEX) {
                db.updateStateColumn(IT.StateValues.LEFTOVER);
            }

            //Importing
            String number, description, area, altaDate, officialUpdate;
            int numberCount = data.size() - AppStatics.Importation.FIRTS_IMPORT_VALUE_INDEX;
            String[] line;
            for (; index < data.size(); index++) {

                if (isCancelled()) {
                    return 0;
                }

                //getting Data
                line = data.get(index).split(",", -1);

                number = line[AppStatics.Importation.NUMBER_INDEX];
                description = line[AppStatics.Importation.DESCRIPTION_INDEX];
                area = line[AppStatics.Importation.AREA_INDEX];
                altaDate = line[AppStatics.Importation.ALTA_DATE_INDEX];
                officialUpdate = line[AppStatics.Importation.OFFICIAL_UPDATE_INDEX];

                if (db.numberExist(number)) {

                    db.updateDescription(number, description);
                    db.updateArea(number, area);
                    db.updateAltaDate(number, altaDate);
                    db.updateOfficialUpdate(number, officialUpdate);
                    db.updateStateColumn(IT.StateValues.MISSING);
                    publishProgress("" + index,
                            index + "/" + numberCount + "\n" +
                                    "(Actualizando Número)\n" +
                                    "Numero: " + number + "\n" +
                                    "Descripción: " + description + "\n" +
                                    "Area: " + area + "\n" +
                                    "AltaDate: " + altaDate + "\n" +
                                    "Última Actualización oficial: " + officialUpdate);

                } else {

                    db.insertNewNumber(number,
                            description,
                            area,
                            altaDate,
                            officialUpdate,
                            false,
                            IT.StateValues.MISSING,
                            Tools.getDate(),
                            IT.TypeValues.UNKNOWN, "", "");

                    publishProgress("" + index,
                            index + "/" + numberCount + "\n" +
                                    "(Nuevo Número)\n" +
                                    "Numero: " + number + "\n" +
                                    "Descripción: " + description + "\n" +
                                    "Area: " + area + "\n" +
                                    "AltaDate: " + altaDate + "\n" +
                                    "Última Actualización oficial: " + officialUpdate);

                }

                //Updating Preferences
                db.setPreference(RT.CURRENT_IMPORTATION_INDEX, index);
            }

            return 1;
        }

        @Override
        protected void onProgressUpdate(String... values) {

            progressBar.setProgress(Integer.parseInt(values[0]));
            textView.setText(values[1]);
        }

        @Override
        protected void onPostExecute(Integer i) {

            if (i == 1) {
                db.setPreference(RT.APP_IMPORTING, RT.FINISHING);

                textView.setText(R.string.text11);
                progressBar.setMax(1);
                progressBar.setProgress(1);

                Tools.showInfoDialog(ImportActivity.this, getString(R.string.text10),
                        "Terminar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                db.setPreference(RT.APP_IMPORTING, RT.NO);
                                startActivity(new Intent(ImportActivity.this, MainActivity.class));
                            }
                        });

            } else {
                db.setPreference(RT.CURRENT_IMPORTATION_INDEX, index);
            }
            Log.d(AppStatics.APP_TAG, "Async Task onPostExecute, result " + i);
        }


    }


    private class ImportSaveAT extends AsyncTask<Void, String, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            return null;
        }

        @Override
        protected void onPostExecute(Integer i) {

            Tools.showToast(ImportActivity.this, "Esta opción todavía no está avilitada", false);

        }
    }

}