package pl.tabhero;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class EditFavTitles extends Activity{
	
DBAdapter db = new DBAdapter(this);
	
	private Button btnDeleteFavPerfs;
	private ListView delFavListView;
	private ArrayAdapter<mItems> listAdapter;
	private ArrayList<mItems> planetList;
	private ArrayList<String> listToEditTitle;
	private ArrayList<String> listToEditUrl;
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.editfavperfs);
        
        btnDeleteFavPerfs = (Button) findViewById(R.id.deleteFavPerfs);
		delFavListView = (ListView) findViewById(R.id.delFavListView);
		
		Intent i = getIntent();
		Bundle extras = i.getExtras();
		
		delFavListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View item, int position, long id) {
				// TODO Auto-generated method stub
				mItems planet = listAdapter.getItem(position);
				planet.toggleChecked();
				SelectViewHolder viewHolder = (SelectViewHolder) item.getTag();
				viewHolder.getCheckBox().setChecked(planet.isChecked());
			}
			
		});
		
		listToEditTitle = extras.getStringArrayList("listOfTitles");
		listToEditUrl = extras.getStringArrayList("listOfUrl");
		Log.d("SIZE EDIT URL", Integer.toString(listToEditUrl.size()));
		
		planetList = new ArrayList<mItems>();
		for(String perf : listToEditTitle) {
			planetList.add(new mItems(perf));
		}
		
		listAdapter = new SelectArralAdapter(this, planetList);
		delFavListView.setAdapter(listAdapter);
	}
	
	public void deleteAndBackToFavorites(View v) {
		db.open();
		for(mItems title : planetList) {
			if(title.isChecked() == true) {	
				//db.deletePerf(title.getName());
				for(int i = 0; i < listToEditTitle.size(); i++) {
					Log.d("TITLE EDIT", listToEditTitle.get(i));
					Log.d("URL EDIT", listToEditUrl.get(i));
					if(title.getName().equals(listToEditTitle.get(i))) {
						db.deleteTab(listToEditUrl.get(i));
					}
				}
			}
		}
		Toast.makeText(getApplicationContext(), R.string.delTitleFromBase, Toast.LENGTH_LONG).show();
		db.close();
		Intent i = new Intent(EditFavTitles.this, FavoritesTitleActivity.class);
		startActivity(i);
	}

}
