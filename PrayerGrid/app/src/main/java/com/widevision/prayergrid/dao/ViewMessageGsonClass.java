package com.widevision.prayergrid.dao;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by mercury-five on 15/03/16.
 */
public class ViewMessageGsonClass {

    public String success;
    public String message;
    public String profile_rec;
    public ArrayList<Post_info> post_info;

    public static class Post_info implements Comparable<Post_info> {
        public ArrayList<Message> message;
        public String name;
        public String profilepic;
        public String user_id;
        public int id;

        @Override
        public int compareTo(Post_info post_info) {
            return id - post_info.id;
        }
    }

    public static class Message {
        /*{
                            "sid": 1,
                            "msg": "tr",
                            "time": "01/14/2016",
                            "stat": 0
                        }*/
        public String sid;
        public String msg;
        public String time;
        public String stat;
    }
}

