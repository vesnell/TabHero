package pl.tabhero.local;
 
import java.io.IOException;
import java.util.ArrayList;
import pl.tabhero.R;
import pl.tabhero.TabHero;
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
import android.view.View.OnTouchListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
 
public class FavoritesActivity extends Activity {
		
	private ListView searchListView;
	private EditText editFavPerformer;
	private ImageButton imgBtn;
	private ArrayList<String> listOfChosenPerfsFromBase;
	private GestureDetector gestureDetector;
	private MyTelephonyManager device = new MyTelephonyManager(this);
	private DBUtils dbUtils = new DBUtils(this);
	private MenuFunctions menuFunc = new MenuFunctions(this);
	
	@SuppressWarnings("deprecation")
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorites);
        
        FileUtils fileUtils = new FileUtils(this);
        fileUtils.checkIfMax();
        
        device.setHomeButtonEnabledForICS();
        
        editFavPerformer = (EditText) findViewById(R.id.editFavPerformer);
        searchListView = (ListView) findViewById(R.id.searchFavListView);
        imgBtn = (ImageButton) findViewById(R.id.searchFavBtn);
        
        gestureDetector = new GestureDetector(new MyGestureDetector(this));
        
        OnTouchListener myOnTouchListener = new MyOnTouchListener(gestureDetector);
        searchListView.setOnTouchListener(myOnTouchListener);
        
        device.hideKeyboard(editFavPerformer);
        
        listOfChosenPerfsFromBase = dbUtils.addPerfFromBase();
        createListOfPerfsAndHandleIt(listOfChosenPerfsFromBase);
        
        InputFilter filter = new MyFilter();
        editFavPerformer.setFilters(new InputFilter[]{filter});
        
        OnKeyListener myOnKeyListener = new MyOnKeyListener(imgBtn);
        editFavPerformer.setOnKeyListener(myOnKeyListener); 
    }
	
	private void createListOfPerfsAndHandleIt(final ArrayList<String> listOfChosenPerfsFromBase) {
		ArrayAdapter<String> listAdapter = 
				new ArrayAdapter<String>(this, R.layout.artistsfav, listOfChosenPerfsFromBase);
        searchListView.setAdapter(listAdapter);
        searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	Intent i = new Intent(FavoritesActivity.this, FavoritesTitleActivity.class);
            	Bundle bun = new Bundle();
            	bun.putString("performerName", listOfChosenPerfsFromBase.get(position));
    			i.putExtras(bun);
    			startActivityForResult(i, 500);
    			overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
           }
        });
        
        //***** change record name onLongClick *****
        LongClickOnItemToChangeRecordName longClickToChangeRecordName = 
        		new LongClickOnItemToChangeRecordName(this, listOfChosenPerfsFromBase);
        searchListView.setOnItemLongClickListener(longClickToChangeRecordName);
	}
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    if(device.isTablet()) {
	    	inflater.inflate(R.menu.favsearchmenuiftablet, menu);
	    } else {
	    	inflater.inflate(R.menu.faveeditcheckbox, menu);
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
	        menuFunc.startEditPerfActivity(listOfChosenPerfsFromBase);
	        return true;
	    case R.id.addOwnRecord:
	    	menuFunc.buildAlertDialogToAddOwnTab();
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
		String performer = new String();
    	performer = editFavPerformer.getText().toString().toLowerCase();
    	device.hideKeyboard(editFavPerformer);
    	
    	if(performer.length() > 0) {
    		if(performer.charAt(0) == ' ')
    			Toast.makeText(getApplicationContext(), R.string.hintSpace, Toast.LENGTH_LONG).show();
    		else {
    			boolean checkContains;
    			ArrayList<String> listOfFavPerfs = new ArrayList<String>();
    			for(String p : listOfChosenPerfsFromBase) {
    				checkContains = p.toLowerCase().contains(performer);
    				if(checkContains == true)
    					listOfFavPerfs.add(p);
    			}
    			createListOfPerfsAndHandleIt(listOfFavPerfs);
    		}
    	} else {
    		createListOfPerfsAndHandleIt(listOfChosenPerfsFromBase);
    	}
    }
    
    protected void onResume() {
    	FileUtils fileUtils = new FileUtils(this);
        fileUtils.checkIfMax();
		super.onResume();
		ImageButton btn = (ImageButton) findViewById(R.id.searchFavBtn);
		btn.performClick();
	}
    
    @Override
    public void onBackPressed() {
    	Intent intent = new Intent(this, TabHero.class);
    	startActivity(intent);
    	overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}