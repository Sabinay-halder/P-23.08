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
public class LoginDao extends QueryManager<String> {

    String email, pin, device_token;

    public LoginDao(String email, String pin, String device_token) {

        this.email = email;
        this.pin = pin;
        this.device_token = device_token;
/*login tag:login
login field:
email
password
device_type, device_token*/
    }

    @Override
    protected Request.Builder buildRequest() {

        RequestBody formBody = new FormBody.Builder()
                .add("tag", "login")
                .add("email", email)
                .add("password", pin)
                .add("device_type", "0")
                .add("device_token", device_token)
                .build();
        Request.Builder request = new Request.Builder();
        request.url(Constant.URL).post(formBody).build();

        return request;
    }

    @Override
    protected String parseResponse(String jsonResponse) {

        Log.e("", "responce --- " + jsonResponse);

      /*  GsonClass agents = null;
        try {
            Gson gson = new GsonBuilder().create();
            agents = gson.fromJson(jsonResponse, GsonClass.class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }*/
        return jsonResponse;
    }

}
