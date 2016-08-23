package com.widevision.prayergrid.dao;

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
public class GetMessagesDao extends QueryManager<ViewMessageGsonClass> {


    String user_id, receiverid;

    public GetMessagesDao(String user_id, String receiverid) {

        this.user_id = user_id;
        this.receiverid = receiverid;
/* Main tag:getMessages
(user_id,receiverid)*/
    }

    @Override
    protected Request.Builder buildRequest() {

        RequestBody formBody = new FormBody.Builder()
                .add("tag", "getMessages")
                .add("user_id", user_id)
                .add("receiverid", receiverid)
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
