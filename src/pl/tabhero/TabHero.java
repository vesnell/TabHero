package pl.tabhero;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class TabHero extends Activity {

	private Button btnOnline;
	private Button btnOnBase;
	
	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private GestureDetector gestureDetector;
	View.OnTouchListener gestureListener;
	
    @SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        
        btnOnline = (Button) findViewById(R.id.online);
        btnOnBase = (Button) findViewById(R.id.favorites);
        
        btnOnline.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				onClickStartActivity(SearchActivity.class);
			}
		});
        
        btnOnBase.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				onClickStartActivity(FavoritesActivity.class);
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
        
    }
    
    class MyGestureDetector extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
				return false;
			// right to left swipe
			if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				onClickStartActivity(SearchActivity.class);
			} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				onClickStartActivity(FavoritesActivity.class);
			}
			return false;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (gestureDetector.onTouchEvent(event))
			return true;
		else
			return false;
	}
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.info:
	        showInfo();
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	private void showInfo() {
		AlertDialog.Builder builder = new AlertDialog.Builder(TabHero.this);
	    builder.setTitle(R.string.info);
	    builder.setMessage(R.string.message_info);
	    builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	            dialog.dismiss();
	        }
	    });
	    builder.setPositiveButton(R.string.help, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(TabHero.this, HelpActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
				dialog.dismiss();
			}
		});
	    AlertDialog alert = builder.create();
	    alert.show();
	}
    
    private void onClickStartActivity(Class<?> activity) {
    	Intent i = new Intent(TabHero.this, activity);
		startActivityForResult(i, 500);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
