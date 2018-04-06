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
    private static final String EMPTY = "Vacía...";
    private static final String YES_VALUE = "Sí";
    private static final String NO_VALUE = "No";

    //Data
    private String location, observation;
    private int following, state, type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_activity);


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

                db.updateLocation(getNumber(), locationET.getText().toString());
                db.updateObservation(getNumber(), observationET.getText().toString());
                db.updateFollowing(getNumber(), getTempFollowing());
                db.updateTypeIfDescription(db.getNumberDescription(getNumber()), getTempType());
                db.updateState(getNumber(), getTempState());

                db.updateLocation(getNumber(), locationET.getText().toString());
                db.updateObservation(getNumber(), observationET.getText().toString());
                Tools.showToast(EditActivity.this, "Cambios guardados!", false);
            }
        });

        //DB
        db = AppStatics.db;

    }

    @Override
    protected void onResume() {
        super.onResume();

        //region Values

        if (getNumber().equals(getTempNumber())) {
            state = getTempState();
            type = getTempType();
            location = getTempLocation();
            observation = getTempObservation();
            following = getTempFollowing();
        } else {
            state = db.getNumberState(getNumber());
            type = db.getNumberType(getNumber());
            location = db.getNumberLocation(getNumber());
            observation = db.getNumberObservation(getNumber());
            following = db.getNumberFollowingValue(getNumber());
            setTempNumber(getNumber());
            setTempType(type);
            setTempState(state);
            setTempLocation(location);
            setTempObservation(observation);
            setTempFollowing(following);

        }

        numberTV.setText(getNumber());
        descriptionTV.setText(db.getNumberDescription(getNumber()));
        areaTV.setText(db.getNumberArea(getNumber()));
        altaDateTV.setText(db.getNumberAltaDate(getNumber()));
        officialUpdateTV.setText(db.getNumberOfficialUpdate(getNumber()));
        lastCheckingTV.setText(Tools.formatDate(db.getNumberLastChecking(getNumber())));


        locationET.setText(location);
        observationET.setText(observation);

        //region following spinner
        if (Tools.contain(AppStatics.AreasToFollow.areasToFollow, db.getNumberArea(getNumber()))) {
            followingS.setAdapter(new ArrayAdapter<>(EditActivity.this,
                    android.R.layout.simple_list_item_1, new String[]{YES_VALUE}));
            followingS.setFocusable(false);
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
            followingS.setSelection(following);
            followingS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String selectedItem = (String) adapterView.getSelectedItem();
                    if (selectedItem.equals(YES_VALUE)) {
                        setTempFollowing(1);
                    } else {
                        setTempFollowing(0);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }

        //endregion following spinner

        //region state spinner
        if (state == DB.IT.StateValues.LEFTOVER) {
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

        } else if (state == DB.IT.StateValues.MISSING ||
                state == DB.IT.StateValues.IGNORED_MISSING) {
            ArrayList<String> stateAL = new ArrayList<>();
            stateAL.add(DB.IT.StateValues.toString(DB.IT.StateValues.MISSING));
            stateAL.add(DB.IT.StateValues.toString(DB.IT.StateValues.IGNORED_MISSING));
            stateS.setAdapter(new ArrayAdapter<>(EditActivity.this,
                    android.R.layout.simple_list_item_1, stateAL));
            stateS.setSelection(Tools.getIndexOf(stateAL, DB.IT.StateValues.toString(state)));
            stateS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    setTempState(DB.IT.StateValues.parse((String) adapterView.getSelectedItem()));
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
            stateS.setSelection(Tools.getIndexOf(stateAL, DB.IT.StateValues.toString(state)));
            stateS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    setTempState(DB.IT.StateValues.parse((String) adapterView.getSelectedItem()));
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

        }

        //endregion

        //region type spinner
        ArrayList<String> typeAL = new ArrayList<>();
        typeAL.add(DB.IT.TypeValues.toString(DB.IT.TypeValues.UNKNOWN));
        typeAL.add(DB.IT.TypeValues.toString(DB.IT.TypeValues.FURNISHING));
        typeAL.add(DB.IT.TypeValues.toString(DB.IT.TypeValues.EQUIPMENT));
        typeS.setAdapter(new ArrayAdapter<>(EditActivity.this,
                android.R.layout.simple_list_item_1, typeAL));
        typeS.setSelection(Tools.getIndexOf(typeAL, DB.IT.TypeValues.toString(type)));
        typeS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setTempType(DB.IT.TypeValues.parse((String) adapterView.getSelectedItem()));

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //endregion

        //region location spinner
        final ArrayList<String> locationAL = new ArrayList<>();
        locationAL.add(LOCATIONS);
        Collections.addAll(locationAL, AppStatics.Location.locations);
        for (int i = 0; i < locationAL.size(); i++) {
            if (locationAL.get(i).equals("")) {
                locationAL.set(i, EMPTY);
                break;
            }
        }
        locationsS.setAdapter(new ArrayAdapter<>(EditActivity.this,
                android.R.layout.simple_list_item_1, locationAL));
        locationsS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = (String) adapterView.getSelectedItem();
                if (!selectedItem.equals(LOCATIONS)) {

                    if (selectedItem.equals(EMPTY)) {
                        locationET.setText("");
                        setTempLocation("");
                        locationsS.setSelection(0);
                    } else {
                        locationET.setText(selectedItem);
                        setTempLocation(selectedItem);
                        locationsS.setSelection(0);
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        //endregion location spinner

        //region observation spinner
        final ArrayList<String> observationAL = new ArrayList<>();
        observationAL.add(OBSERVATION);
        Collections.addAll(observationAL, AppStatics.Observation.observations);
        for (int i = 0; i < observationAL.size(); i++) {
            if (observationAL.get(i).equals("")) {
                observationAL.set(i, EMPTY);
            }
        }
        observationsS.setAdapter(new ArrayAdapter<>(EditActivity.this,
                android.R.layout.simple_list_item_1, observationAL));
        observationsS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = (String) adapterView.getSelectedItem();
                if (!selectedItem.equals(OBSERVATION)) {

                    if (selectedItem.equals(EMPTY)) {
                        observationET.setText("");
                        setTempObservation("");
                        observationsS.setSelection(0);
                    } else {
                        observationET.setText(selectedItem);
                        setTempObservation(selectedItem);
                        observationsS.setSelection(0);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //endregion location spinner


        //endregion

    }

    private String getNumber() {
        return db.getPreference(DB.RT.NUMBER_TO_EDIT);
    }

    private String getTempNumber() {
        return db.getPreference(DB.RT.TEMP_NUMBER);
    }

    private int getTempState() {
        return Integer.parseInt(db.getPreference(DB.RT.TEMP_STATE));
    }

    private int getTempType() {
        return Integer.parseInt(db.getPreference(DB.RT.TEMP_TYPE));
    }

    private String getTempLocation() {
        return db.getPreference(DB.RT.TEMP_LOCATION);
    }

    private int getTempFollowing() {
        return Integer.parseInt(db.getPreference(DB.RT.TEMP_FOLLOWING));
    }

    private String getTempObservation() {
        return db.getPreference(DB.RT.TEMP_OBSERVATION);
    }

    private void setTempNumber(String number) {
        db.setPreference(DB.RT.TEMP_NUMBER, number);
    }

    private void setTempState(int state) {
        db.setPreference(DB.RT.TEMP_STATE, state);
    }

    private void setTempType(int type) {
        db.setPreference(DB.RT.TEMP_TYPE, type);
    }

    private void setTempLocation(String location) {
        db.setPreference(DB.RT.TEMP_LOCATION, location);
    }

    private void setTempObservation(String observation) {
        db.setPreference(DB.RT.TEMP_OBSERVATION, observation);
    }

    private void setTempFollowing(int following) {
        db.setPreference(DB.RT.TEMP_FOLLOWING, following);
    }


}
