
package me.fantouch.tabwithcursor;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;

import me.fantouch.tabwithcursor.RadioGroupWithSlider.onRefreshListener;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView logTxt=(TextView)findViewById(R.id.logTxt);
        
        RadioGroupWithSlider radioGroupWithSlider = new RadioGroupWithSlider(this, R.id.tabBar, R.id.radioGroup, R.id.cursor);
        radioGroupWithSlider.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                logTxt.setText("Change\n\nID="+checkedId);
            }
        });
        radioGroupWithSlider.setOnRefreshListener(new onRefreshListener() {

            @Override
            public void onRefresh(int radioButtonId) {
                logTxt.setText( "Refresh\n\nID=" + radioButtonId);
            }
        });
    }

}
