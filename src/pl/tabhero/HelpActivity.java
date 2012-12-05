package pl.tabhero;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class HelpActivity extends Activity {

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);
	}
	
	@Override
    public void onBackPressed() {
    	Intent intent = new Intent(HelpActivity.this, MainActivity.class);
    	startActivity(intent);
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
    }
}
