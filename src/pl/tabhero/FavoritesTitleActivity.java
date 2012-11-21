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
import android.widget.TextView;

public class FavoritesTitleActivity extends Activity {
	
	DBAdapter db = new DBAdapter(this);
	
	private TextView chosenFavPerf;
	private EditText editFavTitle;
	private ListView searchFavTitleListView;
	private ArrayAdapter<String> listAdapter;
	private List<String> listOfFavTitle;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favoritestitle);
        
        chosenFavPerf = (TextView) findViewById(R.id.chosenFavPerformer);
        editFavTitle = (EditText) findViewById(R.id.editFavTitle);
        searchFavTitleListView = (ListView) findViewById(R.id.searchFavTitleListView);
        
        Intent i = getIntent();
        Bundle extras = i.getExtras();
        String performerName = extras.getString("performerName");
        chosenFavPerf.setText(performerName);
        
        listOfFavTitle = addTitleFromBase(performerName);
        
        listAdapter = new ArrayAdapter<String>(this, R.layout.artists, listOfFavTitle);
        searchFavTitleListView.setAdapter(listAdapter);
        hideKeyboard();
        searchFavTitleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	Intent i = new Intent(FavoritesTitleActivity.this, SearchTitleActivity.class);
            	Bundle bun = new Bundle();
            	bun.putString("songTab", listOfFavTitle.get(position));
    			i.putExtras(bun);
    			startActivity(i);	
           }
        } );
        
        
	}
	
	public void searchTitleView(View v) {
		
	}
	
	public List<String> addTitleFromBase(String performerName) {
    	List<String> list = new ArrayList<String>();
    	db.open();
        Cursor c = db.getRecordPerf(performerName);
    	//Cursor c = db.getAllRecords();
        if (c.moveToFirst())
        {
            do {
            	//if(performerName.equals(c.getString(1))) {
            		list.add(c.getString(2));
            		Log.d("TITLE", c.getString(2));
            		Log.d("TITLE", c.getString(0));
            		Log.d("AAA","AAA");
            	//}
            } while (c.moveToNext());
        }
        db.close();
        return list;      
    }
	
	private void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editFavTitle.getWindowToken(), 0);
	}

}
