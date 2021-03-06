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
    public static final String KEY_VIEWDATE = "date";
    public static final String KEY_VIEWTYPE = "type";

    private static final String DATABASE_NAME = "TabHeroDB";
    private static final String DATABASE_TABLE = "tabhero";
    private static final String DATABASE_TABLE2 = "lastview";
    public static final int DATABASE_VERSION = 3;

    private static final String DATABASE_CREATE =
            "create table if not exists tabhero (id integer primary key autoincrement, "
                    + "performer VARCHAR not null, title VARCHAR not null, tab VARCHAR, url VARCHAR );";
    private static final String DATABASE_CREATE2 =
            "create table if not exists lastview (id integer primary key autoincrement, "
                    + "performer VARCHAR not null, title VARCHAR not null, tab VARCHAR, url VARCHAR, " 
                    + "date VARCHAR not null, type VARCHAR not null );";

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
                db.execSQL(DATABASE_CREATE2);
            } catch (SQLException e) {
                Toast.makeText(contextStatic.getApplicationContext(),
                        R.string.createDatabaseError, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
            //      + newVersion + ", which will destroy all old data");
            if (oldVersion == 2 && newVersion == 3) {
                db.execSQL(DATABASE_CREATE2);
            } else {
                db.execSQL("DROP TABLE IF EXISTS tabhero");
                db.execSQL("DROP TABLE IF EXISTS lastview");
                onCreate(db);
            }
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
    
    public long insertRecord2(String performer, String title, String tab, String url, String date, String type) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_PERFORMER, performer);
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_TAB, tab);
        initialValues.put(KEY_URL, url);
        initialValues.put(KEY_VIEWDATE, date);
        initialValues.put(KEY_VIEWTYPE, type);
        return db.insert(DATABASE_TABLE2, null, initialValues);
    }

    //---deletes a particular record---
    public boolean deleteRecord(long rowId) {
        return db.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
    public boolean deleteRecord2(long rowId) {
        return db.delete(DATABASE_TABLE2, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public boolean deleteTab(String songUrl) {
        return db.delete(DATABASE_TABLE, KEY_URL + "='" + songUrl + "'", null) > 0;
    }
    
    public boolean deleteTab2(String songUrl) {
        return db.delete(DATABASE_TABLE2, KEY_URL + "='" + songUrl + "'", null) > 0;
    }

    public boolean deletePerf(String perfName) {
        return db.delete(DATABASE_TABLE, KEY_PERFORMER + "='" + perfName + "'", null) > 0;
    }
    
    public boolean deleteOldestRecord(String oldestDate) {
        return db.delete(DATABASE_TABLE2, KEY_VIEWDATE + "='" + oldestDate + "'", null) > 0;
    }

    //---retrieves all the records---
    public Cursor getAllRecords() {
        return db.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_PERFORMER, KEY_TITLE,
                KEY_TAB, KEY_URL}, null, null, null, null, null);
    }
    
    public Cursor getAllRecords2() {
        return db.query(DATABASE_TABLE2, new String[] {KEY_ROWID, KEY_PERFORMER, KEY_TITLE,
                KEY_TAB, KEY_URL, KEY_VIEWDATE, KEY_VIEWTYPE}, null, null, null, null, null);
    }

    public int getCount() {
        Cursor cursor = db.rawQuery("SELECT * FROM lastview", null);
        return cursor.getCount();
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
    
    public Cursor getRecordUrl2(String rowUrl) {
        Cursor mCursor =
                db.query(DATABASE_TABLE2, new String[] {KEY_ROWID,
                        KEY_PERFORMER, KEY_TITLE, KEY_TAB, KEY_URL, KEY_VIEWDATE, KEY_VIEWTYPE},
                        KEY_URL + "=?", new String[] {rowUrl }, null, null, null, null);
        if ((mCursor != null) && (mCursor.getCount() > 0)) {
            mCursor.moveToFirst();
        } else if (mCursor != null) {
            mCursor.close();
        }
        return mCursor;
    }
    
    public Cursor getRecord2(long rowId) {
        Cursor mCursor =
                db.query(true, DATABASE_TABLE2, new String[] {KEY_ROWID,
                        KEY_PERFORMER, KEY_TITLE, KEY_TAB, KEY_URL, KEY_VIEWDATE, KEY_VIEWTYPE},
                        KEY_ROWID + "=" + rowId, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
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
    
    public boolean updateTablature2(String tab, long rowId) {
        ContentValues args = new ContentValues();
        args.put(KEY_TAB, tab);
        return db.update(DATABASE_TABLE2, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public boolean updatePerfName(String newPerfName, long rowId) {
        ContentValues args = new ContentValues();
        args.put(KEY_PERFORMER, newPerfName);
        return db.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
    public boolean updatePerfName2(String newPerfName, long rowId) {
        ContentValues args = new ContentValues();
        args.put(KEY_PERFORMER, newPerfName);
        return db.update(DATABASE_TABLE2, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public boolean updateSongTitle(String newSongTitle, long rowId) {
        ContentValues args = new ContentValues();
        args.put(KEY_TITLE, newSongTitle);
        return db.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
    public boolean updateSongTitle2(String newSongTitle, long rowId) {
        ContentValues args = new ContentValues();
        args.put(KEY_TITLE, newSongTitle);
        return db.update(DATABASE_TABLE2, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public boolean updateSongUrl(String newSongUrl, long rowId) {
        ContentValues args = new ContentValues();
        args.put(KEY_URL, newSongUrl);
        return db.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
    public boolean updateDate(String newDate, long rowId) {
        ContentValues args = new ContentValues();
        args.put(KEY_VIEWDATE, newDate);
        return db.update(DATABASE_TABLE2, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
}
