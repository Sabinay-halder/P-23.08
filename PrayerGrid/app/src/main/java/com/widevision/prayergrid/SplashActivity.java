package com.widevision.prayergrid;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import com.widevision.prayergrid.activity.LoginActivity;

public class SplashActivity extends Activity {
    private final static int MSG_CONTINUE = 1234;
    private final static long DELAY = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mHandler.sendEmptyMessageDelayed(MSG_CONTINUE, DELAY);
    }

    @Override
    protected void onDestroy() {
        mHandler.removeMessages(MSG_CONTINUE);
        super.onDestroy();
    }

    private void _continue() {
        startActivity(new Intent(this, LoginActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    private final Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_CONTINUE:
                    _continue();
                    break;
            }
        }
    };
}