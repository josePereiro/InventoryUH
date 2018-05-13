package com.example.compereirowww.inventory20181.Activities;

import android.Manifest;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.compereirowww.inventory20181.DataBase.DB;
import com.example.compereirowww.inventory20181.R;

import java.util.ArrayList;
import java.util.Arrays;

public class AppStatics {

    public static class UHInventoryFile {

        /**
         * This is the index of the first value in a file to import,
         * this depend of the format of the file
         */
        public static final int CSV_FIRST_DATA_LINE_INDEX = 1;

        /**
         * the index of the number in a line of the file to import
         */
        public static final int NUMBER_INDEX = 0;

        /**
         * the index of the description CSVData in a line of the file to import
         */
        public static final int DESCRIPTION_INDEX = 1;

        /**
         * the index of the head data in the first line of the file to import
         */
        public static final int HEAD_INDEX = 0;

        /**
         * the index of the hasc code data in the first line of the file to import
         */
        public static final int HASH_INDEX = 1;

        /**
         * the index of the area CSVData in a line of the file to import
         */
        public static final int AREA_INDEX = 2;

        /**
         * the index of the altaDate CSVData in a line of the file to import
         */
        public static final int ALTA_DATE_INDEX = 3;

        /**
         * the index of the officialUpdate CSVData in a line of the file to import
         */
        public static final int OFFICIAL_UPDATE_INDEX = 4;

        /**
         * The head that any uh inventory file most have to be imported
         */
        public static final String UH_INVENTORY_FILE_HEAD_CODE = "Archivo UH para importar...";

    }

    public static class InventoryBackUpFile {

        /**
         * The head that any salva file most have to be imported
         */
        public static final String INVENTORY_BACKUP_FILE_HEAD_CODE = "Archivo de respaldo...";

        /**
         * the index of the number CSVData in a line of the file to import
         */
        public static final int NUMBER_INDEX = 0;

        /**
         * the index of the location CSVData in a line of the file to import
         */
        public static final int LOCATION_INDEX = 1;

        /**
         * the index of the type CSVData in a line of the file to import
         */
        public static final int TYPE_INDEX = 2;

        /**
         * the index of the observation CSVData in a line of the file to import
         */
        public static final int OBSERVATION_INDEX = 3;

        /**
         * the index of the head data in the first line of the file to import
         */
        public static final int HEAD_INDEX = 0;

        /**
         * the index of the hasc code data in the first line of the file to import
         */
        public static final int HASH_INDEX = 1;


        /**
         * the index of the filter1 data in the first line of the file to import
         */
        public static final int FILTER1_INDEX = 2;

        /**
         * the index of the filter2 data in the first line of the file to import
         */
        public static final int FILTER2_INDEX = 3;

        /**
         * the index of the creation date data in the first line of the file to import
         */
        public static final int CREATION_DATE_INDEX = 4;


    }

    public static class Location {

        /**
         * Track statically the current locations
         */
        public static String[] locations = new String[]{""};

        /**
         * update the locations from the db
         */
        public static void updateLocations() {

            //get areas
            Cursor areaC = db.getLocationColumnData();
            ArrayList<String> locationAL = new ArrayList<>();
            String currentLocation;
            while (areaC.moveToNext()) {
                currentLocation = areaC.getString(0);
                if (!locationAL.contains(currentLocation)) {
                    locationAL.add(currentLocation);
                }
            }
            locations = locationAL.toArray(new String[]{""});
            Arrays.sort(locations, String.CASE_INSENSITIVE_ORDER);

        }

    }

    public static class Area {

        /**
         * Track statically the current area
         */
        public static String[] areas = new String[]{""};

        public static void updateAreas() {

            //get areas
            Cursor areaC = db.getAreaColumnData();
            ArrayList<String> areaAL = new ArrayList<>();
            String currentArea;
            while (areaC.moveToNext()) {
                currentArea = areaC.getString(0);
                if (!areaAL.contains(currentArea)) {
                    areaAL.add(currentArea);
                }
            }
            areas = areaAL.toArray(new String[]{""});
            Arrays.sort(areas, String.CASE_INSENSITIVE_ORDER);
        }

    }

    public static class Observation {

        /**
         * Track statically the current observations
         */
        public static String[] observations = new String[]{""};

        public static void updateObservations() {

            //get areas
            Cursor observationC = db.getObservationColumnData();
            ArrayList<String> observationAL = new ArrayList<>();
            String currentObservation;
            while (observationC.moveToNext()) {
                currentObservation = observationC.getString(0);
                if (!observationAL.contains(currentObservation)) {
                    observationAL.add(currentObservation);
                }
            }
            observations = observationAL.toArray(new String[]{""});
            Arrays.sort(observations, String.CASE_INSENSITIVE_ORDER);

        }

    }

    public static class Description {

        /**
         * Track statically the current observations
         */
        public static String[] descriptions = new String[]{""};

        public static void updateDescriptions(DB db) {

            //get areas
            Cursor allDescriptions = db.getDescriptionColumnData();
            ArrayList<String> descriptionsAL = new ArrayList<>();
            String current;
            while (allDescriptions.moveToNext()) {
                current = allDescriptions.getString(0);
                if (!descriptionsAL.contains(current)) {
                    descriptionsAL.add(current);
                }
            }
            descriptions = descriptionsAL.toArray(new String[]{""});
            Arrays.sort(descriptions, String.CASE_INSENSITIVE_ORDER);

        }

    }

    public static class AreasToFollow {

        public static String[] areasToFollow = new String[]{""};

        public static void updateAreasToFollow() {

            if (!db.getPreference(DB.PT.PNames.AREAS_TO_FOLLOW_CSV).equals(DB.PT.PDefaultValues.PREFERENCE_NOT_FOUND)
                    && !db.getPreference(DB.PT.PNames.AREAS_TO_FOLLOW_CSV).equals(DB.PT.PDefaultValues.EMPTY_PREFERENCE)) {

                areasToFollow = splitAreasCSV(db.getPreference(DB.PT.PNames.AREAS_TO_FOLLOW_CSV));

            } else {
                areasToFollow = new String[]{""};
            }

        }

        public static String[] splitAreasCSV(String areas) {
            if (!areas.equals("")) return areas.split(",", -1);
            else return new String[]{""};
        }

        public static String getAreasAsCSV(ArrayList<String> areas) {
            String areasAsCSV = "";
            for (int i = 0; i < areas.size(); i++) {
                if (areas.get(i) == null) continue;
                if (i < areas.size() - 1) areasAsCSV += areas.get(i) + ",";
                else areasAsCSV += areas.get(i);
            }

            return areasAsCSV;
        }

    }

    public static class Report {

        public static int NUMBER_INDEX = 0;
        public static int DESCRIPTION_INDEX = 1;
        public static int ÁREA_INDEX = 2;
        public static int ALTA_DATE_INDEX = 3;
        public static int UPDATE_DATE_INDEX = 4;
        public static int STATE_INDEX = 5;
        public static int LAST_CHECKING_INDEX = 6;
        public static int TYPE_INDEX = 7;
        public static int LOCATION_INDEX = 8;
        public static int OBSERVATION_INDEX = 9;

    }

    /**
     * The tag used for the application in the log
     */
    public static final String APP_TAG = "JOSE";

    /**
     * The extension of the file
     */
    public static final String IMPORT_FILE_EXTENSION = ".csv";

    public static final int QR_DECODER_REQUEST = 626;

    /**
     * The name of the app folder
     */
    public static final String APP_FILE_NAME = "Inventario UH";

    /**
     * The name of the app folder where will be placed the files to import
     */
    public static final String APP_REPORT_FILE_NAME = "Reportes";

    /**
     * The name of the app folder where will be placed the save files
     */
    public static final String APP_BACKUP_FILE_NAME = "Respaldos";

    /**
     * The name of the qr folder where will be placed all the qr codes
     */
    public static final String APP_QRS_FILE_NAME = "Códigos QRs";

    private static final int SMALL_LETTER_VALUE = 15;
    private static final int MEDIUM_LETTER_VALUE = 20;
    private static final int BIG_LETTER_VALUE = 30;

    /**
     * This is a tool, that allow not to load the DB every time you start an Activity
     * call!!!
     */
    public static DB db;

    //Storage Permissions
    public static final int REQUEST_EXTERNAL_STORAGE = 1;
    public static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void formatView(TextView textView) {

        //SIZE
        if (db.getPreference(DB.PT.PNames.TEXT_SIZE).equals(DB.PT.PDefaultValues.SMALL_LETTER)) {
            textView.setTextSize(SMALL_LETTER_VALUE);
        } else if (db.getPreference(DB.PT.PNames.TEXT_SIZE).equals(DB.PT.PDefaultValues.MEDIUM_LETTER)) {
            textView.setTextSize(MEDIUM_LETTER_VALUE);
        } else if (db.getPreference(DB.PT.PNames.TEXT_SIZE).equals(DB.PT.PDefaultValues.BIG_LETTER)) {
            textView.setTextSize(BIG_LETTER_VALUE);
        }

        //COLOR
        textView.setTextColor(Color.BLACK);
    }

    public static void formatView(EditText editText) {
        if (db.getPreference(DB.PT.PNames.TEXT_SIZE).equals(DB.PT.PDefaultValues.SMALL_LETTER)) {
            editText.setTextSize(SMALL_LETTER_VALUE);
        } else if (db.getPreference(DB.PT.PNames.TEXT_SIZE).equals(DB.PT.PDefaultValues.MEDIUM_LETTER)) {
            editText.setTextSize(MEDIUM_LETTER_VALUE);
        } else if (db.getPreference(DB.PT.PNames.TEXT_SIZE).equals(DB.PT.PDefaultValues.BIG_LETTER)) {
            editText.setTextSize(BIG_LETTER_VALUE);
        }
    }

    public static void formatView(CheckBox checkBox) {
        if (db.getPreference(DB.PT.PNames.TEXT_SIZE).equals(DB.PT.PDefaultValues.SMALL_LETTER)) {
            checkBox.setTextSize(SMALL_LETTER_VALUE);
        } else if (db.getPreference(DB.PT.PNames.TEXT_SIZE).equals(DB.PT.PDefaultValues.MEDIUM_LETTER)) {
            checkBox.setTextSize(MEDIUM_LETTER_VALUE);
        } else if (db.getPreference(DB.PT.PNames.TEXT_SIZE).equals(DB.PT.PDefaultValues.BIG_LETTER)) {
            checkBox.setTextSize(BIG_LETTER_VALUE);
        }
    }

    public static void formatView(Context context, ArrayList<String> items, ListView listView) {

        if (db.getPreference(DB.PT.PNames.TEXT_SIZE).equals(DB.PT.PDefaultValues.SMALL_LETTER)) {
            listView.setAdapter(new ArrayAdapter<>(context, R.layout.my_simple_item_list_text_size_small,
                    items));
        } else if (db.getPreference(DB.PT.PNames.TEXT_SIZE).equals(DB.PT.PDefaultValues.MEDIUM_LETTER)) {
            listView.setAdapter(new ArrayAdapter<>(context, R.layout.my_simple_item_list_text_size_medium,
                    items));
        } else if (db.getPreference(DB.PT.PNames.TEXT_SIZE).equals(DB.PT.PDefaultValues.BIG_LETTER)) {
            listView.setAdapter(new ArrayAdapter<>(context, R.layout.my_simple_item_list_text_size_big,
                    items));
        }


    }

    public static void formatView(Context context, String[] items, ListView listView) {

        if (db.getPreference(DB.PT.PNames.TEXT_SIZE).equals(DB.PT.PDefaultValues.SMALL_LETTER)) {
            listView.setAdapter(new ArrayAdapter<>(context, R.layout.my_simple_item_list_text_size_small,
                    items));
        } else if (db.getPreference(DB.PT.PNames.TEXT_SIZE).equals(DB.PT.PDefaultValues.MEDIUM_LETTER)) {
            listView.setAdapter(new ArrayAdapter<>(context, R.layout.my_simple_item_list_text_size_medium,
                    items));
        } else if (db.getPreference(DB.PT.PNames.TEXT_SIZE).equals(DB.PT.PDefaultValues.BIG_LETTER)) {
            listView.setAdapter(new ArrayAdapter<>(context, R.layout.my_simple_item_list_text_size_big,
                    items));
        }


    }

    public static void formatView(Context context, ArrayList<String> items, Spinner spinner) {

        if (db.getPreference(DB.PT.PNames.TEXT_SIZE).equals(DB.PT.PDefaultValues.SMALL_LETTER)) {
            spinner.setAdapter(new ArrayAdapter<>(context, R.layout.my_simple_item_list_text_size_small,
                    items));
        } else if (db.getPreference(DB.PT.PNames.TEXT_SIZE).equals(DB.PT.PDefaultValues.MEDIUM_LETTER)) {
            spinner.setAdapter(new ArrayAdapter<>(context, R.layout.my_simple_item_list_text_size_medium,
                    items));
        } else if (db.getPreference(DB.PT.PNames.TEXT_SIZE).equals(DB.PT.PDefaultValues.BIG_LETTER)) {
            spinner.setAdapter(new ArrayAdapter<>(context, R.layout.my_simple_item_list_text_size_big,
                    items));
        }


    }

    public static void formatView(Context context, String[] items, Spinner spinner) {

        if (db.getPreference(DB.PT.PNames.TEXT_SIZE).equals(DB.PT.PDefaultValues.SMALL_LETTER)) {
            spinner.setAdapter(new ArrayAdapter<>(context, R.layout.my_simple_item_list_text_size_small,
                    items));
        } else if (db.getPreference(DB.PT.PNames.TEXT_SIZE).equals(DB.PT.PDefaultValues.MEDIUM_LETTER)) {
            spinner.setAdapter(new ArrayAdapter<>(context, R.layout.my_simple_item_list_text_size_medium,
                    items));
        } else if (db.getPreference(DB.PT.PNames.TEXT_SIZE).equals(DB.PT.PDefaultValues.BIG_LETTER)) {
            spinner.setAdapter(new ArrayAdapter<>(context, R.layout.my_simple_item_list_text_size_big,
                    items));
        }


    }


}
