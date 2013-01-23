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
	private String performerName;
	private GestureDetector gestureDetector;
	private MyTelephonyManager device = new MyTelephonyManager(this);
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
        createListOfTitlesAndHandleIt(listTitle, listUrl);
        
        device.hideKeyboard(editFavTitle);
        
        InputFilter filter = new MyFilter();
        editFavTitle.setFilters(new InputFilter[]{filter});
        
        OnKeyListener myOnKeyListener = new MyOnKeyListener(imgBtn);
        editFavTitle.setOnKeyListener(myOnKeyListener);
	}
	
	private void createListOfTitlesAndHandleIt(final ArrayList<String> listOfTitles, final ArrayList<String> listOfUrls) {
		ArrayAdapter<String> listAdapter = 
				new ArrayAdapter<String>(this, R.layout.titlesfav, listOfTitles);
        searchFavTitleListView.setAdapter(listAdapter);
        searchFavTitleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	Intent i = new Intent(FavoritesTitleActivity.this, FavTabViewActivity.class);
            	Bundle bun = new Bundle();
            	bun.putString("performerName", performerName);
            	bun.putString("songTitle", listOfTitles.get(position));
            	bun.putString("songUrl", listOfUrls.get(position));
    			i.putExtras(bun);
    			startActivity(i);	
    			overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
           }
        } );
        
        LongClickOnItemToChangeRecordName longClickToChangeRecordName = 
        		new LongClickOnItemToChangeRecordName(this, listOfTitles, listOfUrls);
        searchFavTitleListView.setOnItemLongClickListener(longClickToChangeRecordName);
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
    	MenuFunctions menuFunc = new MenuFunctions(this);
	    switch (item.getItemId()) {
	    case android.R.id.home:
	    	device.goHomeScreen();
	    	return true;
	    case R.id.delFromFavWithCheckBox:
	        menuFunc.startEditTitleActivity(dbUtils.addTitleFromBase(performerName).get(0), dbUtils.addTitleFromBase(performerName).get(1));
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
	
	public void searchView(View v) {
		String title = new String();
		title = editFavTitle.getText().toString().toLowerCase();
		ArrayList<ArrayList<String>> listOfLists = dbUtils.addTitleFromBase(performerName);
        ArrayList<String> listTitle = listOfLists.get(0);
        ArrayList<String> listUrl = listOfLists.get(1);
        device.hideKeyboard(editFavTitle);

    	if(title.length() > 0) {
    		if(title.charAt(0) == ' ')
    			Toast.makeText(getApplicationContext(), R.string.hintSpace, Toast.LENGTH_LONG).show();
    		else {
    			boolean checkContains;
    			ArrayList<String> listOfFavTitles = new ArrayList<String>();
    			ArrayList<String> listOfFavUrls = new ArrayList<String>();
    			for(int i = 0; i < listTitle.size(); i++) {
    				checkContains = listTitle.get(i).toLowerCase().contains(title);
    				if(checkContains == true) {
    					listOfFavTitles.add(listTitle.get(i));
    					listOfFavUrls.add(listUrl.get(i));
    				}
    			}
    			createListOfTitlesAndHandleIt(listOfFavTitles, listOfFavUrls);
    		}
    		
    	} else {
    		createListOfTitlesAndHandleIt(listTitle, listUrl);
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
