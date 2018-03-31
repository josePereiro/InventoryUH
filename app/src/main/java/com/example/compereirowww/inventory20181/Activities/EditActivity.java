package com.example.compereirowww.inventory20181.Activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.compereirowww.inventory20181.DataBase.DB;
import com.example.compereirowww.inventory20181.R;

public class EditActivity extends AppCompatActivity {

    //GUI
    private TextView numberTV, descriptionTV, areaTV, altaDateTV, officialUpdateTV, lastCheckingTV;
    private EditText locationET, observationET;
    private Spinner followingS, stateS, typeS, locationsS, observationsS;

    //DB
    private DB db;

    //STATIC VALUES
    private static final String HAND_WROTE = "...";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //GUI
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        numberTV = (TextView)findViewById(R.id.number_tv);
        descriptionTV = (TextView)findViewById(R.id.description_tv);
        areaTV = (TextView)findViewById(R.id.area_tv);
        altaDateTV = (TextView)findViewById(R.id.alta_date_tv);
        officialUpdateTV = (TextView)findViewById(R.id.official_update_tv);
        lastCheckingTV = (TextView)findViewById(R.id.last_check_tv);
        locationET = (EditText)findViewById(R.id.location_et);
        locationsS = (Spinner)findViewById(R.id.location_s);
        observationET = (EditText)findViewById(R.id.observation_et);
        observationsS = (Spinner)findViewById(R.id.observation_s);
        followingS = (Spinner)findViewById(R.id.following_s);
        stateS = (Spinner)findViewById(R.id.state_s);
        typeS = (Spinner)findViewById(R.id.type_s);

        //DB
        db = AppStatics.db;

    }

    @Override
    protected void onResume() {
        super.onResume();

        //region Values


        //endregion

    }
}
