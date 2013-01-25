package pl.tabhero.net;

import java.net.HttpURLConnection;
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
	
	public CheckConnection(Context context) {
		this.context = context;
		chordsUrl = this.context.getString(R.string.chordsWykonawcyUrl);
	}

	protected void onPostExecute(boolean isWebAv) {
		super.onPostExecute(isWebAv);
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		Boolean isWebAv;
		if(isConnected()) {
			isWebAv = true;
		} else {
			isWebAv = false;
		}
		return isWebAv;
	}
	
	public boolean isConnected() {
        try {
            ConnectivityManager cm = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                URL url = new URL(chordsUrl);
                HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(2000);
                urlc.connect();
                if (urlc.getResponseCode() == 200) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        catch(Exception e) {
        	Toast.makeText(this.context.getApplicationContext(), R.string.unknownConnectionError, Toast.LENGTH_LONG).show();
        }
        return false;
    }
}
