package pl.tabhero;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.text.WordUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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
import android.widget.TextView;
import android.widget.Toast;

public class SearchTitleActivity extends Activity {
	
	private ListView searchListView;
	private EditText editTitle;
	private ArrayAdapter<String> listAdapter;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchtitle);
        
        Intent i = getIntent();
		Bundle extras = i.getExtras();
		final String performerName = extras.getString("performerName");
        TextView chosenPerformer = (TextView) findViewById(R.id.chosenPerformer);
        chosenPerformer.setText(performerName);
    }
	
	public void searchTitleView(View v) throws IOException {
		String title = new String();
		editTitle = (EditText) findViewById(R.id.editTitle);
		title = editTitle.getText().toString().toLowerCase();
		searchListView = (ListView) findViewById(R.id.searchTitleListView);
		hideKeyboard();
		Intent i = getIntent();
		Bundle extras = i.getExtras();
		final String performerName = extras.getString("performerName");
		String performerUrl = extras.getString("performerUrl");
		
		
		List<String[]> songs = findTitle(performerUrl, title);
		final ArrayList<String> songTitle = new ArrayList<String>();
		final ArrayList<String> songUrl = new ArrayList<String>();
		
		for(String[] sng : songs) {
			Log.d("ART1", sng[1]);
			songTitle.add(WordUtils.capitalize(sng[1]));
			Log.d("ART0", sng[0]);
			songUrl.add(sng[0]);
		}
		
		listAdapter = new ArrayAdapter<String>(this, R.layout.artists, songTitle);
		searchListView.setAdapter(listAdapter);
		
		searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	//Toast.makeText(getApplicationContext(), songUrl.get(position), Toast.LENGTH_SHORT).show();
            	try {
					String tablature = getTablature(songUrl.get(position));
					Intent i = new Intent(SearchTitleActivity.this, TabViewActivity.class);
					Bundle bun = new Bundle();
					bun.putString("performerName", performerName);
					bun.putString("songTitle", songTitle.get(position));
					bun.putString("songUrl", songUrl.get(position));
					bun.putString("tab", tablature);
					i.putExtras(bun);
					startActivity(i);
            	} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
		} );
		
	}
	
	private List<String[]> findTitle(String urlPerformerSongs, String title) throws IOException {
    	String url = "http://www.chords.pl";
    	//Log.d("AAAAAA", urlPerformerSongs);
    	List<String[]> chosenTitles = new ArrayList<String[]>();
    	Document doc = Jsoup.connect(url + urlPerformerSongs).get();
    	String codeSongs = doc.select("table.piosenki").toString();
    	Document songs = Jsoup.parse(codeSongs);
    	Elements chosenLineSong = songs.select("a[href]");
    	String[][] array = new String[chosenLineSong.size()][2];
    	boolean checkContains;
    	for(int i = 0; i < chosenLineSong.size(); i++) {
    		array[i][0] = chosenLineSong.get(i).attr("href");
    		array[i][0] = url + array[i][0];
    		array[i][1] = chosenLineSong.get(i).toString().toLowerCase();
    		array[i][1] = Jsoup.parse(array[i][1]).select("a").first().ownText();
    		array[i][1] = array[i][1].replace("\\", "");
    		String e = array[i][1];
    		checkContains = e.contains(title);
    		if(checkContains == true) {
    			chosenTitles.add(array[i]);
    		}
    	}
    	return chosenTitles;
    }
	
	private void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editTitle.getWindowToken(), 0);
	}
	
	/*private String capitalize(final String string) {
	       if (string == null)
	          throw new NullPointerException("string");
	       if (string.equals(""))
	          throw new NullPointerException("string");

	       return Character.toUpperCase(string.charAt(0)) + string.substring(1);
	    }*/
    
    private String getTablature(String url) throws IOException {
    	Document doc = Jsoup.connect(url).get();
    	Element elements = doc.select("pre").first();
    	String tab = elements.text();
    	String[] table = tab.split("\n");
    	tab = "";
    	for (int i = 3; i < table.length; i++)
    		tab += table[i] + "\n";
    	return tab;
    }
	
}