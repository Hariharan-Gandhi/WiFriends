package tud.cnlab.wifriends.datahandlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import tud.cnlab.wifriends.WiFriendsApplication;

public class TblMyProfile extends SQLiteOpenHelper {

    public static final String TAG = "DBAdapter:dbProfile";
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "dbProfile";
    private static final String DB_TABLE_NAME_MY_PROFILE = "tblMyProfile";
    /* SQL Query Statements */
    private static final String QUERY_READ_MY_PROFILE_DETAILS = "SELECT * FROM " + DB_TABLE_NAME_MY_PROFILE;
    private static final String QUERY_INSERT_MY_PROFILE_DEFAULT = "INSERT INTO "
            + DB_TABLE_NAME_MY_PROFILE
            + " VALUES(1, null, null, null, null, null, null, null);";
    private static final String DB_TABLE_NAME_FRIENDS_PROFILE = "tblFriendsProfile";
    public static String id = "_id";
    public static String fMac = "USER_MAC";
    private static final String QUERY_READ_FRIENDS_PROFILE_DETAILS = "SELECT * FROM "
            + DB_TABLE_NAME_FRIENDS_PROFILE
            + " WHERE " + fMac + " = ?";
    public static String fUname = "USER_NAME";
    public static String fUid = "USER_ID";
    public static String fAbout = "ABOUT";
    public static String fStatus = "STATUS";
    public static String fHappy = "WEEKLY_HAPPY_EVENTS";
    public static String fUnhap = "WEEKLY_ANNOYED_EVENTS";
    /* SQL DDL Statements: Create Table  */
    private static final String DB_TABLE_CREATE_MY_PROFILE = "CREATE TABLE "
            + DB_TABLE_NAME_MY_PROFILE + " ( " + id + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
            + fMac + " TEXT, " + fUname + " TEXT, " + fUid + " TEXT, "
            + fAbout + " TEXT, " + fStatus + " TEXT, " + fHappy + " TEXT, "
            + fUnhap + " TEXT " + ");";
    private static final String DB_TABLE_CREATE_FRIENDS_PROFILE = "CREATE TABLE "
            + DB_TABLE_NAME_FRIENDS_PROFILE + " ( " + id + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
            + fMac + " TEXT, " + fUname + " TEXT, " + fUid + " TEXT, "
            + fAbout + " TEXT, " + fStatus + " TEXT, " + fHappy + " TEXT, "
            + fUnhap + " TEXT " + ");";
    private SQLiteDatabase db;
    private SQLiteOpenHelper dbHelper;

    private static TblMyProfile sInstance;

    /**
     * Create a helper object to create, open, and/or manage a database.
     * This method always returns very quickly.  The database is not actually
     * created or opened until one of {@link #getWritableDatabase} or
     * {@link #getReadableDatabase} is called.
     *
     * @param applicationContext to use to open or create the database
     */
    public TblMyProfile(Context applicationContext) {

        super(applicationContext, DB_NAME, null, DB_VERSION);
    }


    public static TblMyProfile getInstance() {

        Context context = WiFriendsApplication.getAppContext();

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new TblMyProfile(context.getApplicationContext());
        }
        return sInstance;
    }
    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Inside DBHandler onCreate");
        try {
            db.execSQL(DB_TABLE_CREATE_MY_PROFILE);
            db.execSQL(DB_TABLE_CREATE_FRIENDS_PROFILE);
            db.execSQL(QUERY_INSERT_MY_PROFILE_DEFAULT);
            Log.d(TAG, " DB Creation Success");
        } catch (SQLException e) {
            Log.d(TAG, "Failed DB Creation");
            e.printStackTrace();
        }
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     * <p/>
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Database Upgrade From Version " + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_NAME_MY_PROFILE);
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_NAME_FRIENDS_PROFILE);
        onCreate(db);
    }

    public void updateMyProfile(MdlProfileDbHandler myProfile) {

        SQLiteDatabase DB_w = this.getWritableDatabase();

        ContentValues profileContents = new ContentValues();

        System.out.println("Printing Values from Inside Tbl: \n" + myProfile);

        profileContents.put(fMac, myProfile.getUSER_MAC());
        profileContents.put(fUname, myProfile.getUSER_NAME());
        profileContents.put(fUid, myProfile.getUSER_ID());
        profileContents.put(fAbout, myProfile.getABOUT());
        profileContents.put(fStatus, myProfile.getSTATUS());
        profileContents.put(fHappy, myProfile.getWEEKLY_HAPPY_EVENTS());
        profileContents.put(fUnhap, myProfile.getWEEKLY_ANNOYED_EVENTS());

        long dbCheck = DB_w.update(DB_TABLE_NAME_MY_PROFILE, profileContents, null, null);

        if (dbCheck == -1) {
            Log.d(TAG, "Error in inserting");
        } else {
            Log.d(TAG, "Updated My Profile Successfully");
        }
        DB_w.close();

    }

    public void updateProfile(MdlProfileDbHandler myProfile) {

        long dbCheck;

        SQLiteDatabase DB_w = this.getWritableDatabase();

        ContentValues profileContents = new ContentValues();

        System.out.println("Printing Values from Inside Tbl: \n" + myProfile);

        Cursor cursor = DB_w.rawQuery(QUERY_READ_FRIENDS_PROFILE_DETAILS, new String[]{myProfile.getUSER_MAC()});

        profileContents.put(fUname, myProfile.getUSER_NAME());
        profileContents.put(fUid, myProfile.getUSER_ID());
        profileContents.put(fAbout, myProfile.getABOUT());
        profileContents.put(fStatus, myProfile.getSTATUS());
        profileContents.put(fHappy, myProfile.getWEEKLY_HAPPY_EVENTS());
        profileContents.put(fUnhap, myProfile.getWEEKLY_ANNOYED_EVENTS());

        System.out.println("Cursor Count: " + cursor.getCount());
        if (cursor.getCount() == 0) {
            System.out.println("Cursor is NULL");

            profileContents.put(fMac, myProfile.getUSER_MAC());
            dbCheck = DB_w.insert(DB_TABLE_NAME_FRIENDS_PROFILE, null, profileContents);
        } else {
            dbCheck = DB_w.update(DB_TABLE_NAME_FRIENDS_PROFILE, profileContents, fMac + " = ?", new String[]{myProfile.getUSER_MAC()});
        }

        if (dbCheck == -1) {
            Log.d(TAG, "Error in inserting");
        } else {
            Log.d(TAG, "Updated Profile Successfully");
        }
        DB_w.close();

    }

    public MdlProfileDbHandler retrieveMyProfile() {

        MdlProfileDbHandler myProfile = new MdlProfileDbHandler();

        SQLiteDatabase DB_r = this.getReadableDatabase();

        Cursor cursor = DB_r.rawQuery(QUERY_READ_MY_PROFILE_DETAILS, null);

        if (cursor.moveToFirst()) {

            do {
                myProfile.setUSER_MAC(cursor.getString(1));
                myProfile.setUSER_NAME(cursor.getString(2));
                myProfile.setUSER_ID(cursor.getString(3));
                myProfile.setABOUT(cursor.getString(4));
                myProfile.setSTATUS(cursor.getString(5));
                myProfile.setWEEKLY_HAPPY_EVENTS(cursor.getString(6));
                myProfile.setWEEKLY_ANNOYED_EVENTS(cursor.getString(7));
            } while (cursor.moveToNext());
        }

        DB_r.close();
        return myProfile;
    }

    public MdlProfileDbHandler retrieveProfile(String MAC) {

        System.out.println("Inside retrieveMyProfile: " + MAC);
        MdlProfileDbHandler myProfile = new MdlProfileDbHandler();

        SQLiteDatabase DB_r = this.getReadableDatabase();

        Cursor cursor = DB_r.rawQuery(QUERY_READ_FRIENDS_PROFILE_DETAILS, new String[]{MAC});

        System.out.println("Cursor Count: " + cursor.getCount());
        if (cursor.getCount() == 0) {
            System.out.println("Cursor is NULL");
        }
        if (cursor.moveToFirst()) {

            do {
                myProfile.setUSER_MAC(cursor.getString(1));
                myProfile.setUSER_NAME(cursor.getString(2));
                myProfile.setUSER_ID(cursor.getString(3));
                myProfile.setABOUT(cursor.getString(4));
                myProfile.setSTATUS(cursor.getString(5));
                myProfile.setWEEKLY_HAPPY_EVENTS(cursor.getString(6));
                myProfile.setWEEKLY_ANNOYED_EVENTS(cursor.getString(7));
            } while (cursor.moveToNext());
        }

        DB_r.close();
        return myProfile;
    }

    public Cursor RetrieveFriends() {

        String QUERY_READ_TABLE = "SELECT * FROM " + DB_TABLE_NAME_FRIENDS_PROFILE;

        SQLiteDatabase DB_r = this.getReadableDatabase();

        Cursor cursor = DB_r.rawQuery(QUERY_READ_TABLE, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        DB_r.close();

        return cursor;
    }
}
