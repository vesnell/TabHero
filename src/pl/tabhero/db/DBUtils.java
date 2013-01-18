package pl.tabhero.db;

import pl.tabhero.R;
import android.content.Context;
import android.database.Cursor;
import android.widget.Toast;

public class DBUtils {
	
	private DBAdapter db;
	private Context context;
	
	public DBUtils(Context context) {
		this.context = context;
		this.db = new DBAdapter(this.context);
	}
	
	public boolean isIdExistByUrl(String songUrl) {
		String url;
		String idFromBase;
		String idFromNet;
		idFromNet = getIdFromUrl(songUrl);
		db.open();
		Cursor c = db.getAllRecords();
        if (c.moveToFirst())
        {
            do {
            	url = c.getString(4);
            	idFromBase = getIdFromUrl(url);
            	if(idFromNet.equals(idFromBase)) {
            		return true;
            	}
            } while (c.moveToNext());
        }
        db.close();
		return false;
	}
	
	public boolean isExistRecordByUrl(String songUrl) {
		db.open();
        Cursor c = db.getAllRecords();
        if (c.moveToFirst())
        {
            do {
            	if(songUrl.equals(c.getString(4))) {
            		return true;
            	}
            } while (c.moveToNext());
        }
        db.close();
		return false;
	}
	
	public void addTab(String performer, String title, String tab, String songUrl) {
		db.open();
		db.insertRecord(performer, title, tab, songUrl);
		db.close();
		Toast.makeText(this.context.getApplicationContext(), R.string.addToBaseSuccess, Toast.LENGTH_LONG).show();
	}
	
	public void deleteTab(String songUrl) {
		db.open();
		db.deleteTab(songUrl);
		Toast.makeText(this.context.getApplicationContext(), R.string.delFromBaseSuccess, Toast.LENGTH_LONG).show();
		db.close();
	}
	
	public long addId(String url) {
		long rowId = 0;
		this.db.open();
        Cursor c = this.db.getRecordUrl(url);
        if (c.moveToFirst())
        {
            do {
            	rowId = c.getLong(0);
            } while (c.moveToNext());
        }
        this.db.close();
		return rowId;
	}
	
	public String getTablature(String url) {
		String tabl = "";
		this.db.open();
		Cursor c = this.db.getRecordUrl(url);
		if(c.moveToFirst()) {
			do {
				tabl = c.getString(3);
			} while(c.moveToNext());
		}
		this.db.close();
		return tabl;
	}
	
	public void updateTablatureDBU(String songUrl, String tabFromFile) {
		long tabId = addId(songUrl);
		this.db.open();
		this.db.updateTablature(tabFromFile, tabId);
		this.db.close();
	}
	
	private String getIdFromUrl(String url) {
		String [] tab1;
		String[] tab2;
		tab1 = url.split("/");
    	tab2 = tab1[5].split(",");
    	String id = tab2[0];
		return id;
	}

}
