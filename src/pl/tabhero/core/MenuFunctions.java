package pl.tabhero.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import pl.tabhero.R;
import pl.tabhero.db.DBUtils;
import pl.tabhero.local.EditFavPerfs;
import pl.tabhero.local.FavoritesActivity;
import pl.tabhero.local.FavoritesTitleActivity;
import pl.tabhero.utils.FileUtils;
import pl.tabhero.utils.MyFilter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

public class MenuFunctions {
	
	private Context context;
	private Activity activity;
	private DBUtils dbUtils;
	private static final String CONFIG = "config.txt";
	private String className;
	private static final String FAV_PERF_VIEW = FavoritesActivity.class.getSimpleName();
	private static final String FAV_TITLE_VIEW = FavoritesTitleActivity.class.getSimpleName();
	
	public MenuFunctions(Context context) {
		this.context = context;
		this.activity = (Activity) context;
		this.dbUtils = new DBUtils(this.context);
		this.className = this.activity.getClass().getSimpleName();
	}
	
	public void addToFav(String performer, String title, String tab, String songUrl) {
		if(this.dbUtils.isExistRecordByUrl(songUrl) == false) {
			if(this.dbUtils.isIdExistByUrl(songUrl)) {
				buildAlertDialogToAddTabWhithSameId(performer, title, tab, songUrl);
			} else { 
				this.dbUtils.addTab(performer, title, tab, songUrl);
			}
		} else {
			Toast.makeText(this.context.getApplicationContext(), R.string.recordExist, Toast.LENGTH_LONG).show();
		}
	}
	
	public void delFromFav(final String songUrl) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
	    //builder.setTitle("Confirm");
	    builder.setMessage(R.string.areYouSure);
	    builder.setNegativeButton(R.string.yes, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	        	dbUtils.deleteTab(songUrl);
	            dialog.dismiss();
	            Intent i = new Intent(context, FavoritesTitleActivity.class);
	    		context.startActivity(i);
	    		context.startActivity(i);
	    		activity.overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
	        }
	    });
	    builder.setPositiveButton(R.string.no, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	        	Toast.makeText(context.getApplicationContext(), R.string.notDelFromBase, Toast.LENGTH_LONG).show();
	            dialog.dismiss();
	        }
	    });
	    AlertDialog alert = builder.create();
	    alert.show();
	}
	
	public void editTab(String tablature, String songUrl) {
		Writer writer;
		FileUtils fileUtils = new FileUtils(this.context);
        fileUtils.makePaths(songUrl);
        fileUtils.makeFiles();
	    if (!fileUtils.outDir.isDirectory()) {
	      fileUtils.outDir.mkdir();
	    }
	    try {
	      if (!fileUtils.outDir.isDirectory()) {
	        throw new IOException(this.context.getString(R.string.createDirectoryError) + this.context.getPackageName() + "." + this.context.getString(R.string.sdcardMountError));
	      }
	      File outputFile = new File(fileUtils.outDir, fileUtils.fileName);
	      writer = new BufferedWriter(new FileWriter(outputFile));
	      writer.write(tablature);
	      Toast.makeText(this.context.getApplicationContext(), this.context.getString(R.string.sdcardWriteOK) + outputFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
	      writer.close();
	    } catch (IOException e) {
	      Toast.makeText(this.context.getApplicationContext(), e.getMessage() + this.context.getString(R.string.sdcardWriteError), Toast.LENGTH_LONG).show();
	    }
	}
	
	public void openWebBrowser(String performer, String title) {
		String perf = performer.replaceAll(" ", "%20");
		String tit = title.replaceAll(" ", "%20");
		String question = "http://www.google.com/search?q=" + perf + "%20" + tit + "%20lyrics";
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(question));
		this.context.startActivity(browserIntent);
		this.activity.overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
	}

	private void buildAlertDialogToAddTabWhithSameId(final String performer, final String title, final String tab, final String songUrl) {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
		builder.setMessage(R.string.tabExist);
		builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dbUtils.addTab(performer, title, tab, songUrl);
				dialog.dismiss();
			}
		});

		builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Toast.makeText(context.getApplicationContext(), R.string.notAddTab, Toast.LENGTH_LONG).show();
				dialog.dismiss();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
    }
	
	public void minMax() throws IOException {
		String maxForConfig;
		boolean fullScreen = (this.activity.getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) != 0;
		if(fullScreen) {
    	   this.activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    	   this.activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
    	   maxForConfig = "MIN";
        } else {
        	this.activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        	this.activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        	maxForConfig = "MAX";
        }
		FileUtils fileUtils = new FileUtils(this.context);
		String configPath = fileUtils.dir + File.separator + CONFIG;
		File file = new File(configPath);
		if(file.isFile()) {
			String text = fileUtils.readConfig(file);
			String textSize = text.split(",")[1];
			fileUtils.writeForConfig(file, maxForConfig, textSize);
		} else {
			Toast.makeText(this.context.getApplicationContext(), this.context.getString(R.string.configReadError), Toast.LENGTH_LONG).show();
		}
	}
	
	//local activties
	
	private InputFilter filter = new MyFilter();
    
    public void buildAlertDialogToAddOwnTab() {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
        final EditText inputPerf = new EditText(this.context);
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
    
    public void buildAlertDialogNewTitle(final String newPerfName) {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
        final EditText inputTitle = new EditText(this.context);
        inputTitle.setFilters(new InputFilter[]{filter});
        builder.setMessage(this.context.getString(R.string.addOwnTitle) + " " + newPerfName);	
		builder.setView(inputTitle);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String newSongTitle = inputTitle.getText().toString();
				dbUtils.addToBaseNewRecord(newPerfName, newSongTitle);
				Intent refresh = null;
				if(className.equals(FAV_PERF_VIEW)) {
					refresh = new Intent(activity, FavoritesActivity.class);
				} else if(className.equals(FAV_TITLE_VIEW)) {
					refresh = new Intent(activity, FavoritesTitleActivity.class);
				}
				activity.startActivity(refresh);
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
    
    public void startEditPerfActivity(ArrayList<String> listToEdit) {
    	Intent i = new Intent(this.activity, EditFavPerfs.class);
		Bundle bun = new Bundle();
		bun.putStringArrayList("listOfPerformers", listToEdit);
		i.putExtras(bun);
		this.context.startActivity(i);
		this.activity.overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
    }
    
    public void buildAlertDialogToChangeRecordName(final String oldRecordName, final String url) {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
        final EditText input = new EditText(this.context);
        input.setFilters(new InputFilter[]{filter});
        if(className.equals(FAV_PERF_VIEW)) {
        	builder.setMessage(R.string.changePerf);
		} else if(className.equals(FAV_TITLE_VIEW)) {
			builder.setMessage(R.string.changeTitle);
		}
		input.setText(oldRecordName);
		builder.setView(input);

		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String newRecordName = input.getText().toString();
				if(className.equals(FAV_PERF_VIEW)) {
					dbUtils.changePerfName(newRecordName, oldRecordName);
					refresh();
				} else if(className.equals(FAV_TITLE_VIEW)) {
					dbUtils.changeSongTitle(newRecordName, url);
					refresh();
				}
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
    
    public void refresh() {
    	//this.activity.finish();
    	Intent refresh = null;
		if(className.equals(FAV_PERF_VIEW)) {
			refresh = new Intent(this.context, FavoritesActivity.class);
		} else if(className.equals(FAV_TITLE_VIEW)) {
			refresh = new Intent(this.context, FavoritesTitleActivity.class);
		}
		this.activity.startActivity(refresh);
    }
}
