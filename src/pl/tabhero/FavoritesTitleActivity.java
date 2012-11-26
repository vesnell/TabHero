package pl.tabhero;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FavoritesTitleActivity extends Activity {
	
	DBAdapter db = new DBAdapter(this);
	
	private TextView chosenFavPerf;
	private EditText editFavTitle;
	private ListView searchFavTitleListView;
	private ArrayAdapter<String> listAdapter;
	private ArrayList<String> listOfFavTitle;
	private ArrayList<String> listOfChosenTitleFromBase;
	private ArrayList<String> listOfChosenTabFromBase;
	private ArrayList<String> listOfChosenUrlFromBase;
	private ArrayList<String> listOfChosenTitleFromBase2;
	private ArrayList<String> listOfChosenUrlFromBase2;
	String performerName;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favoritestitle);
        
        chosenFavPerf = (TextView) findViewById(R.id.chosenFavPerformer);
        editFavTitle = (EditText) findViewById(R.id.editFavTitle);
        searchFavTitleListView = (ListView) findViewById(R.id.searchFavTitleListView);
        
        Intent i = getIntent();
        Bundle extras = i.getExtras();
        performerName = extras.getString("performerName");
        chosenFavPerf.setText(performerName);
        
        ArrayList<ArrayList<String>> listOfLists = addTitleFromBase(performerName);
        ArrayList<String> listTitle = listOfLists.get(0);
        ArrayList<String> listTab = listOfLists.get(1);
        ArrayList<String> listUrl = listOfLists.get(2);
        listOfChosenTitleFromBase = listTitle;
        listOfChosenTabFromBase = listTab;
        listOfChosenUrlFromBase = listUrl;
        
        Log.d("SIZE2", Integer.toString(listOfLists.get(0).size()));
        
        listAdapter = new ArrayAdapter<String>(this, R.layout.artists, listOfChosenTitleFromBase);
        searchFavTitleListView.setAdapter(listAdapter);
        hideKeyboard();
        searchFavTitleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	Intent i = new Intent(FavoritesTitleActivity.this, FavTabViewActivity.class);
            	Bundle bun = new Bundle();
            	bun.putString("performerName", performerName);
            	bun.putString("songTitle", listOfChosenTitleFromBase.get(position));
            	bun.putString("songTab", listOfChosenTabFromBase.get(position));
            	bun.putString("songUrl", listOfChosenUrlFromBase.get(position));
    			i.putExtras(bun);
    			startActivity(i);	
           }
        } );
        
        
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.faveeditcheckbox, menu);
	    return true;
	}
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.delFromFavWithCheckBox:
	        startEditActivity();
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
    
    private void startEditActivity() {
    	//Log.d("1111", "1111");
    	ArrayList<String> listToEditTitles = new ArrayList<String>();
    	ArrayList<String> listToEditUrl = new ArrayList<String>();
    	//if(listOfChosenPerfsFromBase.size() > 0) {
    		//Log.d("2222", "2222");
    	if(listOfChosenTitleFromBase2.size() == 0) {
    		listToEditTitles = listOfChosenTitleFromBase;
    		listToEditUrl = listOfChosenUrlFromBase;
    	} else {
    		listToEditTitles = listOfChosenTitleFromBase2;
    		listToEditUrl = listOfChosenUrlFromBase2;
    	}
    	//} else {
    		//Log.d("3333", "3333;");
    		//listToEdit = listOfFavPerfs;
    	//}
    	Intent i = new Intent(FavoritesTitleActivity.this, EditFavTitles.class);
		Bundle bun = new Bundle();
		//Log.d("4444", "4444");
		bun.putStringArrayList("listOfTitles", listToEditTitles);
		bun.putStringArrayList("listOfUrl", listToEditUrl);
		i.putExtras(bun);
		//Log.d("5555", "5555");
		startActivity(i);	
    }
	
	public void searchTitleView(View v) {
		hideKeyboard();
		//listOfFavTitle.clear();
		//listOfChosenTitleFromBase.clear();
		//ArrayList<ArrayList<String>> listOfLists = addTitleFromBase(performerName);
        //ArrayList<String> listTitle = listOfLists.get(0);
        //final ArrayList<String> listTab = listOfLists.get(1);
        //final ArrayList<String> listUrl = listOfLists.get(2);
        //listOfChosenTitleFromBase = listTitle;
		
		ArrayList<ArrayList<String>> listOfLists = addTitleFromBase(performerName);
        ArrayList<String> listTitle = listOfLists.get(0);
        ArrayList<String> listTab = listOfLists.get(1);
        ArrayList<String> listUrl = listOfLists.get(2);
        listOfChosenTitleFromBase = listTitle;
        listOfChosenTabFromBase = listTab;
        listOfChosenUrlFromBase = listUrl;
        
		String title = new String();
		listOfChosenTitleFromBase2 = new ArrayList<String>(); //listOfChosenTitleFromBase;
		final ArrayList<String> listOfChosenTabFromBase2 = new ArrayList<String>(); //listOfChosenTabFromBase;
		listOfChosenUrlFromBase2 = new ArrayList<String>(); //listOfChosenUrlFromBase;
		//listOfChosenTitleFromBase2.clear();
		//listOfChosenTabFromBase2.clear();
		//listOfChosenUrlFromBase2.clear();
		//listOfChosenUrlFromBase.clear();
    	title = editFavTitle.getText().toString().toLowerCase();
    	Log.d("TITLE1", title);
    	if(title.length() > 0) {
    		if(title.charAt(0) == ' ')
    			Toast.makeText(getApplicationContext(), R.string.hintSpace, Toast.LENGTH_LONG).show();
    		else {
    			boolean checkContains;
    			//listOfChosenTitleFromBase.clear();
    			Log.d("111", "111");
    			Log.d("SIZE", Integer.toString(listOfChosenTitleFromBase.size()));
    			//for(String p : listOfFavTitle) {
    			for(int i = 0; i < listOfChosenTitleFromBase.size(); i++) {
    				//checkContains = p.toLowerCase().contains(title);
    				Log.d("222", "222");
    				Log.d("TITLE", title);
    				Log.d("DO LISTY", listOfChosenTitleFromBase.get(i));
    				checkContains = listOfChosenTitleFromBase.get(i).toLowerCase().contains(title);
    				if(checkContains == true) {
    					//listOfChosenTitleFromBase.add(p);
    					Log.d("333", "333");
    					listOfChosenTitleFromBase2.add(listOfChosenTitleFromBase.get(i));
    					listOfChosenTabFromBase2.add(listOfChosenTabFromBase.get(i));
    					listOfChosenUrlFromBase2.add(listOfChosenUrlFromBase.get(i));
    				}
    			}
    			Log.d("444", "444");
    			//listOfFavTitle.clear();
    			listAdapter = new ArrayAdapter<String>(this, R.layout.artists, listOfChosenTitleFromBase2);
    			searchFavTitleListView.setAdapter(listAdapter);
    			searchFavTitleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    					Intent i = new Intent(FavoritesTitleActivity.this, FavTabViewActivity.class);
    					Bundle bun = new Bundle();
    					bun.putString("performerName", performerName);
    					bun.putString("songTitle", listOfChosenTitleFromBase2.get(position));
    					bun.putString("songTab", listOfChosenTabFromBase2.get(position));
    					bun.putString("songUrl", listOfChosenUrlFromBase2.get(position));
    					i.putExtras(bun);
    					startActivity(i);	
    				}
    			} );
    		}
    		
    	} else {
    		//listOfChosenUrlFromBase = listUrl;
    		listAdapter = new ArrayAdapter<String>(this, R.layout.artists, listOfChosenTitleFromBase);
			searchFavTitleListView.setAdapter(listAdapter);
			searchFavTitleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Intent i = new Intent(FavoritesTitleActivity.this, FavTabViewActivity.class);
					Bundle bun = new Bundle();
					bun.putString("performerName", performerName);
					bun.putString("songTitle", listOfChosenTitleFromBase.get(position));
					bun.putString("songTab", listOfChosenTabFromBase.get(position));
					bun.putString("songUrl", listOfChosenUrlFromBase.get(position));
					i.putExtras(bun);
					startActivity(i);	
				}
			} );
    	}
		
	}
	
	public ArrayList<ArrayList<String>> addTitleFromBase(String perfName) {
    	ArrayList<String> listTitles = new ArrayList<String>();
    	ArrayList<String> listTabs = new ArrayList<String>();
    	ArrayList<String> listUrl = new ArrayList<String>();
    	ArrayList<ArrayList<String>> listOfList = new ArrayList<ArrayList<String>>();
    	db.open();
        Cursor c = db.getRecordPerf(perfName);
    	//Cursor c = db.getAllRecords();
        if (c.moveToFirst())
        {
            do {
            	//if(performerName.equals(c.getString(1))) {
            		listTitles.add(c.getString(2));
            		listTabs.add(c.getString(3));
            		listUrl.add(c.getString(4));
            		//Log.d("TITLE", c.getString(2));
            		//Log.d("TITLE", c.getString(0));
            		//Log.d("AAA","AAA");
            	//}
            } while (c.moveToNext());
        }
        db.close();
        listOfList.add(listTitles);
        listOfList.add(listTabs);
        listOfList.add(listUrl);
        return listOfList;      
    }
	
	private void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editFavTitle.getWindowToken(), 0);
	}
	
	//protected void onPause() {
	    // TODO Auto-generated method stub
	//    super.onPause();
	//    finish();
	//}
	
	protected void onResume() {
		super.onResume();
		Button btn = (Button) findViewById(R.id.searchFavTitleBtn);
		btn.performClick();
	}
	
	@Override
    public void onBackPressed() {
    	Intent intent = new Intent(this, FavoritesActivity.class);
    	//finish();
    	startActivity(intent);
    }

}
