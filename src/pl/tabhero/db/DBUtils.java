package pl.tabhero.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import pl.tabhero.R;
import pl.tabhero.utils.PolishComparator;
import pl.tabhero.utils.selector.ItemsOnCheckboxList;
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
	
	public ArrayList<String> addPerfFromBase() {
    	Comparator<String> comparator = new PolishComparator();
    	ArrayList<String> list = new ArrayList<String>();
    	db.open();
        Cursor c = db.getAllRecords();
        if (c.moveToFirst())
        {
            do {
            	if(!list.contains(c.getString(1)))
            		list.add(c.getString(1));
            } while (c.moveToNext());
        }
        db.close();
        Collections.sort(list, comparator);
        return list;      
    }
	
	public ArrayList<ArrayList<String>> addTitleFromBase(String perfName) {
		Comparator<String> comparator = new PolishComparator();
    	Map<String, String> mapTitlesUrls = new TreeMap<String, String>(comparator);
    	db.open();
        Cursor c = db.getRecordPerf(perfName);
        if (c.moveToFirst())
        {
            do {
            	mapTitlesUrls.put(c.getString(2), c.getString(4));
            } while (c.moveToNext());
        }
        db.close();
        ArrayList<String> listTitles = new ArrayList<String>(mapTitlesUrls.keySet());
		ArrayList<String> listUrl = new ArrayList<String>(mapTitlesUrls.values());
    	ArrayList<ArrayList<String>> listOfList = new ArrayList<ArrayList<String>>();
        listOfList.add(listTitles);
        listOfList.add(listUrl);
        return listOfList;      
    }
	
	public void addToBaseNewRecord(String newPerfName, String newSongTitle) {
    	String songUrl = "http://www.chords.pl/chwyty/" + newPerfName + "/" + "USER," + newSongTitle;
    	db.open();
		if(isExistRecord(songUrl) == false) {
			db.insertRecord(newPerfName, newSongTitle, "", songUrl);
			Toast.makeText(this.context.getApplicationContext(), R.string.addToBaseSuccess, Toast.LENGTH_LONG).show();
		} else
			Toast.makeText(this.context.getApplicationContext(), R.string.recordExist, Toast.LENGTH_LONG).show();
		db.close();
    }
	
	private boolean isExistRecord(String songUrl) {
        Cursor c = db.getAllRecords();
        if (c.moveToFirst())
        {
            do {
            	if(songUrl.equals(c.getString(4))) {
            		return true;
            	}
            } while (c.moveToNext());
        }
		return false;
	}
	
	public void changePerfName(String newPerfName, String oldPerfName) {
    	ArrayList<Long> listId = new ArrayList<Long>();
    	ArrayList<String> listUrl = new ArrayList<String>();
    	String[] splitTab;
    	String newSongUrl = "";
    	int i = 0;
    	db.open();
        Cursor c = db.getRecordPerf(oldPerfName);
        if (c.moveToFirst())
        {
            do {
            	listId.add(c.getLong(0));
            	listUrl.add(c.getString(4));
            } while (c.moveToNext());
        }
        for(long id : listId) {
        	db.updatePerfName(newPerfName, id);
        	splitTab = listUrl.get(i).split("/");
        	splitTab[4] = newPerfName;
        	for(int j = 0; j < 5; j++)
        		newSongUrl += splitTab[j] + "/";
        	newSongUrl += splitTab[5];
        	db.updateSongUrl(newSongUrl, id);
        	i++;
        	newSongUrl = "";
        }
		db.close();
    }
	
	public void changeSongTitle(String newSongTitle, String url) {
    	ArrayList<Long> listId = new ArrayList<Long>();
    	ArrayList<String> listUrl = new ArrayList<String>();
    	String[] splitTab;
    	String[] splitTab2;
    	String newSongUrl = "";
    	int i = 0;
    	db.open();
        Cursor c = db.getRecordUrl(url);
        if (c.moveToFirst())
        {
            do {
            	listId.add(c.getLong(0));
            	listUrl.add(c.getString(4));
            } while (c.moveToNext());
        }
        for(long id : listId) {
        	db.updateSongTitle(newSongTitle, id);
        	splitTab = listUrl.get(i).split("/");
        	splitTab2 = splitTab[5].split(",");
        	splitTab2[1] = newSongTitle;
        	splitTab[5] = splitTab2[0] + "," + splitTab2[1];
        	for(int j = 0; j < 5; j++)
        		newSongUrl += splitTab[j] + "/";
        	newSongUrl += splitTab[5];
        	db.updateSongUrl(newSongUrl, id);
        	i++;
        	newSongUrl = "";
        }
		db.close();
    }
	
	public void deletePerfsInEdit(ArrayList<ItemsOnCheckboxList> performersCheckList) {
		db.open();
    	for(ItemsOnCheckboxList perf : performersCheckList) {
    		if(perf.isChecked() == true) {	
    			db.deletePerf(perf.getName());
    		}
    	}
    	db.close();
	}
	
	public void deleteTitlesInEdit(ArrayList<ItemsOnCheckboxList> titleCheckList, ArrayList<String> titleList, ArrayList<String> urlList) {
		db.open();
    	for(ItemsOnCheckboxList title : titleCheckList) {
    		if(title.isChecked() == true) {	
    			for(int i = 0; i < titleList.size(); i++) {
    				if(title.getName().equals(titleList.get(i))) {
    					db.deleteTab(urlList.get(i));
    				}
    			}
    		}
    	}
    	db.close();
	}
}
