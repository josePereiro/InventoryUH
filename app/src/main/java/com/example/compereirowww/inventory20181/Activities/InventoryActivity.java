package com.example.compereirowww.inventory20181.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.compereirowww.inventory20181.DataBase.DB;
import com.example.compereirowww.inventory20181.DataBase.DB.IT;
import com.example.compereirowww.inventory20181.R;
import com.example.compereirowww.inventory20181.Tools.Tools;

import java.util.ArrayList;
import java.util.Arrays;

public class InventoryActivity extends AppCompatActivity {

    //Statics
    private static final int w = 10;

    //Filters values
    public class FiltersValues {

        public class Filter1 {

            public static final String ALL = "Todo";
            public static final String FOLLOWING = "En seguimiento";
            public static final String AREA_HEAD_ = "Area: ";
            public static final String LOCATION_HEAD_ = "Localización: ";
            public static final String LOCATION_EMPTY = "Localización vacía";
        }

        public class Filter2 {

            public static final String ALL = "Todo";
            public static final String STATE_ = "Estado: ";
            public static final String TYPE_ = "Tipo: ";
            public static final String OBSERVATION_ = "Observación: ";
            public static final String OBSERVATION_EMPTY = "Observación vacía";

        }
    }

    //GUI
    TextView textView;
    ListView listView;
    Spinner filter1Spinner;
    Spinner filter2Spinner;
    Button bBtn, bbBtn, fBtn, ffBtn;

    //ListView
    Cursor data;
    ArrayList<String> dataToDisplay;


    //DB
    private DB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //GUI
        textView = (TextView) findViewById(R.id.textView2);
        listView = (ListView) findViewById(R.id.listView);
        filter1Spinner = (Spinner) findViewById(R.id.spinner2);
        filter2Spinner = (Spinner) findViewById(R.id.spinner3);
        bBtn = (Button) findViewById(R.id.b_btn);
        bBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (getIndex() - w >= 0) {

                    setIndex(getIndex() - w);

                    updateDataToDisplay();
                    displayData();


                }

            }
        });
        bbBtn = (Button) findViewById(R.id.bb_btn);
        bbBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getIndex() - w * w >= 0) {
                    setIndex(getIndex() - w * w);

                    updateDataToDisplay();
                    displayData();
                }

            }
        });
        fBtn = (Button) findViewById(R.id.f_btn);
        fBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getIndex() + w < data.getCount()) {
                    setIndex(getIndex() + w);

                    updateDataToDisplay();
                    displayData();
                }
            }
        });
        ffBtn = (Button) findViewById(R.id.ff_btn);
        ffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getIndex() + w * w < data.getCount()) {
                    setIndex(getIndex() + w * w);

                    updateDataToDisplay();
                    displayData();
                }
            }
        });

        //DB
        db = AppStatics.db;

    }

    @Override
    protected void onResume() {
        super.onResume();

        //GUI
        //region filter1Spinner...
        ArrayList<String> filter1AL = new ArrayList<>();
        filter1AL.add(FiltersValues.Filter1.ALL);
        filter1AL.add(FiltersValues.Filter1.FOLLOWING);
        filter1AL.add(FiltersValues.Filter1.LOCATION_EMPTY);
        if (!Arrays.equals(AppStatics.Location.locations, new String[]{""})) {
            for (int i = 0; i < AppStatics.Location.locations.length; i++) {
                filter1AL.add(FiltersValues.Filter1.LOCATION_HEAD_ +
                        AppStatics.Location.locations[i]);
            }
        }
        if (!Arrays.equals(AppStatics.Area.areas, new String[]{""})) {
            for (int i = 0; i < AppStatics.Area.areas.length; i++) {
                filter1AL.add(FiltersValues.Filter1.AREA_HEAD_ +
                        AppStatics.Area.areas[i]);
            }
        }
        filter1Spinner.setAdapter(new ArrayAdapter<>(InventoryActivity.this,
                android.R.layout.simple_list_item_1, filter1AL));
        filter1Spinner.setSelection(Tools.getIndexOf(filter1AL, getFilter1()));
        filter1Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = (String) adapterView.getSelectedItem();
                if (!getFilter1().equals(selectedItem)) {
                    setFilter1(selectedItem);
                    //setIndex(0);
                    //updateData();
                    //updateDataToDisplay();
                    //displayData();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //endregion filter1Spinner...

        //region filter2Spinner...

        ArrayList<String> filter2AL = new ArrayList<>();
        filter2AL.add(FiltersValues.Filter2.ALL);
        filter2AL.add(FiltersValues.Filter2.STATE_ + IT.StateValues.toString(DB.IT.StateValues.MISSING));
        filter2AL.add(FiltersValues.Filter2.STATE_ + IT.StateValues.toString(IT.StateValues.PRESENT));
        filter2AL.add(FiltersValues.Filter2.STATE_ + IT.StateValues.toString(IT.StateValues.IGNORED_MISSING));
        filter2AL.add(FiltersValues.Filter2.STATE_ + IT.StateValues.toString(IT.StateValues.LEFTOVER));
        filter2AL.add(FiltersValues.Filter2.TYPE_ + IT.TypeValues.toString(IT.TypeValues.EQUIPMENT));
        filter2AL.add(FiltersValues.Filter2.TYPE_ + IT.TypeValues.toString(IT.TypeValues.FURNISHING));
        filter2AL.add(FiltersValues.Filter2.TYPE_ + IT.TypeValues.toString(IT.TypeValues.UNKNOWN));
        filter2AL.add(FiltersValues.Filter2.OBSERVATION_EMPTY);
        if (!Arrays.equals(AppStatics.Observation.observations, new String[]{""})) {
            for (int i = 0; i < AppStatics.Observation.observations.length; i++) {
                filter2AL.add(FiltersValues.Filter2.OBSERVATION_ +
                        AppStatics.Observation.observations[i]);
            }
        }
        filter2Spinner.setAdapter(new ArrayAdapter<>(InventoryActivity.this,
                android.R.layout.simple_list_item_1, filter2AL));

        //endregion filter2Spinner...

        //Data
        updateData();
        updateDataToDisplay();
        displayData();

    }

    private void updateData() {

        if (getFilter1().equals(FiltersValues.Filter1.ALL)) {

            if (getFilter2().equals(FiltersValues.Filter1.ALL)) {
                data = db.getAllData();
            }

        }

        if (data == null) {

            Tools.showInfoDialog(InventoryActivity.this, getString(R.string.error8), "Atras",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            setFilter1(FiltersValues.Filter1.ALL);
                            setFilter2(FiltersValues.Filter1.ALL);
                            startActivity(new Intent(InventoryActivity.this, MainActivity.class));
                            finish();
                        }
                    });

        }


    }

    private void updateDataToDisplay() {

        //checkPoint
        if (data == null) {
            dataToDisplay = new ArrayList<>();
            return;
        }

        //index
        if (getIndex() > data.getCount()) {
            setIndex(0);
        }

        //data
        data.moveToPosition(getIndex() - 1);

        //dataToDisplay
        dataToDisplay = new ArrayList<>();
        for (int i = 0; i < w; i++) {
            dataToDisplay.add("");
        }

        //filling with data
        for (int i = 0; i < w && data.moveToNext(); i++) {
            dataToDisplay.set(i, data.getString(0) + "\n" + data.getString(1));
        }

    }

    private void displayData() {

        String s = getIndex() + "/" + (getIndex() + w);
        textView.setText(s);
        listView.setAdapter(new ArrayAdapter<String>(InventoryActivity.this,
                android.R.layout.simple_list_item_1, dataToDisplay));

    }

    private String getFilter1() {
        return db.getPreference(DB.RT.CURRENT_FILTER1_VALUE);
    }

    private void setFilter1(String filter) {
        db.setPreference(DB.RT.CURRENT_FILTER1_VALUE, filter);
    }

    private String getFilter2() {
        return db.getPreference(DB.RT.CURRENT_FILTER2_VALUE);
    }

    private void setFilter2(String filter) {
        db.setPreference(DB.RT.CURRENT_FILTER2_VALUE, filter);
    }

    private int getIndex() {
        return Integer.valueOf(db.getPreference(DB.RT.CURRENT_INVENTORY_INDEX));
    }

    private void setIndex(int index) {
        db.setPreference(DB.RT.CURRENT_INVENTORY_INDEX, index);
    }

}
