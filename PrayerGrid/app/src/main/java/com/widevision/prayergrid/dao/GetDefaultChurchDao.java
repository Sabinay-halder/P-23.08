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
public class GetDefaultChurchDao extends QueryManager<GsonClass> {


    String user_id, church_name;

    public GetDefaultChurchDao(String user_id, String church_name) {

        this.user_id = user_id;
        this.church_name = church_name;
/*Default Church
Main tag:getDefaultChurch
(user_id,church_name)*/
    }

    @Override
    protected Request.Builder buildRequest() {

        RequestBody formBody = new FormBody.Builder()
                .add("tag", "getDefaultChurch")
                .add("user_id", user_id)
                .add("church_name", church_name)
                .build();
        Request.Builder request = new Request.Builder();
        request.url(Constant.URL).post(formBody).build();

        return request;
    }

    @Override
    protected GsonClass parseResponse(String jsonResponse) {

        GsonClass agents = null;
        try {
            Gson gson = new GsonBuilder().create();
            agents = gson.fromJson(jsonResponse, GsonClass.class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return agents;
    }

}
