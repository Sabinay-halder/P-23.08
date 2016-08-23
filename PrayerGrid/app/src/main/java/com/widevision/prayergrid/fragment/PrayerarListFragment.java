package com.widevision.prayergrid.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.widevision.prayergrid.R;
import com.widevision.prayergrid.activity.HomeActivity;
import com.widevision.prayergrid.dao.GetAllPrayerDao;
import com.widevision.prayergrid.dao.GsonClass;
import com.widevision.prayergrid.util.AsyncCallback;
import com.widevision.prayergrid.util.Constant;
import com.widevision.prayergrid.util.Extension;
import com.widevision.prayergrid.util.Implementation;
import com.widevision.prayergrid.util.ProgressLoaderHelper;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PrayerarListFragment extends Fragment {

    @Bind(R.id.list)
    ListView mList;
    @Bind(R.id.action_cancel)
    ImageView mCancel;
    private Extension extension;
    private ProgressLoaderHelper progressLoaderHelper;
    private String prayer_id;


    public static PrayerarListFragment newInstance(String p_id) {
        PrayerarListFragment fragment = new PrayerarListFragment();
        Bundle b = new Bundle();
        b.putString("prayer_id", p_id);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();
        if (b != null) {
            prayer_id = b.getString("prayer_id");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_prayer_list, container, false);
        ButterKnife.bind(this, view);
        init();

        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeActivity.fragmentManager.popBackStack();
            }
        });

        getAllPrayer();
    }


    void init() {
        extension = new Extension();
        progressLoaderHelper = ProgressLoaderHelper.getInstance();
    }

    void getAllPrayer() {
        if (extension.executeStrategy(getActivity(), "", Implementation.INTERNET)) {
            progressLoaderHelper.showProgress(getActivity());
            GetAllPrayerDao prayerDao = new GetAllPrayerDao(Constant._id, prayer_id);
            prayerDao.query(new AsyncCallback<GsonClass>() {
                @Override
                public void onOperationCompleted(GsonClass result, Exception e) {
                    progressLoaderHelper.dismissProgress();
                    if (e == null && result != null) {
                        if (result.success.equals("1")) {
                            if (result.post_info != null && result.post_info.size() != 0) {
                                CustomListAdapter listAdapter = new CustomListAdapter(getActivity(), result.post_info);
                                mList.setAdapter(listAdapter);
                            } else {
                                Constant.setAlert(getActivity(), "No prayer to show");
                            }
                        } else {
                            Constant.setAlert(getActivity(), "No prayer to show");
                        }
                    } else {
                        Constant.setAlert(getActivity(), getActivity().getResources().getString(R.string.wrong));
                    }
                }
            });
        } else {
            Constant.setAlert(getActivity(), getActivity().getString(R.string.no_internet));
        }
    }

    class CustomListAdapter extends BaseAdapter {

        private LayoutInflater layoutInflater;
        private ViewHolder viewHolder;
        private ArrayList<GsonClass.Post_info> list;
        private AQuery aQuery;

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

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            GsonClass.Post_info item = list.get(i);
            if (view == null) {
                view = layoutInflater.inflate(R.layout.prayer_list_item, viewGroup, false);
                viewHolder = new ViewHolder();
                viewHolder.mProfilePic = (ImageView) view.findViewById(R.id.profile_image);
                viewHolder.mUserNameTxt = (TextView) view.findViewById(R.id.user_name);
                viewHolder.name_txt = (TextView) view.findViewById(R.id.name_txt);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            viewHolder.mUserNameTxt.setText(item.name);
            if (item.profilepic.equals("http://prayergrid.org/uploads/profilepic/default.png")) {
                viewHolder.mProfilePic.setVisibility(View.GONE);
                viewHolder.name_txt.setText(item.name.substring(0, 1).toUpperCase());
                viewHolder.name_txt.setVisibility(View.VISIBLE);
            } else {
                viewHolder.name_txt.setVisibility(View.GONE);
                viewHolder.mProfilePic.setVisibility(View.VISIBLE);
                aQuery.id(viewHolder.mProfilePic).image(item.profilepic, true, true, 100, R.drawable.default_pic);
            }
            //  aQuery.id(viewHolder.mProfilePic).image(item.profilepic, true, true, 100, R.drawable.default_pic);

            return view;
        }

        class ViewHolder {
            private ImageView mProfilePic;
            private TextView mUserNameTxt, name_txt;
        }
    }
}
