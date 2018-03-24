package com.example.compereirowww.inventory20181.Activities;

import android.Manifest;

import com.example.compereirowww.inventory20181.DataBase.DB;

public class AppStatics {

    public class Importation {

        //region To Import Files
        /**
         * This is the index of the first value in a file to import,
         * this depend of the format of the file
         */
        public static final int FIRTS_IMPORT_VALUE_INDEX = 1;

        /**
         * the index of the number in a line of the file to import
         */
        public static final int NUMBER_INDEX = 0;

        /**
         * the index of the description data in a line of the file to import
         */
        public static final int DESCRIPTION_INDEX = 1;

        /**
         * the index of the area data in a line of the file to import
         */
        public static final int AREA_INDEX = 2;

        /**
         * the index of the altaDate data in a line of the file to import
         */
        public static final int ALTA_DATE_INDEX = 3;

        /**
         * the index of the officialUpdate data in a line of the file to import
         */
        public static final int OFFICIAL_UPDATE_INDEX = 4;

    }


    //endregion

    /**
     * The tag used for the application in the log
     */
    public static final String APP_TAG = "JOSE";

    /**
     * The extension of the file
     */
    public static final String IMPORT_FILE_EXTENTION = ".csv";

    /**
     * The criteria to consider the current import file as
     * valid
     */
    public static final int IMPORTING_FILE_OUT_OF_DATE_CRITERIA = 600000;

    /**
     * The name of the app folder
     */
    public static final String APP_FILE = "Inventario UH";

    /**
     * The name of the app folder where will be placed the files to import
     */
    public static final String APP_TO_IMPORT_FILE = "Para importar";

    /**
     * The name of the app folder where will be placed the save files
     */
    public static final String APP_SAVE_FILE = "Archivos salvados";

    /**
     * This is a tool, that allow not to load the DB every time you start an Activity
     * call!!!
     */
    public static DB db;

    // Storage Permissions
    public static final int REQUEST_EXTERNAL_STORAGE = 1;
    public static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * The head that any uh inventory file most have to be imported
     */
    public static final String UH_INVENTORY_FILE_HEAD_CODE = "Archivo UH para importar...";

    /**
     * The head that any salva file most have to be imported
     */
    public static final String SALVA_INVENTORY_FILE_HEAD_CODE = "Archivo salva...";

}
