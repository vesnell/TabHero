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
        
        Log.d("ZESPOL Z BAZY", listOfFavPerfs.get(0));
        listAdapter = new ArrayAdapter<String>(this, R.layout.artists, listOfFavPerfs);
        searchListView.setAdapter(listAdapter);
        hideKeyboard();
        searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	//Intent i = new Intent(SearchActivity.this, SearchTitleActivity.class);
            	//Bundle bun = new Bundle();
            	//bun.putString("performerName", artistNames.get(position));
    			//bun.putString("performerUrl", artistUrl.get(position));
    			//i.putExtras(bun);
    			//startActivity(i);	
           }
        } );
        
    }
    
    public void searchView(View v) {
    	
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
    	
        //Toast.makeText(this, 
         //       "id: " + c.getString(0) + "\n" +
          //      "Title: " + c.getString(1) + "\n" +
           //     "Due Date:  " + c.getString(2),
            //    Toast.LENGTH_SHORT).show();        
    } 
    
    private void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editFavPerformer.getWindowToken(), 0);
	}
}