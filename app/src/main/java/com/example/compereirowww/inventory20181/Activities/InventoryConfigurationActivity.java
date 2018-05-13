package com.example.compereirowww.inventory20181.Activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.compereirowww.inventory20181.DataBase.DB;
import com.example.compereirowww.inventory20181.R;
import com.example.compereirowww.inventory20181.Tools.Tools;

import java.util.Arrays;

public class InventoryConfigurationActivity extends AppCompatActivity {

    //GUI
    TextView previewTV;
    //descriptionCB, areaCB, stateCB, lastCheckCB, typeCB, locationCB, observationCB;
    CheckBox[] checkBoxes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_configuration);


        //GUI
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPreferenceItemsToDisplay();
                Tools.showToast(InventoryConfigurationActivity.this,
                        "Cambios Guardados!", false);
            }
        });
        previewTV = (TextView) findViewById(R.id.preview_tv);
        AppStatics.formatView(previewTV);
        AppStatics.formatView((TextView) findViewById(R.id.textView));
        AppStatics.formatView((TextView) findViewById(R.id.textView3));
        checkBoxes = new CheckBox[7];
        checkBoxes[0] = (CheckBox) findViewById(R.id.description_cb);
        checkBoxes[1] = (CheckBox) findViewById(R.id.area_cb);
        checkBoxes[2] = (CheckBox) findViewById(R.id.state_cb);
        checkBoxes[3] = (CheckBox) findViewById(R.id.last_check_cb);
        checkBoxes[4] = (CheckBox) findViewById(R.id.type_cb);
        checkBoxes[5] = (CheckBox) findViewById(R.id.location_cb);
        checkBoxes[6] = (CheckBox) findViewById(R.id.observation_cb);
        for (CheckBox cb : checkBoxes) {
            AppStatics.formatView(cb);
            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    updatePreview();
                }
            });
        }

        setUpCheckBoxes();
        updatePreview();
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

    private void setUpCheckBoxes() {
        Boolean[] itemsToDisplay = getPreferenceItemsToDisplay();
        for (int i = 0; i < itemsToDisplay.length; i++) {
            checkBoxes[i].setChecked(itemsToDisplay[i]);
        }
    }

    private void setPreferenceItemsToDisplay() {
        String csv = "";
        for (int i = 0; i < checkBoxes.length; i++) {
            if (checkBoxes[i].isChecked()) {
                if (i == 0) {
                    csv = DB.PT.PDefaultValues.YES;
                } else {
                    csv += ",";
                    csv += DB.PT.PDefaultValues.YES;
                }
            } else {
                if (i == 0) {
                    csv = DB.PT.PDefaultValues.NO;
                } else {
                    csv += ",";
                    csv += DB.PT.PDefaultValues.NO;
                }
            }
        }

        AppStatics.db.setPreference(DB.PT.PNames.FIELDS_TO_DISPLAY_CSV, csv);
    }

    private void updatePreview() {

        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("* Número: 123456789");
        sb.append("\n");
        sb.append("\n");
        if (checkBoxes[0].isChecked()) {
            sb.append("- Descripción: Bla Bla Bla Bla Bla...");
            sb.append("\n");
            sb.append("\n");
        }
        if (checkBoxes[1].isChecked()) {
            sb.append("- Área: 51...");
            sb.append("\n");
            sb.append("\n");
        }
        if (checkBoxes[2].isChecked()) {
            sb.append("- Estado: Presente");
            sb.append("\n");
            sb.append("\n");
        }
        if (checkBoxes[3].isChecked()) {
            sb.append("- Última act. del estado: 1959");
            sb.append("\n");
            sb.append("\n");
        }
        if (checkBoxes[4].isChecked()) {
            sb.append("- Tipo: EQUIPO");
            sb.append("\n");
            sb.append("\n");
        }
        if (checkBoxes[5].isChecked()) {
            sb.append("- Localización: Aquí...");
            sb.append("\n");
            sb.append("\n");
        }
        if (checkBoxes[6].isChecked()) {
            sb.append("- Observación: Bla Bla Bla Bla...");
            sb.append("\n");
        }

        previewTV.setText(sb.toString());

    }

}
