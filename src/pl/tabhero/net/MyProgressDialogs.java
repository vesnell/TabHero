package pl.tabhero.net;

import pl.tabhero.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

public class MyProgressDialogs {
	public Context context;
	public Activity activity;
	private ProgressDialog progressDialog;
	
	public MyProgressDialogs(Context context) {
		this.context = context;
		this.activity = (Activity) context;
	}
	
	public void start(String progressTitle) {
		this.activity.setProgressBarIndeterminateVisibility(true);
		progressDialog = ProgressDialog.show(this.context, progressTitle, this.context.getString(R.string.wait));
	}
	
	public void close() {
		this.activity.setProgressBarIndeterminateVisibility(false);
		progressDialog.dismiss();
	}
}
