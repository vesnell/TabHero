package pl.tabhero;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class EditFavPerfs extends Activity{
	DBAdapter db = new DBAdapter(this);
	
	private Button btnDeleteFavPerfs;
	private ListView delFavListView;
	private ArrayAdapter<mItems> listAdapter;
	private ArrayList<mItems> planetList;
	//private mItems[] itemss;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.editfavperfs);
		
		btnDeleteFavPerfs = (Button) findViewById(R.id.deleteFavPerfs);
		delFavListView = (ListView) findViewById(R.id.delFavListView);
		
		Intent i = getIntent();
		Bundle extras = i.getExtras();
		
		//listAdapter = new ArrayAdapter<String>(this, R.layout.listwithcheckbox, listToEdit);
		delFavListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View item, int position, long id) {
				// TODO Auto-generated method stub
				mItems planet = listAdapter.getItem(position);
				planet.toggleChecked();
				SelectViewHolder viewHolder = (SelectViewHolder) item.getTag();
				viewHolder.getCheckBox().setChecked(planet.isChecked());
			}
			
		});
		
		//itemss = (mItems[]) getLastNonConfigurationInstance();
		
		ArrayList<String> listToEdit = extras.getStringArrayList("listOfPerformers");
		planetList = new ArrayList<mItems>();
		for(String perf : listToEdit) {
			planetList.add(new mItems(perf));
		}
		
		listAdapter = new SelectArralAdapter(this, planetList);
		delFavListView.setAdapter(listAdapter);
		
	}
	
	public void deleteAndBackToFavorites(View v) {
		db.open();
		for(mItems perf : planetList) {
			if(perf.isChecked() == true) {	
				db.deletePerf(perf.getName());
			}
		}
		Toast.makeText(getApplicationContext(), R.string.delPerfFromBase, Toast.LENGTH_LONG).show();
		db.close();
		Intent i = new Intent(EditFavPerfs.this, FavoritesActivity.class);
		startActivityForResult(i, 500);
		overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
	}
	
	 /** Holds planet data. */
	/*private static class mItems {
		private String name = "";
		private boolean checked = false;

		public mItems() {
		}

		public mItems(String name) {
			this.name = name;
		}

		public mItems(String name, boolean checked) {
			this.name = name;
			this.checked = checked;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public boolean isChecked() {
			return checked;
		}

		public void setChecked(boolean checked) {
			this.checked = checked;
		}

		public String toString() {
			return name;
		}

		public void toggleChecked() {
			checked = !checked;
		}
	} */
	
	/** Holds child views for one row. */
	/*private static class SelectViewHolder {
		private CheckBox checkBox;
		private TextView textView;

		public SelectViewHolder() {
		}

		public SelectViewHolder(TextView textView, CheckBox checkBox) {
			this.checkBox = checkBox;
			this.textView = textView;
		}

		public CheckBox getCheckBox() {
			return checkBox;
		}

		public void setCheckBox(CheckBox checkBox) {
			this.checkBox = checkBox;
		}

		public TextView getTextView() {
			return textView;
		}

		public void setTextView(TextView textView) {
			this.textView = textView;
		}
	} */
	
	/** Custom adapter for displaying an array of Planet objects. */
	/*private static class SelectArralAdapter extends ArrayAdapter<mItems> {
		private LayoutInflater inflater;

		public SelectArralAdapter(Context context, List<mItems> planetList) {
			super(context, R.layout.listwithcheckbox, R.id.rowDelArtistsView, planetList);
			// Cache the LayoutInflate to avoid asking for a new one each time.
			inflater = LayoutInflater.from(context);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// Planet to display
			mItems planet = (mItems) this.getItem(position);

			// The child views in each row.
			CheckBox checkBox;
			TextView textView;

			// Create a new row view
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.listwithcheckbox, null);

				// Find the child views.
				textView = (TextView) convertView.findViewById(R.id.rowDelArtistsView);
				checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);
				// Optimization: Tag the row with it's child views, so we don't
				// have to
				// call findViewById() later when we reuse the row.
				convertView.setTag(new SelectViewHolder(textView, checkBox));
				// If CheckBox is toggled, update the planet it is tagged with.
				checkBox.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						CheckBox cb = (CheckBox) v;
						mItems planet = (mItems) cb.getTag();
						planet.setChecked(cb.isChecked());
					}
				});
			}
			// Reuse existing row view
			else {
				// Because we use a ViewHolder, we avoid having to call
				// findViewById().
				SelectViewHolder viewHolder = (SelectViewHolder) convertView
						.getTag();
				checkBox = viewHolder.getCheckBox();
				textView = viewHolder.getTextView();
			}

			// Tag the CheckBox with the Planet it is displaying, so that we can
			// access the planet in onClick() when the CheckBox is toggled.
			checkBox.setTag(planet);
			// Display planet data
			checkBox.setChecked(planet.isChecked());
			textView.setText(planet.getName());
			return convertView;
		}
	} */
	
	//public Object onRetainNonConfigurationInstance() {
	//	return itemss;
	//}
	
	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, FavoritesActivity.class);
		startActivityForResult(intent, 500);
		overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
	}
	
	@Override
	protected void onPause() {
	    // TODO Auto-generated method stub
	    super.onPause();
	    finish();
	}

}
