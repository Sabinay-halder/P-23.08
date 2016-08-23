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
public class GroupPostPrayerDao extends QueryManager<GsonClass> {


    String user_id, type, category, message,gid;

    public GroupPostPrayerDao(String user_id, String type, String gid, String category, String message) {

        this.user_id = user_id;
        this.type = type;
        this.category = category;
        this.message = message;
        this.gid = gid;
/*Group Post
Main tag: setGroupPost
(user_id,gid,type,gpmessage,category)
 type 0: prayer, 1 :answered*/
    }

    @Override
    protected Request.Builder buildRequest() {

        RequestBody formBody = new FormBody.Builder()
                .add("tag", "setGroupPost")
                .add("user_id", user_id)
                .add("type", type)
                .add("gid", gid)
                .add("category", category)
                .add("gpmessage", message)
                .build();
        Request.Builder request = new Request.Builder();
        request.url(Constant.URL).post(formBody).build();

        return request;
    }

    @Override
    protected GsonClass parseResponse(String jsonResponse) {

        Log.e("", "responce --- " + jsonResponse);

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
