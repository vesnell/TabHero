package pl.tabhero;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
 
public class TabViewActivity extends Activity {
	
	private WakeLock mWakeLock = null;
	private ImageButton btnPlus;
	private ImageButton btnMinus;
	private TextView tab;
	private LinearLayout buttons;
	
	int scaleText = 12;
	
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
    	String performer = extras.getString("performerName");
    	String title = extras.getString("songTitle");
    	String listOfSections = extras.getString("tab");

    	head.setText(performer + " - " + title);
    	//tab.setText("Jakaś sobie tabulaturka typu\n----------------------------------------------------------------------------------------\n--------4--------3-------");
    	tab.setText(listOfSections);
    	
    	
    	
    }
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		buttons.setVisibility(View.VISIBLE);
		initBtnPlusOnClick();
    	initBtnMinusOnClick();
	    /*if (event.getAction() == MotionEvent.ACTION_DOWN) {
	        System.out.println("Touch Down X:" + event.getX() + " Y:" + event.getY());
	    } 
	    if (event.getAction() == MotionEvent.ACTION_UP) {
	        System.out.println("Touch Up X:" + event.getX() + " Y:" + event.getY());
	    }*/
	    //return super.onTouchEvent(event);
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