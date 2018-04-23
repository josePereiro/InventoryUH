package com.example.compereirowww.inventory20181.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
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
    private static final String YES = "Sí";
    private static final String NO = "No";

    //DB
    private DB db;

    //STATIC VALUES
    private static final String LOCATIONS = "Localizaciones...";
    private static final String OBSERVATION = "Observaciones...";
    private static final String EMPTY = "Vacía...";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_activity);

        //DB
        db = AppStatics.db;

        //GUI
        numberTV = (TextView) findViewById(R.id.number_tv);
        descriptionTV = (TextView) findViewById(R.id.description_tv);
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
                if (db.getNumberState(getNumber()) != getTempState()) {
                    db.updateState(getNumber(), getTempState());
                }
                Tools.showToast(EditActivity.this, "Cambios guardados!", false);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

        //region PDefaultValues

        if (!getNumber().equals(getTempNumber())) {
            setTempNumber(getNumber());
            setTempType(db.getNumberType(getNumber()));
            setTempState(db.getNumberState(getNumber()));
            setTempLocation(db.getNumberLocation(getNumber()));
            setTempObservation(db.getNumberObservation(getNumber()));
            setTempFollowing(db.getNumberFollowingValue(getNumber()));
        }

        numberTV.setText(getNumber());
        descriptionTV.setText(db.getNumberDescription(getNumber()));
        areaTV.setText(db.getNumberArea(getNumber()));
        altaDateTV.setText(db.getNumberAltaDate(getNumber()));
        officialUpdateTV.setText(db.getNumberOfficialUpdate(getNumber()));
        lastCheckingTV.setText(Tools.formatDate(db.getNumberLastChecking(getNumber())));


        locationET.setText(getTempLocation());
        observationET.setText(getTempObservation());

        //region following spinner
        if (Tools.contain(AppStatics.AreasToFollow.areasToFollow, db.getNumberArea(getNumber()))) {
            followingS.setAdapter(new ArrayAdapter<>(EditActivity.this,
                    android.R.layout.simple_list_item_1, new String[]{YES}));
            followingS.setEnabled(false);
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
        }

        //endregion following spinner

        //region state spinner
        if (db.getNumberState(getNumber()) == DB.IT.StateValues.LEFTOVER) {
            stateS.setAdapter(new ArrayAdapter<>(EditActivity.this,
                    android.R.layout.simple_list_item_1,
                    new String[]{DB.IT.StateValues.toString(DB.IT.StateValues.LEFTOVER)}));
            stateS.setEnabled(false);
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

        } else if (db.getNumberState(getNumber()) == DB.IT.StateValues.LEFTOVER_PRESENT) {
            ArrayList<String> stateAL = new ArrayList<>();
            stateAL.add(DB.IT.StateValues.toString(DB.IT.StateValues.LEFTOVER_PRESENT));
            stateAL.add(DB.IT.StateValues.toString(DB.IT.StateValues.LEFTOVER));
            stateS.setAdapter(new ArrayAdapter<>(EditActivity.this,
                    android.R.layout.simple_list_item_1,
                    stateAL));
            stateS.setSelection(Tools.getIndexOf(stateAL, DB.IT.StateValues.toString(getTempState())));
            stateS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    setTempState(DB.IT.StateValues.parse((String) adapterView.getSelectedItem()));
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

        } else if (getTempState() == DB.IT.StateValues.MISSING ||
                getTempState() == DB.IT.StateValues.IGNORED_MISSING) {
            ArrayList<String> stateAL = new ArrayList<>();
            stateAL.add(DB.IT.StateValues.toString(DB.IT.StateValues.MISSING));
            stateAL.add(DB.IT.StateValues.toString(DB.IT.StateValues.IGNORED_MISSING));
            stateS.setAdapter(new ArrayAdapter<>(EditActivity.this,
                    android.R.layout.simple_list_item_1, stateAL));
            stateS.setSelection(Tools.getIndexOf(stateAL, DB.IT.StateValues.toString(getTempState())));
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
            stateS.setSelection(Tools.getIndexOf(stateAL, DB.IT.StateValues.toString(getTempState())));
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
        typeS.setSelection(Tools.getIndexOf(typeAL, DB.IT.TypeValues.toString(getTempType())));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.qr_viewer) {
            callQRViewerActivity();
            return super.onOptionsItemSelected(item);
        }
        if (id == R.id.help) {
            Tools.showToast(EditActivity.this, "No hay ayuda!!! Por ahora...", false);
            return super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }

    private void callQRViewerActivity() {
        QRViewerActivity.setText(getNumber());
        startActivity(new Intent(EditActivity.this, QRViewerActivity.class));
    }

    private String getNumber() {
        return db.getPreference(DB.PT.PNames.NUMBER_TO_EDIT);
    }

    private String getTempNumber() {
        return db.getPreference(DB.PT.PNames.TEMP_NUMBER);
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

    private void setTempNumber(String number) {
        db.setPreference(DB.PT.PNames.TEMP_NUMBER, number);
    }

    private void setTempState(int state) {
        db.setPreference(DB.PT.PNames.TEMP_STATE, state);
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

    private void setTempFollowing(int following) {
        db.setPreference(DB.PT.PNames.TEMP_FOLLOWING, following);
    }


}
