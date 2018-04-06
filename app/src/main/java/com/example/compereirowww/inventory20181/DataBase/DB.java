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

    //endregion

    //region SQLiteOpenHelper methods...

    public DB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
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

                    case "Faltante Ignorado":
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

            public static int parse(String type) {
                switch (type) {

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

        public static class FollowingType {

            public static final int NO = 0;
            public static final int YES = 1;

            public static String toString(int followingValue) {
                if (followingValue == YES) {
                    return "Sí";
                } else {
                    return "No";
                }
            }

            public static int parse(String followingValue) {
                if (followingValue.equals("Sí")) {
                    return YES;
                } else {
                    return NO;
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
                                String officialUpdate, int following, int state, long lastChecking,
                                int type, String location, String observation) {

        //Official columns
        ContentValues contents = new ContentValues();
        contents.put(IT_NUMBER_COLUMN, newNumber);
        contents.put(IT_DESCRIPTION_COLUMN, description);
        contents.put(IT_AREA_COLUMN, area);
        contents.put(IT_ALTA_DATE_COLUMN, altaDate);
        contents.put(IT_OFFICIAL_UPDATE_COLUMN, officialUpdate);

        //Custom columns
        contents.put(IT_FOLLOWING_COLUMN, following);
        contents.put(IT_STATE_COLUMN, state);
        contents.put(IT_LAST_CHECKING_COLUMN, lastChecking);
        contents.put(IT_TYPE_COLUMN, type);
        contents.put(IT_LOCATION_COLUMN, location);
        contents.put(IT_OBSERVATION_COLUMN, observation);

        db.insert(IT_NAME, null, contents);
        contents.clear();

    }

    public void updateOfficialData(String number, String description, String area, String altaDate,
                                   String officialUpdate) {
        ContentValues contents = new ContentValues();
        contents.put(IT_DESCRIPTION_COLUMN, description);
        contents.put(IT_AREA_COLUMN, area);
        contents.put(IT_ALTA_DATE_COLUMN, altaDate);
        contents.put(IT_OFFICIAL_UPDATE_COLUMN, officialUpdate);
        db.update(IT_NAME, contents,
                IT_NUMBER_COLUMN + _EQUAL_ + QUOTE + number + QUOTE,
                null);
        contents.clear();
    }

    public void updateDescription(String number, String description) {
        ContentValues contents = new ContentValues();
        contents.put(IT_DESCRIPTION_COLUMN, description);
        db.update(IT_NAME, contents,
                IT_NUMBER_COLUMN + _EQUAL_ + QUOTE + number + QUOTE,
                null);
        contents.clear();
    }

    public void updateLocation(String number, String location) {
        ContentValues contents = new ContentValues();
        contents.put(IT_LOCATION_COLUMN, location);
        db.update(IT_NAME, contents,
                IT_NUMBER_COLUMN + _EQUAL_ + QUOTE + number + QUOTE,
                null);
        contents.clear();
    }

    public void updateFollowing(String number, int following) {
        ContentValues contents = new ContentValues();
        contents.put(IT_FOLLOWING_COLUMN, following);
        db.update(IT_NAME, contents,
                IT_NUMBER_COLUMN + _EQUAL_ + QUOTE + number + QUOTE,
                null);
        contents.clear();
    }

    public void updateFollowingByAreas(String[] areas, int following) {
        ContentValues contents = new ContentValues();
        for (String area : areas) {
            contents.put(IT_FOLLOWING_COLUMN, following);
            db.update(IT_NAME, contents,
                    IT_AREA_COLUMN + _EQUAL_ + QUOTE + area + QUOTE,
                    null);
            contents.clear();
        }
    }

    public void updateObservation(String number, String observation) {
        ContentValues contents = new ContentValues();
        contents.put(IT_OBSERVATION_COLUMN, observation);
        db.update(IT_NAME, contents,
                IT_NUMBER_COLUMN + _EQUAL_ + QUOTE + number + QUOTE,
                null);
        contents.clear();
    }

    public void updateArea(String number, String area) {
        ContentValues contents = new ContentValues();
        contents.put(IT_AREA_COLUMN, area);
        db.update(IT_NAME, contents,
                IT_NUMBER_COLUMN + _EQUAL_ + QUOTE + number + QUOTE,
                null);
        contents.clear();
    }

    public void updateAltaDate(String number, String altaDate) {
        ContentValues contents = new ContentValues();
        contents.put(IT_ALTA_DATE_COLUMN, altaDate);
        db.update(IT_NAME, contents,
                IT_NUMBER_COLUMN + _EQUAL_ + QUOTE + number + QUOTE,
                null);
        contents.clear();
    }

    public void updateOfficialUpdate(String number, String officialUpdate) {
        ContentValues contents = new ContentValues();
        contents.put(IT_OFFICIAL_UPDATE_COLUMN, officialUpdate);
        db.update(IT_NAME, contents,
                IT_NUMBER_COLUMN + _EQUAL_ + QUOTE + number + QUOTE,
                null);
        contents.clear();
    }

    public void updateState(String number, int state) {
        ContentValues contents = new ContentValues();
        contents.put(IT_STATE_COLUMN, state);
        db.update(IT_NAME, contents,
                IT_NUMBER_COLUMN + _EQUAL_ + QUOTE + number + QUOTE,
                null);
        contents.clear();
    }

    public void updateType(String number, int type) {
        ContentValues contents = new ContentValues();
        contents.put(IT_TYPE_COLUMN, type);
        db.update(IT_NAME, contents,
                IT_NUMBER_COLUMN + _EQUAL_ + QUOTE + number + QUOTE,
                null);
        contents.clear();
    }

    public void updateTypeIfDescription(String description, int type) {
        ContentValues contents = new ContentValues();
        contents.put(IT_TYPE_COLUMN, type);
        db.update(IT_NAME, contents,
                IT_DESCRIPTION_COLUMN + _EQUAL_ + QUOTE + description + QUOTE,
                null);
        contents.clear();
    }

    public void updateStateColumn(int state) {
        ContentValues contents = new ContentValues();
        contents.put(IT_STATE_COLUMN, state);
        db.update(IT_NAME, contents, null, null);
        contents.clear();
    }

    public void updateFollowingColumn(int following) {
        ContentValues contents = new ContentValues();
        contents.put(IT_FOLLOWING_COLUMN, following);
        db.update(IT_NAME, contents, null, null);
        contents.clear();
    }

    //endregion

    //region queries

    public long getNumberCount() {
        return db.rawQuery(SELECT_ + ASTERISK + _FROM_ + IT_NAME, null).getCount();
    }

    public boolean numberExist(String number) {

        Cursor cursor = db.rawQuery(SELECT_ + IT_NUMBER_COLUMN + _FROM_ + IT_NAME +
                _WHERE_ + IT_NUMBER_COLUMN + _EQUAL_ + QUOTE + number + QUOTE, null);
        boolean toReturn = cursor.getCount() != 0;
        cursor.close();
        return toReturn;
    }

    public Cursor getAllNumberData(String number) {
        return db.rawQuery(SELECT_ + ASTERISK + _FROM_ + IT_NAME + _WHERE_ +
                IT_NUMBER_COLUMN + _EQUAL_ + QUOTE + number + QUOTE, null);
    }

    public Cursor getAllData() {
        return db.rawQuery(SELECT_ + ASTERISK + _FROM_ + IT_NAME, null);
    }

    public Cursor getAllDataIfState(String state) {

        return db.rawQuery(SELECT_ + ASTERISK + _FROM_ + IT_NAME + _WHERE_ +
                IT_STATE_COLUMN + _EQUAL_ + IT.StateValues.parse(state), null);
    }

    public Cursor getAllDataIfType(String type) {

        return db.rawQuery(SELECT_ + ASTERISK + _FROM_ + IT_NAME + _WHERE_ +
                IT_TYPE_COLUMN + _EQUAL_ + IT.TypeValues.parse(type), null);
    }

    public Cursor getAllDataIfObservation(String observation) {

        return db.rawQuery(SELECT_ + ASTERISK + _FROM_ + IT_NAME + _WHERE_ +
                IT_OBSERVATION_COLUMN + _EQUAL_ + QUOTE + observation + QUOTE, null);
    }

    public Cursor getAllDataIfLocation(String location) {

        return db.rawQuery(SELECT_ + ASTERISK + _FROM_ + IT_NAME + _WHERE_ +
                IT_LOCATION_COLUMN + _EQUAL_ + QUOTE + location + QUOTE, null);
    }

    public Cursor getAllDataIfArea(String area) {

        return db.rawQuery(SELECT_ + ASTERISK + _FROM_ + IT_NAME + _WHERE_ +
                IT_AREA_COLUMN + _EQUAL_ + QUOTE + area + QUOTE, null);
    }

    public Cursor getAllDataIfFollowing(int following) {

        return db.rawQuery(SELECT_ + ASTERISK + _FROM_ + IT_NAME + _WHERE_ +
                IT_FOLLOWING_COLUMN + _EQUAL_ + following, null);
    }

    public Cursor getAllDataIfFollowingAndLocation(int f, String l) {

        return getAllDataIfTwoColumn(IT_FOLLOWING_COLUMN, f, IT_LOCATION_COLUMN, l);
    }

    public Cursor getAllDataIfFollowingAndState(int f, String s) {

        return getAllDataIfTwoColumn(IT_FOLLOWING_COLUMN, f,
                IT_STATE_COLUMN, IT.StateValues.parse(s));
    }

    public Cursor getAllDataIfFollowingAndType(int f, String t) {

        return getAllDataIfTwoColumn(IT_FOLLOWING_COLUMN, f,
                IT_TYPE_COLUMN, IT.TypeValues.parse(t));
    }

    public Cursor getAllDataIfFollowingAndObservation(int f, String o) {

        return getAllDataIfTwoColumn(IT_FOLLOWING_COLUMN, f,
                IT_STATE_COLUMN, o);
    }

    public Cursor getAllDataIfFollowingAndArea(int f, String a) {

        return getAllDataIfTwoColumn(IT_FOLLOWING_COLUMN, f,
                IT_AREA_COLUMN, a);
    }

    public Cursor getAllDataIfLocationAndState(String l, String s) {
        return getAllDataIfTwoColumn(IT_LOCATION_COLUMN, l,
                IT_STATE_COLUMN, IT.StateValues.parse(s));
    }

    public Cursor getAllDataIfLocationAndType(String l, String t) {
        return getAllDataIfTwoColumn(IT_LOCATION_COLUMN, l,
                IT_TYPE_COLUMN, IT.TypeValues.parse(t));
    }

    public Cursor getAllDataIfLocationAndObservation(String l, String o) {
        return getAllDataIfTwoColumn(IT_LOCATION_COLUMN, l,
                IT_OBSERVATION_COLUMN, o);
    }

    public Cursor getAllDataIfLocationAndArea(String l, String a) {
        return getAllDataIfTwoColumn(IT_LOCATION_COLUMN, l,
                IT_AREA_COLUMN, a);
    }

    public Cursor getAllDataIfStateAndType(String s, String t) {

        return getAllDataIfTwoColumn(IT_STATE_COLUMN, IT.StateValues.parse(s),
                IT_TYPE_COLUMN, IT.TypeValues.parse(t));
    }

    public Cursor getAllDataIfStateAndObservation(String s, String o) {

        return getAllDataIfTwoColumn(IT_STATE_COLUMN, IT.StateValues.parse(s),
                IT_OBSERVATION_COLUMN, o);
    }

    public Cursor getAllDataIfStateAndArea(String s, String a) {

        return getAllDataIfTwoColumn(IT_STATE_COLUMN, IT.StateValues.parse(s),
                IT_AREA_COLUMN, a);
    }

    public Cursor getAllDataIfTypeAndObservation(String t, String o) {

        return getAllDataIfTwoColumn(IT_TYPE_COLUMN, IT.TypeValues.parse(t),
                IT_OBSERVATION_COLUMN, o);
    }

    public Cursor getAllDataIfTypeAndArea(String t, String a) {

        return getAllDataIfTwoColumn(IT_TYPE_COLUMN, IT.TypeValues.parse(t),
                IT_AREA_COLUMN, a);
    }

    public Cursor getAllDataIfObservationAndArea(String o, String a) {

        return getAllDataIfTwoColumn(IT_OBSERVATION_COLUMN, o,
                IT_AREA_COLUMN, a);
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

    public String getNumberDescription(String number) {

        Cursor cursor = db.rawQuery(SELECT_ + IT_DESCRIPTION_COLUMN + _FROM_ + IT_NAME + _WHERE_ +
                IT_NUMBER_COLUMN + _EQUAL_ + QUOTE + number + QUOTE, null);

        if (cursor.moveToNext()) {
            return cursor.getString(0);
        } else {
            return "";
        }
    }

    private Cursor getAllDataIfTwoColumn(String c1Name, String c1Value, String c2Name, String c2Value) {
        return db.rawQuery(SELECT_ + ASTERISK + _FROM_ + IT_NAME + _WHERE_ +
                c1Name + _EQUAL_ + QUOTE + c1Value + QUOTE + _AND_ +
                c2Name + _EQUAL_ + QUOTE + c2Value + QUOTE, null);
    }

    private Cursor getAllDataIfTwoColumn(String c1Name, int c1Value, String c2Name, String c2Value) {
        return db.rawQuery(SELECT_ + ASTERISK + _FROM_ + IT_NAME + _WHERE_ +
                c1Name + _EQUAL_ + c1Value + _AND_ +
                c2Name + _EQUAL_ + QUOTE + c2Value + QUOTE, null);
    }

    private Cursor getAllDataIfTwoColumn(String c1Name, String c1Value, String c2Name, int c2Value) {
        return db.rawQuery(SELECT_ + ASTERISK + _FROM_ + IT_NAME + _WHERE_ +
                c1Name + _EQUAL_ + QUOTE + c1Value + QUOTE + _AND_ +
                c2Name + _EQUAL_ + c2Value, null);
    }

    private Cursor getAllDataIfTwoColumn(String c1Name, int c1Value, String c2Name, int c2Value) {
        return db.rawQuery(SELECT_ + ASTERISK + _FROM_ + IT_NAME + _WHERE_ +
                c1Name + _EQUAL_ + c1Value + _AND_ +
                c2Name + _EQUAL_ + c2Value, null);
    }

    public String getNumberLocation(String number) {

        Cursor cursor = db.rawQuery(SELECT_ + IT_LOCATION_COLUMN + _FROM_ + IT_NAME + _WHERE_ +
                IT_NUMBER_COLUMN + _EQUAL_ + QUOTE + number + QUOTE, null);

        if (cursor.moveToNext()) {
            return cursor.getString(0);
        } else {
            return "";
        }
    }

    public String getNumberObservation(String number) {

        Cursor cursor = db.rawQuery(SELECT_ + IT_OBSERVATION_COLUMN + _FROM_ + IT_NAME + _WHERE_ +
                IT_NUMBER_COLUMN + _EQUAL_ + QUOTE + number + QUOTE, null);

        if (cursor.moveToNext()) {
            return cursor.getString(0);
        } else {
            return "";
        }
    }

    public String getNumberArea(String number) {

        Cursor cursor = db.rawQuery(SELECT_ + IT_AREA_COLUMN + _FROM_ + IT_NAME + _WHERE_ +
                IT_NUMBER_COLUMN + _EQUAL_ + QUOTE + number + QUOTE, null);

        if (cursor.moveToNext()) {
            return cursor.getString(0);
        } else {
            return "";
        }
    }

    public String getNumberAltaDate(String number) {

        Cursor cursor = db.rawQuery(SELECT_ + IT_ALTA_DATE_COLUMN + _FROM_ + IT_NAME + _WHERE_ +
                IT_NUMBER_COLUMN + _EQUAL_ + QUOTE + number + QUOTE, null);

        if (cursor.moveToNext()) {
            return cursor.getString(0);
        } else {
            return "";
        }
    }

    public String getNumberOfficialUpdate(String number) {

        Cursor cursor = db.rawQuery(SELECT_ + IT_OFFICIAL_UPDATE_COLUMN + _FROM_ + IT_NAME + _WHERE_ +
                IT_NUMBER_COLUMN + _EQUAL_ + QUOTE + number + QUOTE, null);

        if (cursor.moveToNext()) {
            return cursor.getString(0);
        } else {
            return "";
        }
    }

    public long getNumberLastChecking(String number) {

        Cursor cursor = db.rawQuery(SELECT_ + IT_LAST_CHECKING_COLUMN + _FROM_ + IT_NAME + _WHERE_ +
                IT_NUMBER_COLUMN + _EQUAL_ + QUOTE + number + QUOTE, null);

        if (cursor.moveToNext()) {
            return cursor.getLong(0);
        } else {
            return 0;
        }
    }

    public int getNumberFollowingValue(String number) {

        Cursor cursor = db.rawQuery(SELECT_ + IT_FOLLOWING_COLUMN + _FROM_ + IT_NAME + _WHERE_ +
                IT_NUMBER_COLUMN + _EQUAL_ + QUOTE + number + QUOTE, null);

        if (cursor.moveToNext()) {
            return cursor.getInt(0);
        } else {
            return -1;
        }

    }

    public int getNumberState(String number) {
        Cursor cursor = db.rawQuery(SELECT_ + IT_STATE_COLUMN + _FROM_ + IT_NAME + _WHERE_ +
                IT_NUMBER_COLUMN + _EQUAL_ + QUOTE + number + QUOTE, null);
        if (cursor.moveToNext()) {
            return cursor.getInt(0);
        } else {
            return -1;
        }
    }

    public int getNumberType(String number) {
        Cursor cursor = db.rawQuery(SELECT_ + IT_TYPE_COLUMN + _FROM_ + IT_NAME + _WHERE_ +
                IT_NUMBER_COLUMN + _EQUAL_ + QUOTE + number + QUOTE, null);
        if (cursor.moveToNext()) {
            return cursor.getInt(0);
        } else {
            return -1;
        }
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

        //region Preference's names

        //ImportActivity Preference
        public static final int CURRENT_IMPORTING_FILE_PATH = 1;
        public static final int CURRENT_IMPORTATION_INDEX = 2;
        public static final int APP_IMPORTING = 3;
        public static final int CURRENT_IMPORTATION_FILE_HASH = 4;


        //InventoryActivity;
        public static final int CURRENT_INVENTORY_INDEX = 5;
        public static final int CURRENT_FILTER1_VALUE = 6;
        public static final int CURRENT_FILTER2_VALUE = 7;


        //Files Preferences
        public static final int ROOT_DIRECTORY_PATH = 8;
        public static final int SAVE_DIRECTORY_PATH = 9;
        public static final int TO_IMPORT_DIRECTORY_PATH = 10;

        //EditActivity
        public static final int NUMBER_TO_EDIT = 11;
        public static final int TEMP_NUMBER = 12;
        public static final int TEMP_FOLLOWING = 13;
        public static final int TEMP_STATE = 14;
        public static final int TEMP_TYPE = 15;
        public static final int TEMP_LOCATION = 16;
        public static final int TEMP_OBSERVATION = 17;

        //Configuration Preferences
        public static final int AREAS_TO_FOLLOW_CSV = 18;
        public static final int TEMP_AREAS_TO_FOLLOW_CSV = 19;
        public static final int UPDATE_CRITERIA = 20;
        public static final int TEMP_UPDATE_CRITERIA = 21;


        //endregion

        //region Preference's values
        /**
         * it means the preference exist but is empty
         */
        public static String EMPTY_PREFERENCE = "";
        public static String PREFERENCE_NOT_FOUND = "$$$NOT_FOUND$$$";
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
        ContentValues RTCV = new ContentValues();
        if (!getPreference(ref).equals(RT.PREFERENCE_NOT_FOUND)) {
            RTCV.put(RT_VALUE_COLUMN, value);
            db.update(RT_NAME, RTCV,
                    RT_NAME_COLUMN + _EQUAL_ + ref, null);
            RTCV.clear();
        } else {
            RTCV.put(RT_NAME_COLUMN, ref);
            RTCV.put(RT_VALUE_COLUMN, value);
            db.insert(RT_NAME, null, RTCV);
            RTCV.clear();
        }
    }

    /**
     * Set the value of the named reference,
     *
     * @param ref
     * @param value
     */
    public void setPreference(int ref, long value) {
        ContentValues RTCV = new ContentValues();
        if (!getPreference(ref).equals(RT.PREFERENCE_NOT_FOUND)) {
            RTCV.put(RT_VALUE_COLUMN, value);
            db.update(RT_NAME, RTCV,
                    RT_NAME_COLUMN + _EQUAL_ + ref, null);
            RTCV.clear();
        } else {
            RTCV.put(RT_NAME_COLUMN, ref);
            RTCV.put(RT_VALUE_COLUMN, value);
            db.insert(RT_NAME, null, RTCV);
            RTCV.clear();
        }
    }

    /**
     * @param ref The ref of the preference you want.
     * @return the value of the preference if it exist.
     * when the preference is not found return RT.REFERENCE_NOT_FOUND .
     */
    public String getPreference(int ref) {

        Cursor cursor = db.rawQuery(SELECT_ + RT_VALUE_COLUMN + _FROM_ + RT_NAME + _WHERE_ +
                RT_NAME_COLUMN + _EQUAL_ + ref, null);

        if (cursor.moveToNext()) {
            //TODO Deb
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
                    InventoryActivity.FiltersValues.ALL);
        }

        if (getPreference(RT.CURRENT_FILTER2_VALUE).equals(RT.PREFERENCE_NOT_FOUND)) {
            setPreference(RT.CURRENT_FILTER2_VALUE,
                    InventoryActivity.FiltersValues.ALL);
        }

        //Edit Activity Preferences
        if (getPreference(RT.NUMBER_TO_EDIT).equals(RT.PREFERENCE_NOT_FOUND)) {
            setPreference(RT.NUMBER_TO_EDIT, 0);
        }

        if (getPreference(RT.TEMP_NUMBER).equals(RT.PREFERENCE_NOT_FOUND)) {
            setPreference(RT.TEMP_NUMBER, "");
        }

        if (getPreference(RT.TEMP_FOLLOWING).equals(RT.PREFERENCE_NOT_FOUND)) {
            setPreference(RT.TEMP_FOLLOWING, "");
        }

        if (getPreference(RT.TEMP_LOCATION).equals(RT.PREFERENCE_NOT_FOUND)) {
            setPreference(RT.TEMP_LOCATION, "");
        }

        if (getPreference(RT.TEMP_OBSERVATION).equals(RT.PREFERENCE_NOT_FOUND)) {
            setPreference(RT.TEMP_OBSERVATION, "");
        }

        if (getPreference(RT.TEMP_STATE).equals(RT.PREFERENCE_NOT_FOUND)) {
            setPreference(RT.TEMP_STATE, "");
        }

        if (getPreference(RT.TEMP_TYPE).equals(RT.PREFERENCE_NOT_FOUND)) {
            setPreference(RT.TEMP_TYPE, "");
        }


        //Configuration preferences
        if (getPreference(RT.AREAS_TO_FOLLOW_CSV).equals(RT.PREFERENCE_NOT_FOUND)) {
            setPreference(RT.AREAS_TO_FOLLOW_CSV, RT.EMPTY_PREFERENCE);
        }

        if (getPreference(RT.TEMP_AREAS_TO_FOLLOW_CSV).equals(RT.PREFERENCE_NOT_FOUND)) {
            setPreference(RT.TEMP_AREAS_TO_FOLLOW_CSV, RT.EMPTY_PREFERENCE);
        }

        if (getPreference(RT.UPDATE_CRITERIA).equals(RT.PREFERENCE_NOT_FOUND)) {
            setPreference(RT.UPDATE_CRITERIA, "30");
        }

        if (getPreference(RT.TEMP_UPDATE_CRITERIA).equals(RT.PREFERENCE_NOT_FOUND)) {
            setPreference(RT.TEMP_UPDATE_CRITERIA, RT.EMPTY_PREFERENCE);
        }




    }

    //endregion
}
