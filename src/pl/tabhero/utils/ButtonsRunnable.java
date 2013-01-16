package pl.tabhero.utils;

import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class ButtonsRunnable implements Runnable {
	
	private LinearLayout linearBut;
	
	public ButtonsRunnable(LinearLayout linButtons) {
		this.linearBut = linButtons;
	}

	@Override
	public void run() {
		this.linearBut.setVisibility(View.GONE);
	}
	
	public void runLockButtons(boolean lock, ImageButton btnLock, ImageButton btnUnLock) {
		if(lock) {
			btnLock.setVisibility(View.GONE);
			btnUnLock.setVisibility(View.VISIBLE);
		} else {
			btnUnLock.setVisibility(View.GONE);
			btnLock.setVisibility(View.VISIBLE);
		}
	}

}
