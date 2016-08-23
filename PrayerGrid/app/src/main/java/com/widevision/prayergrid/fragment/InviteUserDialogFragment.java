package com.widevision.prayergrid.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.widevision.prayergrid.R;
import com.widevision.prayergrid.dao.FriendSuggestionDao;
import com.widevision.prayergrid.dao.GsonClass;
import com.widevision.prayergrid.dao.InviteUserDao;
import com.widevision.prayergrid.dao.ViewMessageGsonClass;
import com.widevision.prayergrid.util.AsyncCallback;
import com.widevision.prayergrid.util.Constant;
import com.widevision.prayergrid.util.Extension;
import com.widevision.prayergrid.util.ProgressLoaderHelper;
import com.widevision.prayergrid.util.ValidationTemplate;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mercury-five on 22/03/16.
 */
public class InviteUserDialogFragment extends DialogFragment {
    private Extension extension;
    private ProgressLoaderHelper progressLoaderHelper;

    @Bind(R.id.title)
    TextView mTitle;
    @Bind(R.id.action_cancel)
    ImageView mActionCancel;
    @Bind(R.id.btn_layout)
    LinearLayout mBtnLayout;

    @Bind(R.id.email_layout)
    LinearLayout mEmailLayout;

    @Bind(R.id.action_new_user)
    Button mActionNewUser;
    @Bind(R.id.action_existing_user)
    Button mExistionUser;

    @Bind(R.id.email)
    EditText mEmailEdt;
    @Bind(R.id.action_invite)
    Button mActionInviteBtn;

    @Bind(R.id.auto_layout)
    LinearLayout mAutoLayout;
    @Bind(R.id.name_autocomplete)
    AutoCompleteTextView mNameEdt;
    @Bind(R.id.action_invite_auto)
    Button mActionInviteAuto;

    private String group_id = "";

    private ArrayList<String> alist;
    private List<ViewMessageGsonClass.Post_info> resultForSearch, tempSearchList;

    public static InviteUserDialogFragment newInstance(String group_id) {
        InviteUserDialogFragment fragment = new InviteUserDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("group_id", group_id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();
        if (b != null) {
            group_id = b.getString("group_id");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.invite_user_layout, container, false);
        init();
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mActionNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEmailLayout.setVisibility(View.VISIBLE);
                mAutoLayout.setVisibility(View.GONE);
            }
        });

        mExistionUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEmailLayout.setVisibility(View.GONE);
                mAutoLayout.setVisibility(View.VISIBLE);
            }
        });

        mActionCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        mActionInviteAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constant.buttonEnable) {
                    Constant.setButtonEnable();
                    String userName = mNameEdt.getText().toString().trim();
                    if (!userName.isEmpty()) {
                       /* int size = tempSearchList.size();
                        String user_id = "";
                        for (int i = 0; i < size; i++) {
                            if (userName.equals(tempSearchList.get(i).name)) {
                                user_id = tempSearchList.get(i).user_id;
                            }
                        }
if (!user_id.equals("")){

}else{

}*/
                        if (tempSearchList.size() > 0) {
                            if (extension.executeStrategy(getActivity(), "", ValidationTemplate.INTERNET)) {
                                progressLoaderHelper.showProgress(getActivity());
                                InviteUserDao inviteUserDao = new InviteUserDao(tempSearchList.get(0).user_id, group_id);
                                inviteUserDao.query(new AsyncCallback<GsonClass>() {
                                    @Override
                                    public void onOperationCompleted(GsonClass result, Exception e) {
                                        progressLoaderHelper.dismissProgress();
                                        if (e == null && result != null) {
                                            if (result.success.equals("1")) {
                                                dismiss();
                                                Constant.setAlert(getActivity(), "Invitation sent successful.");
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
                        } else {
                            Constant.setAlert(getActivity(), "Enter friend name.");
                        }
                    } else {
                        Constant.setAlert(getActivity(), "Enter friend name.");
                    }
                }
            }
        });


        mNameEdt.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Constant.hideSoftKeyboard(getActivity());
                    return true;
                }
                return false;
            }
        });

        mNameEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String textToGet = mNameEdt.getText().toString().trim();
                tempSearchList = new ArrayList<>();
                alist = new ArrayList<>();
                if (textToGet.length() == 0) {
                    resultForSearch = new ArrayList<>();
                } else if (textToGet.length() == 1) {
                    if (extension.executeStrategy(getActivity(), "", ValidationTemplate.INTERNET)) {
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
                                    mNameEdt.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.search_adapter, R.id.searchText, alist));
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
                        mNameEdt.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.search_adapter, R.id.searchText, alist));
                    }
                }
            }
        });

    }

    void init() {
        extension = new Extension();
        progressLoaderHelper = ProgressLoaderHelper.getInstance();
    }
}
