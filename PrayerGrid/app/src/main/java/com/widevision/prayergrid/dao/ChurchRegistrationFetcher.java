package com.widevision.prayergrid.dao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.widevision.prayergrid.util.Constant;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by widevision on 5/2/15.
 */
public class ChurchRegistrationFetcher extends QueryManager<GsonClass> {

    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    String name, email, pin, city, state, country, denomination, leader, phone, linkname, memberapproval, tv;
    RequestBody requestBody;

    /*
Tag:church_register
Fields:
name (Church Name)
email
password
city
state
country
denomination
leader (Church Leader)
phone
linkname (URL for your church)
memberapproval  (Automatically .. join church prayer group)
tv   ( contribute recorded sermons to ThePrayerPeople TV)*/

    public ChurchRegistrationFetcher(String name, String email, String pin, String city, String state, String country, String denomination, String leader, String phone, String linkname, String memberapproval, String tv) {
        this.name = name;
        this.email = email;
        this.pin = pin;

        requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("tag", "church_register")
                .addFormDataPart("name", name)
                .addFormDataPart("email", email)
                .addFormDataPart("password", pin)
                .addFormDataPart("city", city)
                .addFormDataPart("state", state)
                .addFormDataPart("country", country)
                .addFormDataPart("denomination", denomination)
                .addFormDataPart("leader", leader)
                .addFormDataPart("phone", phone)
                .addFormDataPart("linkname", linkname)
                .addFormDataPart("memberapproval", memberapproval)
                .addFormDataPart("tv", tv)
                .build();
    }

    @Override
    protected Request.Builder buildRequest() {
        Request.Builder request = new Request.Builder();
        request.url(Constant.URL).post(requestBody).build();
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
