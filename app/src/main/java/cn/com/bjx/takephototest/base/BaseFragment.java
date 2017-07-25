package cn.com.bjx.takephototest.base;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by succlz123 on 2015/7/8.
 */
public abstract class BaseFragment extends Fragment {
    public final String TAG = getClass().getSimpleName(); // 不要经常静态,容易无法回收.浪费内存.-----leakCanary

    protected boolean mIsVisible;
    protected CompositeSubscription mCompositeSubscription = new CompositeSubscription(); // 果然来自于 rxjava_1.1.1_sources.jar包

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.e("NEW====", "setUserVisibleHint");
        if (getUserVisibleHint()) {
            mIsVisible = true;
            onVisible();
        } else {
            mIsVisible = false;
            onInvisible();
        }
    }

    protected void onVisible() {
        lazyLoad();
    }

    protected void onInvisible() {

    }

    protected abstract void lazyLoad();

    /**
     * 替代findviewById方法
     */
    protected <T extends View> T f(View view, int resId) {
        return (T) view.findViewById(resId);
    }
}

