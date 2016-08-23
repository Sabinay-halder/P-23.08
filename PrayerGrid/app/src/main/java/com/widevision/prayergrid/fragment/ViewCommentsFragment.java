package com.widevision.prayergrid.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.google.gson.Gson;
import com.widevision.prayergrid.R;
import com.widevision.prayergrid.activity.HomeActivity;
import com.widevision.prayergrid.dao.DeleteCommentDao;
import com.widevision.prayergrid.dao.GetCommentDao;
import com.widevision.prayergrid.dao.GsonClass;
import com.widevision.prayergrid.dao.SetCommentDao;
import com.widevision.prayergrid.model.HideKeyFragment;
import com.widevision.prayergrid.util.AsyncCallback;
import com.widevision.prayergrid.util.Constant;
import com.widevision.prayergrid.util.Extension;
import com.widevision.prayergrid.util.Implementation;
import com.widevision.prayergrid.util.ProgressLoaderHelper;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;


public class ViewCommentsFragment extends HideKeyFragment {

    private View view;

    @Bind(R.id.action_cancel)
    ImageView mCancelBtn;
    @Bind(R.id.message_list)
    ListView mList;
    @Bind(R.id.msg_edt)
    EditText mMsgEdt;
    @Bind(R.id.action_submit)
    ImageView mSubmitBtn;
    private Extension extension;
    private ProgressLoaderHelper progressLoaderHelper;
    private String prayer_id = "";
    private ArrayList<GsonClass.Post_info> list;
    CustomListAdapter adapter;

    public ViewCommentsFragment() {
        // Required empty public constructor
    }


    public static ViewCommentsFragment newInstance(String id) {
        ViewCommentsFragment fragment = new ViewCommentsFragment();
        Bundle b = new Bundle();
        b.putString("id", id);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();
        if (b != null) {
            prayer_id = b.getString("id");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_view_message, container, false);
        ButterKnife.bind(this, view);
        init();
        setupUI(view);

        return view;
    }

    void init() {
        extension = new Extension();
        progressLoaderHelper = ProgressLoaderHelper.getInstance();
        setupUI(view);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeActivity.fragmentManager.popBackStack();
            }
        });

        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
            }
        });


        mMsgEdt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    validate();
                }
                return false;
            }
        });

        getAllComment();
    }

    void validate() {

        if (Constant.buttonEnable) {
            Constant.setButtonEnable();
            String msg = mMsgEdt.getText().toString().trim();
            if (!msg.equals("")) {
                if (extension.executeStrategy(getActivity(), "", Implementation.INTERNET)) {
                    Constant.hideSoftKeyboard(getActivity());
                    sumbmitComment(msg);
                } else {
                    Constant.setAlert(getActivity(), getActivity().getString(R.string.no_internet));
                }
            } else {
                Constant.setAlert(getActivity(), "Please enter your Comment.");
            }
        }
    }

    void sumbmitComment(String comment) {
        progressLoaderHelper.showProgress(getActivity());
        SetCommentDao commentDao = new SetCommentDao(Constant._id, Constant._type, prayer_id, comment);
        commentDao.query(new AsyncCallback<GsonClass>() {
            @Override
            public void onOperationCompleted(GsonClass result, Exception e) {
                progressLoaderHelper.dismissProgress();
                if (e == null && result != null) {
                    if (result.success.equals("1")) {
                        mMsgEdt.getText().clear();
                        getAllComment();
                    } else {
                        Constant.setAlert(getActivity(), result.message);
                    }
                } else {
                    Constant.setAlert(getActivity(), getActivity().getString(R.string.wrong));
                }
            }
        });
    }

    void getAllComment() {
        if (extension.executeStrategy(getActivity(), "", Implementation.INTERNET)) {
            GetCommentDao getCommentDao = new GetCommentDao(Constant._id, prayer_id, Constant._type);
            getCommentDao.query(new AsyncCallback<GsonClass>() {
                @Override
                public void onOperationCompleted(GsonClass result, Exception e) {
                    if (e == null && result != null) {
                        if (result.success.equals("1")) {
                            if (result.post_info != null && result.post_info.size() != 0) {
                                mMsgEdt.getText().clear();
                                try {
                                    list.clear();
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                                list = result.post_info;
                                if (adapter == null) {
                                    adapter = new CustomListAdapter(getActivity());
                                    mList.setAdapter(adapter);
                                } else {
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        } else {
                            //   Constant.setAlert(getActivity(), "No comment on this post.");
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

    class CustomListAdapter extends BaseAdapter {

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
        public View getView(final int i, View view, ViewGroup viewGroup) {
            final GsonClass.Post_info item = list.get(i);
            if (view == null) {
                view = layoutInflater.inflate(R.layout.comments_item, viewGroup, false);
                viewHolder = new ViewHolder();
                viewHolder.mProfilePic = (ImageView) view.findViewById(R.id.profile_image);
                viewHolder.mDeleteBtn = (ImageView) view.findViewById(R.id.action_delete);
                viewHolder.mCommentTxt = (TextView) view.findViewById(R.id.comments_msg);
                viewHolder.mUserNameTxt = (TextView) view.findViewById(R.id.user_name);
                viewHolder.name_txt = (TextView) view.findViewById(R.id.name_txt);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            viewHolder.mCommentTxt.setText(item.comment);
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
            if (item.userid.equals(Constant._id)) {
                viewHolder.mDeleteBtn.setVisibility(View.VISIBLE);
            } else {
                viewHolder.mDeleteBtn.setVisibility(View.GONE);
            }
            viewHolder.mDeleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Constant.buttonEnable) {
                        Constant.setButtonEnable();
                        deleteComment(i, item.cid);
                    }
                }
            });

            return view;
        }

        class ViewHolder {
            private ImageView mProfilePic, mDeleteBtn;
            private TextView mUserNameTxt, mCommentTxt, name_txt;
        }

        private void deleteComment(final int pos, String cid) {
            DeleteCommentDao commentDao = new DeleteCommentDao(Constant._id, cid);
            commentDao.query(new AsyncCallback<GsonClass>() {
                @Override
                public void onOperationCompleted(GsonClass result, Exception e) {
                    if (e == null && result != null) {
                        if (result.success.equals("1")) {
                            list.remove(pos);
                            notifyDataSetChanged();
                        } else {
                            Constant.setAlert(getActivity(), result.message);
                        }
                    } else {
                        Constant.setAlert(getActivity(), getString(R.string.wrong));
                    }
                }
            });
        }
    }
}
