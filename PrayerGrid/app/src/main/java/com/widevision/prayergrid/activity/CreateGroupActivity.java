package com.widevision.prayergrid.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.widevision.prayergrid.R;
import com.widevision.prayergrid.dao.CreateGroupDao;
import com.widevision.prayergrid.dao.GsonClass;
import com.widevision.prayergrid.model.HideKeyActivity;
import com.widevision.prayergrid.util.AsyncCallback;
import com.widevision.prayergrid.util.Constant;
import com.widevision.prayergrid.util.DBHelper;
import com.widevision.prayergrid.util.Extension;
import com.widevision.prayergrid.util.ProgressLoaderHelper;
import com.widevision.prayergrid.util.ValidationTemplate;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CreateGroupActivity extends HideKeyActivity {

    private Extension extension;
    private ProgressLoaderHelper progressLoaderHelper;

    @Bind(R.id.main_container)
    CoordinatorLayout mainLayout;

    @Bind(R.id.c0ver_grid)
    GridView coverGrid;

    @Bind(R.id.profile_grid)
    GridView profileGrid;


    @Bind(R.id.type_radio_group)
    RadioGroup typeRadioGroup;
    @Bind(R.id.privacy)
    RadioGroup mPrivacyRadionGroup;
    @Bind(R.id.give_tab)
    RadioGroup mGiveTabRadionGroup;
    @Bind(R.id.contactRadio)
    RadioGroup mContactRadionGroup;
    @Bind(R.id.meeting)
    RadioGroup mMeetingRadioGroup;
    @Bind(R.id.location)
    RadioGroup mLocationRadioGroup;
    @Bind(R.id.call)
    RadioGroup mCallRadioGroup;
    @Bind(R.id.volunteer)
    RadioGroup mVolunteerRadioGroup;
    @Bind(R.id.member)
    RadioGroup mMemberRadioGroup;

    @Bind(R.id.group_name)
    EditText mGroupNameEdt;
    @Bind(R.id.give_link)
    EditText mGiveLinkEdt;
    @Bind(R.id.meeting_time)
    EditText mMeetingTimeEdt;
    @Bind(R.id.news)
    EditText mNewsEdt;
    @Bind(R.id.email)
    EditText mEmailEdt;
    @Bind(R.id.city)
    EditText mCityEdt;
    @Bind(R.id.state)
    EditText mStateEdt;
    @Bind(R.id.number)
    EditText mNumberEdt;
    @Bind(R.id.volunteer_link)
    EditText mVolunteerLinkEdt;
    @Bind(R.id.give_radio)
    RadioButton mGiveImageRadio;
    @Bind(R.id.create_group_btn)
    Button mCreateBtn;
    @Bind(R.id.address)
    EditText mAddressEdt;
    @Bind(R.id.country)
    Spinner mCountrySp;

    @Bind(R.id.v_1)
    ImageView v1;
    @Bind(R.id.v_2)
    ImageView v2;
    @Bind(R.id.v_3)
    ImageView v3;

    @Bind(R.id.v_image_1)
    ImageView v_image1;
    @Bind(R.id.v_image_2)
    ImageView v_image2;
    @Bind(R.id.v_image_3)
    ImageView v_image3;

    @Bind(R.id.cover_grid_btn)
    Button mUploadCoverBtn;
    @Bind(R.id.profile_grid_btn)
    Button mUploadProfileBtn;

    private final int PICK_FROM_CAMERA = 2;
    private final int PICK_FROM_FILE = 1;
    private final int COVER_IMAGE = 1;
    private final int PROFILE_PIC = 2;
    private String mCoverPhotoPath = "";
    private String mProfilePhotoPath = "";
    private static int ImageFor = 0;

    private int selected_cover_pos = -1;
    private int selected_profile_pos = -1;

    private GridAdapter gridAdapter;
    private GridAdapterProfile gridAdapterProfile;

    private int covers[] = {R.drawable.boat, R.drawable.canyon, R.drawable.chess, R.drawable.children, R.drawable.dog, R.drawable.lake, R.drawable.men, R.drawable.military, R.drawable.rockinghorse, R.drawable.women};
    private int profile[] = {R.drawable.profile_business, R.drawable.profile_ministry, R.drawable.profile_people};

    /*The type field is required.
    * The privacy field is required.*/

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.pull_out_to_left_anim, R.anim.pull_infromright_anim);
        setContentView(R.layout.activity_create_group);
        init();


        gridAdapter = new GridAdapter(CreateGroupActivity.this);
        gridAdapterProfile = new GridAdapterProfile(CreateGroupActivity.this);
        coverGrid.setAdapter(gridAdapter);
        profileGrid.setAdapter(gridAdapterProfile);

        ViewGroup.LayoutParams p = setListViewHeightBasedOnChildren(coverGrid);
        coverGrid.setLayoutParams(p);

        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constant.buttonEnable) {
                    Constant.setButtonEnable();
                    checkValidation();
                }
            }
        });

        mCountrySp.setAdapter(new ArrayAdapter<String>(CreateGroupActivity.this, R.layout.row_spinner, HomeActivity.countries) {
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


        mUploadProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constant.buttonEnable) {
                    Constant.setButtonEnable();
                    ImageFor = 2;
                    selected_profile_pos = -1;
                    gridAdapterProfile.notifyDataSetChanged();
                    alert().show();
                }
            }
        });

        mUploadCoverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constant.buttonEnable) {
                    Constant.setButtonEnable();
                    ImageFor = 1;
                    selected_cover_pos = -1;
                    gridAdapter.notifyDataSetChanged();
                    alert().show();
                }
            }
        });

        v_image1.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.volunteer1, 100, 100));
        v_image2.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.volunteer2, 100, 100));
        v_image3.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.volunteer3, 100, 100));
        v_image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                v1.setVisibility(View.VISIBLE);
                v2.setVisibility(View.GONE);
                v3.setVisibility(View.GONE);
            }
        });
        v_image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                v1.setVisibility(View.GONE);
                v2.setVisibility(View.VISIBLE);
                v3.setVisibility(View.GONE);
            }
        });
        v_image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                v1.setVisibility(View.GONE);
                v2.setVisibility(View.GONE);
                v3.setVisibility(View.VISIBLE);
            }
        });
    }

    private void checkValidation() {

        int typeCheck = typeRadioGroup.getCheckedRadioButtonId();
        if (typeCheck < 0) {
            Constant.setAlert(CreateGroupActivity.this, "The type field is required.");
            return;
        }
        int p = mPrivacyRadionGroup.getCheckedRadioButtonId();
        if (p < 0) {
            Constant.setAlert(CreateGroupActivity.this, "The privacy field is required.");
            return;
        }
        int g = mGiveTabRadionGroup.getCheckedRadioButtonId();
        int c = mContactRadionGroup.getCheckedRadioButtonId();
        int m = mMeetingRadioGroup.getCheckedRadioButtonId();
        int l = mLocationRadioGroup.getCheckedRadioButtonId();
        int cc = mCallRadioGroup.getCheckedRadioButtonId();
        int v = mVolunteerRadioGroup.getCheckedRadioButtonId();
        int mm = mMemberRadioGroup.getCheckedRadioButtonId();

        RadioButton groupTypeButtom = (RadioButton) findViewById(typeCheck);
        String groupType = groupTypeButtom.getText().toString().trim();
        int gType = -1;
        if (groupType.equals("Ministry")) {
            gType = 1;
        } else if (groupType.equals("Workplace")) {
            gType = 2;
        } else if (groupType.equals("People")) {
            gType = 3;
        }

        String groupName = mGroupNameEdt.getText().toString().trim();
        String giveLink = mGiveLinkEdt.getText().toString().trim();
        String contactEmail = mEmailEdt.getText().toString().trim();
        String meetingTime = mMeetingTimeEdt.getText().toString().trim();
        String news = mNewsEdt.getText().toString().trim();
        String address = mAddressEdt.getText().toString().trim();
        String city = mCityEdt.getText().toString().trim();
        String state = mStateEdt.getText().toString().trim();
        String phone = mNumberEdt.getText().toString().trim();
        String volunteer = mVolunteerLinkEdt.getText().toString().trim();
        String country = (String) mCountrySp.getSelectedItem();
        if (groupName.isEmpty()) {
            Constant.setAlert(CreateGroupActivity.this, "Please include name for your group.");
            return;
        } else if (giveLink.isEmpty()) {
            Constant.setAlert(CreateGroupActivity.this, "Please enter give link.");
            return;
        } else if (contactEmail.isEmpty() || !extension.executeStrategy(CreateGroupActivity.this, contactEmail, ValidationTemplate.EMAIL)) {
            if (contactEmail.isEmpty()) {
                Constant.setAlert(CreateGroupActivity.this, "Please enter your email address");
            } else {
                Constant.setAlert(CreateGroupActivity.this, "Please enter valid email address");
            }
            return;
        } else if (meetingTime.isEmpty()) {
            Constant.setAlert(CreateGroupActivity.this, "Please enter your meeting time.");
            return;
        } else if (news.isEmpty()) {
            Constant.setAlert(CreateGroupActivity.this, "Please enter News field.");
            return;
        } else if (country.equals("All Countries")) {
            Constant.setAlert(CreateGroupActivity.this, "Please select your country.");
            return;
        } else if (address.isEmpty()) {
            Constant.setAlert(CreateGroupActivity.this, "Please enter your address.");
            return;
        } else if (city.isEmpty()) {
            Constant.setAlert(CreateGroupActivity.this, "Please enter your city.");
            return;
        } else if (state.isEmpty()) {
            Constant.setAlert(CreateGroupActivity.this, "Please enter your state.");
            return;
        } else if (phone.isEmpty() || !extension.executeStrategy(CreateGroupActivity.this, phone, ValidationTemplate.isnumber)) {
            if (phone.isEmpty()) {
                Constant.setAlert(CreateGroupActivity.this, "Please enter your phone number.");
            } else {
                Constant.setAlert(CreateGroupActivity.this, "Please enter valid phone number.");
            }
            return;
        } else if (volunteer.isEmpty()) {
            Constant.setAlert(CreateGroupActivity.this, "Please enter volunteer link.");
            return;
        }

        int a = mCountrySp.getSelectedItemPosition();
        country = DBHelper.countryBeanArrayList.get(a).getCountryCode();

        RadioButton privacyButton = (RadioButton) findViewById(p);
        int privacreType = -1;
        if (privacyButton.getText().toString().trim().equals("Invite only")) {
            privacreType = 1;
        } else if (privacyButton.getText().toString().trim().equals("Public")) {
            privacreType = 0;
        }

        RadioButton giveButton = (RadioButton) findViewById(g);
        RadioButton contactButton = (RadioButton) findViewById(c);
        RadioButton meetingButton = (RadioButton) findViewById(m);
        RadioButton locationButton = (RadioButton) findViewById(l);
        RadioButton callButton = (RadioButton) findViewById(cc);
        RadioButton volunteerButton = (RadioButton) findViewById(v);
        RadioButton memberButton = (RadioButton) findViewById(mm);

        int give_t, contact_t, meeting_t, location_t, call_t, volunteer_t, member_t;
        if (giveButton.getText().toString().trim().equals("Yes")) {
            give_t = 1;
        } else {
            give_t = 0;
        }

        if (contactButton.getText().toString().trim().equals("Yes")) {
            contact_t = 1;
        } else {
            contact_t = 0;
        }

        if (meetingButton.getText().toString().trim().equals("Yes")) {
            meeting_t = 1;
        } else {
            meeting_t = 0;
        }

        if (locationButton.getText().toString().trim().equals("Yes")) {
            location_t = 1;
        } else {
            location_t = 0;
        }

        if (callButton.getText().toString().trim().equals("Yes")) {
            call_t = 1;
        } else {
            call_t = 0;
        }
        if (volunteerButton.getText().toString().trim().equals("Yes")) {
            volunteer_t = 1;
        } else {
            volunteer_t = 0;
        }
        if (memberButton.getText().toString().trim().equals("Yes")) {
            member_t = 1;
        } else {
            member_t = 0;
        }
        String v_name = "";
        if (v1.getVisibility() == View.VISIBLE) {
            v_name = "volunteer1";
        } else if (v2.getVisibility() == View.VISIBLE) {
            v_name = "volunteer2";
        } else if (v3.getVisibility() == View.VISIBLE) {
            v_name = "volunteer3";
        }

        /*{"give":{"image":"Give.jpg","link":"http:\/\/www.christislove.org"},"contact_email":"haddox186@yahoo.com","time":"Tuesdays: 8:00 am","location":{"address":"Chick-fil-a","city":"","state":"","country":""},"phone":"888-888-8888","volunteer":{"image":"Volunteer1.jpg","link":""}}
*/
        JSONObject detailJonsObject = null;
        JSONObject settingJsonObject = null;
        /*For details tag*/
        try {
            detailJonsObject = new JSONObject();
            JSONObject giveJsonObject = new JSONObject();
            giveJsonObject.put("image", "Give.jpg");
            giveJsonObject.put("link", "http://www.christislove.org");
            detailJonsObject.put("give", giveJsonObject);
            detailJonsObject.put("contact_email", contactEmail);
            detailJonsObject.put("time", meetingTime);

            JSONObject locationJsonObject = new JSONObject();
            locationJsonObject.put("address", address);
            locationJsonObject.put("city", city);
            locationJsonObject.put("state", state);
            locationJsonObject.put("country", country);
            detailJonsObject.put("location", locationJsonObject);
            detailJonsObject.put("phone", phone);

            JSONObject volunteerJsonObject = new JSONObject();
            volunteerJsonObject.put("image", v_name);
            volunteerJsonObject.put("link", "");
            detailJonsObject.put("volunteer", volunteerJsonObject);

            /*Setting data start*/
            settingJsonObject = new JSONObject();
            settingJsonObject.put("give", "" + give_t);
            settingJsonObject.put("contact", "" + contact_t);
            settingJsonObject.put("time", "" + meeting_t);
            settingJsonObject.put("location", "" + location_t);
            settingJsonObject.put("location", "" + location_t);
            settingJsonObject.put("phone", "" + call_t);
            settingJsonObject.put("volunteer", "" + volunteer_t);
            settingJsonObject.put("memberapprove", "" + member_t);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        byte[] cover_byte_array = null;
        byte[] profile_byte_array = null;

        if (mCoverPhotoPath.equals("")) {
            if (selected_cover_pos == -1) {
                Constant.setAlert(CreateGroupActivity.this, "Please upload Cover image.");
                return;
            } else {
                int coverdrawable = covers[selected_cover_pos];
                Bitmap bmp = BitmapFactory.decodeResource(getResources(), coverdrawable);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                cover_byte_array = stream.toByteArray();
            }
        }
        if (mProfilePhotoPath.equals("")) {
            if (selected_profile_pos == -1) {
                Constant.setAlert(CreateGroupActivity.this, "Please upload Profile image.");
                return;
            } else {
                int profiledrawable = profile[selected_profile_pos];
                Bitmap bmp = BitmapFactory.decodeResource(getResources(), profiledrawable);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                profile_byte_array = stream.toByteArray();
            }
        }

        if (extension.executeStrategy(CreateGroupActivity.this, "", ValidationTemplate.INTERNET)) {
            progressLoaderHelper.showProgress(CreateGroupActivity.this);
            CreateGroupDao dao = null;
            if (!mCoverPhotoPath.equals("") && !mProfilePhotoPath.equals("")) {
                dao = new CreateGroupDao(Constant._id, groupName, "" + gType, "" + privacreType, "" + detailJonsObject.toString(), "" + settingJsonObject.toString(), news, "status", new File(mCoverPhotoPath), new File(mProfilePhotoPath));
            } else if (cover_byte_array != null && profile_byte_array != null) {
                dao = new CreateGroupDao(Constant._id, groupName, "" + gType, "" + privacreType, "" + detailJonsObject.toString(), "" + settingJsonObject.toString(), news, "status", cover_byte_array, profile_byte_array);
            } else if (mProfilePhotoPath.equals("") && profile_byte_array != null) {
                dao = new CreateGroupDao(Constant._id, groupName, "" + gType, "" + privacreType, "" + detailJonsObject.toString(), "" + settingJsonObject.toString(), news, "status", new File(mCoverPhotoPath), profile_byte_array);
            } else if (mCoverPhotoPath.equals("") && cover_byte_array != null) {
                dao = new CreateGroupDao(Constant._id, groupName, "" + gType, "" + privacreType, "" + detailJonsObject.toString(), "" + settingJsonObject.toString(), news, "status", cover_byte_array, new File(mProfilePhotoPath));
            } else {
                Constant.setAlert(CreateGroupActivity.this, "Select cover & profile pic.");
                return;
            }

            dao.query(new AsyncCallback<GsonClass>() {
                @Override
                public void onOperationCompleted(GsonClass result, Exception e) {
                    progressLoaderHelper.dismissProgress();
                    if (e == null && result != null) {
                        if (result.success.equals("1")) {
                            finish();
                            Toast.makeText(CreateGroupActivity.this, "Group Created successfully.", Toast.LENGTH_SHORT).show();
                        } else {
                            Constant.setAlert(CreateGroupActivity.this, result.message);
                        }
                    } else {
                        Constant.setAlert(CreateGroupActivity.this, getString(R.string.wrong));
                    }
                }
            });
        } else {
            Constant.setAlert(CreateGroupActivity.this, getString(R.string.no_internet));
        }
    }

    private void init() {
        ButterKnife.bind(CreateGroupActivity.this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        extension = new Extension();
        progressLoaderHelper = ProgressLoaderHelper.getInstance();
        setupUI(mainLayout);
    }

    public ViewGroup.LayoutParams setListViewHeightBasedOnChildren(GridView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return null;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, AbsListView.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = (totalHeight + (listView.getVerticalSpacing() * (listAdapter.getCount() - 1)))/2;

        return params;
    }

    class GridAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater layoutInflater;
        private ViewHolder viewHolder;

        public GridAdapter(Context context) {
            this.context = context;
            layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            return covers.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {

            if (view == null) {
                view = layoutInflater.inflate(R.layout.cover_grid_row, viewGroup, false);
                viewHolder = new ViewHolder();
                viewHolder.image = (ImageView) view.findViewById(R.id.image);
                viewHolder.checkBox = (ImageView) view.findViewById(R.id.check);
                viewHolder.main = (RelativeLayout) view.findViewById(R.id.main);

                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            viewHolder.image.setImageBitmap(decodeSampledBitmapFromResource(getResources(), covers[i], 30, 30));

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selected_cover_pos = i;
                    notifyDataSetChanged();
                }
            });
            if (selected_cover_pos == i) {
                viewHolder.checkBox.setVisibility(View.VISIBLE);
            } else {
                viewHolder.checkBox.setVisibility(View.GONE);
            }
            return view;
        }

        class ViewHolder {
            ImageView image;
            ImageView checkBox;
            RelativeLayout main;
        }
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    class GridAdapterProfile extends BaseAdapter {

        private Context context;
        private LayoutInflater layoutInflater;
        private ViewHolder viewHolder;

        public GridAdapterProfile(Context context) {
            this.context = context;
            layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            return profile.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {

            if (view == null) {
                view = layoutInflater.inflate(R.layout.cover_grid_row, viewGroup, false);
                viewHolder = new ViewHolder();
                viewHolder.image = (ImageView) view.findViewById(R.id.image);
                viewHolder.checkBox = (ImageView) view.findViewById(R.id.check);

                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            viewHolder.image.setImageBitmap(decodeSampledBitmapFromResource(getResources(), profile[i], 100, 100));

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selected_profile_pos = i;
                    notifyDataSetChanged();
                }
            });
            if (selected_profile_pos == i) {
                viewHolder.checkBox.setVisibility(View.VISIBLE);
            } else {
                viewHolder.checkBox.setVisibility(View.GONE);
            }
            return view;
        }

        class ViewHolder {
            ImageView image;
            ImageView checkBox;
        }
    }

    private AlertDialog alert() {

        final String[] items = new String[]{getResources().getString(R.string.txt_takefromcamera), getResources().getString(R.string.txt_selectfromgallery)};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(CreateGroupActivity.this, android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateGroupActivity.this);

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
                        if (ImageFor == COVER_IMAGE) {
                            try {
                                Uri uri = data.getData();
                                try {
                                    mCoverPhotoPath = getRealPathFromURI(uri);
                                    try {
                                        File fs = new File(mCoverPhotoPath);
                                        FileInputStream fileInputStream = new FileInputStream(fs);
                                        Bitmap realImage = BitmapFactory.decodeStream(fileInputStream);
                                        //     mProfileImage.setImageBitmap(realImage);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            } catch (Exception ignored) {

                            }
                        } else if (ImageFor == PROFILE_PIC) {
                            try {
                                Uri uri = data.getData();
                                try {
                                    mProfilePhotoPath = getRealPathFromURI(uri);
                                    try {
                                        File fs = new File(mProfilePhotoPath);
                                        FileInputStream fileInputStream = new FileInputStream(fs);
                                        Bitmap realImage = BitmapFactory.decodeStream(fileInputStream);
                                        //       mProfileImage.setImageBitmap(realImage);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            } catch (Exception ignored) {

                            }
                        }
                    }
                    break;

                case PICK_FROM_CAMERA:
                    if (resultCode == Activity.RESULT_OK) {
                        if (ImageFor == COVER_IMAGE) {
                            Bitmap photo = (Bitmap) data.getExtras().get("data");
                            //    mProfieImage.setImageBitmap(photo);

                            // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                            Uri tempUri = getImageUri(getApplicationContext(), photo);

                            // CALL THIS METHOD TO GET THE ACTUAL PATH
                            mCoverPhotoPath = getRealPathFromURI2(tempUri);
                            try {
                                File fs = new File(mCoverPhotoPath);
                                FileInputStream fileInputStream = new FileInputStream(fs);
                                Bitmap realImage = BitmapFactory.decodeStream(fileInputStream);
                                //     mProfileImage.setImageBitmap(realImage);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (ImageFor == PROFILE_PIC) {
                            Bitmap photo = (Bitmap) data.getExtras().get("data");
                            //    mProfieImage.setImageBitmap(photo);

                            // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                            Uri tempUri = getImageUri(getApplicationContext(), photo);

                            // CALL THIS METHOD TO GET THE ACTUAL PATH
                            mProfilePhotoPath = getRealPathFromURI2(tempUri);
                            try {
                                File fs = new File(mProfilePhotoPath);
                                FileInputStream fileInputStream = new FileInputStream(fs);
                                Bitmap realImage = BitmapFactory.decodeStream(fileInputStream);
                                //        mProfileImage.setImageBitmap(realImage);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ImageFor = 0;
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

}