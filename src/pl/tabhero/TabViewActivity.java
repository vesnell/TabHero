package pl.tabhero;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
 
public class TabViewActivity extends Activity {
	
	private WakeLock mWakeLock = null;
	private ImageButton btnPlus;
	private ImageButton btnMinus;
	private TextView tab;
	private LinearLayout buttons;
	
	private int scaleText = 12;
	
	DBAdapter db = new DBAdapter(this); 
	String performer;
	String title;
	String listOfSections;
	String songUrl;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabview);
        
        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "lockScreenApp");
        mWakeLock.acquire();

        
    	TextView head = (TextView) findViewById(R.id.performerAndTitle);
    	tab = (TextView) findViewById(R.id.tabInTabView);
    	buttons = (LinearLayout) findViewById(R.id.buttons);
    	buttons.setVisibility(View.GONE);
    	
        Intent i = getIntent();
        Bundle extras = i.getExtras();
    	performer = extras.getString("performerName");
    	title = extras.getString("songTitle");
    	listOfSections = extras.getString("tab");
    	songUrl = extras.getString("songUrl");

    	head.setText(performer + " - " + title);
    	//tab.setText("Jakaś sobie tabulaturka typu\n----------------------------------------------------------------------------------------\n--------4--------3-------");
    	tab.setText(listOfSections);
    	
    	
    	
    }
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		buttons.setVisibility(View.VISIBLE);
		initBtnPlusOnClick();
    	initBtnMinusOnClick();
    	new Handler().postDelayed(new Runnable() {
            public void run() {
            	buttons.setVisibility(View.GONE);
            }
        }, 3000);
		return true;
	}
	
	private void initBtnPlusOnClick() {
		
		btnPlus = (ImageButton) findViewById(R.id.btnPlus);
		
		btnPlus.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				scaleText++;
				tab.setTextSize(TypedValue.COMPLEX_UNIT_SP, scaleText);
			}
			
		});
	}
	
	private void initBtnMinusOnClick() {
		
		btnMinus = (ImageButton) findViewById(R.id.btnMinus);
		
		btnMinus.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				scaleText--;
				tab.setTextSize(TypedValue.COMPLEX_UNIT_SP, scaleText);
			}
			
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.tabview, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.addToFav:
	        addToFav();
	        return true;
	    case R.id.minmax:
	    	minMax();
	    	return true;
	    case R.id.openWebBrowser:
	    	openWebBrowser();
	    	return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	private void minMax() {
    	boolean fullScreen = (getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) != 0;
    	//boolean forceNotFullScreen = (getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN) != 0;
    	//boolean actionbarVisible = getActionBar().isShowing();
       if(fullScreen) {
    	   getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    	   getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }
        else {
        	getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }
	
	public void addToFav() {
		db.open();
		if(isExistRecord() == false) {
			db.insertRecord(performer, title, listOfSections, songUrl);
			Toast.makeText(getApplicationContext(), R.string.addToBaseSuccess, Toast.LENGTH_LONG).show();
		} else
			Toast.makeText(getApplicationContext(), R.string.recordExist, Toast.LENGTH_LONG).show();
		db.close();
	}
	
	public boolean isExistRecord() {
        Cursor c = db.getAllRecords();
        if (c.moveToFirst())
        {
            do {
            	/*Log.d("Performer", performer);
            	Log.d("PerformerDB", c.getString(1));
            	Log.d("Title", title);
            	Log.d("TitleDB", c.getString(2));
            	Log.d("Song", songUrl);
            	Log.d("SongDB", c.getString(4));*/
            	if(/*performer.equals(c.getString(1)) && title.equals(c.getString(2)) && */songUrl.equals(c.getString(4))) {
            		return true;
            	}
            } while (c.moveToNext());
        }
		return false;
	}
	
	private void openWebBrowser() {
		String perf = performer.replaceAll(" ", "%20");
		String tit = title.replaceAll(" ", "%20");
		String question = "http://www.google.com/search?q=" + perf + "%20" + tit + "%20lyrics";
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(question));
		startActivityForResult(browserIntent, 600);
		overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
	}
    
	 public void onPause() {
		 super.onPause();
		 mWakeLock.release();
	 }
	 
	 public void onResume() {
		 super.onResume();
		 mWakeLock.acquire();
	 }
	 
	 public void onDestroy() {
		 mWakeLock.release();
		 super.onDestroy();
		 
	 }
}