package com.widevision.prayergrid.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.google.gson.Gson;
import com.widevision.prayergrid.R;
import com.widevision.prayergrid.activity.HomeActivity;
import com.widevision.prayergrid.activity.MemberListActivity;
import com.widevision.prayergrid.dao.ChurchListDao;
import com.widevision.prayergrid.dao.GsonClass;
import com.widevision.prayergrid.dao.JoinChurchDao;
import com.widevision.prayergrid.util.AsyncCallback;
import com.widevision.prayergrid.util.Constant;
import com.widevision.prayergrid.util.Extension;
import com.widevision.prayergrid.util.Implementation;
import com.widevision.prayergrid.util.ProgressLoaderHelper;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChurchListFragment extends Fragment {

    private Extension extension;
    private ProgressLoaderHelper progressLoaderHelper;
    @Bind(R.id.list)
    ListView mList;
    @Bind(R.id.frame)
    RelativeLayout mMain;

    public ChurchListFragment() {
        // Required empty public constructor
    }

    public static ChurchListFragment newInstance() {
        ChurchListFragment fragment = new ChurchListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_church_list, container, false);
        init(view);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getChurchList();
    }

    private void getChurchList() {
        if (extension.executeStrategy(getActivity(), "", Implementation.INTERNET)) {
            progressLoaderHelper.showProgress(getActivity());
            ChurchListDao churchListDao = new ChurchListDao(Constant._id);
            churchListDao.query(new AsyncCallback<GsonClass>() {
                @Override
                public void onOperationCompleted(GsonClass result, Exception e) {
                    progressLoaderHelper.dismissProgress();
                    if (e == null && result != null) {
                        if (result.success.equals("1")) {
                            if (result.post_info != null && result.post_info.size() != 0) {
                                CustomListAdapter adapter = new CustomListAdapter(getActivity(), result.post_info);
                                mList.setAdapter(adapter);
                            } else {
                                Constant.setAlert(getActivity(), "There is no church to show.");
                            }
                        } else {
                            Constant.setAlert(getActivity(), "There is no church to show.");
                        }
                    } else {
                        Constant.setAlert(getActivity(), getActivity().getResources().getString(R.string.wrong));
                    }
                }
            });
        } else {
            Constant.setAlert(getActivity(), getActivity().getResources().getString(R.string.no_internet));
        }
    }

    void init(View view) {
        ButterKnife.bind(this, view);
        extension = new Extension();
        progressLoaderHelper = ProgressLoaderHelper.getInstance();
    }

    class CustomListAdapter extends BaseAdapter {

        private LayoutInflater layoutInflater;
        ArrayList<GsonClass.Post_info> list;
        private ViewHolder viewHolder;
        private AQuery aQuery;

        public CustomListAdapter(Context c, ArrayList<GsonClass.Post_info> list) {
            layoutInflater = getLayoutInflater(null);
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

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final GsonClass.Post_info item = list.get(i);
            if (view == null) {
                view = layoutInflater.inflate(R.layout.church_list_item, viewGroup, false);
                viewHolder = new ViewHolder();
                viewHolder.mChurchNameTxt = (TextView) view.findViewById(R.id.church_name);
                viewHolder.mLeaderName = (TextView) view.findViewById(R.id.leader_name);
                viewHolder.mProfilePic = (ImageView) view.findViewById(R.id.profile_image);
                viewHolder.mJoinBtn = (TextView) view.findViewById(R.id.joinBtn);
                viewHolder.mMemberBtn = (TextView) view.findViewById(R.id.memberBtn);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }


            viewHolder.mChurchNameTxt.setText(item.name);
            viewHolder.mLeaderName.setText(item.leader);
            aQuery.id(viewHolder.mProfilePic).image(item.tithe_image, true, true, 100, R.drawable.default_pic);

            if (item.join.equals("1")) {
                viewHolder.mJoinBtn.setText("Joined");
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Constant.buttonEnable) {
                        Constant.setButtonEnable();/*
                        FragmentTransaction fragmentTransaction = HomeActivity.fragmentManager.beginTransaction();
                        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                        fragmentTransaction.replace(R.id.frame, ChurchProfileFragment.newInstance(item.id, item.join));
                        fragmentTransaction.commit();*/

                        FragmentTransaction fTransaction = HomeActivity.fragmentManager.beginTransaction();
                        HomeActivity.fragmentManager.popBackStack("ChurchProfileFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        fTransaction.add(R.id.frame, new ChurchProfileFragment().newInstance(item.id, item.join, item.name, item.header_image, item.tithe_image)).addToBackStack("ChurchProfileFragment").commit();

                    }
                }
            });

            viewHolder.mJoinBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!item.join.equals("1")) {
                        if (Constant.buttonEnable) {
                            Constant.setButtonEnable();
                            if (extension.executeStrategy(getActivity(), "", Implementation.INTERNET)) {
                                progressLoaderHelper.showProgress(getActivity());
                                JoinChurchDao joinChurchDao = new JoinChurchDao(Constant._id, item.id);
                                joinChurchDao.query(new AsyncCallback<GsonClass>() {
                                    @Override
                                    public void onOperationCompleted(GsonClass result, Exception e) {
                                        progressLoaderHelper.dismissProgress();
                                        if (e == null && result != null) {
                                            item.join = "1";
                                            notifyDataSetChanged();
                                            Constant.setAlert(getActivity(), "Joined Successfully");
                                        } else {
                                            Constant.setAlert(getActivity(), getActivity().getResources().getString(R.string.wrong));
                                        }
                                    }
                                });
                            } else {
                                Constant.setAlert(getActivity(), getResources().getString(R.string.no_internet));
                            }
                        }
                    }
                }
            });


            viewHolder.mMemberBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Constant.buttonEnable) {
                        Constant.setButtonEnable();
                        startActivity(new Intent(getActivity(), MemberListActivity.class).putExtra("c_id", item.id));
                    }
                }
            });

            return view;
        }

        class ViewHolder {
            private ImageView mProfilePic;
            private TextView mChurchNameTxt, mLeaderName;
            private TextView mJoinBtn, mMemberBtn;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        progressLoaderHelper.dismissProgress();
    }
}