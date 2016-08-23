package com.widevision.prayergrid.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.widevision.prayergrid.R;
import com.widevision.prayergrid.dao.GsonClass;
import com.widevision.prayergrid.dao.SendDirectMsgDao;
import com.widevision.prayergrid.util.AsyncCallback;
import com.widevision.prayergrid.util.Constant;
import com.widevision.prayergrid.util.Extension;
import com.widevision.prayergrid.util.Implementation;
import com.widevision.prayergrid.util.ProgressLoaderHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PostMessageDialogFragment extends android.support.v4.app.DialogFragment {

    private Extension extension;
    private ProgressLoaderHelper progressLoaderHelper;

    @Bind(R.id.close_btn)
    ImageView mCloseBtn;
    @Bind(R.id.msg_txt)
    EditText mMsgEdt;
    @Bind(R.id.send_btn)
    Button mSendBtn;

    private String receiver_id = "";

    public static PostMessageDialogFragment newInstance(String receiver_id) {
        PostMessageDialogFragment fragment = new PostMessageDialogFragment();
        Bundle b = new Bundle();
        b.putString("receiver_id", receiver_id);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();
        if (b != null) {
            receiver_id = b.getString("receiver_id");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.fragment_post_message_dialog, container, false);
        init();
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = mMsgEdt.getText().toString().trim();
                if (!msg.equals("")) {
                    /*[{"sid":1,"msg":"tr","time":"01\/14\/2016","stat":0}]*/
                    if (extension.executeStrategy(getActivity(), "", Implementation.INTERNET)) {
                        JSONArray jsonArray = new JSONArray();
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("sid", "" + Constant._id);
                            jsonObject.put("msg", msg);
                            jsonObject.put("stat", "0");
                            jsonObject.put("time", Constant.getMobileDate(Constant.datePattern_date));
                            jsonArray.put(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.e("msg", jsonArray.toString());
                        SendDirectMsgDao msgDao = new SendDirectMsgDao(Constant._id, receiver_id, jsonArray.toString());
                        msgDao.query(new AsyncCallback<GsonClass>() {
                            @Override
                            public void onOperationCompleted(GsonClass result, Exception e) {
                                if (e == null && result != null) {
                                    if (result.success.equals("1")) {
                                        dismiss();
                                    } else {
                                        Toast.makeText(getActivity(), result.message, Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Constant.setAlert(getActivity(), getActivity().getResources().getString(R.string.wrong));
                                }
                            }
                        });
                    } else {
                        Constant.setAlert(getActivity(), getActivity().getResources().getString(R.string.no_internet));
                    }
                } else {
                    Constant.setAlert(getActivity(), "Please enter message.");
                }
            }
        });
    }

    void init() {
        extension = new Extension();
        progressLoaderHelper = ProgressLoaderHelper.getInstance();
    }
}