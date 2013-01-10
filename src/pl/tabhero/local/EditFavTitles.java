package pl.tabhero.local;

import java.util.ArrayList;

import pl.tabhero.R;
import pl.tabhero.TabHero;
import pl.tabhero.R.anim;
import pl.tabhero.R.id;
import pl.tabhero.R.layout;
import pl.tabhero.R.menu;
import pl.tabhero.R.string;
import pl.tabhero.db.DBAdapter;
import pl.tabhero.utils.MyTelephonyManager;
import pl.tabhero.utils.selector.SelectArralAdapter;
import pl.tabhero.utils.selector.SelectViewHolder;
import pl.tabhero.utils.selector.mItems;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class EditFavTitles extends Activity{
	
DBAdapter db = new DBAdapter(this);
	
	private Button btnDeleteFavPerfs;
	private ListView delFavListView;
	private ArrayAdapter<mItems> listAdapter;
	private ArrayList<mItems> planetList;
	private ArrayList<String> listToEditTitle;
	private ArrayList<String> listToEditUrl;
	private MyTelephonyManager device = new MyTelephonyManager(this);
	
	
	@SuppressLint("NewApi")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.editfavperfs);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            getActionBar().setHomeButtonEnabled(true);
        }
        
        btnDeleteFavPerfs = (Button) findViewById(R.id.deleteFavPerfs);
		delFavListView = (ListView) findViewById(R.id.delFavListView);
		
		Intent i = getIntent();
		Bundle extras = i.getExtras();
		
		delFavListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View item, int position, long id) {
				// TODO Auto-generated method stub
				mItems planet = listAdapter.getItem(position);
				planet.toggleChecked();
				SelectViewHolder viewHolder = (SelectViewHolder) item.getTag();
				viewHolder.getCheckBox().setChecked(planet.isChecked());
			}
			
		});
		
		listToEditTitle = extras.getStringArrayList("listOfTitles");
		listToEditUrl = extras.getStringArrayList("listOfUrl");
		Log.d("SIZE EDIT URL", Integer.toString(listToEditUrl.size()));
		
		planetList = new ArrayList<mItems>();
		for(String perf : listToEditTitle) {
			planetList.add(new mItems(perf));
		}
		
		listAdapter = new SelectArralAdapter(this, planetList);
		delFavListView.setAdapter(listAdapter);
	}
	
	public void deleteAndBackToFavorites(View v) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setMessage(R.string.areYouSure);
	    builder.setNegativeButton(R.string.yes, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	        	db.open();
	        	for(mItems title : planetList) {
	        		if(title.isChecked() == true) {	
	        			//db.deletePerf(title.getName());
	        			for(int i = 0; i < listToEditTitle.size(); i++) {
	        				if(title.getName().equals(listToEditTitle.get(i))) {
	        					db.deleteTab(listToEditUrl.get(i));
	        				}
	        			}
	        		}
	        	}
	        	Toast.makeText(getApplicationContext(), R.string.delTitleFromBase, Toast.LENGTH_LONG).show();
	        	db.close();
	        	Intent i = new Intent(EditFavTitles.this, FavoritesTitleActivity.class);
	        	startActivity(i);
	        	startActivityForResult(i, 500);
	        	overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
	        }
	    });
	    builder.setPositiveButton(R.string.no, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	        	Toast.makeText(getApplicationContext(), R.string.notDelFromTitlesBase, Toast.LENGTH_LONG).show();
	            dialog.dismiss();
	        }
	    });
	    AlertDialog alert = builder.create();
	    alert.show();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.checkreverse, menu);
	    return true;
	}
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case android.R.id.home:
	    	device.goHomeScreen();
	    	return true;
	    case R.id.checkReverse:
	        checkReverse();
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
    
    private void checkReverse() {
    	for(mItems title : planetList) {
    		if(title.isChecked() == true) {
    			title.setChecked(false);
    		} else {
    			title.setChecked(true);
    		}
    	}
    	listAdapter = new SelectArralAdapter(this, planetList);
		delFavListView.setAdapter(listAdapter);
    }
	
	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, FavoritesTitleActivity.class);
		startActivityForResult(intent, 500);
		overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
	}

}
