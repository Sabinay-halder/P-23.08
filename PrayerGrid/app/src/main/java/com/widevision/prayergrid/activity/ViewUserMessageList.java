package com.widevision.prayergrid.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.widevision.prayergrid.R;
import com.widevision.prayergrid.dao.FriendSuggestionDao;
import com.widevision.prayergrid.dao.GetMessageUserDao;
import com.widevision.prayergrid.dao.GsonClass;
import com.widevision.prayergrid.dao.SendDirectMsgDao;
import com.widevision.prayergrid.dao.ViewMessageGsonClass;
import com.widevision.prayergrid.util.AsyncCallback;
import com.widevision.prayergrid.util.Constant;
import com.widevision.prayergrid.util.Extension;
import com.widevision.prayergrid.util.Implementation;
import com.widevision.prayergrid.util.ProgressLoaderHelper;
import com.widevision.prayergrid.util.ValidationTemplate;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ViewUserMessageList extends AppCompatActivity {

    private Extension extension;
    private ProgressLoaderHelper progressLoaderHelper;

    @Bind(R.id.list)
    ListView mList;
    @Bind(R.id.new_msg_layout)
    LinearLayout mAddMsg;

    private ArrayList<ViewMessageGsonClass.Post_info> list;
    private CustomListAdapter adapter;
    private ArrayList<String> alist;
    private List<ViewMessageGsonClass.Post_info> resultForSearch, tempSearchList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.pull_out_to_left_anim, R.anim.pull_infromright_anim);
        setContentView(R.layout.activity_view_user_message_list);

        init();

        mAddMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constant.buttonEnable) {
                    Constant.setButtonEnable();
                    addMsgDialog();
                }
            }
        });

        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startActivity(new Intent(ViewUserMessageList.this, ViewSigleMessagesList.class).putExtra("receiver_id", list.get(i).user_id).putExtra("name", list.get(i).name));
            }
        });

        viewMessage();
    }

    private void viewMessage() {
        if (extension.executeStrategy(ViewUserMessageList.this, "", ValidationTemplate.INTERNET)) {
            progressLoaderHelper.showProgress(ViewUserMessageList.this);
            GetMessageUserDao messageUserDao = new GetMessageUserDao(Constant._id);
            messageUserDao.query(new AsyncCallback<ViewMessageGsonClass>() {
                @Override
                public void onOperationCompleted(ViewMessageGsonClass result, Exception e) {
                    progressLoaderHelper.dismissProgress();
                    if (e == null && result != null) {
                        if (result.success.equals("1")) {
                            if (result.post_info != null && result.post_info.size() != 0) {
                                list = result.post_info;
                                adapter = new CustomListAdapter(ViewUserMessageList.this);
                                mList.setAdapter(adapter);
                            } else {
                                Constant.setAlert(ViewUserMessageList.this, "No message to show");
                            }
                        } else {
                            Constant.setAlert(ViewUserMessageList.this, "No message to show");
                        }
                    } else {
                        Constant.setAlert(ViewUserMessageList.this, getString(R.string.wrong));
                    }
                }
            });
        } else {
            Constant.setAlert(ViewUserMessageList.this, getString(R.string.no_internet));
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

    }


    void addMsgDialog() {
        final Dialog dialog = new Dialog(ViewUserMessageList.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.post_message);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageView submit = (ImageView) dialog.findViewById(R.id.action_submit);
        final EditText contentEdt = (EditText) dialog.findViewById(R.id.content_text);
        final AutoCompleteTextView searchEditText = (AutoCompleteTextView) dialog.findViewById(R.id.to_edt);
        ImageView closeBtn = (ImageView) dialog.findViewById(R.id.close_btn);
        //  toEdt.setAdapter(new SuggetionAdapter(ViewUserMessageList.this, toEdt.getText().toString()));

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String to = searchEditText.getText().toString().trim();
                String content = contentEdt.getText().toString().trim();
                if (!to.equals("") && !content.equals("")) {
                    if (tempSearchList.size() > 0) {
                        if (extension.executeStrategy(ViewUserMessageList.this, "", Implementation.INTERNET)) {
                            progressLoaderHelper.showProgress(ViewUserMessageList.this);
                            JSONArray jsonArray = new JSONArray();
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("sid", "" + Constant._id);
                                jsonObject.put("msg", content);
                                jsonObject.put("stat", "0");
                                jsonObject.put("time", Constant.getMobileDate(Constant.datePattern_date));
                                jsonArray.put(jsonObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Log.e("msg", tempSearchList.get(0).user_id);
                            SendDirectMsgDao msgDao = new SendDirectMsgDao(Constant._id, tempSearchList.get(0).user_id, jsonArray.toString());
                            msgDao.query(new AsyncCallback<GsonClass>() {
                                @Override
                                public void onOperationCompleted(GsonClass result, Exception e) {
                                    progressLoaderHelper.dismissProgress();
                                    dialog.dismiss();
                                    viewMessage();
                                    if (e == null && result != null) {
                                        if (!result.success.equals("1")) {
                                            Toast.makeText(ViewUserMessageList.this, result.message, Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Constant.setAlert(ViewUserMessageList.this, getString(R.string.wrong));
                                    }
                                }
                            });
                        } else {
                            Constant.setAlert(ViewUserMessageList.this, getString(R.string.no_internet));
                        }
                    } else {
                        Constant.setAlert(ViewUserMessageList.this, "Select Friend first.");
                    }
                } else if (to.equals("")) {
                    Constant.setAlert(ViewUserMessageList.this, "All field required");
                } else if (content.equals("")) {
                    Constant.setAlert(ViewUserMessageList.this, "All field required");
                }
            }
        });

        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Constant.hideSoftKeyboard(ViewUserMessageList.this);
                    return true;
                }
                return false;
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String textToGet = searchEditText.getText().toString().trim();
                tempSearchList = new ArrayList<>();
                alist = new ArrayList<>();
                if (textToGet.length() == 0) {
                    resultForSearch = new ArrayList<>();
                } else if (textToGet.length() == 1) {
                    if (extension.executeStrategy(ViewUserMessageList.this, "", ValidationTemplate.INTERNET)) {
                        FriendSuggestionDao profileSender = new FriendSuggestionDao(Constant._id, textToGet);
                        profileSender.query(new AsyncCallback<ViewMessageGsonClass>() {
                            @Override
                            public void onOperationCompleted(ViewMessageGsonClass result, Exception e) {
                                if (result != null && result.post_info != null) {
                                    resultForSearch = result.post_info;
                                    for (int z = 0; z < resultForSearch.size(); z++) {
                                        if (!Constant._id.equals(resultForSearch.get(z).user_id)) {
                                            alist.add(resultForSearch.get(z).name);
                                            tempSearchList.add(resultForSearch.get(z));
                                        }
                                    }
                                    searchEditText.setAdapter(new ArrayAdapter<>(ViewUserMessageList.this, R.layout.search_adapter, R.id.searchText, alist));
                                }
                            }
                        });
                    }
                } else {
                    if (resultForSearch != null) {
                        for (ViewMessageGsonClass.Post_info result : resultForSearch) {
                            if (StringUtils.containsIgnoreCase(result.name, textToGet)) {
                                tempSearchList.add(result);
                                alist.add(result.name);
                            }
                        }
                        searchEditText.setAdapter(new ArrayAdapter<>(ViewUserMessageList.this, R.layout.search_adapter, R.id.searchText, alist));
                    }
                }
            }
        });
        dialog.show();
    }

    class CustomListAdapter extends BaseAdapter {

        private LayoutInflater layoutInflater;
        private ViewHolder viewHolder;

        public CustomListAdapter(Context c) {
            layoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                view = layoutInflater.inflate(R.layout.messages_item, viewGroup, false);
                viewHolder.mUserNameTxt = (TextView) view.findViewById(R.id.user_name);
                viewHolder.mMessageTxt = (TextView) view.findViewById(R.id.message_content);
                viewHolder.mMore = (TextView) view.findViewById(R.id.more);
                viewHolder.mMessageDT = (TextView) view.findViewById(R.id.message_date);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            ViewMessageGsonClass.Post_info item = list.get(position);

            viewHolder.mUserNameTxt.setText(item.name);
            if (item.message != null) {
                viewHolder.mMessageTxt.setText(item.message.get(0).msg);
                viewHolder.mMessageDT.setText(item.message.get(0).time);
            }
            return view;
        }

        class ViewHolder {
            private TextView mUserNameTxt, mMessageTxt, mMore, mMessageDT;
        }
    }
}