package com.widevision.prayergrid.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.androidquery.AQuery;
import com.widevision.prayergrid.R;
import com.widevision.prayergrid.fragment.ChurchListFragment;
import com.widevision.prayergrid.fragment.ChurchProfileFragment;
import com.widevision.prayergrid.fragment.MinistryListFragment;
import com.widevision.prayergrid.fragment.MyPeopleListFragment;
import com.widevision.prayergrid.fragment.MyPrayerListFragment;
import com.widevision.prayergrid.fragment.MyPrayerForOtherFragment;
import com.widevision.prayergrid.fragment.TimeLineFragment;
import com.widevision.prayergrid.fragment.WorkplaceListFragment;
import com.widevision.prayergrid.model.HideKeyActivity;
import com.widevision.prayergrid.util.Constant;
import com.widevision.prayergrid.util.DBHelper;
import com.widevision.prayergrid.util.PreferenceConnector;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HomeActivity extends HideKeyActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Bind(R.id.frame)
    FrameLayout frameLayout;
    @Bind(R.id.home)
    ImageView mHomeBtn;

    /*drawer item*/
    @Bind(R.id.userNameEdt)
    EditText mUserNameEdt;
    @Bind(R.id.profile_image)
    ImageView mProfileImage;
    @Bind(R.id.my_request)
    LinearLayout mMyRequestLayout;
    @Bind(R.id.bottom_logout)
    LinearLayout mBottomLayout;
    @Bind(R.id.message)
    LinearLayout mMessageLayout;
    @Bind(R.id.my_prays)
    LinearLayout mMyPraysLayout;
    @Bind(R.id.create_group)
    LinearLayout mCreateGroupLayout;
    @Bind(R.id.edit_profile)
    Button mEditProfileBtn;

    @Bind(R.id.donation)
    LinearLayout mDonationBtn;
    @Bind(R.id.church)
    LinearLayout mChurchBtn;
    @Bind(R.id.workspace)
    LinearLayout mWorkplcaeBtn;
    @Bind(R.id.mmy_people)
    LinearLayout mPeopleBtn;
    @Bind(R.id.ministry)
    LinearLayout mMinistoryBtn;

    @Bind(R.id.invitation)
    LinearLayout mInvitationBtn;

    public static ArrayList<String> countries;
    public static FragmentManager fragmentManager;
    private AQuery aQuery;
    private static int church_default = 0;//if 1 then open church and if 0 then its go to home......

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.pull_out_to_left_anim, R.anim.pull_infromright_anim);
        setContentView(R.layout.activity_home);
        init();
        countries = DBHelper.getInstance().getCountryList();

        mMyRequestLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeDrawer();
                if (Constant.buttonEnable) {
                    Constant.setButtonEnable();
                    FragmentTransaction fTransaction = fragmentManager.beginTransaction();
                    fragmentManager.popBackStack("MyPrayerListFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    fTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    fTransaction.add(R.id.frame, MyPrayerListFragment.newInstance()).addToBackStack("MyPrayerListFragment").commit();

                }
            }
        });

        mInvitationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeDrawer();
                if (Constant.buttonEnable) {
                    Constant.setButtonEnable();
                    startActivity(new Intent(HomeActivity.this, ViewInvitationActivity.class));
                }

            }
        });

        mMessageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeDrawer();
                if (Constant.buttonEnable) {
                    Constant.setButtonEnable();
                   /* FragmentTransaction fTransaction = fragmentManager.beginTransaction();
                    fragmentManager.popBackStack("ViewMessageFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    fTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    fTransaction.add(R.id.frame, ViewMessageFragment.newInstance()).addToBackStack("ViewMessageFragment").commit();
               */
                    startActivity(new Intent(HomeActivity.this, ViewUserMessageList.class));
                }

                /*  Constant.setAlert(HomeActivity.this, getResources().getString(R.string.progress));*/
            }
        });

        mMyPraysLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeDrawer();
                if (Constant.buttonEnable) {
                    Constant.setButtonEnable();
                    FragmentTransaction fTransaction = fragmentManager.beginTransaction();
                    fragmentManager.popBackStack("MyPrayerForOtherFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    fTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    fTransaction.add(R.id.frame, MyPrayerForOtherFragment.newInstance()).addToBackStack("MyPrayerForOtherFragment").commit();
                }

            }
        });

        mCreateGroupLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constant.buttonEnable) {
                    Constant.setButtonEnable();
                    startActivity(new Intent(HomeActivity.this, CreateGroupActivity.class));
                }
                closeDrawer();
            }
        });

        mEditProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constant.buttonEnable) {
                    startActivity(new Intent(HomeActivity.this, EditProfileActivity.class));
                }
                closeDrawer();
            }
        });

        mHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constant.buttonEnable) {
                    Constant.setButtonEnable();
                    Intent intent = getIntent();
                    startActivity(intent);
                    finish();
                    church_default = 1;
                    TimeLineFragment.itemClickTag = 0;
                }
            }
        });

        mBottomLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogout();
            }
        });

        mDonationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constant.buttonEnable) {
                    Constant.setButtonEnable();
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.donation_url)));
                    startActivity(browserIntent);
                }
            }
        });

        mChurchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constant.buttonEnable) {
                    Constant.setButtonEnable();
                    //     removeBackStack();

                    /*FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame, ChurchListFragment.newInstance());
                    fragmentTransaction.commit();
*/
                    addOrReplaceFragment();
                }
            }
        });

        mWorkplcaeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constant.buttonEnable) {
                    Constant.setButtonEnable();
                    FragmentTransaction fTransaction = fragmentManager.beginTransaction();
                    fragmentManager.popBackStack("WorkplaceListFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    fTransaction.add(R.id.frame, new WorkplaceListFragment().newInstance()).addToBackStack("WorkplaceListFragment").commit();
                }
            }
        });

        mPeopleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constant.buttonEnable) {
                    Constant.setButtonEnable();
                    FragmentTransaction fTransaction = fragmentManager.beginTransaction();
                    fragmentManager.popBackStack("MyPrayerListFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    fTransaction.add(R.id.frame, new MyPeopleListFragment().newInstance()).addToBackStack("MyPrayerListFragment").commit();

                }
            }
        });

        mMinistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constant.buttonEnable) {
                    Constant.setButtonEnable();
                    FragmentTransaction fTransaction = fragmentManager.beginTransaction();
                    fragmentManager.popBackStack("MinistryListFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    fTransaction.add(R.id.frame, new MinistryListFragment().newInstance()).addToBackStack("MinistryListFragment").commit();
                }
            }
        });
        if (Constant._type.equals("1")) {
            mEditProfileBtn.setVisibility(View.GONE);
        } else {
            mEditProfileBtn.setVisibility(View.VISIBLE);
        }
    }

    private void closeDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    private void attemptLogout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setMessage("Are you sure you want to logout?");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int ii) {
                dialogInterface.dismiss();
                closeDrawer();
                Intent i = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(i);
                PreferenceConnector.writeString(HomeActivity.this, PreferenceConnector.IS_LOGIN, "No");
                finish();
            }
        });
        builder.create().show();
    }

    private void init() {
        ButterKnife.bind(HomeActivity.this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(HomeActivity.this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // setupUI(frameLayout);
        fragmentManager = getSupportFragmentManager();

        if (Constant._type.equals("1") && church_default == 0) {
            FragmentTransaction fTransaction = fragmentManager.beginTransaction();
            fragmentManager.popBackStack("ChurchProfileFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fTransaction.add(R.id.frame, new ChurchProfileFragment().newInstance(Constant._id, "1", Constant._name, Constant._church_cover, Constant._profile_pic)).addToBackStack("ChurchProfileFragment").commit();
        } else if (!Constant._church_name.equals("") && church_default == 0) {
            FragmentTransaction fTransaction = fragmentManager.beginTransaction();
            fragmentManager.popBackStack("ChurchProfileFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fTransaction.add(R.id.frame, new ChurchProfileFragment().newInstance("", "1", "", "", "")).addToBackStack("ChurchProfileFragment").commit();
        } else {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame, TimeLineFragment.newInstance());
            fragmentTransaction.commit();
        }

        aQuery = new AQuery(HomeActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mUserNameEdt.setText(Constant._name);
        aQuery.id(mProfileImage).image(Constant._profile_pic, false, false, 100, R.drawable.default_pic);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void addOrReplaceFragment() {
        FragmentTransaction fTransaction = fragmentManager.beginTransaction();
        fragmentManager.popBackStack("ChurchListFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        fTransaction.add(R.id.frame, ChurchListFragment.newInstance()).addToBackStack("ChurchListFragment").commit();
    }
}