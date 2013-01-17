package pl.tabhero.local;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import pl.tabhero.R;
import pl.tabhero.core.MenuFunctions;
import pl.tabhero.db.DBAdapter;
import pl.tabhero.utils.ButtonsScale;
import pl.tabhero.utils.MyLongClickAdapterToLock;
import pl.tabhero.utils.MyTelephonyManager;
import pl.tabhero.utils.PinchZoom;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FavTabViewActivity extends Activity {
	
	private DBAdapter db = new DBAdapter(this); 
	private WakeLock mWakeLock = null;
	private TextView tab;
	private TextView head;
	private LinearLayout buttons;
	private LinearLayout lockButtons;
	private boolean max;
	private MyTelephonyManager device = new MyTelephonyManager(this);
	
	private String performer;
	private String title;
	private String tablature;
	private String songUrl;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favtabview);
        
        device.setHomeButtonEnabledForICS();
        
        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "lockScreenApp");
        mWakeLock.acquire();
        
        head = (TextView) findViewById(R.id.favPerformerAndTitle);
        tab = (TextView) findViewById(R.id.favTabInTabView);

        buttons = (LinearLayout) findViewById(R.id.favButtons);
        buttons.setVisibility(View.GONE);
        lockButtons = (LinearLayout) findViewById(R.id.favLockButtons);
    	lockButtons.setVisibility(View.GONE);
        
        Intent i = getIntent();
        Bundle extras = i.getExtras();
        
        max = extras.getBoolean("max");
		if(max) {
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
        
        performer = extras.getString("performerName");
        title = extras.getString("songTitle");
        songUrl = extras.getString("songUrl");
        
        tablature = getTablature(songUrl);
        
        head.setText(performer + " - " + title);
        
        String[] fileTab1 = songUrl.split("/");
		String filePerf = fileTab1[4];
		String[] fileTab2 = fileTab1[5].split(",");
		String fileId = fileTab2[0];
		String fileTitle = fileTab2[1];
		File root = Environment.getExternalStorageDirectory();
	    String dir = root.getAbsolutePath() + File.separator + "Android" + File.separator + getPackageName();
        String fileName = dir + File.separator + filePerf + "-" + fileTitle + "." + fileId + ".txt";
        File file = new File(fileName);
        if(file.isFile()) {
        	try {
				String tabFromFile = readFile(fileName);
				long tabId = addId(songUrl);
				db.open();
				db.updateTablature(tabFromFile, tabId);
				db.close();
				tablature = tabFromFile;
				file.delete();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        
        tab.setText(tablature);
        
        MyLongClickAdapterToLock myLongClickAdapterToLock = new MyLongClickAdapterToLock(this, lockButtons);
        tab.setOnLongClickListener(myLongClickAdapterToLock);
        
        PinchZoom pinchZoom = new PinchZoom(tab, tablature);
        pinchZoom.drawMatrix();
        tab.setOnTouchListener(pinchZoom);
	}
	
	private String getTablature(String url) {
		String tabl = "";
		db.open();
		Cursor c = db.getRecordUrl(url);
		if(c.moveToFirst()) {
			do {
				tabl = c.getString(3);
			} while(c.moveToNext());
		}
		db.close();
		return tabl;
	}
	
	private long addId(String url) {
		long rowId = 0;
		db.open();
        Cursor c = db.getRecordUrl(url);
        if (c.moveToFirst())
        {
            do {
            	rowId = c.getLong(0);
            } while (c.moveToNext());
        }
        db.close();
		return rowId;
	}
	
	private static String readFile(String path) throws IOException {
		  FileInputStream stream = new FileInputStream(new File(path));
		  try {
		    FileChannel fc = stream.getChannel();
		    MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
		    return Charset.defaultCharset().decode(bb).toString();
		  }
		  finally {
		    stream.close();
		  }
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
	    	minMax();
	    	return true;
	    case R.id.editTab:
	    	menuFunc.editTab(tablature, songUrl);
	    	return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	private void minMax() {
    	boolean fullScreen = (getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) != 0;
       if(fullScreen) {
    	   getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    	   getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
    	   max = false;
        }
        else {
        	getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        	max = true;
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
