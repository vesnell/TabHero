package pl.tabhero;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import pl.tabhero.SearchActivity.connect;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
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
	List<String[]> songs = new ArrayList<String[]>();
	ArrayList<String> songTitle = new ArrayList<String>();
	ArrayList<String> songUrl = new ArrayList<String>();
	ProgressDialog progressDialog;
	ProgressDialog progressDialogTab;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchtitle);
        
        Intent i = getIntent();
		Bundle extras = i.getExtras();
		final String performerName = extras.getString("performerName");
        TextView chosenPerformer = (TextView) findViewById(R.id.chosenPerformer);
        chosenPerformer.setText(performerName);
    }
	
	@SuppressWarnings("unchecked")
	public void searchTitleView(View v) {
		
		songs.clear();
		songTitle.clear();
		songUrl.clear();
		
		String title = new String();
		editTitle = (EditText) findViewById(R.id.editTitle);
		title = editTitle.getText().toString().toLowerCase();
		searchListView = (ListView) findViewById(R.id.searchTitleListView);
		hideKeyboard();
		Intent i = getIntent();
		Bundle extras = i.getExtras();
		//String performerName = extras.getString("performerName");
		String performerUrl = extras.getString("performerUrl");

		ArrayList<String> passing = new ArrayList<String>();
		passing.add(performerUrl);
		passing.add(title);
		if(!(checkInternetConnection()))
			Toast.makeText(getApplicationContext(), R.string.connectionError, Toast.LENGTH_LONG).show();
		else 
			new connect().execute(passing);
	}
	
	public class connect extends AsyncTask<ArrayList<String>, List<String[]>, Void>{
		
		@Override
   	 	protected void onPreExecute() {
			setProgressBarIndeterminateVisibility(true);
			progressDialog = ProgressDialog.show(SearchTitleActivity.this, getString(R.string.srchSong), getString(R.string.wait));
   	 	}
   	
		@Override
		protected void onPostExecute(Void result) {
			setProgressBarIndeterminateVisibility(false);
   			progressDialog.dismiss();
   	 	}

		@SuppressWarnings("unchecked")
		@Override
		protected Void doInBackground(ArrayList<String>... params) {
			ArrayList<String> passing = params[0];
			String urlPerformerSongs = passing.get(0);
			String title = passing.get(1);
			Log.d("urlPerformerSongs", urlPerformerSongs);
			Log.d("title", title);
			String url = "http://www.chords.pl";
	    	List<String[]> chosenTitles = new ArrayList<String[]>();
	    	Document doc = null;
			try {
				doc = Jsoup.connect(url + urlPerformerSongs).get();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	String codeSongs = doc.select("table.piosenki").toString();
	    	Document songs = Jsoup.parse(codeSongs);
	    	Elements chosenLineSong = songs.select("a[href]");
	    	String[][] array = new String[chosenLineSong.size()][2];
	    	boolean checkContains;
	    	for(int i = 0; i < chosenLineSong.size(); i++) {
	    		array[i][0] = chosenLineSong.get(i).attr("href");
	    		array[i][0] = url + array[i][0];
	    		array[i][1] = chosenLineSong.get(i).toString();
	    		array[i][1] = Jsoup.parse(array[i][1]).select("a").first().ownText();
	    		array[i][1] = array[i][1].replace("\\", "");
	    		String p = array[i][1].toLowerCase();
	    		checkContains = p.contains(title);
	    		if(checkContains == true) {
	    			chosenTitles.add(array[i]);
	    		}
	    	}
	    	publishProgress(chosenTitles);
			return null;
		}
		
		@Override
	    protected void onProgressUpdate(List<String[]>... ct) {
			songs = ct[0];
			
			for(String[] sng : songs) {
				Log.d("ART1", sng[1]);
				songTitle.add(sng[1]);
				Log.d("ART0", sng[0]);
				songUrl.add(sng[0]);
			}
			
			listAdapter = new ArrayAdapter<String>(SearchTitleActivity.this, R.layout.artists, songTitle);
			searchListView.setAdapter(listAdapter);
			Log.d("AAAAA", "AAAAA");
			searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	            @SuppressWarnings("unchecked")
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	            	//Toast.makeText(getApplicationContext(), songUrl.get(position), Toast.LENGTH_SHORT).show();
	            	
	            		String posUrl = songUrl.get(position);
	            		String posTitle = songTitle.get(position);
	            		ArrayList<String> passing = new ArrayList<String>();
	            		passing.add(posUrl);
	            		passing.add(posTitle);
	            		if(!(checkInternetConnection()))
	            			Toast.makeText(getApplicationContext(), R.string.connectionError, Toast.LENGTH_LONG).show();
	            		else
	            			new getTablature().execute(passing);      		
	            }
			} );
		}
		
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
    
	public class getTablature extends AsyncTask<ArrayList<String>, ArrayList<String>, Void>{
		
		@Override
   	 	protected void onPreExecute() {
			setProgressBarIndeterminateVisibility(true);
			progressDialogTab = ProgressDialog.show(SearchTitleActivity.this, getString(R.string.srchTab), getString(R.string.wait));
   	 	}
   	
		@Override
		protected void onPostExecute(Void result) {
			setProgressBarIndeterminateVisibility(false);
   			progressDialogTab.dismiss();
   	 	}

		@SuppressWarnings("unchecked")
		@Override
		protected Void doInBackground(ArrayList<String>... params) {
			ArrayList<String> passing = params[0];
			String url = passing.get(0);
			String title = passing.get(1);
			
			// TODO Auto-generated method stub
			Document doc = null;
			try {
				doc = Jsoup.connect(url).get();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	Element elements = doc.select("pre").first();
	    	String tab = elements.text();
	    	String[] table = tab.split("\n");
	    	tab = "";
	    	for (int i = 3; i < table.length; i++)
	    		tab += table[i] + "\n";
	    	ArrayList<String> passing2 = new ArrayList<String>();
	    	passing2.add(tab);
	    	passing2.add(title);
	    	passing2.add(url);
	    	publishProgress(passing2);
			return null;
		}
		
		@Override
	    protected void onProgressUpdate(ArrayList<String>... params) {
			ArrayList<String> passing = params[0];
			String tablature = passing.get(0);
			String songTitle = passing.get(1);
			String songUrl = passing.get(2);
			Intent i = getIntent();
			Bundle extras = i.getExtras();
			final String performerName = extras.getString("performerName");
			Intent intent = new Intent(SearchTitleActivity.this, TabViewActivity.class);
			Bundle bun = new Bundle();
			bun.putString("performerName", performerName);
			bun.putString("songTitle", songTitle);
			bun.putString("songUrl", songUrl);
			bun.putString("tab", tablature);
			intent.putExtras(bun);
			startActivity(intent);
		}
		
	}
	
	public boolean checkInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) SearchTitleActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable() && cm.getActiveNetworkInfo().isConnected()) {
            return true;

        } else {
            return false;
        }
    }
	
    /*private String getTablature(String url) throws IOException {
    	Document doc = Jsoup.connect(url).get();
    	Element elements = doc.select("pre").first();
    	String tab = elements.text();
    	String[] table = tab.split("\n");
    	tab = "";
    	for (int i = 3; i < table.length; i++)
    		tab += table[i] + "\n";
    	return tab;
    }*/
	
}