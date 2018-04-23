package com.example.compereirowww.inventory20181.Activities;

import android.database.Cursor;
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
import com.example.compereirowww.inventory20181.R;
import com.example.compereirowww.inventory20181.Tools.Tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static com.example.compereirowww.inventory20181.Activities.AppStatics.APP_TAG;
import static com.example.compereirowww.inventory20181.Activities.AppStatics.db;

public class ReportActivity extends AppCompatActivity {


    //GUI
    TextView textView;
    StringBuilder report;

    //data
    Spinner formatS;
    private static Cursor data;
    static int selectedFormat = 0;
    String creationDate;
    boolean building;

    private static String F_TEX = "Texto";
    private static String F_CSV = "CSV";

    private static AsyncTask<Void, String, Boolean> asyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_activity);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!building) {
                    String rName = "Reporte " + " (" + data.getCount() + "ns) " + creationDate + ".csv";
                    File reportFile = new File(db.getPreference(DB.PT.PNames.REPORTS_DIRECTORY_PATH), rName);
                    try {
                        Tools.writeFile(reportFile, report.toString());
                        Tools.showToast(ReportActivity.this, "Reporte guardado!", false);
                    } catch (IOException e) {
                        Tools.showToast(ReportActivity.this, "El reporte no se pudo guardar!", false);
                    }
                } else {
                    Tools.showToast(ReportActivity.this, "Espere a que se construya el Reporte!", false);
                }

            }
        });
        textView = (TextView) findViewById(R.id.textView);
        formatS = (Spinner) findViewById(R.id.format_s);
        formatS.setAdapter(new ArrayAdapter<>(ReportActivity.this,
                android.R.layout.simple_list_item_1,
                new String[]{F_TEX, F_CSV}));
        formatS.setSelection(selectedFormat);
        formatS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedFormat = i;
                if (asyncTask != null) {
                    asyncTask.cancel(true);
                }
                if (i == 0) {
                    asyncTask = new MakeLongReportTXTAT();
                    asyncTask.execute();
                } else {
                    asyncTask = new MakeReportCSVAT();
                    asyncTask.execute();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (asyncTask != null) {
            asyncTask.cancel(true);
            asyncTask = null;
        }
    }


    public static void setData(Cursor data) {
        ReportActivity.data = data;
    }

    private class MakeReportCSVAT extends AsyncTask<Void, String, Boolean> {

        @Override
        protected void onPreExecute() {
            building = true;
            formatS.setEnabled(false);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {


            report = new StringBuilder();
            creationDate = Tools.getFormattedDateForFileNaming();

            if (isCancelled()) {
                report = new StringBuilder();
                return false;
            }

            report.append("REPORTE DE INVENTARIO ").append(creationDate);
            report.append("\n");
            report.append("Número").
                    append(",").
                    append("Descripción").
                    append(",").
                    append("Área").
                    append(",").
                    append("Fecha de alta").
                    append(",").
                    append("Fecha de actualización oficial").
                    append(",").
                    append("Estado actual").
                    append(",").
                    append("Último chequeo del estado").
                    append(",").
                    append("Tipo").
                    append(",").
                    append("Localización").
                    append(",").
                    append("Observación");
            report.append("\n");

            data.moveToPosition(-1);

            Log.d(APP_TAG, data.getCount() + "");


            String number, description, area, altaDate, updateDate, state, lastCheckDate, type, location, observation;
            int c = 0;
            while (data.moveToNext()) {

                if (isCancelled()) {
                    report = new StringBuilder();
                    return false;
                }

                building = true;

                number = data.getString(DB.IT.Indexes.NUMBER_COLUMN_INDEX);
                description = data.getString(DB.IT.Indexes.DESCRIPTION_COLUMN_INDEX);
                area = data.getString(DB.IT.Indexes.AREA_COLUMN_INDEX);
                altaDate = data.getString(DB.IT.Indexes.ALTA_DATE_COLUMN_INDEX);
                updateDate = data.getString(DB.IT.Indexes.OFFICIAL_UPDATE_COLUMN_INDEX);
                state = DB.IT.StateValues.
                        toString(Integer.parseInt(data.getString(DB.IT.Indexes.STATE_COLUMN_INDEX)));
                lastCheckDate = Tools.formatDate(data.getString(DB.IT.Indexes.LAST_CHECKING_COLUMN_INDEX));
                type = DB.IT.TypeValues.
                        toString(Integer.parseInt(data.getString(DB.IT.Indexes.TYPE_COLUMN_INDEX)));
                location = data.getString(DB.IT.Indexes.LOCATION_COLUMN_INDEX);
                observation = data.getString(DB.IT.Indexes.OBSERVATION_COLUMN_INDEX);
                report.append(number).
                        append(",").
                        append(description).
                        append(",").
                        append(area).
                        append(",").
                        append(altaDate).
                        append(",").
                        append(updateDate).
                        append(",").
                        append(state).
                        append(",").
                        append(lastCheckDate).
                        append(",").
                        append(type).
                        append(",").
                        append(location).
                        append(",").
                        append(observation).
                        append("\n");

                c++;

                publishProgress("Creando Reporte... " + getPerCent(c, data.getCount()));
            }

            publishProgress("Terminando...");
            return true;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            textView.setText(values[0]);
        }

        private String getPerCent(int part, int total) {
            return String.valueOf((part * 100) / total) + " %";
        }

        @Override
        protected void onPostExecute(Boolean r) {

            if (r)
                if (report.length() < 5000) {
                    textView.setText(report);
                } else {
                    textView.setText(report.substring(0, 4900) + " ...");
                }
            building = false;
            formatS.setEnabled(true);
        }
    }

    private class MakeLongReportTXTAT extends AsyncTask<Void, String, Boolean> {

        private String TAB = "   ";
        //STATE
        private int MISSING_INDEX = 0;
        private int PRESENT_INDEX = 1;
        private int LEFTOVER_INDEX = 2;
        private int IGNORED_MISSING_INDEX = 3;
        private int LEFTOVER_PRESENT_INDEX = 4;
        private int STATES_COUNT = 5;

        //TYPE
        private int EQUIPMENT_INDEX = 0;
        private int FURNISHING_INDEX = 1;
        private int UNKNOWN_INDEX = 2;
        private int TYPE_COUNT = 3;

        private long progress = 0;
        private int total;

        @Override
        protected void onPreExecute() {
            building = true;
            formatS.setEnabled(false);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            report = new StringBuilder();
            creationDate = Tools.getFormattedDateForFileNaming();

            total = (int) (data.getCount() * 2.3);
            DataThree three = new DataThree();

            if (isCancelled()) {
                report = new StringBuilder();
                return false;
            }


            report.append("REPORTE DE INVENTARIO ").append(creationDate).append("\n").append("\n");
            if (three.getAreasCount() == 1) {
                report.append("--- ").append(three.getArea(0).toUpperCase()).append(" ---").append("\n").append("\n");
            } else {
                report.append("--- DETALLES GENERALES ---").append("\n").append("\n");
            }
            report.append("TOTAL DE NÚMEROS: ").append(data.getCount()).append("\n");

            int tempSize;
            ArrayList<String> tempNumbers;
            tempSize = three.getTypeTotalNumberCount(EQUIPMENT_INDEX);
            if (tempSize > 0) {
                report.append(TAB).append("EQUIPOS: ").append(tempSize).append("\n");
                tempSize = three.getStateTotalNumberCountOfAType(MISSING_INDEX, EQUIPMENT_INDEX);
                if (tempSize > 0) {
                    tempNumbers = three.getStateNumbersOfAType(MISSING_INDEX, EQUIPMENT_INDEX);
                    report.append(TAB).append(TAB).append("Faltantes: ").
                            append(tempSize).
                            append("\n").append("\n");
                    report.append(Arrays.toString(tempNumbers.toArray())).append("\n").append("\n");
                }
                tempSize = three.getStateTotalNumberCountOfAType(PRESENT_INDEX, EQUIPMENT_INDEX);
                if (tempSize > 0) {
                    tempNumbers = three.getStateNumbersOfAType(PRESENT_INDEX, EQUIPMENT_INDEX);
                    report.append(TAB).append(TAB).append("Presentes: ").
                            append(tempSize).
                            append("\n").append("\n");
                    report.append(Arrays.toString(tempNumbers.toArray())).append("\n").append("\n");
                }
                tempSize = three.getStateTotalNumberCountOfAType(LEFTOVER_INDEX, EQUIPMENT_INDEX);
                if (tempSize > 0) {
                    tempNumbers = three.getStateNumbersOfAType(LEFTOVER_INDEX, EQUIPMENT_INDEX);
                    report.append(TAB).append(TAB).append("Sobrantes: ").
                            append(tempSize).
                            append("\n").append("\n");
                    report.append(Arrays.toString(tempNumbers.toArray())).append("\n").append("\n");
                }
                tempSize = three.getStateTotalNumberCountOfAType(IGNORED_MISSING_INDEX, EQUIPMENT_INDEX);
                if (tempSize > 0) {
                    tempNumbers = three.getStateNumbersOfAType(IGNORED_MISSING_INDEX, EQUIPMENT_INDEX);
                    report.append(TAB).append(TAB).append("Faltantes Ignorados: ").
                            append(tempSize).
                            append("\n").append("\n");
                    report.append(Arrays.toString(tempNumbers.toArray())).append("\n").append("\n");
                }
                tempSize = three.getStateTotalNumberCountOfAType(LEFTOVER_PRESENT_INDEX, EQUIPMENT_INDEX);
                if (tempSize > 0) {
                    tempNumbers = three.getStateNumbersOfAType(LEFTOVER_PRESENT_INDEX, EQUIPMENT_INDEX);
                    report.append(TAB).append(TAB).append("Sobrantes Presentes: ").
                            append(tempSize).
                            append("\n").append("\n");
                    report.append(Arrays.toString(tempNumbers.toArray())).append("\n").append("\n");
                }
            }

            tempSize = three.getTypeTotalNumberCount(FURNISHING_INDEX);
            if (tempSize > 0) {
                report.append(TAB).append("MUEBLES: ").append(tempSize).append("\n");
                tempSize = three.getStateTotalNumberCountOfAType(MISSING_INDEX, FURNISHING_INDEX);
                if (tempSize > 0) {
                    tempNumbers = three.getStateNumbersOfAType(MISSING_INDEX, FURNISHING_INDEX);
                    report.append(TAB).append(TAB).append("Faltantes: ").
                            append(tempSize).
                            append("\n").append("\n");
                    report.append(Arrays.toString(tempNumbers.toArray())).append("\n").append("\n");
                }
                tempSize = three.getStateTotalNumberCountOfAType(PRESENT_INDEX, FURNISHING_INDEX);
                if (tempSize > 0) {
                    tempNumbers = three.getStateNumbersOfAType(PRESENT_INDEX, FURNISHING_INDEX);
                    report.append(TAB).append(TAB).append("Presentes: ").
                            append(tempSize).
                            append("\n").append("\n");
                    report.append(Arrays.toString(tempNumbers.toArray())).append("\n").append("\n");
                }
                tempSize = three.getStateTotalNumberCountOfAType(LEFTOVER_INDEX, FURNISHING_INDEX);
                if (tempSize > 0) {
                    tempNumbers = three.getStateNumbersOfAType(LEFTOVER_INDEX, FURNISHING_INDEX);
                    report.append(TAB).append(TAB).append("Sobrantes: ").
                            append(tempSize).
                            append("\n").append("\n");
                    report.append(Arrays.toString(tempNumbers.toArray())).append("\n").append("\n");
                }
                tempSize = three.getStateTotalNumberCountOfAType(IGNORED_MISSING_INDEX, FURNISHING_INDEX);
                if (tempSize > 0) {
                    tempNumbers = three.getStateNumbersOfAType(IGNORED_MISSING_INDEX, FURNISHING_INDEX);
                    report.append(TAB).append(TAB).append("Faltantes Ignorados: ").
                            append(tempSize).
                            append("\n").append("\n");
                    report.append(Arrays.toString(tempNumbers.toArray())).append("\n").append("\n");
                }
                tempSize = three.getStateTotalNumberCountOfAType(LEFTOVER_PRESENT_INDEX, FURNISHING_INDEX);
                if (tempSize > 0) {
                    tempNumbers = three.getStateNumbersOfAType(LEFTOVER_PRESENT_INDEX, FURNISHING_INDEX);
                    report.append(TAB).append(TAB).append("Sobrantes Presentes: ").
                            append(tempSize).
                            append("\n").append("\n");
                    report.append(Arrays.toString(tempNumbers.toArray())).append("\n").append("\n");
                }
            }

            if (three.getAreasCount() > 1) {

                String currentArea;

                report.append("\n");
                report.append("--- DETALLES POR ÁREAS ---").append("\n").append("\n");
                for (int a = 0; a < three.getAreasCount(); a++) {

                    currentArea = three.getArea(a).toUpperCase();

                    report.append("- ").append(currentArea).append(" -").append("\n");
                    report.append("Total de números: ").append(three.getAreaTotalNumberCount(a)).append("\n").append("\n");

                    tempSize = three.getNumberCount(a, EQUIPMENT_INDEX);
                    if (tempSize > 0) {
                        report.append(TAB).append("EQUIPOS: ").append(tempSize).append("\n");

                        tempSize = three.getNumberCount(a, EQUIPMENT_INDEX, MISSING_INDEX);

                        if (tempSize > 0) {
                            tempNumbers = three.getNumbers(a, EQUIPMENT_INDEX, MISSING_INDEX);
                            report.append(TAB).append(TAB).append("Faltantes: ").
                                    append(tempSize).
                                    append("\n").append("\n");
                            report.append(Arrays.toString(tempNumbers.toArray())).append("\n").append("\n");
                        }
                        tempSize = three.getNumberCount(a, EQUIPMENT_INDEX, PRESENT_INDEX);
                        if (tempSize > 0) {
                            tempNumbers = three.getNumbers(a, EQUIPMENT_INDEX, PRESENT_INDEX);
                            report.append(TAB).append(TAB).append("Presentes: ").
                                    append(tempSize).
                                    append("\n").append("\n");
                            report.append(Arrays.toString(tempNumbers.toArray())).append("\n").append("\n");
                        }
                        tempSize = three.getNumberCount(a, EQUIPMENT_INDEX, LEFTOVER_INDEX);
                        if (tempSize > 0) {
                            tempNumbers = three.getNumbers(a, EQUIPMENT_INDEX, LEFTOVER_INDEX);
                            report.append(TAB).append(TAB).append("Sobrantes: ").
                                    append(tempSize).
                                    append("\n").append("\n");
                            report.append(Arrays.toString(tempNumbers.toArray())).append("\n").append("\n");
                        }
                        tempSize = three.getNumberCount(a, EQUIPMENT_INDEX, IGNORED_MISSING_INDEX);
                        if (tempSize > 0) {
                            tempNumbers = three.getNumbers(a, EQUIPMENT_INDEX, IGNORED_MISSING_INDEX);
                            report.append(TAB).append(TAB).append("Faltantes Ignorados: ").
                                    append(tempSize).
                                    append("\n").append("\n");
                            report.append(Arrays.toString(tempNumbers.toArray())).append("\n").append("\n");
                        }
                        tempSize = three.getNumberCount(a, EQUIPMENT_INDEX, LEFTOVER_PRESENT_INDEX);
                        if (tempSize > 0) {
                            tempNumbers = three.getNumbers(a, EQUIPMENT_INDEX, LEFTOVER_PRESENT_INDEX);
                            report.append(TAB).append(TAB).append("Sobrantes Presentes: ").
                                    append(tempSize).
                                    append("\n").append("\n");
                            report.append(Arrays.toString(tempNumbers.toArray())).append("\n").append("\n");
                        }
                    }

                    tempSize = three.getNumberCount(a, FURNISHING_INDEX);
                    if (tempSize > 0) {
                        report.append("- ").append(currentArea).append(" -").append("\n");
                        report.append(TAB).append("MUEBLES: ").append(tempSize).append("\n");

                        tempSize = three.getNumberCount(a, FURNISHING_INDEX, MISSING_INDEX);
                        if (tempSize > 0) {
                            tempNumbers = three.getNumbers(a, FURNISHING_INDEX, MISSING_INDEX);
                            report.append(TAB).append(TAB).append("Faltantes: ").
                                    append(tempSize).
                                    append("\n").append("\n");
                            report.append(Arrays.toString(tempNumbers.toArray())).append("\n").append("\n");
                        }
                        tempSize = three.getNumberCount(a, FURNISHING_INDEX, PRESENT_INDEX);
                        if (tempSize > 0) {
                            tempNumbers = three.getNumbers(a, FURNISHING_INDEX, PRESENT_INDEX);
                            report.append(TAB).append(TAB).append("Presentes: ").
                                    append(tempSize).
                                    append("\n").append("\n");
                            report.append(Arrays.toString(tempNumbers.toArray())).append("\n").append("\n");
                        }
                        tempSize = three.getNumberCount(a, FURNISHING_INDEX, LEFTOVER_INDEX);
                        if (tempSize > 0) {
                            tempNumbers = three.getNumbers(a, FURNISHING_INDEX, LEFTOVER_INDEX);
                            report.append(TAB).append(TAB).append("Sobrantes: ").
                                    append(tempSize).
                                    append("\n").append("\n");
                            report.append(Arrays.toString(tempNumbers.toArray())).append("\n").append("\n");
                        }
                        tempSize = three.getNumberCount(a, FURNISHING_INDEX, IGNORED_MISSING_INDEX);
                        if (tempSize > 0) {
                            tempNumbers = three.getNumbers(a, FURNISHING_INDEX, IGNORED_MISSING_INDEX);
                            report.append(TAB).append(TAB).append("Faltantes Ignorados: ").
                                    append(tempSize).
                                    append("\n").append("\n");
                            report.append(Arrays.toString(tempNumbers.toArray())).append("\n").append("\n");
                        }
                        tempSize = three.getNumberCount(a, FURNISHING_INDEX, LEFTOVER_PRESENT_INDEX);
                        if (tempSize > 0) {
                            tempNumbers = three.getNumbers(a, FURNISHING_INDEX, LEFTOVER_PRESENT_INDEX);
                            report.append(TAB).append(TAB).append("Sobrantes Presentes: ").
                                    append(tempSize).
                                    append("\n").append("\n");
                            report.append(Arrays.toString(tempNumbers.toArray())).append("\n").append("\n");
                        }
                    }

                    tempSize = three.getNumberCount(a, UNKNOWN_INDEX);
                    if (tempSize > 0) {
                        report.append("- ").append(currentArea).append(" -").append("\n");
                        report.append(TAB).append("CON TIPO DESCONOZIDO: ").append(tempSize).append("\n");

                        tempSize = three.getNumberCount(a, UNKNOWN_INDEX, MISSING_INDEX);
                        if (tempSize > 0) {
                            tempNumbers = three.getNumbers(a, UNKNOWN_INDEX, MISSING_INDEX);
                            report.append(TAB).append(TAB).append("Faltantes: ").
                                    append(tempSize).
                                    append("\n").append("\n");
                            report.append(Arrays.toString(tempNumbers.toArray())).append("\n").append("\n");
                        }
                        tempSize = three.getNumberCount(a, UNKNOWN_INDEX, PRESENT_INDEX);
                        if (tempSize > 0) {
                            tempNumbers = three.getNumbers(a, UNKNOWN_INDEX, PRESENT_INDEX);
                            report.append(TAB).append(TAB).append("Presentes: ").
                                    append(tempSize).
                                    append("\n").append("\n");
                            report.append(Arrays.toString(tempNumbers.toArray())).append("\n").append("\n");
                        }
                        tempSize = three.getNumberCount(a, UNKNOWN_INDEX, LEFTOVER_INDEX);
                        if (tempSize > 0) {
                            tempNumbers = three.getNumbers(a, UNKNOWN_INDEX, LEFTOVER_INDEX);
                            report.append(TAB).append(TAB).append("Sobrantes: ").
                                    append(tempSize).
                                    append("\n").append("\n");
                            report.append(Arrays.toString(tempNumbers.toArray())).append("\n").append("\n");
                        }
                        tempSize = three.getNumberCount(a, UNKNOWN_INDEX, IGNORED_MISSING_INDEX);
                        if (tempSize > 0) {
                            tempNumbers = three.getNumbers(a, UNKNOWN_INDEX, IGNORED_MISSING_INDEX);
                            report.append(TAB).append(TAB).append("Faltantes Ignorados: ").
                                    append(tempSize).
                                    append("\n").append("\n");
                            report.append(Arrays.toString(tempNumbers.toArray())).append("\n").append("\n");
                        }
                        tempSize = three.getNumberCount(a, UNKNOWN_INDEX, LEFTOVER_PRESENT_INDEX);
                        if (tempSize > 0) {
                            tempNumbers = three.getNumbers(a, UNKNOWN_INDEX, LEFTOVER_PRESENT_INDEX);
                            report.append(TAB).append(TAB).append("Sobrantes Presentes: ").
                                    append(tempSize).
                                    append("\n").append("\n");
                            report.append(Arrays.toString(tempNumbers.toArray())).append("\n").append("\n");
                        }
                    }

                    report.append("\n");
                }
            }

            report.append(progress);
            publishProgress("Creando inventario: 100%");

            return true;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            textView.setText(values[0]);
        }

        private String getPerCent(long part, int total) {
            return String.valueOf((part * 100) / total) + " %";
        }

        @Override
        protected void onPostExecute(Boolean r) {

            if (r)
                if (report.length() < 5000) {
                    textView.setText(report);
                } else {
                    textView.setText(report.substring(0, 4900) + " ...");
                }
            building = false;
            formatS.setEnabled(true);
        }

        private class DataThree {

            private ArrayList<String> areas;
            private ArrayList<ArrayList<ArrayList<ArrayList<String>>>> three;

            public DataThree() {
                initializeAreas();
                initializeThree();
                putDataIntoThree();
            }

            private void initializeThree() {

                three = new ArrayList<>();

                //Adding Areas
                for (int a = 0; a < areas.size(); a++) {
                    ArrayList<ArrayList<ArrayList<String>>> areaLevelHolderAL = new ArrayList<>();

                    //Adding types (there are 3 types)
                    for (int t = 0; t < TYPE_COUNT; t++) {
                        ArrayList<ArrayList<String>> typeLevelHolderAL = new ArrayList<>();

                        //Adding states (there are 5 states)
                        for (int s = 0; s < STATES_COUNT; s++) {
                            ArrayList<String> stateLevelHolderAL = new ArrayList<>();
                            typeLevelHolderAL.add(stateLevelHolderAL);
                        }

                        areaLevelHolderAL.add(typeLevelHolderAL);
                    }
                    three.add(areaLevelHolderAL);
                }

            }

            private void putDataIntoThree() {

                String number, numberArea;
                int numberType, numberState;
                data.moveToPosition(-1);
                while (data.moveToNext()) {

                    number = data.getString(DB.IT.Indexes.NUMBER_COLUMN_INDEX);
                    numberArea = data.getString(DB.IT.Indexes.AREA_COLUMN_INDEX);
                    numberType = data.getInt(DB.IT.Indexes.TYPE_COLUMN_INDEX);
                    numberState = data.getInt(DB.IT.Indexes.STATE_COLUMN_INDEX);


                    String area;
                    for (int a = 0; a < areas.size(); a++) {
                        area = areas.get(a);
                        //Separating by area
                        if (numberArea.equals(area)) {
                            for (int t = 0; t < TYPE_COUNT; t++) {
                                //Separating by type
                                if (numberType == getTypeValueByIndex(t)) {
                                    for (int s = 0; s < STATES_COUNT; s++) {
                                        //Separating by state
                                        if (numberState == getStateValueByIndex(s)) {
                                            addNumber(a, t, s, number);
                                            progress++;
                                            publishProgress("Creando inventario: " + getPerCent(progress, total));
                                        }
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
            }

            private void initializeAreas() {
                areas = new ArrayList<>();

                data.moveToPosition(-1);
                while (data.moveToNext()) {
                    String area = data.getString(DB.IT.Indexes.AREA_COLUMN_INDEX);
                    if (!areas.contains(area)) {
                        areas.add(area);
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

            private void addNumber(int areaIndex, int typeIndex, int stateIndex, String number) {
                three.get(areaIndex).get(typeIndex).get(stateIndex).add(number);
            }

            public int getNumberCount(int areaIndex, int typeIndex, int stateIndex) {
                progress++;
                publishProgress("Creando inventario: " + getPerCent(progress, total));
                return three.get(areaIndex).get(typeIndex).get(stateIndex).size();
            }

            public ArrayList<String> getNumbers(int areaIndex, int typeIndex, int stateIndex) {
                ArrayList<String> numbers = three.get(areaIndex).get(typeIndex).get(stateIndex);
                progress++;
                publishProgress("Creando inventario: " + getPerCent(progress, total));
                return numbers;
            }

            public int getNumberCount(int areaIndex, int typeIndex) {
                int count = 0;
                for (int s = 0; s < STATES_COUNT; s++) {
                    count += getNumberCount(areaIndex, typeIndex, s);
                }
                return count;
            }

            public ArrayList<String> getNumbers(int areaIndex, int typeIndex) {
                ArrayList<String> toReturn = new ArrayList<>();
                for (int s = 0; s < STATES_COUNT; s++) {
                    ArrayList<String> tempNumbers = getNumbers(areaIndex, typeIndex, s);
                    for (String tn : tempNumbers) {
                        toReturn.add(tn);
                    }
                }
                return toReturn;
            }

            public int getAreaTotalNumberCount(int areaIndex) {
                int count = 0;
                for (int t = 0; t < TYPE_COUNT; t++) {
                    count += getNumberCount(areaIndex, t);
                }
                return count;
            }

            public ArrayList<String> getAreaTotalNumbers(int areaIndex) {
                ArrayList<String> toReturn = new ArrayList<>();
                for (int t = 0; t < TYPE_COUNT; t++) {
                    ArrayList<String> tempNumbers = getNumbers(areaIndex, t);
                    for (String tn : tempNumbers) {
                        toReturn.add(tn);
                    }
                }
                return toReturn;
            }

            public int getStateTotalNumberCountOfAType(int stateIndex, int typeIndex) {
                int count = 0;
                for (int a = 0; a < areas.size(); a++) {
                    count += getNumberCount(a, typeIndex, stateIndex);
                }
                return count;
            }

            public ArrayList<String> getStateNumbersOfAType(int stateIndex, int typeIndex) {
                ArrayList<String> toReturn = new ArrayList<>();
                for (int a = 0; a < areas.size(); a++) {
                    ArrayList<String> tempNumbers = getNumbers(a, typeIndex, stateIndex);
                    for (String tn : tempNumbers) {
                        toReturn.add(tn);
                    }
                }
                return toReturn;
            }

            public int getTypeTotalNumberCount(int typeIndex) {
                int count = 0;
                for (int a = 0; a < areas.size(); a++) {
                    count += getNumberCount(a, typeIndex);
                }
                return count;
            }

            public ArrayList<String> getTypeNumbers(int typeIndex) {
                ArrayList<String> toReturn = new ArrayList<>();
                for (int a = 0; a < areas.size(); a++) {
                    ArrayList<String> tempNumbers = getNumbers(a, typeIndex);
                    for (String tn : tempNumbers) {
                        toReturn.add(tn);
                    }
                }
                return toReturn;
            }

            public String getArea(int areaIndex) {
                return areas.get(areaIndex);
            }

            public int getAreasCount() {
                return areas.size();
            }

        }
    }

    private class MakeShortReportTXTAT extends AsyncTask<Void, String, Boolean> {

        private String TAB = "   ";
        //STATE
        private int MISSING_INDEX = 0;
        private int PRESENT_INDEX = 1;
        private int LEFTOVER_INDEX = 2;
        private int IGNORED_MISSING_INDEX = 3;
        private int LEFTOVER_PRESENT_INDEX = 4;
        private int STATES_COUNT = 5;

        //TYPE
        private int EQUIPMENT_INDEX = 0;
        private int FURNISHING_INDEX = 1;
        private int UNKNOWN_INDEX = 2;
        private int TYPE_COUNT = 3;

        private long progress = 0;
        private int total;

        @Override
        protected void onPreExecute() {
            building = true;
            formatS.setEnabled(false);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            report = new StringBuilder();
            creationDate = Tools.getFormattedDateForFileNaming();

            total = (int) (data.getCount() * 1.3);
            DataThree three = new DataThree();

            if (isCancelled()) {
                report = new StringBuilder();
                return false;
            }


            report.append("REPORTE DE INVENTARIO ").append(creationDate).append("\n").append("\n");
            if (three.getAreasCount() == 1) {
                report.append("--- ").append(three.getArea(0).toUpperCase()).append(" ---").append("\n").append("\n");
            } else {
                report.append("--- DETALLES GENERALES ---").append("\n").append("\n");
            }
            report.append("TOTAL DE NÚMEROS: ").append(data.getCount()).append("\n");

            int tempSize;
            tempSize = three.getTypeTotalNumberCount(EQUIPMENT_INDEX);
            if (tempSize > 0) {
                report.append(TAB).append("EQUIPOS: ").append(tempSize).append("\n");
                tempSize = three.getStateTotalNumberCountOfAType(MISSING_INDEX, EQUIPMENT_INDEX);
                if (tempSize > 0) {
                    report.append(TAB).append(TAB).append("Faltantes: ").
                            append(tempSize).
                            append("\n");
                }
                tempSize = three.getStateTotalNumberCountOfAType(PRESENT_INDEX, EQUIPMENT_INDEX);
                if (tempSize > 0) {
                    report.append(TAB).append(TAB).append("Presentes: ").
                            append(tempSize).
                            append("\n");
                }
                tempSize = three.getStateTotalNumberCountOfAType(LEFTOVER_INDEX, EQUIPMENT_INDEX);
                if (tempSize > 0) {
                    report.append(TAB).append(TAB).append("Sobrantes: ").
                            append(tempSize).
                            append("\n");
                }
                tempSize = three.getStateTotalNumberCountOfAType(IGNORED_MISSING_INDEX, EQUIPMENT_INDEX);
                if (tempSize > 0) {
                    report.append(TAB).append(TAB).append("Faltantes Ignorados: ").
                            append(tempSize).
                            append("\n");
                }
                tempSize = three.getStateTotalNumberCountOfAType(LEFTOVER_PRESENT_INDEX, EQUIPMENT_INDEX);
                if (tempSize > 0) {
                    report.append(TAB).append(TAB).append("Sobrantes Presentes: ").
                            append(tempSize).
                            append("\n");
                }
            }

            tempSize = three.getTypeTotalNumberCount(FURNISHING_INDEX);
            if (tempSize > 0) {
                report.append(TAB).append("MUEBLES: ").append(tempSize).append("\n");
                tempSize = three.getStateTotalNumberCountOfAType(MISSING_INDEX, FURNISHING_INDEX);
                if (tempSize > 0) {
                    report.append(TAB).append(TAB).append("Faltantes: ").
                            append(tempSize).
                            append("\n");
                }
                tempSize = three.getStateTotalNumberCountOfAType(PRESENT_INDEX, FURNISHING_INDEX);
                if (tempSize > 0) {
                    report.append(TAB).append(TAB).append("Presentes: ").
                            append(tempSize).
                            append("\n");
                }
                tempSize = three.getStateTotalNumberCountOfAType(LEFTOVER_INDEX, FURNISHING_INDEX);
                if (tempSize > 0) {
                    report.append(TAB).append(TAB).append("Sobrantes: ").
                            append(tempSize).
                            append("\n");
                }
                tempSize = three.getStateTotalNumberCountOfAType(IGNORED_MISSING_INDEX, FURNISHING_INDEX);
                if (tempSize > 0) {
                    report.append(TAB).append(TAB).append("Faltantes Ignorados: ").
                            append(tempSize).
                            append("\n");
                }
                tempSize = three.getStateTotalNumberCountOfAType(LEFTOVER_PRESENT_INDEX, FURNISHING_INDEX);
                if (tempSize > 0) {
                    report.append(TAB).append(TAB).append("Sobrantes Presentes: ").
                            append(tempSize).
                            append("\n");
                }
            }

            tempSize = three.getTypeTotalNumberCount(UNKNOWN_INDEX);
            if (tempSize > 0) {
                report.append(TAB).append("CON TIPO DESCONOZIDO: ").append(tempSize).append("\n");
                tempSize = three.getStateTotalNumberCountOfAType(MISSING_INDEX, UNKNOWN_INDEX);
                if (tempSize > 0) {
                    report.append(TAB).append(TAB).append("Faltantes: ").
                            append(tempSize).
                            append("\n");
                }
                tempSize = three.getStateTotalNumberCountOfAType(PRESENT_INDEX, UNKNOWN_INDEX);
                if (tempSize > 0) {
                    report.append(TAB).append(TAB).append("Presentes: ").
                            append(tempSize).
                            append("\n");
                }
                tempSize = three.getStateTotalNumberCountOfAType(LEFTOVER_INDEX, UNKNOWN_INDEX);
                if (tempSize > 0) {
                    report.append(TAB).append(TAB).append("Sobrantes: ").
                            append(tempSize).
                            append("\n");
                }
                tempSize = three.getStateTotalNumberCountOfAType(IGNORED_MISSING_INDEX, UNKNOWN_INDEX);
                if (tempSize > 0) {
                    report.append(TAB).append(TAB).append("Faltantes Ignorados: ").
                            append(tempSize).
                            append("\n");
                }
                tempSize = three.getStateTotalNumberCountOfAType(LEFTOVER_PRESENT_INDEX, UNKNOWN_INDEX);
                if (tempSize > 0) {
                    report.append(TAB).append(TAB).append("Sobrantes Presentes: ").
                            append(tempSize).
                            append("\n");
                }
            }

            if (three.getAreasCount() > 1) {

                String currentArea;

                report.append("\n");
                report.append("--- DETALLES POR ÁREAS ---").append("\n").append("\n");
                for (int a = 0; a < three.getAreasCount(); a++) {

                    currentArea = three.getArea(a).toUpperCase();

                    report.append("- ").append(currentArea).append(" -").append("\n");
                    report.append("Total de números: ").append(three.getAreaTotalNumberCount(a)).append("\n");

                    tempSize = three.getNumberCount(a, EQUIPMENT_INDEX);
                    if (tempSize > 0) {
                        report.append(TAB).append("EQUIPOS: ").append(tempSize).append("\n");

                        tempSize = three.getNumberCount(a, EQUIPMENT_INDEX, MISSING_INDEX);
                        if (tempSize > 0) {
                            report.append(TAB).append(TAB).append("Faltantes: ").
                                    append(tempSize).
                                    append("\n");
                        }
                        tempSize = three.getNumberCount(a, EQUIPMENT_INDEX, PRESENT_INDEX);
                        if (tempSize > 0) {
                            report.append(TAB).append(TAB).append("Presentes: ").
                                    append(tempSize).
                                    append("\n");
                        }
                        tempSize = three.getNumberCount(a, EQUIPMENT_INDEX, LEFTOVER_INDEX);
                        if (tempSize > 0) {
                            report.append(TAB).append(TAB).append("Sobrantes: ").
                                    append(tempSize).
                                    append("\n");
                        }
                        tempSize = three.getNumberCount(a, EQUIPMENT_INDEX, IGNORED_MISSING_INDEX);
                        if (tempSize > 0) {
                            report.append(TAB).append(TAB).append("Faltantes Ignorados: ").
                                    append(tempSize).
                                    append("\n");
                        }
                        tempSize = three.getNumberCount(a, EQUIPMENT_INDEX, LEFTOVER_PRESENT_INDEX);
                        if (tempSize > 0) {
                            report.append(TAB).append(TAB).append("Sobrantes Presentes: ").
                                    append(tempSize).
                                    append("\n");
                        }
                    }

                    tempSize = three.getNumberCount(a, FURNISHING_INDEX);
                    if (tempSize > 0) {
                        report.append(TAB).append("MUEBLES: ").append(tempSize).append("\n");

                        tempSize = three.getNumberCount(a, FURNISHING_INDEX, MISSING_INDEX);
                        if (tempSize > 0) {
                            report.append(TAB).append(TAB).append("Faltantes: ").
                                    append(tempSize).
                                    append("\n");
                        }
                        tempSize = three.getNumberCount(a, FURNISHING_INDEX, PRESENT_INDEX);
                        if (tempSize > 0) {
                            report.append(TAB).append(TAB).append("Presentes: ").
                                    append(tempSize).
                                    append("\n");
                        }
                        tempSize = three.getNumberCount(a, FURNISHING_INDEX, LEFTOVER_INDEX);
                        if (tempSize > 0) {
                            report.append(TAB).append(TAB).append("Sobrantes: ").
                                    append(tempSize).
                                    append("\n");
                        }
                        tempSize = three.getNumberCount(a, FURNISHING_INDEX, IGNORED_MISSING_INDEX);
                        if (tempSize > 0) {
                            report.append(TAB).append(TAB).append("Faltantes Ignorados: ").
                                    append(tempSize).
                                    append("\n");
                        }
                        tempSize = three.getNumberCount(a, FURNISHING_INDEX, LEFTOVER_PRESENT_INDEX);
                        if (tempSize > 0) {
                            report.append(TAB).append(TAB).append("Sobrantes Presentes: ").
                                    append(tempSize).
                                    append("\n");
                        }
                    }

                    tempSize = three.getNumberCount(a, UNKNOWN_INDEX);
                    if (tempSize > 0) {
                        report.append(TAB).append("CON TIPO DESCONOZIDO: ").append(tempSize).append("\n");

                        tempSize = three.getNumberCount(a, UNKNOWN_INDEX, MISSING_INDEX);
                        if (tempSize > 0) {
                            report.append(TAB).append(TAB).append("Faltantes: ").
                                    append(tempSize).
                                    append("\n");
                        }
                        tempSize = three.getNumberCount(a, UNKNOWN_INDEX, PRESENT_INDEX);
                        if (tempSize > 0) {
                            report.append(TAB).append(TAB).append("Presentes: ").
                                    append(tempSize).
                                    append("\n");
                        }
                        tempSize = three.getNumberCount(a, UNKNOWN_INDEX, LEFTOVER_INDEX);
                        if (tempSize > 0) {
                            report.append(TAB).append(TAB).append("Sobrantes: ").
                                    append(tempSize).
                                    append("\n");
                        }
                        tempSize = three.getNumberCount(a, UNKNOWN_INDEX, IGNORED_MISSING_INDEX);
                        if (tempSize > 0) {
                            report.append(TAB).append(TAB).append("Faltantes Ignorados: ").
                                    append(tempSize).
                                    append("\n");
                        }
                        tempSize = three.getNumberCount(a, UNKNOWN_INDEX, LEFTOVER_PRESENT_INDEX);
                        if (tempSize > 0) {
                            report.append(TAB).append(TAB).append("Sobrantes Presentes: ").
                                    append(tempSize).
                                    append("\n");
                        }
                    }

                    report.append("\n");
                }
            }

            publishProgress("Creando inventario: 100%");

            return true;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            textView.setText(values[0]);
        }

        private String getPerCent(long part, int total) {
            return String.valueOf((part * 100) / total) + " %";
        }

        @Override
        protected void onPostExecute(Boolean r) {

            if (r)
                if (report.length() < 5000) {
                    textView.setText(report);
                } else {
                    textView.setText(report.substring(0, 4900) + " ...");
                }
            building = false;
            formatS.setEnabled(true);
        }

        private class DataThree {

            private ArrayList<String> areas;
            private ArrayList<ArrayList<ArrayList<ArrayList<String>>>> three;

            public DataThree() {
                initializeAreas();
                initializeThree();
                putDataIntoThree();
            }

            private void initializeThree() {

                three = new ArrayList<>();

                //Adding Areas
                for (int a = 0; a < areas.size(); a++) {
                    ArrayList<ArrayList<ArrayList<String>>> areaLevelHolderAL = new ArrayList<>();

                    //Adding types (there are 3 types)
                    for (int t = 0; t < TYPE_COUNT; t++) {
                        ArrayList<ArrayList<String>> typeLevelHolderAL = new ArrayList<>();

                        //Adding states (there are 5 states)
                        for (int s = 0; s < STATES_COUNT; s++) {
                            ArrayList<String> stateLevelHolderAL = new ArrayList<>();
                            typeLevelHolderAL.add(stateLevelHolderAL);
                        }

                        areaLevelHolderAL.add(typeLevelHolderAL);
                    }
                    three.add(areaLevelHolderAL);
                }

            }

            private void putDataIntoThree() {

                String number, numberArea;
                int numberType, numberState;
                data.moveToPosition(-1);
                while (data.moveToNext()) {

                    number = data.getString(DB.IT.Indexes.NUMBER_COLUMN_INDEX);
                    numberArea = data.getString(DB.IT.Indexes.AREA_COLUMN_INDEX);
                    numberType = data.getInt(DB.IT.Indexes.TYPE_COLUMN_INDEX);
                    numberState = data.getInt(DB.IT.Indexes.STATE_COLUMN_INDEX);


                    String area;
                    for (int a = 0; a < areas.size(); a++) {
                        area = areas.get(a);
                        //Separating by area
                        if (numberArea.equals(area)) {
                            for (int t = 0; t < TYPE_COUNT; t++) {
                                //Separating by type
                                if (numberType == getTypeValueByIndex(t)) {
                                    for (int s = 0; s < STATES_COUNT; s++) {
                                        //Separating by state
                                        if (numberState == getStateValueByIndex(s)) {
                                            addNumber(a, t, s, number);
                                            progress++;
                                            publishProgress("Creando inventario: " + getPerCent(progress, total));
                                        }
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
            }

            private void initializeAreas() {
                areas = new ArrayList<>();

                data.moveToPosition(-1);
                while (data.moveToNext()) {
                    String area = data.getString(DB.IT.Indexes.AREA_COLUMN_INDEX);
                    if (!areas.contains(area)) {
                        areas.add(area);
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

            private void addNumber(int areaIndex, int typeIndex, int stateIndex, String number) {
                three.get(areaIndex).get(typeIndex).get(stateIndex).add(number);
            }

            public int getNumberCount(int areaIndex, int typeIndex, int stateIndex) {
                progress++;
                publishProgress("Creando inventario: " + getPerCent(progress, total));
                return three.get(areaIndex).get(typeIndex).get(stateIndex).size();
            }

            public int getNumberCount(int areaIndex, int typeIndex) {
                int count = 0;
                for (int s = 0; s < STATES_COUNT; s++) {
                    count += getNumberCount(areaIndex, typeIndex, s);
                }
                return count;
            }

            public int getAreaTotalNumberCount(int areaIndex) {
                int count = 0;
                for (int t = 0; t < TYPE_COUNT; t++) {
                    count += getNumberCount(areaIndex, t);
                }
                return count;
            }

            public int getStateTotalNumberCountOfAType(int stateIndex, int typeIndex) {
                int count = 0;
                for (int a = 0; a < areas.size(); a++) {
                    count += getNumberCount(a, typeIndex, stateIndex);
                }
                return count;
            }

            public int getTypeTotalNumberCount(int typeIndex) {
                int count = 0;
                for (int a = 0; a < areas.size(); a++) {
                    count += getNumberCount(a, typeIndex);
                }
                return count;
            }

            public String getArea(int areaIndex) {
                return areas.get(areaIndex);
            }

            public int getAreasCount() {
                return areas.size();
            }

        }
    }

}
