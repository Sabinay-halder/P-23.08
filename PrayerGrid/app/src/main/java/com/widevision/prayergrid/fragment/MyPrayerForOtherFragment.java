package com.widevision.prayergrid.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.widevision.prayergrid.R;
import com.widevision.prayergrid.activity.HomeActivity;
import com.widevision.prayergrid.dao.GetMyPrayerOthersListDao;
import com.widevision.prayergrid.dao.GsonClass;
import com.widevision.prayergrid.util.AsyncCallback;
import com.widevision.prayergrid.util.Constant;
import com.widevision.prayergrid.util.Extension;
import com.widevision.prayergrid.util.Implementation;
import com.widevision.prayergrid.util.ProgressLoaderHelper;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MyPrayerForOtherFragment extends Fragment {

    @Bind(R.id.list)
    ListView mList;
    @Bind(R.id.title)
    TextView mTitle;
    @Bind(R.id.swipe_to_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    private int enoughTag = 0, pageCount = 0;
    private Extension extension;
    private ProgressLoaderHelper progressLoaderHelper;
    private final ArrayList<GsonClass.Post_info> dataList = new ArrayList<>();
    private CustomAdapterTimeLine adapterTimeLine;


    public MyPrayerForOtherFragment() {
        // Required empty public constructor
    }

    public static MyPrayerForOtherFragment newInstance() {
        return new MyPrayerForOtherFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_myprayer_list, container, false);
        ButterKnife.bind(this, view);
        init();

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeRefreshLayout.setColorSchemeColors(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });

        mTitle.setText(getString(R.string.my_prayer));
        pageCount = 0;
        getTimeLine();

    }

    private void init() {
        extension = new Extension();
        progressLoaderHelper = ProgressLoaderHelper.getInstance();
    }

    private void getTimeLine() {
        if (extension.executeStrategy(getActivity(), "", Implementation.INTERNET)) {
            progressLoaderHelper.showProgress(getActivity());
            GetMyPrayerOthersListDao prayerListDao = new GetMyPrayerOthersListDao(Constant._id, "" + pageCount);
            prayerListDao.query(new AsyncCallback<GsonClass>() {
                @Override
                public void onOperationCompleted(GsonClass result, Exception e) {
                    progressLoaderHelper.dismissProgress();
                    if (e == null && result != null) {
                        if (result.success.equals("1")) {
                            if (result.post_info != null && result.post_info.size() != 0) {
                                enoughTag = 0;
                                if (pageCount == 0) {
                                    dataList.clear();
                                }
                                dataList.addAll(result.post_info);
                                if (adapterTimeLine != null) {
                                    adapterTimeLine.notifyDataSetChanged();
                                } else {
                                    adapterTimeLine = new CustomAdapterTimeLine(getActivity(), dataList);
                                    mList.setAdapter(adapterTimeLine);
                                }
                                mList.setVisibility(View.VISIBLE);
                            } else {
                                enoughTag = 1;
                                if (pageCount == 0) {
                                    mList.setVisibility(View.GONE);
                                    Constant.setAlert(getActivity(), "No prayer to show.");
                                }
                            }
                            swipeRefreshLayout.setRefreshing(false);
                        } else {
                            if (pageCount == 0) {
                                Constant.setAlert(getActivity(), "No prayer to show.");
                            }
                            enoughTag = 1;
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    } else {
                        enoughTag = 1;
                        swipeRefreshLayout.setRefreshing(false);
                        Constant.setAlert(getActivity(), getActivity().getResources().getString(R.string.wrong));
                    }
                }
            });
        } else {
            swipeRefreshLayout.setRefreshing(false);
            Constant.setAlert(getActivity(), getActivity().getResources().getString(R.string.no_internet));
        }
    }


    class CustomAdapterTimeLine extends BaseAdapter {

        private final LayoutInflater layoutInflater;
        private ViewHolder viewHolder;
        private final ArrayList<GsonClass.Post_info> list;
        private final AQuery aQuery;

        public CustomAdapterTimeLine(Context context, ArrayList<GsonClass.Post_info> list) {
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.list = list;
            aQuery = new AQuery(context);
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

        @SuppressLint("SetTextI18n")
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final GsonClass.Post_info item = (GsonClass.Post_info) getItem(i);
            if (view == null) {
                view = layoutInflater.inflate(R.layout.timeline_items, viewGroup, false);
                viewHolder = new ViewHolder();
                viewHolder.commentsTxt = (LinearLayout) view.findViewById(R.id.comments);
                viewHolder.messagesTxt = (LinearLayout) view.findViewById(R.id.messages);
                viewHolder.prayersTxt = (LinearLayout) view.findViewById(R.id.prayers);
                viewHolder.prayTxtLayout = (LinearLayout) view.findViewById(R.id.pray);
                viewHolder.userName = (TextView) view.findViewById(R.id.username);
                viewHolder.postTxt = (TextView) view.findViewById(R.id.post);
                viewHolder.countTxt = (TextView) view.findViewById(R.id.count);
                viewHolder.prayTxt = (TextView) view.findViewById(R.id.pray_txt);
                viewHolder.categoryTxt = (TextView) view.findViewById(R.id.category);
                viewHolder.name_txt = (TextView) view.findViewById(R.id.name_txt);
                viewHolder.profilePic = (ImageView) view.findViewById(R.id.profile);
                viewHolder.prayImg = (ImageView) view.findViewById(R.id.pray_image);
                viewHolder.arrowImg = (ImageView) view.findViewById(R.id.arrow);
                viewHolder.messagesTxt.setVisibility(View.GONE);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            if (i == getCount() - 1) {
                if (enoughTag == 0) {
                    pageCount = pageCount + 1;
                    getTimeLine();
                }
            }
            viewHolder.postTxt.setText(item.message);
            viewHolder.countTxt.setText("(" + item.count + ")");
            viewHolder.categoryTxt.setText(item.category);


            if (item.type.equals("2")) {
                viewHolder.userName.setText(getString(R.string.anonymous));
               // aQuery.id(viewHolder.profilePic).image(R.drawable.cdefault);
                viewHolder.profilePic.setVisibility(View.GONE);
                viewHolder.name_txt.setText(item.name.substring(0, 1).toUpperCase());
                viewHolder.name_txt.setVisibility(View.VISIBLE);
            } else {

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

                if (item.name != null) {
                    viewHolder.userName.setText(item.name);
                }
            }

            if (item.userid != null && item.userid.equals(Constant._id)) {
                viewHolder.messagesTxt.setVisibility(View.INVISIBLE);
            } else {
                viewHolder.messagesTxt.setVisibility(View.VISIBLE);
            }

            viewHolder.prayTxt.setText(getString(R.string.prayed));
            viewHolder.prayImg.setImageResource(R.drawable.pray_icon);


            viewHolder.commentsTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Constant.buttonEnable) {
                        Constant.setButtonEnable();
                        FragmentTransaction fragmentTransaction = HomeActivity.fragmentManager.beginTransaction();
                        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                        fragmentTransaction.add(R.id.frame, ViewCommentsFragment.newInstance(item.pid));
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
                            fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                            fragmentTransaction.add(R.id.frame, PrayerarListFragment.newInstance(item.pid));
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                        }
                    }
                }
            });
            viewHolder.arrowImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Constant.buttonEnable) {
                        Constant.setButtonEnable();
                        showPopup(view, item.userid);
                    }
                }
            });

            return view;
        }

        void showPopup(View view, String id) {
            LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams") View v = layoutInflater.inflate(R.layout.popup_arrow_layout, null);
            final PopupWindow popupWindow = new PopupWindow(v, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            TextView report = (TextView) v.findViewById(R.id.report);
            TextView delete = (TextView) v.findViewById(R.id.delete);
            if (!id.equals(Constant._id)) {
                delete.setVisibility(View.GONE);
            }
            report.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Constant.buttonEnable) {
                        Constant.setButtonEnable();
                        popupWindow.dismiss();
                        Constant.setAlert(getActivity(), getActivity().getResources().getString(R.string.report_msg));
                    }
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Constant.buttonEnable) {
                        Constant.setButtonEnable();
                        popupWindow.dismiss();
                        Constant.setAlert(getActivity(), getActivity().getResources().getString(R.string.report_msg));
                    }
                }
            });

            popupWindow.showAsDropDown(view, 0, -15);
        }

        class ViewHolder {
            ImageView profilePic, prayImg, arrowImg;
            TextView userName, categoryTxt, postTxt, countTxt, prayTxt,name_txt;
            LinearLayout messagesTxt, commentsTxt, prayersTxt, prayTxtLayout;
        }
    }
}