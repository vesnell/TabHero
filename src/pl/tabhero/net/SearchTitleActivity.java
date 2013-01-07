package pl.tabhero.net;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import pl.tabhero.R;
import pl.tabhero.TabHero;
import pl.tabhero.R.anim;
import pl.tabhero.R.drawable;
import pl.tabhero.R.id;
import pl.tabhero.R.layout;
import pl.tabhero.R.menu;
import pl.tabhero.R.string;
import pl.tabhero.utils.PolishComparator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SearchTitleActivity extends Activity {
	
	private ListView searchListView;
	private EditText editTitle;
	private ImageButton btnTitleSearch;
	private ArrayAdapter<String> listAdapter;
	private ProgressDialog progressDialog;
	private static boolean MAX;
	private static final int MENUWIFI = Menu.FIRST;
	private boolean isWebsiteAvailable;
	private String chordsUrl = "http://www.chords.pl";
	
	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private GestureDetector gestureDetector;
	View.OnTouchListener gestureListener;
	
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchtitle);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            getActionBar().setHomeButtonEnabled(true);
        }
        
        Intent i = getIntent();
		Bundle extras = i.getExtras();
		
		MAX = extras.getBoolean("max");
		if(MAX) {
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
		
		final String performerName = extras.getString("performerName");
        TextView chosenPerformer = (TextView) findViewById(R.id.chosenPerformer);
        chosenPerformer.setText(performerName);
        
        editTitle = (EditText) findViewById(R.id.editTitle);
		btnTitleSearch = (ImageButton) findViewById(R.id.searchTitleBtn);
		searchListView = (ListView) findViewById(R.id.searchTitleListView);
		
		editTitle.setFilters(new InputFilter[]{filter});
        editTitle.setOnEditorActionListener(new TextView.OnEditorActionListener() {
		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
		                actionId == EditorInfo.IME_ACTION_DONE ||
		                event.getAction() == KeyEvent.ACTION_DOWN &&
		                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
		            btnTitleSearch.performClick();
		            return true;
		        }
		        return false;
		    }
		});
        
        gestureDetector = new GestureDetector(new MyGestureDetector());
		gestureListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (gestureDetector.onTouchEvent(event)) {
					return true;
				}
				return false;
			}
		};
        
        searchListView.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
        });
    }
	
	InputFilter filter = new InputFilter() { 
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) { 
        	for (int i = start; i < end; i++) { 
        		if (!(Character.isLetterOrDigit(source.charAt(i)) || source.charAt(i) == ' ' || source.charAt(i) == '.')) { 
                    return ""; 
        		}
            } 
            return null; 
        }
    }; 
	
	class MyGestureDetector extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
				return false;
				//right to left swipe
			if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {					Toast.makeText(getApplicationContext(), R.string.chooseTitle, Toast.LENGTH_LONG).show();
			} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				onBackPressed();
				//onClickStartActivity(SearchActivity.class);
			}
			return false;
		}
	}
	
	@SuppressWarnings("unchecked")
	public void searchTitleView(View v) {
		String title = new String();
		title = editTitle.getText().toString().toLowerCase();
		hideKeyboard();
		Intent i = getIntent();
		Bundle extras = i.getExtras();
		String performerUrl = extras.getString("performerUrl");

		ArrayList<String> passing = new ArrayList<String>();
		passing.add(performerUrl);
		passing.add(title);
		if(!(checkInternetConnection()))
			Toast.makeText(getApplicationContext(), R.string.connectionError, Toast.LENGTH_LONG).show();
		else 
			new checkConnectTitle().execute(passing);
	}
	
	public class checkConnectTitle extends AsyncTask<ArrayList<String>, Void, ArrayList<String>>{
   	
    	@SuppressWarnings("unchecked")
		@Override
   	 	protected void onPostExecute(ArrayList<String> passing) {
    		if(isWebsiteAvailable) {
    			new connect().execute(passing);
    		} else {
    			Toast.makeText(getApplicationContext(), R.string.websiteConnectionError, Toast.LENGTH_LONG).show();
    		}
    	}

		@Override
		protected ArrayList<String> doInBackground(ArrayList<String>... params) {
			ArrayList<String> passing = params[0];
			if(isConnected()) {
				isWebsiteAvailable = true;
			} else {
				isWebsiteAvailable = false;
			}
			return passing;
		}
    }
	
	public class checkConnectTab extends AsyncTask<ArrayList<String>, Void, ArrayList<String>>{
	   	
    	@SuppressWarnings("unchecked")
		@Override
   	 	protected void onPostExecute(ArrayList<String> passing) {
    		if(isWebsiteAvailable) {
    			new getTablature().execute(passing);
    		} else {
    			Toast.makeText(getApplicationContext(), R.string.websiteConnectionError, Toast.LENGTH_LONG).show();
    		}
    	}

		@Override
		protected ArrayList<String> doInBackground(ArrayList<String>... params) {
			ArrayList<String> passing = params[0];
			if(isConnected()) {
				isWebsiteAvailable = true;
			} else {
				isWebsiteAvailable = false;
			}
			return passing;
		}
    }
    
    public boolean isConnected() {
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                URL url = new URL(chordsUrl);
                HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(2000);
                urlc.connect();
                if (urlc.getResponseCode() == 200) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }
	
	public class connect extends AsyncTask<ArrayList<String>, Void, Map<String, String>> {
		
		@Override
   	 	protected void onPreExecute() {
			startProgressBar(getString(R.string.srchSong));
		}
   	
		@Override
		protected void onPostExecute(Map<String, String> chosenTitles) {
			final List<String> songTitle = new ArrayList<String>(chosenTitles.keySet());
    		final List<String> songUrl = new ArrayList<String>(chosenTitles.values());
			listAdapter = new ArrayAdapter<String>(SearchTitleActivity.this, R.layout.titlesnet, songTitle);
			searchListView.setAdapter(listAdapter);
			closeProgressBar();
			searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	            @SuppressWarnings("unchecked")
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	            	String posUrl = songUrl.get(position);
	            	String posTitle = songTitle.get(position);
	            	ArrayList<String> passing = new ArrayList<String>();
	            	passing.add(posUrl);
	            	passing.add(posTitle);
	            	if(!(checkInternetConnection()))
	            		Toast.makeText(getApplicationContext(), R.string.connectionError, Toast.LENGTH_LONG).show();
	            	else
	            		new checkConnectTab().execute(passing);      		
	            }
			} );
   	 	}

		@Override
		protected Map<String, String> doInBackground(ArrayList<String>... params) {
			ArrayList<String> passing = params[0];
			String urlPerformerSongs = passing.get(0);
			String title = passing.get(1);
			String url = chordsUrl;
			Comparator<String> comparator = new PolishComparator();
	    	Map<String, String> chosenTitles = new TreeMap<String, String>(comparator);
	    	Document doc = connectUrl(url + urlPerformerSongs);
	    	String codeSongs = doc.select("table.piosenki").toString();
	    	Document songs = Jsoup.parse(codeSongs);
	    	Elements chosenLineSong = songs.select("a[href]");
	    	
	    	for(Element el : chosenLineSong) {
	    		String localUrl = el.attr("href");
	    		localUrl = url + localUrl;
	    		String localTitle = Jsoup.parse(el.toString()).select("a").first().ownText().replace("\\", "");
	    		if(localTitle.toLowerCase().contains(title) == true) {
	    			chosenTitles.put(localTitle, localUrl);
	    		}
	    	}
	    	return chosenTitles;
		}
	}
    
	public class getTablature extends AsyncTask<ArrayList<String>, Void, ArrayList<String>>{
		
		@Override
   	 	protected void onPreExecute() {
			startProgressBar(getString(R.string.srchTab));
   	 	}
   	
		@Override
		protected void onPostExecute(ArrayList<String> passing) {
			closeProgressBar();
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
			bun.putBoolean("max", MAX);
			intent.putExtras(bun);
			startActivityForResult(intent, 500);
			overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
   	 	}

		@Override
		protected ArrayList<String> doInBackground(ArrayList<String>... params) {
			ArrayList<String> passing = params[0];
			String url = passing.get(0);
			String title = passing.get(1);
			Document doc = connectUrl(url);
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
			return passing2;
		}
	}
	
	private Document connectUrl(String url) {
		Document doc = null;
		try {
			doc = Jsoup.connect(url).get();
		}  catch (MalformedURLException ep) {
			Toast.makeText(getApplicationContext(), R.string.errorInInternetConnection, Toast.LENGTH_LONG).show();
		} catch (IOException e) {
			Toast.makeText(getApplicationContext(), R.string.errorInInternetConnection, Toast.LENGTH_LONG).show();
		}
		return doc;
	}
	
	private void startProgressBar(String title) {
		setProgressBarIndeterminateVisibility(true);
		progressDialog = ProgressDialog.show(SearchTitleActivity.this, title, getString(R.string.wait));
	}
	
	private void startProgressBarWifi() {
    	setProgressBarIndeterminateVisibility(true);
        progressDialog = ProgressDialog.show(SearchTitleActivity.this, "", getString(R.string.wait));
    }
	
	private void closeProgressBar() {
		setProgressBarIndeterminateVisibility(false);
		progressDialog.dismiss();
	}
	
	private void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editTitle.getWindowToken(), 0);
	}
	
	public boolean checkInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) SearchTitleActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable() && cm.getActiveNetworkInfo().isConnected()) {
            return true;
        } else {
            return false;
        }
    }
	
	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.searchart, menu);
	    return true;
	}*/
	
	@SuppressLint("NewApi")
	@Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	menu.clear();
    	WifiManager wifi=(WifiManager)getSystemService(Context.WIFI_SERVICE);
    	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
    		if(wifi.isWifiEnabled()) {
    			menu.add(0, MENUWIFI, 0, "").setIcon(R.drawable.wifi_on).setShowAsAction(MENUWIFI);
    		} else {
    			menu.add(0, MENUWIFI, 0, "").setIcon(R.drawable.wifi_ic).setShowAsAction(MENUWIFI);
    		}
    	} else {
    		if(wifi.isWifiEnabled()) {
    			menu.add(0, MENUWIFI, 0, R.string.wifiOn).setIcon(R.drawable.wifi_on);
    		} else {
    			menu.add(0, MENUWIFI, 0, R.string.wifiOff).setIcon(R.drawable.wifi_ic);
    		}
    	}
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.searchart, menu);
    	return super.onPrepareOptionsMenu(menu);
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		closeOptionsMenu();
	    switch (item.getItemId()) {
	    case android.R.id.home:
	    	Intent intent = new Intent(this, TabHero.class);
	    	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    	startActivity(intent);
	    	overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
	    	return true;
	    case MENUWIFI:
	    	new connectWifi().execute();
	    	return true;
	    case R.id.minmax:
	    	minMax();
	    	return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	private void wifiMechanise() {
    	WifiManager wifi=(WifiManager)getSystemService(Context.WIFI_SERVICE);
    	if(wifi.isWifiEnabled()) {
    		try {
    			wifi.setWifiEnabled(false);
    			timer(false);
    		} catch(Exception e) {
    			Log.d("WIFI", e.getMessage());
    			Toast.makeText(getApplicationContext(), R.string.wifiFalseError, Toast.LENGTH_LONG).show();
    		}
    	} else {
    		try {
    			wifi.setWifiEnabled(true);
    			timer(true);
    		} catch(Exception e) {
    			Log.d("WIFI", e.getMessage());
    			Toast.makeText(getApplicationContext(), R.string.wifiTrueError, Toast.LENGTH_LONG).show();
    		}
    	}
    }
    
 public class connectWifi extends AsyncTask<Void, Void, Void>{
    	
    	@Override
    	 protected void onPreExecute() {
    		startProgressBarWifi();
    	 }
    	
    	@Override
   	 	protected void onPostExecute(Void result) {
    		closeProgressBar();
    		openOptionsMenu();
    	}

		@Override
		protected Void doInBackground(Void... params) {
			wifiMechanise();
			return null;
		}
 }
    
    private void timer(final boolean bool) {
    	long start = System.currentTimeMillis();
		long end = 0;
		do {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			end = System.currentTimeMillis();
		} while((checkInternetConnection() != bool) && (end  - start < 15000));
    }
	
	private void minMax() {
    	boolean fullScreen = (getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) != 0;
       if(fullScreen) {
    	   getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    	   getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
    	   MAX = false;
        }
        else {
        	getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        	MAX = true;
        }
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
    }
}