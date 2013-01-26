package pl.tabhero;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import pl.tabhero.core.MenuFunctions;
import pl.tabhero.local.FavoritesActivity;
import pl.tabhero.net.SearchActivity;
import pl.tabhero.utils.FileUtils;
import pl.tabhero.utils.MyGestureDetector;
import pl.tabhero.utils.MyTelephonyManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class TabHero extends Activity {

	private Button btnOnline;
	private Button btnOnBase;
	private GestureDetector gestureDetector;
	private String FILE_NAME;
	private String DEFAULT_CONFIG;
	
    @SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        FILE_NAME = getString(R.string.configNameFile);
        DEFAULT_CONFIG = getString(R.string.configDefault);
        
        setContentView(R.layout.main);
        
        btnOnline = (Button) findViewById(R.id.online);
        btnOnBase = (Button) findViewById(R.id.favorites);
        
        FileUtils fileUtils = new FileUtils(this);
        File outDir = new File(fileUtils.dir);
        if(!outDir.isDirectory()) {
        	outDir.mkdir();
        }
        try {
        	if(!outDir.isDirectory()) {
        		throw new IOException(getString(R.string.createDirectoryError) + getPackageName() + "." + getString(R.string.sdcardMountError));
        	}
        	File file = new File(fileUtils.dir + File.separator + FILE_NAME);
        	if(!file.isFile()) {
        		Writer writer;
        		File outputFile = new File(outDir, FILE_NAME);
        		MenuFunctions menuFunc = new MenuFunctions(this);
        		writer = new BufferedWriter(new FileWriter(outputFile));
        		writer.write(DEFAULT_CONFIG);
        		writer.close();
        		menuFunc.firstRun();
        	} else {
        		String configText = fileUtils.readConfig(file);
        		fileUtils.setIfMax(configText);
        	}
		} catch (IOException e) {
			Toast.makeText(getApplicationContext(), R.string.configReadWriteError, Toast.LENGTH_LONG).show();
		}
        
		gestureDetector = new GestureDetector(new MyGestureDetector(this));
		final MyGestureDetector myGestureDetector = new MyGestureDetector(this);
		
		btnOnline.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				myGestureDetector.onClickStartRightActivity(SearchActivity.class);
			}
		});
        
        btnOnBase.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				myGestureDetector.onClickStartLeftActivity(FavoritesActivity.class);
			}
		});
    }

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (gestureDetector.onTouchEvent(event))
			return true;
		else
			return false;
	}
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    MyTelephonyManager device = new MyTelephonyManager(this);
	    if(device.isTablet()) {
	    	inflater.inflate(R.menu.mainiftablet, menu);
	    } else {
	    	inflater.inflate(R.menu.main, menu);
	    }
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		MenuFunctions menuFunc = new MenuFunctions(this);
	    switch (item.getItemId()) {
	    case R.id.info:
	        menuFunc.showInfo();
	        return true;
	    case R.id.minmax:
	    	try {
				menuFunc.minMax();
			} catch (IOException e) {
				Toast.makeText(getApplicationContext(), R.string.minmaxError, Toast.LENGTH_LONG).show();
			}
	    	return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
    
    @Override
    public void onResume() {
    	FileUtils fileUtils = new FileUtils(this);
        fileUtils.checkIfMax();
    	super.onResume();
    }
    
    @Override
    public void onBackPressed() {
    	TabHero.this.finish();
    	super.onBackPressed();
    	overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
    }
}
