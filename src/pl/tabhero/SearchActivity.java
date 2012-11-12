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
import android.widget.Toast;
 
public class SearchActivity extends Activity {
	
	private ListView searchListView;
	private EditText editPerformer;
	//private ListView searchListView2;
	private ArrayAdapter<String> listAdapter;
	//private ArrayAdapter<String> listAdapter2;

	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
    }
    
    public void searchView(View v) throws IOException {
    	//String url = "http://www.chords.pl";
		String performer = new String();
		//final String title = new String();
		editPerformer = (EditText) findViewById(R.id.editPerformer);
		performer = editPerformer.getText().toString().toLowerCase();
		//final String title = ((EditText) findViewById(R.id.editTitle)).getText().toString().toLowerCase();
		searchListView = (ListView) findViewById(R.id.searchListView);
		//searchListView2 = (ListView) findViewById(R.id.searchListView2);
		hideKeyboard();
		//setVisibilityOf(searchListView, true);
		//setVisibilityOf(searchListView2, false);
		
		if(!(performer.length() > 0)) 
			Toast.makeText(getApplicationContext(), "Musisz wpisać wykonawcę!", Toast.LENGTH_LONG).show();
		else {
			List<String[]> artists = findPerf(performer);
			//List<List<String[]>> listOflists = removeEmptyTitleArtists(artists, title);
			//artists = listOflists.get(0);
			//final List<String[]> titles = listOflists.get(1);// <-------- tu już powinno też również zwracać listę utworów
			final ArrayList<String> artistNames = new ArrayList<String>();
			final ArrayList<String> artistUrl = new ArrayList<String>();
			for(String[] art : artists) {
				Log.d("ART1", art[1]);
				artistNames.add(WordUtils.capitalize(art[1]));
				Log.d("ART0", art[0]);
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
	    			//bun.putString("title", songTitles.get(position));
	    			bun.putString("performerUrl", artistUrl.get(position));
	    			i.putExtras(bun);
	    			startActivity(i);
	            	//Object listItem = searchListView.getItemAtPosition(position);
	            	/*try {
	            		//Log.d("11111", "11111");
						List<String[]> titles = findTitle(artistUrl.get(position), title);
						//Log.d("22222", titles.get(0)[1]);
						final String perfName = artistNames.get(position);
						final ArrayList<String> songTitles = new ArrayList<String>();
						final ArrayList<String> songUrl = new ArrayList<String>();
						for(String[] song : titles) {
							songTitles.add(capitalize(song[1]));
							//Log.d("SONGI", capitalize(song[1]));
							songUrl.add(song[0]);
						}*/
						//setVisibilityOf(searchListView, false);
						//setVisibilityOf(searchListView2, true);
						//Log.d("AA przed AA", "adapter2");
						/*listAdapter2 = new ArrayAdapter<String>(SearchActivity.this, R.layout.artists, songTitles);
						//Log.d("AA za AA", "adapter2");
						searchListView2.setAdapter(listAdapter2);
						searchListView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				            	//Toast.makeText(getApplicationContext(), songUrl.get(position), Toast.LENGTH_SHORT).show();
								try {            	
									String tablature = getTablature(songUrl.get(position));
									Intent i = new Intent(SearchActivity.this, TabViewActivity.class);
					            	Bundle bun = new Bundle();
					    			bun.putString("performer", perfName);
					    			bun.putString("title", songTitles.get(position));
					    			bun.putString("tab", tablature);
					    			i.putExtras(bun);
					    			startActivity(i);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
				            }
						} );*/
					/*} catch (IOException e) {
						// TODO Auto-generated catch block
						//Log.d("DUPA", "DUPAAAA");
						e.printStackTrace();
					}*/
	            	//Toast.makeText(getApplicationContext(), artistUrl.get(position), Toast.LENGTH_SHORT).show();
	            	//Toast.makeText(getApplicationContext(), ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
	            }
	        } );
			//String tablature = "";
			/*for(int i = 0; i < artists.size(); i++) {
				Log.d("SIZE", Integer.toString(artists.size()));
				Log.d("22222222", artists.get(i)[0]);
				Log.d("33333333", artists.get(i)[1]);
			
				//List<String[]> songs = findTitle(p, t);
				//getTablature(t);
			}*/
			//Intent i = new Intent(SearchActivity.this, TabViewActivity.class);
			//Bundle bun = new Bundle();
			//bun.putString("performer", performer);
			//bun.putString("title", title);
			//bun.putString("tab", tablature);
			//i.putExtras(bun);
			//startActivity(i);
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
    		array[i][1] = performers.get(i).toString().toLowerCase();
    		array[i][1] = Jsoup.parse(array[i][1]).select("a").first().ownText();
    		array[i][1] = array[i][1].replace("\\", "");
    		String e = array[i][1];
    		checkContains = e.contains(performer);
    		if(checkContains == true) {
    			Log.d("0000000", array[i][0]);
    			Log.d("1111111", array[i][1]);
    			chosenPerformers.add(array[i]);
    		}
    	}
    	return chosenPerformers;
    }
    
    private void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editPerformer.getWindowToken(), 0);
	}
    
    /*private List<List<String[]>> removeEmptyTitleArtists(List<String[]> artists, String title) throws IOException {
    	List<String[]> titlesOfCheckedArtists = new ArrayList<String[]>();
		for(int i = 0; i < artists.size(); i++) {
			List<String[]> matchedTitles = findTitle(artists.get(i)[0], title);
			if(matchedTitles.size() == 0) {
				artists.remove(i);
				i--;
			}
			else {
				for(String[] a : matchedTitles)
					titlesOfCheckedArtists.add(a);
			}
		}
		List<List<String[]>> artsAndTitles = new ArrayList<List<String[]>>();
		artsAndTitles.add(artists); 
		artsAndTitles.add(titlesOfCheckedArtists);
		return artsAndTitles;
    	//return artists;
    }*/
    
    /*private List<String[]> findTitle(String urlPerformerSongs, String title) throws IOException {
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
    		String e = array[i][1];
    		checkContains = e.contains(title);
    		if(checkContains == true) {
    			chosenTitles.add(array[i]);
    		}
    	}
    	return chosenTitles;
    }
    
    private String getTablature(String url) throws IOException {
    	Document doc = Jsoup.connect(url).get();
    	Element elements = doc.select("pre").first();
    	String tab = elements.text();
    	String[] table = tab.split("\n");
    	tab = "";
    	for (int i = 3; i < table.length; i++)
    		tab += table[i] + "\n";
    	return tab;
    }*/
    
    /*private void setVisibilityOf(View v, boolean visible) {
		int visibility = visible ? View.VISIBLE : View.GONE;
		v.setVisibility(visibility);
	}*/
}