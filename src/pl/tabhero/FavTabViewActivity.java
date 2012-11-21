package pl.tabhero;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FavTabViewActivity extends Activity{
	
	private WakeLock mWakeLock = null;
	private TextView tab;
	private TextView head;
	private ImageButton btnPlus;
	private ImageButton btnMinus;
	private LinearLayout buttons;
	
	private int scaleText = 12;
	
	DBAdapter db = new DBAdapter(this); 
	String performer;
	String title;
	String tablature;
	String songUrl;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favtabview);
        
        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "lockScreenApp");
        mWakeLock.acquire();
        
        head = (TextView) findViewById(R.id.favPerformerAndTitle);
        tab = (TextView) findViewById(R.id.favTabInTabView);
        buttons = (LinearLayout) findViewById(R.id.favButtons);
        buttons.setVisibility(View.GONE);
        
        Intent i = getIntent();
        Bundle extras = i.getExtras();
        performer = extras.getString("performerName");
        title = extras.getString("songTitle");
        tablature = extras.getString("songTab");
        songUrl = extras.getString("songUrl");
        
        head.setText(performer + " - " + title);
        tab.setText(tablature);
        
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
