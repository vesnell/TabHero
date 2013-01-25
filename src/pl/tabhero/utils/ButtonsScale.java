package pl.tabhero.utils;

import java.io.IOException;
import pl.tabhero.R;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ButtonsScale {
	
	private Context context;
	private Activity activity;
	private ImageButton btnPlus;
	private ImageButton btnMinus;
	private float size;
	private FileUtils fileUtils;
	
	public ButtonsScale(Context context) {
		this.context = context;
		this.activity = (Activity) context;
		this.fileUtils = new FileUtils(this.context);
	}
	
	public void init(LinearLayout buttons, TextView txt) {
		buttons.setVisibility(View.VISIBLE);
		initBtnPlusOnClick(txt);
    	initBtnMinusOnClick(txt);
    	ButtonsRunnable btnsRunnable = new ButtonsRunnable(buttons); 
    	new Handler().postDelayed(btnsRunnable, 3000);
	}
	
	private void initBtnPlusOnClick(final TextView tab) {
		btnPlus = (ImageButton) this.activity.findViewById(R.id.btnPlus);
		
		btnPlus.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				float scaleText = tab.getTextSize();
				scaleText++;
				tab.setTextSize(0, scaleText);
				size = scaleText;
				try {
					fileUtils.setSizeToConfig(size);
				} catch (IOException e) {
					Toast.makeText(context.getApplicationContext(), R.string.sdcardWriteError, Toast.LENGTH_LONG).show();
				}
			}
		});
	}
	
	private void initBtnMinusOnClick(final TextView tab) {
		btnMinus = (ImageButton) this.activity.findViewById(R.id.btnMinus);
		
		btnMinus.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				float scaleText = tab.getTextSize();
				scaleText--;
				tab.setTextSize(0, scaleText);
				size = scaleText;
			}
		});
	}
}
