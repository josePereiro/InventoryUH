package com.example.compereirowww.inventory20181.Activities;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.compereirowww.inventory20181.DataBase.DB;
import com.example.compereirowww.inventory20181.R;
import com.example.compereirowww.inventory20181.Tools.Tools;

import java.io.File;
import java.io.IOException;

public class BackUpActivity extends AppCompatActivity {

    //GUI
    Spinner spinner;
    TextView CSVPreviewTV, portDetailTV;

    File selectedFile;
    String backUp;
    String backUpCreationDate;
    BuildBackupCSVAT buildBackupCSVAT;

    private static Cursor data;
    private static String BACKUP_ = "Respaldo ";
    private static String[] ports = new String[]{BACKUP_ + "1", BACKUP_ + "2",
            BACKUP_ + "3", BACKUP_ + "4", BACKUP_ + "5"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!backUp.equals("")) {
                    try {

                        File newBackUpFile = getNewBackUpFile();
                        deleteOldBackUpFile();
                        Tools.writeFile(newBackUpFile, backUp);
                        selectedFile = newBackUpFile;
                        new GetPortInfoAT().execute((spinner.getSelectedItemPosition() + 1) + "");
                        Tools.showToast(BackUpActivity.this, "Respaldo guardado!", false);
                    } catch (IOException e) {
                        Tools.showToast(BackUpActivity.this, "Error al guardar el respaldo!", false);
                    }
                }

            }

            private File getNewBackUpFile(){
                int numbersCount = data.getCount();
                int backUpNumber = spinner.getSelectedItemPosition() + 1;
                String fileName = BACKUP_ + backUpNumber + " (" + numbersCount + "ns) " +
                        Tools.getFormattedDateForFileNaming() + AppStatics.IMPORT_FILE_EXTENSION;

                return new File(AppStatics.db.
                        getPreference(DB.PT.PNames.BACKUPS_DIRECTORY_PATH),
                        fileName);
            }

            private void deleteOldBackUpFile(){
                if (selectedFile != null && selectedFile.exists()) {
                    selectedFile.delete();
                }
            }
        });

        AppStatics.formatView((TextView)findViewById(R.id.textView));
        CSVPreviewTV = (TextView) findViewById(R.id.csv_preview_tv);
        AppStatics.formatView(CSVPreviewTV);
        portDetailTV = (TextView) findViewById(R.id.port_info_tv);
        AppStatics.formatView(portDetailTV);
        spinner = (Spinner) findViewById(R.id.backups_s);
        AppStatics.formatView(BackUpActivity.this, ports, spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    File backUpDir = new File(AppStatics.
                            db.getPreference(DB.PT.PNames.BACKUPS_DIRECTORY_PATH));
                    File[] backUpFiles = backUpDir.listFiles();
                    String selectedItem = (String) adapterView.getItemAtPosition(i);
                    selectedFile = null;
                    for (File f : backUpFiles) {
                        if (f.getName().contains(selectedItem)) {
                            selectedFile = f;
                        }
                    }
                } catch (Exception e) {
                    selectedFile = null;
                }
                new GetPortInfoAT().execute((i + 1) + "");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (buildBackupCSVAT == null) {
            buildBackupCSVAT = new BuildBackupCSVAT();
            buildBackupCSVAT.execute();
        } else {
            buildBackupCSVAT.cancel(true);
            buildBackupCSVAT = new BuildBackupCSVAT();
            buildBackupCSVAT.execute();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_help, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.help) {
            Tools.showToast(BackUpActivity.this, "No hay ayuda!!! Por ahora...", false);
            return super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }

    public static void setData(Cursor data) {
        BackUpActivity.data = data;
    }

    private class BuildBackupCSVAT extends AsyncTask<Void, Void, Boolean> {

        StringBuilder backUpSB;

        @Override
        protected Boolean doInBackground(Void... voids) {

            //CSV Inventory Save Format

            //Head
            //Data: Number, Location, Type, Observation

            if (isCancelled()) {
                return false;
            }

            data.moveToPosition(-1);

            backUpSB = new StringBuilder();
            String line;
            while (data.moveToNext()) {

                if (isCancelled()) {
                    return false;
                }

                line = data.getString(DB.IT.Indexes.NUMBER_COLUMN_INDEX);
                line += ",";
                line += data.getString(DB.IT.Indexes.LOCATION_COLUMN_INDEX);
                line += ",";
                line += data.getString(DB.IT.Indexes.TYPE_COLUMN_INDEX);
                line += ",";
                line += data.getString(DB.IT.Indexes.OBSERVATION_COLUMN_INDEX);
                line += "\n";

                backUpSB.append(line);
            }

            backUpCreationDate = Tools.getFormattedDateForFileNaming();
            String head = AppStatics.InventoryBackUpFile.INVENTORY_BACKUP_FILE_HEAD_CODE + "," +
                    Tools.myStringHashCode(backUpSB.toString().replaceAll("\n", "")) + "," +
                    getFilter1Value() + "," +
                    getFilter2Value() + "," +
                    backUpCreationDate + "," +
                    "\n";

            if (isCancelled()) return false;

            backUp = head + backUpSB.toString();

            return true;
        }

        @Override
        protected void onPostExecute(Boolean r) {
            if (r) {
                CSVPreviewTV.setText("Vista previa del csv que desea guardar:");
                if (backUp.length() < 2000) {
                    CSVPreviewTV.append("\n");
                    CSVPreviewTV.append(backUp);
                } else {
                    CSVPreviewTV.append("\n");
                    CSVPreviewTV.append(backUp.substring(0, 1900) + " ...");
                }
            }
        }

        private String getFilter1Value() {
            return AppStatics.db.getPreference(DB.PT.PNames.CURRENT_FILTER1_VALUE);
        }

        private String getFilter2Value() {
            return AppStatics.db.getPreference(DB.PT.PNames.CURRENT_FILTER2_VALUE);
        }

    }

    private class GetPortInfoAT extends AsyncTask<String, Void, Void> {

        String ms;

        @Override
        protected Void doInBackground(String... backUpNumber) {

            ms = "El repaldo " + backUpNumber[0] + " está vacío!!";

            if (selectedFile == null) return null;
            if (!selectedFile.exists()) return null;
            try {

                ms = "El respaldo " + backUpNumber[0] + " actualmente contiene ";
                String firstLine = Tools.readFileFirstLine(selectedFile);
                String[] firstLineData = firstLine.split(",", -1);
                ms += "un archivo CREADO el " + firstLineData[AppStatics.InventoryBackUpFile.CREATION_DATE_INDEX];
                ms += " con los FILTROS " + firstLineData[AppStatics.InventoryBackUpFile.FILTER1_INDEX] +
                        " y " + firstLineData[AppStatics.InventoryBackUpFile.FILTER2_INDEX] + ".";

            } catch (IOException e) {
                ms = "Error cargando la vista previa";
                return null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            portDetailTV.setText(ms);
        }
    }


}
