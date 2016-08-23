package com.widevision.prayergrid.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.google.gson.Gson;
import com.widevision.prayergrid.R;
import com.widevision.prayergrid.activity.HomeActivity;
import com.widevision.prayergrid.activity.MemberListActivity;
import com.widevision.prayergrid.dao.DeleteChurchPrayerDao;
import com.widevision.prayergrid.dao.DeletePrayerDao;
import com.widevision.prayergrid.dao.GetChurchPostDao;
import com.widevision.prayergrid.dao.GetDefaultChurchDao;
import com.widevision.prayergrid.dao.GsonClass;
import com.widevision.prayergrid.dao.JoinChurchDao;
import com.widevision.prayergrid.dao.ReportinappropriateDao;
import com.widevision.prayergrid.dao.SetPrayerDao;
import com.widevision.prayergrid.model.HideKeyFragment;
import com.widevision.prayergrid.util.AsyncCallback;
import com.widevision.prayergrid.util.Constant;
import com.widevision.prayergrid.util.Extension;
import com.widevision.prayergrid.util.Implementation;
import com.widevision.prayergrid.util.ProgressLoaderHelper;
import com.widevision.prayergrid.util.ValidationTemplate;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChurchProfileFragment extends HideKeyFragment implements View.OnClickListener {

    @Bind(R.id.list)
    ListView mListView;
    /* @Bind(R.id.profile_image)
     ImageView mProfilePic;*/
   /* @Bind(R.id.church_name)
    TextView mChurchName;
    @Bind(R.id.join_btn)
    TextView mJoinBtn;
    @Bind(R.id.member_btn)
    TextView mMemberBtn;*/

    @Bind(R.id.main)
    RelativeLayout mMainLayout;
    @Bind(R.id.swipe_to_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    @Bind(R.id.list_answer)
    ListView mListAnswer;

    @Bind(R.id.prayer_request_layout)
    LinearLayout mPrayerRequest;
    @Bind(R.id.answered_prayer_layout)
    LinearLayout mAnswerd;
    private RefreshReceiver refreshReceiver;
    private Extension extension;
    private ProgressLoaderHelper progressLoaderHelper;
    private String c_id = "", join = "", header_image = "", church_name = "", tith_image = "";
    private final ArrayList<GsonClass.Post_info> list = new ArrayList<>();
    private final static int MSG_CONTINUE = 1234;
    private final static long DELAY = 400;
    private int enoughTag = 0, pageCount = 0;
    private CustomListAdapter adapterTimeLine;
    private int type = 0;

    public ChurchProfileFragment() {
        // Required empty public constructor
    }

    public static ChurchProfileFragment newInstance(String c_id, String isJoin, String church_name, String header_image, String tith_image) {
        ChurchProfileFragment fragment = new ChurchProfileFragment();
        Bundle b = new Bundle();
        b.putString("c_id", c_id);
        b.putString("join", isJoin);
        b.putString("name", church_name);
        b.putString("header_image", header_image);
        b.putString("tith_image", tith_image);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();
        if (b != null) {
            c_id = b.getString("c_id");
            join = b.getString("join");
            church_name = b.getString("name");
            header_image = b.getString("header_image");
            tith_image = b.getString("tith_image");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_church_profile, container, false);
        init(view);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPrayerRequest.setOnClickListener(this);
        mAnswerd.setOnClickListener(this);

        refreshReceiver = new RefreshReceiver();
        getActivity().registerReceiver(refreshReceiver, new IntentFilter("timeline_church"));
      /*  mHandler.sendEmptyMessageDelayed(MSG_CONTINUE, DELAY);
        progressLoaderHelper.showProgress(getActivity());*/
        progressLoaderHelper.showProgress(getActivity());
        pageCount = 0;
        enoughTag = 0;
        swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#ff0008"), Color.parseColor("#e43f2b"), Color.parseColor("#FB4731"));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageCount = 0;
                enoughTag = 0;
                getChurchPost();
            }
        });
        mAnswerd.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mPrayerRequest.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        type = 0;

        if (extension.executeStrategy(getActivity(), "", ValidationTemplate.INTERNET)) {
            if (c_id.equals("")) {
                GetDefaultChurchDao churchDao = new GetDefaultChurchDao(Constant._id, Constant._church_name);
                churchDao.query(new AsyncCallback<GsonClass>() {
                    @Override
                    public void onOperationCompleted(GsonClass result, Exception e) {
                        if (e == null && result != null) {
                            if (result.success.equals("1")) {
                                if (result.post_info != null && result.post_info.size() != 0) {
                                    c_id = result.post_info.get(0).id;
                                    church_name = result.post_info.get(0).name;
                                    header_image = result.post_info.get(0).header_image;
                                    tith_image = result.post_info.get(0).tithe_image;
                                    getChurchPost();
                                }
                            }
                        }
                    }
                });
            } else {
                getChurchPost();
            }
        } else {
            Constant.setAlert(getActivity(), getString(R.string.no_internet));
        }

    }

    private final Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_CONTINUE:
                    mHandler.removeMessages(MSG_CONTINUE);
                    break;
            }
        }
    };


    @Override
    public void onClick(View view) {
        if (Constant.buttonEnable) {
            Constant.setButtonEnable();
            switch (view.getId()) {
               /* case R.id.join_btn:
                    //join button click
                    attemptJoinChurch();
                    break;
                case R.id.member_btn:
                    //member button click
                    startActivity(new Intent(getActivity(), MemberListActivity.class));
                    break;*/
                /*case R.id.back:
                    FragmentTransaction fragmentTransaction1 = HomeActivity.fragmentManager.beginTransaction();
                    fragmentTransaction1.replace(R.id.frame, ChurchListFragment.newInstance());//type,church_id----//type 0 for user and 1 for church
                    fragmentTransaction1.commit();
                    break;*/
                case R.id.answered_prayer_layout:
                    //answer button click
                    //mListAnswer.setVisibility(View.VISIBLE);
                    progressLoaderHelper.showProgress(getActivity());
                    mAnswerd.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    mPrayerRequest.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    pageCount = 0;
                    type = 1;
                    getChurchPost();
                    break;
                case R.id.prayer_request_layout:
                    /*//prayer request button click
                    mAnswerd.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    mPrayerRequest.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    FragmentTransaction fragmentTransaction = HomeActivity.fragmentManager.beginTransaction();
                    fragmentTransaction.add(R.id.frame, PrayerpostFragment.newInstance("1", c_id));//type,church_id----//type 0 for user and 1 for church
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    */
                    type = 0;
                    progressLoaderHelper.showProgress(getActivity());
                    mAnswerd.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    mPrayerRequest.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    pageCount = 0;
                    getChurchPost();
                    break;
            }
        }
    }

    void attemptJoinChurch() {
        if (!join.equals("1")) {
            if (extension.executeStrategy(getActivity(), "", Implementation.INTERNET)) {
                JoinChurchDao joinChurchDao = new JoinChurchDao(Constant._id, c_id);
                joinChurchDao.query(new AsyncCallback<GsonClass>() {
                    @Override
                    public void onOperationCompleted(GsonClass result, Exception e) {
                        if (e == null && result != null) {
                            Constant.setAlert(getActivity(), "Joined Successfully");
                        } else {
                            Constant.setAlert(getActivity(), getActivity().getResources().getString(R.string.wrong));
                        }
                    }
                });
            } else {
                Constant.setAlert(getActivity(), getActivity().getResources().getString(R.string.no_internet));
            }
        }
    }

    private void getChurchPost() {
        if (extension.executeStrategy(getActivity(), "", Implementation.INTERNET)) {
            GetChurchPostDao postDao = new GetChurchPostDao(Constant._id, c_id, "" + pageCount, "" + type);
            postDao.query(new AsyncCallback<GsonClass>() {
                @Override
                public void onOperationCompleted(GsonClass result, Exception e) {
                    progressLoaderHelper.dismissProgress();
                    swipeRefreshLayout.setRefreshing(false);
                    if (e == null && result != null) {
                        if (result.success.equals("1")) {
                            if (result.post_info != null && result.post_info.size() != 0) {
                                enoughTag = 0;
                                if (pageCount == 0) {
                                    list.clear();
                                    list.addAll(result.post_info);
                                    list.add(0, null);
                                    adapterTimeLine = new CustomListAdapter(getActivity());
                                    mListView.setAdapter(adapterTimeLine);
                                } else {
                                    list.addAll(result.post_info);
                                    adapterTimeLine.notifyDataSetChanged();
                                }
                            } else {
                                if (pageCount == 0) {
                                    list.clear();
                                    list.add(0, null);
                                    adapterTimeLine = new CustomListAdapter(getActivity());
                                    mListView.setAdapter(adapterTimeLine);
                                    Constant.setAlert(getActivity(), "No post to show.");
                                }
                                enoughTag = 1;
                            }
                        } else {
                            if (pageCount == 0) {
                                list.clear();
                                list.add(0, null);
                                adapterTimeLine = new CustomListAdapter(getActivity());
                                mListView.setAdapter(adapterTimeLine);
                                Constant.setAlert(getActivity(), "No post to show.");
                            }
                            enoughTag = 1;
                        }
                    } else {
                        enoughTag = 1;
                        progressLoaderHelper.dismissProgress();
                        Constant.setAlert(getActivity(), getActivity().getResources().getString(R.string.wrong));
                    }
                }
            });
        } else {
            progressLoaderHelper.dismissProgress();
            Constant.setAlert(getActivity(), getActivity().getResources().getString(R.string.no_internet));
        }
    }


    class heightTask extends AsyncTask<Void, Void, ViewGroup.LayoutParams> {
        @Override
        protected ViewGroup.LayoutParams doInBackground(Void... voids) {
            return setListViewHeightBasedOnChildren(mListView);
        }

        @Override
        protected void onPostExecute(ViewGroup.LayoutParams layoutParams) {
            super.onPostExecute(layoutParams);
            progressLoaderHelper.dismissProgress();
            //   mListView.setLayoutParams(layoutParams);
        }
    }

    /****
     * For giving list fixed height
     * ListView when placed inside a ScrollView
     ****/
    public ViewGroup.LayoutParams setListViewHeightBasedOnChildren(ListView listView) {
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
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));

        return params;
    }

    void init(View view) {
        ButterKnife.bind(this, view);
        setupUI(view);
        extension = new Extension();
        progressLoaderHelper = ProgressLoaderHelper.getInstance();
    }

    class CustomListAdapter extends BaseAdapter {

        private LayoutInflater layoutInflater;
        private ViewHolder viewHolder;
        private AQuery aQuery;

        public CustomListAdapter(Context c) {
            layoutInflater = getLayoutInflater(null);
            aQuery = new AQuery(c);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        public void removeItem(GsonClass.Post_info item) {
            list.remove(item);
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            if (view == null) {
                view = layoutInflater.inflate(R.layout.timeline_items, viewGroup, false);
                viewHolder = new ViewHolder();
                viewHolder.commentsTxt = (LinearLayout) view.findViewById(R.id.comments);
                viewHolder.messagesTxt = (LinearLayout) view.findViewById(R.id.messages);
                viewHolder.prayersTxt = (LinearLayout) view.findViewById(R.id.prayers);
                viewHolder.prayTxtLayout = (LinearLayout) view.findViewById(R.id.pray);
                viewHolder.bottom = (LinearLayout) view.findViewById(R.id.bottom);
                viewHolder.userName = (TextView) view.findViewById(R.id.username);
                viewHolder.postTxt = (TextView) view.findViewById(R.id.post);
                viewHolder.countTxt = (TextView) view.findViewById(R.id.count);
                viewHolder.prayTxt = (TextView) view.findViewById(R.id.pray_txt);
                viewHolder.categoryTxt = (TextView) view.findViewById(R.id.category);
                viewHolder.profilePic = (ImageView) view.findViewById(R.id.profile);
                viewHolder.background_image = (ImageView) view.findViewById(R.id.background_image);
                viewHolder.prayImg = (ImageView) view.findViewById(R.id.pray_image);
                viewHolder.arrowImg = (ImageView) view.findViewById(R.id.arrow);
                viewHolder.profilePic = (ImageView) view.findViewById(R.id.profile_image);
                viewHolder.mJoinBtn = (TextView) view.findViewById(R.id.join_btn);
                viewHolder.mMemberBtn = (TextView) view.findViewById(R.id.member_btn);
                viewHolder.mPrayButton = (TextView) view.findViewById(R.id.pray_btn);
                viewHolder.mChurchName = (TextView) view.findViewById(R.id.church_name);
                viewHolder.name_txt = (TextView) view.findViewById(R.id.name_txt);
                viewHolder.main = (RelativeLayout) view.findViewById(R.id.mainm);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            if (i == getCount() - 1) {
                if (enoughTag == 0) {
                    pageCount = pageCount + 1;
                    getChurchPost();
                }
            }

            if (i == 0) {
                if (join.equals("1")) {
                    viewHolder.mJoinBtn.setText("Joined");
                }

                viewHolder.mChurchName.setText(church_name);
                aQuery.id(viewHolder.profilePic).image(tith_image, false, false, 0, R.drawable.default_pic);
                aQuery.id(viewHolder.background_image).image(header_image, false, false, 0, R.drawable.default_pic);

                viewHolder.mMemberBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (Constant.buttonEnable) {
                            Constant.setButtonEnable();
                            startActivity(new Intent(getActivity(), MemberListActivity.class).putExtra("c_id", c_id));
                        }
                    }
                });
                viewHolder.mJoinBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (Constant.buttonEnable) {
                            Constant.setButtonEnable();
                            attemptJoinChurch();
                        }
                    }
                });

                viewHolder.mPrayButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (Constant.buttonEnable) {
                            Constant.setButtonEnable();
                            FragmentTransaction fragmentTransaction = HomeActivity.fragmentManager.beginTransaction();
                            fragmentTransaction.add(R.id.frame, ChurchPrayerpostFragment.newInstance("" + type, c_id));//type,church_id----//type 0 for user and 1 for church
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                        }
                    }
                });


                viewHolder.main.setVisibility(View.VISIBLE);
                viewHolder.bottom.setVisibility(View.GONE);
            } else {
                viewHolder.main.setVisibility(View.GONE);
                viewHolder.bottom.setVisibility(View.VISIBLE);

                final GsonClass.Post_info item = (GsonClass.Post_info) getItem(i);
                viewHolder.postTxt.setText(item.cpmessage);
                viewHolder.countTxt.setText("(" + item.count + ")");
                viewHolder.categoryTxt.setText(item.category);
                if (item.prayer.equals("Yes")) {
                    viewHolder.prayTxt.setText("Prayed");
                    viewHolder.prayImg.setImageResource(R.drawable.pray_icon);
                } else {
                    viewHolder.prayTxt.setText("Pray");
                    viewHolder.prayImg.setImageResource(R.drawable.pray);
                }


                if (type == 0) {
                    viewHolder.prayTxtLayout.setVisibility(View.VISIBLE);
                    viewHolder.prayersTxt.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.prayTxtLayout.setVisibility(View.GONE);
                    viewHolder.prayersTxt.setVisibility(View.GONE);
                }

                if (item.profilepic.equals("http://prayergrid.org/uploads/profilepic/default.png")) {
                    // viewHolder.profilePic.setImageBitmap(getBitmapFromText(100, 100, item.name.substring(0, 1).toUpperCase()));
                    viewHolder.profilePic.setVisibility(View.GONE);
                    viewHolder.name_txt.setText(item.name.substring(0, 1).toUpperCase());
                    viewHolder.name_txt.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.name_txt.setVisibility(View.GONE);
                    viewHolder.profilePic.setVisibility(View.VISIBLE);
                    aQuery.id(viewHolder.profilePic).image(item.profilepic, true, true, 100, R.drawable.default_pic);
                }
                
                viewHolder.userName.setText(item.name);
                viewHolder.commentsTxt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (Constant.buttonEnable) {
                            Constant.setButtonEnable();
                            FragmentTransaction fragmentTransaction = HomeActivity.fragmentManager.beginTransaction();
                            fragmentTransaction.add(R.id.frame, ViewCommentsFragment.newInstance(item.cpid));
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                        }
                    }
                });

                viewHolder.prayersTxt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (Constant.buttonEnable) {
                            Constant.setButtonEnable();
                            if (!item.count.equals("0")) {
                                FragmentTransaction fragmentTransaction = HomeActivity.fragmentManager.beginTransaction();
                                fragmentTransaction.add(R.id.frame, PrayerarListFragment.newInstance(item.cpid));
                                fragmentTransaction.addToBackStack(null);
                                fragmentTransaction.commit();
                            }
                        }
                    }
                });

                viewHolder.messagesTxt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (Constant.buttonEnable) {
                            Constant.setButtonEnable();
                            PostMessageDialogFragment fragment = PostMessageDialogFragment.newInstance(item.userid);
                            fragment.show(HomeActivity.fragmentManager, "Send Message");
                        }
                    }
                });

                viewHolder.prayTxtLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (Constant.buttonEnable) {
                            Constant.setButtonEnable();
                            if (item.prayer.equals("No")) {
                                setPrayers(item.cpid);
                                int c = Integer.parseInt(item.count) + 1;
                                item.count = "" + c;
                                item.prayer = "Yes";
                                notifyDataSetChanged();
                            }
                        }
                    }
                });

                viewHolder.arrowImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (Constant.buttonEnable) {
                            Constant.setButtonEnable();
                            showPopup(view, item);
                        }
                    }
                });
                if (item.userid.equals(Constant._id)) {
                    viewHolder.messagesTxt.setVisibility(View.INVISIBLE);
                } else {
                    viewHolder.messagesTxt.setVisibility(View.VISIBLE);
                }
            }
            return view;
        }

        void showPopup(View view, final GsonClass.Post_info item) {
            LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams") View v = layoutInflater.inflate(R.layout.popup_arrow_layout, null);
            final PopupWindow popupWindow = new PopupWindow(v, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            TextView report = (TextView) v.findViewById(R.id.report);
            TextView delete = (TextView) v.findViewById(R.id.delete);
            if (!item.userid.equals(Constant._id)) {
                delete.setVisibility(View.GONE);
            }
            report.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Constant.buttonEnable) {
                        Constant.setButtonEnable();
                        popupWindow.dismiss();
                        if (extension.executeStrategy(getActivity(), "", ValidationTemplate.INTERNET)) {
                            ReportinappropriateDao reportinappropriateDao = new ReportinappropriateDao(Constant._id, "1", item.cpid);
                            reportinappropriateDao.query(new AsyncCallback<GsonClass>() {
                                @Override
                                public void onOperationCompleted(GsonClass result, Exception e) {

                                }
                            });
                            Constant.setAlert(getActivity(), getActivity().getResources().getString(R.string.report_msg));
                        } else {
                            Constant.setAlert(getActivity(), getString(R.string.no_internet));
                        }
                    }
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Constant.buttonEnable) {
                        Constant.setButtonEnable();
                        popupWindow.dismiss();

                        if (extension.executeStrategy(getActivity(), "", ValidationTemplate.INTERNET)) {
                            DeleteChurchPrayerDao deletePrayerDao = new DeleteChurchPrayerDao(Constant._id, item.cpid, c_id);
                            deletePrayerDao.query(new AsyncCallback<GsonClass>() {
                                @Override
                                public void onOperationCompleted(GsonClass result, Exception e) {
                                    if (e == null && result != null) {
                                        if (result.success.equals("1")) {
                                            adapterTimeLine.removeItem(item);
                                            notifyDataSetChanged();
                                            Toast.makeText(getActivity(), "Prayer deleted successfully.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Constant.setAlert(getActivity(), result.message);
                                        }
                                    } else {
                                        Constant.setAlert(getActivity(), getString(R.string.wrong));
                                    }
                                }
                            });
                        } else {
                            Constant.setAlert(getActivity(), getString(R.string.no_internet));
                        }

                    }
                }
            });

            popupWindow.showAsDropDown(view, 0, -15);
        }

        class ViewHolder {
            ImageView profilePic, prayImg, arrowImg, background_image;
            TextView userName, categoryTxt, postTxt, countTxt, prayTxt, mChurchName, mJoinBtn, mMemberBtn, mPrayButton, name_txt;
            LinearLayout messagesTxt, commentsTxt, prayersTxt, prayTxtLayout, bottom;
            RelativeLayout main;
        }
    }

    void setPrayers(String prayer_id) {
        SetPrayerDao prayerDao = new SetPrayerDao(Constant._id, prayer_id, Constant._type);
        prayerDao.query(new AsyncCallback<GsonClass>() {
            @Override
            public void onOperationCompleted(GsonClass result, Exception e) {
                if (e == null && result != null) {
                    if (result.success.equals("1")) {

                    } else {
                        Constant.setAlert(getActivity(), result.message);
                    }
                } else {
                    Constant.setAlert(getActivity(), getActivity().getResources().getString(R.string.wrong));
                }
            }
        });
    }

    class RefreshReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //   refresh list
            pageCount = 0;
            getChurchPost();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(refreshReceiver);
        progressLoaderHelper.dismissProgress();
    }
}
