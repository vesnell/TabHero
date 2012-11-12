package pl.tabhero;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
 
public class SearchActivity extends Activity {
	
	private ListView searchListView;
	private EditText editPerformer;
	private ArrayAdapter<String> listAdapter;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
    }
    
    public void searchView(View v) throws IOException {
		String performer = new String();
		editPerformer = (EditText) findViewById(R.id.editPerformer);
		performer = editPerformer.getText().toString().toLowerCase();
		searchListView = (ListView) findViewById(R.id.searchListView);
		hideKeyboard();
		//setVisibilityOf(searchListView, true);
		//setVisibilityOf(searchListView2, false);
		
		if(!(performer.length() > 0)) 
			Toast.makeText(getApplicationContext(), "Musisz wpisać wykonawcę!", Toast.LENGTH_LONG).show();
		else {
			List<String[]> artists = findPerf(performer);
			final ArrayList<String> artistNames = new ArrayList<String>();
			final ArrayList<String> artistUrl = new ArrayList<String>();
			for(String[] art : artists) {
				//Log.d("ART1", art[1]);
				artistNames.add(art[1]);
				//Log.d("ART0", art[0]);
				artistUrl.add(art[0]);
			}
			
			listAdapter = new ArrayAdapter<String>(this, R.layout.artists, artistNames);
			searchListView.setAdapter(listAdapter);
			
			searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	            	//Toast.makeText(getApplicationContext(), artistUrl.get(position), Toast.LENGTH_SHORT).show();
	            	Intent i = new Intent(SearchActivity.this, SearchTitleActivity.class);
	            	Bundle bun = new Bundle();
	    			bun.putString("performerName", artistNames.get(position));
	    			bun.putString("performerUrl", artistUrl.get(position));
	    			i.putExtras(bun);
	    			startActivity(i);	
	            }
	        } );
		}
    }
    
    /*private String capitalize(final String string) {
       if (string == null)
          throw new NullPointerException("string");
       if (string.equals(""))
          throw new NullPointerException("string");

       return Character.toUpperCase(string.charAt(0)) + string.substring(1);
    }*/
    
    private List<String[]> findPerf(String performer) throws IOException {
    	String url = "http://www.chords.pl/wykonawcy/";
    	List<String[]> chosenPerformers = new ArrayList<String[]>();
    	Document doc = null;
    	if(Character.isDigit(performer.charAt(0))) {
    		doc = Jsoup.connect(url + "1").get();
    	}
    	else {
    		doc = Jsoup.connect(url + performer).get();
    	}
    	String codeFind0 = doc.select("tr.v0").toString();
    	String codeFind1 = doc.select("tr.v1").toString();
    	String codeFind = codeFind0 + codeFind1;
    	Document docFind = Jsoup.parse(codeFind);
    	Elements performers = docFind.select("a[href]");
    	String[][] array = new String[performers.size()][2];
    	boolean checkContains;
    	for(int i = 0; i < performers.size(); i++) {
    		array[i][0] = performers.get(i).attr("href");
    		array[i][1] = performers.get(i).toString();
    		array[i][1] = Jsoup.parse(array[i][1]).select("a").first().ownText();
    		array[i][1] = array[i][1].replace("\\", "");
    		String p = array[i][1].toLowerCase();
    		checkContains = p.contains(performer);
    		if(checkContains == true) {
    			//Log.d("0000000", array[i][0]);
    			//Log.d("1111111", array[i][1]);
    			chosenPerformers.add(array[i]);
    		}
    	}
    	return chosenPerformers;
    }
    
    private void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editPerformer.getWindowToken(), 0);
	}
    
    /*private void setVisibilityOf(View v, boolean visible) {
		int visibility = visible ? View.VISIBLE : View.GONE;
		v.setVisibility(visibility);
	}*/
}