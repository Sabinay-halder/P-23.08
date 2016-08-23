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
public class GetGroupPostDao extends QueryManager<GsonClass> {

    String user_id, gid, pageCount, type;

    public GetGroupPostDao(String user_id, String gid, String pageCount, String type) {
        this.user_id = user_id;
        this.gid = gid;
        this.pageCount = pageCount;
        this.type = type;
/*Group All Post
Main tag: setGroupAllPost
(user_id,gid,type)*/
    }

    @Override
    protected Request.Builder buildRequest() {

        RequestBody formBody = new FormBody.Builder()
                .add("tag", "getGroupAllPost")
                .add("user_id", user_id)
                .add("gid", gid)
                .add("pageid", pageCount)
                .add("type", type)
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
