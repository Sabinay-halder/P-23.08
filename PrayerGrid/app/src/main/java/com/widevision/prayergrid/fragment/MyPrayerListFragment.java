package com.widevision.prayergrid.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import android.widget.Toast;

import com.androidquery.AQuery;
import com.widevision.prayergrid.R;
import com.widevision.prayergrid.activity.HomeActivity;
import com.widevision.prayergrid.dao.DeletePrayerDao;
import com.widevision.prayergrid.dao.GetMyPrayerListDao;
import com.widevision.prayergrid.dao.GsonClass;
import com.widevision.prayergrid.dao.SetPrayerDao;
import com.widevision.prayergrid.util.AsyncCallback;
import com.widevision.prayergrid.util.Constant;
import com.widevision.prayergrid.util.Extension;
import com.widevision.prayergrid.util.Implementation;
import com.widevision.prayergrid.util.ProgressLoaderHelper;
import com.widevision.prayergrid.util.ValidationTemplate;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MyPrayerListFragment extends Fragment {

    @Bind(R.id.list)
    ListView mList;
    @Bind(R.id.swipe_to_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    private int enoughTag = 0, pageCount = 0;
    private Extension extension;
    private ProgressLoaderHelper progressLoaderHelper;
    private final ArrayList<GsonClass.Post_info> dataList = new ArrayList<>();
    private CustomListAdapter adapterTimeLine;

    public static MyPrayerListFragment newInstance() {
        return new MyPrayerListFragment();
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
        pageCount = 0;
        getAllPrayer();

        swipeRefreshLayout.setColorSchemeColors(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageCount = 0;
                getAllPrayer();
            }
        });

    }

    private void init() {
        extension = new Extension();
        progressLoaderHelper = ProgressLoaderHelper.getInstance();
    }

    private void getAllPrayer() {
        if (extension.executeStrategy(getActivity(), "", Implementation.INTERNET)) {
            progressLoaderHelper.showProgress(getActivity());
            GetMyPrayerListDao prayerListDao = new GetMyPrayerListDao(Constant._id, "" + pageCount);
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
                                    adapterTimeLine = new CustomListAdapter(getActivity(), dataList);
                                    mList.setAdapter(adapterTimeLine);
                                    mList.setVisibility(View.VISIBLE);
                                }
                            } else {
                                enoughTag = 1;
                                if (pageCount == 0) {
                                    Constant.setAlert(getActivity(), "You haven't post any prayer.");
                                }
                            }
                        } else {
                            enoughTag = 1;
                            if (pageCount == 0) {
                                Constant.setAlert(getActivity(), "You haven't post any prayer.");
                            }
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    } else {
                        enoughTag = 1;
                        swipeRefreshLayout.setRefreshing(false);
                        Constant.setAlert(getActivity(), getActivity().getResources().getString(R.string.wrong));
                    }
                }
            });
        } else {
            swipeRefreshLayout.setRefreshing(false);
            Constant.setAlert(getActivity(), getActivity().getString(R.string.no_internet));
        }
    }


    class CustomListAdapter extends BaseAdapter {

        private final LayoutInflater layoutInflater;
        private ViewHolder viewHolder;
        private final ArrayList<GsonClass.Post_info> list;
        private final AQuery aQuery;

        public CustomListAdapter(Context c, ArrayList<GsonClass.Post_info> list) {
            layoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.list = list;
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
            notifyDataSetChanged();
        }

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
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            if (i == getCount() - 1) {
                if (enoughTag == 0) {
                    pageCount = pageCount + 1;
                    getAllPrayer();
                }
            }

            if (item.prayer.equals("Yes")) {
                viewHolder.prayTxt.setText(getString(R.string.prayed));
                viewHolder.prayImg.setImageResource(R.drawable.pray_icon);
            } else {
                viewHolder.prayTxt.setText(getString(R.string.pray));
                viewHolder.prayImg.setImageResource(R.drawable.pray);
            }
            viewHolder.postTxt.setText(item.message);
            viewHolder.countTxt.setText("(" + item.count + ")");
            viewHolder.categoryTxt.setText(item.category);

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

            //aQuery.id(viewHolder.profilePic).image(item.profilepic, true, true, 100, R.drawable.default_pic);
            viewHolder.userName.setText(item.name);


            viewHolder.messagesTxt.setVisibility(View.GONE);

            viewHolder.commentsTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Constant.buttonEnable) {
                        Constant.setButtonEnable();

                        FragmentTransaction fTransaction = HomeActivity.fragmentManager.beginTransaction();
                        HomeActivity.fragmentManager.popBackStack("ViewCommentsFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        fTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                        fTransaction.add(R.id.frame, ViewCommentsFragment.newInstance(item.pid)).addToBackStack("ViewCommentsFragment").commit();
                    }
                }
            });

            viewHolder.prayersTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Constant.buttonEnable) {
                        Constant.setButtonEnable();
                        if (!item.count.equals("0")) {
                            FragmentTransaction fTransaction = HomeActivity.fragmentManager.beginTransaction();
                            HomeActivity.fragmentManager.popBackStack("PrayerarListFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            fTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                            fTransaction.add(R.id.frame, PrayerarListFragment.newInstance(item.pid)).addToBackStack("PrayerarListFragment").commit();
                        }
                    }
                }
            });

            viewHolder.messagesTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Constant.buttonEnable) {
                        Constant.setButtonEnable();
                        FragmentTransaction fTransaction = HomeActivity.fragmentManager.beginTransaction();
                        HomeActivity.fragmentManager.popBackStack("ViewMessageFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        fTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                        fTransaction.add(R.id.frame, ViewMessageFragment.newInstance()).addToBackStack("ViewMessageFragment").commit();
                    }
                }
            });

            viewHolder.prayTxtLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Constant.buttonEnable) {
                        Constant.setButtonEnable();
                        if (item.prayer.equals("No")) {
                            setPrayers(item.pid);
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
                        if (extension.executeStrategy(getActivity(), "", ValidationTemplate.INTERNET)) {
                            DeletePrayerDao deletePrayerDao = new DeletePrayerDao(Constant._id, item.pid);
                            deletePrayerDao.query(new AsyncCallback<GsonClass>() {
                                @Override
                                public void onOperationCompleted(GsonClass result, Exception e) {
                                    if (e == null && result != null) {
                                        if (result.success.equals("1")) {
                                            adapterTimeLine.removeItem(item);
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
            ImageView profilePic, prayImg, arrowImg;
            TextView userName, categoryTxt, postTxt, countTxt, prayTxt,name_txt;
            LinearLayout messagesTxt, commentsTxt, prayersTxt, prayTxtLayout;
        }
    }

    private void setPrayers(String prayer_id) {
        SetPrayerDao prayerDao = new SetPrayerDao(Constant._id, prayer_id, Constant._type);
        prayerDao.query(new AsyncCallback<GsonClass>() {
            @Override
            public void onOperationCompleted(GsonClass result, Exception e) {
                if (e == null && result != null) {
                    if (!result.success.equals("1")) {
                        Constant.setAlert(getActivity(), result.message);
                    }
                } else {
                    Constant.setAlert(getActivity(), getActivity().getResources().getString(R.string.wrong));
                }
            }
        });
    }
}