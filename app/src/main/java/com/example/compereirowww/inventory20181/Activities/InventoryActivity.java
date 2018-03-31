package com.example.compereirowww.inventory20181.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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


        public static final String ALL = "Todo";
        public static final String STATE_ = "Estado: ";
        public static final String TYPE_ = "Tipo: ";
        public static final String OBSERVATION_ = "Observación: ";
        public static final String OBSERVATION_EMPTY = "Observación vacía";
        public static final String FOLLOWED = "Con seguimiento";
        public static final String NOT_FOLLOWED = "Sin seguimiento";
        public static final String AREA_ = "Area: ";
        public static final String MY_AREAS_ = "Área(s) en seguimiento";
        public static final String LOCATION_ = "Localización: ";
        public static final String LOCATION_EMPTY = "Localización vacía";

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
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = (String) adapterView.getItemAtPosition(i);
                if (!selectedItem.equals("")) {
                    db.setPreference(DB.RT.NUMBER_TO_EDIT, selectedItem.split(",", -1)[0]);
                    startActivity(new Intent(InventoryActivity.this, EditActivity.class));
                }
            }
        });
        filter1Spinner = (Spinner) findViewById(R.id.spinner2);
        filter2Spinner = (Spinner) findViewById(R.id.spinner3);
        bBtn = (Button) findViewById(R.id.b_btn);
        bBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (data == null) return;

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

                if (data == null) return;

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

                if (data == null) return;

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

                if (data == null) return;

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
        filter1AL.add(FiltersValues.ALL);
        filter1AL.add(FiltersValues.FOLLOWED);
        filter1AL.add(FiltersValues.NOT_FOLLOWED);
        filter1AL.add(FiltersValues.STATE_ + IT.StateValues.toString(DB.IT.StateValues.MISSING));
        filter1AL.add(FiltersValues.STATE_ + IT.StateValues.toString(IT.StateValues.PRESENT));
        filter1AL.add(FiltersValues.STATE_ + IT.StateValues.toString(IT.StateValues.IGNORED_MISSING));
        filter1AL.add(FiltersValues.STATE_ + IT.StateValues.toString(IT.StateValues.LEFTOVER));
        filter1AL.add(FiltersValues.TYPE_ + IT.TypeValues.toString(IT.TypeValues.EQUIPMENT));
        filter1AL.add(FiltersValues.TYPE_ + IT.TypeValues.toString(IT.TypeValues.FURNISHING));
        filter1AL.add(FiltersValues.TYPE_ + IT.TypeValues.toString(IT.TypeValues.UNKNOWN));
        filter1AL.add(FiltersValues.OBSERVATION_EMPTY);
        filter1AL.add(FiltersValues.LOCATION_EMPTY);
        if (!Arrays.equals(AppStatics.Location.locations, new String[]{""})) {
            for (int i = 0; i < AppStatics.Location.locations.length; i++) {
                filter1AL.add(FiltersValues.LOCATION_ +
                        AppStatics.Location.locations[i]);
            }
        }
        if (!Arrays.equals(AppStatics.Area.areas, new String[]{""})) {
            for (int i = 0; i < AppStatics.Area.areas.length; i++) {
                filter1AL.add(FiltersValues.AREA_ +
                        AppStatics.Area.areas[i]);
            }
        }
        if (!Arrays.equals(AppStatics.Observation.observations, new String[]{""})) {
            for (int i = 0; i < AppStatics.Observation.observations.length; i++) {
                filter1AL.add(FiltersValues.OBSERVATION_ +
                        AppStatics.Observation.observations[i]);
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
                    setIndex(0);
                    updateData();
                    updateDataToDisplay();
                    displayData();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //endregion filter1Spinner...

        //region filter2Spinner...

        filter2Spinner.setAdapter(new ArrayAdapter<>(InventoryActivity.this,
                android.R.layout.simple_list_item_1, filter1AL));
        filter2Spinner.setSelection(Tools.getIndexOf(filter1AL, getFilter2()));
        filter2Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = (String) adapterView.getSelectedItem();
                if (!getFilter2().equals(selectedItem)) {
                    setFilter2(selectedItem);
                    setIndex(0);
                    updateData();
                    updateDataToDisplay();
                    displayData();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //endregion filter2Spinner...

        //Data
        updateData();
        updateDataToDisplay();
        displayData();

    }

    private void updateData() {

        //TODO deb
        Log.d(AppStatics.APP_TAG, "updateData method");

        if (getFilter1().equals(FiltersValues.ALL) && getFilter2().equals(FiltersValues.ALL)) {

            data = db.getAllData();

            //TODO deb
            Log.d(AppStatics.APP_TAG, FiltersValues.ALL);
            Log.d(AppStatics.APP_TAG, FiltersValues.ALL);

        } else if (getFilter1().equals(getFilter2()) || (getFilter1().equals(FiltersValues.ALL) ||
                getFilter2().equals(FiltersValues.ALL))) {

            //TODO deb
            Log.d(AppStatics.APP_TAG, FiltersValues.ALL);

            if (getFilter1().equals(FiltersValues.FOLLOWED) ||
                    getFilter2().equals(FiltersValues.FOLLOWED)) {

                data = db.getAllDataIfFollowing(true);

                //TODO deb
                Log.d(AppStatics.APP_TAG, FiltersValues.FOLLOWED);

            } else if (getFilter1().equals(FiltersValues.NOT_FOLLOWED) ||
                    getFilter2().equals(FiltersValues.NOT_FOLLOWED)) {

                data = db.getAllDataIfFollowing(false);

                //TODO deb
                Log.d(AppStatics.APP_TAG, FiltersValues.NOT_FOLLOWED);

            } else if (getFilter1().equals(FiltersValues.LOCATION_EMPTY) ||
                    getFilter2().equals(FiltersValues.LOCATION_EMPTY)) {

                data = db.getAllDataIfLocation("");

                //TODO deb
                Log.d(AppStatics.APP_TAG, FiltersValues.LOCATION_EMPTY);

            } else if ((getFilter1().contains(FiltersValues.LOCATION_) &&
                    getFilter1().substring(0, FiltersValues.LOCATION_.length()).
                            equals(FiltersValues.LOCATION_))) {

                data = db.getAllDataIfLocation(getFilter1().
                        substring(FiltersValues.LOCATION_.length()));

                //TODO deb
                Log.d(AppStatics.APP_TAG, FiltersValues.LOCATION_);

            } else if (getFilter2().contains(FiltersValues.LOCATION_) &&
                    getFilter2().substring(0, FiltersValues.LOCATION_.length()).
                            equals(FiltersValues.LOCATION_)) {

                data = db.getAllDataIfLocation(getFilter2().
                        substring(FiltersValues.LOCATION_.length()));

                //TODO deb
                Log.d(AppStatics.APP_TAG, FiltersValues.LOCATION_);

            } else if (getFilter1().contains(FiltersValues.STATE_)) {

                data = db.getAllDataIfState(getFilter1().
                        substring(FiltersValues.STATE_.length()));

                //TODO deb
                Log.d(AppStatics.APP_TAG, FiltersValues.STATE_);

            } else if (getFilter2().contains(FiltersValues.STATE_)) {

                data = db.getAllDataIfState(getFilter2().
                        substring(FiltersValues.STATE_.length()));

                //TODO deb
                Log.d(AppStatics.APP_TAG, FiltersValues.STATE_);

            } else if (getFilter1().contains(FiltersValues.TYPE_)) {

                data = db.getAllDataIfType(getFilter1().
                        substring(FiltersValues.TYPE_.length()));

                //TODO deb
                Log.d(AppStatics.APP_TAG, FiltersValues.TYPE_);

            } else if (getFilter2().contains(FiltersValues.TYPE_)) {

                data = db.getAllDataIfType(getFilter2().
                        substring(FiltersValues.TYPE_.length()));

                //TODO deb
                Log.d(AppStatics.APP_TAG, FiltersValues.TYPE_);

            } else if (getFilter1().equals(FiltersValues.OBSERVATION_EMPTY) ||
                    getFilter2().equals(FiltersValues.OBSERVATION_EMPTY)) {

                data = db.getAllDataIfObservation("");

                //TODO deb
                Log.d(AppStatics.APP_TAG, FiltersValues.OBSERVATION_EMPTY);

            } else if (getFilter1().contains(FiltersValues.AREA_) &&
                    getFilter1().substring(0, FiltersValues.AREA_.length()).
                            equals(FiltersValues.AREA_)) {

                data = db.getAllDataIfArea(getFilter1().
                        substring(FiltersValues.AREA_.length()));

                //TODO deb
                Log.d(AppStatics.APP_TAG, FiltersValues.AREA_);


            } else if (getFilter2().contains(FiltersValues.AREA_) &&
                    getFilter2().substring(0, FiltersValues.AREA_.length()).
                            equals(FiltersValues.AREA_)) {

                data = db.getAllDataIfArea(getFilter2().
                        substring(FiltersValues.AREA_.length()));

                //TODO deb
                Log.d(AppStatics.APP_TAG, FiltersValues.AREA_);

            } else {

                data = null;
                Tools.showToast(InventoryActivity.this, "Eso no es posible!", false);

                //TODO deb
                Log.d(AppStatics.APP_TAG, "AMBIGUOUS");

            }

        } else if (getFilter1().equals(FiltersValues.FOLLOWED) ||
                getFilter2().equals(FiltersValues.FOLLOWED)) {

            //TODO deb
            Log.d(AppStatics.APP_TAG, FiltersValues.FOLLOWED);

            if (getFilter1().equals(FiltersValues.LOCATION_EMPTY) ||
                    getFilter2().equals(FiltersValues.LOCATION_EMPTY)) {

                data = db.getAllDataIfFollowingAndLocation(true, "");

                //TODO deb
                Log.d(AppStatics.APP_TAG, FiltersValues.LOCATION_EMPTY);

            } else if ((getFilter1().contains(FiltersValues.LOCATION_) &&
                    getFilter1().substring(0, FiltersValues.LOCATION_.length()).
                            equals(FiltersValues.LOCATION_))) {

                data = db.getAllDataIfFollowingAndLocation(true,
                        getFilter1().substring(FiltersValues.LOCATION_.length()));

                //TODO deb
                Log.d(AppStatics.APP_TAG, FiltersValues.LOCATION_);

            } else if (getFilter2().contains(FiltersValues.LOCATION_) &&
                    getFilter2().substring(0, FiltersValues.LOCATION_.length()).
                            equals(FiltersValues.LOCATION_)) {

                data = db.getAllDataIfFollowingAndLocation(true,
                        getFilter2().substring(FiltersValues.LOCATION_.length()));

                //TODO deb
                Log.d(AppStatics.APP_TAG, FiltersValues.LOCATION_);

            } else if (getFilter1().contains(FiltersValues.STATE_)) {

                data = db.getAllDataIfFollowingAndState(true,
                        getFilter1().substring(FiltersValues.STATE_.length()));

                //TODO deb
                Log.d(AppStatics.APP_TAG, FiltersValues.STATE_);

            } else if (getFilter2().contains(FiltersValues.STATE_)) {

                //TODO deb
                Log.d(AppStatics.APP_TAG, FiltersValues.STATE_);

                data = db.getAllDataIfFollowingAndState(true,
                        getFilter2().substring(FiltersValues.STATE_.length()));

            } else if (getFilter1().contains(FiltersValues.TYPE_)) {

                //TODO deb
                Log.d(AppStatics.APP_TAG, FiltersValues.TYPE_);

                data = db.getAllDataIfFollowingAndType(true,
                        getFilter1().substring(FiltersValues.TYPE_.length()));

            } else if (getFilter2().contains(FiltersValues.TYPE_)) {

                //TODO deb
                Log.d(AppStatics.APP_TAG, FiltersValues.TYPE_);

                data = db.getAllDataIfFollowingAndType(true,
                        getFilter2().substring(FiltersValues.TYPE_.length()));

            } else if (getFilter1().equals(FiltersValues.OBSERVATION_EMPTY) ||
                    getFilter2().equals(FiltersValues.OBSERVATION_EMPTY)) {

                //TODO deb
                Log.d(AppStatics.APP_TAG, FiltersValues.OBSERVATION_EMPTY);

                data = db.getAllDataIfFollowingAndObservation(true, "");

            } else if (getFilter1().contains(FiltersValues.AREA_) &&
                    getFilter1().substring(0, FiltersValues.AREA_.length()).
                            equals(FiltersValues.AREA_)) {

                data = db.getAllDataIfFollowingAndArea(true, getFilter1().
                        substring(FiltersValues.AREA_.length()));

                //TODO deb
                Log.d(AppStatics.APP_TAG, FiltersValues.AREA_);


            } else if (getFilter2().contains(FiltersValues.AREA_) &&
                    getFilter2().substring(0, FiltersValues.AREA_.length()).
                            equals(FiltersValues.AREA_)) {

                data = db.getAllDataIfFollowingAndArea(true, getFilter2().
                        substring(FiltersValues.AREA_.length()));

                //TODO deb
                Log.d(AppStatics.APP_TAG, FiltersValues.AREA_);

            } else {

                data = null;
                Tools.showToast(InventoryActivity.this, "Eso no es posible!", false);

                //TODO deb
                Log.d(AppStatics.APP_TAG, "AMBIGUOUS");

            }

        } else if (getFilter1().equals(FiltersValues.NOT_FOLLOWED) ||
                getFilter2().equals(FiltersValues.NOT_FOLLOWED)) {

            //TODO deb
            Log.d(AppStatics.APP_TAG, FiltersValues.NOT_FOLLOWED);

            if (getFilter1().equals(FiltersValues.LOCATION_EMPTY) ||
                    getFilter2().equals(FiltersValues.LOCATION_EMPTY)) {

                data = db.getAllDataIfFollowingAndLocation(false, "");

                //TODO deb
                Log.d(AppStatics.APP_TAG, FiltersValues.LOCATION_EMPTY);

            } else if ((getFilter1().contains(FiltersValues.LOCATION_) &&
                    getFilter1().substring(0, FiltersValues.LOCATION_.length()).
                            equals(FiltersValues.LOCATION_))) {

                data = db.getAllDataIfFollowingAndLocation(false, getFilter1().
                        substring(FiltersValues.LOCATION_.length()));

                //TODO deb
                Log.d(AppStatics.APP_TAG, FiltersValues.LOCATION_);

            } else if (getFilter2().contains(FiltersValues.LOCATION_) &&
                    getFilter2().substring(0, FiltersValues.LOCATION_.length()).
                            equals(FiltersValues.LOCATION_)) {

                data = db.getAllDataIfFollowingAndLocation(false, getFilter2().
                        substring(FiltersValues.LOCATION_.length()));

                //TODO deb
                Log.d(AppStatics.APP_TAG, FiltersValues.LOCATION_);

            } else if (getFilter1().contains(FiltersValues.STATE_)) {

                data = db.getAllDataIfFollowingAndState(false, getFilter1().
                        substring(FiltersValues.STATE_.length()));

                //TODO deb
                Log.d(AppStatics.APP_TAG, FiltersValues.STATE_);

            } else if (getFilter2().contains(FiltersValues.STATE_)) {

                data = db.getAllDataIfFollowingAndState(false, getFilter2().
                        substring(FiltersValues.STATE_.length()));

                //TODO deb
                Log.d(AppStatics.APP_TAG, FiltersValues.STATE_);

            } else if (getFilter1().contains(FiltersValues.TYPE_)) {

                data = db.getAllDataIfFollowingAndType(false, getFilter1().
                        substring(FiltersValues.TYPE_.length()));

                //TODO deb
                Log.d(AppStatics.APP_TAG, FiltersValues.TYPE_);

            } else if (getFilter2().contains(FiltersValues.TYPE_)) {

                data = db.getAllDataIfFollowingAndType(false, getFilter2().
                        substring(FiltersValues.TYPE_.length()));

                //TODO deb
                Log.d(AppStatics.APP_TAG, FiltersValues.TYPE_);

            } else if (getFilter1().equals(FiltersValues.OBSERVATION_EMPTY) ||
                    getFilter2().equals(FiltersValues.OBSERVATION_EMPTY)) {

                data = db.getAllDataIfFollowingAndObservation(false, "");

                //TODO deb
                Log.d(AppStatics.APP_TAG, FiltersValues.OBSERVATION_EMPTY);

            } else if (getFilter1().contains(FiltersValues.AREA_) &&
                    getFilter1().substring(0, FiltersValues.AREA_.length()).
                            equals(FiltersValues.AREA_)) {

                data = db.getAllDataIfFollowingAndArea(false, getFilter1().
                        substring(FiltersValues.AREA_.length()));

                //TODO deb
                Log.d(AppStatics.APP_TAG, FiltersValues.AREA_);


            } else if (getFilter2().contains(FiltersValues.AREA_) &&
                    getFilter2().substring(0, FiltersValues.AREA_.length()).
                            equals(FiltersValues.AREA_)) {

                data = db.getAllDataIfFollowingAndArea(false, getFilter2().
                        substring(FiltersValues.AREA_.length()));

                //TODO deb
                Log.d(AppStatics.APP_TAG, FiltersValues.AREA_);

            } else {

                data = null;
                Tools.showToast(InventoryActivity.this, "Eso no es posible!", false);

                //TODO deb
                Log.d(AppStatics.APP_TAG, "AMBIGUOUS");

            }

        } else if (getFilter1().equals(FiltersValues.LOCATION_EMPTY) ||
                getFilter2().equals(FiltersValues.LOCATION_EMPTY)) {

            //TODO deb
            Log.d(AppStatics.APP_TAG, FiltersValues.LOCATION_EMPTY);

            if (getFilter1().contains(FiltersValues.STATE_)) {

                data = db.getAllDataIfLocationAndState("", getFilter1().
                        substring(FiltersValues.STATE_.length()));

                //TODO deb
                Log.d(AppStatics.APP_TAG, FiltersValues.STATE_);

            } else if (getFilter2().contains(FiltersValues.STATE_)) {

                data = db.getAllDataIfLocationAndState("", getFilter2().
                        substring(FiltersValues.STATE_.length()));

                //TODO deb
                Log.d(AppStatics.APP_TAG, FiltersValues.STATE_);

            } else if (getFilter1().contains(FiltersValues.TYPE_)) {

                data = db.getAllDataIfLocationAndType("", getFilter1().
                        substring(FiltersValues.TYPE_.length()));

                //TODO deb
                Log.d(AppStatics.APP_TAG, FiltersValues.TYPE_);

            } else if (getFilter2().contains(FiltersValues.TYPE_)) {

                data = db.getAllDataIfLocationAndType("", getFilter2().
                        substring(FiltersValues.TYPE_.length()));

                //TODO deb
                Log.d(AppStatics.APP_TAG, FiltersValues.TYPE_);

            } else if (getFilter1().equals(FiltersValues.OBSERVATION_EMPTY) ||
                    getFilter2().equals(FiltersValues.OBSERVATION_EMPTY)) {

                data = db.getAllDataIfLocationAndObservation("", "");

                //TODO deb
                Log.d(AppStatics.APP_TAG, FiltersValues.OBSERVATION_EMPTY);

            } else if (getFilter1().contains(FiltersValues.AREA_) &&
                    getFilter1().substring(0, FiltersValues.AREA_.length()).
                            equals(FiltersValues.AREA_)) {

                data = db.getAllDataIfLocationAndArea("", getFilter1().
                        substring(FiltersValues.AREA_.length()));

                //TODO deb
                Log.d(AppStatics.APP_TAG, FiltersValues.AREA_);


            } else if (getFilter2().contains(FiltersValues.AREA_) &&
                    getFilter2().substring(0, FiltersValues.AREA_.length()).
                            equals(FiltersValues.AREA_)) {

                data = db.getAllDataIfLocationAndArea("", getFilter2().
                        substring(FiltersValues.AREA_.length()));

                //TODO deb
                Log.d(AppStatics.APP_TAG, FiltersValues.AREA_);

            } else {

                data = null;
                Tools.showToast(InventoryActivity.this, "Eso no es posible!", false);

                //TODO deb
                Log.d(AppStatics.APP_TAG, "AMBIGUOUS");

            }

        } else if (getFilter1().contains(FiltersValues.LOCATION_) &&
                getFilter1().substring(0, FiltersValues.LOCATION_.length()).
                        equals(FiltersValues.LOCATION_)) {
            //TODO deb
            Log.d(AppStatics.APP_TAG, FiltersValues.LOCATION_);

            if (getFilter2().contains(FiltersValues.STATE_)) {

                data = db.getAllDataIfLocationAndState(getFilter1().
                                substring(FiltersValues.LOCATION_.length()),
                        getFilter2().substring(FiltersValues.STATE_.length()));

                //TODO deb
                Log.d(AppStatics.APP_TAG, FiltersValues.STATE_);

            } else if (getFilter2().contains(FiltersValues.TYPE_)) {

                data = db.getAllDataIfLocationAndType(getFilter1().
                                substring(FiltersValues.LOCATION_.length()),
                        getFilter2().substring(FiltersValues.TYPE_.length()));

                //TODO deb
                Log.d(AppStatics.APP_TAG, FiltersValues.TYPE_);

            } else if (getFilter2().equals(FiltersValues.OBSERVATION_EMPTY)) {

                data = db.getAllDataIfLocationAndObservation(getFilter1().
                        substring(FiltersValues.LOCATION_.length()), "");

                //TODO deb
                Log.d(AppStatics.APP_TAG, FiltersValues.OBSERVATION_EMPTY);

            } else if (getFilter2().contains(FiltersValues.AREA_) &&
                    getFilter2().substring(0, FiltersValues.AREA_.length()).
                            equals(FiltersValues.AREA_)) {

                data = db.getAllDataIfLocationAndArea(getFilter1().
                                substring(FiltersValues.LOCATION_.length()),
                        getFilter2().substring(FiltersValues.AREA_.length()));

                //TODO deb
                Log.d(AppStatics.APP_TAG, FiltersValues.AREA_);

            } else {

                data = null;
                Tools.showToast(InventoryActivity.this, "Eso no es posible!", false);

                //TODO deb
                Log.d(AppStatics.APP_TAG, "AMBIGUOUS");

            }

        } else if (getFilter2().contains(FiltersValues.LOCATION_) &&
                getFilter2().substring(0, FiltersValues.LOCATION_.length()).
                        equals(FiltersValues.LOCATION_)) {

            //TODO Deb
            Log.d(AppStatics.APP_TAG, FiltersValues.LOCATION_);

            if (getFilter1().contains(FiltersValues.STATE_)) {

                data = db.getAllDataIfLocationAndState(getFilter2().
                                substring(FiltersValues.LOCATION_.length()),
                        getFilter1().substring(FiltersValues.STATE_.length()));

                //TODO deb
                Log.d(AppStatics.APP_TAG, FiltersValues.STATE_);

            } else if (getFilter1().contains(FiltersValues.TYPE_)) {

                data = db.getAllDataIfLocationAndType(getFilter2().
                                substring(FiltersValues.LOCATION_.length()),
                        getFilter1().substring(FiltersValues.TYPE_.length()));

                //TODO deb
                Log.d(AppStatics.APP_TAG, FiltersValues.TYPE_);

            } else if (getFilter1().equals(FiltersValues.OBSERVATION_EMPTY)) {

                data = db.getAllDataIfLocationAndObservation(getFilter2().
                        substring(FiltersValues.LOCATION_.length()), "");

                //TODO deb
                Log.d(AppStatics.APP_TAG, FiltersValues.OBSERVATION_EMPTY);

            } else if (getFilter1().contains(FiltersValues.AREA_) &&
                    getFilter1().substring(0, FiltersValues.AREA_.length()).
                            equals(FiltersValues.AREA_)) {

                data = db.getAllDataIfLocationAndArea(getFilter2().
                                substring(FiltersValues.LOCATION_.length()),
                        getFilter1().substring(FiltersValues.AREA_.length()));

                //TODO deb
                Log.d(AppStatics.APP_TAG, FiltersValues.AREA_);


            } else {

                data = null;
                Tools.showToast(InventoryActivity.this, "Eso no es posible!", false);

                //TODO deb
                Log.d(AppStatics.APP_TAG, "AMBIGUOUS");

            }

        } else if (getFilter1().contains(FiltersValues.STATE_)) {

            //TODO Deb
            Log.d(AppStatics.APP_TAG, FiltersValues.STATE_);

            if (getFilter2().contains(FiltersValues.TYPE_)) {

                data = db.getAllDataIfStateAndType(getFilter1().
                                substring(FiltersValues.STATE_.length()),
                        getFilter2().substring(FiltersValues.TYPE_.length()));

                //TODO deb
                Log.d(AppStatics.APP_TAG, FiltersValues.TYPE_);

            } else if (getFilter2().equals(FiltersValues.OBSERVATION_EMPTY)) {

                data = db.getAllDataIfStateAndObservation(getFilter1().
                        substring(FiltersValues.STATE_.length()), "");

                //TODO deb
                Log.d(AppStatics.APP_TAG, FiltersValues.OBSERVATION_EMPTY);

            } else if (getFilter2().contains(FiltersValues.AREA_) &&
                    getFilter2().substring(0, FiltersValues.AREA_.length()).
                            equals(FiltersValues.AREA_)) {

                data = db.getAllDataIfStateAndArea(getFilter1().
                                substring(FiltersValues.STATE_.length()),
                        getFilter2().substring(FiltersValues.AREA_.length()));

                //TODO deb
                Log.d(AppStatics.APP_TAG, FiltersValues.AREA_);

            } else {

                data = null;
                Tools.showToast(InventoryActivity.this, "Eso no es posible!", false);

                //TODO deb
                Log.d(AppStatics.APP_TAG, "AMBIGUOUS");

            }

        } else if (getFilter2().contains(FiltersValues.STATE_)) {

            //TODO Deb
            Log.d(AppStatics.APP_TAG, FiltersValues.STATE_);

            if (getFilter1().contains(FiltersValues.TYPE_)) {

                data = db.getAllDataIfStateAndType(getFilter2().
                                substring(FiltersValues.STATE_.length()),
                        getFilter1().substring(FiltersValues.TYPE_.length()));

                //TODO deb
                Log.d(AppStatics.APP_TAG, FiltersValues.TYPE_);

            } else if (getFilter1().equals(FiltersValues.OBSERVATION_EMPTY)) {

                data = db.getAllDataIfStateAndObservation(getFilter2().
                        substring(FiltersValues.STATE_.length()), "");

                //TODO deb
                Log.d(AppStatics.APP_TAG, FiltersValues.OBSERVATION_EMPTY);

            } else if (getFilter1().contains(FiltersValues.AREA_) &&
                    getFilter1().substring(0, FiltersValues.AREA_.length()).
                            equals(FiltersValues.AREA_)) {

                data = db.getAllDataIfStateAndArea(getFilter2().
                                substring(FiltersValues.STATE_.length()),
                        getFilter2().substring(FiltersValues.AREA_.length()));

                //TODO deb
                Log.d(AppStatics.APP_TAG, FiltersValues.AREA_);


            } else {

                data = null;
                Tools.showToast(InventoryActivity.this, "Eso no es posible!", false);

                //TODO deb
                Log.d(AppStatics.APP_TAG, "AMBIGUOUS");

            }

        } else if (getFilter1().contains(FiltersValues.TYPE_)) {

            //TODO deb
            Log.d(AppStatics.APP_TAG, FiltersValues.TYPE_);

            if (getFilter2().equals(FiltersValues.OBSERVATION_EMPTY)) {

                data = db.getAllDataIfTypeAndObservation(getFilter1().
                        substring(FiltersValues.TYPE_.length()), "");

                //TODO deb
                Log.d(AppStatics.APP_TAG, FiltersValues.OBSERVATION_EMPTY);

            } else if (getFilter2().contains(FiltersValues.AREA_) &&
                    getFilter2().substring(0, FiltersValues.AREA_.length()).
                            equals(FiltersValues.AREA_)) {

                data = db.getAllDataIfTypeAndArea(getFilter1().
                                substring(FiltersValues.TYPE_.length()),
                        getFilter2().substring(FiltersValues.AREA_.length()));

                //TODO deb
                Log.d(AppStatics.APP_TAG, FiltersValues.AREA_);

            } else {

                data = null;
                Tools.showToast(InventoryActivity.this, "Eso no es posible!", false);

                //TODO deb
                Log.d(AppStatics.APP_TAG, "AMBIGUOUS");

            }

        } else if (getFilter2().contains(FiltersValues.TYPE_)) {

            //TODO Deb
            Log.d(AppStatics.APP_TAG, FiltersValues.TYPE_);

            if (getFilter1().equals(FiltersValues.OBSERVATION_EMPTY)) {

                data = db.getAllDataIfTypeAndObservation(getFilter2().
                        substring(FiltersValues.TYPE_.length()), "");

                //TODO deb
                Log.d(AppStatics.APP_TAG, FiltersValues.OBSERVATION_EMPTY);

            } else if (getFilter1().contains(FiltersValues.AREA_) &&
                    getFilter1().substring(0, FiltersValues.AREA_.length()).
                            equals(FiltersValues.AREA_)) {

                data = db.getAllDataIfTypeAndArea(getFilter2().
                        substring(FiltersValues.TYPE_.length()), getFilter1().
                        substring(FiltersValues.AREA_.length()));

                //TODO deb
                Log.d(AppStatics.APP_TAG, FiltersValues.AREA_);


            } else {

                data = null;
                Tools.showToast(InventoryActivity.this, "Eso no es posible!", false);

                //TODO deb
                Log.d(AppStatics.APP_TAG, "AMBIGUOUS");

            }

        } else if (getFilter1().equals(FiltersValues.OBSERVATION_EMPTY) ||
                getFilter2().equals(FiltersValues.OBSERVATION_EMPTY)) {

            //TODO Deb
            Log.d(AppStatics.APP_TAG, FiltersValues.OBSERVATION_EMPTY);

            if (getFilter1().contains(FiltersValues.AREA_) &&
                    getFilter1().substring(0, FiltersValues.AREA_.length()).
                            equals(FiltersValues.AREA_)) {

                data = db.getAllDataIfObservationAndArea("", getFilter1().
                        substring(FiltersValues.AREA_.length()));

                //TODO deb
                Log.d(AppStatics.APP_TAG, FiltersValues.AREA_);


            } else if (getFilter2().contains(FiltersValues.AREA_) &&
                    getFilter2().substring(0, FiltersValues.AREA_.length()).
                            equals(FiltersValues.AREA_)) {

                data = db.getAllDataIfObservationAndArea("", getFilter2().
                        substring(FiltersValues.AREA_.length()));

                //TODO deb
                Log.d(AppStatics.APP_TAG, FiltersValues.AREA_);

            } else {

                data = null;

                Tools.showToast(InventoryActivity.this, "Eso no es posible!", false);

                //TODO deb
                Log.d(AppStatics.APP_TAG, "AMBIGUOUS");

            }

        } else {

            data = null;
            Tools.showToast(InventoryActivity.this, "Eso no es posible!", false);

            //TODO deb
            Log.d(AppStatics.APP_TAG, "AMBIGUOUS");

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
            dataToDisplay.set(i, data.getString(0) + ",\n" + data.getString(1));
        }

    }

    private void displayData() {

        if (data == null) {
            String s = "0/0 de 0";
            textView.setText(s);

        } else {

            String s = getIndex() + "/" + (getIndex() + w) + " de " + data.getCount();
            textView.setText(s);
        }
        listView.setAdapter(new ArrayAdapter<>(InventoryActivity.this,
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
