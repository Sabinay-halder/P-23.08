package com.widevision.prayergrid.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.widevision.prayergrid.R;
import com.widevision.prayergrid.activity.HomeActivity;
import com.widevision.prayergrid.dao.ChurchPostPrayerDao;
import com.widevision.prayergrid.dao.GroupPostPrayerDao;
import com.widevision.prayergrid.dao.GsonClass;
import com.widevision.prayergrid.model.HideKeyFragment;
import com.widevision.prayergrid.util.AsyncCallback;
import com.widevision.prayergrid.util.Constant;
import com.widevision.prayergrid.util.Extension;
import com.widevision.prayergrid.util.Implementation;
import com.widevision.prayergrid.util.ProgressLoaderHelper;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class GroupPrayerpostFragment extends HideKeyFragment implements Validator.ValidationListener {

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

    public GroupPrayerpostFragment() {
        // Required empty public constructor
    }

    public static GroupPrayerpostFragment newInstance(String type, String id) {
        GroupPrayerpostFragment fragment = new GroupPrayerpostFragment();
        Bundle b = new Bundle();
        b.putString("type", type);
        b.putString("id", id);
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
        mCheck.setVisibility(View.GONE);

        if (type.equals("0")) {
            mPrayEdt.setHint("Receive prayer from others...");
        } else {
            mPrayEdt.setHint("Post your answered prayer here!");
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
        mCatagorySp.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.place_spinner_row, categoryList));

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
        attemptChurchPost();
    }

    void attemptChurchPost() {
        String msg = mPrayEdt.getText().toString().trim();
        String category = "" + mCatagorySp.getSelectedItemPosition();

        if (extension.executeStrategy(getActivity(), "", Implementation.INTERNET)) {
            progressLoaderHelper.showProgress(getActivity());
            GroupPostPrayerDao prayerDao = new GroupPostPrayerDao(Constant._id, type, id, category, msg);
            prayerDao.query(new AsyncCallback<GsonClass>() {
                @Override
                public void onOperationCompleted(GsonClass result, Exception e) {
                    progressLoaderHelper.dismissProgress();
                    if (e == null && result != null) {
                        if (result.success.equals("1")) {
                            Toast.makeText(getActivity(), result.message, Toast.LENGTH_SHORT).show();
                            HomeActivity.fragmentManager.popBackStack();
                            getActivity().sendBroadcast(new Intent("timeline_group"));
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