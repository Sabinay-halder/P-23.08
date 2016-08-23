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
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.google.gson.Gson;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.widevision.prayergrid.R;
import com.widevision.prayergrid.dao.EditProfileDao;
import com.widevision.prayergrid.dao.GetJoinChurchListDao;
import com.widevision.prayergrid.dao.GsonClass;
import com.widevision.prayergrid.model.HideKeyActivity;
import com.widevision.prayergrid.util.AsyncCallback;
import com.widevision.prayergrid.util.Constant;
import com.widevision.prayergrid.util.DBHelper;
import com.widevision.prayergrid.util.Extension;
import com.widevision.prayergrid.util.PreferenceConnector;
import com.widevision.prayergrid.util.ProgressLoaderHelper;
import com.widevision.prayergrid.util.ValidationTemplate;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends HideKeyActivity implements View.OnClickListener, Validator.ValidationListener {


    @Bind(R.id.profile_image)
    CircleImageView mProfileImage;
    private final int PICK_FROM_CAMERA = 2;
    private final int PICK_FROM_FILE = 1;
    private String mPhotoPath = "";

    @Bind(R.id.radio_frist)
    RadioGroup mFirstRadio;
    @Bind(R.id.radio_second)
    RadioGroup mSecondRadio;

    @NotEmpty(message = "Please Enter your Name")
    @Bind(R.id.name)
    EditText mNameEdt;

    @Email(message = "Invalid Email")
    @Bind(R.id.mail)
    EditText mEmailEdt;

    @Bind(R.id.state)
    EditText mStateEdt;

    @Bind(R.id.church_spinner)
    Spinner mChurchSp;

    @Bind(R.id.country)
    Spinner mCountrySp;

    @Bind(R.id.gender_radio)
    RadioGroup mGenderRadio;
    @Bind(R.id.male)
    RadioButton mMale;
    @Bind(R.id.female)
    RadioButton mFeMale;

    @Bind(R.id.first_y)
    RadioButton mPrayYes;
    @Bind(R.id.first_n)
    RadioButton mPrayNo;

    @Bind(R.id.second_y)
    RadioButton mCommentYes;
    @Bind(R.id.second_n)
    RadioButton mCommentNo;

    @Bind(R.id.change_pass)
    TextView mChangePass;

    @Bind(R.id.pass_layout)
    LinearLayout mPassLayout;

    @Bind(R.id.password)
    EditText mCurrentPasswordTxt;
    @Bind(R.id.password_txt)
    EditText mPasswordTxt;
    @Bind(R.id.c_password_txt)
    EditText mConfirmPasswordTxt;

    @Bind(R.id.save_btn)
    Button mSaveBtn;

    @Bind(R.id.church_sp_layout)
    RelativeLayout mChurchLayout;
    @Bind(R.id.txt_church)
    LinearLayout txtChurch;

    @Bind(R.id.profile_name)
    TextView profile_name;

    @Bind(R.id.main_content_editProfile)
    RelativeLayout mainContent;

    private Extension extension;
    private ProgressLoaderHelper loaderHelper;
    private ArrayList<GsonClass.Post_info> list;
    private ArrayList<String> churchList = new ArrayList<>();

    int pray = 0, comment = 0;
    private String name = "", email = "", state = "", churchName = "", country = "", notify_prayer = "", notify_comment = "", genderStr = "";
    String currentPassStr = "", passStr = "", confirmPassStr = "";

    private AQuery aQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.pull_out_to_left_anim, R.anim.pull_infromright_anim);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);
        init();
        mPassLayout.setVisibility(View.GONE);
        mProfileImage.setOnClickListener(this);
        mSaveBtn.setOnClickListener(this);

        mChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constant.buttonEnable) {
                    Constant.setButtonEnable();
                    if (mPassLayout.getVisibility() == View.VISIBLE) {
                        mPassLayout.setVisibility(View.GONE);
                    } else {
                        mPassLayout.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        getJoinChurchList();
        setAllFields();
    }

    private void setAllFields() {
        aQuery = new AQuery(EditProfileActivity.this);
        mNameEdt.setText(Constant._name);
        mEmailEdt.setText(Constant._email);
        mStateEdt.setText(Constant._state);
        profile_name.setText(Constant._name);
      /*  if (!Constant._country.isEmpty()) {
            int s = DBHelper.countryBeanArrayList.size();
            int index = 0;
            for (int i = 0; i < s; i++) {
                if (country.equals(DBHelper.countryBeanArrayList.get(i).getCountryCode())) {
                    index = i;
                    break;
                }
            }
            mCountrySp.setSelection(index);
        }*/
        if (Constant._gender.equals("Male")) {
            mMale.setSelected(true);
            mMale.setChecked(true);
        } else if (Constant._gender.equals("Female")) {
            mFeMale.setSelected(true);
            mFeMale.setChecked(true);
        }
        if (!Constant._profile_pic.equals("")) {
            aQuery.id(mProfileImage).image(Constant._profile_pic, true, true, 100, R.drawable.default_pic);
        }
        if (Constant._notify_prays.equals("1")) {
            mPrayYes.setSelected(true);
            mPrayYes.setChecked(true);
        } else if (Constant._notify_prays.equals("0")) {
            mPrayNo.setSelected(true);
            mPrayNo.setChecked(true);
        }

        if (Constant._notify_comments.equals("1")) {
            mCommentYes.setSelected(true);
            mCommentYes.setChecked(true);
        } else if (Constant._notify_comments.equals("0")) {
            mCommentNo.setSelected(true);
            mCommentNo.setChecked(true);
        }
    }

    private void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        extension = new Extension();
        loaderHelper = ProgressLoaderHelper.getInstance();

        mCountrySp.setAdapter(new ArrayAdapter<String>(EditProfileActivity.this, android.R.layout.simple_spinner_dropdown_item, HomeActivity.countries) {
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

        mChurchSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        if (!Constant._country.equals("")) {
            int index = HomeActivity.countries.indexOf(Constant._country);
            mCountrySp.setSelection(index);
        }
        setupUI(mainContent);
    }

    private void getJoinChurchList() {
        if (extension.executeStrategy(EditProfileActivity.this, "", ValidationTemplate.INTERNET)) {
            final GetJoinChurchListDao listDao = new GetJoinChurchListDao(Constant._id);
            listDao.query(new AsyncCallback<GsonClass>() {
                @Override
                public void onOperationCompleted(GsonClass result, Exception e) {
                    if (e == null && result != null) {
                        if (result.success.equals("1")) {
                            if (result.post_info != null && result.post_info.size() != 0) {
                                mChurchLayout.setVisibility(View.VISIBLE);
                                txtChurch.setVisibility(View.VISIBLE);
                                list = result.post_info;
                                int size = list.size();
                                for (int a = 0; a < size; a++) {
                                    churchList.add(list.get(a).name);
                                }
                                churchList.add(0, "Select Church");
                                list.add(0, null);
                                mChurchSp.setAdapter(new ArrayAdapter<String>(EditProfileActivity.this, android.R.layout.simple_spinner_dropdown_item, churchList) {
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
                                        return view;
                                    }
                                });
                                if (!Constant._church_name.equals("")) {
                                    int index = churchList.indexOf(Constant._church_name);
                                    mChurchSp.setSelection(index);
                                }
                            } else {
                                mChurchLayout.setVisibility(View.GONE);
                                txtChurch.setVisibility(View.GONE);
                            }
                        } else {
                            mChurchLayout.setVisibility(View.GONE);
                            txtChurch.setVisibility(View.GONE);
                        }
                    } else {
                        mChurchLayout.setVisibility(View.GONE);
                        txtChurch.setVisibility(View.GONE);
                    }
                }
            });
        } else {
            mChurchLayout.setVisibility(View.GONE);
            Constant.setAlert(EditProfileActivity.this, getString(R.string.no_internet));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.profile_image:
                alert().show();
                break;
            case R.id.save_btn:
                updateProfile();
                break;
        }
    }

    private void updateProfile() {
        name = mNameEdt.getText().toString().trim();
        email = mEmailEdt.getText().toString().trim();
        state = mStateEdt.getText().toString().trim();
        churchName = "";
        if (mChurchLayout.getVisibility() == View.VISIBLE) {
            churchName = (String) mChurchSp.getSelectedItem();
            if (churchName.equals("Select Church")) {
                churchName = "";
            } else {
                Constant._church_name = churchName;
            }
        }
        country = (String) mCountrySp.getSelectedItem();

        int a = mFirstRadio.getCheckedRadioButtonId();
        int b = mSecondRadio.getCheckedRadioButtonId();

        if (a > -1) {
            RadioButton rb1 = (RadioButton) findViewById(a);
            notify_prayer = rb1.getText().toString().trim();
        }
        if (b > -1) {
            RadioButton rb2 = (RadioButton) findViewById(b);
            notify_comment = rb2.getText().toString().trim();
        }

        int gId = mGenderRadio.getCheckedRadioButtonId();

        if (gId > -1) {
            RadioButton gender = (RadioButton) findViewById(gId);
            genderStr = gender.getText().toString().trim();
        }

        if (name.equals("")) {
            Constant.setAlert(EditProfileActivity.this, "Please enter your name.");
            return;
        }
        if (email.equals("") || !extension.executeStrategy(EditProfileActivity.this, email, ValidationTemplate.EMAIL)) {
            Constant.setAlert(EditProfileActivity.this, "Please enter valid email id.");
            return;
        }
        if (state.equals("")) {
            state = Constant._state;
        }
        if (country.equals("All Countries")) {
            Constant.setAlert(EditProfileActivity.this, "Please select your country.");
            return;
        }

        if (notify_prayer.equals("Yes")) {
            pray = 1;
        }
        if (notify_comment.equals("Yes")) {
            comment = 1;
        }
        /*{"feed":"0","pray":"1","comment":"1"}*/
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("feed", "0");
            jsonObject.put("pray", "" + pray);
            jsonObject.put("comment", "" + comment);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (mPassLayout.getVisibility() == View.VISIBLE) {
            currentPassStr = mCurrentPasswordTxt.getText().toString().trim();
            passStr = mPasswordTxt.getText().toString().trim();
            confirmPassStr = mConfirmPasswordTxt.getText().toString().trim();
            if (currentPassStr.equals("")) {
                Constant.setAlert(EditProfileActivity.this, "Enter your current password.");
                return;
            }
            if (passStr.equals("")) {
                Constant.setAlert(EditProfileActivity.this, "Enter your Password.");
                return;
            }

            if (!currentPassStr.equals(Constant._password)) {
                Constant.setAlert(EditProfileActivity.this, "Current password not match.");
                return;
            }
            if (!passStr.equals(confirmPassStr)) {
                Constant.setAlert(EditProfileActivity.this, "Password didn't match.");
                return;
            }
            if (passStr.length() < 6) {
                Constant.setAlert(EditProfileActivity.this, "Password length at least 6 digits.");
                return;
            }
        } else {
            passStr = Constant._password;
        }

        Constant._password = passStr;

        if (extension.executeStrategy(EditProfileActivity.this, "", ValidationTemplate.INTERNET)) {

            final ProgressLoaderHelper progressLoaderHelper = ProgressLoaderHelper.getInstance();
            progressLoaderHelper.showProgress(EditProfileActivity.this);

            File f = null;
            EditProfileDao dao;
            if (!mPhotoPath.equals("")) {
                f = new File(mPhotoPath);
                dao = new EditProfileDao(Constant._id, name, email, f, genderStr, state, country, jsonObject.toString(), passStr, churchName);
            } else {
                dao = new EditProfileDao(Constant._id, name, email, Constant._profile_pic, genderStr, country, state, jsonObject.toString(), passStr, churchName);
            }

            dao.query(new AsyncCallback<GsonClass>() {
                @Override
                public void onOperationCompleted(GsonClass result, Exception e) {
                    progressLoaderHelper.dismissProgress();
                    if (e == null && result != null) {
                        if (result.success.equals("1")) {
                            finish();
                            Toast.makeText(EditProfileActivity.this, "Profile updated successfully.", Toast.LENGTH_SHORT).show();
                            PreferenceConnector.writeString(EditProfileActivity.this, PreferenceConnector.LOGIN_EMAIL, email);
                            PreferenceConnector.writeString(EditProfileActivity.this, PreferenceConnector.NAME, name);
                            PreferenceConnector.writeString(EditProfileActivity.this, PreferenceConnector.GENDER, genderStr);
                            PreferenceConnector.writeString(EditProfileActivity.this, PreferenceConnector.COUNTRY, country);
                            PreferenceConnector.writeString(EditProfileActivity.this, PreferenceConnector.STATE, state);
                            PreferenceConnector.writeString(EditProfileActivity.this, PreferenceConnector.NOTIFY_PRAYS, "" + pray);
                            PreferenceConnector.writeString(EditProfileActivity.this, PreferenceConnector.NOTIFY_COMMENTS, "" + comment);
                            PreferenceConnector.writeString(EditProfileActivity.this, PreferenceConnector.PROFILE_PIC, result.url);
                            PreferenceConnector.writeString(EditProfileActivity.this, PreferenceConnector.PASSWORD, Constant._password);

                            Constant._email = email;
                            Constant._name = name;
                            Constant._gender = genderStr;
                            Constant._country = country;
                            Constant._state = state;
                            Constant._notify_prays = "" + pray;
                            Constant._notify_comments = "" + comment;
                            Constant._profile_pic = result.url;

                        } else {
                            Constant.setAlert(EditProfileActivity.this, result.message);
                        }
                    } else {
                        Constant.setAlert(EditProfileActivity.this, getString(R.string.wrong));
                    }
                }
            });
        } else {
            Constant.setAlert(EditProfileActivity.this, getString(R.string.no_internet));
        }

    }

    private AlertDialog alert() {
        final String[] items = new String[]{getResources().getString(R.string.txt_takefromcamera), getResources().getString(R.string.txt_selectfromgallery)};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(EditProfileActivity.this, android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
        builder.setTitle(getResources().getString(R.string.txt_selectimage));
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(Environment.getExternalStorageDirectory(), "profilepic.png");
                    //intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
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
                                    mProfileImage.setImageBitmap(realImage);
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
                        try {
                            File fs = new File(mPhotoPath);
                            FileInputStream fileInputStream = new FileInputStream(fs);
                            Bitmap realImage = BitmapFactory.decodeStream(fileInputStream);
                            mProfileImage.setImageBitmap(realImage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
    public void onValidationSucceeded() {

    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        Constant.setAlert(EditProfileActivity.this, errors.get(0).getCollatedErrorMessage(EditProfileActivity.this));
    }
}