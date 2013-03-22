package pl.tabhero.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import pl.tabhero.R;
import pl.tabhero.core.ItemOfLastTen;
import pl.tabhero.utils.PolishComparator;
import pl.tabhero.utils.selector.ItemsOnCheckboxList;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

public class DBUtils {

    private DBAdapter db;
    private Context context;
    private static final int POINTER_ON_TITLE_ID = 5;
    private static final int ARRAY_URL_SIZE = 5;
    private static final int POINTER_ON_PERF = 4;
    private static final int POINTER_ON_ID = 5;
    private static final int URL_COLUMN = 4;
    private static final int TAB_COLUMN = 3;
    private static final int PERF_COLUMN = 1;
    private static final int TITLE_COLUMN = 2;
    private static final int VIEWDATE_COLUMN = 5;
    private static final int VIEWTYPE_COLUMN = 6;

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
        if (c.moveToFirst()) {
            do {
                url = c.getString(URL_COLUMN);
                idFromBase = getIdFromUrl(url);
                if (idFromNet.equals(idFromBase)) {
                    c.close();
                    db.close();
                    return true;
                }
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return false;
    }
    
    public boolean isExistRecordByUrl(String songUrl) {
        db.open();
        Cursor c = db.getAllRecords();
        if (c.moveToFirst()) {
            do {
                if (songUrl.equals(c.getString(URL_COLUMN))) {
                    c.close();
                    db.close();
                    return true;
                }
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return false;
    }
    
    public boolean isExistRecordByUrl2(String performer, String title, String tab, String url, String date, String type) {
        db.open();
        Cursor c = db.getAllRecords2();
         
        if (c.moveToFirst()) {
            do {
                if (url.equals(c.getString(URL_COLUMN)) && type.equals(c.getString(VIEWTYPE_COLUMN))) {
                    
                    //coś tu nie działa z updatem...
                    long tabId = getId2(url, type);
                    updateDate(date, tabId);
                    Log.d("LOGTYPE", type);
                    Log.d("DATE", date);
                    if (!tab.equals(c.getString(TAB_COLUMN))) {
                        updateTablature2(tab, tabId);
                    }
                    if (!performer.equals(c.getString(PERF_COLUMN))) {
                        updatePerfName2(performer, tabId);
                    }
                    if (!title.equals(c.getString(TITLE_COLUMN))) {
                        updateSongTitle2(title, tabId);
                    }
                    c.close();
                    db.close();
                    return true;
                }
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return false;
    }
    
    private void updateDate(String date, long tabId) {
        db.open();
        db.updateDate(date, tabId);
        db.close();
    }
    
    private void updateTablature2(String tab, long tabId) {
        db.open();
        db.updateTablature2(tab, tabId);
        db.close();
    }
    
    private void updatePerfName2(String performer, long tabId) {
        db.open();
        db.updatePerfName2(performer, tabId);
        db.close();
    }
    
    private void updateSongTitle2(String title, long tabId) {
        db.open();
        db.updateSongTitle2(title, tabId);
        db.close();
    }

    public void addTab(String performer, String title, String tab, String songUrl) {
        db.open();
        db.insertRecord(performer, title, tab, songUrl);
        db.close();
        Toast.makeText(this.context.getApplicationContext(),
                R.string.addToBaseSuccess, Toast.LENGTH_LONG).show();
    }
    
    public void addToLastTen(String performer, String title, String tab, String url, String date, String type) {
        db.open();
        db.insertRecord2(performer, title, tab, url, date, type);
        db.close();
    }

    public void deleteTab(String songUrl) {
        db.open();
        db.deleteTab(songUrl);
        Toast.makeText(this.context.getApplicationContext(),
                R.string.delFromBaseSuccess, Toast.LENGTH_LONG).show();
        db.close();
    }
    
    public void deleteRecordByDate(String date) {
        db.open();
        db.deleteOldestRecord(date);
        db.close();
    }

    public long getId(String url) {
        long rowId = 0;
        db.open();
        Cursor c = this.db.getRecordUrl(url);
        if (c.moveToFirst()) {
            do {
                rowId = c.getLong(0);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return rowId;
    }
    
    public long getId2(String url, String type) {
        long rowId = 0;
        db.open();
        Cursor c = this.db.getRecordUrl2(url);
        if (c.moveToFirst()) {
            do {
                if (c.getString(VIEWTYPE_COLUMN).equals(type)) {
                    rowId = c.getLong(0);
                }
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return rowId;
    }
    
    public String getType(String url, String date) {
        String type = "";
        db.open();
        Cursor c = this.db.getRecordUrl2(url);
        if (c.moveToFirst()) {
            do {
                if (c.getString(VIEWDATE_COLUMN).equals(date)) {
                    type = c.getString(VIEWTYPE_COLUMN);
                }
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return type;
    }

    public String getTablature(String url) {
        String tabl = "";
        db.open();
        Cursor c = this.db.getRecordUrl(url);
        if (c.moveToFirst()) {
            do {
                tabl = c.getString(TAB_COLUMN);
            } while(c.moveToNext());
        }
        c.close();
        db.close();
        return tabl;
    }
    
    public String getTablature2(String url, String type) {
        String tabl = "";
        db.open();
        Cursor c = this.db.getRecordUrl2(url);
        if (c.moveToFirst()) {
            do {
                if (c.getString(VIEWTYPE_COLUMN).equals(type)) {
                    tabl = c.getString(TAB_COLUMN);
                }
            } while(c.moveToNext());
        }
        c.close();
        db.close();
        return tabl;
    }
    
    public String getPerformer(String url) {
        String perf = "";
        db.open();
        Cursor c = this.db.getRecordUrl(url);
        if (c.moveToFirst()) {
            do {
                perf = c.getString(PERF_COLUMN);
            } while(c.moveToNext());
        }
        c.close();
        db.close();
        return perf;
    }
    
    public String getTitle(String url) {
        String title = "";
        db.open();
        Cursor c = this.db.getRecordUrl(url);
        if (c.moveToFirst()) {
            do {
                title = c.getString(TITLE_COLUMN);
            } while(c.moveToNext());
        }
        c.close();
        db.close();
        return title;
    }

    public void updateTablatureDBU(String songUrl, String tabFromFile) {
        long tabId = getId(songUrl);
        this.db.open();
        this.db.updateTablature(tabFromFile, tabId);
        this.db.close();
    }

    private String getIdFromUrl(String url) {
        String [] tab1;
        String[] tab2;
        tab1 = url.split("/");
        tab2 = tab1[POINTER_ON_TITLE_ID].split(",");
        String id = tab2[0];
        return id;
    }

    public ArrayList<String> addPerfFromBase() {
        Comparator<String> comparator = new PolishComparator();
        ArrayList<String> list = new ArrayList<String>();
        db.open();
        Cursor c = db.getAllRecords();
        if (c.moveToFirst()) {
            do {
                if (!list.contains(c.getString(1))) {
                    list.add(c.getString(1));
                }
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        Collections.sort(list, comparator);
        return list;
    }
    
    public ArrayList<ItemOfLastTen> getLastTenItems() {
        ArrayList<ItemOfLastTen> list = new ArrayList<ItemOfLastTen>();
        db.open();
        Cursor c = db.getAllRecords2();
        if (c.moveToFirst()) {
            do {
                String perf = c.getString(PERF_COLUMN);
                String title = c.getString(TITLE_COLUMN);
                String tab = c.getString(TAB_COLUMN);
                String url = c.getString(URL_COLUMN);
                String date = c.getString(VIEWDATE_COLUMN);
                String type = c.getString(VIEWTYPE_COLUMN);
                ItemOfLastTen item = new ItemOfLastTen(perf, title, tab, url, date, type);
                list.add(item);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return list;
    }
    
    public ArrayList<String> getLastTenPerfs() {
        ArrayList<String> list = new ArrayList<String>();
        db.open();
        Cursor c = db.getAllRecords2();
        if (c.moveToFirst()) {
            do {
                list.add(c.getString(1));
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return list;
    }
    
    public ArrayList<String> getLastTenTitles() {
        ArrayList<String> list = new ArrayList<String>();
        db.open();
        Cursor c = db.getAllRecords2();
        if (c.moveToFirst()) {
            do {
                list.add(c.getString(2));
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return list;
    }
    
    public ArrayList<String> getLastTenUrls() {
        ArrayList<String> list = new ArrayList<String>();
        db.open();
        Cursor c = db.getAllRecords2();
        if (c.moveToFirst()) {
            do {
                list.add(c.getString(4));
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return list;
    }
    
    public ArrayList<String> getLastTenDates() {
        ArrayList<String> list = new ArrayList<String>();
        db.open();
        Cursor c = db.getAllRecords2();
        if (c.moveToFirst()) {
            do {
                list.add(c.getString(5));
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return list;
    }
    
    public ArrayList<String> getLastTenViewTypes() {
        ArrayList<String> list = new ArrayList<String>();
        db.open();
        Cursor c = db.getAllRecords2();
        if (c.moveToFirst()) {
            do {
                list.add(c.getString(6));
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return list;
    }
    
    public int getCount() {
        db.open();
        int length = db.getCount();
        db.close();
        return length;
    }

    public ArrayList<ArrayList<String>> addTitleFromBase(String perfName) {
        Comparator<String> comparator = new PolishComparator();
        Map<String, String> mapTitlesUrls = new TreeMap<String, String>(comparator);
        db.open();
        Cursor c = db.getRecordPerf(perfName);
        if (c.moveToFirst()) {
            do {
                mapTitlesUrls.put(c.getString(2), c.getString(URL_COLUMN));
            } while (c.moveToNext());
        }
        c.close();
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
        if (!(isExistRecord(songUrl))) {
            db.insertRecord(newPerfName, newSongTitle, "", songUrl);
            Toast.makeText(this.context.getApplicationContext(),
                    R.string.addToBaseSuccess, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this.context.getApplicationContext(),
                    R.string.recordExist, Toast.LENGTH_LONG).show();
        }
        db.close();
    }

    private boolean isExistRecord(String songUrl) {
        Cursor c = db.getAllRecords();
        if (c.moveToFirst()) {
            do {
                if (songUrl.equals(c.getString(URL_COLUMN))) {
                    c.close();
                    return true;
                }
            } while (c.moveToNext());
        }
        c.close();
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
        if (c.moveToFirst()) {
            do {
                listId.add(c.getLong(0));
                listUrl.add(c.getString(URL_COLUMN));
            } while (c.moveToNext());
        }
        c.close();
        for (long id : listId) {
            db.updatePerfName(newPerfName, id);
            splitTab = listUrl.get(i).split("/");
            splitTab[POINTER_ON_PERF] = newPerfName;
            for (int j = 0; j < ARRAY_URL_SIZE; j++) {
                newSongUrl += splitTab[j] + "/";
            }
            newSongUrl += splitTab[POINTER_ON_ID];
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
        if (c.moveToFirst()) {
            do {
                listId.add(c.getLong(0));
                listUrl.add(c.getString(URL_COLUMN));
            } while (c.moveToNext());
        }
        c.close();
        for (long id : listId) {
            db.updateSongTitle(newSongTitle, id);
            splitTab = listUrl.get(i).split("/");
            splitTab2 = splitTab[POINTER_ON_PERF].split(",");
            splitTab2[1] = newSongTitle;
            splitTab[POINTER_ON_ID] = splitTab2[0] + "," + splitTab2[1];
            for (int j = 0; j < ARRAY_URL_SIZE; j++) {
                newSongUrl += splitTab[j] + "/";
            }
            newSongUrl += splitTab[POINTER_ON_ID];
            db.updateSongUrl(newSongUrl, id);
            i++;
            newSongUrl = "";
        }
        db.close();
    }

    public void deletePerfsInEdit(ArrayList<ItemsOnCheckboxList> performersCheckList) {
        db.open();
        for (ItemsOnCheckboxList perf : performersCheckList) {
            if (perf.isChecked()) {
                db.deletePerf(perf.getName());
            }
        }
        db.close();
    }

    public void deleteTitlesInEdit(ArrayList<ItemsOnCheckboxList> titleCheckList, ArrayList<String> titleList, ArrayList<String> urlList) {
        db.open();
        for (ItemsOnCheckboxList title : titleCheckList) {
            if (title.isChecked()) {
                for (int i = 0; i < titleList.size(); i++) {
                    if (title.getName().equals(titleList.get(i))) {
                        db.deleteTab(urlList.get(i));
                    }
                }
            }
        }
        db.close();
    }
}
