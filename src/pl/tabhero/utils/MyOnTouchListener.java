package pl.tabhero.utils;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class MyOnTouchListener implements OnTouchListener {
	
	private GestureDetector gestureDetector;
	
	public MyOnTouchListener(GestureDetector gestureDetector) {
		this.gestureDetector = gestureDetector;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return this.gestureDetector.onTouchEvent(event);
	}

}
