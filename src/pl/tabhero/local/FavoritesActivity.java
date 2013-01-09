package pl.tabhero.local;
 
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import pl.tabhero.R;
import pl.tabhero.TabHero;
import pl.tabhero.db.DBAdapter;
import pl.tabhero.utils.MyFilter;
import pl.tabhero.utils.MyGestureDetector;
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
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
 
public class FavoritesActivity extends Activity {
	
	DBAdapter db = new DBAdapter(this); 
	
	private ListView searchListView;
	private EditText editFavPerformer;
	private ArrayList<String> listOfFavPerfs;
	private ArrayList<String> listOfChosenPerfsFromBase;
	private ArrayAdapter<String> listAdapter;
	private boolean max;
	private GestureDetector gestureDetector;
	private View.OnTouchListener gestureListener;
	
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorites);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            getActionBar().setHomeButtonEnabled(true);
        }
        
        editFavPerformer = (EditText) findViewById(R.id.editFavPerformer);
        searchListView = (ListView) findViewById(R.id.searchFavListView);
        
        gestureDetector = new GestureDetector(new MyGestureDetector(this));
		gestureListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (gestureDetector.onTouchEvent(event)) {
					return true;
				}
				return false;
			}
		};
        
        searchListView.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
        });
        
        listOfChosenPerfsFromBase = addPerfFromBase();

        listAdapter = new ArrayAdapter<String>(this, R.layout.artistsfav, listOfChosenPerfsFromBase);
        searchListView.setAdapter(listAdapter);
        hideKeyboard();
        searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	Intent i = new Intent(FavoritesActivity.this, FavoritesTitleActivity.class);
            	Bundle bun = new Bundle();
            	bun.putString("performerName", listOfChosenPerfsFromBase.get(position));
            	bun.putBoolean("max", max);
    			i.putExtras(bun);
    			startActivityForResult(i, 500);
    			overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
           }
        } );
        
        searchListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
				buildAlertDialogToChangePerf(listOfChosenPerfsFromBase.get(position));
				return false;
			}
        	
        });
        
        editFavPerformer.setFilters(new InputFilter[]{filter});
        editFavPerformer.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER) {
					searchView(v);
					return true;
				} else {
					return false;
				}
			}
		}); 
    }
    
	private InputFilter filter = new MyFilter();
    
    private void buildAlertDialogToAddOwnTab() {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText inputPerf = new EditText(this);
        inputPerf.setFilters(new InputFilter[]{filter});
        builder.setMessage(R.string.addOwnPerf);	
		builder.setView(inputPerf);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String newPerfName = inputPerf.getText().toString();
				buildAlertDialogNewTitle(newPerfName);
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
    
    private void buildAlertDialogNewTitle(final String newPerfName) {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText inputTitle = new EditText(this);
        //inputTitle.setKeyListener(DigitsKeyListener.getInstance("AĄBCĆDEĘFGHIJKLŁMNŃOÓPRSŚTUWYZŹŻaąbcćdeęfghijklłmnńoópqrsśtuvwxyzźż1234567890 ."));
        inputTitle.setFilters(new InputFilter[]{filter});
        builder.setMessage(getString(R.string.addOwnTitle) + " " + newPerfName);	
		builder.setView(inputTitle);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String newSongTitle = inputTitle.getText().toString();
				addToBaseNewRecord(newPerfName, newSongTitle);
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
    
    private void addToBaseNewRecord(String newPerfName, String newSongTitle) {
    	String songUrl = "http://www.chords.pl/chwyty/" + newPerfName + "/" + "USER," + newSongTitle;
    	db.open();
		if(isExistRecord(songUrl) == false) {
			db.insertRecord(newPerfName, newSongTitle, "", songUrl);
			Toast.makeText(getApplicationContext(), R.string.addToBaseSuccess, Toast.LENGTH_LONG).show();
		} else
			Toast.makeText(getApplicationContext(), R.string.recordExist, Toast.LENGTH_LONG).show();
		db.close();
    }
    
    public boolean isExistRecord(String songUrl) {
        Cursor c = db.getAllRecords();
        if (c.moveToFirst())
        {
            do {
            	if(songUrl.equals(c.getString(4))) {
            		return true;
            	}
            } while (c.moveToNext());
        }
		return false;
	}
    
    private void buildAlertDialogToChangePerf(final String oldPerfName) {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        input.setFilters(new InputFilter[]{filter}); 
		builder.setMessage(R.string.changePerf);
		input.setText(oldPerfName);
		builder.setView(input);

		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String newPerfName = input.getText().toString();
				changePerfName(newPerfName, oldPerfName);
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
    
    private void changePerfName(String newPerfName, String oldPerfName) {
    	ArrayList<Long> listId = new ArrayList<Long>();
    	ArrayList<String> listUrl = new ArrayList<String>();
    	String[] splitTab;
    	String newSongUrl = "";
    	int i = 0;
    	db.open();
        Cursor c = db.getRecordPerf(oldPerfName);
        if (c.moveToFirst())
        {
            do {
            	listId.add(c.getLong(0));
            	listUrl.add(c.getString(4));
            } while (c.moveToNext());
        }
        for(long id : listId) {
        	db.updatePerfName(newPerfName, id);
        	splitTab = listUrl.get(i).split("/");
        	splitTab[4] = newPerfName;
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
	    inflater.inflate(R.menu.faveeditcheckbox, menu);
	    return true;
	}
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case android.R.id.home:
	    	Intent intent = new Intent(this, TabHero.class);
	    	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    	startActivity(intent);
	    	overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
	    	return true;
	    case R.id.delFromFavWithCheckBox:
	        startEditActivity();
	        return true;
	    case R.id.addOwnRecord:
	    	buildAlertDialogToAddOwnTab();
	    	return true;
	    case R.id.minmax:
	    	minMax();
	    	return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
    
    private void minMax() {
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
	}
    
    private void startEditActivity() {
    	ArrayList<String> listToEdit = new ArrayList<String>();
    	listToEdit = listOfChosenPerfsFromBase;
    	Intent i = new Intent(FavoritesActivity.this, EditFavPerfs.class);
		Bundle bun = new Bundle();
		bun.putStringArrayList("listOfPerformers", listToEdit);
		i.putExtras(bun);
		startActivityForResult(i, 500);
		overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
    }
    
	public void searchView(View v) {
    	hideKeyboard();
    	listOfChosenPerfsFromBase.clear();
    	listOfChosenPerfsFromBase = addPerfFromBase();
    	listOfFavPerfs = addPerfFromBase();
    	String performer = new String();
    	performer = editFavPerformer.getText().toString().toLowerCase();
    	
    	if(performer.length() > 0) {
    		if(performer.charAt(0) == ' ')
    			Toast.makeText(getApplicationContext(), R.string.hintSpace, Toast.LENGTH_LONG).show();
    		else {
    			boolean checkContains;
    			listOfChosenPerfsFromBase.clear();
    			for(String p : listOfFavPerfs) {
    				checkContains = p.toLowerCase().contains(performer);
    				if(checkContains == true)
    					listOfChosenPerfsFromBase.add(p);
    			}
    			listOfFavPerfs.clear();
    			listAdapter = new ArrayAdapter<String>(this, R.layout.artistsfav, listOfChosenPerfsFromBase);
    			searchListView.setAdapter(listAdapter);
    			searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    					Intent i = new Intent(FavoritesActivity.this, FavoritesTitleActivity.class);
    					Bundle bun = new Bundle();
    					bun.putString("performerName", listOfChosenPerfsFromBase.get(position));
    					bun.putBoolean("max", max);
    					i.putExtras(bun);
    					startActivityForResult(i, 500);
    	    			overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);	
    				}
    			} );
    			
    			searchListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
    				public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
    					buildAlertDialogToChangePerf(listOfChosenPerfsFromBase.get(position));
    					return false;
    				}
    	        	
    	        });

    		}
    	} else {
			listAdapter = new ArrayAdapter<String>(this, R.layout.artistsfav, listOfChosenPerfsFromBase);
			searchListView.setAdapter(listAdapter);
			searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Intent i = new Intent(FavoritesActivity.this, FavoritesTitleActivity.class);
					Bundle bun = new Bundle();
					bun.putString("performerName", listOfChosenPerfsFromBase.get(position));
					bun.putBoolean("max", max);
					i.putExtras(bun);
					startActivityForResult(i, 500);
	    			overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
				}
			} );
			
			searchListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
				public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
					buildAlertDialogToChangePerf(listOfChosenPerfsFromBase.get(position));
					return false;
				}
	        	
	        });

    	}
    }
    
    public ArrayList<String> addPerfFromBase() {
    	Comparator<String> comparator = new PolishComparator();
    	ArrayList<String> list = new ArrayList<String>();
    	db.open();
        Cursor c = db.getAllRecords();
        if (c.moveToFirst())
        {
            do {
            	if(!list.contains(c.getString(1)))
            		list.add(c.getString(1));
            } while (c.moveToNext());
        }
        db.close();
        Collections.sort(list, comparator);
        return list;      
    } 
    
    private void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editFavPerformer.getWindowToken(), 0);
	}
    
    protected void onResume() {
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