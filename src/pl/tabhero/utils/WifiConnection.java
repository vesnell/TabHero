package pl.tabhero.utils;

import pl.tabhero.R;
import pl.tabhero.net.MyProgressDialogs;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.widget.Toast;

public class WifiConnection extends AsyncTask<Void, Void, Void> {
	
	private MyProgressDialogs progressDialog;
	
	public WifiConnection(Context context) {
		this.progressDialog = new MyProgressDialogs(context);
	}
	
	@Override
	 protected void onPreExecute() {
		WifiManager wifi = (WifiManager) this.progressDialog.context.getSystemService(Context.WIFI_SERVICE);
		if(wifi.isWifiEnabled()) {
			progressDialog.start(this.progressDialog.context.getString(R.string.wifiTryOff));
		} else {
			progressDialog.start(this.progressDialog.context.getString(R.string.wifiTryOn));
		}
	 }
	
	@Override
	 	protected void onPostExecute(Void result) {
		progressDialog.close();
		this.progressDialog.activity.openOptionsMenu();
	}

	@Override
	protected Void doInBackground(Void... params) {
		wifiMechanise();
		return null;
	}
	
	private void wifiMechanise() {
    	WifiManager wifi = (WifiManager) this.progressDialog.context.getSystemService(Context.WIFI_SERVICE);
    	if(wifi.isWifiEnabled()) {
    		try {
    			wifi.setWifiEnabled(false);
    			timer(false);
    		} catch(Exception e) {
    			Toast.makeText(this.progressDialog.context.getApplicationContext(), R.string.wifiFalseError, Toast.LENGTH_LONG).show();
    		}
    	} else {
    		try {
    			wifi.setWifiEnabled(true);
    			timer(true);
    		} catch(Exception e) {
    			Toast.makeText(this.progressDialog.context.getApplicationContext(), R.string.wifiTrueError, Toast.LENGTH_LONG).show();
    		}
    	}
    }
	
	private void timer(final boolean bool) {
		InternetUtils myWifi = new InternetUtils(this.progressDialog.context);
    	long start = System.currentTimeMillis();
		long end = 0;
		do {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			end = System.currentTimeMillis();
		} while((myWifi.checkInternetConnection() != bool) && (end  - start < 15000));
    }
}
