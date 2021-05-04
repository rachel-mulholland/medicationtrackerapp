package com.example.meditrack;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;

import androidx.core.app.NotificationCompat;

import com.example.meditrack.Constants;

import java.util.Date;

import static android.app.Activity.RESULT_OK;

/**
 * AlarmClass.java
 */

public class AlarmClass extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent)
    {
        String medName = intent.getStringExtra(Constants.MED_NAME);             //retrieves intent and assigns them String variables
        String medDosage = intent.getStringExtra(Constants.MED_DOSAGE);
        String qtyToTake = intent.getStringExtra(Constants.QTY_TO_TAKE);
        String  packetQty = intent.getStringExtra(Constants.PKT_QUANTITY);


        //Vibrate
        Vibrator v = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(1000);    //calls the device to vibrate
        try{
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION); //plays notiifcation sound
            Ringtone r = RingtoneManager.getRingtone(context,notification);
            r.play();

        } catch(Exception e)
        {
            e.printStackTrace();
        }

        //Notification
        Intent in = new Intent(context, HomeActivity.class); //passes intent to HomeActivity
        in.putExtra(Constants.MED_NAME,medName);        //takes med details for notification
        in.putExtra(Constants.MED_DOSAGE,medDosage);
        in.putExtra(Constants.PKT_QUANTITY,packetQty);
        in.putExtra(Constants.QTY_TO_TAKE,qtyToTake);
        setResultCode(RESULT_OK);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(context,0,
                        in,PendingIntent.FLAG_UPDATE_CURRENT);

        //builds and creates notification
        NotificationCompat.Builder builder;
        builder = new NotificationCompat.Builder(context);
        Notification notification = builder.setContentTitle("Reminder")
                .setContentText("Have you taken your " + medName +"?")
                .setTicker("New notification alert!")
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)        //stackOverflow starts HomeActivity
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLights(Color.YELLOW,1,1) //sets notification light colour
                .setOngoing(true) //stops user from swiping away notification
                .build();

        Date now = new Date();
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify((int) ((new Date().getTime()/1000L)% Integer.MAX_VALUE),notification); //displays multiple notifications through unique id that is created

    }


}
