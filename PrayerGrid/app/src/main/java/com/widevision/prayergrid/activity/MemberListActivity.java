package com.widevision.prayergrid.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.widevision.prayergrid.R;
import com.widevision.prayergrid.dao.GetChurchMemberDao;
import com.widevision.prayergrid.dao.GsonClass;
import com.widevision.prayergrid.model.HideKeyActivity;
import com.widevision.prayergrid.util.AsyncCallback;
import com.widevision.prayergrid.util.Constant;
import com.widevision.prayergrid.util.Extension;
import com.widevision.prayergrid.util.Implementation;
import com.widevision.prayergrid.util.ProgressLoaderHelper;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MemberListActivity extends HideKeyActivity {

    @Bind(R.id.member_list)
    GridView mListView;
    private Extension extension;
    private ProgressLoaderHelper progressLoaderHelper;
    private String c_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.pull_out_to_left_anim, R.anim.pull_infromright_anim);
        setContentView(R.layout.activity_member_list);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getMemberList();
    }

    private void getMemberList() {
        if (extension.executeStrategy(MemberListActivity.this, "", Implementation.INTERNET)) {
            progressLoaderHelper.showProgress(MemberListActivity.this);
            GetChurchMemberDao memberDao = new GetChurchMemberDao(Constant._id, c_id);
            memberDao.query(new AsyncCallback<GsonClass>() {
                @Override
                public void onOperationCompleted(GsonClass result, Exception e) {
                    progressLoaderHelper.dismissProgress();
                    if (e == null && result != null) {
                        if (result.success.equals("1")) {
                            if (result.post_info != null && result.post_info.size() != 0) {
                                CustomListAdapter adapter = new CustomListAdapter(MemberListActivity.this, result.post_info);
                                mListView.setAdapter(adapter);
                            } else {
                                finish();
                                Toast.makeText(MemberListActivity.this, "No member in this Church.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            finish();
                            Toast.makeText(MemberListActivity.this, "No member in this Church.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Constant.setAlert(MemberListActivity.this, getResources().getString(R.string.wrong));
                    }
                }
            });
        } else {
            Constant.setAlert(MemberListActivity.this, getString(R.string.no_internet));
        }
    }

    private void init() {
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        extension = new Extension();
        progressLoaderHelper = ProgressLoaderHelper.getInstance();
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Bundle b = getIntent().getExtras();
        if (b != null) {
            c_id = b.getString("c_id");
        }
    }

    class CustomListAdapter extends BaseAdapter {

        private final LayoutInflater layoutInflater;
        private ViewHolder viewHolder;
        private final AbsListView.LayoutParams layoutParams;
        private final ArrayList<GsonClass.Post_info> list;
        private final AQuery aQuery;


        public CustomListAdapter(Context c, ArrayList<GsonClass.Post_info> list) {
            layoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layoutParams = new AbsListView.LayoutParams((Constant.mDeviceWidth / 2) - 20, Constant.mDeviceWidth / 2);
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
                view = layoutInflater.inflate(R.layout.member_item, viewGroup, false);
                viewHolder = new ViewHolder();
                viewHolder.userName = (TextView) view.findViewById(R.id.username);
                viewHolder.profilePic = (ImageView) view.findViewById(R.id.profile);
                viewHolder.main = (RelativeLayout) view.findViewById(R.id.main);
                viewHolder.main.setLayoutParams(layoutParams);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            aQuery.id(viewHolder.profilePic).image(item.profilepic, false, false, 100, R.drawable.default_pic);
            viewHolder.userName.setText(item.name);

            return view;
        }

        class ViewHolder {
            ImageView profilePic;
            TextView userName;
            RelativeLayout main;
        }
    }
}