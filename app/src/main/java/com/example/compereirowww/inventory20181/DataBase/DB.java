package com.example.compereirowww.inventory20181.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.compereirowww.inventory20181.Activities.InventoryActivity;

public class DB extends SQLiteOpenHelper {


    //General stuffs -------------------------------------------------------------------------------

    //region General DB fields...

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Inventory.db";

    //SQLite CONSTANTS
    private static final String CREATE_TABLE_ = "CREATE TABLE ";
    private static final String _LIKE_ = " LIKE ";
    private static final String PERCENT = "%";
    private static final String SMALL_QUOTE = "'";
    private static final String _TEXT_TYPE = " TEXT";
    private static final String _BIGINT_TYPE = " BIGINT";
    private static final String _INTEGER_TYPE = " INTEGER";
    private static final String _BOOLEAN_TYPE = " BOOLEAN";
    private static final String _SMALLINT_TYPE = " SMALLINT";
    private static final String _OPEN_PARENTHESIS = " (";
    private static final String OPEN_PARENTHESIS = "(";
    private static final String CLOSE_PARENTHESIS = ")";
    private static final String _CLOSE_PARENTHESIS = " )";
    private static final String SELECT_ = "SELECT ";
    private static final String ASTERISK = "*";
    private static final String _FROM_ = " FROM ";
    private static final String _WHERE_ = " WHERE ";
    private static final String _AND_ = " AND ";
    private static final String _OR_ = " OR ";
    private static final String _EQUAL_ = " = ";
    private static final String _UNION_ = " UNION ";
    private static final String _LESS_THAN_ = " < ";
    private static final String _GRATER_THAN_ = " < ";
    private static final String _NOT_EQUAL_ = " <> ";
    private static final String QUOTE = "\"";
    private static final String _COMMA_SEP = " ,";

    //DB Tools
    SQLiteDatabase db;
    private final ContentValues contentValues;
    private Cursor cursor;

    //endregion

    //region SQLiteOpenHelper methods...

    public DB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        contentValues = new ContentValues();
        if (db == null) {
            db = getWritableDatabase();
        }
        setUpPreferences();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_INVENTORY_TABLE);
        sqLiteDatabase.execSQL(CREATE_PREFERENCE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    //endregion

    //Inventory Table ------------------------------------------------------------------------------
    //region Inventory Table Fields...

    //region Table's names...
    private static final String IT_NAME = "Inventory";

    //Columns
    //Official fields
    private static final String IT_NUMBER_COLUMN = "number";
    private static final String IT_DESCRIPTION_COLUMN = "description";
    private static final String IT_AREA_COLUMN = "area";
    private static final String IT_ALTA_DATE_COLUMN = "alta_date";
    private static final String IT_OFFICIAL_UPDATE_COLUMN = "last_official_update";

    //Custom fields
    private static final String IT_FOLLOWING_COLUMN = "fallowing";
    private static final String IT_STATE_COLUMN = "state";
    private static final String IT_LAST_CHECKING_COLUMN = "last_checking";
    private static final String IT_LOCATION_COLUMN = "location";
    private static final String IT_TYPE_COLUMN = "type";
    private static final String IT_OBSERVATION_COLUMN = "observation";

    //endregion

    //region Values...

    /**
     * This class handle all the static values of the
     * inventory table
     */
    public static class IT {

        /**
         * This class handle the values of the state column
         */
        public static class StateValues {

            //VALUES
            /**
             * This value represent that the object in the inventory was
             * checked lately (within the update criteria).
             */
            public static final int PRESENT = 50;
            /**
             * This value represent that the object in the inventory wasn't
             * checked lately (within the update criteria) or was
             * set this way handily
             */
            public static final int MISSING = 51;
            /**
             * This value represent that the object in the inventory is
             * missing but you do not care!!!
             */
            public static final int IGNORED_MISSING = 52;
            /**
             * This value represent that the object in the inventory wasn't
             * imported the last time.
             */
            public static final int LEFTOVER = 53;


            public static String toString(int state) {
                if (state == PRESENT) {
                    return "Presente";
                } else if (state == MISSING) {
                    return "Faltante";
                } else if (state == IGNORED_MISSING) {
                    return "Faltante Ignorado";
                } else if (state == LEFTOVER) {
                    return "Sobrante";
                } else {
                    return "";
                }
            }

            public static int parse(String state) {
                switch (state) {

                    case "Presente":
                        return PRESENT;

                    case "Faltante":
                        return MISSING;

                    case "Faltante Ignorao":
                        return IGNORED_MISSING;

                    case "Sobrante":
                        return LEFTOVER;

                    default:
                        return -1;
                }
            }

        }

        public static class TypeValues {

            /**
             * This type is used to label an inventory object as an equipment, as
             * ex. microscope.
             */
            public static final int EQUIPMENT = 40;
            /**
             * This type is used to label an inventory object as a furnishing, as
             * ex. a table.
             */
            public static final int FURNISHING = 41;
            /**
             * This type is used when you do not know the type of the inventory
             * object
             */
            public static final int UNKNOWN = 42;

            public static String toString(int type) {
                if (type == EQUIPMENT) {
                    return "Equipo";
                } else if (type == FURNISHING) {
                    return "Mueble";
                } else if (type == UNKNOWN) {
                    return "Desconocido";
                } else {
                    return "";
                }
            }

            public static int parse(String state) {
                switch (state) {

                    case "Equipo":
                        return EQUIPMENT;

                    case "Mueble":
                        return FURNISHING;

                    case "Desconocido":
                        return UNKNOWN;

                    default:
                        return -1;
                }
            }

        }

    }

    //endregion

    //region Statements...

    //Create table
    private static final String CREATE_INVENTORY_TABLE =
            CREATE_TABLE_ + IT_NAME + _OPEN_PARENTHESIS +
                    //official columns
                    IT_NUMBER_COLUMN + _TEXT_TYPE + _COMMA_SEP +
                    IT_DESCRIPTION_COLUMN + _TEXT_TYPE + _COMMA_SEP +
                    IT_AREA_COLUMN + _TEXT_TYPE + _COMMA_SEP +
                    IT_ALTA_DATE_COLUMN + _TEXT_TYPE + _COMMA_SEP +
                    IT_OFFICIAL_UPDATE_COLUMN + _TEXT_TYPE + _COMMA_SEP +
                    //custom columns
                    IT_FOLLOWING_COLUMN + _BOOLEAN_TYPE + _COMMA_SEP +
                    IT_STATE_COLUMN + _SMALLINT_TYPE + _COMMA_SEP +
                    IT_LAST_CHECKING_COLUMN + _BIGINT_TYPE + _COMMA_SEP +
                    IT_TYPE_COLUMN + _SMALLINT_TYPE + _COMMA_SEP +
                    IT_LOCATION_COLUMN + _TEXT_TYPE + _COMMA_SEP +
                    IT_OBSERVATION_COLUMN + _TEXT_TYPE + CLOSE_PARENTHESIS;


    //endregion

    //endregion

    //region Inventory Methods...

    //region change base data

    /**
     * Insert a new number, it do not check if the number is repeated, so
     * be careful!!!
     *
     * @param newNumber
     * @param description
     * @param area
     * @param altaDate
     * @param officialUpdate
     * @param following
     * @param state
     * @param lastChecking
     * @param type
     * @param location
     * @param observation
     */
    public void insertNewNumber(String newNumber, String description, String area, String altaDate,
                                String officialUpdate, boolean following, int state, long lastChecking,
                                int type, String location, String observation) {

        //Official columns
        contentValues.put(IT_NUMBER_COLUMN, newNumber);
        contentValues.put(IT_DESCRIPTION_COLUMN, description);
        contentValues.put(IT_AREA_COLUMN, area);
        contentValues.put(IT_ALTA_DATE_COLUMN, altaDate);
        contentValues.put(IT_OFFICIAL_UPDATE_COLUMN, officialUpdate);

        //Custom columns
        contentValues.put(IT_FOLLOWING_COLUMN, following);
        contentValues.put(IT_STATE_COLUMN, state);
        contentValues.put(IT_LAST_CHECKING_COLUMN, lastChecking);
        contentValues.put(IT_TYPE_COLUMN, type);
        contentValues.put(IT_LOCATION_COLUMN, location);
        contentValues.put(IT_OBSERVATION_COLUMN, observation);

        db.insert(IT_NAME, null, contentValues);
        contentValues.clear();

    }

    public void updateDescription(String number, String description) {
        contentValues.put(IT_DESCRIPTION_COLUMN, description);
        db.update(IT_NAME, contentValues,
                IT_NUMBER_COLUMN + _EQUAL_ + QUOTE + number + QUOTE,
                null);
        contentValues.clear();
    }

    public void updateArea(String number, String area) {
        contentValues.put(IT_AREA_COLUMN, area);
        db.update(IT_NAME, contentValues,
                IT_NUMBER_COLUMN + _EQUAL_ + QUOTE + number + QUOTE,
                null);
        contentValues.clear();
    }

    public void updateAltaDate(String number, String altaDate) {
        contentValues.put(IT_ALTA_DATE_COLUMN, altaDate);
        db.update(IT_NAME, contentValues,
                IT_NUMBER_COLUMN + _EQUAL_ + QUOTE + number + QUOTE,
                null);
        contentValues.clear();
    }

    public void updateOfficialUpdate(String number, String officialUpdate) {
        contentValues.put(IT_OFFICIAL_UPDATE_COLUMN, officialUpdate);
        db.update(IT_NAME, contentValues,
                IT_NUMBER_COLUMN + _EQUAL_ + QUOTE + number + QUOTE,
                null);
        contentValues.clear();
    }

    public void updateState(String number, int state) {
        contentValues.put(IT_STATE_COLUMN, state);
        db.update(IT_NAME, contentValues,
                IT_NUMBER_COLUMN + _EQUAL_ + QUOTE + number + QUOTE,
                null);
        contentValues.clear();
    }

    public void updateStateColumn(int state) {
        contentValues.put(IT_STATE_COLUMN, state);
        db.update(IT_NAME, contentValues, null, null);
        contentValues.clear();
    }

    //endregion

    //region queries

    public long getNumberCount() {
        return db.rawQuery(SELECT_ + ASTERISK + _FROM_ + IT_NAME, null).getCount();
    }

    public boolean numberExist(String number) {

        cursor = db.rawQuery(SELECT_ + IT_NUMBER_COLUMN + _FROM_ + IT_NAME +
                _WHERE_ + IT_NUMBER_COLUMN + _EQUAL_ + QUOTE + number + QUOTE, null);
        boolean toReturn = cursor.getCount() != 0;
        cursor.close();
        return toReturn;
    }

    public Cursor getAllData() {
        return db.rawQuery(SELECT_ + ASTERISK + _FROM_ + IT_NAME, null);
    }

    public Cursor getLocationColumnData() {
        return db.rawQuery(SELECT_ + IT_LOCATION_COLUMN + _FROM_ + IT_NAME, null);
    }

    public Cursor getAreaColumnData() {
        return db.rawQuery(SELECT_ + IT_AREA_COLUMN + _FROM_ + IT_NAME, null);
    }

    public Cursor getObservationColumnData() {
        return db.rawQuery(SELECT_ + IT_OBSERVATION_COLUMN + _FROM_ + IT_NAME, null);
    }

    //endregion

    //endregion Inventory Methods...

    //References Table -----------------------------------------------------------------------------
    //region Preference Table Fields...

    //region Table's names...

    private static final String RT_NAME = "Preferences";

    //Columns
    /**
     * This column store the name of the preference,
     * Most be unique!!!
     */
    private static final String RT_NAME_COLUMN = "name";

    /**
     * This column store the value of the preference
     */
    private static final String RT_VALUE_COLUMN = "value";


    //endregion

    //region Values

    /**
     * This class handle with all the static values of yhe
     * preference table.
     */
    public static class RT {

        /**
         * it means the preference exist but is empty
         */
        public static String EMPTY_PREFERENCE = "";
        public static String PREFERENCE_NOT_FOUND = "$$$NOT_FOUND$$$";

        //region Preference's names

        //ImportActivity Preference
        public static final int CURRENT_IMPORTING_FILE_PATH = 1;
        public static final int CURRENT_IMPORTATION_INDEX = 2;
        public static final int APP_IMPORTING = 3;
        public static final int CURRENT_IMPORTATION_FILE_HASH = 7;


        //InventoryActivity;
        public static final int CURRENT_INVENTORY_INDEX = 8;
        public static final int CURRENT_FILTER1_VALUE = 9;
        public static final int CURRENT_FILTER2_VALUE = 10;


        //Files Preferences
        public static final int ROOT_DIRECTORY_PATH = 4;
        public static final int SAVE_DIRECTORY_PATH = 5;
        public static final int TO_IMPORT_DIRECTORY_PATH = 6;

        //endregion

        //region Preference's values

        public static final String YES = "Yes";
        public static final String CANCELLED = "Can";
        public static final String FINISHING = "Fin";
        public static final String NO = "No";

        //endregion


    }


    //endregion

    //region Statements

    private static final String CREATE_PREFERENCE_TABLE =
            CREATE_TABLE_ + RT_NAME + _OPEN_PARENTHESIS +
                    RT_NAME_COLUMN + _INTEGER_TYPE + _COMMA_SEP +
                    RT_VALUE_COLUMN + _TEXT_TYPE + CLOSE_PARENTHESIS;
    //endregion

    //endregion

    //region Preference Table Methods...

    /**
     * Set the value of the named reference,
     *
     * @param ref
     * @param value
     */
    public void setPreference(int ref, String value) {

        if (!getPreference(ref).equals(RT.PREFERENCE_NOT_FOUND)) {
            contentValues.put(RT_VALUE_COLUMN, value);
            db.update(RT_NAME, contentValues,
                    RT_NAME_COLUMN + _EQUAL_ + ref, null);
            contentValues.clear();
        } else {
            contentValues.put(RT_NAME_COLUMN, ref);
            contentValues.put(RT_VALUE_COLUMN, value);
            db.insert(RT_NAME, null, contentValues);
            contentValues.clear();
        }
    }

    /**
     * Set the value of the named reference,
     *
     * @param ref
     * @param value
     */
    public void setPreference(int ref, long value) {

        if (!getPreference(ref).equals(RT.PREFERENCE_NOT_FOUND)) {
            contentValues.put(RT_VALUE_COLUMN, value);
            db.update(RT_NAME, contentValues,
                    RT_NAME_COLUMN + _EQUAL_ + ref, null);
            contentValues.clear();
        } else {
            contentValues.put(RT_NAME_COLUMN, ref);
            contentValues.put(RT_VALUE_COLUMN, value);
            db.insert(RT_NAME, null, contentValues);
            contentValues.clear();
        }
    }

    /**
     * @param ref The ref of the preference you want.
     * @return the value of the preference if it exist.
     * when the preference is not found return RT.REFERENCE_NOT_FOUND .
     */
    public String getPreference(int ref) {

        if (ref == RT.APP_IMPORTING) {
            System.out.print("bla");
        }

        Cursor cursor = db.rawQuery(SELECT_ + RT_VALUE_COLUMN + _FROM_ + RT_NAME + _WHERE_ +
                RT_NAME_COLUMN + _EQUAL_ + ref, null);

        if (cursor.moveToNext()) {
            return cursor.getString(0);
        }

        cursor.close();
        return RT.PREFERENCE_NOT_FOUND;
    }

    /**
     * This method initialize all the preferences if they doesn't exist!
     */
    private void setUpPreferences() {

        //Importation
        if (getPreference(RT.CURRENT_IMPORTING_FILE_PATH).equals(RT.PREFERENCE_NOT_FOUND)) {
            setPreference(RT.CURRENT_IMPORTING_FILE_PATH, RT.EMPTY_PREFERENCE);
        }

        if (getPreference(RT.CURRENT_IMPORTATION_INDEX).equals(RT.PREFERENCE_NOT_FOUND)) {
            setPreference(RT.CURRENT_IMPORTATION_INDEX, 0);
        }

        if (getPreference(RT.APP_IMPORTING).equals(RT.PREFERENCE_NOT_FOUND)) {
            setPreference(RT.APP_IMPORTING, RT.NO);
        }

        if (getPreference(RT.CURRENT_IMPORTATION_FILE_HASH).equals(RT.PREFERENCE_NOT_FOUND)) {
            setPreference(RT.CURRENT_IMPORTATION_FILE_HASH, RT.EMPTY_PREFERENCE);
        }

        //App directories
        if (getPreference(RT.ROOT_DIRECTORY_PATH).equals(RT.PREFERENCE_NOT_FOUND)) {
            setPreference(RT.ROOT_DIRECTORY_PATH, RT.EMPTY_PREFERENCE);
        }

        if (getPreference(RT.SAVE_DIRECTORY_PATH).equals(RT.PREFERENCE_NOT_FOUND)) {
            setPreference(RT.SAVE_DIRECTORY_PATH, RT.EMPTY_PREFERENCE);
        }

        if (getPreference(RT.TO_IMPORT_DIRECTORY_PATH).equals(RT.PREFERENCE_NOT_FOUND)) {
            setPreference(RT.TO_IMPORT_DIRECTORY_PATH, RT.EMPTY_PREFERENCE);
        }

        //Inventory Activity Preferences
        if (getPreference(RT.CURRENT_INVENTORY_INDEX).equals(RT.PREFERENCE_NOT_FOUND)) {
            setPreference(RT.CURRENT_INVENTORY_INDEX, 0);
        }

        if (getPreference(RT.CURRENT_FILTER1_VALUE).equals(RT.PREFERENCE_NOT_FOUND)) {
            setPreference(RT.CURRENT_FILTER1_VALUE,
                    InventoryActivity.FiltersValues.Filter1.ALL);
        }

        if (getPreference(RT.CURRENT_FILTER2_VALUE).equals(RT.PREFERENCE_NOT_FOUND)) {
            setPreference(RT.CURRENT_FILTER2_VALUE,
                    InventoryActivity.FiltersValues.Filter2.ALL);
        }

    }

    //endregion
}
