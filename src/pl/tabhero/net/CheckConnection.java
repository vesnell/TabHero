package pl.tabhero.net;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import pl.tabhero.R;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.Toast;

public class CheckConnection extends AsyncTask<Void, Void, Boolean> {

    private Context context;
    private final String chordsUrl;
    private static final int MILSEC_CONNECT_TIMEOUT = 2000;
    private static final int RESPONSE_CODE_AVAILABLE = 200;

    public CheckConnection(Context context) {
        this.context = context;
        this.chordsUrl = this.context.getString(R.string.chordsWykonawcyUrl);
    }

    protected void onPostExecute(boolean isWebAv) {
        super.onPostExecute(isWebAv);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        Boolean isWebAv;
        if (isConnected()) {
            isWebAv = true;
        } else {
            isWebAv = false;
        }
        return isWebAv;
    }

    public boolean isConnected() {
        //try {
            ConnectivityManager cm = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                URL url = null;
                try {
                    url = new URL(this.chordsUrl);
                } catch (MalformedURLException e) {
                    Toast.makeText(this.context.getApplicationContext(),
                            this.context.getString(R.string.unknownConnectionError) + "1", Toast.LENGTH_LONG).show();
                }
                HttpURLConnection urlc = null;
                try {
                    urlc = (HttpURLConnection) url.openConnection();
                } catch (IOException e) {
                    Toast.makeText(this.context.getApplicationContext(),
                            this.context.getString(R.string.unknownConnectionError) + "2", Toast.LENGTH_LONG).show();
                }
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(MILSEC_CONNECT_TIMEOUT);
                try {
                    urlc.connect();
                } catch (IOException e) {
                    Toast.makeText(this.context.getApplicationContext(),
                            this.context.getString(R.string.unknownConnectionError) + "3", Toast.LENGTH_LONG).show();
                }
                try {
                    return urlc.getResponseCode() == RESPONSE_CODE_AVAILABLE;
                } catch (IOException e) {
                    Toast.makeText(this.context.getApplicationContext(),
                            this.context.getString(R.string.unknownConnectionError) + "4", Toast.LENGTH_LONG).show();
                }
            }
        //} catch (Exception e) {
        //    Toast.makeText(this.context.getApplicationContext(),
        //            R.string.unknownConnectionError, Toast.LENGTH_LONG).show();
        //}
        return false;
    }
}
