package pl.tabhero;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {
    public static final String KEY_ROWID = "id";
    public static final String KEY_PERFORMER = "performer";
    public static final String KEY_TITLE = "title";
    //public static final String KEY_RATE = "rate";
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

    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public DBAdapter(Context ctx) 
    {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }
        
    private static class DatabaseHelper extends SQLiteOpenHelper 
    {
        DatabaseHelper(Context context) 
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) 
        {
        	try {
        		db.execSQL(DATABASE_CREATE);	
        	} catch (SQLException e) {
        		e.printStackTrace();
        	}
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
        {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS contacts");
            onCreate(db);
        }
    }    

    //---opens the database---
    public DBAdapter open() throws SQLException 
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //---closes the database---    
    public void close() 
    {
        DBHelper.close();
    }
    
    //---insert a record into the database---
    public long insertRecord(String performer, String title, String tab, String url) 
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_PERFORMER, performer);
        initialValues.put(KEY_TITLE, title);
        //initialValues.put(KEY_RATE, rate);
        initialValues.put(KEY_TAB, tab);
        initialValues.put(KEY_URL, url);
        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    //---deletes a particular record---
    public boolean deleteContact(long rowId) 
    {
        return db.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
    public boolean deleteTab(String songUrl) 
    {
        return db.delete(DATABASE_TABLE, KEY_URL + "='" + songUrl + "'", null) > 0;
    }

    //---retrieves all the records---
    public Cursor getAllRecords() 
    {
        return db.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_PERFORMER, KEY_TITLE, 
        		KEY_TAB, KEY_URL}, null, null, null, null, null);
    }

    //---retrieves a particular record---
    public Cursor getRecord(long rowId) throws SQLException 
    {
        Cursor mCursor =
                db.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                KEY_PERFORMER, KEY_TITLE, KEY_TAB, KEY_URL}, 
                KEY_ROWID + "=" + rowId, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    
    public Cursor getRecordPerf(String rowPerf) throws SQLException 
    {
        Cursor mCursor =
                db.query(DATABASE_TABLE, new String[] {KEY_ROWID,
                KEY_PERFORMER, KEY_TITLE, KEY_TAB, KEY_URL}, 
                KEY_PERFORMER + "=?", new String[] { rowPerf }, null, null, null, null);
        if ((mCursor != null) && (mCursor.getCount() > 0)) {
            mCursor.moveToFirst();
        } 
        else if (mCursor != null) {
            mCursor.close();
        }
        
        return mCursor;
    }

    //---updates a record---
    public boolean updateRecord(long rowId, String performer, String title, String tab, String url) 
    {
        ContentValues args = new ContentValues();
        args.put(KEY_PERFORMER, performer);
        args.put(KEY_TITLE, title);
        //args.put(KEY_RATE, rate);
        args.put(KEY_TAB, tab);
        args.put(KEY_URL, url);
        return db.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
}
