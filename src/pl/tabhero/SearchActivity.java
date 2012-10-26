package pl.tabhero;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
 
public class SearchActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
    }
    
    public void searchView(View v) {
		String performer = new String();
		String title = new String();
		performer = ((EditText) findViewById(R.id.editPerformer)).getText().toString();
		title = ((EditText) findViewById(R.id.editTitle)).getText().toString();
		Intent i = new Intent(SearchActivity.this, TabViewActivity.class);
		Bundle bun = new Bundle();
		bun.putString("performer", performer);
		bun.putString("title", title);
		i.putExtras(bun);
		startActivity(i);
		//startActivityForResult(i, 500);
		//overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    
    //@Override
    //protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    //    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    //}
}