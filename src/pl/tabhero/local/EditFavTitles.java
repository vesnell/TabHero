package pl.tabhero.local;

import java.util.ArrayList;
import pl.tabhero.R;
import pl.tabhero.core.MenuFunctions;
import pl.tabhero.db.DBUtils;
import pl.tabhero.utils.FileUtils;
import pl.tabhero.utils.MyTelephonyManager;
import pl.tabhero.utils.selector.SelectArralAdapter;
import pl.tabhero.utils.selector.SelectViewHolder;
import pl.tabhero.utils.selector.mItems;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class EditFavTitles extends Activity{
	
	private DBUtils dbUtils = new DBUtils(this);
	private ListView delFavListView;
	private ArrayList<mItems> titleCheckList;
	private ArrayList<String> listToEditTitle;
	private ArrayList<String> listToEditUrl;
	private MyTelephonyManager device = new MyTelephonyManager(this);
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.editrecords);
        
        FileUtils fileUtils = new FileUtils(this);
        fileUtils.checkIfMax();
        
        device.setHomeButtonEnabledForICS();
        
		delFavListView = (ListView) findViewById(R.id.delFavListView);
		
		Intent i = getIntent();
		Bundle extras = i.getExtras();
		
		listToEditTitle = extras.getStringArrayList("listOfTitles");
		listToEditUrl = extras.getStringArrayList("listOfUrl");
		
		titleCheckList = new ArrayList<mItems>();
		for(String perf : listToEditTitle) {
			titleCheckList.add(new mItems(perf));
		}
		
		final ArrayAdapter<mItems> listAdapter = new SelectArralAdapter(this, titleCheckList);
		delFavListView.setAdapter(listAdapter);
		delFavListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View item, int position, long id) {
				mItems titleCheckboxItem = listAdapter.getItem(position);
				titleCheckboxItem.toggleChecked();
				SelectViewHolder viewHolder = (SelectViewHolder) item.getTag();
				viewHolder.getCheckBox().setChecked(titleCheckboxItem.isChecked());
			}
		});
	}
	
	public void deleteAndBackToFavorites(View v) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setMessage(R.string.areYouSure);
	    builder.setNegativeButton(R.string.yes, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	        	dbUtils.deleteTitlesInEdit(titleCheckList, listToEditTitle, listToEditUrl);
	        	Toast.makeText(getApplicationContext(), R.string.delTitleFromBase, Toast.LENGTH_LONG).show();
	        	onBackPressed();
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
    	MenuFunctions menuFunc = new MenuFunctions(this);
	    switch (item.getItemId()) {
	    case android.R.id.home:
	    	device.goHomeScreen();
	    	return true;
	    case R.id.checkReverse:
	        menuFunc.checkReverse(titleCheckList, delFavListView);
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, FavoritesTitleActivity.class);
		EditFavTitles.this.finish();
		startActivity(intent);
		overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
	}
}
