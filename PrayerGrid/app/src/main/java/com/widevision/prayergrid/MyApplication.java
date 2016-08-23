package com.widevision.prayergrid;

import android.app.Application;

import com.activeandroid.ActiveAndroid;

/*
import com.activeandroid.ActiveAndroid;
*/

/**
 * Created by mercury-five on 21/01/16.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
      ActiveAndroid.initialize(this);
    }
}
