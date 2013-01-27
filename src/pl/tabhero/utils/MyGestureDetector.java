package pl.tabhero.utils;

import pl.tabhero.R;
import pl.tabhero.TabHero;
import pl.tabhero.local.FavoritesActivity;
import pl.tabhero.local.FavoritesTitleActivity;
import pl.tabhero.net.SearchActivity;
import pl.tabhero.net.SearchTitleActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.Toast;

public class MyGestureDetector extends SimpleOnGestureListener {
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private Context context;
    private Activity activity;
    private static final String TABHERO = TabHero.class.getSimpleName();
    private static final String NET_SEARCH_PERF = SearchActivity.class.getSimpleName();
    private static final String NET_SEARCH_TITLE = SearchTitleActivity.class.getSimpleName();
    private static final String FAV_SEARCH_PERF = FavoritesActivity.class.getSimpleName();
    private static final String FAV_SEARCH_TITLE = FavoritesTitleActivity.class.getSimpleName();

    public MyGestureDetector(Context context) {
        this.context = context.getApplicationContext();
        this.activity = (Activity) context;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        String className = this.activity.getClass().getSimpleName();
        if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) {
            return false;
        } else if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
            if (className.equals(TABHERO)) {
                onClickStartRightActivity(SearchActivity.class);
            } else if (className.equals(NET_SEARCH_PERF)) {
                Toast.makeText(this.context, R.string.choosePerf, Toast.LENGTH_LONG).show();
            } else if (className.equals(NET_SEARCH_TITLE)) {
                Toast.makeText(this.context, R.string.chooseTitle, Toast.LENGTH_LONG).show();
            } else if (className.equals(FAV_SEARCH_PERF)) {
                this.activity.onBackPressed();
            } else if (className.equals(FAV_SEARCH_TITLE)) {
                this.activity.onBackPressed();
            }
        } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
            if (className.equals(TABHERO)) {
                onClickStartLeftActivity(FavoritesActivity.class);
            } else if (className.equals(NET_SEARCH_PERF)) {
                this.activity.onBackPressed();
            } else if (className.equals(NET_SEARCH_TITLE)) {
                this.activity.onBackPressed();
            } else if (className.equals(FAV_SEARCH_PERF)) {
                Toast.makeText(this.context, R.string.choosePerf, Toast.LENGTH_LONG).show();
            } else if (className.equals(FAV_SEARCH_TITLE)) {
                Toast.makeText(this.context, R.string.chooseTitle, Toast.LENGTH_LONG).show();
            }
        }
        return false;
    }

    public void onClickStartRightActivity(Class<?> activity) {
        Intent i = new Intent(this.activity, activity);
        this.activity.startActivity(i);
        this.activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void onClickStartLeftActivity(Class<?> activity) {
        Intent i = new Intent(this.activity, activity);
        this.activity.startActivity(i);
        this.activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
