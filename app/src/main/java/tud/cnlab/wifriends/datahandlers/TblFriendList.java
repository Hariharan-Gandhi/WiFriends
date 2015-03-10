package tud.cnlab.wifriends.datahandlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import tud.cnlab.wifriends.WiFriendsApplication;

/**
 * Created by Hariharan Gandhi, DSS Master Student, TU Darmstadt on "12/20/2014"
 * for the project "WiFriends"
 */
public class TblFriendList extends SQLiteOpenHelper {

    public static final String TAG = "DBAdapter";
    private static final String DB_NAME = "dbFriendList";
    private static final int DB_VERSION = 1;
    private static final String DB_TABLE_NAME = "tblFriendList";
    public static String fUid = "_id";
    public static String fMac = "USER_MAC";
    public static String fKey = "AES_KEY";
    private static final String DB_TABLE_CREATE = "CREATE TABLE "
            + DB_TABLE_NAME + " ( " + fUid + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + fMac + " TEXT, "
            + fKey + " BLOB " + ");";

    private static TblFriendList sInstance;
    /**
     * Create a helper object to create, open, and/or manage a database.
     * This method always returns very quickly.  The database is not actually
     * created or opened until one of {@link #getWritableDatabase} or
     * {@link #getReadableDatabase} is called.
     *
     * @param applicationContext to use to open or create the database
     */
    public TblFriendList(Context applicationContext) {
        super(applicationContext, DB_NAME, null, DB_VERSION);
    }

    public static TblFriendList getInstance() {
        
        Context context = WiFriendsApplication.getAppContext();

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new TblFriendList(context.getApplicationContext());
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
        try {
            db.execSQL(DB_TABLE_CREATE);
        } catch (SQLException e) {
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
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_NAME);
        onCreate(db);
    }

    /**
     * Function to create a record in the table <b>tblFriendList</b>
     *
     * @param oFriendInfo Object of <b>MdlFriendListDbHandler</b> contains {MAC, AES_KEY}
     * @return
     */
    public boolean CreateFriendRecord(MdlFriendListDbHandler oFriendInfo) {

        SQLiteDatabase DB_w = this.getWritableDatabase();

        ContentValues friendInfo = new ContentValues();
        friendInfo.put(fMac, oFriendInfo.getUSER_MAC());
        friendInfo.put(fKey, oFriendInfo.getAES_KEY());

        long dbCheck = DB_w.insert(DB_TABLE_NAME, null, friendInfo);
        DB_w.close();
        return dbCheck != -1;
    }

   /* public boolean CheckFriendship(String MAC){

        MdlFriendListDbHandler friendInfo = new MdlFriendListDbHandler();

        String QUERY_READ_TABLE = "SELECT * FROM " + DB_TABLE_NAME +
                " WHERE" + fMac + " = ?";

        SQLiteDatabase DB_r = this.getReadableDatabase();

        Cursor cursor = DB_r.rawQuery(QUERY_READ_TABLE, new String[]{MAC});

        if (cursor.getCount()==1){
            return true;
        }else{
            return false;
        }
    }*/

    /**
     * CheckFriendship: Method that verify if the broadcast service is a Friend
     *
     * @param MAC MAC address of the incoming broadcast request
     * @return Object of type MdlFriendListDbHandler with {MAC, AES_KEY}
     */
    public MdlFriendListDbHandler CheckFriendship(String MAC) {

        MdlFriendListDbHandler friendInfo = new MdlFriendListDbHandler();

        String QUERY_READ_TABLE = "SELECT * FROM " + DB_TABLE_NAME +
                " WHERE " + fMac + " = ?";

        SQLiteDatabase DB_r = this.getReadableDatabase();

        Cursor cursor = DB_r.rawQuery(QUERY_READ_TABLE, new String[]{MAC});

        if (cursor.moveToFirst()) {
            do {
                friendInfo.setUSER_MAC(cursor.getString(1));
                friendInfo.setAES_KEY(cursor.getBlob(2));
            } while (cursor.moveToNext());
        }
        DB_r.close();
        return friendInfo;
    }

    public Cursor RetrieveFriends() {

        String QUERY_READ_TABLE = "SELECT * FROM " + DB_TABLE_NAME;

        SQLiteDatabase DB_r = this.getReadableDatabase();

        Cursor cursor = DB_r.rawQuery(QUERY_READ_TABLE, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        DB_r.close();

        return cursor;
    }

    public MdlFriendListDbHandler CheckFriendship1() {

        MdlFriendListDbHandler friendInfo = new MdlFriendListDbHandler();

        String QUERY_READ_TABLE = "SELECT * FROM " + DB_TABLE_NAME;

        SQLiteDatabase DB_r = this.getReadableDatabase();

        Cursor cursor = DB_r.rawQuery(QUERY_READ_TABLE, null);

        if (cursor.moveToFirst()) {
            do {
                friendInfo.setUSER_MAC(cursor.getString(1));
                friendInfo.setAES_KEY(cursor.getBlob(2));
            } while (cursor.moveToNext());
        }
        DB_r.close();
        return friendInfo;
    }

}
