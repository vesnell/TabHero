package pl.tabhero.utils;

import java.util.ArrayList;
import pl.tabhero.core.MenuFunctions;
import pl.tabhero.local.FavoritesActivity;
import pl.tabhero.local.FavoritesTitleActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.view.View;
import android.widget.AdapterView;

public class LongClickOnItemToChangeRecordName implements AdapterView.OnItemLongClickListener {
	
	private Context context;
	private Activity activity;
	private ArrayList<String> listOfRecords;
	private ArrayList<String> listOfUrls;
	private String className;
	private static final String FAV_PERFS_VIEW = FavoritesActivity.class.getSimpleName();
	private static final String FAV_TITLE_VIEW = FavoritesTitleActivity.class.getSimpleName();
	
	public LongClickOnItemToChangeRecordName(Context context, ArrayList<String> listOfRecords, ArrayList<String> listOfUrls) {
		this.context = context;
		this.activity = (Activity) context;
		this.className = this.activity.getClass().getSimpleName();
		this.listOfRecords = listOfRecords;
		this.listOfUrls = listOfUrls;
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
		Vibrator v = (Vibrator) this.context.getSystemService(Context.VIBRATOR_SERVICE);
		v.vibrate(100);
		MenuFunctions menuFunc = new MenuFunctions(this.context);
		if(className.equals(FAV_PERFS_VIEW)) {
			menuFunc.buildAlertDialogToChangeRecordName(this.listOfRecords.get(position), null);
		} else if(className.equals(FAV_TITLE_VIEW)) {
			menuFunc.buildAlertDialogToChangeRecordName(this.listOfRecords.get(position), this.listOfUrls.get(position));
		}
		return false;
	}

}
