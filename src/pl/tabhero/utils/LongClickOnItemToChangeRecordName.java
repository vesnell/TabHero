package pl.tabhero.utils;

import java.util.ArrayList;
import pl.tabhero.core.MenuFunctions;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;

public class LongClickOnItemToChangeRecordName implements AdapterView.OnItemLongClickListener {
	
	private Context context;
	private ArrayList<String> listOfRecords;
	
	public LongClickOnItemToChangeRecordName(Context context, ArrayList<String> listOfRecords) {
		this.context = context;
		this.listOfRecords = listOfRecords;
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
		MenuFunctions menuFunc = new MenuFunctions(this.context);
		menuFunc.buildAlertDialogToChangeRecordName(this.listOfRecords.get(position), null);
		return false;
	}

}
