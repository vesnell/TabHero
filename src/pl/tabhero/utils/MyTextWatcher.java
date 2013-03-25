package pl.tabhero.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ImageButton;
import android.widget.ListView;

public class MyTextWatcher implements TextWatcher {
    
    private ListView listView;
    private ImageButton imgBtn;
    
    public MyTextWatcher(ListView listView, ImageButton imgBtn) {
        this.listView = listView;
        this.imgBtn = imgBtn;
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() == 0) {
            listView.clearTextFilter();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
            int after) {           
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before,
            int count) {
        listView.setTextFilterEnabled(true);
        listView.setFilterText(s.toString());
        imgBtn.performClick();
    }
}
