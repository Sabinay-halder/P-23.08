package com.widevision.prayergrid.activity;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mobsandgeeks.saripaar.Validator;
import com.widevision.prayergrid.R;
import com.widevision.prayergrid.model.HideKeyActivity;
import com.widevision.prayergrid.util.Extension;
import com.widevision.prayergrid.util.ProgressLoaderHelper;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TimeLineActivity extends HideKeyActivity {

    @Bind(R.id.main)
    CoordinatorLayout mMainContentView;

    private int animTag = 0;
    private Extension extension;
    private ProgressLoaderHelper progressLoaderHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.pull_out_to_left_anim, R.anim.pull_infromright_anim);
        setContentView(R.layout.activity_time_line);
        init();

    }

    void init() {
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        extension = new Extension();
        progressLoaderHelper = ProgressLoaderHelper.getInstance();
        setupUI(mMainContentView);
    }

    @Override
    protected void onResume() {
        if (animTag == 1) {
            animTag = 0;
            overridePendingTransition(R.anim.push_infromright_anim, R.anim.push_out_to_left_anim);
        }
        super.onResume();
    }
}
