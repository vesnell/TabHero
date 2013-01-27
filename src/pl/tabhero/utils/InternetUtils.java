package pl.tabhero.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;

public class InternetUtils {

    private Activity activity;

    public InternetUtils(Context context) {
        this.activity = (Activity) context;
    }

    public boolean checkInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) this.activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().isAvailable() && cm.getActiveNetworkInfo().isConnected();
    }
}
