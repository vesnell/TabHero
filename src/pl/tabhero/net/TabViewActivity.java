package pl.tabhero.net;

import java.text.SimpleDateFormat;
import java.util.Date;

import pl.tabhero.R;
import pl.tabhero.core.MenuFunctions;
import pl.tabhero.utils.ButtonsScale;
import pl.tabhero.utils.FileUtils;
import pl.tabhero.utils.MyLongClickAdapterToLock;
import pl.tabhero.utils.MyTelephonyManager;
import pl.tabhero.utils.PinchZoom;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TabViewActivity extends Activity {

    private WakeLock mWakeLock = null;
    private TextView tab;
    private LinearLayout buttons;
    private LinearLayout lockButtons;
    private MyTelephonyManager device = new MyTelephonyManager(this);
    private String performer;
    private String title;
    private String tablature;
    private String songUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabview);

        FileUtils fileUtils = new FileUtils(this);
        fileUtils.fillUIFromPreferences();

        device.setHomeButtonEnabledForICS();

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "lockScreenApp");
        mWakeLock.acquire();


        TextView head = (TextView) findViewById(R.id.performerAndTitle);
        tab = (TextView) findViewById(R.id.tabInTabView);
        Float size = fileUtils.setSizeText();
        tab.setTextSize(size);

        buttons = (LinearLayout) findViewById(R.id.buttons);
        buttons.setVisibility(View.GONE);

        lockButtons = (LinearLayout) findViewById(R.id.lockButtons);
        lockButtons.setVisibility(View.GONE);

        Intent i = getIntent();
        Bundle extras = i.getExtras();

        performer = extras.getString("performerName");
        title = extras.getString("songTitle");
        tablature = extras.getString("tab");
        songUrl = extras.getString("songUrl");
        String type = extras.getString("type");
                
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateAndTime = sdf.format(new Date());
        if (type == null) {
            fileUtils.setToLastTen(performer, title, tablature, songUrl, currentDateAndTime, getString(R.string.net));
        } else {
            fileUtils.setToLastTen(performer, title, tablature, songUrl, currentDateAndTime, type);
        }

        head.setText(performer + " - " + title);

        MyLongClickAdapterToLock myLongClickAdapterToLock = new MyLongClickAdapterToLock(this, lockButtons);
        tab.setOnLongClickListener(myLongClickAdapterToLock);

        PinchZoom pinchZoom = new PinchZoom(this, tab, tablature);
        pinchZoom.drawMatrix();
        tab.setOnTouchListener(pinchZoom);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        ButtonsScale buttonsScale = new ButtonsScale(this);
        buttonsScale.init(buttons, tab);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        MyTelephonyManager manager = new MyTelephonyManager(this);
        if (manager.isTablet()) {
            inflater.inflate(R.menu.tabviewiftablet, menu);
        } else {
            inflater.inflate(R.menu.tabview, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MenuFunctions menuFunc = new MenuFunctions(this);
        switch (item.getItemId()) {
        case android.R.id.home:
            FileUtils fileUtils = new FileUtils(this);
            fileUtils.saveTabSize(tab);
            device.goHomeScreen();
            return true;
        case R.id.addToFav:
            menuFunc.addToFav(performer, title, tablature, songUrl);
            return true;
        case R.id.openWebBrowser:
            menuFunc.openWebBrowser(performer, title);
            return true;
        case R.id.minmax:
            menuFunc.minMax();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    public void onPause() {
        super.onPause();
        mWakeLock.release();
    }

    public void onResume() {
        super.onResume();
        mWakeLock.acquire();
    }

    @Override
    public void onBackPressed() {
        FileUtils fileUtils = new FileUtils(this);
        fileUtils.saveTabSize(tab);
        TabViewActivity.this.finish();
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
    }
}
