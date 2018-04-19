package com.example.compereirowww.inventory20181.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

import com.example.compereirowww.inventory20181.DataBase.DB;
import com.example.compereirowww.inventory20181.R;
import com.example.compereirowww.inventory20181.Tools.Tools;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.ArrayList;
import java.util.Hashtable;

public class SearchActivity extends AppCompatActivity {

    //GUI
    private EditText criteriaET;
    private ListView resultLV;
    private static final int MAX_RESULT_TO_SHOW = 50;
    private static final String HIGHLIGHT = "\"";

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //DB
        db = AppStatics.db;

        //GUI
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
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                disableGUI();
                performSearchProcess();
                enableGUI();

            }
        });
        criteriaET = (EditText) findViewById(R.id.criteria_et);
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

    }

    @Override
    protected void onResume() {
        super.onResume();

        updateSelectedCheckBoxes();
        setCriteriaPreference(criteriaET.getText().toString());

    }

    @Override
    protected void onStop() {
        super.onStop();

        updateSelectedCheckBoxesPreference();
    }

    private String getCriteriaPreference() {
        return db.getPreference(DB.PT.PNames.TEMP_UPDATE_CRITERIA);
    }

    private void setCriteriaPreference(String criteria) {
        db.setPreference(DB.PT.PNames.TEMP_UPDATE_CRITERIA, criteria);
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
                            append(db.getNumberState(number)).
                            append(HIGHLIGHT).
                            append("\n");
                }

                //Type
                if (columnsToSearchIn[i].equals(DB.IT.ITNames.TYPE_COLUMN_NAME)) {
                    temp.append("Tipo: ").
                            append(HIGHLIGHT).
                            append(db.getNumberType(number)).
                            append(HIGHLIGHT).
                            append("\n");
                }

            }
            temp.deleteCharAt(temp.length() - 1);//last "\n"
            formattedSearchResults.add(temp.toString());

            c++;
        }

        return formattedSearchResults;
    }

    private String highlightMatch(String text, String match) {
        return text.toUpperCase().replaceAll(match.toUpperCase(), HIGHLIGHT + match.toUpperCase() + HIGHLIGHT);
    }

    private void parseCriteriaIfNeeded()   {

        for (int i = 0; i < columnsToSearchIn.length && i < criteria.length; i++) {
            if (columnsToSearchIn[i].equals(DB.IT.ITNames.STATE_COLUMN_NAME)) {
                int state = DB.IT.StateValues.parse(criteria[i]);
                if (state != -1) {
                    criteria[i] = String.valueOf(state);
                }
            } else if (columnsToSearchIn[i].equals(DB.IT.ITNames.FOLLOWING_COLUMN_NAME)) {
                int followingValue = DB.IT.FollowingValues.parse(criteria[i]);
                if (followingValue != -1) {
                    criteria[i] = String.valueOf(followingValue);
                }
            } else if (columnsToSearchIn[i].equals(DB.IT.ITNames.TYPE_COLUMN_NAME)) {
                int typeValue = DB.IT.TypeValues.parse(criteria[i]);
                if (typeValue != -1) {
                    criteria[i] = String.valueOf(typeValue);
                }
            }
        }

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

        if (columnsToSearch.size() == 0) {
            columnsToSearchIn = null;
            return;
        }
        columnsToSearchIn = columnsToSearch.toArray(new String[]{""});
    }

    private void disableGUI() {
        resultLV.setEnabled(false);
        criteriaET.setEnabled(false);
    }

    private void enableGUI() {
        resultLV.setEnabled(true);
        criteriaET.setEnabled(true);
    }

    private void performSearchProcess() {


        updateSelectedCheckBoxesPreference();
        updateCriteria();
        if (criteria == null) {
            Tools.showToast(SearchActivity.this,
                    "No ha establecido ningún criterio de búsqueda!!", false);
            displayListViewData(new ArrayList<String>());
            enableGUI();
            return;
        }
        updateColumnsToSearchIn();
        if (columnsToSearchIn == null) {
            Tools.showToast(SearchActivity.this,
                    "No ha seleccionado en donde buscar!!", false);
            displayListViewData(new ArrayList<String>());
            enableGUI();
            return;
        }
        parseCriteriaIfNeeded();
        Cursor searchResultCursor = db.getNumbersDataThatContain(columnsToSearchIn, criteria);
        ArrayList<String> searchResultsAL = formatSearchResultAndReturnsAsList(searchResultCursor);
        displayListViewData(searchResultsAL);
        Tools.showToast(SearchActivity.this, searchResultCursor.getCount() + " resultados encontrados " +
                searchResultsAL.size() + " mostrados", false);
        searchResultCursor.close();
    }

    private void displayListViewData(ArrayList<String> data) {
        resultLV.setAdapter(new ArrayAdapter<>(SearchActivity.this, android.R.layout.simple_list_item_1,
                data));
    }

    private String extractNumberFromDisplayedData(String displayedData) {
        return displayedData.split("\n", -1)[0].replaceAll("Número: ", "").
                replaceAll(HIGHLIGHT, "").replaceAll(HIGHLIGHT, "");
    }




}
