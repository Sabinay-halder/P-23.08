package com.widevision.prayergrid.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.widevision.prayergrid.R;
import com.widevision.prayergrid.dao.ChurchListDao;
import com.widevision.prayergrid.dao.GsonClass;
import com.widevision.prayergrid.dao.JoinChurchDao;
import com.widevision.prayergrid.dao.JoinGroupDao;
import com.widevision.prayergrid.dao.ViewInvitationDao;
import com.widevision.prayergrid.fragment.ChurchProfileFragment;
import com.widevision.prayergrid.fragment.InviteUserDialogFragment;
import com.widevision.prayergrid.model.HideKeyActivity;
import com.widevision.prayergrid.util.AsyncCallback;
import com.widevision.prayergrid.util.Constant;
import com.widevision.prayergrid.util.Extension;
import com.widevision.prayergrid.util.Implementation;
import com.widevision.prayergrid.util.ProgressLoaderHelper;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ViewInvitationActivity extends HideKeyActivity {

    private Extension extension;
    private ProgressLoaderHelper progressLoaderHelper;
    @Bind(R.id.list)
    ListView mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_invitation);
        init();
        getChurchList();
    }

    void init() {
        ButterKnife.bind(this);
        extension = new Extension();
        progressLoaderHelper = ProgressLoaderHelper.getInstance();
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
    }


    private void getChurchList() {
        if (extension.executeStrategy(ViewInvitationActivity.this, "", Implementation.INTERNET)) {
            progressLoaderHelper.showProgress(ViewInvitationActivity.this);
            ViewInvitationDao churchListDao = new ViewInvitationDao(Constant._id);
            churchListDao.query(new AsyncCallback<GsonClass>() {
                @Override
                public void onOperationCompleted(GsonClass result, Exception e) {
                    progressLoaderHelper.dismissProgress();
                    if (e == null && result != null) {
                        if (result.success.equals("1")) {
                            if (result.invitation_info != null && result.invitation_info.size() != 0) {
                                CustomListAdapter adapter = new CustomListAdapter(ViewInvitationActivity.this, result.invitation_info);
                                mList.setAdapter(adapter);
                            } else {
                                Constant.setAlert(ViewInvitationActivity.this, "You have no invitation.");
                            }
                        } else {
                            Constant.setAlert(ViewInvitationActivity.this, "You have no invitation.");
                        }
                    } else {
                        Constant.setAlert(ViewInvitationActivity.this, result.message);
                    }
                }
            });
        } else {
            Constant.setAlert(ViewInvitationActivity.this, getResources().getString(R.string.no_internet));
        }
    }

    class CustomListAdapter extends BaseAdapter {

        private LayoutInflater layoutInflater;
        ArrayList<GsonClass.Invitation_info> list;
        private ViewHolder viewHolder;
        private AQuery aQuery;

        public CustomListAdapter(Context c, ArrayList<GsonClass.Invitation_info> list) {
            layoutInflater = getLayoutInflater();
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
            final GsonClass.Invitation_info item = list.get(i);
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

            viewHolder.mChurchNameTxt.setText(item.group_name);
            viewHolder.mLeaderName.setText(item.admin_name);
            aQuery.id(viewHolder.mProfilePic).image(item.group_profilepic, true, true, 100, R.drawable.default_pic);

           /* if (item.join.equals("1")) {
                viewHolder.mJoinBtn.setText("Joined");
            }*/

            viewHolder.mJoinBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (item.admin_id.equals(Constant._id)) {
                        InviteUserDialogFragment.newInstance(item.group_id).show(HomeActivity.fragmentManager, "InviteUserDialogFragment");
                    } else {
                        if (Constant.buttonEnable) {
                            Constant.setButtonEnable();
                            if (extension.executeStrategy(ViewInvitationActivity.this, "", Implementation.INTERNET)) {
                                progressLoaderHelper.showProgress(ViewInvitationActivity.this);
                                JoinGroupDao joinChurchDao = new JoinGroupDao(Constant._id, item.group_id, "1");
                                joinChurchDao.query(new AsyncCallback<GsonClass>() {
                                    @Override
                                    public void onOperationCompleted(GsonClass result, Exception e) {
                                        progressLoaderHelper.dismissProgress();
                                        if (e == null && result != null) {
                                            getChurchList();
                                            Constant.setAlert(ViewInvitationActivity.this, "Joined Successfully");
                                        } else {
                                            Constant.setAlert(ViewInvitationActivity.this, getResources().getString(R.string.wrong));
                                        }
                                    }
                                });
                            } else {
                                Constant.setAlert(ViewInvitationActivity.this, getResources().getString(R.string.no_internet));
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
                        startActivity(new Intent(ViewInvitationActivity.this, GroupMemberListActivity.class).putExtra("g_id", item.group_id));
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
}