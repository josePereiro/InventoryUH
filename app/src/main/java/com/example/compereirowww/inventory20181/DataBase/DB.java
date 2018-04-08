package com.example.compereirowww.inventory20181.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.DateUtils;

import com.example.compereirowww.inventory20181.Activities.InventoryActivity;
import com.example.compereirowww.inventory20181.DataBase.DB.IT.ITNames;
import com.example.compereirowww.inventory20181.Tools.Tools;

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
        sqLiteDatabase.execSQL(IT.CREATE_INVENTORY_TABLE);
        sqLiteDatabase.execSQL(PT.CREATE_PREFERENCE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

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
        contents.put(ITNames.NUMBER_COLUMN_NAME, newNumber);
        contents.put(ITNames.DESCRIPTION_COLUMN_NAME, description);
        contents.put(ITNames.AREA_COLUMN_NAME, area);
        contents.put(ITNames.ALTA_DATE_COLUMN_NAME, altaDate);
        contents.put(ITNames.OFFICIAL_UPDATE_COLUMN_NAME, officialUpdate);

        //Custom columns
        contents.put(ITNames.FOLLOWING_COLUMN_NAME, following);
        contents.put(ITNames.STATE_COLUMN_NAME, state);
        contents.put(ITNames.LAST_CHECKING_COLUMN_NAME, lastChecking);
        contents.put(ITNames.TYPE_COLUMN_NAME, type);
        contents.put(ITNames.LOCATION_COLUMN_NAME, location);
        contents.put(ITNames.OBSERVATION_COLUMN_NAME, observation);

        db.insert(ITNames.INVENTORY_TABLE_NAME, null, contents);
        contents.clear();

    }

    public int updateOfficialDataAndState(String number, String description, String area, String altaDate,
                                          String officialUpdate, int state) {
        ContentValues contents = new ContentValues();
        contents.put(ITNames.DESCRIPTION_COLUMN_NAME, description);
        contents.put(ITNames.AREA_COLUMN_NAME, area);
        contents.put(ITNames.ALTA_DATE_COLUMN_NAME, altaDate);
        contents.put(ITNames.OFFICIAL_UPDATE_COLUMN_NAME, officialUpdate);
        contents.put(ITNames.STATE_COLUMN_NAME, state);
        return db.update(ITNames.INVENTORY_TABLE_NAME, contents,
                ITNames.NUMBER_COLUMN_NAME + _EQUAL_ + QUOTE + number + QUOTE,
                null);
    }

    public void updateDescription(String number, String description) {
        ContentValues contents = new ContentValues();
        contents.put(ITNames.DESCRIPTION_COLUMN_NAME, description);
        db.update(ITNames.INVENTORY_TABLE_NAME, contents,
                ITNames.NUMBER_COLUMN_NAME + _EQUAL_ + QUOTE + number + QUOTE,
                null);
        contents.clear();
    }

    public void updateLocation(String number, String location) {
        ContentValues contents = new ContentValues();
        contents.put(ITNames.LOCATION_COLUMN_NAME, location);
        db.update(ITNames.INVENTORY_TABLE_NAME, contents,
                ITNames.NUMBER_COLUMN_NAME + _EQUAL_ + QUOTE + number + QUOTE,
                null);
        contents.clear();
    }

    public void updateFollowing(String number, int following) {
        ContentValues contents = new ContentValues();
        contents.put(ITNames.FOLLOWING_COLUMN_NAME, following);
        db.update(ITNames.INVENTORY_TABLE_NAME, contents,
                ITNames.NUMBER_COLUMN_NAME + _EQUAL_ + QUOTE + number + QUOTE,
                null);
        contents.clear();
    }

    public void updateFollowingByAreas(String[] areas, int following) {
        ContentValues contents = new ContentValues();
        for (String area : areas) {
            contents.put(ITNames.FOLLOWING_COLUMN_NAME, following);
            db.update(ITNames.INVENTORY_TABLE_NAME, contents,
                    ITNames.AREA_COLUMN_NAME + _EQUAL_ + QUOTE + area + QUOTE,
                    null);
            contents.clear();
        }
    }

    public void updateObservation(String number, String observation) {
        ContentValues contents = new ContentValues();
        contents.put(ITNames.OBSERVATION_COLUMN_NAME, observation);
        db.update(ITNames.INVENTORY_TABLE_NAME, contents,
                ITNames.NUMBER_COLUMN_NAME + _EQUAL_ + QUOTE + number + QUOTE,
                null);
        contents.clear();
    }

    public void updateLastChecking(String number, long lastChecking) {
        ContentValues contents = new ContentValues();
        contents.put(ITNames.LAST_CHECKING_COLUMN_NAME, lastChecking);
        db.update(ITNames.INVENTORY_TABLE_NAME, contents,
                ITNames.NUMBER_COLUMN_NAME + _EQUAL_ + QUOTE + number + QUOTE,
                null);
        contents.clear();
    }

    public void updateState(String number, int state) {
        ContentValues contents = new ContentValues();
        contents.put(ITNames.STATE_COLUMN_NAME, state);
        db.update(ITNames.INVENTORY_TABLE_NAME, contents,
                ITNames.NUMBER_COLUMN_NAME + _EQUAL_ + QUOTE + number + QUOTE,
                null);
        contents.clear();
    }

    public void updateTypeIfDescription(String description, int type) {
        ContentValues contents = new ContentValues();
        contents.put(ITNames.TYPE_COLUMN_NAME, type);
        db.update(ITNames.INVENTORY_TABLE_NAME, contents,
                ITNames.DESCRIPTION_COLUMN_NAME + _EQUAL_ + QUOTE + description + QUOTE,
                null);
        contents.clear();
    }

    public void updateStateColumn(int state) {
        ContentValues contents = new ContentValues();
        contents.put(ITNames.STATE_COLUMN_NAME, state);
        db.update(ITNames.INVENTORY_TABLE_NAME, contents, null, null);
        contents.clear();
    }

    public void updateLastCheckingColumn(long lastChecking) {
        ContentValues contents = new ContentValues();
        contents.put(ITNames.LAST_CHECKING_COLUMN_NAME, lastChecking);
        db.update(ITNames.INVENTORY_TABLE_NAME, contents, null, null);
        contents.clear();
    }

    public void updateFollowingColumn(int following) {
        ContentValues contents = new ContentValues();
        contents.put(ITNames.FOLLOWING_COLUMN_NAME, following);
        db.update(ITNames.INVENTORY_TABLE_NAME, contents, null, null);
        contents.clear();
    }

    public void updatePresentToMissingIfOutOfDate(int outOfDateCriteria) {

        long limitDate = Tools.getDate() - outOfDateCriteria * DateUtils.DAY_IN_MILLIS;
        ContentValues contents = new ContentValues();
        contents.put(ITNames.STATE_COLUMN_NAME, IT.StateValues.MISSING);
        db.update(ITNames.INVENTORY_TABLE_NAME, contents, ITNames.LAST_CHECKING_COLUMN_NAME +
                _LESS_THAN_ + limitDate +
                _AND_
                + ITNames.STATE_COLUMN_NAME + _EQUAL_ +
                IT.StateValues.PRESENT, null);
        contents.clear();
    }

    //endregion

    //region queries

    public long getNumberCount() {
        return db.rawQuery(SELECT_ + ASTERISK + _FROM_ + ITNames.INVENTORY_TABLE_NAME, null).getCount();
    }

    public boolean numberExist(String number) {

        Cursor cursor = db.rawQuery(SELECT_ + ITNames.NUMBER_COLUMN_NAME + _FROM_ + ITNames.INVENTORY_TABLE_NAME +
                _WHERE_ + ITNames.NUMBER_COLUMN_NAME + _EQUAL_ + QUOTE + number + QUOTE, null);
        boolean toReturn = cursor.getCount() != 0;
        cursor.close();
        return toReturn;
    }

    public Cursor getAllNumberData(String number) {
        return db.rawQuery(SELECT_ + ASTERISK + _FROM_ + ITNames.INVENTORY_TABLE_NAME + _WHERE_ +
                ITNames.NUMBER_COLUMN_NAME + _EQUAL_ + QUOTE + number + QUOTE, null);
    }

    public Cursor getAllData() {
        return db.rawQuery(SELECT_ + ASTERISK + _FROM_ + ITNames.INVENTORY_TABLE_NAME, null);
    }

    public Cursor getAllDataIfState(String state) {

        return db.rawQuery(SELECT_ + ASTERISK + _FROM_ + ITNames.INVENTORY_TABLE_NAME + _WHERE_ +
                ITNames.STATE_COLUMN_NAME + _EQUAL_ + IT.StateValues.parse(state), null);
    }

    public Cursor getAllDataIfType(String type) {

        return db.rawQuery(SELECT_ + ASTERISK + _FROM_ + ITNames.INVENTORY_TABLE_NAME + _WHERE_ +
                ITNames.TYPE_COLUMN_NAME + _EQUAL_ + IT.TypeValues.parse(type), null);
    }

    public Cursor getAllDataIfObservation(String observation) {

        return db.rawQuery(SELECT_ + ASTERISK + _FROM_ + ITNames.INVENTORY_TABLE_NAME + _WHERE_ +
                ITNames.OBSERVATION_COLUMN_NAME + _EQUAL_ + QUOTE + observation + QUOTE, null);
    }

    public Cursor getAllDataIfLocation(String location) {

        return db.rawQuery(SELECT_ + ASTERISK + _FROM_ + ITNames.INVENTORY_TABLE_NAME + _WHERE_ +
                ITNames.LOCATION_COLUMN_NAME + _EQUAL_ + QUOTE + location + QUOTE, null);
    }

    public Cursor getAllDataIfArea(String area) {

        return db.rawQuery(SELECT_ + ASTERISK + _FROM_ + ITNames.INVENTORY_TABLE_NAME + _WHERE_ +
                ITNames.AREA_COLUMN_NAME + _EQUAL_ + QUOTE + area + QUOTE, null);
    }

    public Cursor getAllDataIfFollowing(int following) {

        return db.rawQuery(SELECT_ + ASTERISK + _FROM_ + ITNames.INVENTORY_TABLE_NAME + _WHERE_ +
                ITNames.FOLLOWING_COLUMN_NAME + _EQUAL_ + following, null);
    }

    public Cursor getAllDataIfFollowingAndLocation(int f, String l) {

        return getAllDataIfTwoColumn(ITNames.FOLLOWING_COLUMN_NAME, f, ITNames.LOCATION_COLUMN_NAME, l);
    }

    public Cursor getAllDataIfFollowingAndState(int f, String s) {

        return getAllDataIfTwoColumn(ITNames.FOLLOWING_COLUMN_NAME, f,
                ITNames.STATE_COLUMN_NAME, IT.StateValues.parse(s));
    }

    public Cursor getAllDataIfFollowingAndType(int f, String t) {

        return getAllDataIfTwoColumn(ITNames.FOLLOWING_COLUMN_NAME, f,
                ITNames.TYPE_COLUMN_NAME, IT.TypeValues.parse(t));
    }

    public Cursor getAllDataIfFollowingAndObservation(int f, String o) {

        return getAllDataIfTwoColumn(ITNames.FOLLOWING_COLUMN_NAME, f,
                ITNames.STATE_COLUMN_NAME, o);
    }

    public Cursor getAllDataIfFollowingAndArea(int f, String a) {

        return getAllDataIfTwoColumn(ITNames.FOLLOWING_COLUMN_NAME, f,
                ITNames.AREA_COLUMN_NAME, a);
    }

    public Cursor getAllDataIfLocationAndState(String l, String s) {
        return getAllDataIfTwoColumn(ITNames.LOCATION_COLUMN_NAME, l,
                ITNames.STATE_COLUMN_NAME, IT.StateValues.parse(s));
    }

    public Cursor getAllDataIfLocationAndType(String l, String t) {
        return getAllDataIfTwoColumn(ITNames.LOCATION_COLUMN_NAME, l,
                ITNames.TYPE_COLUMN_NAME, IT.TypeValues.parse(t));
    }

    public Cursor getAllDataIfLocationAndObservation(String l, String o) {
        return getAllDataIfTwoColumn(ITNames.LOCATION_COLUMN_NAME, l,
                ITNames.OBSERVATION_COLUMN_NAME, o);
    }

    public Cursor getAllDataIfLocationAndArea(String l, String a) {
        return getAllDataIfTwoColumn(ITNames.LOCATION_COLUMN_NAME, l,
                ITNames.AREA_COLUMN_NAME, a);
    }

    public Cursor getAllDataIfStateAndType(String s, String t) {

        return getAllDataIfTwoColumn(ITNames.STATE_COLUMN_NAME, IT.StateValues.parse(s),
                ITNames.TYPE_COLUMN_NAME, IT.TypeValues.parse(t));
    }

    public Cursor getAllDataIfStateAndObservation(String s, String o) {

        return getAllDataIfTwoColumn(ITNames.STATE_COLUMN_NAME, IT.StateValues.parse(s),
                ITNames.OBSERVATION_COLUMN_NAME, o);
    }

    public Cursor getAllDataIfStateAndArea(String s, String a) {

        return getAllDataIfTwoColumn(ITNames.STATE_COLUMN_NAME, IT.StateValues.parse(s),
                ITNames.AREA_COLUMN_NAME, a);
    }

    public Cursor getAllDataIfTypeAndObservation(String t, String o) {

        return getAllDataIfTwoColumn(ITNames.TYPE_COLUMN_NAME, IT.TypeValues.parse(t),
                ITNames.OBSERVATION_COLUMN_NAME, o);
    }

    public Cursor getAllDataIfTypeAndArea(String t, String a) {

        return getAllDataIfTwoColumn(ITNames.TYPE_COLUMN_NAME, IT.TypeValues.parse(t),
                ITNames.AREA_COLUMN_NAME, a);
    }

    public Cursor getAllDataIfObservationAndArea(String o, String a) {

        return getAllDataIfTwoColumn(ITNames.OBSERVATION_COLUMN_NAME, o,
                ITNames.AREA_COLUMN_NAME, a);
    }

    public Cursor getLocationColumnData() {
        return db.rawQuery(SELECT_ + ITNames.LOCATION_COLUMN_NAME + _FROM_ + ITNames.INVENTORY_TABLE_NAME, null);
    }

    public Cursor getAreaColumnData() {
        return db.rawQuery(SELECT_ + ITNames.AREA_COLUMN_NAME + _FROM_ + ITNames.INVENTORY_TABLE_NAME, null);
    }

    public Cursor getObservationColumnData() {
        return db.rawQuery(SELECT_ + ITNames.OBSERVATION_COLUMN_NAME + _FROM_ + ITNames.INVENTORY_TABLE_NAME, null);
    }

    public Cursor getDescriptionColumnData() {
        return db.rawQuery(SELECT_ + ITNames.DESCRIPTION_COLUMN_NAME + _FROM_ + ITNames.INVENTORY_TABLE_NAME, null);
    }

    public String getNumberDescription(String number) {

        Cursor cursor = db.rawQuery(SELECT_ + ITNames.DESCRIPTION_COLUMN_NAME + _FROM_ + ITNames.INVENTORY_TABLE_NAME + _WHERE_ +
                ITNames.NUMBER_COLUMN_NAME + _EQUAL_ + QUOTE + number + QUOTE, null);

        if (cursor.moveToNext()) {
            return cursor.getString(0);
        } else {
            return "";
        }
    }

    private Cursor getAllDataIfTwoColumn(String c1Name, String c1Value, String c2Name, String c2Value) {
        return db.rawQuery(SELECT_ + ASTERISK + _FROM_ + ITNames.INVENTORY_TABLE_NAME + _WHERE_ +
                c1Name + _EQUAL_ + QUOTE + c1Value + QUOTE + _AND_ +
                c2Name + _EQUAL_ + QUOTE + c2Value + QUOTE, null);
    }

    private Cursor getAllDataIfTwoColumn(String c1Name, int c1Value, String c2Name, String c2Value) {
        return db.rawQuery(SELECT_ + ASTERISK + _FROM_ + ITNames.INVENTORY_TABLE_NAME + _WHERE_ +
                c1Name + _EQUAL_ + c1Value + _AND_ +
                c2Name + _EQUAL_ + QUOTE + c2Value + QUOTE, null);
    }

    private Cursor getAllDataIfTwoColumn(String c1Name, String c1Value, String c2Name, int c2Value) {
        return db.rawQuery(SELECT_ + ASTERISK + _FROM_ + ITNames.INVENTORY_TABLE_NAME + _WHERE_ +
                c1Name + _EQUAL_ + QUOTE + c1Value + QUOTE + _AND_ +
                c2Name + _EQUAL_ + c2Value, null);
    }

    private Cursor getAllDataIfTwoColumn(String c1Name, int c1Value, String c2Name, int c2Value) {
        return db.rawQuery(SELECT_ + ASTERISK + _FROM_ + ITNames.INVENTORY_TABLE_NAME + _WHERE_ +
                c1Name + _EQUAL_ + c1Value + _AND_ +
                c2Name + _EQUAL_ + c2Value, null);
    }

    public String getNumberLocation(String number) {

        Cursor cursor = db.rawQuery(SELECT_ + ITNames.LOCATION_COLUMN_NAME + _FROM_ + ITNames.INVENTORY_TABLE_NAME + _WHERE_ +
                ITNames.NUMBER_COLUMN_NAME + _EQUAL_ + QUOTE + number + QUOTE, null);

        if (cursor.moveToNext()) {
            return cursor.getString(0);
        } else {
            return "";
        }
    }

    public String getNumberObservation(String number) {

        Cursor cursor = db.rawQuery(SELECT_ + ITNames.OBSERVATION_COLUMN_NAME + _FROM_ + ITNames.INVENTORY_TABLE_NAME + _WHERE_ +
                ITNames.NUMBER_COLUMN_NAME + _EQUAL_ + QUOTE + number + QUOTE, null);

        if (cursor.moveToNext()) {
            return cursor.getString(0);
        } else {
            return "";
        }
    }

    public String getNumberArea(String number) {

        Cursor cursor = db.rawQuery(SELECT_ + ITNames.AREA_COLUMN_NAME + _FROM_ + ITNames.INVENTORY_TABLE_NAME + _WHERE_ +
                ITNames.NUMBER_COLUMN_NAME + _EQUAL_ + QUOTE + number + QUOTE, null);

        if (cursor.moveToNext()) {
            return cursor.getString(0);
        } else {
            return "";
        }
    }

    public String getNumberAltaDate(String number) {

        Cursor cursor = db.rawQuery(SELECT_ + ITNames.ALTA_DATE_COLUMN_NAME + _FROM_ + ITNames.INVENTORY_TABLE_NAME + _WHERE_ +
                ITNames.NUMBER_COLUMN_NAME + _EQUAL_ + QUOTE + number + QUOTE, null);

        if (cursor.moveToNext()) {
            return cursor.getString(0);
        } else {
            return "";
        }
    }

    public String getNumberOfficialUpdate(String number) {

        Cursor cursor = db.rawQuery(SELECT_ + ITNames.OFFICIAL_UPDATE_COLUMN_NAME + _FROM_ + ITNames.INVENTORY_TABLE_NAME + _WHERE_ +
                ITNames.NUMBER_COLUMN_NAME + _EQUAL_ + QUOTE + number + QUOTE, null);

        if (cursor.moveToNext()) {
            return cursor.getString(0);
        } else {
            return "";
        }
    }

    public long getNumberLastChecking(String number) {

        Cursor cursor = db.rawQuery(SELECT_ + ITNames.LAST_CHECKING_COLUMN_NAME + _FROM_ + ITNames.INVENTORY_TABLE_NAME + _WHERE_ +
                ITNames.NUMBER_COLUMN_NAME + _EQUAL_ + QUOTE + number + QUOTE, null);

        if (cursor.moveToNext()) {
            return cursor.getLong(0);
        } else {
            return 0;
        }
    }

    public int getNumberFollowingValue(String number) {

        Cursor cursor = db.rawQuery(SELECT_ + ITNames.FOLLOWING_COLUMN_NAME + _FROM_ + ITNames.INVENTORY_TABLE_NAME + _WHERE_ +
                ITNames.NUMBER_COLUMN_NAME + _EQUAL_ + QUOTE + number + QUOTE, null);

        if (cursor.moveToNext()) {
            return cursor.getInt(0);
        } else {
            return -1;
        }

    }

    public int getNumberState(String number) {
        Cursor cursor = db.rawQuery(SELECT_ + ITNames.STATE_COLUMN_NAME + _FROM_ + ITNames.INVENTORY_TABLE_NAME + _WHERE_ +
                ITNames.NUMBER_COLUMN_NAME + _EQUAL_ + QUOTE + number + QUOTE, null);
        if (cursor.moveToNext()) {
            return cursor.getInt(0);
        } else {
            return -1;
        }
    }

    public int getNumberType(String number) {
        Cursor cursor = db.rawQuery(SELECT_ + ITNames.TYPE_COLUMN_NAME + _FROM_ + ITNames.INVENTORY_TABLE_NAME + _WHERE_ +
                ITNames.NUMBER_COLUMN_NAME + _EQUAL_ + QUOTE + number + QUOTE, null);
        if (cursor.moveToNext()) {
            return cursor.getInt(0);
        } else {
            return -1;
        }
    }

    public Cursor getNumbersDataThatContain(String[] columnsToSearch, String[] matchesForEachColumn) {

        String query = SELECT_ + ITNames.NUMBER_COLUMN_NAME + _FROM_ + ITNames.INVENTORY_TABLE_NAME + _WHERE_;
        int minLength = Math.min(columnsToSearch.length, matchesForEachColumn.length);
        for (int i = 0; i < minLength; i++) {
            if (i == 0) {
                query += columnsToSearch[i] + _LIKE_ + SMALL_QUOTE + PERCENT + matchesForEachColumn[i] +
                        PERCENT + SMALL_QUOTE;
            } else {
                query += _AND_ + columnsToSearch[i] + _LIKE_ + SMALL_QUOTE + PERCENT + matchesForEachColumn[i] +
                        PERCENT + SMALL_QUOTE;
            }
        }

        return db.rawQuery(query, null);
    }

    //endregion

    //endregion Inventory Methods...

    //region Preference Table Methods...

    /**
     * Set the value of the named reference,
     *
     * @param ref
     * @param value
     */
    public void setPreference(int ref, String value) {
        ContentValues RTCV = new ContentValues();
        if (!getPreference(ref).equals(PT.Values.PREFERENCE_NOT_FOUND)) {
            RTCV.put(PT.PTNames.VALUE_COLUMN_NAME, value);
            db.update(PT.PTNames.PREFERENCE_TABLE_NAME, RTCV,
                    PT.PTNames.NAME_COLUMN_NAME + _EQUAL_ + ref, null);
            RTCV.clear();
        } else {
            RTCV.put(PT.PTNames.NAME_COLUMN_NAME, ref);
            RTCV.put(PT.PTNames.VALUE_COLUMN_NAME, value);
            db.insert(PT.PTNames.PREFERENCE_TABLE_NAME, null, RTCV);
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
        if (!getPreference(ref).equals(PT.Values.PREFERENCE_NOT_FOUND)) {
            RTCV.put(PT.PTNames.VALUE_COLUMN_NAME, value);
            db.update(PT.PTNames.PREFERENCE_TABLE_NAME, RTCV,
                    PT.PTNames.NAME_COLUMN_NAME + _EQUAL_ + ref, null);
            RTCV.clear();
        } else {
            RTCV.put(PT.PTNames.NAME_COLUMN_NAME, ref);
            RTCV.put(PT.PTNames.VALUE_COLUMN_NAME, value);
            db.insert(PT.PTNames.PREFERENCE_TABLE_NAME, null, RTCV);
            RTCV.clear();
        }
    }

    /**
     * @param ref The ref of the preference you want.
     * @return the value of the preference if it exist.
     * when the preference is not found return PT.REFERENCE_NOT_FOUND .
     */
    public String getPreference(int ref) {

        Cursor cursor = db.rawQuery(SELECT_ + PT.PTNames.VALUE_COLUMN_NAME + _FROM_ + PT.PTNames.PREFERENCE_TABLE_NAME + _WHERE_ +
                PT.PTNames.NAME_COLUMN_NAME + _EQUAL_ + ref, null);

        if (cursor.moveToNext()) {
            return cursor.getString(0);
        }

        cursor.close();
        return PT.Values.PREFERENCE_NOT_FOUND;
    }

    /**
     * This method initialize all the preferences if they doesn't exist!
     */
    private void setUpPreferences() {

        //Importation
        if (getPreference(PT.PNames.CURRENT_IMPORTING_FILE_PATH).equals(PT.Values.PREFERENCE_NOT_FOUND)) {
            setPreference(PT.PNames.CURRENT_IMPORTING_FILE_PATH, PT.Values.EMPTY_PREFERENCE);
        }

        if (getPreference(PT.PNames.CURRENT_IMPORTATION_INDEX).equals(PT.Values.PREFERENCE_NOT_FOUND)) {
            setPreference(PT.PNames.CURRENT_IMPORTATION_INDEX, 0);
        }

        if (getPreference(PT.PNames.APP_IMPORTING).equals(PT.Values.PREFERENCE_NOT_FOUND)) {
            setPreference(PT.PNames.APP_IMPORTING, PT.Values.NO);
        }

        if (getPreference(PT.PNames.CURRENT_IMPORTATION_FILE_HASH).equals(PT.Values.PREFERENCE_NOT_FOUND)) {
            setPreference(PT.PNames.CURRENT_IMPORTATION_FILE_HASH, PT.Values.EMPTY_PREFERENCE);
        }

        //App directories
        if (getPreference(PT.PNames.ROOT_DIRECTORY_PATH).equals(PT.Values.PREFERENCE_NOT_FOUND)) {
            setPreference(PT.PNames.ROOT_DIRECTORY_PATH, PT.Values.EMPTY_PREFERENCE);
        }

        if (getPreference(PT.PNames.SAVE_DIRECTORY_PATH).equals(PT.Values.PREFERENCE_NOT_FOUND)) {
            setPreference(PT.PNames.SAVE_DIRECTORY_PATH, PT.Values.EMPTY_PREFERENCE);
        }

        if (getPreference(PT.PNames.TO_IMPORT_DIRECTORY_PATH).equals(PT.Values.PREFERENCE_NOT_FOUND)) {
            setPreference(PT.PNames.TO_IMPORT_DIRECTORY_PATH, PT.Values.EMPTY_PREFERENCE);
        }

        //Inventory Activity Preferences
        if (getPreference(PT.PNames.CURRENT_INVENTORY_INDEX).equals(PT.Values.PREFERENCE_NOT_FOUND)) {
            setPreference(PT.PNames.CURRENT_INVENTORY_INDEX, 0);
        }

        if (getPreference(PT.PNames.CURRENT_FILTER1_VALUE).equals(PT.Values.PREFERENCE_NOT_FOUND)) {
            setPreference(PT.PNames.CURRENT_FILTER1_VALUE,
                    InventoryActivity.FiltersValues.ALL);
        }

        if (getPreference(PT.PNames.CURRENT_FILTER2_VALUE).equals(PT.Values.PREFERENCE_NOT_FOUND)) {
            setPreference(PT.PNames.CURRENT_FILTER2_VALUE,
                    InventoryActivity.FiltersValues.ALL);
        }

        //Edit Activity Preferences
        if (getPreference(PT.PNames.NUMBER_TO_EDIT).equals(PT.Values.PREFERENCE_NOT_FOUND)) {
            setPreference(PT.PNames.NUMBER_TO_EDIT, 0);
        }

        if (getPreference(PT.PNames.TEMP_NUMBER).equals(PT.Values.PREFERENCE_NOT_FOUND)) {
            setPreference(PT.PNames.TEMP_NUMBER, "");
        }

        if (getPreference(PT.PNames.TEMP_FOLLOWING).equals(PT.Values.PREFERENCE_NOT_FOUND)) {
            setPreference(PT.PNames.TEMP_FOLLOWING, "");
        }

        if (getPreference(PT.PNames.TEMP_LOCATION).equals(PT.Values.PREFERENCE_NOT_FOUND)) {
            setPreference(PT.PNames.TEMP_LOCATION, "");
        }

        if (getPreference(PT.PNames.TEMP_OBSERVATION).equals(PT.Values.PREFERENCE_NOT_FOUND)) {
            setPreference(PT.PNames.TEMP_OBSERVATION, "");
        }

        if (getPreference(PT.PNames.TEMP_STATE).equals(PT.Values.PREFERENCE_NOT_FOUND)) {
            setPreference(PT.PNames.TEMP_STATE, "");
        }

        if (getPreference(PT.PNames.TEMP_TYPE).equals(PT.Values.PREFERENCE_NOT_FOUND)) {
            setPreference(PT.PNames.TEMP_TYPE, "");
        }


        //Configuration preferences
        if (getPreference(PT.PNames.AREAS_TO_FOLLOW_CSV).equals(PT.Values.PREFERENCE_NOT_FOUND)) {
            setPreference(PT.PNames.AREAS_TO_FOLLOW_CSV, PT.Values.EMPTY_PREFERENCE);
        }

        if (getPreference(PT.PNames.TEMP_AREAS_TO_FOLLOW_CSV).equals(PT.Values.PREFERENCE_NOT_FOUND)) {
            setPreference(PT.PNames.TEMP_AREAS_TO_FOLLOW_CSV, PT.Values.EMPTY_PREFERENCE);
        }

        if (getPreference(PT.PNames.UPDATE_CRITERIA).equals(PT.Values.PREFERENCE_NOT_FOUND)) {
            setPreference(PT.PNames.UPDATE_CRITERIA, "30");
        }

        if (getPreference(PT.PNames.TEMP_UPDATE_CRITERIA).equals(PT.Values.PREFERENCE_NOT_FOUND)) {
            setPreference(PT.PNames.TEMP_UPDATE_CRITERIA, PT.Values.EMPTY_PREFERENCE);
        }

        //Search preferences
        if (getPreference(PT.PNames.TEMP_SEARCH_CRITERIA).equals(PT.Values.PREFERENCE_NOT_FOUND)) {
            setPreference(PT.PNames.TEMP_SEARCH_CRITERIA, PT.Values.EMPTY_PREFERENCE);
        }

        if (getPreference(PT.PNames.SELECTED_CHECKBOXES).equals(PT.Values.PREFERENCE_NOT_FOUND)) {
            setPreference(PT.PNames.SELECTED_CHECKBOXES, PT.Values.EMPTY_PREFERENCE);
        }

        //New number preferences
        if (getPreference(PT.PNames.TEMP_DESCRIPTION).equals(PT.Values.PREFERENCE_NOT_FOUND)) {
            setPreference(PT.PNames.TEMP_DESCRIPTION, PT.Values.EMPTY_PREFERENCE);
        }

    }

    //endregion

    public static class IT {


        public static class ITNames {

            public static final String INVENTORY_TABLE_NAME = "Inventory";

            //Columns
            //Official fields
            public static final String NUMBER_COLUMN_NAME = "number";
            public static final String DESCRIPTION_COLUMN_NAME = "description";
            public static final String AREA_COLUMN_NAME = "area";
            public static final String ALTA_DATE_COLUMN_NAME = "alta_date";
            public static final String OFFICIAL_UPDATE_COLUMN_NAME = "last_official_update";

            //Custom fields
            public static final String FOLLOWING_COLUMN_NAME = "fallowing";
            public static final String STATE_COLUMN_NAME = "state";
            public static final String LAST_CHECKING_COLUMN_NAME = "last_checking";
            public static final String LOCATION_COLUMN_NAME = "location";
            public static final String TYPE_COLUMN_NAME = "type";
            public static final String OBSERVATION_COLUMN_NAME = "observation";

        }

        public static class Indexes {

            public static final int NUMBER_COLUMN_INDEX = 0;
            public static final int DESCRIPTION_COLUMN_INDEX = 1;
            public static final int AREA_COLUMN_INDEX = 2;
            public static final int ALTA_DATE_COLUMN_INDEX = 3;
            public static final int OFFICIAL_UPDATE_COLUMN_INDEX = 4;
            public static final int FOLLOWING_COLUMN_INDEX = 5;
            public static final int STATE_COLUMN_INDEX = 6;
            public static final int LAST_CHECKING_COLUMN_INDEX = 7;
            public static final int LOCATION_COLUMN_INDEX = 8;
            public static final int TYPE_COLUMN_INDEX = 9;
            public static final int OBSERVATION_COLUMN_INDEX = 10;


        }

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
            /**
             * This value represent that the object in the inventory wasn't
             * imported the last time but its qr code was founded
             */
            public static final int LEFTOVER_PRESENT = 54;


            public static String toString(int state) {
                if (state == PRESENT) {
                    return "Presente";
                } else if (state == MISSING) {
                    return "Faltante";
                } else if (state == IGNORED_MISSING) {
                    return "Faltante Ignorado";
                } else if (state == LEFTOVER) {
                    return "Sobrante";
                } else if (state == LEFTOVER_PRESENT) {
                    return "Sobrante Presente";
                } else {
                    return "";
                }
            }

            public static int parse(String stateValue) {
                switch (stateValue.toUpperCase()) {

                    case "PRESENTE":
                        return PRESENT;

                    case "FALTANTE":
                        return MISSING;

                    case "FALTANTE IGNORADO":
                        return IGNORED_MISSING;

                    case "SOBRANTE":
                        return LEFTOVER;

                    case "SOBRANTE PRESENTE":
                        return LEFTOVER_PRESENT;

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

            public static int parse(String typeValue) {
                switch (typeValue.toUpperCase()) {

                    case "EQUIPO":
                        return EQUIPMENT;

                    case "MUEBLE":
                        return FURNISHING;

                    case "DESCONOCIDO":
                        return UNKNOWN;

                    default:
                        return -1;
                }
            }

        }

        public static class FollowingValues {

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

                switch (followingValue.toUpperCase()) {

                    case "SÍ":
                        return YES;

                    case "NO":
                        return YES;

                    case "SI":
                        return YES;

                    default:
                        return -1;
                }
            }


        }

        //region Statements...

        //Create table
        private static final String CREATE_INVENTORY_TABLE =
                CREATE_TABLE_ + ITNames.INVENTORY_TABLE_NAME + _OPEN_PARENTHESIS +
                        //official columns
                        ITNames.NUMBER_COLUMN_NAME + _TEXT_TYPE + _COMMA_SEP +
                        ITNames.DESCRIPTION_COLUMN_NAME + _TEXT_TYPE + _COMMA_SEP +
                        ITNames.AREA_COLUMN_NAME + _TEXT_TYPE + _COMMA_SEP +
                        ITNames.ALTA_DATE_COLUMN_NAME + _TEXT_TYPE + _COMMA_SEP +
                        ITNames.OFFICIAL_UPDATE_COLUMN_NAME + _TEXT_TYPE + _COMMA_SEP +
                        //custom columns
                        ITNames.FOLLOWING_COLUMN_NAME + _BOOLEAN_TYPE + _COMMA_SEP +
                        ITNames.STATE_COLUMN_NAME + _SMALLINT_TYPE + _COMMA_SEP +
                        ITNames.LAST_CHECKING_COLUMN_NAME + _BIGINT_TYPE + _COMMA_SEP +
                        ITNames.TYPE_COLUMN_NAME + _SMALLINT_TYPE + _COMMA_SEP +
                        ITNames.LOCATION_COLUMN_NAME + _TEXT_TYPE + _COMMA_SEP +
                        ITNames.OBSERVATION_COLUMN_NAME + _TEXT_TYPE + CLOSE_PARENTHESIS;


        //endregion

    }

    public static class PT {

        public static class PNames {

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

            //Search preferences
            public static final int TEMP_SEARCH_CRITERIA = 22;
            public static final int SELECTED_CHECKBOXES = 23;

            //New number preferences
            public static final int TEMP_DESCRIPTION = 24;

        }

        private static class PTNames {

            private static final String PREFERENCE_TABLE_NAME = "Preferences";

            //Columns
            /**
             * This column store the name of the preference,
             * Most be unique!!!
             */
            private static final String NAME_COLUMN_NAME = "name";

            /**
             * This column store the value of the preference
             */
            private static final String VALUE_COLUMN_NAME = "value";


        }

        public static class Values {

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

        private static final String CREATE_PREFERENCE_TABLE =
                CREATE_TABLE_ + PT.PTNames.PREFERENCE_TABLE_NAME + _OPEN_PARENTHESIS +
                        PT.PTNames.NAME_COLUMN_NAME + _INTEGER_TYPE + _COMMA_SEP +
                        PT.PTNames.VALUE_COLUMN_NAME + _TEXT_TYPE + CLOSE_PARENTHESIS;

    }
}
