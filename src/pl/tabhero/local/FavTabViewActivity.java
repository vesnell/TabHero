package pl.tabhero.local;

import java.io.IOException;
import pl.tabhero.R;
import pl.tabhero.core.MenuFunctions;
import pl.tabhero.db.DBUtils;
import pl.tabhero.utils.ButtonsScale;
import pl.tabhero.utils.FileUtils;
import pl.tabhero.utils.MyLongClickAdapterToLock;
import pl.tabhero.utils.MyTelephonyManager;
import pl.tabhero.utils.PinchZoom;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FavTabViewActivity extends Activity {
	
	private WakeLock mWakeLock = null;
	private TextView tab;
	private TextView head;
	private LinearLayout buttons;
	private LinearLayout lockButtons;
	private MyTelephonyManager device = new MyTelephonyManager(this);
	private DBUtils dbUtils = new DBUtils(this);
	private String performer;
	private String title;
	private String tablature;
	private String songUrl;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favtabview);
        
        FileUtils fileUtils = new FileUtils(this);
        fileUtils.checkIfMax();
        
        device.setHomeButtonEnabledForICS();
        
        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "lockScreenApp");
        mWakeLock.acquire();
        
        head = (TextView) findViewById(R.id.favPerformerAndTitle);
        tab = (TextView) findViewById(R.id.favTabInTabView);
        Float size = fileUtils.getTabSize();
        Log.d("SIZE2", Float.toString(size));
    	tab.setTextSize(size);

        buttons = (LinearLayout) findViewById(R.id.favButtons);
        buttons.setVisibility(View.GONE);
        lockButtons = (LinearLayout) findViewById(R.id.favLockButtons);
    	lockButtons.setVisibility(View.GONE);
        
        Intent i = getIntent();
        Bundle extras = i.getExtras();
        
        performer = extras.getString("performerName");
        title = extras.getString("songTitle");
        songUrl = extras.getString("songUrl");
        
        tablature = dbUtils.getTablature(songUrl);
        
        head.setText(performer + " - " + title);
        
        fileUtils.makePaths(songUrl);
        fileUtils.makeFiles();
        if(fileUtils.file.isFile()) {
			try {
				fileUtils.updateTablatureFU(tablature, songUrl, fileUtils.file, fileUtils.filePath);
			} catch (IOException e) {
				e.printStackTrace();
			}
			tablature = dbUtils.getTablature(songUrl);
        }
        
        tab.setText(tablature);
        
        MyLongClickAdapterToLock myLongClickAdapterToLock = new MyLongClickAdapterToLock(this, lockButtons);
        tab.setOnLongClickListener(myLongClickAdapterToLock);
        
        PinchZoom pinchZoom = new PinchZoom(this, tab, tablature);
        pinchZoom.drawMatrix();
        tab.setOnTouchListener(pinchZoom);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		ButtonsScale buttonsScale = new ButtonsScale(this);
		buttonsScale.init(buttons, tab);
		return true;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    MyTelephonyManager manager = new MyTelephonyManager(this);
	    if(manager.isTablet()) {
	    	inflater.inflate(R.menu.favtabviewiftablet, menu);
	    } else {
	    	inflater.inflate(R.menu.favtabview, menu);
	    }
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		MenuFunctions menuFunc = new MenuFunctions(this);
	    switch (item.getItemId()) {
	    case android.R.id.home:
	    	device.goHomeScreen();
	    	return true;
	    case R.id.delFromFav:
	        menuFunc.delFromFav(songUrl);
	        return true;
	    case R.id.openWebBrowser:
	    	menuFunc.openWebBrowser(performer, title);
	    	return true;
	    case R.id.minmax:
	    	try {
				menuFunc.minMax();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    	return true;
	    case R.id.editTab:
	    	menuFunc.editTab(tablature, songUrl);
	    	return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
    public void onBackPressed() {
		super.onBackPressed();
    	overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
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
