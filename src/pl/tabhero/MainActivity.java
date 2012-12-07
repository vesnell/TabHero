package pl.tabhero;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;

public class MainActivity extends Activity {

	private Button btnOnline;
	private Button btnOnBase;
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
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
	    builder.setTitle(R.string.info);
	    builder.setMessage(R.string.message_info);
	    builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	            dialog.dismiss();
	        }
	    });
	    builder.setPositiveButton(R.string.help, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(MainActivity.this, HelpActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
				dialog.dismiss();
			}
		});
	    AlertDialog alert = builder.create();
	    alert.show();
	}
    
    private void onClickStartActivity(Class<?> activity) {
    	Intent i = new Intent(MainActivity.this, activity);
		startActivityForResult(i, 500);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
