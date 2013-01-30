package pl.tabhero.utils;

import pl.tabhero.R;
import pl.tabhero.net.MyProgressDialogs;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.widget.Toast;

public class WifiConnection extends AsyncTask<Void, Void, Void> {

    private Context context;
    private MyProgressDialogs progressDialog;
    private static final int MILSECONDS_SLEEP = 500;
    private static final int MILSECONDS_WAIT = 15000;

    public WifiConnection(Context context) {
        this.context = context;
        this.progressDialog = new MyProgressDialogs(context);
    }

    @Override
    protected void onPreExecute() {
        WifiManager wifi = (WifiManager) this.progressDialog.getContext().getSystemService(Context.WIFI_SERVICE);
        if (wifi.isWifiEnabled()) {
            progressDialog.start(this.progressDialog.getContext().getString(R.string.wifiTryOff));
        } else {
            MobileData mobileData = new MobileData(this.context);
            if (mobileData.isEnabled()) {
                mobileData.setMobileDataEnabled(false);
            }
            progressDialog.start(this.progressDialog.getContext().getString(R.string.wifiTryOn));
        }
    }

    @Override
    protected void onPostExecute(Void result) {
        progressDialog.close();
        this.progressDialog.getActivity().openOptionsMenu();
    }

    @Override
    protected Void doInBackground(Void... params) {
        wifiMechanise();
        return null;
    }

    private void wifiMechanise() {
        WifiManager wifi = (WifiManager) this.progressDialog.getContext().getSystemService(Context.WIFI_SERVICE);
        if (wifi.isWifiEnabled()) {
            try {
                wifi.setWifiEnabled(false);
                timer(false);
            } catch (Exception e) {
                Toast.makeText(this.progressDialog.getContext().getApplicationContext(),
                        R.string.wifiFalseError, Toast.LENGTH_LONG).show();
            }
        } else {
            try {
                wifi.setWifiEnabled(true);
                timer(true);
            } catch (Exception e) {
                Toast.makeText(this.progressDialog.getContext().getApplicationContext(),
                        R.string.wifiTrueError, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void timer(final boolean bool) {
        InternetUtils myWifi = new InternetUtils(this.progressDialog.getContext());
        long start = System.currentTimeMillis();
        long end = 0;
        do {
            try {
                Thread.sleep(MILSECONDS_SLEEP);
            } catch (InterruptedException e) {
                Toast.makeText(this.progressDialog.getContext().getApplicationContext(),
                        R.string.sleepTaskError, Toast.LENGTH_LONG).show();
            }
            end = System.currentTimeMillis();
        } while((myWifi.checkInternetConnection() != bool) && (end  - start < MILSECONDS_WAIT));
    }
}
