package com.widevision.prayergrid;//package com.widevision.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.widevision.prayergrid.R;
import com.widevision.prayergrid.dao.GsonClass;
import com.widevision.prayergrid.dao.TokenRefreshDao;
import com.widevision.prayergrid.util.AsyncCallback;
import com.widevision.prayergrid.util.Constant;


import java.io.IOException;

//
///**
// * An {@link IntentService} subclass for handling asynchronous task requests in
// * a service on a separate handler thread.
// * <p/>
// * TODO: Customize class - update intent actions, extra parameters and static
// * helper methods.
// */
public class RegistrationIntentService extends IntentService {
    private static final String TAG = "RegIntentService";

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Make a call to Instance API
        InstanceID instanceID = InstanceID.getInstance(this);
        String senderId = getResources().getString(R.string.gcm_SenderId);
        try {
            // request token that will be used by the server to send push notifications
            String token = instanceID.getToken(senderId, GoogleCloudMessaging.INSTANCE_ID_SCOPE);
            Log.d(TAG, "GCM Registration Token: " + token);

            // pass along this data
            sendRegistrationToServer(token);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.
        TokenRefreshDao tokenRefreshDao = new TokenRefreshDao(Constant._id, token);
        tokenRefreshDao.query(new AsyncCallback<GsonClass>() {
            @Override
            public void onOperationCompleted(GsonClass result, Exception e) {

            }
        });
    }
}