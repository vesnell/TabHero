package pl.tabhero.utils;

import android.content.Context;
import android.telephony.TelephonyManager;

public class MyTelephonyManager {
	private Context context;
	
	public MyTelephonyManager(Context context) {
		this.context = context;
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
}
