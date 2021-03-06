package com.example.compereirowww.inventory20181.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.compereirowww.inventory20181.Activities.AppStatics.Area;
import com.example.compereirowww.inventory20181.Activities.AppStatics.Location;
import com.example.compereirowww.inventory20181.Activities.AppStatics.Observation;
import com.example.compereirowww.inventory20181.DataBase.DB;
import com.example.compereirowww.inventory20181.DataBase.DB.IT;
import com.example.compereirowww.inventory20181.DataBase.DB.PT.PNames;
import com.example.compereirowww.inventory20181.R;
import com.example.compereirowww.inventory20181.Tools.Tools;

import java.util.ArrayList;
import java.util.Arrays;

import static com.example.compereirowww.inventory20181.Activities.AppStatics.APP_TAG;
import static com.example.compereirowww.inventory20181.Activities.AppStatics.db;

public class InventoryActivity extends AppCompatActivity {

    //Statics
    private static final int WINDOW = 10;
    private static final int QR_DECODER_REQUEST = 626;
    private static final int INFO_LENGHT = 5;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //GUI
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callQRDecoder(QR_DECODER_REQUEST);
            }
        });
        textView = (TextView) findViewById(R.id.textView2);
        AppStatics.formatView(textView);
        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = (String) adapterView.getItemAtPosition(i);
                if (!selectedItem.equals("")) {
                    String number = selectedItem.split("\n", -1)[1];
                    number = number.substring((number.indexOf(":") + 2), number.length());
                    callEditActivity(number);
                }
            }
        });
        filter1Spinner = (Spinner) findViewById(R.id.filter1_s);
        filter2Spinner = (Spinner) findViewById(R.id.filter2_s);

        bBtn = (Button) findViewById(R.id.b_btn);
        bBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveBack(WINDOW);
            }
        });
        bbBtn = (Button) findViewById(R.id.bb_btn);
        bbBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveBack(WINDOW * WINDOW);
            }
        });
        fBtn = (Button) findViewById(R.id.f_btn);
        fBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveForward(WINDOW);
            }
        });
        ffBtn = (Button) findViewById(R.id.ff_btn);
        ffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveForward(WINDOW * WINDOW);
            }
        });

        //AppStatics
        Area.updateAreas();
        Location.updateLocations();
        Observation.updateObservations();

    }

    private void moveForward(int toMove) {
        if (data == null) return;

        if (getIndex() + toMove < data.getCount()) {
            setIndex(getIndex() + toMove);

            updateDataToDisplay();
            displayData();
        }
    }

    private void moveBack(int toMove) {

        if (data == null) return;
        if (getIndex() - toMove >= 0) {

            setIndex(getIndex() - toMove);

            updateDataToDisplay();
            displayData();


        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //GUI
        //region filters...

        ArrayList<String> filterAL = new ArrayList<>();
        filterAL.add(FiltersValues.ALL);
        filterAL.add(FiltersValues.FOLLOWED);
        filterAL.add(FiltersValues.NOT_FOLLOWED);
        filterAL.add(FiltersValues.STATE_ + IT.StateValues.toString(DB.IT.StateValues.MISSING));
        filterAL.add(FiltersValues.STATE_ + IT.StateValues.toString(IT.StateValues.PRESENT));
        filterAL.add(FiltersValues.STATE_ + IT.StateValues.toString(IT.StateValues.IGNORED_MISSING));
        filterAL.add(FiltersValues.STATE_ + IT.StateValues.toString(IT.StateValues.LEFTOVER));
        filterAL.add(FiltersValues.STATE_ + IT.StateValues.toString(IT.StateValues.LEFTOVER_PRESENT));
        filterAL.add(FiltersValues.TYPE_ + IT.TypeValues.toString(IT.TypeValues.EQUIPMENT));
        filterAL.add(FiltersValues.TYPE_ + IT.TypeValues.toString(IT.TypeValues.FURNISHING));
        filterAL.add(FiltersValues.TYPE_ + IT.TypeValues.toString(IT.TypeValues.UNKNOWN));
        filterAL.add(FiltersValues.LOCATION_EMPTY);
        if (!Arrays.equals(Location.locations, new String[]{""})) {
            for (int i = 0; i < Location.locations.length; i++) {
                if (!Location.locations[i].equals("")) {
                    filterAL.add(FiltersValues.LOCATION_ +
                            Location.locations[i]);
                }
            }
        }
        if (!Arrays.equals(Area.areas, new String[]{""})) {
            for (int i = 0; i < Area.areas.length; i++) {
                if (!Area.areas[i].equals(""))
                    filterAL.add(FiltersValues.AREA_ +
                            Area.areas[i]);
            }
        }
        filterAL.add(FiltersValues.OBSERVATION_EMPTY);
        if (!Arrays.equals(Observation.observations, new String[]{""})) {
            for (int i = 0; i < Observation.observations.length; i++) {
                if (!Observation.observations[i].equals(""))
                    filterAL.add(FiltersValues.OBSERVATION_ +
                            Observation.observations[i]);
            }
        }
        AppStatics.formatView(InventoryActivity.this, filterAL, filter1Spinner);
        filter1Spinner.setSelection(Tools.getIndexOf(filterAL, getFilter1()));
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


        AppStatics.formatView(InventoryActivity.this, filterAL, filter2Spinner);
        filter2Spinner.setSelection(Tools.getIndexOf(filterAL, getFilter2()));
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

        //endregion filters...

        //Data
        updateData();
        updateDataToDisplay();
        displayData();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == QR_DECODER_REQUEST) {
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra("SCAN_RESULT");

                if (db.numberExist(result)) {

                    if (db.getNumberState(result) != IT.StateValues.LEFTOVER &&
                            db.getNumberState(result) != IT.StateValues.LEFTOVER_PRESENT) {

                        db.updateState(result, IT.StateValues.PRESENT);
                        db.updateLastChecking(result, Tools.getDate());
                        Tools.showToast(InventoryActivity.this,
                                "El número ha sido marcado como " +
                                        IT.StateValues.toString(IT.StateValues.PRESENT), false);

                    } else {
                        db.updateState(result, IT.StateValues.LEFTOVER_PRESENT);
                        db.updateLastChecking(result, Tools.getDate());
                        Tools.showToast(InventoryActivity.this,
                                "El número ha sido marcado como " +
                                        IT.StateValues.toString(IT.StateValues.LEFTOVER_PRESENT), false);
                    }

                    showNumberDetailDialog(result);

                } else {
                    Tools.showToast(InventoryActivity.this, "Ningún número válido leído!", false);
                }


            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_inventory, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.report) {
            callReportActivity();
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
        if (id == R.id.insert_new_number) {
            callNewNumberActivity();
            return super.onOptionsItemSelected(item);
        }
        if (id == R.id.search) {
            callSearchActivity();
            return super.onOptionsItemSelected(item);
        }
        if (id == R.id.help) {
            Tools.showToast(InventoryActivity.this, "No hay ayuda!!! Por ahora...", false);
            return super.onOptionsItemSelected(item);
        }
        if (id == R.id.configuration) {
            callConfigurations();
            return super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }

    private void showNumberDetailDialog(final String number) {
        String ms = "Número: " + number + "\n";
        Cursor numberData = db.getAllNumberData(number);
        numberData.moveToNext();
        ms += "Description: " + numberData.getString(IT.Indexes.DESCRIPTION_COLUMN_INDEX) + "\n";
        ms += "Área: " + numberData.getString(IT.Indexes.AREA_COLUMN_INDEX) + "\n";
        ms += "Estado: " +
                IT.StateValues.toString(Integer.
                        parseInt(numberData.getString(IT.Indexes.STATE_COLUMN_INDEX))) + "\n";
        ms += "Tipo: " +
                IT.TypeValues.toString(Integer.
                        parseInt(numberData.getString(IT.Indexes.TYPE_COLUMN_INDEX))) + "\n";
        ms += "Localización: " + numberData.getString(IT.Indexes.LOCATION_COLUMN_INDEX) + "\n";
        ms += "Observación: " + numberData.getString(IT.Indexes.OBSERVATION_COLUMN_INDEX) + "\n";
        numberData.close();

        Tools.showDialogMessage(InventoryActivity.this, ms, "Editar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                callEditActivity(number);
            }
        }, "Continuar", null);
    }

    private void callConfigurations() {
        startActivity(new Intent(InventoryActivity.this, InventoryConfigurationActivity.class));
    }

    private void callEditActivity(String number) {
        db.setPreference(PNames.NUMBER_TO_EDIT, number);
        db.setPreference(PNames.TEMP_NUMBER, DB.PT.PDefaultValues.EMPTY_PREFERENCE);
        startActivity(new Intent(InventoryActivity.this, EditActivity.class));
    }

    private void updateData() {

        //TODO deb
        Log.d(APP_TAG, "updateData method");

        //region All && All ...
        if (getFilter1().equals(FiltersValues.ALL) && getFilter2().equals(FiltersValues.ALL)) {

            data = db.getAllData();

            //TODO deb
            Log.d(APP_TAG, FiltersValues.ALL);
            Log.d(APP_TAG, FiltersValues.ALL);
        }
        //endregion

        //region All && * ...
        else if (getFilter1().equals(getFilter2()) || (getFilter1().equals(FiltersValues.ALL) ||
                getFilter2().equals(FiltersValues.ALL))) {

            //TODO deb
            Log.d(APP_TAG, FiltersValues.ALL);

            if (getFilter1().equals(FiltersValues.FOLLOWED) ||
                    getFilter2().equals(FiltersValues.FOLLOWED)) {

                data = db.getAllDataIfFollowing(IT.FollowingValues.YES);

                //TODO deb
                Log.d(APP_TAG, FiltersValues.FOLLOWED);

            } else if (getFilter1().equals(FiltersValues.NOT_FOLLOWED) ||
                    getFilter2().equals(FiltersValues.NOT_FOLLOWED)) {

                data = db.getAllDataIfFollowing(IT.FollowingValues.NO);

                //TODO deb
                Log.d(APP_TAG, FiltersValues.NOT_FOLLOWED);

            } else if (getFilter1().equals(FiltersValues.LOCATION_EMPTY) ||
                    getFilter2().equals(FiltersValues.LOCATION_EMPTY)) {

                data = db.getAllDataIfLocation("");

                //TODO deb
                Log.d(APP_TAG, FiltersValues.LOCATION_EMPTY);

            } else if ((getFilter1().contains(FiltersValues.LOCATION_) &&
                    getFilter1().substring(0, FiltersValues.LOCATION_.length()).
                            equals(FiltersValues.LOCATION_))) {

                data = db.getAllDataIfLocation(getFilter1().
                        substring(FiltersValues.LOCATION_.length()));

                //TODO deb
                Log.d(APP_TAG, FiltersValues.LOCATION_);

            } else if (getFilter2().contains(FiltersValues.LOCATION_) &&
                    getFilter2().substring(0, FiltersValues.LOCATION_.length()).
                            equals(FiltersValues.LOCATION_)) {

                data = db.getAllDataIfLocation(getFilter2().
                        substring(FiltersValues.LOCATION_.length()));

                //TODO deb
                Log.d(APP_TAG, FiltersValues.LOCATION_);

            } else if (getFilter1().contains(FiltersValues.STATE_)) {

                data = db.getAllDataIfState(getFilter1().
                        substring(FiltersValues.STATE_.length()));

                //TODO deb
                Log.d(APP_TAG, FiltersValues.STATE_);

            } else if (getFilter2().contains(FiltersValues.STATE_)) {

                data = db.getAllDataIfState(getFilter2().
                        substring(FiltersValues.STATE_.length()));

                //TODO deb
                Log.d(APP_TAG, FiltersValues.STATE_);

            } else if (getFilter1().contains(FiltersValues.TYPE_)) {

                data = db.getAllDataIfType(getFilter1().
                        substring(FiltersValues.TYPE_.length()));

                //TODO deb
                Log.d(APP_TAG, FiltersValues.TYPE_);

            } else if (getFilter2().contains(FiltersValues.TYPE_)) {

                data = db.getAllDataIfType(getFilter2().
                        substring(FiltersValues.TYPE_.length()));

                //TODO deb
                Log.d(APP_TAG, FiltersValues.TYPE_);

            } else if (getFilter1().equals(FiltersValues.OBSERVATION_EMPTY) ||
                    getFilter2().equals(FiltersValues.OBSERVATION_EMPTY)) {

                data = db.getAllDataIfObservation("");

                //TODO deb
                Log.d(APP_TAG, FiltersValues.OBSERVATION_EMPTY);

            } else if (getFilter1().contains(FiltersValues.OBSERVATION_) &&
                    getFilter1().substring(0, FiltersValues.OBSERVATION_.length()).
                            equals(FiltersValues.OBSERVATION_)) {

                data = db.getAllDataIfObservation(getFilter1().
                        substring(FiltersValues.OBSERVATION_.length()));

                //TODO deb
                Log.d(APP_TAG, FiltersValues.OBSERVATION_);

            } else if (getFilter2().contains(FiltersValues.OBSERVATION_) &&
                    getFilter2().substring(0, FiltersValues.OBSERVATION_.length()).
                            equals(FiltersValues.OBSERVATION_)) {

                data = db.getAllDataIfObservation(getFilter2().
                        substring(FiltersValues.OBSERVATION_.length()));

                //TODO deb
                Log.d(APP_TAG, FiltersValues.OBSERVATION_);

            } else if (getFilter1().contains(FiltersValues.AREA_) &&
                    getFilter1().substring(0, FiltersValues.AREA_.length()).
                            equals(FiltersValues.AREA_)) {

                data = db.getAllDataIfArea(getFilter1().
                        substring(FiltersValues.AREA_.length()));

                //TODO deb
                Log.d(APP_TAG, FiltersValues.AREA_);


            } else if (getFilter2().contains(FiltersValues.AREA_) &&
                    getFilter2().substring(0, FiltersValues.AREA_.length()).
                            equals(FiltersValues.AREA_)) {

                data = db.getAllDataIfArea(getFilter2().
                        substring(FiltersValues.AREA_.length()));

                //TODO deb
                Log.d(APP_TAG, FiltersValues.AREA_);

            } else {

                data = null;
                Tools.showToast(InventoryActivity.this, "Eso no es posible!", false);

                //TODO deb
                Log.d(APP_TAG, "AMBIGUOUS");

            }

        }
        //endregion All && * ...

        //region FOLLOWED && * ...
        else if (getFilter1().equals(FiltersValues.FOLLOWED) ||
                getFilter2().equals(FiltersValues.FOLLOWED)) {

            //TODO deb
            Log.d(APP_TAG, FiltersValues.FOLLOWED);

            if (getFilter1().equals(FiltersValues.LOCATION_EMPTY) ||
                    getFilter2().equals(FiltersValues.LOCATION_EMPTY)) {

                data = db.getAllDataIfFollowingAndLocation(IT.FollowingValues.YES, "");

                //TODO deb
                Log.d(APP_TAG, FiltersValues.LOCATION_EMPTY);

            } else if ((getFilter1().contains(FiltersValues.LOCATION_) &&
                    getFilter1().substring(0, FiltersValues.LOCATION_.length()).
                            equals(FiltersValues.LOCATION_))) {

                data = db.getAllDataIfFollowingAndLocation(IT.FollowingValues.YES,
                        getFilter1().substring(FiltersValues.LOCATION_.length()));

                //TODO deb
                Log.d(APP_TAG, FiltersValues.LOCATION_);

            } else if (getFilter2().contains(FiltersValues.LOCATION_) &&
                    getFilter2().substring(0, FiltersValues.LOCATION_.length()).
                            equals(FiltersValues.LOCATION_)) {

                data = db.getAllDataIfFollowingAndLocation(IT.FollowingValues.YES,
                        getFilter2().substring(FiltersValues.LOCATION_.length()));

                //TODO deb
                Log.d(APP_TAG, FiltersValues.LOCATION_);

            } else if (getFilter1().contains(FiltersValues.STATE_)) {

                data = db.getAllDataIfFollowingAndState(IT.FollowingValues.YES,
                        getFilter1().substring(FiltersValues.STATE_.length()));

                //TODO deb
                Log.d(APP_TAG, FiltersValues.STATE_);

            } else if (getFilter2().contains(FiltersValues.STATE_)) {

                //TODO deb
                Log.d(APP_TAG, FiltersValues.STATE_);

                data = db.getAllDataIfFollowingAndState(IT.FollowingValues.YES,
                        getFilter2().substring(FiltersValues.STATE_.length()));

            } else if (getFilter1().contains(FiltersValues.TYPE_)) {

                //TODO deb
                Log.d(APP_TAG, FiltersValues.TYPE_);

                data = db.getAllDataIfFollowingAndType(IT.FollowingValues.YES,
                        getFilter1().substring(FiltersValues.TYPE_.length()));

            } else if (getFilter2().contains(FiltersValues.TYPE_)) {

                //TODO deb
                Log.d(APP_TAG, FiltersValues.TYPE_);

                data = db.getAllDataIfFollowingAndType(IT.FollowingValues.YES,
                        getFilter2().substring(FiltersValues.TYPE_.length()));

            } else if (getFilter1().equals(FiltersValues.OBSERVATION_EMPTY) ||
                    getFilter2().equals(FiltersValues.OBSERVATION_EMPTY)) {

                //TODO deb
                Log.d(APP_TAG, FiltersValues.OBSERVATION_EMPTY);

                data = db.getAllDataIfFollowingAndObservation(IT.FollowingValues.YES, "");

            } else if (getFilter1().contains(FiltersValues.OBSERVATION_) &&
                    getFilter1().substring(0, FiltersValues.OBSERVATION_.length()).
                            equals(FiltersValues.OBSERVATION_)) {

                data = db.getAllDataIfFollowingAndObservation(IT.FollowingValues.YES, getFilter1().
                        substring(FiltersValues.OBSERVATION_.length()));

                //TODO deb
                Log.d(APP_TAG, FiltersValues.OBSERVATION_);

            } else if (getFilter2().contains(FiltersValues.OBSERVATION_) &&
                    getFilter2().substring(0, FiltersValues.OBSERVATION_.length()).
                            equals(FiltersValues.OBSERVATION_)) {

                data = db.getAllDataIfFollowingAndObservation(IT.FollowingValues.YES, getFilter2().
                        substring(FiltersValues.OBSERVATION_.length()));

                //TODO deb
                Log.d(APP_TAG, FiltersValues.OBSERVATION_);

            } else if (getFilter1().contains(FiltersValues.AREA_) &&
                    getFilter1().substring(0, FiltersValues.AREA_.length()).
                            equals(FiltersValues.AREA_)) {

                data = db.getAllDataIfFollowingAndArea(IT.FollowingValues.YES, getFilter1().
                        substring(FiltersValues.AREA_.length()));

                //TODO deb
                Log.d(APP_TAG, FiltersValues.AREA_);


            } else if (getFilter2().contains(FiltersValues.AREA_) &&
                    getFilter2().substring(0, FiltersValues.AREA_.length()).
                            equals(FiltersValues.AREA_)) {

                data = db.getAllDataIfFollowingAndArea(IT.FollowingValues.YES, getFilter2().
                        substring(FiltersValues.AREA_.length()));

                //TODO deb
                Log.d(APP_TAG, FiltersValues.AREA_);

            } else {

                data = null;
                Tools.showToast(InventoryActivity.this, "Eso no es posible!", false);

                //TODO deb
                Log.d(APP_TAG, "AMBIGUOUS");

            }

        }
        //endregion FOLLOWED && * ...

        //region NOT_FOLLOWED && * ...
        else if (getFilter1().equals(FiltersValues.NOT_FOLLOWED) ||
                getFilter2().equals(FiltersValues.NOT_FOLLOWED)) {

            //TODO deb
            Log.d(APP_TAG, FiltersValues.NOT_FOLLOWED);

            if (getFilter1().equals(FiltersValues.LOCATION_EMPTY) ||
                    getFilter2().equals(FiltersValues.LOCATION_EMPTY)) {

                data = db.getAllDataIfFollowingAndLocation(IT.FollowingValues.NO, "");

                //TODO deb
                Log.d(APP_TAG, FiltersValues.LOCATION_EMPTY);

            } else if ((getFilter1().contains(FiltersValues.LOCATION_) &&
                    getFilter1().substring(0, FiltersValues.LOCATION_.length()).
                            equals(FiltersValues.LOCATION_))) {

                data = db.getAllDataIfFollowingAndLocation(IT.FollowingValues.NO, getFilter1().
                        substring(FiltersValues.LOCATION_.length()));

                //TODO deb
                Log.d(APP_TAG, FiltersValues.LOCATION_);

            } else if (getFilter2().contains(FiltersValues.LOCATION_) &&
                    getFilter2().substring(0, FiltersValues.LOCATION_.length()).
                            equals(FiltersValues.LOCATION_)) {

                data = db.getAllDataIfFollowingAndLocation(IT.FollowingValues.NO, getFilter2().
                        substring(FiltersValues.LOCATION_.length()));

                //TODO deb
                Log.d(APP_TAG, FiltersValues.LOCATION_);

            } else if (getFilter1().contains(FiltersValues.STATE_)) {

                data = db.getAllDataIfFollowingAndState(IT.FollowingValues.NO, getFilter1().
                        substring(FiltersValues.STATE_.length()));

                //TODO deb
                Log.d(APP_TAG, FiltersValues.STATE_);

            } else if (getFilter2().contains(FiltersValues.STATE_)) {

                data = db.getAllDataIfFollowingAndState(IT.FollowingValues.NO, getFilter2().
                        substring(FiltersValues.STATE_.length()));

                //TODO deb
                Log.d(APP_TAG, FiltersValues.STATE_);

            } else if (getFilter1().contains(FiltersValues.TYPE_)) {

                data = db.getAllDataIfFollowingAndType(IT.FollowingValues.NO, getFilter1().
                        substring(FiltersValues.TYPE_.length()));

                //TODO deb
                Log.d(APP_TAG, FiltersValues.TYPE_);

            } else if (getFilter2().contains(FiltersValues.TYPE_)) {

                data = db.getAllDataIfFollowingAndType(IT.FollowingValues.NO, getFilter2().
                        substring(FiltersValues.TYPE_.length()));

                //TODO deb
                Log.d(APP_TAG, FiltersValues.TYPE_);

            } else if (getFilter1().equals(FiltersValues.OBSERVATION_EMPTY) ||
                    getFilter2().equals(FiltersValues.OBSERVATION_EMPTY)) {

                data = db.getAllDataIfFollowingAndObservation(IT.FollowingValues.NO, "");

                //TODO deb
                Log.d(APP_TAG, FiltersValues.OBSERVATION_EMPTY);

            } else if (getFilter1().contains(FiltersValues.OBSERVATION_) &&
                    getFilter1().substring(0, FiltersValues.OBSERVATION_.length()).
                            equals(FiltersValues.OBSERVATION_)) {

                data = db.getAllDataIfFollowingAndObservation(IT.FollowingValues.NO, getFilter1().
                        substring(FiltersValues.OBSERVATION_.length()));

                //TODO deb
                Log.d(APP_TAG, FiltersValues.OBSERVATION_);

            } else if (getFilter2().contains(FiltersValues.OBSERVATION_) &&
                    getFilter2().substring(0, FiltersValues.OBSERVATION_.length()).
                            equals(FiltersValues.OBSERVATION_)) {

                data = db.getAllDataIfFollowingAndObservation(IT.FollowingValues.NO, getFilter2().
                        substring(FiltersValues.OBSERVATION_.length()));

                //TODO deb
                Log.d(APP_TAG, FiltersValues.OBSERVATION_);

            } else if (getFilter1().contains(FiltersValues.AREA_) &&
                    getFilter1().substring(0, FiltersValues.AREA_.length()).
                            equals(FiltersValues.AREA_)) {

                data = db.getAllDataIfFollowingAndArea(IT.FollowingValues.NO, getFilter1().
                        substring(FiltersValues.AREA_.length()));

                //TODO deb
                Log.d(APP_TAG, FiltersValues.AREA_);


            } else if (getFilter2().contains(FiltersValues.AREA_) &&
                    getFilter2().substring(0, FiltersValues.AREA_.length()).
                            equals(FiltersValues.AREA_)) {

                data = db.getAllDataIfFollowingAndArea(IT.FollowingValues.NO, getFilter2().
                        substring(FiltersValues.AREA_.length()));

                //TODO deb
                Log.d(APP_TAG, FiltersValues.AREA_);

            } else {

                data = null;
                Tools.showToast(InventoryActivity.this, "Eso no es posible!", false);

                //TODO deb
                Log.d(APP_TAG, "AMBIGUOUS");

            }

        }
        //endregion NOT_FOLLOWED && * ...

        //region LOCATION_EMPTY && * ...
        else if (getFilter1().equals(FiltersValues.LOCATION_EMPTY) ||
                getFilter2().equals(FiltersValues.LOCATION_EMPTY)) {

            //TODO deb
            Log.d(APP_TAG, FiltersValues.LOCATION_EMPTY);

            if (getFilter1().contains(FiltersValues.STATE_)) {

                data = db.getAllDataIfLocationAndState("", getFilter1().
                        substring(FiltersValues.STATE_.length()));

                //TODO deb
                Log.d(APP_TAG, FiltersValues.STATE_);

            } else if (getFilter2().contains(FiltersValues.STATE_)) {

                data = db.getAllDataIfLocationAndState("", getFilter2().
                        substring(FiltersValues.STATE_.length()));

                //TODO deb
                Log.d(APP_TAG, FiltersValues.STATE_);

            } else if (getFilter1().contains(FiltersValues.TYPE_)) {

                data = db.getAllDataIfLocationAndType("", getFilter1().
                        substring(FiltersValues.TYPE_.length()));

                //TODO deb
                Log.d(APP_TAG, FiltersValues.TYPE_);

            } else if (getFilter2().contains(FiltersValues.TYPE_)) {

                data = db.getAllDataIfLocationAndType("", getFilter2().
                        substring(FiltersValues.TYPE_.length()));

                //TODO deb
                Log.d(APP_TAG, FiltersValues.TYPE_);

            } else if (getFilter1().equals(FiltersValues.OBSERVATION_EMPTY) ||
                    getFilter2().equals(FiltersValues.OBSERVATION_EMPTY)) {

                data = db.getAllDataIfLocationAndObservation("", "");

                //TODO deb
                Log.d(APP_TAG, FiltersValues.OBSERVATION_EMPTY);

            } else if (getFilter1().contains(FiltersValues.OBSERVATION_) &&
                    getFilter1().substring(0, FiltersValues.OBSERVATION_.length()).
                            equals(FiltersValues.OBSERVATION_)) {

                data = db.getAllDataIfLocationAndObservation("", getFilter1().
                        substring(FiltersValues.OBSERVATION_.length()));

                //TODO deb
                Log.d(APP_TAG, FiltersValues.OBSERVATION_);

            } else if (getFilter2().contains(FiltersValues.OBSERVATION_) &&
                    getFilter2().substring(0, FiltersValues.OBSERVATION_.length()).
                            equals(FiltersValues.OBSERVATION_)) {

                data = db.getAllDataIfLocationAndObservation("", getFilter2().
                        substring(FiltersValues.OBSERVATION_.length()));

                //TODO deb
                Log.d(APP_TAG, FiltersValues.OBSERVATION_);

            } else if (getFilter1().contains(FiltersValues.AREA_) &&
                    getFilter1().substring(0, FiltersValues.AREA_.length()).
                            equals(FiltersValues.AREA_)) {

                data = db.getAllDataIfLocationAndArea("", getFilter1().
                        substring(FiltersValues.AREA_.length()));

                //TODO deb
                Log.d(APP_TAG, FiltersValues.AREA_);


            } else if (getFilter2().contains(FiltersValues.AREA_) &&
                    getFilter2().substring(0, FiltersValues.AREA_.length()).
                            equals(FiltersValues.AREA_)) {

                data = db.getAllDataIfLocationAndArea("", getFilter2().
                        substring(FiltersValues.AREA_.length()));

                //TODO deb
                Log.d(APP_TAG, FiltersValues.AREA_);

            } else {

                data = null;
                Tools.showToast(InventoryActivity.this, "Eso no es posible!", false);

                //TODO deb
                Log.d(APP_TAG, "AMBIGUOUS");

            }

        }

        //endregion LOCATION_EMPTY && * ...

        //region LOCATION_f1 && * ...
        else if (getFilter1().contains(FiltersValues.LOCATION_) &&
                getFilter1().substring(0, FiltersValues.LOCATION_.length()).
                        equals(FiltersValues.LOCATION_)) {
            //TODO deb
            Log.d(APP_TAG, FiltersValues.LOCATION_);

            if (getFilter2().contains(FiltersValues.STATE_)) {

                data = db.getAllDataIfLocationAndState(getFilter1().
                                substring(FiltersValues.LOCATION_.length()),
                        getFilter2().substring(FiltersValues.STATE_.length()));

                //TODO deb
                Log.d(APP_TAG, FiltersValues.STATE_);

            } else if (getFilter2().contains(FiltersValues.TYPE_)) {

                data = db.getAllDataIfLocationAndType(getFilter1().
                                substring(FiltersValues.LOCATION_.length()),
                        getFilter2().substring(FiltersValues.TYPE_.length()));

                //TODO deb
                Log.d(APP_TAG, FiltersValues.TYPE_);

            } else if (getFilter2().equals(FiltersValues.OBSERVATION_EMPTY)) {

                data = db.getAllDataIfLocationAndObservation(getFilter1().
                        substring(FiltersValues.LOCATION_.length()), "");

                //TODO deb
                Log.d(APP_TAG, FiltersValues.OBSERVATION_EMPTY);

            } else if (getFilter2().contains(FiltersValues.OBSERVATION_) &&
                    getFilter2().substring(0, FiltersValues.OBSERVATION_.length()).
                            equals(FiltersValues.OBSERVATION_)) {

                data = db.getAllDataIfLocationAndObservation(getFilter1().
                                substring(FiltersValues.LOCATION_.length()),
                        getFilter2().substring(FiltersValues.OBSERVATION_.length()));

                //TODO deb
                Log.d(APP_TAG, FiltersValues.OBSERVATION_);

            } else if (getFilter2().contains(FiltersValues.AREA_) &&
                    getFilter2().substring(0, FiltersValues.AREA_.length()).
                            equals(FiltersValues.AREA_)) {

                data = db.getAllDataIfLocationAndArea(getFilter1().
                                substring(FiltersValues.LOCATION_.length()),
                        getFilter2().substring(FiltersValues.AREA_.length()));

                //TODO deb
                Log.d(APP_TAG, FiltersValues.AREA_);

            } else {

                data = null;
                Tools.showToast(InventoryActivity.this, "Eso no es posible!", false);

                //TODO deb
                Log.d(APP_TAG, "AMBIGUOUS");

            }

        }
        //endregion LOCATION_f1 && * ...

        //region LOCATION_f2 && * ...
        else if (getFilter2().contains(FiltersValues.LOCATION_) &&
                getFilter2().substring(0, FiltersValues.LOCATION_.length()).
                        equals(FiltersValues.LOCATION_)) {

            //TODO Deb
            Log.d(APP_TAG, FiltersValues.LOCATION_);

            if (getFilter1().contains(FiltersValues.STATE_)) {

                data = db.getAllDataIfLocationAndState(getFilter2().
                                substring(FiltersValues.LOCATION_.length()),
                        getFilter1().substring(FiltersValues.STATE_.length()));

                //TODO deb
                Log.d(APP_TAG, FiltersValues.STATE_);

            } else if (getFilter1().contains(FiltersValues.TYPE_)) {

                data = db.getAllDataIfLocationAndType(getFilter2().
                                substring(FiltersValues.LOCATION_.length()),
                        getFilter1().substring(FiltersValues.TYPE_.length()));

                //TODO deb
                Log.d(APP_TAG, FiltersValues.TYPE_);

            } else if (getFilter1().equals(FiltersValues.OBSERVATION_EMPTY)) {

                data = db.getAllDataIfLocationAndObservation(getFilter2().
                        substring(FiltersValues.LOCATION_.length()), "");

                //TODO deb
                Log.d(APP_TAG, FiltersValues.OBSERVATION_EMPTY);

            } else if (getFilter1().contains(FiltersValues.OBSERVATION_) &&
                    getFilter1().substring(0, FiltersValues.OBSERVATION_.length()).
                            equals(FiltersValues.OBSERVATION_)) {

                data = db.getAllDataIfLocationAndObservation(getFilter2().
                                substring(FiltersValues.LOCATION_.length()),
                        getFilter1().substring(FiltersValues.OBSERVATION_.length()));

                //TODO deb
                Log.d(APP_TAG, FiltersValues.OBSERVATION_);

            } else if (getFilter1().contains(FiltersValues.AREA_) &&
                    getFilter1().substring(0, FiltersValues.AREA_.length()).
                            equals(FiltersValues.AREA_)) {

                data = db.getAllDataIfLocationAndArea(getFilter2().
                                substring(FiltersValues.LOCATION_.length()),
                        getFilter1().substring(FiltersValues.AREA_.length()));

                //TODO deb
                Log.d(APP_TAG, FiltersValues.AREA_);


            } else {

                data = null;
                Tools.showToast(InventoryActivity.this, "Eso no es posible!", false);

                //TODO deb
                Log.d(APP_TAG, "AMBIGUOUS");

            }

        }
        //endregion LOCATION_f2 && * ...

        //region STATE_f1 && * ...
        else if (getFilter1().contains(FiltersValues.STATE_)) {

            //TODO Deb
            Log.d(APP_TAG, FiltersValues.STATE_);

            if (getFilter2().contains(FiltersValues.TYPE_)) {

                data = db.getAllDataIfStateAndType(getFilter1().
                                substring(FiltersValues.STATE_.length()),
                        getFilter2().substring(FiltersValues.TYPE_.length()));

                //TODO deb
                Log.d(APP_TAG, FiltersValues.TYPE_);

            } else if (getFilter2().equals(FiltersValues.OBSERVATION_EMPTY)) {

                data = db.getAllDataIfStateAndObservation(getFilter1().
                        substring(FiltersValues.STATE_.length()), "");

                //TODO deb
                Log.d(APP_TAG, FiltersValues.OBSERVATION_EMPTY);

            } else if (getFilter2().contains(FiltersValues.OBSERVATION_) &&
                    getFilter2().substring(0, FiltersValues.OBSERVATION_.length()).
                            equals(FiltersValues.OBSERVATION_)) {

                data = db.getAllDataIfStateAndObservation(getFilter1().
                                substring(FiltersValues.STATE_.length()),
                        getFilter2().substring(FiltersValues.OBSERVATION_.length()));

                //TODO deb
                Log.d(APP_TAG, FiltersValues.OBSERVATION_);

            } else if (getFilter2().contains(FiltersValues.AREA_) &&
                    getFilter2().substring(0, FiltersValues.AREA_.length()).
                            equals(FiltersValues.AREA_)) {

                data = db.getAllDataIfStateAndArea(getFilter1().
                                substring(FiltersValues.STATE_.length()),
                        getFilter2().substring(FiltersValues.AREA_.length()));

                //TODO deb
                Log.d(APP_TAG, FiltersValues.AREA_);

            } else {

                data = null;
                Tools.showToast(InventoryActivity.this, "Eso no es posible!", false);

                //TODO deb
                Log.d(APP_TAG, "AMBIGUOUS");

            }

        }
        //endregion STATE_f1 && * ...

        //region STATE_f2 && * ...
        else if (getFilter2().contains(FiltersValues.STATE_)) {

            //TODO Deb
            Log.d(APP_TAG, FiltersValues.STATE_);

            if (getFilter1().contains(FiltersValues.TYPE_)) {

                data = db.getAllDataIfStateAndType(getFilter2().
                                substring(FiltersValues.STATE_.length()),
                        getFilter1().substring(FiltersValues.TYPE_.length()));

                //TODO deb
                Log.d(APP_TAG, FiltersValues.TYPE_);

            } else if (getFilter1().equals(FiltersValues.OBSERVATION_EMPTY)) {

                data = db.getAllDataIfStateAndObservation(getFilter2().
                        substring(FiltersValues.STATE_.length()), "");

                //TODO deb
                Log.d(APP_TAG, FiltersValues.OBSERVATION_EMPTY);

            } else if (getFilter1().contains(FiltersValues.OBSERVATION_) &&
                    getFilter1().substring(0, FiltersValues.OBSERVATION_.length()).
                            equals(FiltersValues.OBSERVATION_)) {

                data = db.getAllDataIfStateAndObservation(getFilter2().
                                substring(FiltersValues.STATE_.length()),
                        getFilter1().substring(FiltersValues.OBSERVATION_.length()));

                //TODO deb
                Log.d(APP_TAG, FiltersValues.OBSERVATION_);

            } else if (getFilter1().contains(FiltersValues.AREA_) &&
                    getFilter1().substring(0, FiltersValues.AREA_.length()).
                            equals(FiltersValues.AREA_)) {

                data = db.getAllDataIfStateAndArea(getFilter2().
                                substring(FiltersValues.STATE_.length()),
                        getFilter1().substring(FiltersValues.AREA_.length()));

                //TODO deb
                Log.d(APP_TAG, FiltersValues.AREA_);


            } else {

                data = null;
                Tools.showToast(InventoryActivity.this, "Eso no es posible!", false);

                //TODO deb
                Log.d(APP_TAG, "AMBIGUOUS");

            }

        }
        //endregion STATE_f2 && * ...

        //region TYPE_f1 && * ...
        else if (getFilter1().contains(FiltersValues.TYPE_)) {

            //TODO deb
            Log.d(APP_TAG, FiltersValues.TYPE_);

            if (getFilter2().equals(FiltersValues.OBSERVATION_EMPTY)) {

                data = db.getAllDataIfTypeAndObservation(getFilter1().
                        substring(FiltersValues.TYPE_.length()), "");

                //TODO deb
                Log.d(APP_TAG, FiltersValues.OBSERVATION_EMPTY);

            } else if (getFilter2().contains(FiltersValues.OBSERVATION_) &&
                    getFilter2().substring(0, FiltersValues.OBSERVATION_.length()).
                            equals(FiltersValues.OBSERVATION_)) {

                data = db.getAllDataIfTypeAndObservation(getFilter1().
                                substring(FiltersValues.TYPE_.length()),
                        getFilter2().substring(FiltersValues.OBSERVATION_.length()));

                //TODO deb
                Log.d(APP_TAG, FiltersValues.OBSERVATION_);

            } else if (getFilter2().contains(FiltersValues.AREA_) &&
                    getFilter2().substring(0, FiltersValues.AREA_.length()).
                            equals(FiltersValues.AREA_)) {

                data = db.getAllDataIfTypeAndArea(getFilter1().
                                substring(FiltersValues.TYPE_.length()),
                        getFilter2().substring(FiltersValues.AREA_.length()));

                //TODO deb
                Log.d(APP_TAG, FiltersValues.AREA_);

            } else {

                data = null;
                Tools.showToast(InventoryActivity.this, "Eso no es posible!", false);

                //TODO deb
                Log.d(APP_TAG, "AMBIGUOUS");

            }

        }
        //endregion TYPE_f1 && * ...

        //region TYPE_f2 && ...
        else if (getFilter2().contains(FiltersValues.TYPE_)) {

            //TODO Deb
            Log.d(APP_TAG, FiltersValues.TYPE_);

            if (getFilter1().equals(FiltersValues.OBSERVATION_EMPTY)) {

                data = db.getAllDataIfTypeAndObservation(getFilter2().
                        substring(FiltersValues.TYPE_.length()), "");

                //TODO deb
                Log.d(APP_TAG, FiltersValues.OBSERVATION_EMPTY);

            } else if (getFilter1().contains(FiltersValues.OBSERVATION_) &&
                    getFilter1().substring(0, FiltersValues.OBSERVATION_.length()).
                            equals(FiltersValues.OBSERVATION_)) {

                data = db.getAllDataIfTypeAndObservation(getFilter2().
                                substring(FiltersValues.TYPE_.length()),
                        getFilter1().substring(FiltersValues.OBSERVATION_.length()));

                //TODO deb
                Log.d(APP_TAG, FiltersValues.OBSERVATION_);

            } else if (getFilter1().contains(FiltersValues.AREA_) &&
                    getFilter1().substring(0, FiltersValues.AREA_.length()).
                            equals(FiltersValues.AREA_)) {

                data = db.getAllDataIfTypeAndArea(getFilter2().
                        substring(FiltersValues.TYPE_.length()), getFilter1().
                        substring(FiltersValues.AREA_.length()));

                //TODO deb
                Log.d(APP_TAG, FiltersValues.AREA_);


            } else {

                data = null;
                Tools.showToast(InventoryActivity.this, "Eso no es posible!", false);

                //TODO deb
                Log.d(APP_TAG, "AMBIGUOUS");

            }

        }
        //endregion TYPE_f2 && * ...

        //region OBSERVATION_EMPTY && * ...
        else if (getFilter1().equals(FiltersValues.OBSERVATION_EMPTY) ||
                getFilter2().equals(FiltersValues.OBSERVATION_EMPTY)) {

            //TODO Deb
            Log.d(APP_TAG, FiltersValues.OBSERVATION_EMPTY);

            if (getFilter1().contains(FiltersValues.AREA_) &&
                    getFilter1().substring(0, FiltersValues.AREA_.length()).
                            equals(FiltersValues.AREA_)) {

                data = db.getAllDataIfObservationAndArea("", getFilter1().
                        substring(FiltersValues.AREA_.length()));

                //TODO deb
                Log.d(APP_TAG, FiltersValues.AREA_);


            } else if (getFilter2().contains(FiltersValues.AREA_) &&
                    getFilter2().substring(0, FiltersValues.AREA_.length()).
                            equals(FiltersValues.AREA_)) {

                data = db.getAllDataIfObservationAndArea("", getFilter2().
                        substring(FiltersValues.AREA_.length()));

                //TODO deb
                Log.d(APP_TAG, FiltersValues.AREA_);

            } else {

                data = null;

                Tools.showToast(InventoryActivity.this, "Eso no es posible!", false);

                //TODO deb
                Log.d(APP_TAG, "AMBIGUOUS");

            }

        }
        //endregion OBSERVATION_EMPTY && * ...

        //region OBSERVATION_f1 && * ...
        else if (getFilter1().contains(FiltersValues.OBSERVATION_) &&
                getFilter1().substring(0, FiltersValues.OBSERVATION_.length()).
                        equals(FiltersValues.OBSERVATION_)) {

            //TODO deb
            Log.d(APP_TAG, FiltersValues.OBSERVATION_);

            if (getFilter2().contains(FiltersValues.AREA_) &&
                    getFilter2().substring(0, FiltersValues.AREA_.length()).
                            equals(FiltersValues.AREA_)) {

                data = db.getAllDataIfObservationAndArea(getFilter1().
                        substring(FiltersValues.OBSERVATION_.length()), getFilter2().
                        substring(FiltersValues.AREA_.length()));

                //TODO deb
                Log.d(APP_TAG, FiltersValues.AREA_);

            } else {

                data = null;

                Tools.showToast(InventoryActivity.this, "Eso no es posible!", false);

                //TODO deb
                Log.d(APP_TAG, "AMBIGUOUS");

            }

        }
        //endregion OBSERVATION_f1 && * ...

        //region OBSERVATION_f2 && * ...
        else if (getFilter2().contains(FiltersValues.OBSERVATION_) &&
                getFilter2().substring(0, FiltersValues.OBSERVATION_.length()).
                        equals(FiltersValues.OBSERVATION_)) {

            //TODO deb
            Log.d(APP_TAG, FiltersValues.OBSERVATION_);

            if (getFilter1().contains(FiltersValues.AREA_) &&
                    getFilter1().substring(0, FiltersValues.AREA_.length()).
                            equals(FiltersValues.AREA_)) {

                data = db.getAllDataIfObservationAndArea(getFilter2().
                        substring(FiltersValues.OBSERVATION_.length()), getFilter1().
                        substring(FiltersValues.AREA_.length()));

                //TODO deb
                Log.d(APP_TAG, FiltersValues.AREA_);

            } else {

                data = null;

                Tools.showToast(InventoryActivity.this, "Eso no es posible!", false);

                //TODO deb
                Log.d(APP_TAG, "AMBIGUOUS");

            }

        }
        //endregion OBSERVATION_f2 && * ...

        //region DEFAULT...
        else {

            data = null;
            Tools.showToast(InventoryActivity.this, "Eso no es posible!", false);

            //TODO deb
            Log.d(APP_TAG, "AMBIGUOUS");

        }
        //endregion DEFAULT..


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

        //CSVData
        data.moveToPosition(getIndex() - 1);

        //dataToDisplay
        dataToDisplay = new ArrayList<>();
        for (int i = 0; i < WINDOW; i++) {
            dataToDisplay.add("");
        }

        //filling with CSVData
        StringBuilder sb;
        Boolean[] itemsToDisplay = getPreferenceItemsToDisplay();
        for (int i = 0; i < WINDOW && data.moveToNext(); i++) {
            sb = new StringBuilder();
            sb.append("\n");
            sb.append("* Número: ").append(data.getString(IT.Indexes.NUMBER_COLUMN_INDEX));
            sb.append("\n");
            sb.append("\n");
            if (itemsToDisplay[0]) {
                sb.append("- Descripción: ");
                sb.append(data.getString(IT.Indexes.DESCRIPTION_COLUMN_INDEX));
                sb.append("\n");
                sb.append("\n");
            }
            if (itemsToDisplay[1]) {
                sb.append("- Área: ");
                sb.append(data.getString(IT.Indexes.AREA_COLUMN_INDEX));
                sb.append("\n");
                sb.append("\n");
            }
            if (itemsToDisplay[2]) {
                sb.append("- Estado: ");
                sb.append(IT.StateValues.toString(data.getInt(IT.Indexes.STATE_COLUMN_INDEX)));
                sb.append("\n");
                sb.append("\n");
            }
            if (itemsToDisplay[3]) {
                sb.append("- Última act. del estado: ");
                sb.append(Tools.formatDate(data.getString(IT.Indexes.LAST_CHECKING_COLUMN_INDEX)));
                sb.append("\n");
                sb.append("\n");
            }
            if (itemsToDisplay[4]) {
                sb.append("- Tipo: ");
                sb.append(IT.TypeValues.toString(data.getInt(IT.Indexes.TYPE_COLUMN_INDEX)));
                sb.append("\n");
                sb.append("\n");
            }
            if (itemsToDisplay[5]) {
                sb.append("- Localización: ");
                sb.append(data.getString(IT.Indexes.LOCATION_COLUMN_INDEX));
                sb.append("\n");
                sb.append("\n");
            }
            if (itemsToDisplay[6]) {
                sb.append("- Observación: ");
                sb.append(data.getString(IT.Indexes.OBSERVATION_COLUMN_INDEX));
                sb.append("\n");
            }

            dataToDisplay.set(i, sb.toString());
        }

    }

    private void displayData() {

        if (data == null || data.getCount() == 0) {
            String s = format(0, INFO_LENGHT) +
                    "/" + format(0, INFO_LENGHT) + " de "
                    + format(0, INFO_LENGHT);
            textView.setText(s);
        } else {
            String s = format(getIndex() + 1, INFO_LENGHT) + "/" +
                    format(getIndex() + WINDOW, INFO_LENGHT) + " de " +
                    format(data.getCount(), INFO_LENGHT);
            textView.setText(s);
        }
        AppStatics.formatView(InventoryActivity.this, dataToDisplay, listView);

    }

    private String format(int index, int lenght) {

        String s = String.valueOf(index);
        while (s.length() < lenght) {
            s = "0" + s;
        }
        return s;
    }

    private String getFilter1() {
        return db.getPreference(PNames.CURRENT_FILTER1_VALUE);
    }

    private void setFilter1(String filter) {
        db.setPreference(PNames.CURRENT_FILTER1_VALUE, filter);
    }

    private String getFilter2() {
        return db.getPreference(PNames.CURRENT_FILTER2_VALUE);
    }

    private void setFilter2(String filter) {
        db.setPreference(PNames.CURRENT_FILTER2_VALUE, filter);
    }

    private int getIndex() {
        return Integer.valueOf(db.getPreference(PNames.CURRENT_INVENTORY_INDEX));
    }

    private void setIndex(int index) {
        db.setPreference(PNames.CURRENT_INVENTORY_INDEX, index);
    }

    private void callQRDecoder(int requestCode) {
        try {
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes
            startActivityForResult(intent, requestCode);

        } catch (Exception e) {

            Toast.makeText(getApplicationContext(), "Error: Debe instalar primero la aplicación " +
                            "de escanear código QR: com.google.zxing.client.android-4.7.3-103-minAPI15",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void callSearchActivity() {
        db.setPreference(DB.PT.PNames.TEMP_SEARCH_CRITERIA, DB.PT.PDefaultValues.EMPTY_PREFERENCE);
        startActivity(new Intent(InventoryActivity.this, SearchActivity.class));
    }

    private void callNewNumberActivity() {
        db.setPreference(DB.PT.PNames.NUMBER_TO_EDIT, DB.PT.PDefaultValues.EMPTY_PREFERENCE);
        db.setPreference(DB.PT.PNames.TEMP_NUMBER, DB.PT.PDefaultValues.EMPTY_PREFERENCE);
        db.setPreference(DB.PT.PNames.TEMP_DESCRIPTION, DB.PT.PDefaultValues.EMPTY_PREFERENCE);
        db.setPreference(DB.PT.PNames.TEMP_LOCATION, DB.PT.PDefaultValues.EMPTY_PREFERENCE);
        db.setPreference(DB.PT.PNames.TEMP_FOLLOWING, IT.FollowingValues.NO);
        db.setPreference(DB.PT.PNames.TEMP_STATE, IT.StateValues.LEFTOVER);
        db.setPreference(DB.PT.PNames.TEMP_TYPE, IT.TypeValues.UNKNOWN);
        db.setPreference(DB.PT.PNames.TEMP_OBSERVATION, DB.PT.PDefaultValues.EMPTY_PREFERENCE);
        startActivity(new Intent(InventoryActivity.this, NewNumberActivity.class));
    }

    private void callReportActivity() {
        if (data == null || data.getCount() == 0) {
            Tools.showToast(InventoryActivity.this, "No hay números seleccionados!!!", false);
        } else {
            ReportActivity.setData(data);
            startActivity(new Intent(InventoryActivity.this, ReportActivity.class));
        }
    }

    private void callExportInventoryActivity() {
        if (data == null || data.getCount() == 0) {
            Tools.showToast(InventoryActivity.this, "No hay números seleccionados!!!", false);
        } else {
            BackUpActivity.setData(data);
            startActivity(new Intent(InventoryActivity.this, BackUpActivity.class));
        }
    }

    private void callQrFactory() {
        if (data == null || data.getCount() == 0) {
            Tools.showToast(InventoryActivity.this, "No hay números seleccionados!!!", false);
        } else {
            PrintableQRsFactoryActivity.setData(data);
            PrintableQRsFactoryActivity.setImporting(false);
            db.setPreference(PNames.P_QR_CURRENT_INDEX, "0");
            startActivity(new Intent(InventoryActivity.this, PrintableQRsFactoryActivity.class));
        }
    }

    private Boolean[] getPreferenceItemsToDisplay() {
        Boolean[] toReturn = new Boolean[7];
        Arrays.fill(toReturn, false);
        String[] items = AppStatics.db.getPreference(DB.PT.PNames.FIELDS_TO_DISPLAY_CSV).split(",", -1);
        for (int i = 0; i < items.length; i++) {
            toReturn[i] = items[i].equals(DB.PT.PDefaultValues.YES);
        }

        return toReturn;
    }
}
