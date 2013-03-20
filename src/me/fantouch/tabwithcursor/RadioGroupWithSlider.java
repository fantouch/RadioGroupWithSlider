
package me.fantouch.tabwithcursor;

import android.app.Activity;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;

public class RadioGroupWithSlider {

    public RadioGroupWithSlider(Activity activity, int tabBarId, int radioGpId, int cursorId,
        OnCheckedChangeListener checkChangeListrner) {
        super();
        this.checkChangeListrner = checkChangeListrner;
        initTabBar(activity, tabBarId, radioGpId, cursorId);
    }

    public RadioGroupWithSlider(Activity activity, int tabBarId, int radioGpId, int cursorId) {
        super();
        initTabBar(activity, tabBarId, radioGpId, cursorId);
    }

    public void setOnRefreshListener(onRefreshListener l) {
        this.refreshListener = l;
    }
    
    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener){
        this.checkChangeListrner = onCheckedChangeListener;
    }

    private OnCheckedChangeListener checkChangeListrner;
    private RadioGroup radioGroup;

    private void initTabBar(Activity activity, int tabBarId, int radioGpId, int cursorId) {
        // 取得屏幕宽度
        WindowManager windowManager = activity.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int screenWidth = display.getWidth();
        // 取得tabBar
        View tabBar = activity.findViewById(tabBarId);
        // 取得tab数量
        radioGroup = (RadioGroup) tabBar.findViewById(radioGpId);
        int tabCount = radioGroup.getChildCount();
        // 计算每个tab宽度
        int eachTabWidth = (int) ((float) screenWidth / tabCount);
        // 设置cursor宽度=tab宽度
        View cursor = tabBar.findViewById(cursorId);
        ViewGroup.LayoutParams layoutParams = cursor.getLayoutParams();
        layoutParams.width = eachTabWidth;
        cursor.setLayoutParams(layoutParams);
        // 设置tab切换监听
        radioGroup.setOnCheckedChangeListener(new MyOnCheckedChangeListener(cursor, screenWidth));
        // 设置单击监听
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            radioGroup.getChildAt(i).setOnClickListener(new MyOnClickListener());
        }

    }

    private RelativeLayout.LayoutParams layoutParamsFactory(View theCursor, int screenWidth, int tabCount, int tabIdx) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) theCursor.getLayoutParams();
        params.leftMargin = (int) ((float) tabIdx / tabCount * screenWidth);
        return params;
    }

    private static class AnimFactory {
        private static int lastIdx = 0;

        public static TranslateAnimation getAnim(int tabIdx) {
            TranslateAnimation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f, 
                Animation.RELATIVE_TO_SELF, tabIdx - lastIdx, 
                Animation.RELATIVE_TO_SELF, 0.0f, 
                Animation.RELATIVE_TO_SELF, 0.0f);
            lastIdx = tabIdx;
            animation.setDuration(300);
            return animation;
        }
    }

    private class MyOnCheckedChangeListener implements OnCheckedChangeListener {
        private View cursor;
        private int screenWidth;

        public MyOnCheckedChangeListener(View theCursor, int screenWidth) {
            this.cursor = theCursor;
            this.screenWidth = screenWidth;
        }

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            // 计算出当前选中的是第几个btn
            int tabCount = group.getChildCount();
            int checkTabIndex = 0;
            RadioButton checkedButton;
            for (int i = 0; i < tabCount; i++) {
                checkedButton = (RadioButton) group.getChildAt(i);
                int btnId = checkedButton.getId();
                if (checkedId == btnId) {
                    checkTabIndex = i;
                    break;
                }
            }
            // 从动画工厂获取动画
            TranslateAnimation animation = AnimFactory.getAnim(checkTabIndex);
            // 从布局属性工厂获取动画结束后的布局位置
            final RelativeLayout.LayoutParams layoutParams = layoutParamsFactory(cursor, screenWidth, tabCount, checkTabIndex);
            // 设置动画监听器,以便在结束的时候设置布局
            animation.setAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    cursor.clearAnimation();
                    cursor.setLayoutParams(layoutParams);
                }
            });
            // 执行动画
            cursor.startAnimation(animation);
            // 执行外部监听器动作
            if (checkChangeListrner != null) {
                checkChangeListrner.onCheckedChanged(group, checkedId);
            }
            //不触发刷新监听器标志位
            radioGpHasJustChange=true;
        }
    }

    private onRefreshListener refreshListener;
    private boolean radioGpHasJustChange=false;
    private class MyOnClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
            if (!radioGpHasJustChange && refreshListener != null) {
                refreshListener.onRefresh(checkedRadioButtonId);
            }
            radioGpHasJustChange=false;
        }
    }

    public interface onRefreshListener {
        public void onRefresh(int radioButtonId);
    }
}
