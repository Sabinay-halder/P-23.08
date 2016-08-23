package com.widevision.prayergrid.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.widevision.prayergrid.R;
import com.widevision.prayergrid.dao.GsonClass;
import com.widevision.prayergrid.dao.RegistrationFetcher;
import com.widevision.prayergrid.model.HideKeyActivity;
import com.widevision.prayergrid.util.AsyncCallback;
import com.widevision.prayergrid.util.Constant;
import com.widevision.prayergrid.util.DBHelper;
import com.widevision.prayergrid.util.Extension;
import com.widevision.prayergrid.util.Implementation;
import com.widevision.prayergrid.util.ProgressLoaderHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Registration extends HideKeyActivity implements Validator.ValidationListener {

    @NotEmpty(message = "The name field is required.")
    @Bind(R.id.name)
    EditText mNameEdt;
    @NotEmpty(message = "The email field is required.")
    @Email(message = "Please enter valid email id.")
    @Bind(R.id.email)
    EditText mEmailEdt;
    @NotEmpty(message = "The password field is required.")
    @Bind(R.id.pin)
    EditText mPasswordEdt;
    @NotEmpty(message = "The confirm password field is required.")
    @Bind(R.id.conformPin)
    EditText mConfirmPasswordEdt;
    @Bind(R.id.register_button)
    ImageView mRegisterBtn;
    @Bind(R.id.tcCheck)
    CheckBox mCheckBox;
    @Bind(R.id.tcTxt)
    TextView mTcTxt;
    @Bind(R.id.main)
    CoordinatorLayout mMainContentView;
    @Bind(R.id.countrySp)
    Spinner mCountrySp;

    private Validator validator;
    private Extension extension;
    private ProgressLoaderHelper progressLoaderHelper;

    private final int PICK_FROM_CAMERA = 2;
    private final int PICK_FROM_FILE = 1;
    private String mPhotoPath = "";
    private int animTag = 0;
    private ArrayList<String> countries;
    private String token = "";
    private int tokenTag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.pull_out_to_left_anim, R.anim.pull_infromright_anim);
        setContentView(R.layout.activity_registration2);
        init();
        countries = DBHelper.getInstance().getCountryList();
        mCountrySp.setAdapter(new ArrayAdapter<String>(Registration.this, R.layout.row_spinner, countries) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(getResources().getColor(R.color.text_color));
                return view;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(Color.parseColor("#3C4D5C"));
                text.setSingleLine(false);
                return view;
            }
        });
        mCountrySp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //  Constant.setAlert(getActivity(), "Not implemented yet.");
                if (i != 0) {
                    String a = DBHelper.countryBeanArrayList.get(i).getCountryCode();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        mTcTxt.setText(Html.fromHtml("I have read and agree to the <u><font color='red'>terms and conditions.</font></u>"));
        mConfirmPasswordEdt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.register || id == EditorInfo.IME_NULL) {
                    if (Constant.buttonEnable) {
                        Constant.setButtonEnable();
                        validator.validate();
                    }
                }
                return false;
            }
        });
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constant.buttonEnable) {
                    Constant.setButtonEnable();
                    validator.validate();
                }
            }
        });
        mTcTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constant.buttonEnable) {
                    Constant.setButtonEnable();
                    startActivity(new Intent(Registration.this, TCActivity.class));
                }
            }
        });
    }

    private void init() {
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.back);
        validator = new Validator(Registration.this);
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
        confirmPass();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        if (errors.size() < 4) {
            Constant.setAlert(Registration.this, errors.get(errors.size() - 1).getCollatedErrorMessage(Registration.this));
        } else {
            Constant.setAlert(Registration.this, "All fields are mandatory.");
        }
    }

    private void confirmPass() {
        String passwordStr = mPasswordEdt.getText().toString().trim();
        String confirmPasswordStr = mConfirmPasswordEdt.getText().toString().trim();

        if (passwordStr.equals(confirmPasswordStr)) {
            if (passwordStr.length() >= 6) {
                String name = mNameEdt.getText().toString().trim();
                String email = mEmailEdt.getText().toString().trim();
                if (extension.executeStrategy(Registration.this, "", Implementation.INTERNET)) {
                    final RegistrationFetcher registrationFetcher;
                    int a = mCountrySp.getSelectedItemPosition();
                    if (a == 0) {
                        Constant.setAlert(Registration.this, "Please select your country.");
                        return;
                    }
                    String c = DBHelper.countryBeanArrayList.get(a).getCountryCode();
                    if (!mPhotoPath.equals("")) {
                        registrationFetcher = new RegistrationFetcher(name, email, passwordStr, new File(mPhotoPath), c, token);
                    } else {
                        registrationFetcher = new RegistrationFetcher(name, email, passwordStr, c, token);
                    }
                    if (mCheckBox.isChecked()) {
                        progressLoaderHelper.showProgress(Registration.this);
                        if (token.equals("") && false) {
                            new gcmTockenTask().execute();
                        } else {
                            registrationFetcher.query(new AsyncCallback<GsonClass>() {
                                @Override
                                public void onOperationCompleted(GsonClass result, Exception e) {
                                    progressLoaderHelper.dismissProgress();
                                    if (e == null && result != null) {
                                        if (result.success.equals("1")) {
                                            finish();
                                            startActivity(new Intent(Registration.this, LoginActivity.class));
                                            Toast.makeText(Registration.this, "Registration successful.", Toast.LENGTH_SHORT).show();
                                          /* public static final String LOGIN_UserId = "LOGIN_UserId";
                                public static final String LOGIN_EMAIL = "LOGIN_EMAIL";
                                public static final String IS_LOGIN = "IS_LOGIN";*/
                                        } else {
                                            Constant.setAlert(Registration.this, result.message);
                                        }
                                    } else {
                                        Constant.setAlert(Registration.this, getResources().getString(R.string.wrong));
                                    }
                                }
                            });
                        }
                    } else {
                        Constant.setAlert(Registration.this, "You must agree to the terms and conditions to register");
                    }
                } else {
                    Constant.setAlert(Registration.this, getResources().getString(R.string.no_internet));
                }
            } else {
                Constant.setAlert(Registration.this, "Password must be at least of 6 digits.");
            }
        } else {
            Constant.setAlert(Registration.this, "Password and confirm password doesn't match.");
        }
    }

    private AlertDialog alert() {

        final String[] items = new String[]{getResources().getString(R.string.txt_takefromcamera), getResources().getString(R.string.txt_selectfromgallery)};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(Registration.this, android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(Registration.this);

        builder.setTitle(getResources().getString(R.string.txt_selectimage));
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(Environment.getExternalStorageDirectory(), "profilepic.png");
//					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, PICK_FROM_CAMERA);
                } else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_PICK);
                    startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.txt_completeActionusing)), PICK_FROM_FILE);
                }
            }
        });

        return builder.create();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            switch (requestCode) {
                case PICK_FROM_FILE:
                    if (resultCode == Activity.RESULT_OK) {
                        try {
                            Uri uri = data.getData();
                            try {
                                mPhotoPath = getRealPathFromURI(uri);
                                try {
                                    File fs = new File(mPhotoPath);
                                    FileInputStream fileInputStream = new FileInputStream(fs);
                                    Bitmap realImage = BitmapFactory.decodeStream(fileInputStream);
                                    //        mProfieImage.setImageBitmap(realImage);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        } catch (Exception ignored) {

                        }
                    }
                    break;

                case PICK_FROM_CAMERA:

                    if (resultCode == Activity.RESULT_OK) {
                        Bitmap photo = (Bitmap) data.getExtras().get("data");
                        //    mProfieImage.setImageBitmap(photo);

                        // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                        Uri tempUri = getImageUri(getApplicationContext(), photo);

                        // CALL THIS METHOD TO GET THE ACTUAL PATH
                        mPhotoPath = getRealPathFromURI2(tempUri);

                        break;
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private String getRealPathFromURI2(Uri uri) {
        @SuppressLint("Recycle") Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        assert cursor != null;
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
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
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Registration.this, LoginActivity.class));
    }

    class gcmTockenTask extends AsyncTask<Void, String, String> {
        @Override
        protected String doInBackground(Void... voids) {
            InstanceID instanceID = InstanceID.getInstance(Registration.this);
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
                Constant.setAlert(Registration.this, "Gcm not generated.");
                progressLoaderHelper.dismissProgress();
            } else if (tokenTag == 1) {
                progressLoaderHelper.dismissProgress();
                confirmPass();
                tokenTag = 2;
            } else {
                tokenTag = 1;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        tokenTag = 0;
    }
}
