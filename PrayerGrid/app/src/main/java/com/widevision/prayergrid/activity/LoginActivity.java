package com.widevision.prayergrid.activity;

import android.content.Intent;

import android.net.Uri;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.widevision.prayergrid.R;
import com.widevision.prayergrid.dao.GsonClass;
import com.widevision.prayergrid.dao.LoginDao;
import com.widevision.prayergrid.dao.TokenRefreshDao;
import com.widevision.prayergrid.model.HideKeyActivity;
import com.widevision.prayergrid.util.AsyncCallback;
import com.widevision.prayergrid.util.Constant;
import com.widevision.prayergrid.util.Extension;
import com.widevision.prayergrid.util.Implementation;
import com.widevision.prayergrid.util.PreferenceConnector;
import com.widevision.prayergrid.util.ProgressLoaderHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A login screen that offers login via email/password.
 * in this a type paramente is return when login
 */
public class LoginActivity extends HideKeyActivity implements Validator.ValidationListener {

    // UI references.
    @NotEmpty(message = "Please enter Email.")
    @Email(message = "Invalid email id.")
    @Bind(R.id.email)
    EditText mEmailView;
    @NotEmpty(message = "Please enter Password.")
    @Bind(R.id.password)
    EditText mPasswordView;
    @Bind(R.id.email_login_form)
    LinearLayout mMainContentView;
    @Bind(R.id.email_sign_in_button)
    ImageView mEmailSignInButton;
    @Bind(R.id.forgot_pass)
    TextView mForgotPass;
    @Bind(R.id.action_userRegistration)
    ImageView mRegisterUser;
    @Bind(R.id.action_churchRegistration)
    ImageView mRegisterChurch;
    @Bind(R.id.aboutUs)
    TextView mAboutUs;
    @Bind(R.id.check)
    CheckBox mRememberCheck;
    private int animTag = 0;
    private Validator validator;
    private Extension extension;
    private ProgressLoaderHelper progressLoaderHelper;
    private String token = "";
    private int tokenTag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        Constant.mDeviceWidth = displayMetrics.widthPixels;
        Constant.mDeviceHeight = displayMetrics.heightPixels;
        if (PreferenceConnector.readString(LoginActivity.this, PreferenceConnector.IS_LOGIN, "No").equals("Yes")) {
            new gcmTockenTask().execute();
            finish();
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            Constant._id = PreferenceConnector.readString(LoginActivity.this, PreferenceConnector.LOGIN_UserId, "");
            Constant._name = PreferenceConnector.readString(LoginActivity.this, PreferenceConnector.NAME, "");
            Constant._email = PreferenceConnector.readString(LoginActivity.this, PreferenceConnector.LOGIN_EMAIL, "");
            Constant._gender = PreferenceConnector.readString(LoginActivity.this, PreferenceConnector.GENDER, "");
            Constant._country = PreferenceConnector.readString(LoginActivity.this, PreferenceConnector.COUNTRY, "");
            Constant._state = PreferenceConnector.readString(LoginActivity.this, PreferenceConnector.STATE, "");
            Constant._notify_prays = PreferenceConnector.readString(LoginActivity.this, PreferenceConnector.NOTIFY_PRAYS, "");
            Constant._notify_comments = PreferenceConnector.readString(LoginActivity.this, PreferenceConnector.NOTIFY_COMMENTS, "");
            Constant._profile_pic = PreferenceConnector.readString(LoginActivity.this, PreferenceConnector.PROFILE_PIC, "");
            Constant._type = PreferenceConnector.readString(LoginActivity.this, PreferenceConnector.TYPE, "");
            Constant._password = PreferenceConnector.readString(LoginActivity.this, PreferenceConnector.PASSWORD, "");
            Constant._church_name = PreferenceConnector.readString(LoginActivity.this, PreferenceConnector.CHURCH_NAME, "");
            Constant._church_cover = PreferenceConnector.readString(LoginActivity.this, PreferenceConnector.CHURCH_Cover, "");
        }

        overridePendingTransition(R.anim.pull_out_to_left_anim, R.anim.pull_infromright_anim);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        init();

        //Action Login
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    if (Constant.buttonEnable) {
                        Constant.setButtonEnable();
                        validator.validate();
                    }
                    return true;
                }
                return false;
            }
        });

        //Action Login
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constant.buttonEnable) {
                    Constant.setButtonEnable();
                    validator.validate();
                }
            }
        });

        //Action forgot Password
        mForgotPass.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constant.buttonEnable) {
                    Constant.setButtonEnable();
                    animTag = 1;
                    startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
                }
            }
        });

        //Action user Registration
        mRegisterUser.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constant.buttonEnable) {
                    Constant.setButtonEnable();
                    finish();
                    animTag = 1;
                    startActivity(new Intent(LoginActivity.this, Registration.class));
                }
            }
        });

        //Action church Registration
        mRegisterChurch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constant.buttonEnable) {
                    Constant.setButtonEnable();
                    finish();
                    animTag = 1;
                    startActivity(new Intent(LoginActivity.this, ChurchRegistration.class));
                }
            }
        });

        //Action about us.
        mAboutUs.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constant.buttonEnable) {
                    Constant.setButtonEnable();
                    /*Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://prayergrid.org/about"));
                    startActivity(browserIntent);*/
                    startActivity(new Intent(LoginActivity.this, AboutUsActivity.class));
                }
            }
        });
    }

    private void init() {
        validator = new Validator(LoginActivity.this);
        validator.setValidationListener(this);
        extension = new Extension();
        progressLoaderHelper = ProgressLoaderHelper.getInstance();
        setupUI(mMainContentView);
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        StringBuilder stringBuilder = new StringBuilder();
        if (errors.size() == 2) {
            Constant.setAlert(LoginActivity.this, "All fields are mandatory.");
        } else {
            for (ValidationError e : errors) {
                stringBuilder.append(e.getCollatedErrorMessage(LoginActivity.this)).append("\n");
            }
            Constant.setAlert(LoginActivity.this, stringBuilder.toString());
        }
    }

    @Override
    public void onValidationSucceeded() {
        attemptLogin();
    }


    // attempt to login
    private void attemptLogin() {
        if (extension.executeStrategy(LoginActivity.this, "", Implementation.INTERNET)) {
            String email = mEmailView.getText().toString().trim();
            final String pass = mPasswordView.getText().toString().trim();
            if (pass.length() >= 6) {
                progressLoaderHelper.showProgress(LoginActivity.this);
                if (token.equals("") && false) {
                    new gcmTockenTask().execute();
                } else {
                    LoginDao loginDao = new LoginDao(email, pass, token);
                    loginDao.query(new AsyncCallback<String>() {
                        @Override
                        public void onOperationCompleted(String result, Exception e) {
                            progressLoaderHelper.dismissProgress();

                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                String success = jsonObject.getString("success");
                                if (success.equals("1")) {
                                    String email = jsonObject.getString("email");
                                    String user_id = jsonObject.getString("user_id");
                                    String name = jsonObject.getString("name");
                                    String type = jsonObject.getString("type");
                                    String gender = jsonObject.getString("gender");
                                    String country = jsonObject.getString("country");
                                    String state = jsonObject.getString("state");
                                    String profilepic = jsonObject.getString("profilepic");
                                    String defaultchurch = jsonObject.getString("defaultchurch");

                                    JSONObject notifications = null;
                                    try {
                                        notifications = jsonObject.getJSONObject("notifications");
                                    } catch (JSONException e1) {
                                        e1.printStackTrace();
                                    }
                                    if (notifications != null) {
                                        String pray = notifications.getString("pray");
                                        String comment = notifications.getString("comment");
                                        PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.NOTIFY_PRAYS, pray);
                                        PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.NOTIFY_COMMENTS, comment);
                                    } else {
                                        PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.NOTIFY_PRAYS, "0");
                                        PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.NOTIFY_COMMENTS, "0");
                                    }

                                    if (defaultchurch != null) {
                                        PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.CHURCH_NAME, defaultchurch);
                                    } else {
                                        PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.CHURCH_NAME, "");
                                    }


                                    PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.LOGIN_EMAIL, email);
                                    PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.LOGIN_UserId, user_id);
                                    PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.NAME, name);
                                    PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.TYPE, type);
                                    PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.GENDER, gender);
                                    PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.COUNTRY, country);
                                    PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.STATE, state);


                                    if (mRememberCheck.isChecked()) {
                                        PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.IS_LOGIN, "Yes");
                                    }
                                    PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.PROFILE_PIC, profilepic);
                                    PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.PASSWORD, pass);

                                    if (type.equals("1")) {
                                        String header_image = jsonObject.getString("header_image");
                                        PreferenceConnector.writeString(LoginActivity.this, PreferenceConnector.CHURCH_Cover, header_image);
                                    }
                                    finish();
                                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                } else {
                                    String msg = jsonObject.getString("message");
                                    Constant.setAlert(LoginActivity.this, msg);
                                }
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                                Constant.setAlert(LoginActivity.this, getResources().getString(R.string.wrong));
                            }

                            Constant._id = PreferenceConnector.readString(LoginActivity.this, PreferenceConnector.LOGIN_UserId, "");
                            Constant._name = PreferenceConnector.readString(LoginActivity.this, PreferenceConnector.NAME, "");
                            Constant._email = PreferenceConnector.readString(LoginActivity.this, PreferenceConnector.LOGIN_EMAIL, "");
                            Constant._gender = PreferenceConnector.readString(LoginActivity.this, PreferenceConnector.GENDER, "");
                            Constant._country = PreferenceConnector.readString(LoginActivity.this, PreferenceConnector.COUNTRY, "");
                            Constant._state = PreferenceConnector.readString(LoginActivity.this, PreferenceConnector.STATE, "");
                            Constant._notify_prays = PreferenceConnector.readString(LoginActivity.this, PreferenceConnector.NOTIFY_PRAYS, "");
                            Constant._notify_comments = PreferenceConnector.readString(LoginActivity.this, PreferenceConnector.NOTIFY_COMMENTS, "");
                            Constant._profile_pic = PreferenceConnector.readString(LoginActivity.this, PreferenceConnector.PROFILE_PIC, "");
                            Constant._type = PreferenceConnector.readString(LoginActivity.this, PreferenceConnector.TYPE, "");
                            Constant._password = PreferenceConnector.readString(LoginActivity.this, PreferenceConnector.PASSWORD, "");
                            Constant._church_name = PreferenceConnector.readString(LoginActivity.this, PreferenceConnector.CHURCH_NAME, "");
                            Constant._church_cover = PreferenceConnector.readString(LoginActivity.this, PreferenceConnector.CHURCH_Cover, "");
                        }
                    });
                }
            } else {
                Constant.setAlert(LoginActivity.this, "Invalid password.");
            }
        } else {
            Constant.setAlert(LoginActivity.this, getResources().getString(R.string.no_internet));
        }
    }

    @Override
    protected void onResume() {
        if (animTag == 1) {
            animTag = 0;
            overridePendingTransition(R.anim.push_infromright_anim, R.anim.push_out_to_left_anim);
        }
        new gcmTockenTask().execute();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        tokenTag = 0;
    }

    class gcmTockenTask extends AsyncTask<Void, String, String> {
        @Override
        protected String doInBackground(Void... voids) {
            InstanceID instanceID = InstanceID.getInstance(LoginActivity.this);
            String senderId = getResources().getString(R.string.gcm_SenderId);
            try {
                // request token that will be used by the server to send push notifications
                token = instanceID.getToken(senderId, GoogleCloudMessaging.INSTANCE_ID_SCOPE);
                Log.e("", "GCM Registration Token: " + token);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return token;
        }

        @Override
        protected void onPostExecute(String token) {
            super.onPostExecute(token);
            if (tokenTag == 2) {
                Constant.setAlert(LoginActivity.this, "Gcm not generated.");
                progressLoaderHelper.dismissProgress();
            } else if (tokenTag == 1) {
                progressLoaderHelper.dismissProgress();
                attemptLogin();
                tokenTag = 2;
            } else {
                tokenTag = 1;
            }

            if (PreferenceConnector.readString(LoginActivity.this, PreferenceConnector.IS_LOGIN, "No").equals("Yes")) {
                TokenRefreshDao refreshDao = new TokenRefreshDao(Constant._id, token);
                refreshDao.query(new AsyncCallback<GsonClass>() {
                    @Override
                    public void onOperationCompleted(GsonClass result, Exception e) {
                    }
                });
            }
        }
    }
}

