package com.widevision.prayergrid.dao;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.widevision.prayergrid.util.Constant;

import java.io.File;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

public class EditProfileDao extends QueryManager<GsonClass> {

    String email, user_id, name, gender, country, state, notifications, password, church_name, profilePicStr;
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private File profilePic;
    RequestBody requestBody;

    public EditProfileDao(String user_id, String name, String email, File profilepic, String gender, String country, String state, String notifications, String password, String church_name) {

        this.user_id = user_id;
        this.name = name;
        this.email = email;
        this.profilePic = profilepic;
        this.gender = gender;
        this.country = country;
        this.state = state;
        this.notifications = notifications;
        this.password = password;
        this.church_name = church_name;
/* EditProfile
Main tag: setEditProfile
(user_id,name,email,profilepic,gender,country,state,notifications)
 notifications={"feed":"0","pray":"1","comment":"1"}*/
        requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("tag", "setEditProfile")
                .addFormDataPart("user_id", user_id)
                .addFormDataPart("email", email)
                .addFormDataPart("name", name)
                .addFormDataPart("gender", gender)
                .addFormDataPart("country", country)
                .addFormDataPart("state", state)
                .addFormDataPart("notifications", notifications)
                .addFormDataPart("password", password)
                .addFormDataPart("church", church_name)
                .addFormDataPart("profilepic", "profile_pic.png", RequestBody.create(MEDIA_TYPE_PNG, profilePic))
                .build();
    }

    public EditProfileDao(String user_id, String name, String email, String profilepic, String gender, String country, String state, String notifications, String password, String church_name) {
        this.user_id = user_id;
        this.name = name;
        this.email = email;
        this.profilePicStr = profilepic;
        this.gender = gender;
        this.country = country;
        this.state = state;
        this.notifications = notifications;
        this.password = password;
        this.church_name = church_name;
/* EditProfile
Main tag: setEditProfile
(user_id,name,email,profilepic,gender,country,state,notifications)
 notifications={"feed":"0","pray":"1","comment":"1"}*/
        requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("tag", "setEditProfile")
                .addFormDataPart("user_id", user_id)
                .addFormDataPart("email", email)
                .addFormDataPart("name", name)
                .addFormDataPart("gender", gender)
                .addFormDataPart("country", country)
                .addFormDataPart("state", state)
                .addFormDataPart("notifications", notifications)
                .addFormDataPart("password", password)
                .addFormDataPart("church", church_name)
                .addFormDataPart("profilepic", "test")
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