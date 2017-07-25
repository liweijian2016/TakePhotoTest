package cn.com.bjx.takephototest.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.zhy.autolayout.AutoLayoutActivity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @File 包含Fragment的 Activity.--->目前只有 MainActivity 包含Fragment.
 * @Function 页面统计, 用于区别:包含 Fragment的Activity 和 纯Activity.区别在于 onResume 和 onPause.
 * @Author lwj.
 * @Time 2017/6/23.
 * @Copyright 2017 Polaris.
 */

public class BaseFragmentActivity extends AutoLayoutActivity {
    public final String TAG = getClass().getSimpleName();
    public Resources res; // 方便在各个地方获取对应的资源.
    //屏幕高度
    protected int screenHeight = 0;
    //软件盘弹起后所占高度阀值
    protected int keyHeight = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // 无界面,这个东西是个基类,父类.
        // 资源
        res = getResources();
        //获取屏幕高度
        screenHeight = this.getWindowManager().getDefaultDisplay().getHeight();
        //阀值设置为屏幕高度的1/3
        keyHeight = screenHeight / 3;
        // crashHandler.
    }

    protected <T extends View> T f(int resId) {
        return (T) super.findViewById(resId); //这个方法好,直接简化了findViewById的操作.
    }

    // 修改状态栏.start===============================================================================
    public void initSystemBar(int resColor) {
        initSystemBar(this, resColor);
    }

    public void initSystemBar(Activity activity, int resColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(activity, true);
        }
        // 系统栏着色管理器.
        SystemBarTintManager tintManager = new SystemBarTintManager(activity);
        tintManager.setStatusBarTintEnabled(true);
        // 使用颜色资源
        tintManager.setStatusBarTintResource(resColor);
        Class clazz = this.getWindow().getClass();
        try {
            int darkModeFlag = 0;
            Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            if (false) {
                extraFlagField.invoke(this.getWindow(), darkModeFlag, darkModeFlag);//状态栏透明且黑色字体
            } else {
                extraFlagField.invoke(this.getWindow(), 0, darkModeFlag);//清除黑色字体
            }
        } catch (Exception e) {

        }
    }

    @TargetApi(19)
    private static void setTranslucentStatus(Activity activity, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winP = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winP.flags |= bits;
        } else {
            winP.flags &= ~bits;
        }
        win.setAttributes(winP);
    }
    // 修改状态栏.end===============================================================================
}
