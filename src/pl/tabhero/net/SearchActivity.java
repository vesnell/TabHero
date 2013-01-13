package pl.tabhero.net;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import pl.tabhero.R;
import pl.tabhero.core.Performers;
import pl.tabhero.utils.MyEditorKeyActions;
import pl.tabhero.utils.MyFilter;
import pl.tabhero.utils.MyGestureDetector;
import pl.tabhero.utils.MyOnTouchListener;
import pl.tabhero.utils.MyTelephonyManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
 
public class SearchActivity extends Activity {
	
	private ListView searchListView;
	private EditText editPerformer;
	private ImageButton btnSearch;
	private ArrayAdapter<String> listAdapter;
	private MyProgressDialogs progressDialog = new MyProgressDialogs(this);
	private static boolean MAX;
	private static final int MENUWIFI = Menu.FIRST;
	private String chordsUrl = "http://www.chords.pl/wykonawcy/";
	private GestureDetector gestureDetector;
	private MyTelephonyManager device = new MyTelephonyManager(this);
	private boolean errorConnection;
	
    @SuppressWarnings("deprecation")
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        
        device.setHomeButtonEnabledForICS();
        
        btnSearch = (ImageButton) findViewById(R.id.searchBtn);
		editPerformer = (EditText) findViewById(R.id.editPerformer);
		searchListView = (ListView) findViewById(R.id.searchListView);
		
		InputFilter filter = new MyFilter();
		editPerformer.setFilters(new InputFilter[]{filter}); 
		
		TextView.OnEditorActionListener myEditorKeyActions = new MyEditorKeyActions(btnSearch);
		editPerformer.setOnEditorActionListener(myEditorKeyActions);
		
		gestureDetector = new GestureDetector(new MyGestureDetector(this));
        
		OnTouchListener myOnTouchListener = new MyOnTouchListener(gestureDetector);
        searchListView.setOnTouchListener(myOnTouchListener);
    }
    
    public void searchView(View v) {
    	String typedPerformer = editPerformer.getText().toString().toLowerCase();
    	Performers performer = new Performers(typedPerformer);
		device.hideKeyboard(editPerformer);
		
		if(!(performer.typedName.length() > 0)) 
			Toast.makeText(getApplicationContext(), R.string.hintEmpty, Toast.LENGTH_LONG).show();
		else if ((performer.typedName.charAt(0) == ' ') || (performer.typedName.charAt(0) == '.'))
			Toast.makeText(getApplicationContext(), R.string.hintSpace, Toast.LENGTH_LONG).show();
		else if(!(checkInternetConnection()))
			Toast.makeText(getApplicationContext(), R.string.connectionError, Toast.LENGTH_LONG).show();
		else {
			AsyncTask<Void, Void, Boolean> checkConnection = new CheckConnection(this).execute();
			try {
				if(checkConnection.get()) {
					new ConnectToPerformers().execute(performer);
				} else {
					Toast.makeText(getApplicationContext(), R.string.errorInInternetConnection, Toast.LENGTH_LONG).show();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
    }
    
    public class ConnectToPerformers extends AsyncTask<Performers, Void, Performers>{
    	
    	@Override
    	 protected void onPreExecute() {
    		progressDialog.start(getString(R.string.srchPerf));
    	 }
    	
    	@Override
    	 protected void onPostExecute(final Performers performer) {
    		if(!errorConnection) {
    			performer.setListOfNames();
    			performer.setListOfUrls();
    		
    			listAdapter = new ArrayAdapter<String>(SearchActivity.this, R.layout.artistsnet, performer.listOfNames);
    			searchListView.setAdapter(listAdapter);
    			searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    					Intent i = new Intent(SearchActivity.this, SearchTitleActivity.class);
    					Bundle bun = new Bundle();
    					bun.putString("performerName", performer.listOfNames.get(position));
    					bun.putString("performerUrl", performer.listOfUrls.get(position));
    					bun.putBoolean("max", MAX);
    					i.putExtras(bun);
    					startActivity(i);
    					overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    				}
    			});
    		} else {
				Toast.makeText(getApplicationContext(), R.string.websiteConnectionError, Toast.LENGTH_LONG).show();
			}
    		progressDialog.close();
    	 }
    	
		@Override
		protected Performers doInBackground(Performers... params) {
			Performers performer = params[0];
			String url = chordsUrl;
			Document doc = prepareAndConnect(performer.typedName, url);
			if(!errorConnection) {
				performer.setMapOfChosenPerformers(doc);
			}
			return performer;
		} 
    }
    
    private Document prepareAndConnect(String performer, String url) {
		Document doc = null;
		if(Character.isDigit(performer.charAt(0))) {
    		url = url + "1";
    		doc = connect(url);
    	}
    	else {
    		String temp = performer;
    		switch(performer.charAt(0)) {
    			case 'ą':
    				temp = performer.replaceAll("^ą", "a");
    				break;
    			case 'ć':
    				temp = performer.replaceAll("^ć", "c");
    				break;
    			case 'ę':
    				temp = performer.replaceAll("^ę", "e");
    				break;
    			case 'ł':
    				temp = performer.replaceAll("^ł", "l");
    				break;
    			case 'ń':
    				temp = performer.replaceAll("^ń", "n");
    				break;
    			case 'ó':
    				temp = performer.replaceAll("^ó", "o");
    				break;
    			case 'ś':
    				temp = performer.replaceAll("^ś", "s");
    				break;
    			case 'ź':
    				temp = performer.replaceAll("^ź", "z");
    				break;
    			case 'ż':
    				temp = performer.replaceAll("^ż", "z");
    				break;
    		}
    		url = url + temp;
    		doc = connect(url);
    	}
		return doc;
	}
    
    private Document connect(String url) {
    	Document doc = null;
    	try {
			doc = Jsoup.connect(url).get();
			errorConnection = false;
		} catch (MalformedURLException ep) {
			errorConnection = true;
			
		} catch (IOException e) {
			errorConnection = true;
		}
    	return doc;
    }
    
    @SuppressLint("NewApi")
    private void setWifiMenuIcon(Menu menu) {
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
    }
    
	@Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	menu.clear();
    	setWifiMenuIcon(menu);
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
    			Toast.makeText(getApplicationContext(), R.string.wifiFalseError, Toast.LENGTH_LONG).show();
    		}
    	} else {
    		try {
    			wifi.setWifiEnabled(true);
    			timer(true);
    		} catch(Exception e) {
    			Toast.makeText(getApplicationContext(), R.string.wifiTrueError, Toast.LENGTH_LONG).show();
    		}
    	}
    }
    
 public class connectWifi extends AsyncTask<Void, Void, Void>{
    	
    	@Override
    	 protected void onPreExecute() {
    		WifiManager wifi=(WifiManager)getSystemService(Context.WIFI_SERVICE);
    		if(wifi.isWifiEnabled()) {
    			progressDialog.start(getString(R.string.wifiTryOff));
    		} else {
    			progressDialog.start(getString(R.string.wifiTryOn));
    		}
    	 }
    	
    	@Override
   	 	protected void onPostExecute(Void result) {
    		progressDialog.close();
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
    
    public boolean checkInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) SearchActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable() && cm.getActiveNetworkInfo().isConnected()) {
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
    
    @Override
    public void onBackPressed() {
    	super.onBackPressed();
    	overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}