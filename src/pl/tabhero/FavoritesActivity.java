package pl.tabhero;
 
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
 
public class FavoritesActivity extends Activity {
	
	DBAdapter db = new DBAdapter(this); 
	
	private ListView searchListView;
	private EditText editFavPerformer;
	private List<String> listOfFavPerfs;
	private ArrayAdapter<String> listAdapter;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorites);
        
        editFavPerformer = (EditText) findViewById(R.id.editFavPerformer);
        searchListView = (ListView) findViewById(R.id.searchFavListView);
        
        
        listOfFavPerfs = addPerfFromBase();

        listAdapter = new ArrayAdapter<String>(this, R.layout.artists, listOfFavPerfs);
        searchListView.setAdapter(listAdapter);
        hideKeyboard();
        searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	Intent i = new Intent(FavoritesActivity.this, FavoritesTitleActivity.class);
            	Bundle bun = new Bundle();
            	bun.putString("performerName", listOfFavPerfs.get(position));
    			i.putExtras(bun);
    			startActivity(i);	
           }
        } );
        
    }
    
    public void searchView(View v) {
    	hideKeyboard();
    	listOfFavPerfs.clear();
    	listOfFavPerfs = addPerfFromBase();
    	String performer = new String();
    	final List<String> listOfChosenPerfsFromBase = new ArrayList<String>();
    	Log.d("1111","1111");
    	performer = editFavPerformer.getText().toString().toLowerCase();
    	Log.d("2222", "2222");
    	if(performer.length() > 0) {
    		if(performer.charAt(0) == ' ')
    			Toast.makeText(getApplicationContext(), R.string.hintSpace, Toast.LENGTH_LONG).show();
    		else {
    			boolean checkContains;
    			listOfChosenPerfsFromBase.clear();
    			for(String p : listOfFavPerfs) {
    				checkContains = p.toLowerCase().contains(performer);
    				if(checkContains == true)
    					listOfChosenPerfsFromBase.add(p);
    			}
    			listOfFavPerfs.clear();
    			listAdapter = new ArrayAdapter<String>(this, R.layout.artists, listOfChosenPerfsFromBase);
    			searchListView.setAdapter(listAdapter);
    			searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    					Intent i = new Intent(FavoritesActivity.this, FavoritesTitleActivity.class);
    					Bundle bun = new Bundle();
    					bun.putString("performerName", listOfChosenPerfsFromBase.get(position));
    					i.putExtras(bun);
    					startActivity(i);	
    				}
    			} );
    		}
    	} else {
			listAdapter = new ArrayAdapter<String>(this, R.layout.artists, listOfFavPerfs);
			searchListView.setAdapter(listAdapter);
			searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Intent i = new Intent(FavoritesActivity.this, FavoritesTitleActivity.class);
					Bundle bun = new Bundle();
					bun.putString("performerName", listOfFavPerfs.get(position));
					i.putExtras(bun);
					startActivity(i);	
				}
			} );
    	}
    }
    
    public List<String> addPerfFromBase() {
    	List<String> list = new ArrayList<String>();
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
        return list;      
    } 
    
    private void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editFavPerformer.getWindowToken(), 0);
	}
}