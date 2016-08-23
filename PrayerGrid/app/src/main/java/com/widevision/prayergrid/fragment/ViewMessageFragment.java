package com.widevision.prayergrid.fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.Validator;
import com.widevision.prayergrid.R;
import com.widevision.prayergrid.dao.GetMessageUserDao;
import com.widevision.prayergrid.dao.GsonClass;
import com.widevision.prayergrid.dao.ViewMessageGsonClass;
import com.widevision.prayergrid.model.HideKeyFragment;
import com.widevision.prayergrid.util.AsyncCallback;
import com.widevision.prayergrid.util.Constant;
import com.widevision.prayergrid.util.Extension;
import com.widevision.prayergrid.util.ProgressLoaderHelper;
import com.widevision.prayergrid.util.ValidationTemplate;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ViewMessageFragment extends Fragment {

    private View view;
    private Extension extension;
    private ProgressLoaderHelper progressLoaderHelper;

    @Bind(R.id.list)
    ListView mList;
    @Bind(R.id.new_msg_layout)
    LinearLayout mAddMsg;

    private ArrayList<ViewMessageGsonClass.Post_info> list;
    private CustomListAdapter adapter;

    public ViewMessageFragment() {
        // Required empty public constructor
    }

    public static ViewMessageFragment newInstance() {
        ViewMessageFragment fragment = new ViewMessageFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_view_message2, container, false);
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

        viewMessage();

        return view;
    }

    private void viewMessage() {
        if (extension.executeStrategy(getActivity(), "", ValidationTemplate.INTERNET)) {
            GetMessageUserDao messageUserDao = new GetMessageUserDao(Constant._id);
            messageUserDao.query(new AsyncCallback<ViewMessageGsonClass>() {
                @Override
                public void onOperationCompleted(ViewMessageGsonClass result, Exception e) {
                    if (e == null && result != null) {
                        if (result.success.equals("1")) {
                            if (result.post_info != null && result.post_info.size() != 0) {
                                list = result.post_info;
                                adapter = new CustomListAdapter(getActivity());
                                mList.setAdapter(adapter);
                            } else {
                                Constant.setAlert(getActivity(), "No message.");
                            }
                        } else {
                            Constant.setAlert(getActivity(), "No message.");
                        }
                    } else {
                        Constant.setAlert(getActivity(), getActivity().getString(R.string.wrong));
                    }
                }
            });
        } else {
            Constant.setAlert(getActivity(), getActivity().getString(R.string.no_internet));
        }
    }

    void init() {
        ButterKnife.bind(this, view);
        extension = new Extension();
        progressLoaderHelper = ProgressLoaderHelper.getInstance();
    }

    void addMsgDialog() {
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.post_message);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageView submit = (ImageView) dialog.findViewById(R.id.action_submit);
        final EditText contentEdt = (EditText) dialog.findViewById(R.id.content_text);
        final EditText toEdt = (EditText) dialog.findViewById(R.id.to_edt);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String to = toEdt.getText().toString().trim();
                String content = contentEdt.getText().toString().trim();
                if (!to.equals("") && !content.equals("")) {
                    Constant.setAlert(getActivity(), getActivity().getString(R.string.progress));
                } else if (to.equals("")) {
                    Constant.setAlert(getActivity(), "All field required");
                } else if (content.equals("")) {
                    Constant.setAlert(getActivity(), "All field required");
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
            view = layoutInflater.inflate(R.layout.messages_item, viewGroup, false);
            if (view == null) {
                viewHolder = new ViewHolder();
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
            viewHolder.mMessageTxt.setText(item.message.get(0).msg);
            viewHolder.mMessageDT.setText(item.message.get(0).time);
            return view;
        }

        class ViewHolder {
            private TextView mUserNameTxt, mMessageTxt, mMore, mMessageDT;
        }
    }
}
