package pl.tabhero.utils;

import java.lang.reflect.Method;
import android.content.Context;
import android.net.ConnectivityManager;

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
            e.printStackTrace();
            return null;
        }
    }

}
