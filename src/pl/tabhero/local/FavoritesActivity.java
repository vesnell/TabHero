package pl.tabhero.local;

import java.util.ArrayList;
import java.util.Locale;
import pl.tabhero.R;
import pl.tabhero.TabHero;
import pl.tabhero.core.MenuFunctions;
import pl.tabhero.db.DBUtils;
import pl.tabhero.utils.FileUtils;
import pl.tabhero.utils.LongClickOnItemToChangeRecordName;
import pl.tabhero.utils.MyFilter;
import pl.tabhero.utils.MyGestureDetector;
import pl.tabhero.utils.MyLayout;
import pl.tabhero.utils.MyOnKeyListener;
import pl.tabhero.utils.MyOnTouchListener;
import pl.tabhero.utils.MyTelephonyManager;
import pl.tabhero.utils.MyTextWatcher;
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
    private GestureDetector gestureDetector;
    private MyTelephonyManager device = new MyTelephonyManager(this);
    private DBUtils dbUtils = new DBUtils(this);
    private MenuFunctions menuFunc;
    private boolean onEditClick = false;
    private ArrayList<String> listToEdit = new ArrayList<String>();

    @SuppressWarnings("deprecation")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        menuFunc = new MenuFunctions(this);

        FileUtils fileUtils = new FileUtils(this);
        fileUtils.fillUIFromPreferences();

        device.setHomeButtonEnabledForICS();

        editFavPerformer = (EditText) findViewById(R.id.editPerformer);
        searchListView = (ListView) findViewById(R.id.searchListView);
        imgBtn = (ImageButton) findViewById(R.id.searchBtn);

        gestureDetector = new GestureDetector(new MyGestureDetector(this));

        OnTouchListener myOnTouchListener = new MyOnTouchListener(gestureDetector);
        searchListView.setOnTouchListener(myOnTouchListener);

        device.hideKeyboard(editFavPerformer);

        ArrayList<String> listOfChosenPerfsFromBase = dbUtils.addPerfFromBase();
        if (listOfChosenPerfsFromBase.size() == 0) {
            Toast.makeText(getApplicationContext(), R.string.favoritesEmpty, Toast.LENGTH_LONG).show();
        } else {
            createListOfPerfsAndHandleIt(listOfChosenPerfsFromBase);
        }

        InputFilter filter = new MyFilter();
        editFavPerformer.setFilters(new InputFilter[]{filter});

        OnKeyListener myOnKeyListener = new MyOnKeyListener(imgBtn);
        editFavPerformer.setOnKeyListener(myOnKeyListener);
        
        MyTextWatcher myTextWatcher = new MyTextWatcher(searchListView, imgBtn);
        editFavPerformer.addTextChangedListener(myTextWatcher);
        
        new MyLayout(this).customizeFastScroller(searchListView);
    }

    public void searchView(View v) {
        ArrayList<String> listOfPerfs = dbUtils.addPerfFromBase();
        String performer = new String();
        performer = editFavPerformer.getText().toString().toLowerCase(Locale.getDefault());

        if (performer.length() > 0) {
            if (performer.charAt(0) == ' ') {
                Toast.makeText(getApplicationContext(), R.string.hintSpace, Toast.LENGTH_LONG).show();
            } else {
                boolean checkContains;
                ArrayList<String> listOfFavPerfs = new ArrayList<String>();
                for (String p : listOfPerfs) {
                    checkContains = p.toLowerCase(Locale.getDefault()).contains(performer);
                    if (checkContains) {
                        listOfFavPerfs.add(p);
                    }
                }
                createListOfPerfsAndHandleIt(listOfFavPerfs);
                listToEdit = listOfFavPerfs;
                onEditClick = true;
            }
        } else {
            createListOfPerfsAndHandleIt(listOfPerfs);
            onEditClick = false;
        }
    }

    private void createListOfPerfsAndHandleIt(final ArrayList<String> listOfPerfs) {
        ArrayAdapter<String> listAdapter =
                new ArrayAdapter<String>(this, R.layout.artistsfav, listOfPerfs);
        searchListView.setAdapter(listAdapter);
        searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(FavoritesActivity.this, FavoritesTitleActivity.class);
                Bundle bun = new Bundle();
                bun.putString("performerName", listOfPerfs.get(position));
                i.putExtras(bun);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
        LongClickOnItemToChangeRecordName longClickToChangeRecordName =
                new LongClickOnItemToChangeRecordName(this, listOfPerfs, null);
        searchListView.setOnItemLongClickListener(longClickToChangeRecordName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (device.isTablet()) {
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
            if (onEditClick) {
                menuFunc.startEditPerfActivity(listToEdit);
            } else {
                menuFunc.startEditPerfActivity(dbUtils.addPerfFromBase());
            }
            return true;
        case R.id.addOwnRecord:
            menuFunc.buildAlertDialogToAddOwnTab();
            return true;
        case R.id.minmax:
            menuFunc.minMax();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    protected void onResume() {
        FileUtils fileUtils = new FileUtils(this);
        fileUtils.fillUIFromPreferences();
        super.onResume();
        ImageButton btn = (ImageButton) findViewById(R.id.searchBtn);
        btn.performClick();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, TabHero.class);
        FavoritesActivity.this.finish();
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
