package pl.tabhero.utils;

import java.io.IOException;
import pl.tabhero.R;
import pl.tabhero.local.FavTabViewActivity;
import pl.tabhero.net.TabViewActivity;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ButtonsScale {
	
	private Context context;
	private Activity activity;
	private ImageButton btnPlus;
	private ImageButton btnMinus;
	private String className;
	private float size;
	private FileUtils fileUtils;
	private static final String FAV_TAB_VIEW = FavTabViewActivity.class.getSimpleName();
	private static final String NET_TAB_VIEW = TabViewActivity.class.getSimpleName();
	
	public ButtonsScale(Context context) {
		this.context = context;
		this.activity = (Activity) context;
		this.className = this.activity.getClass().getSimpleName();
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
		if(this.className.equals(NET_TAB_VIEW)) {
			btnPlus = (ImageButton) this.activity.findViewById(R.id.btnPlus);
		} else if(this.className.equals(FAV_TAB_VIEW)) {
			btnPlus = (ImageButton) this.activity.findViewById(R.id.favBtnPlus);
		}
		
		btnPlus.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				float scaleText = tab.getTextSize();
				scaleText++;
				tab.setTextSize(0, scaleText);
				size = scaleText;
				try {
					fileUtils.setSizeToConfig(size);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private void initBtnMinusOnClick(final TextView tab) {
		if(this.className.equals(NET_TAB_VIEW)) {
			btnMinus = (ImageButton) this.activity.findViewById(R.id.btnMinus);
		} else if(this.className.equals(FAV_TAB_VIEW)) {
			btnMinus = (ImageButton) this.activity.findViewById(R.id.favBtnMinus);
		}
		
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
