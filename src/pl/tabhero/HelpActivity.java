package pl.tabhero;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

public class HelpActivity extends Activity {
	
	@SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            getActionBar().setHomeButtonEnabled(true);
        }
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
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
    public void onBackPressed() {
    	Intent intent = new Intent(HelpActivity.this, MainActivity.class);
    	startActivity(intent);
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
    }
}
