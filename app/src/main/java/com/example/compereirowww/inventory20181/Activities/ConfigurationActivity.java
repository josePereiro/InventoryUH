package com.example.compereirowww.inventory20181.Activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.compereirowww.inventory20181.DataBase.DB;
import com.example.compereirowww.inventory20181.R;
import com.example.compereirowww.inventory20181.Tools.Tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class ConfigurationActivity extends AppCompatActivity {

    //GUI
    ListView areasLV;
    ListView selectedAreasLV;
    Spinner criteriaS;
    TextView selectedAreasTV;

    //DB
    private DB db;

    //data
    ArrayList<String> areasToFollowAL;
    ArrayList<String> allAreasAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.setPreference(DB.PT.PNames.AREAS_TO_FOLLOW_CSV,
                        AppStatics.AreasToFollow.getAreasAsCSV(areasToFollowAL));
                AppStatics.AreasToFollow.updateAreasToFollow(db);
                db.updateFollowingColumn(DB.IT.FollowingValues.NO);
                db.updateFollowingByAreas(AppStatics.AreasToFollow.areasToFollow, DB.IT.FollowingValues.YES);
                db.setPreference(DB.PT.PNames.UPDATE_CRITERIA, (String) criteriaS.getSelectedItem());
                Tools.showToast(ConfigurationActivity.this, getString(R.string.text20), false);

            }
        });

        //db
        db = AppStatics.db;

        //data
        areasToFollowAL = new ArrayList<>();
        if (db.getPreference(DB.PT.PNames.TEMP_AREAS_TO_FOLLOW_CSV).equals(DB.PT.Values.EMPTY_PREFERENCE)) {
            if (!Arrays.equals(AppStatics.AreasToFollow.areasToFollow, new String[]{""})) {
                Collections.addAll(areasToFollowAL, AppStatics.AreasToFollow.areasToFollow);
            }
        } else {
            Collections.addAll(areasToFollowAL,
                    AppStatics.AreasToFollow.splitAreasCSV(db.getPreference(DB.PT.PNames.TEMP_AREAS_TO_FOLLOW_CSV)));
        }

        //GUI
        selectedAreasTV = (TextView) findViewById(R.id.selected_areas_tv);
        areasLV = (ListView) findViewById(R.id.areas_lv);
        allAreasAL = new ArrayList<>();
        Collections.addAll(allAreasAL, AppStatics.Area.areas);

        areasLV.setAdapter(new ArrayAdapter<>(ConfigurationActivity.this,
                android.R.layout.simple_list_item_1, allAreasAL));
        areasLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String clickedItem = (String) adapterView.getItemAtPosition(i);

                if (areasToFollowAL.contains(clickedItem)) {

                    Tools.showToast(ConfigurationActivity.this, "Esa área ya fue seleccionada!", false);

                } else {

                    //Add
                    areasToFollowAL.add(clickedItem);
                    selectedAreasLV.setAdapter(new ArrayAdapter<>(ConfigurationActivity.this,
                            android.R.layout.simple_list_item_1, areasToFollowAL));
                    db.setPreference(DB.PT.PNames.TEMP_AREAS_TO_FOLLOW_CSV,
                            AppStatics.AreasToFollow.getAreasAsCSV(areasToFollowAL));
                }

                selectedAreasTV.setText(getString(R.string.text22) + areasToFollowAL.size());
                selectedAreasLV.setSelection(Tools.getIndexOf(areasToFollowAL, clickedItem));

            }
        });

        selectedAreasLV = (ListView) findViewById(R.id.selected_areas_lv);
        selectedAreasLV.setAdapter(new ArrayAdapter<>(ConfigurationActivity.this,
                android.R.layout.simple_list_item_1, areasToFollowAL));
        selectedAreasLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //Remove
                areasToFollowAL.remove(i);
                int firstVisibleView = adapterView.getFirstVisiblePosition();
                selectedAreasLV.setAdapter(new ArrayAdapter<>(ConfigurationActivity.this,
                        android.R.layout.simple_list_item_1, areasToFollowAL));
                selectedAreasLV.setSelection(firstVisibleView);
                selectedAreasTV.setText(getString(R.string.text22) + areasToFollowAL.size());

            }
        });


        criteriaS = (Spinner) findViewById(R.id.criteria_s);
        String[] criteriaValues = new String[]{"0", "1", "7", "30", "45", "60", "120", "365", "730", "1095"};
        criteriaS.setAdapter(new ArrayAdapter<>(ConfigurationActivity.this,
                android.R.layout.simple_list_item_1,
                criteriaValues));
        if (db.getPreference(DB.PT.PNames.TEMP_UPDATE_CRITERIA).equals(DB.PT.Values.EMPTY_PREFERENCE)) {
            criteriaS.setSelection(Tools.getIndexOf(criteriaValues, db.getPreference(DB.PT.PNames.UPDATE_CRITERIA)));
        } else {
            criteriaS.setSelection(Tools.getIndexOf(criteriaValues, db.getPreference(DB.PT.PNames.TEMP_UPDATE_CRITERIA)));
        }
        criteriaS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                db.setPreference(DB.PT.PNames.TEMP_UPDATE_CRITERIA, (String) adapterView.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        selectedAreasTV.setText(getString(R.string.text22) + areasToFollowAL.size());
    }


}
