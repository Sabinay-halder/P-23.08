package com.widevision.prayergrid.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class GsonClass {

    public String success;
    public String user_id;
    public String message;
    public String code;
    public String phone;
    public String email;
    public String name;
    public String type;
    public String profilepic;
    public String header_image;
    public String gender;
    public String country;
    public String state;
    public String url;
    public String defaultchurch;

    public Notifications notifications;

    public class Notifications {
        /*"comment": 0,
        "pray": 0,
        "feed": 0*/
        public String comment;
        public String pray;
        public String feed;
    }

    public ArrayList<Post_info> post_info;
    public Post_info group_info;
    public ArrayList<Invitation_info> invitation_info;

    public class Invitation_info {
        /*  "admin_id": "54",
                    "admin_name": "deepak",
                    "group_name": "fsd",
                    "group_profilepic": "http://103.231.44.154/prayergrid/images/prc1458888503_profile_pic.png",
                    "group_id": "73"*/
        public String admin_id;
        public String admin_name;
        public String group_name;
        public String group_profilepic;
        public String group_id;
        public String join = "0";

    }

    public static class Post_info {
        /*"name": "android",
            "profilepic": "",
            "message": "tsetstettsetsev set weat wtw"*/
        /* "prayer": "No",
            "name": "android",
            "userid": "34",
            "profilepic": "prc1458380518_profile_pic.png",
            "gpid": "3",
            "gpmessage": "pray for project........!",
            "category": "Careers & Business"*/


        public String name;
        public String profilepic;
        public String message;
        public String count;
        public String pid;
        public String type;
        public String category;
        public String prayer;
        public String comment;
        public String userid;
        public String id;
        public String header_image;
        public String leader;
        public String denomination;
        public String churchtiming;
        public String tithe_image;
        public String join;
        public String cpid;
        public String cpmessage;
        public String gpmessage;
        public String cid;
        public String tithe_link;
        public String gpid;
        public String admin_id;
        public String admin_name;
        public String created_at;
        public Address address;

        /* "id": "3",
            "name": "VEGAS CHURCH",
            "header_image": "Crosses.jpg",
            "leader": "",
            "denomination": "Denonimation",
            "churchtiming": "",
            "tithe_image": "Tithe.jpg",
            "address": "{\"city\":\"Dallas\",\"state\":\"\",\"country\":\"\"}"*/
/* "prayer": "No",
            "count": "SELECT * FROM `prayers` WHERE `pid`=6 AND uid=34",
            "name": "VEGAS CHURCH",
            "profilepic": "Church2.png",
            "cpid": "6",
            "cpmessage": "vegas new prayer",
            "category": "General"*/

        public String privacy;
        public String news;
        public String cover;
        public detail details;
        public settings settings;



        /* "tithe_link": "http://www.google.com",
            "name": "VEGAS CHURCH",
            "id": "3",
            "phone": "",
            "address": {
                "city": "Dallas",
                "state": "",
                "country": ""
            },
            "churchtiming": "",
            "news": "News for vegas church",
            "tithe_image": "http://103.231.44.154/prayergrid/images/Tithe.jpg",
            "profilepic": "http://103.231.44.154/prayergrid/images/Church2.png",
            "header_image": "http://103.231.44.154/prayergrid/images/Crosses.jpg"*/


        /*{
            "privacy": "1",
            "type": "1",
            "name": "Mohit",
            "details": {
                "give": {
                    "image": "Give.jpg",
                    "link": "sdfd"
                },
                "contact_email": "dsf",
                "time": "df",
                "location": {
                    "address": "sdfds",
                    "city": "dsf",
                    "state": "dsf",
                    "country": "sdf"
                },
                "phone": "545456",
                "volunteer": {
                    "image": "Volunteer1.jpg",
                    "link": ""
                }
            },
            "settings": {
                "give": "0",
                "contact": "0",
                "time": "0",
                "location": "0",
                "phone": "0",
                "volunteer": "0",
                "memberapprove": "0"
            },
            "news": "Gnews",
            "cover": "http://103.231.44.154/prayergrid/images/",
            "profilepic": "http://103.231.44.154/prayergrid/images/prc1458382478_Beautiful-Natural-Desktop-HD-Wallpapers-Serene-Collection-2013-9.jpg"
        }*/
    }

    public class settings {
        public String give;
        public String contact;
        public String time;
        public String location;
        public String phone;
        public String volunteer;
        public String memberapprove;
    }

    public class detail {
        /*"details": {
                "give": {
                    "image": "Give.jpg",
                    "link": "sdfd"
                },
                "contact_email": "dsf",
                "time": "df",
                "location": {
                    "address": "sdfds",
                    "city": "dsf",
                    "state": "dsf",
                    "country": "sdf"
                },
                "phone": "545456",
                "volunteer": {
                    "image": "Volunteer1.jpg",
                    "link": ""
                }
            }*/
        public String contact_email;
        public String time;
        public String phone;
        public give give;
        public location location;
        public volunteer volunteer;

        public class give {
            public String image;
            public String link;
        }

        public class location {
            public String address;
            public String city;
            public String state;
            public String country;
        }

        public class volunteer {
            public String image;
            public String link;
        }
    }

    public class Address {
        /*  "address": "{\"city\":\"Dallas\",\"state\":\"\",\"country\":\"\"}"*/
        public String city;
        public String state;
        public String country;
    }

    public class ViewMessage {

    }

}
