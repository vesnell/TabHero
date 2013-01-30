package pl.tabhero.utils;

import java.util.concurrent.ExecutionException;
import pl.tabhero.R;
import pl.tabhero.net.CheckConnection;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

public class MenuUtils {

    private Menu menu;
    private Context context;
    private Activity activity;
    private static final int MENUWIFI = Menu.FIRST + 1;
    private static final int MOBILEDATA = Menu.FIRST;
    private MyTelephonyManager device;

    public MenuUtils(Context context, Menu menu) {
        this.menu = menu;
        this.context = context;
        this.activity = (Activity) context;
        this.device = new MyTelephonyManager(this.context);
    }

    public Menu setMyWifiMenu() {
        this.menu.clear();
        setWifiMenuIcon(this.menu);
        try {
            setMobileDataIcon(this.menu);
        } catch (InterruptedException e) {
            Toast.makeText(this.context.getApplicationContext(),
                    R.string.mobileDataCheckError, Toast.LENGTH_LONG).show();
        } catch (ExecutionException e) {
            Toast.makeText(this.context.getApplicationContext(),
                    R.string.mobileDataCheckError, Toast.LENGTH_LONG).show();
        }
        MenuInflater inflater = this.activity.getMenuInflater();
        if (!(device.isTablet())) {
            inflater.inflate(R.menu.searchart, menu);
        }
        return this.menu;
    }

    @SuppressLint("NewApi")
    private void setWifiMenuIcon(Menu menu) {
        WifiManager wifi = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            if (wifi.isWifiEnabled()) {
                menu.add(1, MENUWIFI, 0, "").setIcon(R.drawable.wifi_on).setShowAsAction(MENUWIFI);
            } else {
                menu.add(1, MENUWIFI, 0, "").setIcon(R.drawable.wifi_ic).setShowAsAction(MENUWIFI);
            }
        } else {
            if (wifi.isWifiEnabled()) {
                menu.add(1, MENUWIFI, 0, R.string.wifiOn).setIcon(R.drawable.wifi_on);
            } else {
                menu.add(1, MENUWIFI, 0, R.string.wifiOff).setIcon(R.drawable.wifi_ic);
            }
        }
    }
    
    @SuppressLint("NewApi")
    private void setMobileDataIcon(Menu menu) throws InterruptedException, ExecutionException {
        WifiManager wifi = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
        AsyncTask<Void, Void, Boolean> checkConnection = new CheckConnection(this.context).execute();
        if (!wifi.isWifiEnabled() && checkConnection.get()) {
            MobileData mobileData = new MobileData(this.context);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                if (mobileData.isEnabled()) {
                    menu.add(1, MOBILEDATA, 0, "").setIcon(R.drawable.mobiledata).setShowAsAction(MOBILEDATA);
                }
            } else {
                if (mobileData.isEnabled()) {
                    menu.add(1, MOBILEDATA, 0, R.string.mobileData).setIcon(R.drawable.mobiledata);
                }    
            }
        }
    }
}
