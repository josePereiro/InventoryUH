package com.example.compereirowww.inventory20181.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.compereirowww.inventory20181.DataBase.DB;
import com.example.compereirowww.inventory20181.R;
import com.example.compereirowww.inventory20181.Tools.Tools;

import java.util.ArrayList;

import static com.example.compereirowww.inventory20181.Activities.AppStatics.QR_DECODER_REQUEST;

public class SearchActivity extends AppCompatActivity {

    //GUI
    private EditText criteriaET;
    private ListView resultLV;
    FloatingActionButton fab;
    private static final int MAX_RESULT_TO_SHOW = 50;
    private static final String HIGHLIGHT = "\"";
    Cursor searchResultCursor;

    //search
    String[] criteria;
    String[] columnsToSearchIn;


    //DB
    DB db;

    //checkboxes
    private ArrayList<CheckBox> checkBoxes;
    private final static int NUMBER_CB_INDEX = 0;
    private final static int DESCRIPTION_CB_INDEX = 1;
    private final static int AREA_CB_INDEX = 2;
    private final static int OBSERVATION_CB_INDEX = 3;
    private final static int LOCATION_CB_INDEX = 4;
    private final static int STATE_CB_INDEX = 5;
    private final static int TYPE_CB_INDEX = 6;
    private final static int FOLLOWING_CB_INDEX = 7;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Buscar");

        //DB
        db = AppStatics.db;

        //GUI
        AppStatics.formatView((TextView) findViewById(R.id.textView));
        AppStatics.formatView((TextView) findViewById(R.id.textView2));
        resultLV = (ListView) findViewById(R.id.listView);
        resultLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String clickedItem = (String) adapterView.getItemAtPosition(i);
                db.setPreference(DB.PT.PNames.NUMBER_TO_EDIT,
                        extractNumberFromDisplayedData(clickedItem));
                db.setPreference(DB.PT.PNames.TEMP_NUMBER, DB.PT.PDefaultValues.EMPTY_PREFERENCE);
                startActivity(new Intent(SearchActivity.this, EditActivity.class));
            }
        });
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new SearchAT().execute();

            }
        });
        criteriaET = (EditText) findViewById(R.id.criteria_et);
        AppStatics.formatView(criteriaET);
        criteriaET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                setCriteriaPreference(editable.toString());
            }
        });
        criteriaET.setText(getCriteriaPreference());
        checkBoxes = new ArrayList<>();
        checkBoxes.add((CheckBox) findViewById(R.id.number_cb));
        checkBoxes.add((CheckBox) findViewById(R.id.description_cb));
        checkBoxes.add((CheckBox) findViewById(R.id.area_cb));
        checkBoxes.add((CheckBox) findViewById(R.id.observation_cb));
        checkBoxes.add((CheckBox) findViewById(R.id.location_cb));
        checkBoxes.add((CheckBox) findViewById(R.id.state_cb));
        checkBoxes.add((CheckBox) findViewById(R.id.type_cb));
        checkBoxes.add((CheckBox) findViewById(R.id.following_cb));
        for (CheckBox cb : checkBoxes)
            AppStatics.formatView(cb);

    }

    @Override
    protected void onResume() {
        super.onResume();

        updateSelectedCheckBoxes();
        setCriteriaPreference(criteriaET.getText().toString());

        new SearchAT().execute();
    }

    @Override
    protected void onStop() {
        super.onStop();

        updateSelectedCheckBoxesPreference();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.report) {
            callReportActivity();
            return super.onOptionsItemSelected(item);
        }
        if (id == R.id.search_by_qr) {
            callQRDecoder(QR_DECODER_REQUEST);
            return super.onOptionsItemSelected(item);
        }
        if (id == R.id.export) {
            callExportInventoryActivity();
            return super.onOptionsItemSelected(item);
        }
        if (id == R.id.qr_factory) {
            callQrFactory();
            return super.onOptionsItemSelected(item);
        }
        if (id == R.id.help) {
            Tools.showToast(SearchActivity.this, "No hay ayuda!!! Por ahora...", false);
            return super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == QR_DECODER_REQUEST) {
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra("SCAN_RESULT");

                if (db.numberExist(result)) {

                    if (db.getNumberState(result) != DB.IT.StateValues.LEFTOVER &&
                            db.getNumberState(result) != DB.IT.StateValues.LEFTOVER_PRESENT) {

                        db.updateState(result, DB.IT.StateValues.PRESENT);
                        db.updateLastChecking(result, Tools.getDate());
                        Tools.showToast(SearchActivity.this,
                                "El número ha sido marcado como " +
                                        DB.IT.StateValues.toString(DB.IT.StateValues.PRESENT), false);

                    } else {
                        db.updateState(result, DB.IT.StateValues.LEFTOVER_PRESENT);
                        db.updateLastChecking(result, Tools.getDate());
                        Tools.showToast(SearchActivity.this,
                                "El número ha sido marcado como " +
                                        DB.IT.StateValues.toString(DB.IT.StateValues.LEFTOVER_PRESENT), false);
                    }

                    showNumberDetailDialog(result);

                } else {
                    Tools.showToast(SearchActivity.this, "Ningún número válido leído!", false);
                }


            }
        }

    }

    private void showNumberDetailDialog(final String number) {
        String ms = "Número: " + number + "\n";
        Cursor numberData = db.getAllNumberData(number);
        numberData.moveToNext();
        ms += "Description: " + numberData.getString(DB.IT.Indexes.DESCRIPTION_COLUMN_INDEX) + "\n";
        ms += "Área: " + numberData.getString(DB.IT.Indexes.AREA_COLUMN_INDEX) + "\n";
        ms += "Estado: " +
                DB.IT.StateValues.toString(Integer.
                        parseInt(numberData.getString(DB.IT.Indexes.STATE_COLUMN_INDEX))) + "\n";
        ms += "Tipo: " +
                DB.IT.TypeValues.toString(Integer.
                        parseInt(numberData.getString(DB.IT.Indexes.TYPE_COLUMN_INDEX))) + "\n";
        ms += "Localización: " + numberData.getString(DB.IT.Indexes.LOCATION_COLUMN_INDEX) + "\n";
        ms += "Observación: " + numberData.getString(DB.IT.Indexes.OBSERVATION_COLUMN_INDEX) + "\n";
        numberData.close();

        Tools.showDialogMessage(SearchActivity.this, ms, "Editar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                callEditActivity(number);
            }
        }, "Continuar", null);
    }

    private void callEditActivity(String number) {
        db.setPreference(DB.PT.PNames.NUMBER_TO_EDIT, number);
        db.setPreference(DB.PT.PNames.TEMP_NUMBER, DB.PT.PDefaultValues.EMPTY_PREFERENCE);
        startActivity(new Intent(SearchActivity.this, EditActivity.class));
    }

    private void callQrFactory() {
        if (searchResultCursor == null || searchResultCursor.getCount() == 0) {
            Tools.showToast(SearchActivity.this, "No hay números seleccionados!!!", false);
        } else {
            PrintableQRsFactoryActivity.setData(searchResultCursor);
            PrintableQRsFactoryActivity.setImporting(false);
            db.setPreference(DB.PT.PNames.P_QR_CURRENT_INDEX, "0");
            startActivity(new Intent(SearchActivity.this, PrintableQRsFactoryActivity.class));
        }
    }

    private void callReportActivity() {
        if (searchResultCursor == null || searchResultCursor.getCount() == 0) {
            Tools.showToast(SearchActivity.this, "No hay números seleccionados!!!", false);
        } else {
            ReportActivity.setData(searchResultCursor);
            startActivity(new Intent(SearchActivity.this, ReportActivity.class));
        }
    }

    private void callExportInventoryActivity() {
        if (searchResultCursor == null || searchResultCursor.getCount() == 0) {
            Tools.showToast(SearchActivity.this, "No hay números seleccionados!!!", false);
        } else {
            BackUpActivity.setData(searchResultCursor);
            startActivity(new Intent(SearchActivity.this, BackUpActivity.class));
        }
    }

    private void callQRDecoder(int requestCode) {

        try {
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes
            startActivityForResult(intent, requestCode);

        } catch (Exception e) {

            Toast.makeText(SearchActivity.this, "Error: Debe instalar primero la aplicación " +
                            "de escanear código QR: com.google.zxing.client.android-4.7.3-103-minAPI15",
                    Toast.LENGTH_LONG).show();
        }
    }

    private String extractNumberFromDisplayedData(String displayedData) {
        return displayedData.split("\n", -1)[0].replaceAll("Número: ", "").
                replaceAll(HIGHLIGHT, "").replaceAll(HIGHLIGHT, "");
    }

    private void updateSelectedCheckBoxesPreference() {

        String selectedCheckBoxesCSV = "";
        for (int i = 0; i < checkBoxes.size(); i++) {
            if (checkBoxes.get(i).isChecked()) {
                if (i < checkBoxes.size() - 1) {
                    selectedCheckBoxesCSV += i + ",";
                } else {
                    selectedCheckBoxesCSV += i;
                }
            }
        }

        db.setPreference(DB.PT.PNames.SELECTED_CHECKBOXES, selectedCheckBoxesCSV);

    }

    private void updateSelectedCheckBoxes() {

        ArrayList<Integer> selectedCBs = getSelectedCheckBoxesFromPreference();
        for (int i : selectedCBs) {
            if (i < checkBoxes.size()) {
                checkBoxes.get(i).setChecked(true);
            }
        }

    }

    private ArrayList<Integer> getSelectedCheckBoxesFromPreference() {

        ArrayList<Integer> toReturn = new ArrayList<>();
        String[] selectedCheckBoxes = db.getPreference(DB.PT.PNames.SELECTED_CHECKBOXES).split(",", -1);
        for (String s : selectedCheckBoxes) {
            if (s != null && !s.equals("")) {
                toReturn.add(Integer.parseInt(s));
            }
        }

        return toReturn;
    }

    private void setCriteriaPreference(String criteria) {
        db.setPreference(DB.PT.PNames.TEMP_UPDATE_CRITERIA, criteria);
    }

    private String getCriteriaPreference() {
        return db.getPreference(DB.PT.PNames.TEMP_UPDATE_CRITERIA);
    }

    private class SearchAT extends AsyncTask<Void, Void, Cursor> {

        boolean search;

        @Override
        protected void onPreExecute() {
            disableGUI();
            search = preSearch();
        }

        @Override
        protected Cursor doInBackground(Void... voids) {

            if (search) return search();
            return null;
        }

        @Override
        protected void onPostExecute(Cursor data) {


            displayListViewData(data);
            enableGUI();
        }

        private void enableGUI() {
            resultLV.setEnabled(true);
            criteriaET.setEnabled(true);
            fab.setEnabled(true);
        }

        private boolean preSearch() {
            updateSelectedCheckBoxesPreference();
            updateCriteria();
            if (criteria == null) {
                Tools.showToast(SearchActivity.this,
                        "No ha establecido ningún criterio de búsqueda!!", false);
                displayListViewData(null);
                enableGUI();
                return false;
            }
            updateColumnsToSearchIn();
            if (columnsToSearchIn == null) {
                Tools.showToast(SearchActivity.this,
                        "No ha seleccionado en donde buscar!!", false);
                displayListViewData(null);
                enableGUI();
                return false;
            }
            parseCriteriaIfNeeded();
            return true;
        }

        private Cursor search() {
            if (searchResultCursor != null) searchResultCursor.close();
            searchResultCursor = db.getNumbersDataThatContain(columnsToSearchIn, criteria);
            return searchResultCursor;
        }

        private void disableGUI() {
            resultLV.setEnabled(false);
            criteriaET.setEnabled(false);
            fab.setEnabled(false);
        }

        private void displayListViewData(Cursor data) {

            if (data != null) {
                AppStatics.formatView(SearchActivity.this, formatSearchResultAndReturnsAsList(data), resultLV);
                if (searchResultCursor.getCount() > MAX_RESULT_TO_SHOW)
                    Tools.showToast(SearchActivity.this, searchResultCursor.getCount() + " resultados encontrados " +
                            MAX_RESULT_TO_SHOW + " mostrados", false);
                else
                    Tools.showToast(SearchActivity.this, searchResultCursor.getCount() + " resultados encontrados", false);
            } else {
                AppStatics.formatView(SearchActivity.this, new ArrayList<String>(), resultLV);
            }
        }

        private ArrayList<String> formatSearchResultAndReturnsAsList(Cursor searchResult) {

            ArrayList<String> formattedSearchResults = new ArrayList<>();
            StringBuilder temp;
            int c = 0;
            String number;
            while (searchResult.moveToNext() && c < MAX_RESULT_TO_SHOW) {

                temp = new StringBuilder();
                number = searchResult.getString(0);

                if (!Tools.contain(columnsToSearchIn, DB.IT.ITNames.NUMBER_COLUMN_NAME)) {
                    temp.append("Número: ").
                            append(number).
                            append("\n");
                }

                for (int i = 0; i < criteria.length && i < columnsToSearchIn.length; i++) {

                    //Number
                    if (columnsToSearchIn[i].equals(DB.IT.ITNames.NUMBER_COLUMN_NAME)) {
                        temp.append("Número: ").
                                append(highlightMatch(number, criteria[i])).
                                append("\n");
                    }

                    //Description
                    if (columnsToSearchIn[i].equals(DB.IT.ITNames.DESCRIPTION_COLUMN_NAME)) {
                        temp.append("Descripción: ").
                                append(highlightMatch(db.getNumberDescription(number), criteria[i])).
                                append("\n");
                    }

                    //Area
                    if (columnsToSearchIn[i].equals(DB.IT.ITNames.AREA_COLUMN_NAME)) {
                        temp.append("Área: ").
                                append(highlightMatch(db.getNumberArea(number), criteria[i])).
                                append("\n");
                    }

                    //Location
                    if (columnsToSearchIn[i].equals(DB.IT.ITNames.LOCATION_COLUMN_NAME)) {
                        temp.append("Localización: ").
                                append(highlightMatch(db.getNumberLocation(number), criteria[i])).
                                append("\n");
                    }

                    //Observation
                    if (columnsToSearchIn[i].equals(DB.IT.ITNames.OBSERVATION_COLUMN_NAME)) {
                        temp.append("Observación: ").
                                append(highlightMatch(db.getNumberObservation(number), criteria[i])).
                                append("\n");
                    }

                    //State
                    if (columnsToSearchIn[i].equals(DB.IT.ITNames.STATE_COLUMN_NAME)) {
                        temp.append("Estado: ").
                                append(HIGHLIGHT).
                                append(DB.IT.StateValues.toString(db.getNumberState(number))).
                                append(HIGHLIGHT).
                                append("\n");
                    }

                    //Type
                    if (columnsToSearchIn[i].equals(DB.IT.ITNames.TYPE_COLUMN_NAME)) {
                        temp.append("Tipo: ").
                                append(HIGHLIGHT).
                                append(DB.IT.TypeValues.toString(db.getNumberType(number))).
                                append(HIGHLIGHT).
                                append("\n");
                    }

                    //Type
                    if (columnsToSearchIn[i].equals(DB.IT.ITNames.FOLLOWING_COLUMN_NAME)) {
                        temp.append("En seguimineto: ").
                                append(HIGHLIGHT).
                                append(DB.IT.FollowingValues.toString(db.getNumberFollowingValue(number))).
                                append(HIGHLIGHT);
                    }

                }
                formattedSearchResults.add(temp.toString());

                c++;
            }

            return formattedSearchResults;
        }

        private void parseCriteriaIfNeeded() {

            for (int i = 0; i < columnsToSearchIn.length && i < criteria.length; i++) {
                if (columnsToSearchIn[i].equals(DB.IT.ITNames.STATE_COLUMN_NAME)) {
                    if (criteria[i].toUpperCase().equals("P")) {
                        criteria[i] = DB.IT.StateValues.PRESENT + "";
                    } else if (criteria[i].toUpperCase().equals("S")) {
                        criteria[i] = DB.IT.StateValues.LEFTOVER + "";
                    } else if (criteria[i].toUpperCase().equals("F")) {
                        criteria[i] = DB.IT.StateValues.MISSING + "";
                    } else if (criteria[i].toUpperCase().equals("SP")) {
                        criteria[i] = DB.IT.StateValues.LEFTOVER_PRESENT + "";
                    } else if (criteria[i].toUpperCase().equals("FI")) {
                        criteria[i] = DB.IT.StateValues.IGNORED_MISSING + "";
                    } else {
                        int state = DB.IT.StateValues.parse(criteria[i]);
                        criteria[i] = String.valueOf(state);
                    }
                } else if (columnsToSearchIn[i].equals(DB.IT.ITNames.TYPE_COLUMN_NAME)) {
                    if (criteria[i].toUpperCase().equals("E")) {
                        criteria[i] = DB.IT.TypeValues.EQUIPMENT + "";
                    } else if (criteria[i].toUpperCase().equals("M")) {
                        criteria[i] = DB.IT.TypeValues.FURNISHING + "";
                    } else if (criteria[i].toUpperCase().equals("D")) {
                        criteria[i] = DB.IT.TypeValues.UNKNOWN + "";
                    } else {
                        int typeValue = DB.IT.TypeValues.parse(criteria[i]);
                        criteria[i] = String.valueOf(typeValue);
                    }
                } else if (columnsToSearchIn[i].equals(DB.IT.ITNames.FOLLOWING_COLUMN_NAME)) {
                    if (criteria[i].toUpperCase().equals("S")) {
                        criteria[i] = DB.IT.FollowingValues.YES + "";
                    } else if (criteria[i].toUpperCase().equals("N")) {
                        criteria[i] = DB.IT.FollowingValues.NO + "";
                    } else {
                        int followingValue = DB.IT.FollowingValues.parse(criteria[i]);
                        criteria[i] = String.valueOf(followingValue);
                    }
                }
            }

        }

        private String highlightMatch(String text, String match) {
            return text.toUpperCase().replaceAll(match.toUpperCase(), HIGHLIGHT + match.toUpperCase() + HIGHLIGHT);
        }

        private void updateCriteria() {

            //checking criteria string
            String c = criteriaET.getText().toString();
            if (c.equals("")) {
                criteria = null;
                return;
            }
            int criteriaLength = -1;
            while (criteriaLength != c.length()) {
                criteriaLength = c.length();
                c = c.replaceAll(" ,", ",");
                c = c.replaceAll(", ", ",");
            }

            criteriaET.setText(c);
            criteria = c.split(",", -1);
        }

        private void updateColumnsToSearchIn() {

            ArrayList<String> columnsToSearch = new ArrayList<>();
            if (checkBoxes.get(NUMBER_CB_INDEX).isChecked()) {
                columnsToSearch.add(DB.IT.ITNames.NUMBER_COLUMN_NAME);
            }
            if (checkBoxes.get(DESCRIPTION_CB_INDEX).isChecked()) {
                columnsToSearch.add(DB.IT.ITNames.DESCRIPTION_COLUMN_NAME);
            }
            if (checkBoxes.get(AREA_CB_INDEX).isChecked()) {
                columnsToSearch.add(DB.IT.ITNames.AREA_COLUMN_NAME);
            }
            if (checkBoxes.get(OBSERVATION_CB_INDEX).isChecked()) {
                columnsToSearch.add(DB.IT.ITNames.OBSERVATION_COLUMN_NAME);
            }
            if (checkBoxes.get(LOCATION_CB_INDEX).isChecked()) {
                columnsToSearch.add(DB.IT.ITNames.LOCATION_COLUMN_NAME);
            }
            if (checkBoxes.get(STATE_CB_INDEX).isChecked()) {
                columnsToSearch.add(DB.IT.ITNames.STATE_COLUMN_NAME);
            }
            if (checkBoxes.get(TYPE_CB_INDEX).isChecked()) {
                columnsToSearch.add(DB.IT.ITNames.TYPE_COLUMN_NAME);
            }
            if (checkBoxes.get(FOLLOWING_CB_INDEX).isChecked()) {
                columnsToSearch.add(DB.IT.ITNames.FOLLOWING_COLUMN_NAME);
            }

            if (columnsToSearch.size() == 0) {
                columnsToSearchIn = null;
                return;
            }
            columnsToSearchIn = columnsToSearch.toArray(new String[]{""});
        }
    }

}
