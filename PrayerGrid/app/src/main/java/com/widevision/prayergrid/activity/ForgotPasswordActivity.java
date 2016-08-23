package com.widevision.prayergrid.activity;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.widevision.prayergrid.R;
import com.widevision.prayergrid.dao.ForgotPasswordDao;
import com.widevision.prayergrid.dao.GsonClass;
import com.widevision.prayergrid.model.HideKeyActivity;
import com.widevision.prayergrid.util.AsyncCallback;
import com.widevision.prayergrid.util.Constant;
import com.widevision.prayergrid.util.Extension;
import com.widevision.prayergrid.util.Implementation;
import com.widevision.prayergrid.util.PreferenceConnector;
import com.widevision.prayergrid.util.ProgressLoaderHelper;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ForgotPasswordActivity extends HideKeyActivity {

    @Bind(R.id.email)
    EditText mEmailEdt;
    @Bind(R.id.action_send)
    ImageView mSendBtn;
    @Bind(R.id.action_close)
    ImageView mCancelBtn;
    @Bind(R.id.main)
    CoordinatorLayout mainLayout;
    private Extension extension;
    private ProgressLoaderHelper progressLoaderHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.pull_out_to_left_anim, R.anim.pull_infromright_anim);
        setContentView(R.layout.activity_forgot_password);
        init();

        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constant.buttonEnable) {
                    Constant.setButtonEnable();
                    sendEmailTask();
                }
            }
        });

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    void init() {
        ButterKnife.bind(this);
        extension = new Extension();
        progressLoaderHelper = ProgressLoaderHelper.getInstance();
        setupUI(mainLayout);
    }

    void sendEmailTask() {
        String email = mEmailEdt.getText().toString().trim();
        if (email.equals("")) {
            Constant.setAlert(ForgotPasswordActivity.this, "Enter your email.");
        } else if (!extension.executeStrategy(ForgotPasswordActivity.this, email, Implementation.EMAIL)) {
            Constant.setAlert(ForgotPasswordActivity.this, "Invalid email.");
        } else {
            if (extension.executeStrategy(ForgotPasswordActivity.this, "", Implementation.INTERNET)) {
                progressLoaderHelper.showProgress(ForgotPasswordActivity.this);

                String id = PreferenceConnector.readString(ForgotPasswordActivity.this, PreferenceConnector.LOGIN_UserId, "");
                ForgotPasswordDao dao = new ForgotPasswordDao(email);
                dao.query(new AsyncCallback<GsonClass>() {
                    @Override
                    public void onOperationCompleted(GsonClass result, Exception e) {
                        progressLoaderHelper.dismissProgress();
                        if (e == null && result != null) {
                            if (result.success.equals("1")) {
                                Toast.makeText(ForgotPasswordActivity.this, "" + result.message, Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Constant.setAlert(ForgotPasswordActivity.this, "This email Id is not registered.");
                            }
                        } else {
                            Constant.setAlert(ForgotPasswordActivity.this, getResources().getString(R.string.wrong));
                        }
                    }
                });
            } else {
                Constant.setAlert(ForgotPasswordActivity.this, getResources().getString(R.string.no_internet));
            }
        }
    }
}