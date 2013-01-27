package pl.tabhero.net;

import pl.tabhero.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

public class MyProgressDialogs {
    private Context context;
    private Activity activity;
    private ProgressDialog progressDialog;

    public MyProgressDialogs(Context context) {
        this.setContext(context);
        this.setActivity((Activity) context);
    }

    public void start(String progressTitle) {
        this.getActivity().setProgressBarIndeterminateVisibility(true);
        progressDialog = ProgressDialog.show(this.getContext(), progressTitle,
                this.getContext().getString(R.string.wait));
    }

    public void close() {
        this.getActivity().setProgressBarIndeterminateVisibility(false);
        progressDialog.dismiss();
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}
