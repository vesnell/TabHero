package pl.tabhero.db;

import pl.tabhero.R;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DBAdapter {
    public static final String KEY_ROWID = "id";
    public static final String KEY_PERFORMER = "performer";
    public static final String KEY_TITLE = "title";
    public static final String KEY_TAB = "tab";
    public static final String KEY_URL = "url";
    private static final String TAG = "DBAdapter";

    private static final String DATABASE_NAME = "TabHeroDB";
    private static final String DATABASE_TABLE = "tabhero";
    private static final int DATABASE_VERSION = 2;

    private static final String DATABASE_CREATE =
            "create table if not exists tabhero (id integer primary key autoincrement, "
                    + "performer VARCHAR not null, title VARCHAR not null, tab VARCHAR, url VARCHAR );";

    private final Context context;
    private static Context contextStatic;

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    @SuppressWarnings("static-access")
    public DBAdapter(Context ctx) {
        this.context = ctx;
        this.contextStatic = ctx;
        dbHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(DATABASE_CREATE);
            } catch (SQLException e) {
                Toast.makeText(contextStatic.getApplicationContext(),
                        R.string.createDatabaseError, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
            //      + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS contacts");
            onCreate(db);
        }
    }

    //---opens the database---
    public DBAdapter open() {
        db = dbHelper.getWritableDatabase();
        return this;
    }

    //---closes the database---
    public void close() {
        dbHelper.close();
    }

    //---insert a record into the database---
    public long insertRecord(String performer, String title, String tab, String url) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_PERFORMER, performer);
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_TAB, tab);
        initialValues.put(KEY_URL, url);
        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    //---deletes a particular record---
    public boolean deleteContact(long rowId) {
        return db.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public boolean deleteTab(String songUrl) {
        return db.delete(DATABASE_TABLE, KEY_URL + "='" + songUrl + "'", null) > 0;
    }

    public boolean deletePerf(String perfName) {
        return db.delete(DATABASE_TABLE, KEY_PERFORMER + "='" + perfName + "'", null) > 0;
    }

    //---retrieves all the records---
    public Cursor getAllRecords() {
        return db.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_PERFORMER, KEY_TITLE,
                KEY_TAB, KEY_URL}, null, null, null, null, null);
    }

    //---retrieves a particular record---
    public Cursor getRecord(long rowId) {
        Cursor mCursor =
                db.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                        KEY_PERFORMER, KEY_TITLE, KEY_TAB, KEY_URL},
                        KEY_ROWID + "=" + rowId, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Cursor getRecordPerf(String rowPerf) {
        Cursor mCursor =
                db.query(DATABASE_TABLE, new String[] {KEY_ROWID,
                        KEY_PERFORMER, KEY_TITLE, KEY_TAB, KEY_URL},
                        KEY_PERFORMER + "=?", new String[] {rowPerf }, null, null, null, null);
        if ((mCursor != null) && (mCursor.getCount() > 0)) {
            mCursor.moveToFirst();
        } else if (mCursor != null) {
            mCursor.close();
        }

        return mCursor;
    }

    public Cursor getRecordUrl(String rowUrl) {
        Cursor mCursor =
                db.query(DATABASE_TABLE, new String[] {KEY_ROWID,
                        KEY_PERFORMER, KEY_TITLE, KEY_TAB, KEY_URL},
                        KEY_URL + "=?", new String[] {rowUrl }, null, null, null, null);
        if ((mCursor != null) && (mCursor.getCount() > 0)) {
            mCursor.moveToFirst();
        } else if (mCursor != null) {
            mCursor.close();
        }
        return mCursor;
    }

    //---updates a record---
    public boolean updateRecord(long rowId, String performer, String title, String tab, String url) {
        ContentValues args = new ContentValues();
        args.put(KEY_PERFORMER, performer);
        args.put(KEY_TITLE, title);
        args.put(KEY_TAB, tab);
        args.put(KEY_URL, url);
        return db.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public boolean updateTablature(String tab, long rowId) {
        ContentValues args = new ContentValues();
        args.put(KEY_TAB, tab);
        return db.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public boolean updatePerfName(String newPerfName, long rowId) {
        ContentValues args = new ContentValues();
        args.put(KEY_PERFORMER, newPerfName);
        return db.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public boolean updateSongTitle(String newSongTitle, long rowId) {
        ContentValues args = new ContentValues();
        args.put(KEY_TITLE, newSongTitle);
        return db.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public boolean updateSongUrl(String newSongUrl, long rowId) {
        ContentValues args = new ContentValues();
        args.put(KEY_URL, newSongUrl);
        return db.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
}
