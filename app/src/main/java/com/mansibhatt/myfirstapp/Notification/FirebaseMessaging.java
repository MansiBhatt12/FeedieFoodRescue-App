package com.mansibhatt.myfirstapp.Notification;

import  android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mansibhatt.myfirstapp.HomeActivity;
import com.mansibhatt.myfirstapp.R;

import java.util.Random;

public class FirebaseMessaging extends FirebaseMessagingService {

    private static final String NOTIFICATION_CHANNEL_ID = "MY_NOTIFICATION_CHANNEL_ID"; //required for android o and above

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        //all notifications received here
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        //get data from message
        String notificationType = remoteMessage.getData().get("notificationType");

        if (notificationType.equals("requestingFood")){
            String Uid = remoteMessage.getData().get("userId");
            String notificationTitle = remoteMessage.getData().get("notificationTitle");
            String notificationDescription = remoteMessage.getData().get("notificationMessage");

         //   if (firebaseUser != null && firebaseAuth.getUid().equals(Uid)){
                //user is signed in and is same user to which notification is sent
                showNotification(Uid,notificationTitle,notificationDescription,notificationType);
          //  }
        }
    }

    private void showNotification(String uid, String notificationTitle, String notificationDescription, String notificationType){
        //notification
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        //id for notification, random
        int notificationId = new Random().nextInt(3000);

        //check if android version is Oreo/O or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            setupNotificationChannel(notificationManager);
        }

        //handle notification click, start order activity
        Intent intent= null;
        if (notificationType.equals("requestingFood")){

            //open orderDetailsSellerActivity
            intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }


        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent, PendingIntent.FLAG_ONE_SHOT);

        //Large icon
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_baseline_notifications_active_24);

        //sound of notification
        Uri notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setSmallIcon(R.drawable.ic_baseline_notifications_active_24)
                .setLargeIcon(largeIcon)
                .setContentTitle(notificationTitle)
                .setContentText(notificationDescription)
                .setSound(notificationSoundUri)
                .setAutoCancel(true) //cancel/dismiss when clicked
                .setContentIntent(pendingIntent); //add intent

        //show notification
        notificationManager.notify(notificationId,notificationBuilder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupNotificationChannel(NotificationManager notificationManager) {
        CharSequence channelName = "Some Sample Text";
        String channelDescription = "Channel Description here";

        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,channelName,NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.setDescription(channelDescription);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.enableVibration(true);
        if (notificationManager !=null){
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
