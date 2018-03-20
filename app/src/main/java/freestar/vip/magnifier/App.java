package freestar.vip.magnifier;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

/**
 * 描述：
 * 作者：一颗浪星
 * 日期：2018/3/14
 * github：
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("FreeStar", "onCreate: ");
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.e("FreeStar", "onTerminate: ");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.e("FreeStar", "onConfigurationChanged: ");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.e("FreeStar", "onLowMemory: ");
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Log.e("FreeStar", "onTrimMemory: ");
    }
}
