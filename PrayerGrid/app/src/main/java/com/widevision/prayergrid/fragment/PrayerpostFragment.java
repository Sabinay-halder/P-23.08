package com.widevision.prayergrid.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.widevision.prayergrid.R;
import com.widevision.prayergrid.activity.HomeActivity;
import com.widevision.prayergrid.dao.ChurchPostPrayerDao;
import com.widevision.prayergrid.dao.GsonClass;
import com.widevision.prayergrid.dao.UserPostPrayerDao;
import com.widevision.prayergrid.model.HideKeyFragment;
import com.widevision.prayergrid.util.AsyncCallback;
import com.widevision.prayergrid.util.Constant;
import com.widevision.prayergrid.util.Extension;
import com.widevision.prayergrid.util.Implementation;
import com.widevision.prayergrid.util.ProgressLoaderHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PrayerpostFragment extends HideKeyFragment implements Validator.ValidationListener {

    private View view;

    @NotEmpty(message = "Please enter your prayer.")
    @Bind(R.id.edittext_pray)
    EditText mPrayEdt;
    @Bind(R.id.catagory)
    Spinner mCatagorySp;
    @Bind(R.id.action_submit)
    ImageView mSubmitBtn;
    @Bind(R.id.check)
    CheckBox mCheck;
    @Bind(R.id.action_cancel)
    ImageView mCancelBtn;

    private String categoryList[];
    private String type = "", id = "";

    private Validator validator;
    private Extension extension;
    private ProgressLoaderHelper progressLoaderHelper;

    public PrayerpostFragment() {
        // Required empty public constructor
    }

    public static PrayerpostFragment newInstance(String type, String id) {
        PrayerpostFragment fragment = new PrayerpostFragment();
        Bundle b = new Bundle();
        b.putString("type", type);
        b.putString("id", id);
        fragment.setArguments(b);
        return fragment;
    }

    public static PrayerpostFragment newInstance(String type) {
        PrayerpostFragment fragment = new PrayerpostFragment();
        Bundle b = new Bundle();
        b.putString("type", type);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();
        type = b.getString("type");
        id = b.getString("id");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.pray_now_post, container, false);
        ButterKnife.bind(this, view);
        addCategory();
        init();

        if (type.equals("1")) {
            mCheck.setVisibility(View.GONE);
        }


        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        TimeLineFragment.itemClickTag = 0;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCatagorySp.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.row_spinner, categoryList) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(Color.WHITE);
                return view;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(Color.parseColor("#3C4D5C"));
                text.setSingleLine(false);
                return view;
            }
        });

        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constant.buttonEnable) {
                    validator.validate();
                }
            }
        });
        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constant.buttonEnable) {
                    Constant.setButtonEnable();
                    HomeActivity.fragmentManager.popBackStack();
                }
            }
        });
    }

    void init() {
        validator = new Validator(this);
        validator.setValidationListener(this);
        extension = new Extension();
        progressLoaderHelper = ProgressLoaderHelper.getInstance();
        setupUI(view);
    }

    void addCategory() {
        categoryList = getActivity().getResources().getStringArray(R.array.category_post);
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        Constant.setAlert(getActivity(), errors.get(errors.size() - 1).getCollatedErrorMessage(getActivity()));
    }

    @Override
    public void onValidationSucceeded() {
        if (type.equals("0")) {
            attemptPost();
        } else {
            attemptChurchPost();
        }
    }

    void attemptPost() {

        String msg = mPrayEdt.getText().toString().trim();
        String type = "0";
        if (mCheck.isChecked()) {
            type = "2";
        }
        String category = "" + mCatagorySp.getSelectedItemPosition();

        if (extension.executeStrategy(getActivity(), "", Implementation.INTERNET)) {
            progressLoaderHelper.showProgress(getActivity());
            UserPostPrayerDao prayerDao = new UserPostPrayerDao(Constant._id, type, category, msg);
            prayerDao.query(new AsyncCallback<GsonClass>() {
                @Override
                public void onOperationCompleted(GsonClass result, Exception e) {
                    progressLoaderHelper.dismissProgress();
                    if (e == null && result != null) {
                        if (result.success.equals("1")) {
                            Toast.makeText(getActivity(), result.message, Toast.LENGTH_SHORT).show();
                            HomeActivity.fragmentManager.popBackStack();
                            getActivity().sendBroadcast(new Intent("timeline"));
                        } else {
                            Constant.setAlert(getActivity(), result.message);
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


    void attemptChurchPost() {
        String msg = mPrayEdt.getText().toString().trim();
        String category = "" + mCatagorySp.getSelectedItemPosition();

        if (extension.executeStrategy(getActivity(), "", Implementation.INTERNET)) {
            progressLoaderHelper.showProgress(getActivity());
            ChurchPostPrayerDao prayerDao = new ChurchPostPrayerDao(Constant._id, type, id, category, msg);
            prayerDao.query(new AsyncCallback<GsonClass>() {
                @Override
                public void onOperationCompleted(GsonClass result, Exception e) {
                    progressLoaderHelper.dismissProgress();
                    if (e == null && result != null) {
                        if (result.success.equals("1")) {
                            Toast.makeText(getActivity(), result.message, Toast.LENGTH_SHORT).show();
                            HomeActivity.fragmentManager.popBackStack();
                            getActivity().sendBroadcast(new Intent("timeline_church"));
                        } else {
                            Constant.setAlert(getActivity(), result.message);
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
}