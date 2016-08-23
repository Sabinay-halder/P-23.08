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
public class RegistrationFetcher extends QueryManager<GsonClass> {

    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private String name, email, pin;
    private File profilePic;
    RequestBody requestBody;

    public RegistrationFetcher(String name, String email, String pin, File profilePic, String country,String device_token) {
        this.name = name;
        this.email = email;
        this.pin = pin;
        this.profilePic = profilePic;
        /*(name,email,password,image,device_type, device_token)*/
        requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("tag", "register")
                .addFormDataPart("name", name)
                .addFormDataPart("email", email)
                .addFormDataPart("password", pin)
                .addFormDataPart("country", country)
                .addFormDataPart("device_type", "0")
                .addFormDataPart("device_token", device_token)
                .addFormDataPart("image", "profile_pic.png", RequestBody.create(MEDIA_TYPE_PNG, profilePic))
                .build();
    }

    public RegistrationFetcher(String name, String email, String pin, String country,String device_token) {
        this.name = name;
        this.email = email;
        this.pin = pin;
        this.profilePic = profilePic;
        /*(name,email,password,image)*/
        requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("tag", "register")
                .addFormDataPart("name", name)
                .addFormDataPart("email", email)
                .addFormDataPart("password", pin)
                .addFormDataPart("country", country)
                .addFormDataPart("device_type", "0")
                .addFormDataPart("device_token", device_token)
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
