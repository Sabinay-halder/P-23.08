package com.widevision.prayergrid.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.widevision.prayergrid.R;
import com.widevision.prayergrid.dao.GetMessagesDao;
import com.widevision.prayergrid.dao.GsonClass;
import com.widevision.prayergrid.dao.SendDirectMsgDao;
import com.widevision.prayergrid.dao.ViewMessageGsonClass;
import com.widevision.prayergrid.model.HideKeyActivity;
import com.widevision.prayergrid.util.AsyncCallback;
import com.widevision.prayergrid.util.Constant;
import com.widevision.prayergrid.util.Extension;
import com.widevision.prayergrid.util.PreferenceConnector;
import com.widevision.prayergrid.util.ProgressLoaderHelper;
import com.widevision.prayergrid.util.ValidationTemplate;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ViewSigleMessagesList extends HideKeyActivity {
    private Extension extension;
    private ProgressLoaderHelper progressLoaderHelper;
    private ArrayList<ViewMessageGsonClass.Post_info> list = new ArrayList<>();
    private String receiverId = "", name = "";
    private CustomListAdapter adapter;

    @Bind(R.id.username)
    TextView mReceiverNameTxt;

    @Bind(R.id.msg_list)
    ListView mList;

    @Bind(R.id.msg_edt)
    EditText mMsgEdt;

    @Bind(R.id.btn_send)
    TextView mSendBtn;

    @Bind(R.id.main_container)
    RelativeLayout mMainContainer;

    private MessageReceiver receiver;
    public static final String tag = "com.widevision.prayergrid.meaasge";
    private String profile_rec = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.pull_out_to_left_anim, R.anim.pull_infromright_anim);
        setContentView(R.layout.activity_view_sigle_messages_list);
        Bundle b = getIntent().getExtras();
        if (b != null) {
            receiverId = b.getString("receiver_id");
            name = b.getString("name");
        }
        init();

        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendSms();
            }
        });

        getMessagesList();

        mMsgEdt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendSms();
                    handled = true;
                }
                return handled;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        PreferenceConnector.writeString(ViewSigleMessagesList.this, PreferenceConnector.MESSAGE_STATE, "1");
        PreferenceConnector.writeString(ViewSigleMessagesList.this, PreferenceConnector.CHATTING_USER_ID, receiverId);
        receiver = new MessageReceiver();
        registerReceiver(receiver, new IntentFilter(tag));
    }

    @Override
    protected void onPause() {
        super.onPause();
        PreferenceConnector.writeString(ViewSigleMessagesList.this, PreferenceConnector.MESSAGE_STATE, "0");
        unregisterReceiver(receiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceConnector.writeString(ViewSigleMessagesList.this, PreferenceConnector.CHATTING_USER_ID, "");
        PreferenceConnector.writeString(ViewSigleMessagesList.this, PreferenceConnector.MESSAGE_STATE, "0");
    }

    private void sendSms() {
        if (extension.executeStrategy(ViewSigleMessagesList.this, "", ValidationTemplate.INTERNET)) {
            final String msg = mMsgEdt.getText().toString().trim();
            if (!msg.isEmpty()) {
                final JSONArray jsonArray = new JSONArray();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("sid", "" + Constant._id);
                    jsonObject.put("msg", msg);
                    jsonObject.put("stat", "0");
                    jsonObject.put("time", Constant.getMobileDate(Constant.datePattern_date));
                    jsonArray.put(jsonObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ViewMessageGsonClass.Post_info post_info = new ViewMessageGsonClass.Post_info();
                ViewMessageGsonClass.Message message = new ViewMessageGsonClass.Message();
                ArrayList<ViewMessageGsonClass.Message> listMessage = new ArrayList<>();
                message.msg = msg;
                message.sid = Constant._id;
                message.stat = "0";
                message.time = Constant.getMobileDate(Constant.datePattern_date);
                listMessage.add(message);
                post_info.message = listMessage;
                post_info.name = Constant._name;
                post_info.profilepic = "";
                post_info.user_id = "" + Constant._id;
                list.add(post_info);
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                } else {
                    adapter = new CustomListAdapter(ViewSigleMessagesList.this);
                    mList.setAdapter(adapter);
                }
                mMsgEdt.setText("");
                mList.smoothScrollToPosition(list.size() - 1);
                //  Log.e("msg", jsonArray.toString());
                SendDirectMsgDao msgDao = new SendDirectMsgDao(Constant._id, receiverId, jsonArray.toString());
                msgDao.query(new AsyncCallback<GsonClass>() {
                    @Override
                    public void onOperationCompleted(GsonClass result, Exception e) {
                        if (e == null && result != null) {
                            if (result.success.equals("1")) {
                                //           getMessagesList();
                            } else {
                                Toast.makeText(ViewSigleMessagesList.this, result.message, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Constant.setAlert(ViewSigleMessagesList.this, getResources().getString(R.string.wrong));
                        }
                    }
                });
            } else {
                Constant.setAlert(ViewSigleMessagesList.this, "Enter your message first.");
            }
        } else {
            Constant.setAlert(ViewSigleMessagesList.this, getString(R.string.no_internet));
        }
    }

    private void init() {
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
        mReceiverNameTxt.setText(name);
        setupUI(mMainContainer);
    }

    private void getMessagesList() {
        if (extension.executeStrategy(ViewSigleMessagesList.this, "", ValidationTemplate.INTERNET)) {

            GetMessagesDao messagesDao = new GetMessagesDao(Constant._id, receiverId);
            messagesDao.query(new AsyncCallback<ViewMessageGsonClass>() {
                @Override
                public void onOperationCompleted(ViewMessageGsonClass result, Exception e) {

                    if (e == null && result != null) {
                        if (result.success.equals("1")) {
                            profile_rec = result.profile_rec;
                            if (result.post_info != null && result.post_info.size() != 0) {
                                if (list != null) {
                                    list.clear();
                                }
                                Collections.sort(result.post_info);
                                list = result.post_info;
                                if (adapter != null) {
                                    adapter.notifyDataSetChanged();
                                    mList.setSelection(list.size() - 1);
                                } else {
                                    adapter = new CustomListAdapter(ViewSigleMessagesList.this);
                                    mList.setAdapter(adapter);
                                    mList.setSelection(list.size() - 1);
                                }
                            } else {
                                Constant.setAlert(ViewSigleMessagesList.this, result.message);
                            }
                        } else {
                            Constant.setAlert(ViewSigleMessagesList.this, result.message);
                        }
                    } else {
                        Constant.setAlert(ViewSigleMessagesList.this, getString(R.string.wrong));
                    }
                }
            });
        } else {
            Constant.setAlert(ViewSigleMessagesList.this, getString(R.string.no_internet));
        }
    }

    private class CustomListAdapter extends BaseAdapter {

        private LayoutInflater layoutInflater;
        private ViewHolder viewHolder;
        private AQuery aQuery;

        public CustomListAdapter(Context c) {
            layoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        public View getView(int position, View view, ViewGroup viewGroup) {
            if (view == null) {
                viewHolder = new ViewHolder();
                view = layoutInflater.inflate(R.layout.msg_list_row, viewGroup, false);
                viewHolder.mSenderImage = (ImageView) view.findViewById(R.id.sender_image);
                viewHolder.mReceiverImage = (ImageView) view.findViewById(R.id.receiver_image);
                viewHolder.mSenderLayout = (LinearLayout) view.findViewById(R.id.sender_layout);
                viewHolder.mReceiverLayout = (LinearLayout) view.findViewById(R.id.receiver_layout);
                viewHolder.mReceiverMsgTxt = (TextView) view.findViewById(R.id.receiver_msg);
                viewHolder.mSenderMsgTxt = (TextView) view.findViewById(R.id.sender_msg);
                viewHolder.mSenderDate = (TextView) view.findViewById(R.id.sender_date);
                viewHolder.mReceiverDate = (TextView) view.findViewById(R.id.receiver_date);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            ViewMessageGsonClass.Post_info item = list.get(position);
            if (item.message != null && item.message.size() != 0) {
                if (item.message.get(0).sid.equals(Constant._id)) {
                    viewHolder.mSenderLayout.setVisibility(View.VISIBLE);
                    viewHolder.mReceiverLayout.setVisibility(View.GONE);
                    viewHolder.mSenderMsgTxt.setText(item.message.get(0).msg);
                    viewHolder.mSenderDate.setText(item.message.get(0).time);
                    aQuery.id(viewHolder.mSenderImage).image(Constant._profile_pic, true, true, 60, R.drawable.default_pic);
                } else {
                    aQuery.id(viewHolder.mReceiverImage).image(profile_rec, true, true, 60, R.drawable.default_pic);
                    viewHolder.mReceiverLayout.setVisibility(View.VISIBLE);
                    viewHolder.mSenderLayout.setVisibility(View.GONE);
                    viewHolder.mReceiverMsgTxt.setText(item.message.get(0).msg);
                    viewHolder.mReceiverDate.setText(item.message.get(0).time);
                }
            }
            return view;
        }

        class ViewHolder {
            private TextView mSenderMsgTxt, mReceiverMsgTxt, mSenderDate, mReceiverDate;
            private ImageView mSenderImage, mReceiverImage;
            private LinearLayout mSenderLayout, mReceiverLayout;
        }
    }

    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            getMessagesList();
        }
    }
}