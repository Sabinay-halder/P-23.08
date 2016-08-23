package com.widevision.prayergrid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.widevision.prayergrid.R;
import com.widevision.prayergrid.dao.ChurchRegistrationFetcher;
import com.widevision.prayergrid.dao.GsonClass;
import com.widevision.prayergrid.model.HideKeyActivity;
import com.widevision.prayergrid.util.AsyncCallback;
import com.widevision.prayergrid.util.Constant;
import com.widevision.prayergrid.util.DBHelper;
import com.widevision.prayergrid.util.Extension;
import com.widevision.prayergrid.util.Implementation;
import com.widevision.prayergrid.util.ProgressLoaderHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChurchRegistration extends HideKeyActivity implements Validator.ValidationListener {

    @Bind(R.id.main)
    CoordinatorLayout mMainContentView;

    @NotEmpty(message = "The churchname field is required.")
    @Bind(R.id.church_name)
    EditText mChurchName;

    @NotEmpty(message = "City name required.")
    @Bind(R.id.city)
    EditText mCity;

    @NotEmpty(message = "State name required.")
    @Bind(R.id.state)
    EditText mState;

    @Bind(R.id.country)
    Spinner mCountry;

    @NotEmpty(message = "The denomination field is required.")
    @Bind(R.id.denomination)
    EditText mDenomination;

    @NotEmpty(message = "Leader name required.")
    @Bind(R.id.church_Leader)
    EditText mLeader;

    @NotEmpty(message = "Phone number required.")
    @Bind(R.id.phone)
    EditText mPhone;

    @Email(message = "Invalid Email.")
    @NotEmpty(message = "The email field is required.")
    @Bind(R.id.leader_email)
    EditText mEmail;

    @NotEmpty(message = "The linkname field is required.")
    @Bind(R.id.url)
    EditText mUrl;

    @NotEmpty(message = "The password field is required.")
    @Bind(R.id.password)
    EditText mPassword;

    @NotEmpty(message = "Please enter confirm password.")
    @Bind(R.id.c_password)
    EditText mCPassword;

    @Bind(R.id.radio_frist)
    RadioGroup mFirstRadioGrp;
    @Bind(R.id.radio_second)
    RadioGroup mSecondRadioGrp;
    @Bind(R.id.radio_third)
    RadioGroup mThirdRadioGrp;

    @Bind(R.id.check_authorize)
    CheckBox mCheckAuthorize;
    @Bind(R.id.check_TC)
    CheckBox mCheckTc;

    @Bind(R.id.register_button)
    ImageView mRegisterBtn;

    @Bind(R.id.tcTxt)
    TextView tc;

    private ArrayList<String> countries;
    private Validator validator;
    private Extension extension;
    private ProgressLoaderHelper progressLoaderHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.pull_out_to_left_anim, R.anim.pull_infromright_anim);
        setContentView(R.layout.activity_curch_registration);
        init();
        countries = DBHelper.getInstance().getCountryList();
        mCountry.setAdapter(new ArrayAdapter<>(ChurchRegistration.this, R.layout.place_spinner_row, countries));
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constant.buttonEnable) {
                    Constant.setButtonEnable();
                    validator.validate();
                }
            }
        });
        tc.setText(Html.fromHtml("I have read and agree to the <u><font color='red'>terms and conditions.</font></u>"));
        tc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constant.buttonEnable) {
                    Constant.setButtonEnable();
                    startActivity(new Intent(ChurchRegistration.this, TCActivity.class));
                }
            }
        });
    }

    void init() {
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.back);
        validator = new Validator(ChurchRegistration.this);
        validator.setValidationListener(this);
        extension = new Extension();
        progressLoaderHelper = ProgressLoaderHelper.getInstance();
        setupUI(mMainContentView);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onValidationSucceeded() {
        checkValidation();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        if (errors.size() > 8) {
            Constant.setAlert(ChurchRegistration.this, "All fields required.");
        } else {
            Constant.setAlert(ChurchRegistration.this, errors.get(errors.size() - 1).getCollatedErrorMessage(ChurchRegistration.this));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ChurchRegistration.this, LoginActivity.class));
    }

    void checkValidation() {
        String pass = mPassword.getText().toString().trim();
        String cPass = mCPassword.getText().toString().trim();
        if (pass.equals(cPass)) {
            if (pass.length() >= 6) {
                if (mCheckAuthorize.isChecked() && mCheckTc.isChecked()) {
                    String phone = mPhone.getText().toString().trim();
                    if (extension.executeStrategy(ChurchRegistration.this, phone, Implementation.isnumber)) {
                        //attempt registration
                        attemptRegistratino();
                    } else {
                        Constant.setAlert(ChurchRegistration.this, "Invalid number.");
                    }
                } else {
                    if (mCheckAuthorize.isChecked()) {
                        Constant.setAlert(ChurchRegistration.this, "You must agree to the terms and conditions to register");
                    } else {
                        Constant.setAlert(ChurchRegistration.this, "You must be a Church Leader to register.");
                    }
                }
            } else {
                Constant.setAlert(ChurchRegistration.this, "Password length should be minimum 6 digit.");
            }
        } else {
            Constant.setAlert(ChurchRegistration.this, "Password and confirm password doesn't match.");
        }
    }

    void attemptRegistratino() {
        String name = mChurchName.getText().toString().trim();
        String email = mEmail.getText().toString().trim();
        String pin = mPassword.getText().toString().trim();
        String city = mCity.getText().toString().trim();
        String state = mState.getText().toString().trim();
        int a = mCountry.getSelectedItemPosition();
        String country = DBHelper.countryBeanArrayList.get(a).getCountryCode();
        String denomination = mDenomination.getText().toString().trim();
        String leader = mLeader.getText().toString().trim();
        String phone = mPhone.getText().toString().trim();
        String linkname = mUrl.getText().toString().trim();

        int id = mFirstRadioGrp.getCheckedRadioButtonId();
        int id2 = mThirdRadioGrp.getCheckedRadioButtonId();
        RadioButton rb_f = (RadioButton) findViewById(id);
        RadioButton rb_t = (RadioButton) findViewById(id2);
        String memberapproval = rb_f.getText().toString().trim();
        String tv = rb_t.getText().toString().trim();
        progressLoaderHelper.showProgress(ChurchRegistration.this);
        ChurchRegistrationFetcher churchRegistration = new ChurchRegistrationFetcher(name, email, pin, city, state, country, denomination, leader, phone, linkname, memberapproval, tv);
        churchRegistration.query(new AsyncCallback<GsonClass>() {
            @Override
            public void onOperationCompleted(GsonClass result, Exception e) {
                progressLoaderHelper.dismissProgress();
                if (e == null && result != null) {
                    if (result.success.equals("1")) {
                        Toast.makeText(ChurchRegistration.this, result.message, Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    } else {
                        Constant.setAlert(ChurchRegistration.this, result.message);
                    }
                } else {
                    Constant.setAlert(ChurchRegistration.this, getResources().getString(R.string.wrong));
                }
            }
        });
    }
}