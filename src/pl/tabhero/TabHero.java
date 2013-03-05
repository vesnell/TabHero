package pl.tabhero;

import pl.tabhero.core.MenuFunctions;
import pl.tabhero.local.FavoritesActivity;
import pl.tabhero.net.SearchActivity;
import pl.tabhero.utils.FileUtils;
import pl.tabhero.utils.MyGestureDetector;
import pl.tabhero.utils.MyTelephonyManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class TabHero extends Activity {

    private Button btnOnline;
    private Button btnOnBase;
    private GestureDetector gestureDetector;

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        btnOnline = (Button) findViewById(R.id.online);
        btnOnBase = (Button) findViewById(R.id.favorites);
        
        FileUtils fileUtils = new FileUtils(this);
        fileUtils.fillUIFromPreferences();

        gestureDetector = new GestureDetector(new MyGestureDetector(this));
        final MyGestureDetector myGestureDetector = new MyGestureDetector(this);

        btnOnline.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myGestureDetector.onClickStartRightActivity(SearchActivity.class);
            }
        });

        btnOnBase.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myGestureDetector.onClickStartLeftActivity(FavoritesActivity.class);
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        MyTelephonyManager device = new MyTelephonyManager(this);
        if (device.isTablet()) {
            inflater.inflate(R.menu.mainiftablet, menu);
        } else {
            inflater.inflate(R.menu.main, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MenuFunctions menuFunc = new MenuFunctions(this);
        switch (item.getItemId()) {
        case R.id.info:
            menuFunc.showInfo();
            return true;
        case R.id.minmax:
            menuFunc.minMax();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onResume() {
        FileUtils fileUtils = new FileUtils(this);
        fileUtils.fillUIFromPreferences();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        TabHero.this.finish();
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
    }
}
