package pl.tabhero.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import pl.tabhero.R;
import android.content.Context;
import android.net.ConnectivityManager;
import android.widget.Toast;

public class MobileData {
    
    private Context context;
    
    public MobileData(Context context) {
        this.context = context;
    }
    
    public Boolean isEnabled() {
        Object connectivityService = this.context.getSystemService(Context.CONNECTIVITY_SERVICE); 
        ConnectivityManager cm = (ConnectivityManager) connectivityService;

        try {
            Class<?> c = Class.forName(cm.getClass().getName());
            Method m = c.getDeclaredMethod("getMobileDataEnabled");
            m.setAccessible(true);
            return (Boolean)m.invoke(cm);
        } catch (Exception e) {
            Toast.makeText(this.context.getApplicationContext(),
                    R.string.mobileDataCheckError, Toast.LENGTH_LONG).show();
            return null;
        }
    }
    
    public boolean setMobileDataEnabled(boolean enable) {
        boolean result = false;
        final ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {    
            final Class<?> conmanClass = Class.forName(conman.getClass().getName());
            final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
            iConnectivityManagerField.setAccessible(true);
            final Object iConnectivityManager = iConnectivityManagerField.get(conman);
            final Class<?> iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
            final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
            setMobileDataEnabledMethod.setAccessible(true);

            setMobileDataEnabledMethod.invoke(iConnectivityManager, enable);
            result = true;
        } catch (Exception e){
            result = false;
        }
        return result;
    }

}
