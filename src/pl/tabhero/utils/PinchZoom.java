package pl.tabhero.utils;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

public class PinchZoom implements OnTouchListener {
	
	private Context context;
	private int touchState;
	private final int IDLE = 0;
	private final int TOUCH = 1;
	private final int PINCH = 2;
	private double dist0, distCurrent;
	private TextView myTouchEvent;
	private String myStringTab;
	
	public PinchZoom(Context context, TextView tabView, String stringTab) {
		this.context = context;
		myTouchEvent = tabView;
		myStringTab = stringTab;
	}
	
	@Override
	public boolean onTouch(View view, MotionEvent event) {
	   
	   float distx, disty;
	   
	   switch(event.getAction() & MotionEvent.ACTION_MASK) {
	   		case MotionEvent.ACTION_DOWN:
	   			//A pressed gesture has started, the motion contains the initial starting location.
	   			touchState = TOUCH;
	   			break;
	   		case MotionEvent.ACTION_POINTER_DOWN:
	   			//A non-primary pointer has gone down.
	   			touchState = PINCH;
	    
	   			//Get the distance when the second pointer touch
	   			distx = event.getX(0) - event.getX(1);
	   			disty = event.getY(0) - event.getY(1);
	   			dist0 = Math.sqrt(distx * distx + disty * disty);

	   			break;
	   		case MotionEvent.ACTION_MOVE:
	   			//A change has happened during a press gesture (between ACTION_DOWN and ACTION_UP).
	    
	   			if(touchState == PINCH){      
	   				//Get the current distance
	   				distx = event.getX(0) - event.getX(1);
	   				disty = event.getY(0) - event.getY(1);
	   				distCurrent = Math.sqrt(distx * distx + disty * disty);

	   				drawMatrix();
	   			}
	    
	   			break;
	   		case MotionEvent.ACTION_UP:
	   			//A pressed gesture has finished.
	   			touchState = IDLE;
	   			break;
	   		case MotionEvent.ACTION_POINTER_UP:
	   			//A non-primary pointer has gone up.
	   			touchState = TOUCH;
	   			break;
	   		}
	   return false;
	}
	
	public void drawMatrix() {
		FileUtils fileUtils = new FileUtils(this.context);
		float sizeText = (int) myTouchEvent.getTextSize();
		double curScale = distCurrent/dist0;
		if (curScale < 0.1) {
			curScale = 0.1; 
		}
		if(!(Double.isNaN(curScale))) {
			sizeText = (int) (sizeText * curScale);
			if(sizeText < 8) {
				sizeText = 8;
			} else if(sizeText > 50) {
				sizeText = 50;
			}
		} else {
			sizeText = fileUtils.setSizeTextAndCheckSDCardReadable();
		}
		
		myTouchEvent.setTextSize(0, sizeText);
		myTouchEvent.setText(myStringTab);
	}
}
