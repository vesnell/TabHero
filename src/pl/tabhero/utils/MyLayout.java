package pl.tabhero.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import pl.tabhero.R;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.AbsListView;
import android.widget.Toast;

public class MyLayout {
    
    private Context context;
    
    public MyLayout (Context context) {
        this.context = context;
    }
    
    public void customizeFastScroller(AbsListView listView) {
        try {
            Class<?> clazz = Class.forName("android.widget.FastScroller");
            Constructor<?> constructor = clazz.getConstructor(Context.class, AbsListView.class);
            Object newFastScroller = constructor.newInstance(this.context, listView);
     
            Field fieldOverlay = clazz.getDeclaredField("mOverlayDrawable");
            Drawable overlay = this.context.getResources().getDrawable(R.drawable.scrollbar_vertical_track);
            fieldOverlay.setAccessible(true);
            fieldOverlay.set(newFastScroller, overlay);
     
            Field fieldThumb = clazz.getDeclaredField("mThumbDrawable");
            Drawable scroller = this.context.getResources().getDrawable(R.drawable.scrollbar_vertical_thumb);
            fieldThumb.setAccessible(true);
            fieldThumb.set(newFastScroller, scroller);
     
            Field orgFastScroller = AbsListView.class.getDeclaredField("mFastScroller");
            orgFastScroller.setAccessible(true);
            orgFastScroller.set(listView, newFastScroller);
     
        } catch (Exception e) {
            Toast.makeText(this.context.getApplicationContext(), 
                    this.context.getString(R.string.fastScrollError), Toast.LENGTH_LONG).show();
        }
    }

}
