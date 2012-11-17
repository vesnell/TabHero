package pl.tabhero;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
 
public class TabViewActivity extends Activity {
	
	private WakeLock mWakeLock = null;
	private ImageButton btnPlus;
	private TextView tab;
	float scaleText;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabview);
        
        btnPlus = (ImageButton) findViewById(R.id.btnPlus);
        
        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "lockScreenApp");
        mWakeLock.acquire();

        
    	TextView head = (TextView) findViewById(R.id.performerAndTitle);
    	tab = (TextView) findViewById(R.id.tabInTabView);
    	
        Intent i = getIntent();
        Bundle extras = i.getExtras();
    	String performer = extras.getString("performerName");
    	String title = extras.getString("songTitle");
    	String listOfSections = extras.getString("tab");

    	head.setText(performer + " - " + title);
    	//tab.setText("Jakaś sobie tabulaturka typu\n----------------------------------------------------------------------------------------\n--------4--------3-------");
    	tab.setText(listOfSections);
    	
    	initBtnPlusOnClick();
    	
  
    }
	
	private void initBtnPlusOnClick() {
		btnPlus.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				scaleText = tab.getTextSize();
				scaleText += 0.5;
				tab.setTextSize(scaleText);
			}
			
		});
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