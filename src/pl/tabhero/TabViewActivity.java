package pl.tabhero;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
 
public class TabViewActivity extends Activity {
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabview);
    	TextView head = (TextView) findViewById(R.id.performerAndTitle);
    	TextView tab = (TextView) findViewById(R.id.tabInTabView);
    	
        Intent i = getIntent();
        Bundle extras = i.getExtras();
    	String performer = extras.getString("performer");
    	String title = extras.getString("title");

    	head.setText(performer + " - " + title);
    	tab.setText("Jakaś sobie tabulaturka typu\n----------------------------------------------------------------------------------------\n--------4--------3-------");
    	
  
    }
    
    public void tabview(View v) {
    	
    }
}