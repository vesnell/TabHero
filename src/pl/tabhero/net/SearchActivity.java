package pl.tabhero.net;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import pl.tabhero.R;
import pl.tabhero.TabHero;
import pl.tabhero.core.Performer;
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
 
public class SearchActivity extends Activity {
	
	private ListView searchListView;
	private EditText editPerformer;
	private ImageButton btnSearch;
	private ArrayAdapter<String> listAdapter;
	private ProgressDialog progressDialog;
	private static boolean MAX;
	private static final int MENUWIFI = Menu.FIRST;
	private boolean isWebsiteAvailable;
	private String chordsUrl = "http://www.chords.pl/wykonawcy/";
	
	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private GestureDetector gestureDetector;
	View.OnTouchListener gestureListener;
	
    @SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.search);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            getActionBar().setHomeButtonEnabled(true);
        }
        
        btnSearch = (ImageButton) findViewById(R.id.searchBtn);
		editPerformer = (EditText) findViewById(R.id.editPerformer);
		searchListView = (ListView) findViewById(R.id.searchListView);
		
		editPerformer.setFilters(new InputFilter[]{filter}); 
		editPerformer.setOnEditorActionListener(new TextView.OnEditorActionListener() {
		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
		                actionId == EditorInfo.IME_ACTION_DONE ||
		                event.getAction() == KeyEvent.ACTION_DOWN &&
		                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
		            btnSearch.performClick();
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
			// right to left swipe
			if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {					Toast.makeText(getApplicationContext(), R.string.choosePerf, Toast.LENGTH_LONG).show();
			} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				onBackPressed();
				//onClickStartActivity(MainActivity.class);
			}
			return false;
		}
	}
    
    public void searchView(View v) {
    	String typedPerformer = editPerformer.getText().toString().toLowerCase();
    	Performer performer = new Performer(typedPerformer);
		hideKeyboard();
		
		if(!(performer.typedName.length() > 0)) 
			Toast.makeText(getApplicationContext(), R.string.hintEmpty, Toast.LENGTH_LONG).show();
		else if ((performer.typedName.charAt(0) == ' ') || (performer.typedName.charAt(0) == '.'))
			Toast.makeText(getApplicationContext(), R.string.hintSpace, Toast.LENGTH_LONG).show();
		else if(!(checkInternetConnection()))
			Toast.makeText(getApplicationContext(), R.string.connectionError, Toast.LENGTH_LONG).show();
		else {
				new checkConnect().execute(performer);		
		}
    }
    
    public class checkConnect extends AsyncTask<Performer, Void, Performer>{

    	@Override
   	 	protected void onPostExecute(Performer performer) {
    		if(isWebsiteAvailable) {
    			new connect().execute(performer);
    		} else {
    			Toast.makeText(getApplicationContext(), R.string.websiteConnectionError, Toast.LENGTH_LONG).show();
    		}
    	}

		@Override
		protected Performer doInBackground(Performer... params) {
			Performer performer = params[0];
			if(isConnected()) {
				isWebsiteAvailable = true;
			} else {
				isWebsiteAvailable = false;
			}
			return performer;
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
    
    public class connect extends AsyncTask<Performer, Void, Performer>{
    	
    	@Override
    	 protected void onPreExecute() {
    		startProgressBar();
    	 }
    	
    	@Override
    	 protected void onPostExecute(final Performer performer) {
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
	        } );
			
    		closeProgressBar();
    	 }
    	
		@Override
		protected Performer doInBackground(Performer... params) {
			Performer performer = params[0];
			String url = chordsUrl;
			Document doc = prepareAndConnect(performer.typedName, url);
	    	performer.setMapOfChosenPerformers(doc);
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
		} catch (MalformedURLException ep) {
			Toast.makeText(getApplicationContext(), R.string.errorInInternetConnection, Toast.LENGTH_LONG).show();
			
		} catch (IOException e) {
			Toast.makeText(getApplicationContext(), R.string.errorInInternetConnection, Toast.LENGTH_LONG).show();
		}
    	return doc;
    }
    
    private void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editPerformer.getWindowToken(), 0);
	}
    
    private void startProgressBar() {
    	setProgressBarIndeterminateVisibility(true);
        progressDialog = ProgressDialog.show(SearchActivity.this, getString(R.string.srchPerf), getString(R.string.wait));
    }
    
    private void startProgressBarWifiOn() {
    	setProgressBarIndeterminateVisibility(true);
        progressDialog = ProgressDialog.show(SearchActivity.this, getString(R.string.wifiTryOn), getString(R.string.wait));
    }
    
    private void startProgressBarWifiOff() {
    	setProgressBarIndeterminateVisibility(true);
        progressDialog = ProgressDialog.show(SearchActivity.this, getString(R.string.wifiTryOff), getString(R.string.wait));
    }
    
    private void closeProgressBar() {
    	setProgressBarIndeterminateVisibility(false);
		progressDialog.dismiss();
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
    		WifiManager wifi=(WifiManager)getSystemService(Context.WIFI_SERVICE);
    		if(wifi.isWifiEnabled()) {
    			startProgressBarWifiOff();
    		} else {
    			startProgressBarWifiOn();
    		}
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
}