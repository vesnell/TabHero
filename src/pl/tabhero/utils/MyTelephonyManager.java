package pl.tabhero.utils;

import pl.tabhero.R;
import pl.tabhero.TabHero;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

public class MyTelephonyManager {
	private Context context;
	private Activity activity;
	
	public MyTelephonyManager(Context context) {
		this.context = context;
		this.activity = (Activity) context;
	}
	
	public boolean isTablet() {
		boolean isTablet;
		TelephonyManager manager = (TelephonyManager)this.context.getSystemService(Context.TELEPHONY_SERVICE);
        if(manager.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE){
            isTablet = true;
        } else {
            isTablet = false;
        }
        return isTablet;
	}
	
	public void goHomeScreen() {
		Intent intent = new Intent(this.context, TabHero.class);
    	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	this.context.startActivity(intent);
    	this.activity.overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
	}
}
