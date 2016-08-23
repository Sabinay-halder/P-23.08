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
public class FriendSuggestionDao extends QueryManager<ViewMessageGsonClass> {

    String text, user_id;

    public FriendSuggestionDao(String user_id, String text) {
        this.text = text;
        this.user_id = user_id;
/*mainTag: getAutoSuggestName (user_id,keyword)*/
    }

    @Override
    protected Request.Builder buildRequest() {

        RequestBody formBody = new FormBody.Builder()
                .add("tag", "getAutoSuggestName")
                .add("user_id", user_id)
                .add("keyword", text)
                .build();
        Request.Builder request = new Request.Builder();
        request.url(Constant.URL).post(formBody).build();
        return request;
    }

    @Override
    protected ViewMessageGsonClass parseResponse(String jsonResponse) {

        Log.e("", "responce --- " + jsonResponse);

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
