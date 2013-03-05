package pl.tabhero.utils;

import java.math.BigDecimal;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

public class PinchZoom implements OnTouchListener {

    private Context context;
    private int touchState;
    private final int idle = 0;
    private final int touch = 1;
    private final int pinch = 2;
    private static final double MIN_DIV = 0.1;
    private static final int MIN_SIZE_TEXT = 8;
    private static final int MAX_SIZE_TEXT = 50;
    private double dist0, distCurrent;
    private TextView myTouchEvent;
    private String myStringTab;
    private double prevScale = 0;

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
            touchState = touch;
            break;
        case MotionEvent.ACTION_POINTER_DOWN:
            //A non-primary pointer has gone down.
            touchState = pinch;

            //Get the distance when the second pointer touch
            distx = event.getX(0) - event.getX(1);
            disty = event.getY(0) - event.getY(1);
            dist0 = Math.sqrt(distx * distx + disty * disty);

            break;
        case MotionEvent.ACTION_MOVE:
            //A change has happened during a press gesture (between ACTION_DOWN and ACTION_UP).
            if (touchState == pinch) {
                //Get the current distance
                distx = event.getX(0) - event.getX(1);
                disty = event.getY(0) - event.getY(1);
                distCurrent = Math.sqrt(distx * distx + disty * disty);

                drawMatrix();
            }
            break;
        case MotionEvent.ACTION_UP:
            //A pressed gesture has finished.
            touchState = idle;
            break;
        case MotionEvent.ACTION_POINTER_UP:
            //A non-primary pointer has gone up.
            touchState = touch;
            break;
        default:
            break;
        }
        return false;
    }

    public void drawMatrix() {
        FileUtils fileUtils = new FileUtils(this.context);
        float sizeText = (int) myTouchEvent.getTextSize();
        double curScale = distCurrent / dist0;
        if (curScale < MIN_DIV) {
            curScale = MIN_DIV;
        }
        if (!(Double.isNaN(curScale))) {
            if (roundToThreeDigits(curScale) != roundToThreeDigits(prevScale)) {
                sizeText = (int) (sizeText * curScale);
                prevScale = curScale;
                if (sizeText < MIN_SIZE_TEXT) {
                    sizeText = MIN_SIZE_TEXT;
                } else if (sizeText > MAX_SIZE_TEXT) {
                    sizeText = MAX_SIZE_TEXT;
                }
            }
        } else {
            sizeText = fileUtils.setSizeText();
        }

        myTouchEvent.setTextSize(0, sizeText);
        myTouchEvent.setText(myStringTab);
    }
    
    private static double roundToThreeDigits(double input) {
        return new BigDecimal(input).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
