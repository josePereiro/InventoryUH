package com.example.compereirowww.inventory20181.Activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AbsListView;
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
    TextView areasTV;
    ListView areasLV;
    Spinner criteriaS;

    //DB
    private DB db;

    //data
    ArrayList<String> areasToFollowAL;
    ArrayList<String> allAreasAL;

    private static final String MARKER = "(*) ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.setPreference(DB.RT.AREAS_TO_FOLLOW_CSV,
                        AppStatics.AreasToFollow.getAreasAsCSV(areasToFollowAL.toArray(new String[]{""})));
                AppStatics.AreasToFollow.updateAreasToFollow(db);
                db.updateFollowingColumn(DB.IT.FollowingType.NO);
                db.updateFollowingByAreas(AppStatics.AreasToFollow.areasToFollow, DB.IT.FollowingType.YES);
                db.setPreference(DB.RT.UPDATE_CRITERIA, (String) criteriaS.getSelectedItem());
                Tools.showToast(ConfigurationActivity.this, getString(R.string.text20), false);

            }
        });

        //db
        db = AppStatics.db;

        //data
        areasToFollowAL = new ArrayList<>();
        if (db.getPreference(DB.RT.TEMP_AREAS_TO_FOLLOW_CSV).equals(DB.RT.EMPTY_PREFERENCE)) {
            if (!Arrays.equals(AppStatics.AreasToFollow.areasToFollow, new String[]{""})) {
                Collections.addAll(areasToFollowAL, AppStatics.AreasToFollow.areasToFollow);
            }
        } else {
            Collections.addAll(areasToFollowAL,
                    AppStatics.AreasToFollow.splitAreasCSV(db.getPreference(DB.RT.TEMP_AREAS_TO_FOLLOW_CSV)));
        }

        //GUI
        areasLV = (ListView) findViewById(R.id.areas_lv);
        areasTV = (TextView) findViewById(R.id.areas_tv);
        if (areasToFollowAL.size() == 0) {
            areasTV.setText(R.string.text19);
        } else {
            areasTV.setText(AppStatics.AreasToFollow.getAreasAsCSV(areasToFollowAL.toArray(new String[]{""})));
        }

        allAreasAL = new ArrayList<>();
        Collections.addAll(allAreasAL, AppStatics.Area.areas);
        for (int i = 0; i < allAreasAL.size(); i++) {
            if (areasToFollowAL.contains(allAreasAL.get(i))) {
                allAreasAL.set(i, MARKER + allAreasAL.get(i));
            }
        }
        areasLV.setAdapter(new ArrayAdapter<>(ConfigurationActivity.this,
                android.R.layout.simple_list_item_1, allAreasAL));
        areasLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String clickedItem = (String) adapterView.getItemAtPosition(i);
                clickedItem = clickedItem.replace(MARKER, "");

                if (areasToFollowAL.contains(clickedItem)) {

                    //Remove
                    areasToFollowAL.remove(clickedItem);
                    allAreasAL.set(i, clickedItem);

                } else {

                    //Add
                    areasToFollowAL.add(clickedItem);
                    allAreasAL.set(i, MARKER + allAreasAL.get(i));
                }

                db.setPreference(DB.RT.TEMP_AREAS_TO_FOLLOW_CSV,
                        AppStatics.AreasToFollow.getAreasAsCSV(areasToFollowAL.
                                toArray(new String[]{""})));

                int firstVisiblePosition = adapterView.getFirstVisiblePosition();
                areasLV.setAdapter(new ArrayAdapter<>(ConfigurationActivity.this,
                        android.R.layout.simple_list_item_1, allAreasAL));
                areasLV.setSelection(firstVisiblePosition);
                if (areasToFollowAL.size() == 0) {
                    areasTV.setText(R.string.text19);
                } else {
                    areasTV.setText(AppStatics.AreasToFollow.getAreasAsCSV(areasToFollowAL.toArray(new String[]{""})));
                }

            }
        });

        criteriaS = (Spinner) findViewById(R.id.criteria_s);
        String[] criteriaValues = new String[]{"0", "1", "7", "30", "45", "60", "120", "365", "730", "1095"};
        criteriaS.setAdapter(new ArrayAdapter<>(ConfigurationActivity.this,
                android.R.layout.simple_list_item_1,
                criteriaValues));
        if (db.getPreference(DB.RT.TEMP_UPDATE_CRITERIA).equals(DB.RT.EMPTY_PREFERENCE)) {
            criteriaS.setSelection(Tools.getIndexOf(criteriaValues, db.getPreference(DB.RT.UPDATE_CRITERIA)));
        } else {
            criteriaS.setSelection(Tools.getIndexOf(criteriaValues, db.getPreference(DB.RT.TEMP_UPDATE_CRITERIA)));
        }
        criteriaS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                db.setPreference(DB.RT.TEMP_UPDATE_CRITERIA, (String) adapterView.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }


}
