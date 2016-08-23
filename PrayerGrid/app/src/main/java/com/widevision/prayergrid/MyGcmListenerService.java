package com.widevision.prayergrid;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.gcm.GcmListenerService;
import com.widevision.prayergrid.R;
import com.widevision.prayergrid.activity.LoginActivity;
import com.widevision.prayergrid.activity.ViewInvitationActivity;
import com.widevision.prayergrid.activity.ViewSigleMessagesList;
import com.widevision.prayergrid.util.PreferenceConnector;


import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by mercury-three on 2/1/16.
 */
public class MyGcmListenerService extends GcmListenerService {

    public static final String MESSAGE_TYPE = "message_notification";
    public static final String INVITATION_TYPE = "Invitations_notification";
    public static final String PRAY_TYPE = "prayer_notification";
    public static final String COMMENT_TYPE = "comment_notification";

    @Override
    public void onMessageReceived(String from, Bundle data) {

        // createNotification(from, message);
        try {
            String message = data.getString("message");
            if (PreferenceConnector.readString(MyGcmListenerService.this, PreferenceConnector.IS_LOGIN, "").equals("Yes")) {
                JSONObject jsonObject = new JSONObject(message);
                String type = jsonObject.getString("type");
                if (type.equals(MESSAGE_TYPE)) {
                    JSONObject bodyObj = jsonObject.getJSONObject("body");
                    String msg = bodyObj.getString("message");
                    String name = bodyObj.getString("name");
                    JSONArray jsonArray = new JSONArray(msg);
                    JSONObject msgObject = jsonArray.getJSONObject(0);
                    String s_id = msgObject.getString("sid");
                    String user_msg = msgObject.getString("msg");

                    if (PreferenceConnector.readString(MyGcmListenerService.this, PreferenceConnector.MESSAGE_STATE, "0").equals("1")) {
                        if (PreferenceConnector.readString(MyGcmListenerService.this, PreferenceConnector.CHATTING_USER_ID, "").equals(s_id)) {
                            // send broadcast
                            sendBroadcast(new Intent(ViewSigleMessagesList.tag));
                        } else {
                            //show notification
                            Notify(name, user_msg, s_id);
                        }
                    } else {
                        // show notification
                        Notify(name, user_msg, s_id);
                    }
                } else if (type.equals(PRAY_TYPE)) {
                    JSONObject bodyObj = jsonObject.getJSONObject("body");
                    String msg = bodyObj.getString("message");
                    Notify("PrayerGrid", msg);
                } else if (type.equals(INVITATION_TYPE)) {
                    JSONObject bodyObj = jsonObject.getJSONObject("body");
                    String msg = bodyObj.getString("message");
                    NotifyInvitation("PrayerGrid", msg);
                } else if (type.equals(COMMENT_TYPE)) {
                    JSONObject bodyObj = jsonObject.getJSONObject("body");
                    String msg = bodyObj.getString("message");
                    Notify("PrayerGrid", msg);
                } else {
                    Notify("PrayerGrid", type);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void Notify(String notificationTitle, String notificationMessage, String s_id) {
        Intent intent = new Intent(this, ViewSigleMessagesList.class);
        intent.putExtra("name", notificationTitle);
        intent.putExtra("receiver_id", s_id);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, 0);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setAutoCancel(true);
        builder.setContentTitle(notificationTitle);
        builder.setContentText(notificationMessage);
        builder.setSmallIcon(R.drawable.icon_app);
        builder.setContentIntent(pendingIntent);
        builder.build();
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification myNotication = builder.getNotification();
        manager.notify(11, myNotication);
    }


    private void Notify(String notificationTitle, String notificationMessage) {
       /* Intent intent = new Intent(this, LoginActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, 0);*/
        Notification.Builder builder = new Notification.Builder(this);
        builder.setAutoCancel(true);
        builder.setContentTitle(notificationTitle);
        builder.setContentText(notificationMessage);
        builder.setSmallIcon(R.drawable.icon_app);
        builder.build();
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification myNotication = builder.getNotification();
        manager.notify(111, myNotication);
    }

    private void NotifyInvitation(String notificationTitle, String notificationMessage) {
        Intent intent = new Intent(this, ViewInvitationActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, 0);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setAutoCancel(true);
        builder.setContentTitle(notificationTitle);
        builder.setContentText(notificationMessage);
        builder.setSmallIcon(R.drawable.icon_app);
        builder.setContentIntent(pendingIntent);
        builder.build();
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification myNotication = builder.getNotification();
        manager.notify(1111, myNotication);
    }

}
