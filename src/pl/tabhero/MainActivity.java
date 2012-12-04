package pl.tabhero;

import android.os.Bundle;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

//@SuppressWarnings("deprecation")
public class MainActivity extends Activity /*TabActivity*/ {

	private Button btnOnline;
	private Button btnOnBase;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        
        btnOnline = (Button) findViewById(R.id.online);
        btnOnBase = (Button) findViewById(R.id.favorites);
        
        btnOnline.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onClickStartActivity(SearchActivity.class);
				//Intent i = new Intent(MainActivity.this, SearchActivity.class);
				//startActivityForResult(i, 500);
				//overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
			}
		});
        
        btnOnBase.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onClickStartActivity(FavoritesActivity.class);
				//Intent i = new Intent(MainActivity.this, FavoritesActivity.class);
				//startActivityForResult(i, 500);
				//overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
			}
		});
        
        
        
        /*requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);
        
        Button startBtn = (Button) findViewById(R.id.startBtn);
        
        startBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(MainActivity.this, SearchActivity.class);
				startActivity(i);
			}
		});*/
        
        /*TabHost tabhost = getTabHost();
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
        tabhost.addTab(favspec);*/
    }
    
    private void onClickStartActivity(Class<?> activity) {
    	Intent i = new Intent(MainActivity.this, activity);
		startActivityForResult(i, 500);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	//if(requestCode == 500) {
    		//Log.d("OK", "OK");
    	//Log.d("1111", Integer.toString(requestCode));
    	//Log.d("2222", Integer.toString(resultCode));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    	//} else 
    		//Log.d("EROR", "EROR");
    }
}
