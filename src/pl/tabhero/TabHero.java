package pl.tabhero;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import pl.tabhero.local.FavoritesActivity;
import pl.tabhero.net.SearchActivity;
import pl.tabhero.utils.FileUtils;
import pl.tabhero.utils.MyGestureDetector;
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

public class TabHero extends Activity {

	private Button btnOnline;
	private Button btnOnBase;
	private GestureDetector gestureDetector;
	private static final String DEFAULT_CONFIG = "MIN,12";
	private static final String CONFIG = "config.txt";
	
    @SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
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
        	String fileName = CONFIG;
        	File file = new File(fileUtils.dir + File.separator + fileName);
        	if(!file.isFile()) {
        		Writer writer;
        		File outputFile = new File(outDir, fileName);
        		writer = new BufferedWriter(new FileWriter(outputFile));
        		writer.write(DEFAULT_CONFIG);
        		writer.close();
        		showInfo();
        	} else {
        		String configText = fileUtils.readConfig(file);
        		fileUtils.setIfMax(configText);
        	}
		} catch (IOException e) {
			e.printStackTrace();
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
	    inflater.inflate(R.menu.main, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.info:
	        showInfo();
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	private void showInfo() {
		AlertDialog.Builder builder = new AlertDialog.Builder(TabHero.this);
	    builder.setTitle(R.string.info);
	    builder.setMessage(R.string.message_info);
	    builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	            dialog.dismiss();
	        }
	    });
	    builder.setPositiveButton(R.string.help, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(TabHero.this, HelpActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
				dialog.dismiss();
			}
		});
	    AlertDialog alert = builder.create();
	    alert.show();
	}
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
