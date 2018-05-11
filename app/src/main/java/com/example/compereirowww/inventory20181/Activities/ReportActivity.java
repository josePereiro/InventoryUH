package com.example.compereirowww.inventory20181.Activities;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.compereirowww.inventory20181.DataBase.DB;
import com.example.compereirowww.inventory20181.R;
import com.example.compereirowww.inventory20181.Tools.Tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static com.example.compereirowww.inventory20181.Activities.AppStatics.APP_TAG;
import static com.example.compereirowww.inventory20181.Activities.AppStatics.db;

public class ReportActivity extends AppCompatActivity {


    //GUI
    TextView textView;
    Spinner formatS;

    //data
    private static Cursor data;
    StringBuilder report;
    static int selectedFormat = 0;
    String creationDate;
    boolean building;

    private static String F_S_TEX = "Texto";
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
                    String rName;
                    if (selectedFormat == 2) {
                        rName = "Reporte " + " (" + data.getCount() + "ns) " + creationDate + ".csv";
                    } else {
                        rName = "Reporte " + " (" + data.getCount() + "ns) " + creationDate + ".txt";
                    }
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
        AppStatics.formatView((TextView) findViewById(R.id.textView2));
        textView = (TextView) findViewById(R.id.textView);
        AppStatics.formatView(textView);
        formatS = (Spinner) findViewById(R.id.format_s);
        AppStatics.formatView(ReportActivity.this, new String[]{F_S_TEX, F_CSV}, formatS);
        formatS.setSelection(selectedFormat);
        formatS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedFormat = i;
                if (asyncTask != null) {
                    asyncTask.cancel(true);
                }
                if (i == 0) {
                    asyncTask = new MakeReportTXTAT();
                    asyncTask.execute();
                } else if (i == 1) {
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

    private class MakeReportTXTAT extends AsyncTask<Void, String, Boolean> {

        private String TAB = "   ";
        ReportActivity.DataThree three;

        @Override
        protected void onPreExecute() {
            building = true;
            formatS.setEnabled(false);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            initialising();

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
            building = false;
            formatS.setEnabled(true);
        }

        private void initialising() {
            report = new StringBuilder();
            creationDate = Tools.getFormattedDateForFileNaming();
            three = new DataThree(data) {
                @Override
                public boolean onThreeProgressUpdate(long progress) {

                    int total = (int) (data.getCount() * 2.1);

                    publishProgress("Creando Reporte... " + getPerCent(progress,total));
                    //publishProgress(progress + "");

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

            report.append("REPORTE DEL INVENTARIO ");
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
            report.append("TOTAL DE NÚMEROS: ");
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
            report.append("AREAS ANALIZADAS: ");
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

        private String getPerCent(long part, int total) {
            return String.valueOf((part * 100) / total) + " %";
        }

    }

    public abstract class DataThree {

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

                            if (!updateProgress()) return;

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
                    if (!updateProgress(100)) return;
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
                            if (!updateProgress(100)) return;
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

        private boolean updateProgress(int progressToAdd) {

            progress += progressToAdd;
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
