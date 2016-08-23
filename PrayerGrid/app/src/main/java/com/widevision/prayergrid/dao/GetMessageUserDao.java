package com.widevision.prayergrid.dao;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.widevision.prayergrid.util.Constant;

import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by widevision on 5/2/15.
 */
public class GetMessageUserDao extends QueryManager<ViewMessageGsonClass> {


    String user_id;

    public GetMessageUserDao(String user_id) {

        this.user_id = user_id;
/* getAllMessages
 Main tag:getAllMessages
(user_id)*/
    }

    @Override
    protected Request.Builder buildRequest() {

        RequestBody formBody = new FormBody.Builder()
                .add("tag", "getAllMessages")
                .add("user_id", user_id)
                .build();
        Request.Builder request = new Request.Builder();
        request.url(Constant.URL).post(formBody).build();

        return request;
    }

    @Override
    protected ViewMessageGsonClass parseResponse(String jsonResponse) {

        ViewMessageGsonClass agents = null;
        try {
            Gson gson = new GsonBuilder().create();
            agents = gson.fromJson(jsonResponse, ViewMessageGsonClass.class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return agents;
    }

}
