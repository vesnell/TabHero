package pl.tabhero;

import android.os.Bundle;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.view.Menu;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        TabHost tabhost = getTabHost();
        String search = getString(R.string.search);
        String favorites = getString(R.string.favorites);
        
        TabSpec searchspec = tabhost.newTabSpec(search);
        searchspec.setIndicator(search, getResources().getDrawable(R.drawable.icon_search_tab));
        Intent searchsIntent = new Intent(this, SearchActivity.class);
        searchspec.setContent(searchsIntent);
        
        TabSpec favspec = tabhost.newTabSpec(favorites);
        favspec.setIndicator(favorites, getResources().getDrawable(R.drawable.icon_favorites_tab));
        Intent favIntent = new Intent(this, FavoritesActivity.class);
        favspec.setContent(favIntent);
        
        tabhost.addTab(searchspec);
        tabhost.addTab(favspec);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
