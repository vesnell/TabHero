package pl.tabhero;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class FavTabViewActivity extends Activity{
	
	private WakeLock mWakeLock = null;
	private TextView tab;
	private TextView head;
	private ImageButton btnPlus;
	private ImageButton btnMinus;
	private LinearLayout buttons;
	
	
	private int scaleText = 12;
	
	DBAdapter db = new DBAdapter(this); 
	private String performer;
	private String title;
	private String tablature;
	private String songUrl;
	
	@SuppressLint("NewApi")
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favtabview);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            getActionBar().setHomeButtonEnabled(true);
        }
        
        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "lockScreenApp");
        mWakeLock.acquire();
        
        head = (TextView) findViewById(R.id.favPerformerAndTitle);
        tab = (TextView) findViewById(R.id.favTabInTabView);
        //ScrollView sv = (ScrollView) findViewById(R.id.favScrollInTabView);
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
        
        //sv.scrollTo(0, 12);
        //hsv.smoothScrollTo(0, tab.getHeight());
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
		
		btnPlus = (ImageButton) findViewById(R.id.favBtnPlus);
		
		btnPlus.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				scaleText++;
				tab.setTextSize(TypedValue.COMPLEX_UNIT_SP, scaleText);
			}
			
		});
	}
	
	private void initBtnMinusOnClick() {
		
		btnMinus = (ImageButton) findViewById(R.id.favBtnMinus);
		
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
	    inflater.inflate(R.menu.favtabview, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case android.R.id.home:
	    	Intent intent = new Intent(this, MainActivity.class);
	    	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    	startActivity(intent);
	    	overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
	    	return true;
	    case R.id.delFromFav:
	        delFromFav();
	        return true;
	    case R.id.openWebBrowser:
	    	openWebBrowser();
	    	return true;
	    case R.id.minmax:
	    	minMax();
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
        }
        else {
        	getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }
	
	public void delFromFav() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    //builder.setTitle("Confirm");
	    builder.setMessage(R.string.areYouSure);
	    builder.setNegativeButton(R.string.yes, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	        	db.open();
	    		db.deleteTab(songUrl);
	    		Toast.makeText(getApplicationContext(), R.string.delFromBaseSuccess, Toast.LENGTH_LONG).show();
	    		db.close();
	            dialog.dismiss();
	            Intent i = new Intent(FavTabViewActivity.this, FavoritesTitleActivity.class);
	    		startActivity(i);
	    		startActivityForResult(i, 500);
	    		overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
	        }
	    });
	    builder.setPositiveButton(R.string.no, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	        	Toast.makeText(getApplicationContext(), R.string.notDelFromBase, Toast.LENGTH_LONG).show();
	            dialog.dismiss();
	        }
	    });
	    AlertDialog alert = builder.create();
	    alert.show();
	}
	
	private void openWebBrowser() {
		String perf = performer.replaceAll(" ", "%20");
		String tit = title.replaceAll(" ", "%20");
		String question = "http://www.google.com/search?q=" + perf + "%20" + tit + "%20lyrics";
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(question));
		startActivityForResult(browserIntent, 600);
		overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
	}
	
	/*private void getScrollable() {
		new Runnable() {
            public void run() {
				ScrollView sv = (ScrollView) findViewById(R.id.favScrollInTabView);
				sv.scrollTo(0, sv.getBottom());
			}
		};
	}*/
	@Override
    public void onBackPressed() {
    	Intent intent = new Intent(this, FavoritesTitleActivity.class);
    	startActivityForResult(intent, 500);
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
