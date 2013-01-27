package pl.tabhero.utils;

import pl.tabhero.R;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MyLongClickAdapterToLock implements AdapterView.OnLongClickListener {

    private Context context;
    private LinearLayout lockButtons;
    private ImageButton btnLock;
    private ImageButton btnUnLock;
    private static final long MILSEC_SHOW_VIEW = 3000;
    private boolean lock = false;
    private Activity activity;

    public MyLongClickAdapterToLock(Context context, LinearLayout lockButtons) {
        this.context = context;
        this.activity = (Activity) context;
        this.lockButtons = lockButtons;
    }

    @Override
    public boolean onLongClick(View v) {
        this.lockButtons.setVisibility(View.VISIBLE);
        btnLock = (ImageButton) this.activity.findViewById(R.id.btnLock);
        btnUnLock = (ImageButton) this.activity.findViewById(R.id.btnUnLock);
        if (!lock) {
            btnUnLock.setVisibility(View.GONE);
            btnLock.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    int result = context.getResources().getConfiguration().orientation;
                    if (result == 1) {
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    } else {
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    }
                    lock = true;
                    Toast.makeText(context.getApplicationContext(),
                            R.string.lockOn, Toast.LENGTH_LONG).show();
                    btnLock.setVisibility(View.GONE);
                }
            });
        } else {
            btnLock.setVisibility(View.GONE);
            btnUnLock.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                    lock = false;
                    Toast.makeText(context.getApplicationContext(),
                            R.string.lockOff, Toast.LENGTH_LONG).show();
                    btnUnLock.setVisibility(View.GONE);
                }
            });
        }
        ButtonsRunnable lockBtnsRunnable = new ButtonsRunnable(this.lockButtons);
        lockBtnsRunnable.runLockButtons(lock, btnLock, btnUnLock);
        new Handler().postDelayed(lockBtnsRunnable, MILSEC_SHOW_VIEW);
        return false;
    }

}
