package com.widevision.prayergrid.activity;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.Validator;
import com.widevision.prayergrid.R;
import com.widevision.prayergrid.util.Constant;
import com.widevision.prayergrid.util.Extension;
import com.widevision.prayergrid.util.PreferenceConnector;
import com.widevision.prayergrid.util.ProgressLoaderHelper;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mercury-five on 25/02/16.
 */
public class TestActivity extends AppCompatActivity{
    @Bind(R.id.frame)
    RelativeLayout frameLayout;
    @Bind(R.id.home)
    ImageView mHomeBtn;

    /*drawer item*/
    @Bind(R.id.userNameEdt)
    EditText mUserNameEdt;
    @Bind(R.id.profile_image)
    ImageView mProfileImage;
    @Bind(R.id.my_request)
    LinearLayout mMyRequestLayout;
    @Bind(R.id.bottom)
    LinearLayout mBottomLayout;
    @Bind(R.id.message)
    LinearLayout mMessageLayout;
    @Bind(R.id.my_prays)
    LinearLayout mMyPraysLayout;
    @Bind(R.id.country)
    Spinner mCountrySpinner;
    @Bind(R.id.category)
    Spinner mCategorySpinner;
    @Bind(R.id.prayer_request_layout)
    LinearLayout mPrayerRequestLayout;
    @Bind(R.id.answered_prayer_layout)
    LinearLayout mAnsweredPrayerLayout;
    @Bind(R.id.timeline_list)
    ListView mList;

    private ArrayList<String> categoryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();
        //  countries = DBHelper.getInstance().getCountryList();
        addCategory();
        //  CustomAdapter adapter = new CustomAdapter(HomeActivity.this, R.layout.place_spinner_row, countries, getResources());
//        mCountrySpinner.setAdapter(adapter);
       // CustomAdapter adapter2 = new CustomAdapter(TestActivity.this, R.layout.place_spinner_row, categoryList, getResources());
        mCategorySpinner.setAdapter(new ArrayAdapter<String>(TestActivity.this,R.layout.place_spinner_row,categoryList));

    }


    void init() {
        ButterKnife.bind(TestActivity.this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        // setupUI(frameLayout);


        Constant._id = PreferenceConnector.readString(TestActivity.this, PreferenceConnector.LOGIN_UserId, "");
        Constant._name = PreferenceConnector.readString(TestActivity.this, PreferenceConnector.NAME, "");
        Constant._email = PreferenceConnector.readString(TestActivity.this, PreferenceConnector.LOGIN_EMAIL, "");
       /* fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, TimeLineFragment.newInstance());
        fragmentTransaction.commit();*/
    }

    void addCategory() {
        categoryList.add("General");
        categoryList.add("School / Education");
        categoryList.add("Careers & Business");
        categoryList.add("Travel");
        categoryList.add("Finances");
        categoryList.add("Health / Healing");
        categoryList.add("Family & Relationships");
        categoryList.add("Addictions / Abuse");
        categoryList.add("Emotions");
    }


    /*****
     * Adapter class extends with ArrayAdapter
     ******/
    public class CustomAdapter extends ArrayAdapter<String> {

        private Activity activity;
        private ArrayList<String> data;
        public Resources res;
        LayoutInflater inflater;

        public CustomAdapter(Activity activitySpinner, int textViewResourceId, ArrayList<String> objects, Resources resLocal) {
            super(activitySpinner, textViewResourceId, objects);
            activity = activitySpinner;
            data = objects;
            res = resLocal;
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {
            View row = inflater.inflate(R.layout.place_spinner_row, parent, false);
            TextView label = (TextView) row.findViewById(R.id.placeSpn);
            label.setText(data.get(position));
            return row;
        }
    }
}
