package com.example.compereirowww.inventory20181.Activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.compereirowww.inventory20181.DataBase.DB;
import com.example.compereirowww.inventory20181.R;
import com.example.compereirowww.inventory20181.Tools.Tools;

import java.util.ArrayList;
import java.util.Collections;

public class EditActivity extends AppCompatActivity {

    //GUI
    private TextView numberTV, descriptionTV, areaTV, altaDateTV, officialUpdateTV, lastCheckingTV;
    private EditText locationET, observationET;
    private Spinner followingS, stateS, typeS, locationsS, observationsS;
    private LinearLayout followingLL, stateLL;

    //DB
    private DB db;

    //STATIC VALUES
    private static final String LOCATIONS = "Localizaciones...";
    private static final String OBSERVATION = "Observaciones...";
    private static final String AREAS = "Areas...";
    private static final String YES_VALUE = "Sí";
    private static final String NO_VALUE = "No";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //GUI
        numberTV = (TextView) findViewById(R.id.number_tv);
        descriptionTV = (TextView) findViewById(R.id.description_tv);
        areaTV = (TextView) findViewById(R.id.area_tv);
        altaDateTV = (TextView) findViewById(R.id.alta_date_tv);
        officialUpdateTV = (TextView) findViewById(R.id.official_update_tv);
        lastCheckingTV = (TextView) findViewById(R.id.last_check_tv);
        locationET = (EditText) findViewById(R.id.location_et);
        locationsS = (Spinner) findViewById(R.id.location_s);
        observationET = (EditText) findViewById(R.id.observation_et);
        observationsS = (Spinner) findViewById(R.id.observation_s);
        followingS = (Spinner) findViewById(R.id.following_s);
        followingLL = (LinearLayout) findViewById(R.id.following_ll);
        stateS = (Spinner) findViewById(R.id.state_s);
        stateLL = (LinearLayout) findViewById(R.id.state_ll);
        typeS = (Spinner) findViewById(R.id.type_s);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (locationET.isFocused()) {
                    locationET.clearFocus();
                } else if (observationsS.isFocused()) {
                    observationET.clearFocus();
                } else {
                    db.updateLocation(getNumber(), locationET.getText().toString());
                    db.updateObservation(getNumber(), observationET.getText().toString());
                    Tools.showToast(EditActivity.this, "Cambios guardados!", false);
                }
            }
        });

        //DB
        db = AppStatics.db;

    }

    @Override
    protected void onResume() {
        super.onResume();

        //region Values
        numberTV.setText(getNumber());
        descriptionTV.setText(db.getNumberDescription(getNumber()));
        areaTV.setText(db.getNumberArea(getNumber()));
        altaDateTV.setText(db.getNumberAltaDate(getNumber()));
        officialUpdateTV.setText(db.getNumberOfficialUpdate(getNumber()));

        //region following spinner
        if (Tools.contain(AppStatics.AreasToFollow.areasToFollow, db.getNumberArea(getNumber()))) {
            followingS.setAdapter(new ArrayAdapter<>(EditActivity.this,
                    android.R.layout.simple_list_item_1, new String[]{YES_VALUE}));
            followingS.setClickable(false);
            followingLL.setOnClickListener(new View.OnClickListener() {
                int c = -1;

                @Override
                public void onClick(View view) {
                    if (c % 2 == 0) {
                        Tools.showToast(EditActivity.this, getString(R.string.text14), false);
                    }
                    c++;
                }
            });
        } else {
            followingS.setAdapter(new ArrayAdapter<>(EditActivity.this,
                    android.R.layout.simple_list_item_1, new String[]{NO_VALUE, YES_VALUE}));
            followingS.setSelection(db.getNumberFollowingValue(getNumber()));
            followingS.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String selectedItem = (String) adapterView.getSelectedItem();
                    String f;
                    if (db.getNumberFollowingValue(getNumber()) == 0) {
                        f = NO_VALUE;
                    } else {
                        f = YES_VALUE;
                    }

                    if (!selectedItem.equals(f)) {
                        if (selectedItem.equals(YES_VALUE)) {
                            db.updateFollowing(getNumber(), true);
                            Tools.showToast(EditActivity.this, "Seguimiento actualizado!", false);
                        } else {
                            db.updateFollowing(getNumber(), false);
                            Tools.showToast(EditActivity.this, "Seguimiento actualizado!", false);
                        }
                    }
                }
            });
        }

        //endregion following spinner

        //region state spinner
        if (db.getNumberState(getNumber()).equals(DB.IT.StateValues.toString(DB.IT.StateValues.LEFTOVER))) {
            stateS.setAdapter(new ArrayAdapter<>(EditActivity.this,
                    android.R.layout.simple_list_item_1,
                    new String[]{DB.IT.StateValues.toString(DB.IT.StateValues.LEFTOVER)}));
            stateS.setClickable(false);
            stateLL.setOnClickListener(new View.OnClickListener() {
                int c = -1;

                @Override
                public void onClick(View view) {

                    if (c % 2 == 0) {
                        Tools.showToast(EditActivity.this, getString(R.string.text15), false);
                    }
                    c++;
                }
            });

        } else if (db.getNumberState(getNumber()).equals(DB.IT.StateValues.toString(DB.IT.StateValues.MISSING)) ||
                db.getNumberState(getNumber()).equals(DB.IT.StateValues.toString(DB.IT.StateValues.IGNORED_MISSING))) {
            ArrayList<String> stateAL = new ArrayList<>();
            stateAL.add(DB.IT.StateValues.toString(DB.IT.StateValues.MISSING));
            stateAL.add(DB.IT.StateValues.toString(DB.IT.StateValues.IGNORED_MISSING));
            stateS.setAdapter(new ArrayAdapter<>(EditActivity.this,
                    android.R.layout.simple_list_item_1, stateAL));
            stateS.setSelection(Tools.getIndexOf(stateAL, db.getNumberState(getNumber())));
            stateS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String selectedItem = (String) adapterView.getSelectedItem();
                    if (!selectedItem.equals(db.getNumberState(getNumber()))) {
                        db.updateState(getNumber(), DB.IT.StateValues.parse(selectedItem));
                        Tools.showToast(EditActivity.this, getString(R.string.text18), false);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        } else {
            ArrayList<String> stateAL = new ArrayList<>();
            stateAL.add(DB.IT.StateValues.toString(DB.IT.StateValues.PRESENT));
            stateAL.add(DB.IT.StateValues.toString(DB.IT.StateValues.MISSING));
            stateAL.add(DB.IT.StateValues.toString(DB.IT.StateValues.IGNORED_MISSING));
            stateS.setAdapter(new ArrayAdapter<>(EditActivity.this,
                    android.R.layout.simple_list_item_1, stateAL));
            stateS.setSelection(Tools.getIndexOf(stateAL, db.getNumberState(getNumber())));

        }

        //endregion

        //region type spinner
        ArrayList<String> typeAL = new ArrayList<>();
        typeAL.add(DB.IT.TypeValues.toString(DB.IT.TypeValues.UNKNOWN));
        typeAL.add(DB.IT.TypeValues.toString(DB.IT.TypeValues.FURNISHING));
        typeAL.add(DB.IT.TypeValues.toString(DB.IT.TypeValues.EQUIPMENT));
        typeS.setAdapter(new ArrayAdapter<>(EditActivity.this,
                android.R.layout.simple_list_item_1, typeAL));
        typeS.setSelection(Tools.getIndexOf(typeAL, db.getNumberType(getNumber())));
        typeS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = (String) adapterView.getSelectedItem();
                if (!selectedItem.equals(db.getNumberType(getNumber()))) {
                    db.updateType(getNumber(), DB.IT.TypeValues.parse(selectedItem));
                    Tools.showToast(EditActivity.this, "Tipo actualizado!", false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //endregion

        //region location spinner and location editText
        final ArrayList<String> locationAL = new ArrayList<>();
        locationAL.add(LOCATIONS);
        Collections.addAll(locationAL, AppStatics.Location.locations);
        locationsS.setAdapter(new ArrayAdapter<>(EditActivity.this,
                android.R.layout.simple_list_item_1, locationAL));
        if (locationAL.contains(db.getNumberLocation(getNumber()))) {
            locationsS.setSelection(Tools.getIndexOf(locationAL, db.getNumberLocation(getNumber())));
        }
        locationET.setText(db.getNumberLocation(getNumber()));
        locationsS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = (String) adapterView.getSelectedItem();
                if (selectedItem.equals(LOCATIONS)) {
                    if (locationAL.contains(db.getNumberLocation(getNumber()))) {
                        locationsS.setSelection(Tools.getIndexOf(locationAL, db.getNumberLocation(getNumber())));
                    }
                    return;
                }

                if (selectedItem.equals(db.getNumberLocation(getNumber()))) {
                    if (!locationET.getText().toString().equals(selectedItem)) {
                        locationET.setText(selectedItem);
                    }
                } else {
                    db.updateLocation(getNumber(), selectedItem);
                    Tools.showToast(EditActivity.this, getString(R.string.text16), false);
                    locationET.setText(selectedItem);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        locationET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (locationET.getText().toString().equals(db.getNumberLocation(getNumber()))) {
                    if (locationAL.contains(locationET.getText().toString())) {
                        locationsS.setSelection(Tools.getIndexOf(locationAL, locationET.getText().toString()));
                    } else {
                        locationsS.setSelection(0);
                    }
                } else {
                    db.updateLocation(getNumber(), locationET.getText().toString());
                    Tools.showToast(EditActivity.this, getString(R.string.text16), false);
                    if (locationAL.contains(locationET.getText().toString())) {
                        locationsS.setSelection(Tools.getIndexOf(locationAL, locationET.getText().toString()));
                    } else {
                        locationsS.setSelection(0);
                    }
                }
            }
        });


        //endregion location spinner and location editText

        //region observation spinner and observation editText
        final ArrayList<String> observationAL = new ArrayList<>();
        observationAL.add(OBSERVATION);
        Collections.addAll(observationAL, AppStatics.Observation.observations);
        observationsS.setAdapter(new ArrayAdapter<>(EditActivity.this,
                android.R.layout.simple_list_item_1, observationAL));
        if (observationAL.contains(db.getNumberObservation(getNumber()))) {
            observationsS.setSelection(Tools.getIndexOf(observationAL, db.getNumberObservation(getNumber())));
        }
        observationET.setText(db.getNumberObservation(getNumber()));
        observationsS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = (String) adapterView.getSelectedItem();
                if (selectedItem.equals(OBSERVATION)) {
                    if (observationAL.contains(db.getNumberObservation(getNumber()))) {
                        observationsS.setSelection(Tools.getIndexOf(observationAL, db.getNumberObservation(getNumber())));
                    }
                    return;
                }

                if (selectedItem.equals(db.getNumberObservation(getNumber()))) {
                    if (!observationET.getText().toString().equals(selectedItem)) {
                        observationET.setText(selectedItem);
                    }
                } else {
                    db.updateObservation(getNumber(), selectedItem);
                    Tools.showToast(EditActivity.this, getString(R.string.text17), false);
                    observationET.setText(selectedItem);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        observationET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (observationET.getText().toString().equals(db.getNumberObservation(getNumber()))) {
                    if (observationAL.contains(observationET.getText().toString())) {
                        observationsS.setSelection(Tools.getIndexOf(observationAL,
                                observationET.getText().toString()));
                    } else {
                        observationsS.setSelection(0);
                    }
                } else {
                    db.updateObservation(getNumber(), observationET.getText().toString());
                    Tools.showToast(EditActivity.this, getString(R.string.text17), false);
                    if (observationAL.contains(observationET.getText().toString())) {
                        observationsS.setSelection(Tools.getIndexOf(observationAL,
                                observationET.getText().toString()));
                    } else {
                        observationsS.setSelection(0);
                    }
                }
            }
        });


        //endregion location spinner and location editText

        lastCheckingTV.setText(Tools.formatDate(db.getNumberLastChecking(getNumber())));


        //endregion

    }

    private String getNumber() {
        return db.getPreference(DB.RT.NUMBER_TO_EDIT);
    }
}
