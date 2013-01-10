package pl.tabhero.utils;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.ImageButton;

public class MyOnKeyListener implements OnKeyListener {
	
	public ImageButton imgBtn;
	
	public MyOnKeyListener(ImageButton imgBtn) {
		this.imgBtn = imgBtn;
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_ENTER) {
			this.imgBtn.performClick();
			return true;
		} else {
			return false;
		}
	}

}
