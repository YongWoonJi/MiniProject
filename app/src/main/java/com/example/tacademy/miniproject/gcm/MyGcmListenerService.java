/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.tacademy.miniproject.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.tacademy.miniproject.ChatActivity;
import com.example.tacademy.miniproject.MainActivity;
import com.example.tacademy.miniproject.R;
import com.example.tacademy.miniproject.SplashActivity;
import com.example.tacademy.miniproject.Utils;
import com.example.tacademy.miniproject.data.ChatContract;
import com.example.tacademy.miniproject.data.ChatMessage;
import com.example.tacademy.miniproject.data.NetworkResult;
import com.example.tacademy.miniproject.manager.DBManager;
import com.example.tacademy.miniproject.manager.NetworkManager;
import com.example.tacademy.miniproject.request.MessageListRequest;
import com.google.android.gms.gcm.GcmListenerService;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";
    public static final String ACTION_CHAT = "com.example.tacademy.miniproject.action.chatmessage";
    public static final String EXTRA_CHAT_USER = "chatuser";
    public static final String EXTRA_RESULT = "result";

    LocalBroadcastManager mLBM;

    @Override
    public void onCreate() {
        super.onCreate();
        mLBM = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);

        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
            long lastTime = DBManager.getInstance().getLastReceiveDate();
            Date date = new Date(lastTime);
            MessageListRequest request = new MessageListRequest(this, date);
            try {
                NetworkResult<List<ChatMessage>> result = NetworkManager.getInstance().getNetworkDataSync(request);
                List<ChatMessage> list = result.getResult();
                for (ChatMessage m : list) {
                    try {
                        DBManager.getInstance().addMessage(m.getSender(),
                                ChatContract.ChatMessage.TYPE_RECEIVE, m.getMessage(), Utils.convertStringToTime(m.getDate()));
                        Intent i = new Intent(ACTION_CHAT);
                        i.putExtra(EXTRA_CHAT_USER, m.getSender());
                        mLBM.sendBroadcastSync(i);
                        boolean processed = i.getBooleanExtra(EXTRA_RESULT, false);
                        if (!processed) {
                            sendNotification(m);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        sendNotification(message);
    }


    private void sendNotification(ChatMessage m) {
        Intent intent = new Intent(this, SplashActivity.class);
        intent.putExtra(ChatActivity.EXTRA_USER, m.getSender());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notificationBuilder = new android.support.v7.app.NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("Chat Message")
                .setContentTitle(m.getSender().getUserName())
                .setContentText(m.getMessage())
                .setAutoCancel(true)
                .setDefaults(android.support.v7.app.NotificationCompat.DEFAULT_ALL)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }

    private void sendNotification(String message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("GCM Message")
                .setTicker("GCM Message")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
