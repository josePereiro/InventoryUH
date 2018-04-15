package com.example.compereirowww.inventory20181.Activities;

import android.Manifest;
import android.database.Cursor;

import com.example.compereirowww.inventory20181.DataBase.DB;

import java.util.ArrayList;
import java.util.Arrays;

public class AppStatics {

    public static class Importation {

        //region To Import Files
        /**
         * This is the index of the first value in a file to import,
         * this depend of the format of the file
         */
        public static final int CSV_FILE_UH_INVENTORY_FIRST_DATA_LINE_INDEX = 1;

        /**
         * the index of the number in a line of the file to import
         */
        public static final int NUMBER_INDEX = 0;

        /**
         * the index of the description CSVData in a line of the file to import
         */
        public static final int DESCRIPTION_INDEX = 1;

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

        //endregion
    }

    public static class Location {

        /**
         * Track statically the current locations
         */
        public static String[] locations = new String[]{""};

        /**
         * update the locations from the db
         */
        public static void updateLocations(DB db) {

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

        public static void updateAreas(DB db) {

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

        public static void updateObservations(DB db) {

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

        public static void updateAreasToFollow(DB db) {

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

    public static class AllNumbers {

        public static String[] allNumbers = new String[]{""};

        public static void updateAllNumbers(DB db) {

            if (Arrays.equals(allNumbers, new String[]{""})) {
                Cursor cursor = db.getAllNumbers();
                ArrayList<String> numbersAL = new ArrayList<>();
                String current;
                while (cursor.moveToNext()) {
                    current = cursor.getString(0);
                    numbersAL.add(current);
                }

                allNumbers = numbersAL.toArray(new String[]{""});
                Arrays.sort(allNumbers, String.CASE_INSENSITIVE_ORDER);
            }
        }


    }

    /**
     * The tag used for the application in the log
     */
    public static final String APP_TAG = "JOSE";

    /**
     * The head that any uh inventory file most have to be imported
     */
    public static final String UH_INVENTORY_FILE_HEAD_CODE = "Archivo UH para importar...";

    /**
     * The head that any salva file most have to be imported
     */
    public static final String SALVA_INVENTORY_FILE_HEAD_CODE = "Archivo salva...";


    /**
     * The extension of the file
     */
    public static final String IMPORT_FILE_EXTENTION = ".csv";


    public static final int QR_DECODER_REQUEST = 626;

    /**
     * The criteria to consider the current import file as
     * valid
     */
    public static final int IMPORTING_FILE_OUT_OF_DATE_CRITERIA = 600000;

    /**
     * The name of the app folder
     */
    public static final String APP_FILE_NAME = "Inventario UH";

    /**
     * The name of the app folder where will be placed the files to import
     */
    public static final String APP_TO_IMPORT_FILE_NAME = "Para importar";

    /**
     * The name of the app folder where will be placed the save files
     */
    public static final String APP_SAVE_FILE_NAME = "Archivos salvados";

    /**
     * The name of the qr folder where will be placed all the qr codes
     */
    public static final String APP_QRS_FILE_NAME = "Códigos QRs";

    public static final int QRS_SIZE = 50;

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

}
