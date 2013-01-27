package pl.tabhero.utils;

import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.TextView;

public class MyEditorKeyActions implements TextView.OnEditorActionListener {

    private ImageButton imgBtn;

    public MyEditorKeyActions(ImageButton imgBtn) {
        this.imgBtn = imgBtn;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH
                || actionId == EditorInfo.IME_ACTION_DONE
                || event.getAction() == KeyEvent.ACTION_DOWN
                && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            this.imgBtn.performClick();
            return true;
        }
        return false;
    }

}
