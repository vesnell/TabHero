package pl.tabhero.local;

import java.io.IOException;
import java.util.ArrayList;
import pl.tabhero.R;
import pl.tabhero.core.MenuFunctions;
import pl.tabhero.db.DBUtils;
import pl.tabhero.utils.FileUtils;
import pl.tabhero.utils.LongClickOnItemToChangeRecordName;
import pl.tabhero.utils.MyFilter;
import pl.tabhero.utils.MyGestureDetector;
import pl.tabhero.utils.MyOnKeyListener;
import pl.tabhero.utils.MyOnTouchListener;
import pl.tabhero.utils.MyTelephonyManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FavoritesTitleActivity extends Activity {
	
	private TextView chosenFavPerf;
	private EditText editFavTitle;
	private ListView searchFavTitleListView;
	private ImageButton imgBtn;
	private ArrayAdapter<String> listAdapter;
	private ArrayList<String> listOfFavTitle;
	private ArrayList<String> listOfChosenTitleFromBase;
	private ArrayList<String> listOfChosenUrlFromBase;
	private ArrayList<String> listOfChosenTitleFromBase2;
	private ArrayList<String> listOfChosenUrlFromBase2;
	private String performerName;
	private GestureDetector gestureDetector;
	private MyTelephonyManager device = new MyTelephonyManager(this);
	private MenuFunctions menuFunc = new MenuFunctions(this);
	private DBUtils dbUtils = new DBUtils(this);
	
	@SuppressWarnings("deprecation")
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favoritestitle);
        
        FileUtils fileUtils = new FileUtils(this);
        fileUtils.checkIfMax();
        
        device.setHomeButtonEnabledForICS();
        
        chosenFavPerf = (TextView) findViewById(R.id.chosenFavPerformer);
        editFavTitle = (EditText) findViewById(R.id.editFavTitle);
        searchFavTitleListView = (ListView) findViewById(R.id.searchFavTitleListView);
        imgBtn = (ImageButton) findViewById(R.id.searchFavTitleBtn);
        
        gestureDetector = new GestureDetector(new MyGestureDetector(this));
        
        OnTouchListener myOnTouchListener = new MyOnTouchListener(gestureDetector);
        searchFavTitleListView.setOnTouchListener(myOnTouchListener);
        
        Intent i = getIntent();
        Bundle extras = i.getExtras();
        
        performerName = extras.getString("performerName");
        chosenFavPerf.setText(performerName);
        
        ArrayList<ArrayList<String>> listOfLists = dbUtils.addTitleFromBase(performerName);
        ArrayList<String> listTitle = listOfLists.get(0);
        ArrayList<String> listUrl = listOfLists.get(1);
        listOfChosenTitleFromBase = listTitle;
        listOfChosenUrlFromBase = listUrl;
        
        listAdapter = new ArrayAdapter<String>(this, R.layout.titlesfav, listOfChosenTitleFromBase);
        searchFavTitleListView.setAdapter(listAdapter);
        device.hideKeyboard(editFavTitle);
        searchFavTitleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	Intent i = new Intent(FavoritesTitleActivity.this, FavTabViewActivity.class);
            	Bundle bun = new Bundle();
            	bun.putString("performerName", performerName);
            	bun.putString("songTitle", listOfChosenTitleFromBase.get(position));
            	bun.putString("songUrl", listOfChosenUrlFromBase.get(position));
    			i.putExtras(bun);
    			startActivity(i);	
    			overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
           }
        } );
        
        LongClickOnItemToChangeRecordName longClickToChangeRecordName = 
        		new LongClickOnItemToChangeRecordName(this, listOfChosenTitleFromBase, listOfChosenUrlFromBase);
        searchFavTitleListView.setOnItemLongClickListener(longClickToChangeRecordName);
        
        InputFilter filter = new MyFilter();
        editFavTitle.setFilters(new InputFilter[]{filter});
        
        OnKeyListener myOnKeyListener = new MyOnKeyListener(imgBtn);
        editFavTitle.setOnKeyListener(myOnKeyListener);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    if(device.isTablet()) {
	    	inflater.inflate(R.menu.favtitlemenuiftablet, menu);
	    } else {
	    	inflater.inflate(R.menu.favtitle, menu);
	    }
	    return true;
	}
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case android.R.id.home:
	    	device.goHomeScreen();
	    	return true;
	    case R.id.delFromFavWithCheckBox:
	        startEditTitleActivity();
	        return true;
	    case R.id.addOwnRecord:
	    	menuFunc.buildAlertDialogNewTitle(performerName);
	    	return true;
	    case R.id.minmax:
	    	try {
				menuFunc.minMax();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    	return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
    
    private void startEditTitleActivity() {
    	ArrayList<String> listToEditTitles = new ArrayList<String>();
    	ArrayList<String> listToEditUrl = new ArrayList<String>();
    	if(listOfChosenTitleFromBase2.size() == 0) {
    		listToEditTitles = listOfChosenTitleFromBase;
    		listToEditUrl = listOfChosenUrlFromBase;
    	} else {
    		listToEditTitles = listOfChosenTitleFromBase2;
    		listToEditUrl = listOfChosenUrlFromBase2;
    	}
    	Intent i = new Intent(FavoritesTitleActivity.this, EditFavTitles.class);
		Bundle bun = new Bundle();
		bun.putStringArrayList("listOfTitles", listToEditTitles);
		bun.putStringArrayList("listOfUrl", listToEditUrl);
		i.putExtras(bun);
		startActivity(i);	
		overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
    }
	
	public void searchView(View v) {
		device.hideKeyboard(editFavTitle);
		
		ArrayList<ArrayList<String>> listOfLists = dbUtils.addTitleFromBase(performerName);
        ArrayList<String> listTitle = listOfLists.get(0);
        ArrayList<String> listUrl = listOfLists.get(1);
        listOfChosenTitleFromBase = listTitle;
        listOfChosenUrlFromBase = listUrl;
        
		String title = new String();
		listOfChosenTitleFromBase2 = new ArrayList<String>();
		listOfChosenUrlFromBase2 = new ArrayList<String>(); 
    	title = editFavTitle.getText().toString().toLowerCase();
    	if(title.length() > 0) {
    		if(title.charAt(0) == ' ')
    			Toast.makeText(getApplicationContext(), R.string.hintSpace, Toast.LENGTH_LONG).show();
    		else {
    			boolean checkContains;
    			for(int i = 0; i < listOfChosenTitleFromBase.size(); i++) {
    				checkContains = listOfChosenTitleFromBase.get(i).toLowerCase().contains(title);
    				if(checkContains == true) {
    					listOfChosenTitleFromBase2.add(listOfChosenTitleFromBase.get(i));
    					listOfChosenUrlFromBase2.add(listOfChosenUrlFromBase.get(i));
    				}
    			}
    			listAdapter = new ArrayAdapter<String>(this, R.layout.titlesfav, listOfChosenTitleFromBase2);
    			searchFavTitleListView.setAdapter(listAdapter);
    			searchFavTitleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    					Intent i = new Intent(FavoritesTitleActivity.this, FavTabViewActivity.class);
    					Bundle bun = new Bundle();
    					bun.putString("performerName", performerName);
    					bun.putString("songTitle", listOfChosenTitleFromBase2.get(position));
    					bun.putString("songUrl", listOfChosenUrlFromBase2.get(position));
    					i.putExtras(bun);
    					startActivity(i);	
    	    			overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
    				}
    			});
    			
    			LongClickOnItemToChangeRecordName longClickToChangeRecordName = 
    	        		new LongClickOnItemToChangeRecordName(this, listOfChosenTitleFromBase2, listOfChosenUrlFromBase2);
    	        searchFavTitleListView.setOnItemLongClickListener(longClickToChangeRecordName);
    		}
    		
    	} else {
    		listAdapter = new ArrayAdapter<String>(this, R.layout.titlesfav, listOfChosenTitleFromBase);
			searchFavTitleListView.setAdapter(listAdapter);
			searchFavTitleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Intent i = new Intent(FavoritesTitleActivity.this, FavTabViewActivity.class);
					Bundle bun = new Bundle();
					bun.putString("performerName", performerName);
					bun.putString("songTitle", listOfChosenTitleFromBase.get(position));
					bun.putString("songUrl", listOfChosenUrlFromBase.get(position));
					i.putExtras(bun);
					startActivity(i);	
	    			overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
				}
			} );
			
			LongClickOnItemToChangeRecordName longClickToChangeRecordName = 
	        		new LongClickOnItemToChangeRecordName(this, listOfChosenTitleFromBase, listOfChosenUrlFromBase);
	        searchFavTitleListView.setOnItemLongClickListener(longClickToChangeRecordName);
    	}
	}
	
	protected void onResume() {
		FileUtils fileUtils = new FileUtils(this);
        fileUtils.checkIfMax();
		super.onResume();
		ImageButton btn = (ImageButton) findViewById(R.id.searchFavTitleBtn);
		btn.performClick();
	}
	
	@Override
    public void onBackPressed() {
    	Intent intent = new Intent(this, FavoritesActivity.class);
    	startActivity(intent);
    	overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
