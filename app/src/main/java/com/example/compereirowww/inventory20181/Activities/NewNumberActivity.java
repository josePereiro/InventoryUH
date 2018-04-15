package com.example.compereirowww.inventory20181.Activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.compereirowww.inventory20181.DataBase.DB;
import com.example.compereirowww.inventory20181.DataBase.DB.IT.TypeValues;
import com.example.compereirowww.inventory20181.DataBase.DB.PT.PDefaultValues;
import com.example.compereirowww.inventory20181.R;
import com.example.compereirowww.inventory20181.Tools.Tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class NewNumberActivity extends AppCompatActivity {

    //GUI
    private TextView areaTV, altaDateTV, officialUpdateTV, lastCheckingTV;
    private EditText numberET;
    private EditText locationET;
    private EditText observationET;
    private EditText descriptionET;
    private Spinner followingS, stateS, typeS, locationsS, observationsS, descriptionS;

    //DB
    DB db;

    //STATIC VALUES
    private static final String LOCATIONS = "Localizaciones...";
    private static final String OBSERVATIONS = "Observaciones...";
    private static final String DESCRIPTIONS = "Descripciones...";
    private static final String EMPTY = "Vacía...";
    private static final String YES = "Sí";
    private static final String NO = "No";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_number);

        //DB
        db = AppStatics.db;

        //GUI
        numberET = (EditText) findViewById(R.id.number_et);
        numberET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String number = editable.toString();
                number = number.replaceAll(",", "").
                        replaceAll(" ", "");
                setNumber(number);
            }
        });
        descriptionET = (EditText) findViewById(R.id.description_et);
        descriptionET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                setTempDescription(editable.toString());
            }
        });
        descriptionS = (Spinner) findViewById(R.id.description_s);
        areaTV = (TextView) findViewById(R.id.area_tv);
        altaDateTV = (TextView) findViewById(R.id.alta_date_tv);
        officialUpdateTV = (TextView) findViewById(R.id.official_update_tv);
        lastCheckingTV = (TextView) findViewById(R.id.last_check_tv);
        locationET = (EditText) findViewById(R.id.location_et);
        locationET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                setTempLocation(editable.toString());
            }
        });
        locationsS = (Spinner) findViewById(R.id.location_s);
        observationET = (EditText) findViewById(R.id.observation_et);
        observationET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                setTempObservation(editable.toString());
            }
        });
        observationsS = (Spinner) findViewById(R.id.observation_s);
        followingS = (Spinner) findViewById(R.id.following_s);
        stateS = (Spinner) findViewById(R.id.state_s);
        typeS = (Spinner) findViewById(R.id.type_s);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (db.numberExist(getNumber())) {

                    if (!db.getNumberArea(getNumber()).equals(DB.IT.DefaultValues.MANUAL_INTRODUCED_NUMBER_AREA)) {
                        Tools.showToast(NewNumberActivity.this, "Ese número ya existe, usa otro!!!", false);
                        return;
                    }
                    db.updateLocation(getNumber(), getTempLocation());
                    db.updateObservation(getNumber(), getTempObservation());
                    db.updateDescription(getNumber(), getTempDescription());
                    db.updateFollowing(getNumber(), getTempFollowing());
                    db.updateTypeIfDescription(db.getNumberDescription(getNumber()), getTempType());
                    if (db.getNumberState(getNumber()) != getTempState()) {
                        db.updateState(getNumber(), getTempState());
                    }
                    Tools.showToast(NewNumberActivity.this, "Cambios guardados!", false);
                } else {
                    db.insertNewNumber(getNumber(),
                            getTempDescription(),
                            DB.IT.DefaultValues.MANUAL_INTRODUCED_NUMBER_AREA,
                            "",
                            "",
                            getTempFollowing(),
                            getTempState(),
                            Tools.getDate(),
                            getTempType(),
                            getTempLocation(),
                            getTempObservation());

                    Tools.showToast(NewNumberActivity.this, "Número insertado!!!", false);
                }
            }
        });

        //DB
        db = AppStatics.db;
        if (Arrays.equals(AppStatics.Description.descriptions, new String[]{""})) {
            AppStatics.Description.updateDescriptions(db);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


        if (getNumber().equals(PDefaultValues.EMPTY_PREFERENCE)) {
            setNumber(getNumber());
        } else {
            numberET.setText(getNumber());
        }

        areaTV.setText(DB.IT.DefaultValues.MANUAL_INTRODUCED_NUMBER_AREA);
        altaDateTV.setText(DB.IT.DefaultValues.EMPTY_VALUE);
        officialUpdateTV.setText(DB.IT.DefaultValues.EMPTY_VALUE);
        lastCheckingTV.setText(Tools.getFormattedDate());
        locationET.setText(getTempLocation());
        descriptionET.setText(getTempDescription());
        observationET.setText(getTempObservation());

        //region following spinner
        followingS.setAdapter(new ArrayAdapter<>(NewNumberActivity.this,
                android.R.layout.simple_list_item_1, new String[]{NO, YES}));
        followingS.setSelection(getTempFollowing());
        followingS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = (String) adapterView.getSelectedItem();
                if (selectedItem.equals(YES)) {
                    setTempFollowing(1);
                } else {
                    setTempFollowing(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //endregion following spinner

        //region state spinner

        stateS.setAdapter(new ArrayAdapter<>(NewNumberActivity.this,
                android.R.layout.simple_list_item_1,
                new String[]{DB.IT.StateValues.toString(DB.IT.StateValues.LEFTOVER)}));
        stateS.setEnabled(false);
        stateS.setClickable(false);
        findViewById(R.id.state_ll).setOnClickListener(new View.OnClickListener() {
            int c = -1;

            @Override
            public void onClick(View view) {

                if (c % 2 == 0) {
                    Tools.showToast(NewNumberActivity.this, getString(R.string.text15), false);
                }
                c++;
            }
        });


        //endregion

        //region type spinner

        ArrayList<String> typeAL = new ArrayList<>();
        typeAL.add(TypeValues.toString(TypeValues.UNKNOWN));
        typeAL.add(TypeValues.toString(TypeValues.FURNISHING));
        typeAL.add(TypeValues.toString(TypeValues.EQUIPMENT));
        typeS.setAdapter(new ArrayAdapter<>(NewNumberActivity.this,
                android.R.layout.simple_list_item_1, typeAL));
        typeS.setSelection(Tools.getIndexOf(typeAL, TypeValues.toString(getTempType())));
        typeS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setTempType(TypeValues.parse((String) adapterView.getSelectedItem()));

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //endregion

        //region location spinner
        final ArrayList<String> locationAL = new ArrayList<>();
        locationAL.add(LOCATIONS);
        locationAL.add(EMPTY);
        Collections.addAll(locationAL, AppStatics.Location.locations);
        locationAL.remove("");
        locationsS.setAdapter(new ArrayAdapter<>(NewNumberActivity.this,
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
        observationAL.add(OBSERVATIONS);
        observationAL.add(EMPTY);
        Collections.addAll(observationAL, AppStatics.Observation.observations);
        observationAL.remove("");
        observationsS.setAdapter(new ArrayAdapter<>(NewNumberActivity.this,
                android.R.layout.simple_list_item_1, observationAL));
        observationsS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = (String) adapterView.getSelectedItem();
                if (!selectedItem.equals(OBSERVATIONS)) {

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

        //endregion observation spinner

        //region description spinner
        final ArrayList<String> descriptionAL = new ArrayList<>();
        descriptionAL.add(DESCRIPTIONS);
        descriptionAL.add(EMPTY);
        Collections.addAll(descriptionAL, AppStatics.Description.descriptions);
        descriptionAL.remove("");
        descriptionS.setAdapter(new ArrayAdapter<>(NewNumberActivity.this,
                android.R.layout.simple_list_item_1, descriptionAL));
        descriptionS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = (String) adapterView.getSelectedItem();
                if (!selectedItem.equals(DESCRIPTIONS)) {

                    if (selectedItem.equals(EMPTY)) {
                        descriptionET.setText("");
                        setTempDescription("");
                    } else {
                        descriptionET.setText(selectedItem);
                        setTempDescription(selectedItem);
                    }

                    descriptionS.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //endregion

    }

    private String getNumber() {
        return db.getPreference(DB.PT.PNames.NUMBER_TO_EDIT);
    }

    private int getTempState() {
        return Integer.parseInt(db.getPreference(DB.PT.PNames.TEMP_STATE));
    }

    private int getTempType() {
        return Integer.parseInt(db.getPreference(DB.PT.PNames.TEMP_TYPE));
    }

    private String getTempLocation() {
        return db.getPreference(DB.PT.PNames.TEMP_LOCATION);
    }

    private int getTempFollowing() {
        return Integer.parseInt(db.getPreference(DB.PT.PNames.TEMP_FOLLOWING));
    }

    private String getTempObservation() {
        return db.getPreference(DB.PT.PNames.TEMP_OBSERVATION);
    }

    private String getTempDescription() {
        return db.getPreference(DB.PT.PNames.TEMP_DESCRIPTION);
    }


    private void setTempState(int state) {
        db.setPreference(DB.PT.PNames.TEMP_STATE, state);
    }

    private void setNumber(String number) {
        db.setPreference(DB.PT.PNames.NUMBER_TO_EDIT, number);
    }

    private void setTempType(int type) {
        db.setPreference(DB.PT.PNames.TEMP_TYPE, type);
    }

    private void setTempLocation(String location) {
        db.setPreference(DB.PT.PNames.TEMP_LOCATION, location);
    }

    private void setTempObservation(String observation) {
        db.setPreference(DB.PT.PNames.TEMP_OBSERVATION, observation);
    }

    private void setTempDescription(String description) {
        db.setPreference(DB.PT.PNames.TEMP_DESCRIPTION, description);
    }

    private void setTempFollowing(int following) {
        db.setPreference(DB.PT.PNames.TEMP_FOLLOWING, following);
    }


}
