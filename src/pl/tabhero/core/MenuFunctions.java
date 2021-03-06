package pl.tabhero.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import pl.tabhero.HelpActivity;
import pl.tabhero.R;
import pl.tabhero.db.DBUtils;
import pl.tabhero.local.EditFavPerfs;
import pl.tabhero.local.EditFavTitles;
import pl.tabhero.local.FavoritesActivity;
import pl.tabhero.local.FavoritesTitleActivity;
import pl.tabhero.net.TabViewActivity;
import pl.tabhero.utils.FileUtils;
import pl.tabhero.utils.MyFilter;
import pl.tabhero.utils.MyFilterDialog;
import pl.tabhero.utils.selector.MyLastTenAdapter;
import pl.tabhero.utils.selector.SelectArralAdapter;
import pl.tabhero.utils.selector.ItemsOnCheckboxList;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MenuFunctions {

    private Context context;
    private Activity activity;
    private DBUtils dbUtils;
    private final String maximize;
    private final String minimize;
    private String className;
    private static final String FAV_PERF_VIEW = FavoritesActivity.class.getSimpleName();
    private static final String FAV_TITLE_VIEW = FavoritesTitleActivity.class.getSimpleName();
    private final String googleSearch;
    private final String spaceCode;
    private final String lyricsString;

    public MenuFunctions(Context context) {
        this.context = context;
        this.activity = (Activity) context;
        this.dbUtils = new DBUtils(this.context);
        this.className = this.activity.getClass().getSimpleName();
        googleSearch = this.context.getString(R.string.googleUrl);
        spaceCode = this.context.getString(R.string.spaceUrl);
        lyricsString = this.context.getString(R.string.lyricsUrl);
        maximize = this.context.getString(R.string.configMAX);
        minimize = this.context.getString(R.string.configMIN);
    }

    public void addToFav(String performer, String title, String tab, String songUrl) {
        if (!this.dbUtils.isExistRecordByUrl(songUrl)) {
            if (this.dbUtils.isIdExistByUrl(songUrl)) {
                buildAlertDialogToAddTabWhithSameId(performer, title, tab, songUrl);
            } else {
                this.dbUtils.addTab(performer, title, tab, songUrl);
            }
        } else {
            Toast.makeText(this.context.getApplicationContext(),
                    R.string.recordExist, Toast.LENGTH_LONG).show();
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
                Toast.makeText(context.getApplicationContext(),
                        R.string.notDelFromBase, Toast.LENGTH_LONG).show();
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
        if (!fileUtils.getOutDir().isDirectory()) {
            fileUtils.getOutDir().mkdir();
        }
        try {
            if (!fileUtils.getOutDir().isDirectory()) {
                throw new IOException(this.context.getString(R.string.createDirectoryError)
                        + this.context.getPackageName() + "." + this.context.getString(R.string.sdcardMountError));
            }
            File outputFile = new File(fileUtils.getOutDir(), fileUtils.getFileName());
            writer = new BufferedWriter(new FileWriter(outputFile));
            writer.write(tablature);
            Toast.makeText(this.context.getApplicationContext(), this.context.getString(R.string.sdcardWriteOK)
                    + outputFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
            writer.close();
        } catch (IOException e) {
            Toast.makeText(this.context.getApplicationContext(),
                    e.getMessage() + this.context.getString(R.string.sdcardWriteError),
                    Toast.LENGTH_LONG).show();
        }
    }

    public void openWebBrowser(String performer, String title) {
        String perf = performer.replaceAll(" ", spaceCode);
        String tit = title.replaceAll(" ", spaceCode);
        String question = googleSearch + perf + spaceCode + tit + spaceCode + lyricsString;
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
                Toast.makeText(context.getApplicationContext(),
                        R.string.notAddTab, Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void minMax() {
        String configName = this.context.getString(R.string.configNameFile);
        SharedPreferences preferences = this.context.getSharedPreferences(configName, Activity.MODE_PRIVATE);
        String maxForConfig;
        boolean fullScreen = (this.activity.getWindow().getAttributes().flags
                & WindowManager.LayoutParams.FLAG_FULLSCREEN) != 0;
        if (fullScreen) {
            this.activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            this.activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            maxForConfig = minimize;
        } else {
            this.activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            this.activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            maxForConfig = maximize;
        }
        
        SharedPreferences.Editor preferencesEditor = preferences.edit();
        preferencesEditor.putString(this.context.getString(R.string.isConfigMax), maxForConfig);
        preferencesEditor.commit();
    }

    private InputFilter filter = new MyFilter();
    private InputFilter filterDialog = new MyFilterDialog();

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
                if (className.equals(FAV_PERF_VIEW)) {
                    refresh = new Intent(activity, FavoritesActivity.class);
                } else if (className.equals(FAV_TITLE_VIEW)) {
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

    public void startEditTitleActivity(ArrayList<String> listToEditTitles, ArrayList<String> listToEditUrl) {
        Intent i = new Intent(this.activity, EditFavTitles.class);
        Bundle bun = new Bundle();
        bun.putStringArrayList("listOfTitles", listToEditTitles);
        bun.putStringArrayList("listOfUrl", listToEditUrl);
        i.putExtras(bun);
        this.context.startActivity(i);
        this.activity.overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
    }

    public void buildAlertDialogToChangeRecordName(final String oldRecordName, final String url) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
        final EditText input = new EditText(this.context);
        input.setFilters(new InputFilter[]{filterDialog});
        if (className.equals(FAV_PERF_VIEW)) {
            builder.setMessage(R.string.changePerf);
        } else if (className.equals(FAV_TITLE_VIEW)) {
            builder.setMessage(R.string.changeTitle);
        }
        input.setText(oldRecordName);
        builder.setView(input);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String newRecordName = input.getText().toString();
                if (className.equals(FAV_PERF_VIEW)) {
                    dbUtils.changePerfName(newRecordName, oldRecordName);
                    refresh();
                } else if (className.equals(FAV_TITLE_VIEW)) {
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
        Intent refresh = null;
        if (className.equals(FAV_PERF_VIEW)) {
            refresh = new Intent(this.context, FavoritesActivity.class);
        } else if (className.equals(FAV_TITLE_VIEW)) {
            refresh = new Intent(this.context, FavoritesTitleActivity.class);
        }
        this.activity.startActivity(refresh);
    }

    public void checkReverse(ArrayList<ItemsOnCheckboxList> recordCheckList, ListView delListView) {
        for (ItemsOnCheckboxList perf : recordCheckList) {
            if (perf.isChecked()) {
                perf.setChecked(false);
            } else {
                perf.setChecked(true);
            }
        }
        ArrayAdapter<ItemsOnCheckboxList> listAdapter = new SelectArralAdapter(this.context, recordCheckList);
        delListView.setAdapter(listAdapter);
    }

    public void showInfo() {
        String versionCode = "";
        String appName = "";
        try {
            appName = this.context.getString(R.string.app_name);
            versionCode = this.context.getPackageManager().getPackageInfo(this.context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            Toast.makeText(this.context.getApplicationContext(),
                    R.string.versionReadError, Toast.LENGTH_LONG).show();
        }
        final Dialog builder = new Dialog(this.context);
        builder.setContentView(R.layout.builderlayout);
        builder.setTitle(R.string.info);
        TextView text = (TextView) builder.findViewById(R.id.textDialog);
        text.setText(this.context.getString(R.string.message_info) + "\n\n"
                + appName + " v" + versionCode);
        Button dialogButtonHelp = (Button) builder.findViewById(R.id.dialogButtonHelp);
        Button dialogButtonOK = (Button) builder.findViewById(R.id.dialogButtonOK);
        dialogButtonHelp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, HelpActivity.class);
                context.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
                builder.dismiss();
            }
        });
        dialogButtonOK.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });
        builder.show();
    }

    public void firstRun() {
        String configName = this.context.getString(R.string.configNameFile);
        final SharedPreferences preferences = this.context.getSharedPreferences(configName, Activity.MODE_PRIVATE);
        boolean isFirstRun = preferences.getBoolean(this.context.getString(R.string.isFirstRun), true);
        if (isFirstRun) {
            final SharedPreferences.Editor preferencesEditor = preferences.edit();
            AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
            builder.setTitle(R.string.hello);
            builder.setMessage(this.context.getString(R.string.firstRun));
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    preferencesEditor.putBoolean(context.getString(R.string.isFirstRun), false);
                    //preferencesEditor.putBoolean("transparent", true);
                    preferencesEditor.commit();
                    dialog.dismiss();
                }
            });
            builder.setPositiveButton(R.string.help, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(context, HelpActivity.class);
                    context.startActivity(intent);
                    activity.overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
                    preferencesEditor.putBoolean(context.getString(R.string.isFirstRun), false);
                    //preferencesEditor.putBoolean("transparent", true);
                    preferencesEditor.commit();
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }
    
    public void searchTitleRun() {
        String configName = this.context.getString(R.string.configNameFile);
        final SharedPreferences preferences = this.context.getSharedPreferences(configName, Activity.MODE_PRIVATE);
        boolean isChecked = preferences.getBoolean(this.context.getString(R.string.isCheckedInConfig), false);
        if (!isChecked) {
            final Dialog builder = new Dialog(this.context);
            builder.setContentView(R.layout.checkbox_in_search_title);
            builder.setTitle(R.string.hint);
            TextView text = (TextView) builder.findViewById(R.id.textHint);
            text.setText(this.context.getString(R.string.infoAboutHintEmpty));
            Button dialogButtonHelp = (Button) builder.findViewById(R.id.okButton);
            final CheckBox dontShowAgain = (CheckBox) builder.findViewById(R.id.skip);
            dialogButtonHelp.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dontShowAgain.isChecked()) {
                        SharedPreferences.Editor preferencesEditor = preferences.edit();
                        preferencesEditor.putBoolean(context.getString(R.string.isCheckedInConfig), true);
                        preferencesEditor.commit();
                    }
                    builder.dismiss();
                }
            });
            builder.show();
        }
    }
    
    public void showLastTen() {
        
        final DBUtils dbUtils = new DBUtils(this.context);
        if (dbUtils.getCount() != 0) {
            final Dialog builder = new Dialog(this.context);
            builder.setContentView(R.layout.last_ten_list);
            builder.setTitle(R.string.lastShown);
            ListView lastTenListView = (ListView) builder.findViewById(R.id.lastTenListView);       
            final ArrayList<ItemOfLastTen> listOfLastTen = dbUtils.getLastTenItems();
            sortByDate(listOfLastTen);
            MyLastTenAdapter adapter = new MyLastTenAdapter(this.context, listOfLastTen);
            lastTenListView.setAdapter(adapter);
            lastTenListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String perf = listOfLastTen.get(position).getPerformer();
                    String title = listOfLastTen.get(position).getTitle();
                    String tablature = listOfLastTen.get(position).getTablature();
                    String url = listOfLastTen.get(position).getUrl();
                    //String date = listOfLastTen.get(position).getDate();
                    String type = listOfLastTen.get(position).getType();
                    Intent intent = new Intent(context, TabViewActivity.class);
                    Bundle bun = new Bundle();
                    bun.putString("performerName", perf);
                    bun.putString("songTitle", title);
                    bun.putString("songUrl", url);
                    bun.putString("tab", tablature);
                    bun.putString("type", type);
                    intent.putExtras(bun);
                    context.startActivity(intent);
                    activity.overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
                    builder.dismiss();
                }
            });
            builder.show();
        } else {
            Toast.makeText(this.context.getApplicationContext(),
                    R.string.lastTenEmpty, Toast.LENGTH_LONG).show();
        }
    }
    
    private void sortByDate(ArrayList<ItemOfLastTen> listOfLastTen) {
        int n = listOfLastTen.size();
        do {
            for (int i = 0; i < n - 1; i++) {
                SimpleDateFormat sdf = new SimpleDateFormat(this.context.getString(R.string.formatDate));
                Date date1 = null;
                Date date2 = null;
                try {
                    date1 = sdf.parse(listOfLastTen.get(i).getDate());
                    date2 = sdf.parse(listOfLastTen.get(i + 1).getDate());
                } catch (ParseException e) {
                    Toast.makeText(this.context.getApplicationContext(),
                            R.string.parseDateError, Toast.LENGTH_LONG).show();
                }  
                if (date1.before(date2)) {
                    ItemOfLastTen temp = listOfLastTen.get(i + 1);
                    listOfLastTen.set(i + 1, listOfLastTen.get(i));
                    listOfLastTen.set(i, temp);                    
                }
            }
            n = n - 1;
        } while (n > 1);
    }
    
    public void getTransparent() {
        String configName = this.context.getString(R.string.configNameFile);
        final SharedPreferences preferences = this.context.getSharedPreferences(configName, Activity.MODE_PRIVATE);
        boolean isFirstRun = preferences.getBoolean(this.context.getString(R.string.isFirstRun), true);
        final LinearLayout transpLin = (LinearLayout) this.activity.findViewById(R.id.transparentLayout);
        final ImageView transpImg = (ImageView) this.activity.findViewById(R.id.transparentImage);
        transpLin.setVisibility(View.INVISIBLE);
        transpImg.setVisibility(View.INVISIBLE);
        boolean transparent = preferences.getBoolean("transparent", false);
        if (!transparent /*&& !isFirstRun*/) {
            transpLin.setVisibility(View.VISIBLE);
            transpImg.setVisibility(View.VISIBLE);
            transpImg.setOnClickListener(new View.OnClickListener() {        
                @Override
                public void onClick(View view) {
                    transpLin.setVisibility(View.INVISIBLE);
                    transpImg.setVisibility(View.INVISIBLE);
                    SharedPreferences.Editor preferencesEditor = preferences.edit();
                    preferencesEditor.putBoolean("transparent", true);
                    preferencesEditor.commit();
                }
            });
        }
    }
    
    public void changeWallpaper() {
        String configName = this.context.getString(R.string.configNameFile);
        final SharedPreferences preferences = this.context.getSharedPreferences(configName, Activity.MODE_PRIVATE);
        int orientation = this.context.getResources().getConfiguration().orientation;
        if (orientation == 1) {
            ImageView robot = (ImageView) this.activity.findViewById(R.id.robot);
            robot.setOnLongClickListener(new View.OnLongClickListener() {
                
                @Override
                public boolean onLongClick(View v) {
                    //Log.d("KLIK", "KLIK");
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(R.string.chooseWallpaper);
                    builder.setItems(R.array.select_wall_items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            /* User clicked so do some stuff */
                            //String[] items = context.getResources().getStringArray(R.array.select_wall_items);
                            //new AlertDialog.Builder(context)
                            //        .setMessage("You selected: " + which + " , " + items[which])
                            //       .show();
                            int wallpaper = which + 1;
                            setWallpaper(wallpaper);
                            SharedPreferences.Editor preferencesEditor = preferences.edit();
                            preferencesEditor.putInt("wallpaper", wallpaper);
                            preferencesEditor.commit();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                    return false;
                }
            });
        }
        int wallpaper = preferences.getInt("wallpaper", 3);
        setWallpaper(wallpaper);
    }
    
    private void setWallpaper(int wallpaper) {
        switch (wallpaper) {
            case 1:
                this.activity.getWindow().setBackgroundDrawableResource(R.drawable.tap1);
                break;
            case 2:
                this.activity.getWindow().setBackgroundDrawableResource(R.drawable.tap2);
                break;
            case 3:
                this.activity.getWindow().setBackgroundDrawableResource(R.drawable.tap3);
                break;
            case 4:
                this.activity.getWindow().setBackgroundDrawableResource(R.drawable.tap4);
                break;
        }  
    }
}
