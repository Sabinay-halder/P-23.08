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
public class ChurchPostPrayerDao extends QueryManager<GsonClass> {


    String user_id, type, category, message,churchid;

    public ChurchPostPrayerDao(String user_id, String type,String churchid, String category, String message) {

        this.user_id = user_id;
        this.type = type;
        this.category = category;
        this.message = message;
        this.churchid = churchid;
/*main tag: churchPostPrayer
(user_id,churchid,type,category,cpmessage)*/
    }

    @Override
    protected Request.Builder buildRequest() {

        RequestBody formBody = new FormBody.Builder()
                .add("tag", "churchPostPrayer")
                .add("user_id", user_id)
                .add("type", type)
                .add("churchid", churchid)
                .add("category", category)
                .add("cpmessage", message)
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
