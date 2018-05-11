package com.example.compereirowww.inventory20181.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.compereirowww.inventory20181.DataBase.DB;
import com.example.compereirowww.inventory20181.R;
import com.example.compereirowww.inventory20181.Tools.Tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static com.example.compereirowww.inventory20181.Activities.AppStatics.db;

public class MainConfigurationActivity extends AppCompatActivity {

    //GUI
    ListView areasLV;
    ListView selectedAreasLV;
    Spinner criteriaS, letterSizeS;
    TextView selectedAreasTV;


    //CSVData
    ArrayList<String> areasToFollowAL;
    ArrayList<String> allAreasAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_configuration);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.setPreference(DB.PT.PNames.AREAS_TO_FOLLOW_CSV,
                        AppStatics.AreasToFollow.getAreasAsCSV(areasToFollowAL));
                AppStatics.AreasToFollow.updateAreasToFollow();
                db.updateFollowingColumn(DB.IT.FollowingValues.NO);
                db.updateFollowingByAreas(AppStatics.AreasToFollow.areasToFollow, DB.IT.FollowingValues.YES);
                db.setPreference(DB.PT.PNames.UPDATE_CRITERIA, (String) criteriaS.getSelectedItem());
                db.setPreference(DB.PT.PNames.TEXT_SIZE, (String) letterSizeS.getSelectedItem());
                Tools.showToast(MainConfigurationActivity.this, getString(R.string.text20), false);

                startActivity(new Intent(MainConfigurationActivity.this, MainConfigurationActivity.class));
                finish();
            }
        });


        //CSVData
        areasToFollowAL = new ArrayList<>();
        if (db.getPreference(DB.PT.PNames.TEMP_AREAS_TO_FOLLOW_CSV).equals(DB.PT.PDefaultValues.EMPTY_PREFERENCE)) {
            if (!Arrays.equals(AppStatics.AreasToFollow.areasToFollow, new String[]{""})) {
                Collections.addAll(areasToFollowAL, AppStatics.AreasToFollow.areasToFollow);
            }
        } else {
            Collections.addAll(areasToFollowAL,
                    AppStatics.AreasToFollow.splitAreasCSV(db.getPreference(DB.PT.PNames.TEMP_AREAS_TO_FOLLOW_CSV)));
        }

        //GUI
        AppStatics.formatView((TextView) findViewById(R.id.textView2));
        AppStatics.formatView((TextView) findViewById(R.id.textView3));
        AppStatics.formatView((TextView) findViewById(R.id.textView8));
        AppStatics.formatView((TextView) findViewById(R.id.textView9));
        selectedAreasTV = (TextView) findViewById(R.id.selected_areas_tv);
        AppStatics.formatView(selectedAreasTV);
        areasLV = (ListView) findViewById(R.id.areas_lv);
        allAreasAL = new ArrayList<>();
        Collections.addAll(allAreasAL, AppStatics.Area.areas);

        AppStatics.formatView(MainConfigurationActivity.this, allAreasAL, areasLV);
        areasLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String clickedItem = (String) adapterView.getItemAtPosition(i);

                if (areasToFollowAL.contains(clickedItem)) {

                    Tools.showToast(MainConfigurationActivity.this, "Esa área ya fue seleccionada!", false);

                } else {

                    //Add
                    areasToFollowAL.add(clickedItem);
                    AppStatics.formatView(MainConfigurationActivity.this, areasToFollowAL, selectedAreasLV);
                    db.setPreference(DB.PT.PNames.TEMP_AREAS_TO_FOLLOW_CSV,
                            AppStatics.AreasToFollow.getAreasAsCSV(areasToFollowAL));
                }

                selectedAreasTV.setText(getString(R.string.text22) + areasToFollowAL.size());
                selectedAreasLV.setSelection(Tools.getIndexOf(areasToFollowAL, clickedItem));

            }
        });

        selectedAreasLV = (ListView) findViewById(R.id.selected_areas_lv);
        AppStatics.formatView(MainConfigurationActivity.this, areasToFollowAL, selectedAreasLV);
        selectedAreasLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //Remove
                areasToFollowAL.remove(i);
                int firstVisibleView = adapterView.getFirstVisiblePosition();
                AppStatics.formatView(MainConfigurationActivity.this, areasToFollowAL, selectedAreasLV);
                selectedAreasLV.setSelection(firstVisibleView);
                selectedAreasTV.setText(getString(R.string.text22) + areasToFollowAL.size());

            }
        });


        criteriaS = (Spinner) findViewById(R.id.criteria_s);
        String[] criteriaValues = new String[]{"0", "1", "7", "30", "45", "60", "120", "365", "730", "1095"};
        AppStatics.formatView(MainConfigurationActivity.this, criteriaValues, criteriaS);
        if (db.getPreference(DB.PT.PNames.TEMP_UPDATE_CRITERIA).equals(DB.PT.PDefaultValues.EMPTY_PREFERENCE)) {
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

        letterSizeS = (Spinner) findViewById(R.id.letter_size_s);
        String[] letterSizes = new String[]{DB.PT.PDefaultValues.SMALL_LETTER
                , DB.PT.PDefaultValues.MEDIUM_LETTER, DB.PT.PDefaultValues.BIG_LETTER};
        AppStatics.formatView(MainConfigurationActivity.this, letterSizes, letterSizeS);
        if (db.getPreference(DB.PT.PNames.TEMP_TEXT_SIZE).equals(DB.PT.PDefaultValues.EMPTY_PREFERENCE)) {
            letterSizeS.setSelection(Tools.getIndexOf(letterSizes, db.getPreference(DB.PT.PNames.TEXT_SIZE)));
        } else {
            letterSizeS.setSelection(Tools.getIndexOf(letterSizes, db.getPreference(DB.PT.PNames.TEMP_TEXT_SIZE)));
        }
        letterSizeS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = (String) adapterView.getItemAtPosition(i);
                if (!selectedItem.equals(db.getPreference(DB.PT.PNames.TEMP_TEXT_SIZE))) {
                    db.setPreference(DB.PT.PNames.TEMP_TEXT_SIZE, selectedItem);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        selectedAreasTV.setText(getString(R.string.text22) + areasToFollowAL.size());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_help, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.help) {
            Tools.showToast(MainConfigurationActivity.this, "No hay ayuda!!! Por ahora...", false);
            return super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(MainConfigurationActivity.this, MainActivity.class));
        finish();
    }
}
