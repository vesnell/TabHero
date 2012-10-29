package pl.tabhero;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
 
public class SearchActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
    }
    
    public void searchView(View v) throws IOException {
    	String url = "http://www.chords.pl/chwyty/metallica/17824,fight_fire_with_fire";
		String performer = new String();
		String title = new String();
		performer = ((EditText) findViewById(R.id.editPerformer)).getText().toString();
		title = ((EditText) findViewById(R.id.editTitle)).getText().toString();
		
		String tablature = getSection(url);
		Intent i = new Intent(SearchActivity.this, TabViewActivity.class);
		Bundle bun = new Bundle();
		bun.putString("performer", performer);
		bun.putString("title", title);
		bun.putString("tab", tablature);
		i.putExtras(bun);
		startActivity(i);
		//startActivityForResult(i, 500);
		//overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    
    private String getSection(String url) throws IOException {
    	Document doc = Jsoup.connect(url).get();
    	Element elements = doc.select("pre").first();
    	String tab = elements.text();
    	String[] table = tab.split("\n");
    	tab = "";
    	for (int i = 3; i < table.length; i++)
    		tab += table[i] + "\n";
    	return tab;
    }
    
    //@Override
    //protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    //    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    //}
}