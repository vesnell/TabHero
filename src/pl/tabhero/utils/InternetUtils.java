package pl.tabhero.utils;

import java.io.IOException;
import java.net.MalformedURLException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;

public class InternetUtils {
	
	public Context context;
	public Activity activity;
	
	public InternetUtils(Context context) {
		this.context = context;
		this.activity = (Activity) context;
	}
	
	public boolean checkInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) this.activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable() && cm.getActiveNetworkInfo().isConnected()) {
            return true;
        } else {
            return false;
        }
	}
}
