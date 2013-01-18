package pl.tabhero.local;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import pl.tabhero.R;
import pl.tabhero.TabHero;
import pl.tabhero.core.MenuFunctions;
import pl.tabhero.db.DBAdapter;
import pl.tabhero.utils.FileUtils;
import pl.tabhero.utils.MyFilter;
import pl.tabhero.utils.MyGestureDetector;
import pl.tabhero.utils.MyOnKeyListener;
import pl.tabhero.utils.MyOnTouchListener;
import pl.tabhero.utils.MyTelephonyManager;
import pl.tabhero.utils.PolishComparator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FavoritesTitleActivity extends Activity {
	
	DBAdapter db = new DBAdapter(this);
	
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
	private static final String CONFIG = "config.txt";
	//private boolean max;
	private String performerName;
	private GestureDetector gestureDetector;
	private MyTelephonyManager device = new MyTelephonyManager(this);
	
	@SuppressWarnings("deprecation")
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favoritestitle);
        
        FileUtils fileUtils = new FileUtils(this);
        File file = new File(fileUtils.dir + File.separator + CONFIG);
    	if(file.isFile()) {
    		String configText = fileUtils.readConfig(file);
    		fileUtils.setIfMax(configText);
    	}
        
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
        
        /*max = extras.getBoolean("max");
		if(max) {
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}*/
        
        performerName = extras.getString("performerName");
        chosenFavPerf.setText(performerName);
        
        ArrayList<ArrayList<String>> listOfLists = addTitleFromBase(performerName);
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
            	//bun.putBoolean("max", max);
    			i.putExtras(bun);
    			startActivityForResult(i, 500);	
    			overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
           }
        } );
        
        searchFavTitleListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
				buildAlertDialogToChangeSong(listOfChosenTitleFromBase.get(position), listOfChosenUrlFromBase.get(position));
				return false;
			}
        	
        });
        
        editFavTitle.setFilters(new InputFilter[]{filter});
        
        OnKeyListener myOnKeyListener = new MyOnKeyListener(imgBtn);
        editFavTitle.setOnKeyListener(myOnKeyListener);
	}
	
	private InputFilter filter = new MyFilter();
	
	private void buildAlertDialogToChangeSong(final String oldSongTitle, final String url) {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        input.setFilters(new InputFilter[]{filter}); 
		builder.setMessage(R.string.changeTitle);	
		input.setText(oldSongTitle);
		builder.setView(input);

		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String newSongTitle = input.getText().toString();
				changeSongTitle(newSongTitle, url);
				onResume();
				dialog.dismiss();
			}
		});

		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.dismiss();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
    }
    
    private void changeSongTitle(String newSongTitle, String url) {
    	ArrayList<Long> listId = new ArrayList<Long>();
    	ArrayList<String> listUrl = new ArrayList<String>();
    	String[] splitTab;
    	String[] splitTab2;
    	String newSongUrl = "";
    	int i = 0;
    	db.open();
        Cursor c = db.getRecordUrl(url);
        if (c.moveToFirst())
        {
            do {
            	listId.add(c.getLong(0));
            	listUrl.add(c.getString(4));
            } while (c.moveToNext());
        }
        for(long id : listId) {
        	db.updateSongTitle(newSongTitle, id);
        	splitTab = listUrl.get(i).split("/");
        	splitTab2 = splitTab[5].split(",");
        	splitTab2[1] = newSongTitle;
        	splitTab[5] = splitTab2[0] + "," + splitTab2[1];
        	for(int j = 0; j < 5; j++)
        		newSongUrl += splitTab[j] + "/";
        	newSongUrl += splitTab[5];
        	db.updateSongUrl(newSongUrl, id);
        	i++;
        	Log.d("NOWY URL", newSongUrl);
        	newSongUrl = "";
        }
		db.close();
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
	        startEditActivity();
	        return true;
	    case R.id.minmax:
	    	try {
				menuFunc.minMax();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
    
    /*private void minMax() {
    	boolean fullScreen = (getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) != 0;
       if(fullScreen) {
    	   getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    	   getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
    	   max = false;
        }
        else {
        	getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        	max = true;
        }
	}*/
    
    private void startEditActivity() {
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
		startActivityForResult(i, 500);	
		overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
    }
	
	public void searchView(View v) {
		device.hideKeyboard(editFavTitle);
		
		ArrayList<ArrayList<String>> listOfLists = addTitleFromBase(performerName);
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
    					//bun.putBoolean("max", max);
    					i.putExtras(bun);
    					startActivityForResult(i, 500);	
    	    			overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
    				}
    			});
    			
    			searchFavTitleListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
    				public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
    					buildAlertDialogToChangeSong(listOfChosenTitleFromBase2.get(position), listOfChosenUrlFromBase2.get(position));
    					return false;
    				}
    	        	
    	        });
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
					//bun.putBoolean("max", max);
					i.putExtras(bun);
					startActivityForResult(i, 500);	
	    			overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
				}
			} );
			
			searchFavTitleListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
				public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
					buildAlertDialogToChangeSong(listOfChosenTitleFromBase.get(position), listOfChosenUrlFromBase.get(position));
					return false;
				}
	        	
	        });
    	}
		
	}
	
	public ArrayList<ArrayList<String>> addTitleFromBase(String perfName) {
		Comparator<String> comparator = new PolishComparator();
    	Map<String, String> mapTitlesUrls = new TreeMap<String, String>(comparator);
    	db.open();
        Cursor c = db.getRecordPerf(perfName);
        if (c.moveToFirst())
        {
            do {
            	mapTitlesUrls.put(c.getString(2), c.getString(4));
            } while (c.moveToNext());
        }
        db.close();
        ArrayList<String> listTitles = new ArrayList<String>(mapTitlesUrls.keySet());
		ArrayList<String> listUrl = new ArrayList<String>(mapTitlesUrls.values());
    	ArrayList<ArrayList<String>> listOfList = new ArrayList<ArrayList<String>>();
        listOfList.add(listTitles);
        listOfList.add(listUrl);
        return listOfList;      
    }
	
	
	protected void onResume() {
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
