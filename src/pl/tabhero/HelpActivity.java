package pl.tabhero;

import pl.tabhero.utils.FileUtils;
import pl.tabhero.utils.MyTelephonyManager;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

public class HelpActivity extends Activity {
	
	private MyTelephonyManager device = new MyTelephonyManager(this);
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);
        
        FileUtils fileUtils = new FileUtils(this);
        fileUtils.checkIfMax();
        
        device.setHomeButtonEnabledForICS();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case android.R.id.home:
	    	device.goHomeScreen();
	    	return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
    public void onBackPressed() {
		HelpActivity.this.finish();
		super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
    }
}
