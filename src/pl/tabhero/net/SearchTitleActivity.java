package pl.tabhero.net;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import pl.tabhero.R;
import pl.tabhero.TabHero;
import pl.tabhero.core.Songs;
import pl.tabhero.core.Tablature;
import pl.tabhero.utils.MyEditorKeyActions;
import pl.tabhero.utils.MyFilter;
import pl.tabhero.utils.MyGestureDetector;
import pl.tabhero.utils.MyTelephonyManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
	private GestureDetector gestureDetector;
	private MyTelephonyManager device = new MyTelephonyManager(this);
	
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
		
		InputFilter filter = new MyFilter();
		editTitle.setFilters(new InputFilter[]{filter});
		
		TextView.OnEditorActionListener myEditorKeyActions = new MyEditorKeyActions(btnTitleSearch);
		editTitle.setOnEditorActionListener(myEditorKeyActions);
        
        gestureDetector = new GestureDetector(new MyGestureDetector(this));
        
        searchListView.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
        });
    }
	
	public void searchTitleView(View v) {
		String typedTitle = editTitle.getText().toString().toLowerCase();
		hideKeyboard();
		
		Intent i = getIntent();
		Bundle extras = i.getExtras();
		String performerUrl = extras.getString("performerUrl");

		Songs song = new Songs(typedTitle, performerUrl);
		if(!(checkInternetConnection()))
			Toast.makeText(getApplicationContext(), R.string.connectionError, Toast.LENGTH_LONG).show();
		else 
			new checkConnectTitle().execute(song);
	}
	
	public class checkConnectTitle extends AsyncTask<Songs, Void, Songs>{
   	
		@Override
   	 	protected void onPostExecute(Songs song) {
    		if(isWebsiteAvailable) {
    			new connect().execute(song);
    		} else {
    			Toast.makeText(getApplicationContext(), R.string.websiteConnectionError, Toast.LENGTH_LONG).show();
    		}
    	}

		@Override
		protected Songs doInBackground(Songs... params) {
			Songs song = params[0];
			if(isConnected()) {
				isWebsiteAvailable = true;
			} else {
				isWebsiteAvailable = false;
			}
			return song;
		}
    }
	
	public class checkConnectTab extends AsyncTask<Tablature, Void, Tablature>{
	   	
		@Override
   	 	protected void onPostExecute(Tablature tablature) {
    		if(isWebsiteAvailable) {
    			new getTablature().execute(tablature);
    		} else {
    			Toast.makeText(getApplicationContext(), R.string.websiteConnectionError, Toast.LENGTH_LONG).show();
    		}
    	}

		@Override
		protected Tablature doInBackground(Tablature... params) {
			Tablature tablature = params[0];
			if(isConnected()) {
				isWebsiteAvailable = true;
			} else {
				isWebsiteAvailable = false;
			}
			return tablature;
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
	
	public class connect extends AsyncTask<Songs, Void, Songs> {
		
		@Override
   	 	protected void onPreExecute() {
			startProgressBar(getString(R.string.srchSong));
		}
   	
		@Override
		protected void onPostExecute(final Songs song) {
			song.setListOfTitles();
			song.setListOfUrls();
			listAdapter = new ArrayAdapter<String>(SearchTitleActivity.this, R.layout.titlesnet, song.listOfTitles);
			searchListView.setAdapter(listAdapter);
			closeProgressBar();
			searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	            	String posUrl = song.listOfSongUrls.get(position);
	            	String posTitle = song.listOfTitles.get(position);
	            	Tablature tablature = new Tablature(posTitle, posUrl);
	            	if(!(checkInternetConnection()))
	            		Toast.makeText(getApplicationContext(), R.string.connectionError, Toast.LENGTH_LONG).show();
	            	else
	            		new checkConnectTab().execute(tablature);      		
	            }
			} );
   	 	}

		@Override
		protected Songs doInBackground(Songs... params) {
			Songs song = params[0];
	    	Document doc = connectUrl(chordsUrl + song.performerUrl);
	    	song.setMapOfChosenTitles(doc);
	    	return song;
		}
	}
	
	public class getTablature extends AsyncTask<Tablature, Void, Tablature>{
		
		@Override
   	 	protected void onPreExecute() {
			startProgressBar(getString(R.string.srchTab));
   	 	}
   	
		@Override
		protected void onPostExecute(Tablature tablature) {
			closeProgressBar();
			Intent i = getIntent();
			Bundle extras = i.getExtras();
			final String performerName = extras.getString("performerName");
			Intent intent = new Intent(SearchTitleActivity.this, TabViewActivity.class);
			Bundle bun = new Bundle();
			bun.putString("performerName", performerName);
			bun.putString("songTitle", tablature.songTitle);
			bun.putString("songUrl", tablature.songUrl);
			bun.putString("tab", tablature.songTablature);
			bun.putBoolean("max", MAX);
			intent.putExtras(bun);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
   	 	}

		@Override
		protected Tablature doInBackground(Tablature... params) {
			Tablature tablature = params[0];
			tablature.setSongTablature(connectUrl(tablature.songUrl));
			return tablature;
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
	    if(!(device.isTablet())) {
	    	inflater.inflate(R.menu.searchart, menu);
	    }
    	return super.onPrepareOptionsMenu(menu);
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		closeOptionsMenu();
	    switch (item.getItemId()) {
	    case android.R.id.home:
	    	device.goHomeScreen();
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
	
	@Override
    public void onBackPressed() {
    	super.onBackPressed();
    	overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}