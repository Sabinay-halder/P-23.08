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

/**
 * Created by widevision on 5/2/15.
 */
public class CreateGroupDao extends QueryManager<GsonClass> {


    String user_id, gname, type, privacy, details, settings, news,
            status;
    File cover, profilepic, gimage, vimage;
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    RequestBody requestBody;
    byte[] coverByte, profileBtye;

    public CreateGroupDao(String user_id, String gname, String type, String privacy, String details, String settings, String news, String status, File cover, File profilepic) {

        this.user_id = user_id;
        this.gname = gname;
        this.type = type;
        this.privacy = privacy;
        this.details = details;
        this.settings = settings;
        this.news = news;
        this.status = status;
        this.cover = cover;
        this.profilepic = profilepic;
        this.gimage = gimage;
        this.vimage = vimage;
/*MAin tag: setNewGroup
(user_id,gname,type,privacy,details,settings,news,status,cover,profilepic ,gimage, vimage)
*/
        requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("tag", "setNewGroup")
                .addFormDataPart("user_id", user_id)
                .addFormDataPart("gname", gname)
                .addFormDataPart("type", type)
                .addFormDataPart("privacy", privacy)
                .addFormDataPart("details", details)
                .addFormDataPart("settings", settings)
                .addFormDataPart("news", news)
                .addFormDataPart("status", status)
                .addFormDataPart("cover", "coverpic.png", RequestBody.create(MEDIA_TYPE_PNG, cover))
                .addFormDataPart("profilepic", "profile_pic.png", RequestBody.create(MEDIA_TYPE_PNG, profilepic))
                .build();
    }

    public CreateGroupDao(String user_id, String gname, String type, String privacy, String details, String settings, String news, String status, byte[] cover, byte[] profilepic) {

        this.user_id = user_id;
        this.gname = gname;
        this.type = type;
        this.privacy = privacy;
        this.details = details;
        this.settings = settings;
        this.news = news;
        this.status = status;
        this.coverByte = cover;
        this.profileBtye = profilepic;
        this.gimage = gimage;
        this.vimage = vimage;
/*MAin tag: setNewGroup
(user_id,gname,type,privacy,details,settings,news,status,cover,profilepic ,gimage, vimage)
*/
        requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("tag", "setNewGroup")
                .addFormDataPart("user_id", user_id)
                .addFormDataPart("gname", gname)
                .addFormDataPart("type", type)
                .addFormDataPart("privacy", privacy)
                .addFormDataPart("details", details)
                .addFormDataPart("settings", settings)
                .addFormDataPart("news", news)
                .addFormDataPart("status", status)
                .addFormDataPart("cover", "coverpic.png", RequestBody.create(MEDIA_TYPE_PNG, cover))
                .addFormDataPart("profilepic", "profile_pic.png", RequestBody.create(MEDIA_TYPE_PNG, profilepic))
                .build();
    }

    public CreateGroupDao(String user_id, String gname, String type, String privacy, String details, String settings, String news, String status, byte[] cover, File profilepic) {

        this.user_id = user_id;
        this.gname = gname;
        this.type = type;
        this.privacy = privacy;
        this.details = details;
        this.settings = settings;
        this.news = news;
        this.status = status;
        this.coverByte = cover;
        this.profilepic = profilepic;
        this.gimage = gimage;
        this.vimage = vimage;
/*MAin tag: setNewGroup
(user_id,gname,type,privacy,details,settings,news,status,cover,profilepic ,gimage, vimage)
*/
        requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("tag", "setNewGroup")
                .addFormDataPart("user_id", user_id)
                .addFormDataPart("gname", gname)
                .addFormDataPart("type", type)
                .addFormDataPart("privacy", privacy)
                .addFormDataPart("details", details)
                .addFormDataPart("settings", settings)
                .addFormDataPart("news", news)
                .addFormDataPart("status", status)
                .addFormDataPart("cover", "coverpic.png", RequestBody.create(MEDIA_TYPE_PNG, cover))
                .addFormDataPart("profilepic", "profile_pic.png", RequestBody.create(MEDIA_TYPE_PNG, profilepic))
                .build();
    }

    public CreateGroupDao(String user_id, String gname, String type, String privacy, String details, String settings, String news, String status, File cover, byte[] profilepic) {

        this.user_id = user_id;
        this.gname = gname;
        this.type = type;
        this.privacy = privacy;
        this.details = details;
        this.settings = settings;
        this.news = news;
        this.status = status;
        this.cover = cover;
        this.profileBtye = profilepic;
        this.gimage = gimage;
        this.vimage = vimage;
/*MAin tag: setNewGroup
(user_id,gname,type,privacy,details,settings,news,status,cover,profilepic ,gimage, vimage)
*/
        requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("tag", "setNewGroup")
                .addFormDataPart("user_id", user_id)
                .addFormDataPart("gname", gname)
                .addFormDataPart("type", type)
                .addFormDataPart("privacy", privacy)
                .addFormDataPart("details", details)
                .addFormDataPart("settings", settings)
                .addFormDataPart("news", news)
                .addFormDataPart("status", status)
                .addFormDataPart("cover", "coverpic.png", RequestBody.create(MEDIA_TYPE_PNG, cover))
                .addFormDataPart("profilepic", "profile_pic.png", RequestBody.create(MEDIA_TYPE_PNG, profilepic))
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
