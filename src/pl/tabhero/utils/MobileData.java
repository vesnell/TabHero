package pl.tabhero.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import pl.tabhero.R;
import android.content.Context;
import android.net.ConnectivityManager;
import android.widget.Toast;

public class MobileData {
    
    private Context context;
    private static final int MILSECONDS_SLEEP = 1000;
    private static final int MILSECONDS_WAIT = 15000;
    
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
    
    public void turnOff() {
        MyTelephonyManager device = new MyTelephonyManager(this.context);
        setMobileDataEnabled(false);
        timer(false);
        device.netOpenOptionsMenu();
    }
    
    private void timer(final boolean bool) {
        InternetUtils myWifi = new InternetUtils(this.context);
        long start = System.currentTimeMillis();
        long end = 0;
        do {
            try {
                Thread.sleep(MILSECONDS_SLEEP);
            } catch (InterruptedException e) {
                Toast.makeText(this.context.getApplicationContext(),
                        R.string.sleepTaskError, Toast.LENGTH_LONG).show();
            }
            end = System.currentTimeMillis();
        } while((myWifi.checkInternetConnection() != bool) && (end  - start < MILSECONDS_WAIT));
    }
}
